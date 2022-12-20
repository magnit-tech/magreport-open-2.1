import { FLOW_STATE_BROWSE_FOLDER } from './flowStates';
import { FolderItemTypes } from 'main/FolderContent/FolderItemTypes';
import { folderStateReducer } from './folderStateReducer';
import SidebarItems from 'main/Main/Sidebar/SidebarItems';

import { JOBS_FILTER } from 'redux/reduxTypes';

const initialState = {
    flowState : FLOW_STATE_BROWSE_FOLDER
}

export function usersJobsMenuViewReducer(state = initialState, action){
    switch(action.type){
        
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
