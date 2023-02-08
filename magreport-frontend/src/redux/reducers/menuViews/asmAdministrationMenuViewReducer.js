import {
    ASM_LIST_LOADED,
    ASM_LIST_LOAD_FAILED,
    ASM_ADDED,
    ASM_EDITED,
    ASM_DELETED,
    ASM_LIST_SHOW,
    ASM_REFRESH_START,
    ASM_REFRESH_FINISH,
} from "redux/reduxTypes";

import {asmAdministrationMenuViewFlowStates} from "./flowStates";

const initialState = {
    refresh: false,
    data: [],
    flowState: asmAdministrationMenuViewFlowStates.externalSecurityList,
    needReload: true
};

export const asmAdministrationMenuViewReducer = (state = initialState, action) => {
    switch (action.type) {

        case ASM_LIST_SHOW:
            return {
                ...state,
                needReload: false,
                flowState: asmAdministrationMenuViewFlowStates.externalSecurityList
            }
        case ASM_LIST_LOADED:
            return {...state, needReload: false, data: action.data};
            
        case ASM_LIST_LOAD_FAILED:
            return {...state, needReload: false, data: action.error};

        case ASM_ADDED:
        case ASM_DELETED:
        case ASM_EDITED:
            return {
                ...state,
                needReload: true,
                flowState: asmAdministrationMenuViewFlowStates.externalSecurityList
            }
        case ASM_REFRESH_START:
        case ASM_REFRESH_FINISH:
            return {...state, refresh: action.refresh}
        default:
            return state;
    }
}