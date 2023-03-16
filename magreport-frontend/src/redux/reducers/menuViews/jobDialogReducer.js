import { JOB_SQL_CLICK,  JOB_DIALOG_CLOSE, JOB_SQL_LOADED, JOB_SQL_LOAD_FAILED,
    JOB_STATUS_HISTORY_CLICK, JOB_STATUS_HISTORY_LOADED, JOB_STATUS_HISTORY_LOAD_FAILED, JOB_SHARE_LIST_CLICK } from   '../../reduxTypes';

const initialState = {
        open: false,
        itemsType: "",
        titleName: "",
        data: {}
}

export const jobDialogReducer = (state = initialState, action) => {
    switch (action.type){
        case JOB_SQL_CLICK:
            return {
                ...state,
                itemsType: action.itemsType,
                titleName: action.titleName,
                data: {id: action.id}
            }
        case JOB_SQL_LOADED:
            return {
                ...state, open: true, data: action.data
            }
        case JOB_SQL_LOAD_FAILED:
            return {
                state
            }

        case JOB_DIALOG_CLOSE:
            return {
                ...state, open: false
            }
        case JOB_STATUS_HISTORY_CLICK:
            return {
                ...state,
                itemsType: action.itemsType,
                titleName: action.titleName,
                data: {id: action.id}
            }
        case JOB_STATUS_HISTORY_LOADED:
            return {
                ...state, open: true, data: action.data
            }
        case JOB_STATUS_HISTORY_LOAD_FAILED:
            return {
                state
            }
        case JOB_SHARE_LIST_CLICK:
            return {
                ...state,
                open: true,
                itemsType: action.itemsType,
                titleName: action.titleName,
                data: {id: action.id, shareList: action.data}
            }
            
        default:
            return state
    }
}