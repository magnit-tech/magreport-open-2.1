package ru.magnit.magreportbackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.magnit.magreportbackend.config.DatasourceCheckQueryConfig;
import ru.magnit.magreportbackend.domain.datasource.DataSourceTypeEnum;
import ru.magnit.magreportbackend.dto.inner.datasource.DataSourceData;
import ru.magnit.magreportbackend.dto.response.datasource.DataSourceObjectResponse;
import ru.magnit.magreportbackend.dto.response.datasource.ObjectFieldResponse;
import ru.magnit.magreportbackend.exception.MetaDataQueryException;
import ru.magnit.magreportbackend.service.dao.ConnectionPoolManager;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetaDataService {

    public static final String QUERY_ERROR = "Error while trying to query datasource(id:%s) table types:%n %s";
    private final ConnectionPoolManager poolManager;

    private final DatasourceCheckQueryConfig checkQueryConfig;

    public List<String> getCatalogs(DataSourceData dataSource) {
        var result = new LinkedList<String>();

        try (
                Connection connection = poolManager.getConnection(dataSource);
                ResultSet resultSet = connection.getMetaData().getCatalogs()) {
            while (resultSet.next()) {
                result.add(resultSet.getString(1));
            }

        } catch (SQLException ex) {
            throw new MetaDataQueryException(String.format("Error while trying to query datasource(id:%s) catalogs:%n %s", dataSource.id(), ex.getMessage()), ex);
        }
        return result;
    }

    public List<String> getSchemas(DataSourceData dataSource, String catalogName) {
        var result = new LinkedList<String>();

        try (
                Connection connection = poolManager.getConnection(dataSource);
                ResultSet resultSet = connection.getMetaData().getSchemas(catalogName, null)) {
            while (resultSet.next()) {
                result.add(resultSet.getString(1));
            }

        } catch (SQLException ex) {
            throw new MetaDataQueryException(String.format("Error while trying to query datasource(id:%s) schemas:%n %s", dataSource.id(), ex.getMessage()), ex);
        }
        return result;
    }

    public List<DataSourceObjectResponse> getSchemaObjects(DataSourceData dataSource, String catalogName, String schemaName, String objectType) {
        var result = new LinkedList<DataSourceObjectResponse>();

        try (
                Connection connection = poolManager.getConnection(dataSource);
                ResultSet resultSet = connection.getMetaData().getTables(catalogName, schemaName, null, objectType == null ? null : new String[]{objectType})) {
            while (resultSet.next()) {
                result.add(
                        new DataSourceObjectResponse()
                                .setCatalog(resultSet.getString(1))
                                .setSchema(resultSet.getString(2))
                                .setName(resultSet.getString(3))
                                .setType(resultSet.getString(4))
                                .setComment(resultSet.getString(5))
                );
            }

        } catch (SQLException ex) {
            throw new MetaDataQueryException(String.format("Error while trying to query datasource(id:%s) schema objects:%n %s", dataSource.id(), ex.getMessage()), ex);
        }
        return result;

    }

    @SuppressWarnings("unused")
    public List<String> getObjectTypes(DataSourceData dataSource) {
        var result = new LinkedList<String>();

        try (
                Connection connection = poolManager.getConnection(dataSource);
                ResultSet resultSet = connection.getMetaData().getTableTypes()) {
            while (resultSet.next()) {
                result.add(resultSet.getString(1));
            }

        } catch (SQLException ex) {
            throw new MetaDataQueryException(String.format(QUERY_ERROR, dataSource.id(), ex.getMessage()), ex);
        }
        return result;
    }

    public List<ObjectFieldResponse> getObjectFields(DataSourceData dataSource, String catalogName, String schemaName, String objectName) {
        var result = new LinkedList<ObjectFieldResponse>();

        try (
                Connection connection = poolManager.getConnection(dataSource);
                ResultSet resultSet = connection.getMetaData().getColumns((catalogName == null || catalogName.isBlank()) ? null : catalogName, schemaName, objectName, null)) {

            while (resultSet.next()) {
                var fieldMetaData = new ObjectFieldResponse()
                        .setCatalogName(resultSet.getString(1))
                        .setSchemaName(resultSet.getString(2))
                        .setTableName(resultSet.getString(3))
                        .setFieldName(resultSet.getString(4))
                        .setDataType(resultSet.getInt(5))
                        .setDataTypeName(resultSet.getString(6))
                        .setFieldSize(resultSet.getInt(7))
                        .setBufferSize(resultSet.getInt(8))
                        .setDecimalDigits(resultSet.getInt(9))
                        .setNumPrecision(resultSet.getInt(10))
                        .setNullable(resultSet.getBoolean(11))
                        .setRemarks(resultSet.getString(12))
                        .setSqlDataType(resultSet.getInt(14))
                        .setCharOctetLength(resultSet.getInt(16))
                        .setOrdinalPosition(resultSet.getInt(17));

                result.add(fieldMetaData);
            }
        } catch (SQLException ex) {
            throw new MetaDataQueryException(String.format(QUERY_ERROR, dataSource.id(), ex.getMessage()), ex);
        }
        return result;
    }

    public List<DataSourceObjectResponse> getProcedures(DataSourceData dataSource, String catalogName, String schemaName) {
        var result = new LinkedList<DataSourceObjectResponse>();

        try (
                Connection connection = poolManager.getConnection(dataSource);
                ResultSet resultSet = connection.getMetaData().getProcedures(catalogName, schemaName, null)) {
            while (resultSet.next()) {
                result.add(
                        new DataSourceObjectResponse()
                                .setCatalog(resultSet.getString(1))
                                .setSchema(resultSet.getString(2))
                                .setName(resultSet.getString(3))
                                .setType(resultSet.getString(8))
                                .setComment(resultSet.getString(7))
                );
            }

        } catch (SQLException ex) {
            throw new MetaDataQueryException(String.format("Error while trying to query datasource(id:%s) schema objects:%n %s", dataSource.id(), ex.getMessage()), ex);
        }
        return result;
    }

    public List<ObjectFieldResponse> getProcedureFields(DataSourceData dataSource, String catalogName, String schemaName, String objectName) {

        var result = new LinkedList<ObjectFieldResponse>();

        try (
                Connection connection = poolManager.getConnection(dataSource);
                ResultSet resultSet = connection.getMetaData().getProcedureColumns(catalogName, schemaName, objectName, null)) {

            while (resultSet.next()) {
                var fieldMetaData = new ObjectFieldResponse()
                        .setCatalogName(resultSet.getString(1))
                        .setSchemaName(resultSet.getString(2))
                        .setTableName(resultSet.getString(3))
                        .setFieldName(resultSet.getString(4))
                        .setDataType(resultSet.getInt(6))
                        .setDataTypeName(resultSet.getString(7))
                        .setFieldSize(resultSet.getInt(8))
                        .setDecimalDigits(resultSet.getInt(10))
                        .setNullable(resultSet.getBoolean(12))
                        .setRemarks(resultSet.getString(13))
                        .setSqlDataType(resultSet.getInt(15))
                        .setCharOctetLength(resultSet.getInt(16))
                        .setOrdinalPosition(resultSet.getInt(18));

                result.add(fieldMetaData);
            }
        } catch (SQLException ex) {
            throw new MetaDataQueryException(String.format(QUERY_ERROR, dataSource.id(), ex.getMessage()), ex);
        }
        return result;
    }

    @SuppressWarnings("unused")
    public List<ObjectFieldResponse> getProcedureFields2(DataSourceData dataSource, String catalogName, String schemaName, String objectName) {
        var result = new LinkedList<ObjectFieldResponse>();
        var query = String.format("CALL %s.%s (null)", schemaName, objectName);
        if (dataSource.type() == DataSourceTypeEnum.POSTGRESQL) {
            query = String.format("{? = call %s.%s(null::int)}", schemaName, objectName);

            try (
                    final var connection = poolManager.getConnection(dataSource);
                    final var statement = connection.prepareCall(query)
            ) {
                connection.setAutoCommit(false);
                statement.registerOutParameter(1, Types.OTHER);
                statement.execute();

                processResultSet(dataSource, result, statement);
            } catch (SQLException ex) {
                throw new MetaDataQueryException(String.format(QUERY_ERROR, dataSource.id(), ex.getMessage()), ex);
            }

            return result;
        }

        try (
                Connection connection = poolManager.getConnection(dataSource);
                Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                ResultSet resultSet = statement.executeQuery(query)) {
            processFields(result, resultSet);
        } catch (SQLException ex) {
            throw new MetaDataQueryException(String.format(QUERY_ERROR, dataSource.id(), ex.getMessage()), ex);
        }
        return result;
    }

    private static void processResultSet(DataSourceData dataSource, LinkedList<ObjectFieldResponse> result, CallableStatement statement) {
        try (final var resultSet = (ResultSet) statement.getObject(1)) {
            processFields(result, resultSet);
        } catch (SQLException ex) {
            throw new MetaDataQueryException(String.format(QUERY_ERROR, dataSource.id(), ex.getMessage()), ex);
        }
    }

    private static void processFields(LinkedList<ObjectFieldResponse> result, ResultSet resultSet) throws SQLException {
        var resultSetMD = resultSet.getMetaData();
        int cols = resultSetMD.getColumnCount();
        for (int i = 1; i <= cols; i++) {
            var fieldMetaData = new ObjectFieldResponse()
                    .setCatalogName(resultSetMD.getCatalogName(i))
                    .setSchemaName(resultSetMD.getSchemaName(i))
                    .setTableName(resultSetMD.getTableName(i))
                    .setFieldName(resultSetMD.getColumnName(i))
                    .setFieldSize(resultSetMD.getColumnDisplaySize(i))
                    .setDataType(resultSetMD.getColumnType(i))
                    .setDataTypeName(resultSetMD.getColumnTypeName(i))
                    .setDecimalDigits(resultSetMD.getScale(i))
                    .setNullable(resultSetMD.isNullable(i) == ResultSetMetaData.columnNullable)
                    .setOrdinalPosition(i);

            result.add(fieldMetaData);
        }
    }

    public void checkDataSource(DataSourceData dataSource) {
        try (
                final var connection = poolManager.getConnection(dataSource);
                final var preparedStatement = connection.prepareStatement(checkQueryConfig.getQueryForDatasourceType(dataSource.type()));
                final var resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                log.debug("Connection check successful");
            }
        } catch (SQLException ex) {
            throw new MetaDataQueryException(String.format("Error while trying to query datasource(id:%s) catalogs:%n %s", dataSource.id(), ex.getMessage()), ex);
        }
    }

    public boolean checkObjectExists(DataSourceData dataSource, String schema, String table) {
        final var schemaTables = getSchemaObjects(dataSource, null, schema, null);
        final var existingTables = schemaTables.stream()
                .map(DataSourceObjectResponse::getName)
                .map(String::toUpperCase)
                .collect(Collectors.toSet());
        return existingTables.contains(table);
    }
}
