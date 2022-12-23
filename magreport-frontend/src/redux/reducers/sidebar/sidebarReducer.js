import { DRAWERTOOGLE } from '../../reduxTypes'


const initialState = {
    drawerOpen: true,
}

export const sidebarReducer = (state = initialState, action) => {

    switch (action.type){

        case DRAWERTOOGLE:
            return { ...state, open: !state.open }
			
        default:
            return state
    }
}