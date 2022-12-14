import React, { useState } from 'react';
import PlainTablePanel from './PlainTablePanel';
import PivotPanel from './Pivot/PivotPanel';

/**
 * @param {Number} props.jobId - id задания
 * @param {Number} props.reportId - id отчёта
 * @param {Number} props.folderId - id разработческой папки в которой находится отчет
 * @param {String} props.jobOwnerName - login владельца отчета
 * @param {Number} props.excelRowLimit - лимит кол-ва строк для выгрузки в Excel
 * @param {*} props.onRestartReportClick - function() - callback перезапуска отчёта
 */
export default function ReportJobData(props){
    const [viewType, setViewType] = useState('PlainTable');

    function handleChangeViewType(value){
        setViewType(value);
    };

    return (
        <div style={{ display: 'flex', flex: 1}}>
            { (viewType === 'PlainTable') ? 
                <PlainTablePanel
                    canExecute = {props.canExecute}
                    jobId = {props.jobId}
                    excelTemplates = {props.excelTemplates}
                    excelRowLimit = {props.excelRowLimit}
                    onRestartReportClick = {props.onRestartReportClick}
                    onViewTypeChange = {handleChangeViewType}
                />
                :
                <PivotPanel
                    jobId = {props.jobId}
                    reportId = {props.reportId}
                    folderId = {props.folderId}
                    jobOwnerName = {props.jobOwnerName}
                    onRestartReportClick = {props.onRestartReportClick}
                    onViewTypeChange = {handleChangeViewType}
                />
            }
        </div>
    );

}
