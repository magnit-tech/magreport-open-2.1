package ru.magnit.magreportbackend.service.jobengine.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.magnit.magreportbackend.domain.dataset.DataSetTypeEnum;
import ru.magnit.magreportbackend.domain.datasource.DataSourceTypeEnum;
import ru.magnit.magreportbackend.dto.inner.jobengine.CacheEntry;
import ru.magnit.magreportbackend.dto.inner.jobengine.CacheRow;
import ru.magnit.magreportbackend.dto.inner.jobengine.ReportReaderData;
import ru.magnit.magreportbackend.dto.inner.reportjob.ReportFieldData;
import ru.magnit.magreportbackend.exception.QueryExecutionException;
import ru.magnit.magreportbackend.exception.ReportExportException;
import ru.magnit.magreportbackend.service.dao.ConnectionPoolManager;
import ru.magnit.magreportbackend.service.jobengine.QueryBuilder;
import ru.magnit.magreportbackend.service.jobengine.ReaderStatus;
import ru.magnit.magreportbackend.service.jobengine.ReportReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@RequiredArgsConstructor
public class ReportReaderImpl implements ReportReader {

    private ReaderStatus status = ReaderStatus.IDLE;
    private final ReportReaderData readerData;
    private final QueryBuilder queryBuilder;
    private final ConnectionPoolManager poolManager;
    private final Long maxRows;
    private long rowCount;
    private long waitTime;
    private boolean isCanceled;
    private String errorDescription = null;
    private final AtomicReference<PreparedStatement> runningStatement = new AtomicReference<>();

    @Override
    public void run() {
        try (
            var connection = poolManager.getConnection(readerData.dataSource())
        ) {
            status = ReaderStatus.RUNNING;
            if (readerData.dataSource().type() == DataSourceTypeEnum.POSTGRESQL && readerData.report().dataSetTypeId() == DataSetTypeEnum.PROCEDURE.ordinal()) {
                processPostgresFunction(connection);
            } else {
                processData(connection);
            }
            status = isCanceled ? ReaderStatus.CANCELED : ReaderStatus.FINISHED;
        } catch (Exception ex) {
            status = ReaderStatus.FAILED;
            errorDescription = ex.getMessage() + "\n" + ex;
            status = isCanceled ? ReaderStatus.CANCELED : ReaderStatus.FAILED;
            if (status == ReaderStatus.FAILED) throw new QueryExecutionException("Error trying to execute query", ex);
        }
    }

    private void processPostgresFunction(Connection connection) throws SQLException {
        var query = queryBuilder.getQuery(readerData.report());
        log.debug("Query for job " + readerData.jobId() + ": " + query);
        if (isCanceled) return;
        try (final var statement = connection.prepareCall(query)) {
            runningStatement.set(statement);
            connection.setAutoCommit(false);
            statement.registerOutParameter(1, Types.OTHER);
            statement.execute();
            final var reportFields = readerData.report().reportData().fields()
                .stream()
                .filter(ReportFieldData::visible)
                .toList();

            try (final var resultSet =  (ResultSet) statement.getObject(1)) {
                while (resultSet.next() && !isCanceled) {
                    processDataEntry(resultSet, reportFields);
                }
                log.debug("Total time of reader waiting writer (jobId:" + readerData.jobId() + "): " + waitTime / 1000.0);
            }
        }
    }

    private void processData(Connection connection) throws SQLException {
        var query = queryBuilder.getQuery(readerData.report());

        log.debug("Query for job " + readerData.jobId() + ": " + query);

        if (isCanceled) return;

        try (PreparedStatement statement = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
        ) {
            final var reportFields = readerData.report().reportData().fields()
                .stream()
                .filter(ReportFieldData::visible)
                .toList();
            runningStatement.set(statement);
            try (ResultSet resultSet = statement.executeQuery()
            ) {
                while (resultSet.next() && !isCanceled) {
                    processDataEntry(resultSet, reportFields);
                }
                log.debug("Total time of reader waiting writer (jobId:" + readerData.jobId() + "): " + waitTime / 1000.0);
            }
        }
    }

    @SneakyThrows(InterruptedException.class)
    private void processDataEntry(ResultSet resultSet, List<ReportFieldData> reportFields) {
        var cacheRow = new CacheRow(
            DataSetTypeEnum.PROCEDURE.equalsIsLong(readerData.report().dataSetTypeId()) ?
                getListCacheEntryForProcedure(resultSet, reportFields)
                :
                getListCacheEntry(resultSet, reportFields));

        while (!readerData.cache().offer(cacheRow) && !isCanceled) {
            final var startWaiting = System.currentTimeMillis();
            //noinspection BusyWait
            Thread.sleep(10);
            waitTime += System.currentTimeMillis() - startWaiting;
        }
        rowCount++;
        if (rowCount > maxRows)
            throw new ReportExportException("Превышено максимально допустимое количество строк отчета:" + maxRows);
    }

    private CacheEntry getCacheEntryFromResultSet(ResultSet resultSet, ReportFieldData field, int fieldNumber) {
        try {
            return new CacheEntry(field, resultSet.getString(fieldNumber));
        } catch (SQLException ex) {
            throw new QueryExecutionException("Error while fetching data", ex);
        }
    }

    private List<CacheEntry> getListCacheEntry(ResultSet resultSet, List<ReportFieldData> reportFields) {
        final var result = new ArrayList<CacheEntry>(reportFields.size());
        for (int i = 0; i < reportFields.size(); i++) {
            result.add(getCacheEntryFromResultSet(resultSet, reportFields.get(i), i + 1));
        }

        return result;
    }

    private List<CacheEntry> getListCacheEntryForProcedure(ResultSet resultSet, List<ReportFieldData> reportFields) {
        final var result = new ArrayList<CacheEntry>(reportFields.size());

        for (int i = 0; i < readerData.report().reportData().fields().size(); i++) {
            final var currentField = readerData.report().reportData().fields().get(i);
            if (currentField.visible()) {
                result.add(getCacheEntryFromResultSet(resultSet, currentField, currentField.ordinal()));
            }
        }

        return result;
    }

    @Override
    public ReaderStatus getReaderStatus() {
        return status;
    }

    @Override
    public boolean isFinished() {
        return status.isFinalStatus();
    }

    @Override
    public boolean isFailed() {
        return status == ReaderStatus.FAILED;
    }

    @Override
    public long getRowCount() {
        return rowCount;
    }

    @Override
    public void cancel() {
        isCanceled = true;

        PreparedStatement currentStatement = runningStatement.get();
        if (currentStatement != null) {
            try {
                currentStatement.cancel();
            } catch (SQLException ignored) {
                log.debug("Job: " + readerData.jobId() + " was canceled.");
            }
        }
    }

    @Override
    public boolean isCanceled() {
        return status == ReaderStatus.CANCELED;
    }

    @Override
    public String getErrorDescription() {
        return errorDescription;
    }
}
