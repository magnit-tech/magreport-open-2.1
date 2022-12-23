import { TASK_SWITCHED } from 'redux/reduxTypes';
import SidebarItems from 'main/Main/Sidebar/SidebarItems';
import {FolderItemTypes} from 'main/FolderContent/FolderItemTypes';
// import {folderInitialState} from './folderInitialState';
import {folderStateReducer} from './folderStateReducer';

const initialState = {
    // ...folderInitialState,
}

export function scheduleTasksMenuViewReducer(state = initialState, action={}) {
    switch (action.type) {

        case TASK_SWITCHED:
            if(action.itemsType === FolderItemTypes.scheduleTasks){
                let newState = {...state}
                const fullArr = [...state.currentFolderData.scheduleTasks]
                const filteredArr = [...state.filteredFolderData.scheduleTasks]

                let newTask = {};

                if (state.searchParams){
                    newTask = {...filteredArr[action.taskIndex], status: action.status}
                    let indexFullArr = fullArr.findIndex(item => item.id === action.taskId)
                    filteredArr.splice(action.taskIndex, 1, newTask)
                    fullArr.splice(indexFullArr, 1, newTask)
                    
                }
                else {
                    newTask = {...fullArr[action.taskIndex], status: action.status}
                    fullArr.splice(action.taskIndex, 1, newTask)
                    filteredArr.splice(action.taskIndex, 1, newTask)
                }
                newState.filteredFolderData.scheduleTasks = filteredArr;
                newState.currentFolderData.scheduleTasks = fullArr;
                return newState
            }
            else {
                return state;
            }
    
        default:
            return folderStateReducer(state, action, SidebarItems.schedule.subItems.scheduleTasks, FolderItemTypes.scheduleTasks);
    }
}
