import dataHub from 'ajax/DataHub';

import {FLOW_STATE_BROWSE_FOLDER, jobsMenuViewFlowStates} from './flowStates';
import {FolderItemTypes} from 'main/FolderContent/FolderItemTypes';
import {folderInitialState} from './folderInitialState';
import {folderStateReducer} from './folderStateReducer';
import SidebarItems from 'main/Main/Sidebar/SidebarItems';

import {REPORT_START, FOLDER_CONTENT_ITEM_CLICK, JOBS_FILTER, JOB_ADD_COMMENT_SUCCESS} from 'redux/reduxTypes';

const initialState = {
    ...folderInitialState,
    flowState : FLOW_STATE_BROWSE_FOLDER
}

export function jobsMenuViewReducer(state = initialState, action){
    switch(action.type){
        case REPORT_START:
            if(action.sidebarItemKey === SidebarItems.jobs.key){
                return{
                    ...state,
                    flowState : jobsMenuViewFlowStates.startReport,
                    reportId : action.reportId,
                    jobId : action.jobId
                }
            }
            else{
                return state;
            }

        case FOLDER_CONTENT_ITEM_CLICK:
            if(action.itemType === FolderItemTypes.job){
                const reportInfo = dataHub.localCache.getJobInfo(action.itemId)
                const reportName = reportInfo.report.name
                const reportId = reportInfo.report.id
                const excelTemplates = reportInfo.excelTemplates

                return{
                    ...state,
                    flowState : jobsMenuViewFlowStates.reportJob,
                    jobId : action.itemId,
                    reportName,
                    reportId,
                    excelTemplates,
                }
            }
            else{
                return state;
            }
        
        case JOBS_FILTER:
            if(action.itemsType === FolderItemTypes.job){
                let newState = {}
                if (action.filters.isCleared){
                    newState = {...state}
                    delete newState.filters
                }
                else {
                    newState = {
                        ...state, filters: action.filters
                    }
                }
                

                return newState
            }
            else{
                return state;
            }

        case JOB_ADD_COMMENT_SUCCESS:
            if(action.itemsType === FolderItemTypes.job){
                let newState = {...state}
                const fullArr = [...state.currentFolderData.jobs]
    
                let newTask = {};
                    newTask = {...fullArr[action.jobIndex], comment: action.comment}
                    fullArr.splice(action.jobIndex, 1, newTask)
    
                    newState.currentFolderData.jobs = fullArr;
                    return newState
            }
            else {
                return state;
            } 
        default:
            return folderStateReducer(state, action, SidebarItems.jobs, FolderItemTypes.job);
    }
}
