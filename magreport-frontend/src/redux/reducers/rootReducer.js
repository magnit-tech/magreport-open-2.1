import { APPLOGOUT } from 'redux/reduxTypes'

import { combineReducers } from 'redux'

// UI
import { alertReducer } from './UI/alertReducer'
import { alertDialogReducer } from './UI/alertDialogReducer'
import { loaderReducer } from './UI/loaderReducer'
import { snackbarReducer } from './UI/snackbarReducer'
import { navbarReducer } from './navbar/navbarReducer'

import { sidebarReducer } from './sidebar/sidebarReducer'

import { folderTreeReducer } from './sidebar/folderTreeReducer'

import { jobDialogReducer } from  './menuViews/jobDialogReducer'
import { asmAdministrationMenuViewReducer } from "./menuViews/asmAdministrationMenuViewReducer"

import { usersReducer } from './admin/usersReducer'
import { rolesReducer } from './admin/rolesReducer'
import { asmDesignerRootReducer } from "./admin/asmDesigner/asmDesignerRootReducer";
import { securityFiltersReducer } from "./admin/securityFiltersReducer";
import { serverSettingsReducer } from './admin/serverSettingsReducer'
import { reportFiltersReducer } from './developer/reportFiltersReducer'
import { reportTemplatesReducer } from './developer/reportTemplatesReducer'
import { themesMenuViewReducer } from './menuViews/themesMenuViewReducer'
import { olapReducer } from './olap/olapReducer'

import { folderDataReducer } from './folderDataReducer'


const appReducer = combineReducers({

    alert: alertReducer,
    alertDialog: alertDialogReducer,
    loader: loaderReducer,
    snackbar: snackbarReducer,
    navbar: navbarReducer,

    sidebar: sidebarReducer,

    // menuViews
    asmAdministrationMenuView : asmAdministrationMenuViewReducer,

    jobDialog: jobDialogReducer,
    themesMenuView: themesMenuViewReducer, // !!!!!

    // **********************************
    users: usersReducer,
    roles: rolesReducer,
    asmDesigner: asmDesignerRootReducer,
    securityFilters: securityFiltersReducer,
    serverSettings: serverSettingsReducer, //!!!!!!

    reportFilters: reportFiltersReducer,
    reportTemplates: reportTemplatesReducer, 

    folderTree: folderTreeReducer,

    //olap
    olap: olapReducer,

    folderData: folderDataReducer
})

export const rootReducer = (state, action) => {
    if (action.type === APPLOGOUT) {
        state = undefined
    }

    return appReducer(state, action)
}