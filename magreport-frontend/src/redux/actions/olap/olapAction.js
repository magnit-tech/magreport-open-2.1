import { SET_AGG_MODAL_PARAMS } from '../../reduxTypes'

export const setAggModalParams = (params) =>{
    return {
        type: SET_AGG_MODAL_PARAMS, 
        params
    }
}