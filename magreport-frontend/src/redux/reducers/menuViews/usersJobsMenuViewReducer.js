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
                    delete newState.filteredJobs
                    delete newState.filters
                }
                else {
                    const tmp = [...state.currentFolderData.jobs]
                    let arr = tmp.filter(item => {
                        let flagStart = true
                        let flagEnd = true
                        let flagStatus = true
                        let flagUsername = true
                        if (action.filters.periodStart && new Date(item.created) < action.filters.periodStart){
                            flagStart = false
                        }
                        if (action.filters.periodEnd && new Date(item.created) > action.filters.periodEnd){
                            flagEnd = false
                        }
                        if (action.filters.selectedStatuses && !action.filters.selectedStatuses.includes(item.status)){
                            flagStatus = false
                        }
                        if (action.filters.user && item.user.name.trim().toLowerCase() !== action.filters.user.trim().toLowerCase()){
                            flagUsername = false
                        }
                        return flagStart && flagEnd && flagStatus && flagUsername
                    })

                    newState = {
                        ...state, filteredJobs: {jobs: arr}, filters: action.filters
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
