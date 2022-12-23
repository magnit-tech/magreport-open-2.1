import { ADD_NAVBAR, VIEW_ITEM_NAVBAR, EDIT_ITEM_NAVBAR, ADD_ITEM_NAVBAR, ADD_REPORT_NAVBAR, ADD_REPORT_STARTER_NAVBAR } from '../../reduxTypes'

export const addNavbar = (text, itemsType) =>{
    return {
        type: ADD_NAVBAR, 
        text,
        itemsType
    }
}

export const viewItemNavbar = (itemsType, name, id, parentId, path) =>{
    return {
        type: VIEW_ITEM_NAVBAR,
        itemsType,
        name,
        id,
        parentId,
        path
    }
}

export const editItemNavbar = (itemsType, name, id, parentId, path) =>{
    return {
        type: EDIT_ITEM_NAVBAR,
        itemsType,
        name,
        id,
        parentId,
        path
    }
}

export const addItemNavbar = (itemsType, parentId, path) =>{
    return {
        type: ADD_ITEM_NAVBAR,
        itemsType,
        parentId,
        path
    }
}

export const addReportNavbar = (itemsType, name, id) =>{
    return {
        type: ADD_REPORT_NAVBAR, 
        itemsType,
        name,
        id
    }
}

export const addReportStarterNavbar = (itemsType, name, id) =>{
    return {
        type: ADD_REPORT_STARTER_NAVBAR, 
        itemsType,
        name,
        id
    }
}
