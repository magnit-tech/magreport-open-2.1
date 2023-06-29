import {
    ASM_ADDED,
    ASM_DATA_LOAD_FAILED,
    ASM_DATA_LOADED,
    ASM_DELETED,
    ASM_EDITED,
    ASM_SWITCH,
    ASM_SWITCHED,
    ASM_SWITCH_FAILED,
    ASM_LIST_LOAD_FAILED,
    ASM_LIST_LOADED,
    ASM_LIST_SHOW,
    ASM_REFRESH_FINISH,
    ASM_REFRESH_START
} from "redux/reduxTypes";

import store from 'redux/store';
import dataHub from "ajax/DataHub";

export const actionAsmListShow = () => {
    return {type: ASM_LIST_SHOW};
}

export const actionAsmListLoaded = (data) => {
    return {
        type: ASM_LIST_LOADED,
        data
    };
}

export const actionAsmListLoadFailed = (error) => {
    return {
        type: ASM_LIST_LOAD_FAILED,
        error
    };
}

export const actionAsmAdded = (data) => {
    return {
        type: ASM_ADDED,
        data
    };
}

export const actionAsmEdited = (data) => {
    return {
        type: ASM_EDITED,
        data
    };
}

export const actionAsmSwitch = (id) => {
    dataHub.asmController.switch(id, m => handleSwitchResponse(id, m))
    return {
        type: ASM_SWITCH,
        id
    };
}

function handleSwitchResponse(id, m) {
    let type = m.ok ? ASM_SWITCHED : ASM_SWITCH_FAILED
    store.dispatch({
        type,
        id,
        data: m.data
    })
}


/*
export function actionScheduleTaskSwitch(itemsType, taskIndex, taskId){
    dataHub.scheduleController.taskSwitch(taskId, m => handleScheduleTaskSwitchResponse(itemsType, taskIndex, taskId, m))
    return {
        type: TASK_SWITCH,
        itemsType,
        taskIndex,
        taskId
    }
}

function handleScheduleTaskSwitchResponse(itemsType, taskIndex, taskId, m){
    let type = m.ok ? TASK_SWITCHED : TASK_SWITCH_FAILED
    store.dispatch({
        type,
        itemsType,
        taskIndex,
        taskId,
        status: m.data
    })
}
*/

export const actionAsmDeleted = (data) => {
    return {
        type: ASM_DELETED,
        data
    };
}

export const actionAmsRefresh = status => {
    return {
        type: status === 'START' ? ASM_REFRESH_START : ASM_REFRESH_FINISH,
        refresh: status === 'START',
    }
}

export function actionAsmDataLoaded(data, actionFor) {
    return {
        type: ASM_DATA_LOADED,
        data,
        actionFor
    };
}

export function actionAsmDataLoadFailed(error) {
    return {
        type: ASM_DATA_LOAD_FAILED,
        error
    }
}
