import dataHub from 'ajax/DataHub';

import {FLOW_STATE_BROWSE_FOLDER, usersJobsMenuViewFlowStates} from './flowStates';
import {FolderItemTypes} from 'main/FolderContent/FolderItemTypes';
import {folderInitialState} from './folderInitialState';
import {folderStateReducer} from './folderStateReducer';
import SidebarItems from 'main/Main/Sidebar/SidebarItems';

import {REPORT_START, FOLDER_CONTENT_ITEM_CLICK, JOBS_FILTER} from 'redux/reduxTypes';

const initialState = {
    ...folderInitialState,
    flowState : FLOW_STATE_BROWSE_FOLDER
}

export function usersJobsMenuViewReducer(state = initialState, action){
    switch(action.type){
        case REPORT_START:
            if(action.sidebarItemKey === SidebarItems.admin.subItems.userJobs.key){
                return{
                    ...state,
                    flowState : usersJobsMenuViewFlowStates.startReport,
                    reportId : action.reportId,
                    jobId : action.jobId
                }
            }
            else{
                return state;
            }

        case FOLDER_CONTENT_ITEM_CLICK:
            if(action.itemType === FolderItemTypes.userJobs){
                const reportInfo = dataHub.localCache.getJobInfo(action.itemId)
                const reportName = reportInfo.report.name
                const reportId = reportInfo.report.id
                const excelTemplates = reportInfo.excelTemplates

                return{
                    ...state,
                    flowState : usersJobsMenuViewFlowStates.reportJob,
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
            if(action.itemsType === FolderItemTypes.userJobs){
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

        default:
            return folderStateReducer(state, action, SidebarItems.admin.subItems.userJobs, FolderItemTypes.userJobs);
    }
}
