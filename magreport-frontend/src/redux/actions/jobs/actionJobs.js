import store from 'redux/store';
import dataHub from 'ajax/DataHub';
import {FolderItemTypes} from 'main/FolderContent/FolderItemTypes';

import { JOBS_FILTER, USERS_JOBS_FILTER, JOB_CANCEL, JOB_CANCELED, JOB_CANCEL_FAILED, JOB_SQL_CLICK, 
        JOB_DIALOG_CLOSE , JOB_SQL_LOADED, JOB_SQL_LOAD_FAILED, 
        JOB_STATUS_HISTORY_CLICK, JOB_STATUS_HISTORY_LOADED, JOB_STATUS_HISTORY_LOAD_FAILED, 
        JOB_ADD_COMMENT, JOB_ADD_COMMENT_SUCCESS, JOB_ADD_COMMENT_FAILED, JOB_SHARE_LIST_CLICK} from '../../reduxTypes'

export function actionFilterJobs(itemsType, jobsFilters){
    return {
        type: JOBS_FILTER,
        itemsType,
        jobsFilters
    }
}

export function actionFilterUsersJobs(itemsType, usersJobsFilters){
    return {
        type: USERS_JOBS_FILTER,
        itemsType,
        usersJobsFilters
    }
}

export function actionJobCancel(itemsType, jobIndex, jobId){

    dataHub.reportJobController.jobCancel(jobId, m => handleCancelResponse(itemsType, jobIndex, jobId, m))
    return {
        type: JOB_CANCEL,
        itemsType,
        jobIndex,
        jobId
    }
}

function handleCancelResponse(itemsType, jobIndex, jobId, m){
    let type = m.ok ? JOB_CANCELED : JOB_CANCEL_FAILED
    
    store.dispatch({
        type,
        itemsType,
        jobIndex,
        jobId
    })
}

function handleClickSql(magrepResponse){
    let type = magrepResponse.ok ? JOB_SQL_LOADED : JOB_SQL_LOAD_FAILED;
    let data = magrepResponse.data;

    store.dispatch({
        open: true,
        type: type,
        data : data,
    });
}

export const showSqlDialog = (itemsType, titleName, id) =>{
    if (itemsType === FolderItemTypes.job || itemsType === FolderItemTypes.userJobs) {
        dataHub.reportJobController.getSqlQuery(id, handleClickSql)
    }
    return {
        type: JOB_SQL_CLICK,
        itemsType,
        titleName,
        id
    }
}

export const hideJobDialog = (itemsType) =>{
    return {type: JOB_DIALOG_CLOSE, open: false, itemsType, data: {}}
}

export const actionJobSqlLoadFailed = error =>{
    return {
        type: JOB_SQL_LOAD_FAILED,
        error
    }
}

function handleClickStatusHistory(magrepResponse){
    let type = magrepResponse.ok ? JOB_STATUS_HISTORY_LOADED : JOB_STATUS_HISTORY_LOAD_FAILED;
    let data = magrepResponse.data;

    store.dispatch({
        open: true,
        type: type,
        data : data,
    });
}

export const actionShowStatusHistory = (itemsType, titleName, id) => {

    if (itemsType === FolderItemTypes.job || itemsType === FolderItemTypes.userJobs) {
        dataHub.reportJobController.getHistory(id, handleClickStatusHistory)
    }
    return {
        type: JOB_STATUS_HISTORY_CLICK,
        itemsType,
        titleName,
        id
    }
}

export const actionStatusHistoryLoadFailed = error =>{
    return {
        type: JOB_STATUS_HISTORY_LOAD_FAILED,
        error
    }
}



function handleJobAddComment(itemsType, jobId, jobIndex, comment, magrepResponse){
    let type = magrepResponse.ok ? JOB_ADD_COMMENT_SUCCESS : JOB_ADD_COMMENT_FAILED;

    store.dispatch({
        type,
        itemsType,
        jobId,
        jobIndex,
        comment
    });
}

export function actionJobAddComment(itemsType, jobId, jobIndex, comment){

    dataHub.reportJobController.addComment(jobId, comment, m => handleJobAddComment(itemsType, jobId, jobIndex, comment, m))
    return({
        type: JOB_ADD_COMMENT,
        itemsType,
        jobIndex,
        jobId,
        comment
    })
}

export const actionShowShareList = (itemsType, titleName, id, data, count) => {
    return {
        type: JOB_SHARE_LIST_CLICK,
        itemsType,
        titleName,
        id,
        data,
        count
    }
}