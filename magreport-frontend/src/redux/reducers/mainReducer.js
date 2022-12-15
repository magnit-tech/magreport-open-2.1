import { MAINFLOWSTATE, FOLDER_CONTENT_LOADED } from '../reduxTypes'

const initialState = {
    mainState: MAINFLOWSTATE.init,
    path: ''
}

export const mainReducer = (state = initialState, action) => {
    // if (Object.values(MAINFLOWSTATE).includes(action.type)){
    //     return {mainState: action.type};
    // } else {
    //     return state;
    // }
    switch(action.type){
        case FOLDER_CONTENT_LOADED:
            return {...state, path: action.itemsType}
        default:
            return state;
    }


}