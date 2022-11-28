import { JOB_SQL_CLICK,  JOB_SQL_CLOSE, JOB_SQL_LOADED, JOB_SQL_LOAD_FAILED,
    JOB_STATUS_HISTORY_CLICK,  JOB_STATUS_HISTORY_CLOSE, JOB_STATUS_HISTORY_LOADED, JOB_STATUS_HISTORY_LOAD_FAILED } from   '../../reduxTypes';

const initialState = {
        open: false,
        itemsType: "",
        titleName: "",
        data: {}
}

export const jobSqlReducer = (state = initialState, action) => {
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

        case JOB_SQL_CLOSE:
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

        case JOB_STATUS_HISTORY_CLOSE:
            return {
                ...state, open: false
            }

            
        default:
            return state
    }
}