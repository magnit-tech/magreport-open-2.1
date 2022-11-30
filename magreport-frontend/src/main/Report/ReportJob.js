import React, { useState, useRef, useEffect } from 'react';
import { useSnackbar } from 'notistack';

import { useParams } from 'react-router-dom'

// dataHub
import dataHub from 'ajax/DataHub';

// components
import { CircularProgress, Typography, Button } from '@material-ui/core';

// local
import DataLoader from 'main/DataLoader/DataLoader';
import ReportJobData from './ReportJobData';

// styles
import { ReportDataCSS } from "./ReportCSS";


const JobStatus = {
    UNDEFINED: 0,
    SCHEDULED: 1,
    RUNNING: 2, 
    COMPLETE: 3, 
    FAILED: 4, 
    CANCELING: 5, 
    CANCELED: 6,
    EXPORT: 7,
    PENDING_DB_CONNECTION: 8
}

const REQUEST_TIMEOUT_INTERVAL_MS = 1000;

/**
 * Запуск отчёта
 * @param {*} props.jobId - id задания
 * @param {*} props.reportId - id задания
 * @param {*} props.onRestartReportClick - function(reportId) - callback перезапуска отчёта
 */
export default function ReportJob(props){
    const {id} = useParams()

    const classes = ReportDataCSS();

    const [reload, setReload] = useState({needReload: true});
    const [jobStatus, setJobStatus] = useState(JobStatus.UNDEFINED);
    const jobData = useRef(null);
    const reportId = useRef(null);
    const folderId = useRef(null);
    const timer = useRef(null);
    const jobOwnerName = useRef(null);
    const excelRowLimit = useRef(1000000);
    const { enqueueSnackbar } = useSnackbar();

    // убираем таймер при переключении на другой пункт меню, чтобы не было утечек памяти
    useEffect(() => {
        return () => {
            if (timer.current) {
                clearTimeout(timer.current);
            }
        }
    }, [timer])

    function handleJobInfoLoaded(data){
        jobData.current = data;
        reportId.current = data.report.id;
        folderId.current = data.report.folderId;
        jobOwnerName.current = data.user.name;
        excelRowLimit.current = data.excelRowLimit;
        let newStatus = JobStatus[data.status];
        if(    newStatus === JobStatus.RUNNING 
            || newStatus === JobStatus.SCHEDULED 
            || newStatus === JobStatus.RUNNING 
            || newStatus === JobStatus.CANCELING 
            || newStatus === JobStatus.EXPORT){
            timer.current = window.setTimeout(setReload, REQUEST_TIMEOUT_INTERVAL_MS, {needReload: true});
        }
        setJobStatus(newStatus);
    }

    function handleCancelClick(){
        dataHub.reportJobController.jobCancel(jobData.current.id, handleCancelResponse)
    }

    function handleCancelResponse(magrepResponse){
        if (magrepResponse.ok){
            setJobStatus(JobStatus.CANCELING);
        }
        else {
            enqueueSnackbar(`Не удалось отменить выполнение отчета`, {variant : "error"})
        }
    }

    function handleRestartReportClick(){
        props.onRestartReportClick(jobData.current.report.id, jobData.current.id);
    }

    function setCustomErrorMessage(){

        let message = jobData.current.message,
            arrWithMessages = []

        if (message && message.indexOf("\n") !== -1) {
            message.split('\n').map((item,index) => arrWithMessages.push(<Typography gutterBottom key={index} variant="subtitle1" align = "justify"> {item}</Typography>))
            return arrWithMessages
        }

        return message
    }

    return (
        // !props.jobId || props.jobId === null ?
            // <div></div>
            // :
            <div className={classes.flexDiv}>
                <DataLoader
                    loadFunc = {dataHub.reportJobController.get}
                    // loadParams = {[props.jobId]}
                    loadParams = {id ? [Number(id)] : [null]}
                    reload = {reload}
                    onDataLoaded = {handleJobInfoLoaded}
                    onDataLoadFailed = {message => {console.log(message)}}
                    showSpinner = {false}
                    disabledScroll = {true}
                >
                <div className={classes.flexDiv}>
                {
                    jobStatus === JobStatus.UNDEFINED ?
                        
                            <CircularProgress/>

                    :jobStatus === JobStatus.SCHEDULED || jobStatus === JobStatus.RUNNING || jobStatus === JobStatus.PENDING_DB_CONNECTION ?
                        <div className={classes.repExec}>
                            <Typography gutterBottom variant="h6">Отчет выполняется</Typography>
                            <Button color="secondary" onClick={handleCancelClick}>Отменить</Button>
                            <CircularProgress className = {classes.progress}/>
                        </div>

                    :jobStatus === JobStatus.COMPLETE || jobStatus === JobStatus.EXPORT ?
                        <ReportJobData
                            jobStatus = {jobStatus}
                            canExecute = {jobData.current.canExecute}
                            reportId = {reportId.current}
                            folderId = {folderId.current}
                            jobId = {Number(id)}
                            jobOwnerName = {jobOwnerName.current}
                            excelRowLimit = {excelRowLimit.current}
                            excelTemplates = {props.excelTemplates}
                            onRestartReportClick = {handleRestartReportClick}
                        />

                    :jobStatus === JobStatus.CANCELING ?
                        <div className={classes.repExec}>
                            <Typography gutterBottom variant="h6">Отчет отменяется</Typography>
                            <CircularProgress className = {classes.progress}/>
                        </div>

                    :jobStatus === JobStatus.CANCELED ?
                        <div className={classes.repExec}>
                            <Typography gutterBottom variant="h6">Отчет отменен</Typography>
                        </div>            
                    
                    :jobStatus === JobStatus.FAILED ?
                        <div className={classes.repExecFailed}>
                            <Typography gutterBottom variant="subtitle1" color = "error">Отчёт завершён с ошибкой:</Typography>
                            {setCustomErrorMessage()}
                        </div>
                    :
                    <div></div>

                }
                </div>
                </DataLoader>
            </div>
    )
}