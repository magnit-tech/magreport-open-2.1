import {FLOW_STATE_BROWSE_FOLDER} from './flowStates';
import SidebarItems from 'main/Main/Sidebar/SidebarItems';
import {FolderItemTypes} from 'main/FolderContent/FolderItemTypes';
import {folderInitialState} from './folderInitialState';
import {folderStateReducer} from './folderStateReducer';

const initialState = {
    ...folderInitialState,
    flowState: FLOW_STATE_BROWSE_FOLDER,
}

export function cubesMenuViewReducer(state = initialState, action={}) {
    return folderStateReducer(state, action, SidebarItems.admin.subItems.cubes, FolderItemTypes.cubes);
}
