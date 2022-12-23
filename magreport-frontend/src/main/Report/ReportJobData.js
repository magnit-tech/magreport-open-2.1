import React from 'react';
import PlainTablePanel from './PlainTablePanel';
import PivotPanel from './Pivot/PivotPanel';

/**
 * @param {Number} props.jobId - id задания
 * @param {String} props.jobStatus - статус задания
 * @param {Number} props.reportId - id отчёта
 * @param {Number} props.folderId - id разработческой папки в которой находится отчет
 * @param {String} props.jobOwnerName - login владельца отчета
 * @param {Number} props.excelRowLimit - лимит кол-ва строк для выгрузки в Excel
 */
export default function ReportJobData(props){

    function handleChangeViewType(value){
        props.changeViewType(value)
    };

    return (
        <div style={{ display: 'flex', flex: 1}}>
            { props.viewType === 'pivot' ? 
                    <PivotPanel
                        jobId = {props.jobId}
                        reportId = {props.reportId}
                        folderId = {props.folderId}
                        jobOwnerName = {props.jobOwnerName}
                        onRestartReportClick = {props.onRestartReportClick}
                        onViewTypeChange = {handleChangeViewType}
                    />

                :
                    <PlainTablePanel
                        jobStatus = {props.jobStatus}
                        canExecute = {props.canExecute}
                        jobId = {props.jobId}
                        reportId = {props.reportId}
                        excelTemplates = {props.excelTemplates}
                        excelRowLimit = {props.excelRowLimit}
                        onViewTypeChange = {handleChangeViewType}
                    />
            }
        </div>
    );

}
