import { DESIGN_SETTINGS_SET_VALUE, APPLOGOUT, DARKTHEME, LIGHTTHEME } from '../../reduxTypes';

import SidebarItems from 'main/Main/Sidebar/SidebarItems';
import {FolderItemTypes} from 'main/FolderContent/FolderItemTypes';
// import {folderInitialState} from './folderInitialState';
import { folderDataReducer } from '../folderDataReducer';

import defaultTheme from  '../../../themes/defaultTheme';
import defaultDarkTheme from  '../../../themes/defaultDarkTheme'; 

const isDarkTheme = localStorage.getItem('isDarkTheme')==='true' ? true: false;

const initialState = {
        // ...folderInitialState,
        theme: isDarkTheme ? defaultDarkTheme : defaultTheme,
        darkTheme: isDarkTheme
}
    
export const themesMenuViewReducer = (state = initialState, action) => {
    switch (action.type){

        case DESIGN_SETTINGS_SET_VALUE: 
            return {...state, theme: action.theme}
        case APPLOGOUT: 
            return {...state, darkTheme: action.darkTheme}
        case DARKTHEME:
            return {...state, darkTheme: action.darkTheme, theme: defaultDarkTheme}
        case LIGHTTHEME: 
            return {...state, darkTheme: action.darkTheme, theme: defaultTheme}
        default:
            return folderDataReducer(state, action, SidebarItems.admin.subItems.theme, FolderItemTypes.theme);
    }
    
    
}