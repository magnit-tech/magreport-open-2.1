import { SET_AGG_MODAL_PARAMS } from '../../reduxTypes'

const initialState = {
    aggModalParams: {
        open: false, 
        index: 0, 
        type: 'add', 
        dataType: null
    }
}

export const olapReducer = (state = initialState, action) => {
    switch (action.type){
        case SET_AGG_MODAL_PARAMS:
            return {...state, aggModalParams: action.params};
        default:
            return state
    }
}