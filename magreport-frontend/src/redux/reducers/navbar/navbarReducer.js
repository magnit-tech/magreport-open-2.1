import React from "react";
import { Edit, NoteAdd, Pageview, ListAlt, LibraryBooks } from "@material-ui/icons"

import {
    FOLDER_CONTENT_LOADED,
    ADD_NAVBAR,
    VIEW_ITEM_NAVBAR,
    EDIT_ITEM_NAVBAR,
    ADD_ITEM_NAVBAR,
    ADD_REPORT_NAVBAR,
    ADD_REPORT_STARTER_NAVBAR,
    ASM_DATA_LOADED
} from 'redux/reduxTypes';

import SidebarItems from '../../../main/Main/Sidebar/SidebarItems';
import {folderItemTypeName, FolderItemTypes} from 'main/FolderContent/FolderItemTypes.js';

const initialState = {
    items: []
}


function buildNavigationPathToFolder(itemsType, path){

    let sidebarItem = itemsType === FolderItemTypes.reports ? SidebarItems.reports
                     :itemsType === FolderItemTypes.favorites ? SidebarItems.favorites
                     :itemsType === FolderItemTypes.datasource ? SidebarItems.development.subItems.datasources
                     :itemsType === FolderItemTypes.dataset ? SidebarItems.development.subItems.datasets
                     :itemsType === FolderItemTypes.filterTemplate ? SidebarItems.development.subItems.filterTemplates
                     :itemsType === FolderItemTypes.filterInstance ? SidebarItems.development.subItems.filterInstances
                     :itemsType === FolderItemTypes.reportsDev ? SidebarItems.development.subItems.reportsDev
                     :itemsType === FolderItemTypes.roles ? SidebarItems.admin.subItems.roles
                     :itemsType === FolderItemTypes.securityFilters ? SidebarItems.admin.subItems.securityFilters
                     :itemsType === FolderItemTypes.userJobs ? SidebarItems.admin.subItems.userJobs
                     :itemsType === FolderItemTypes.job ? SidebarItems.jobs
                     :itemsType === FolderItemTypes.asm ? SidebarItems.admin.subItems.ASMAdministration
                     :itemsType === FolderItemTypes.schedules ? SidebarItems.schedule.subItems.schedules
                     :itemsType === FolderItemTypes.scheduleTasks ? SidebarItems.schedule.subItems.scheduleTasks
                     :itemsType === FolderItemTypes.systemMailTemplates ? SidebarItems.admin.subItems.mailTexts
                     :itemsType === FolderItemTypes.theme ? SidebarItems.admin.subItems.theme
                     :itemsType === FolderItemTypes.cubes ? SidebarItems.admin.subItems.cubes
                     :  SidebarItems.reports;

    let navbarArray = []

    const firstLink = {
        text : sidebarItem.text,
        icon : sidebarItem.icon,
        action: null,
        itemsType,
        id: null,
        parentId: null,
        isLast: (path && path.length > 0) ? false : true
    }

    navbarArray.push(firstLink)

    if (path && path.length > 0) {
        path.forEach((item, index) => {
            navbarArray.push({
                text : item.name,
                icon : null,
                itemsType,
                id: item.id,
                parentId: item.parentId,
                isLast: path.length - 1 === index ? true : false
            })
        })
    }
    
    return navbarArray
}

function buildNavigationPathToViewItem(itemsType, name, id, parentId, path) {
    const icon = <Pageview/>;
    let newNavbar = buildNavigationPathToFolder(itemsType, path)

    const newItem = {
        icon,
        text: name,
        action: 'view',
        itemsType,
        id,
        parentId,
        isLast: true,
    }
    
    const newItems = [...newNavbar, newItem];
    
    newItems.slice(0, newItems.length - 1).forEach(i => i.isLast = false);
    return newItems;
}

function buildNavigationPathToEditItem(items, itemsType, name, id, parentId, path) {
    const icon = <Edit/>;

    const newItem = {
        icon,
        text: name,
        action: 'edit',
        itemsType,
        id,
        parentId,
        isLast: true,
    }

    if (items.length > 0) {
        const newItems = [...items, newItem];
        newItems.slice(0, newItems.length - 1).forEach(i => i.isLast = false);
        return newItems;
    } else {
        let newNavbar = buildNavigationPathToFolder(itemsType, path)
        const newItems = [...newNavbar, newItem];
        newItems.slice(0, newItems.length - 1).forEach(i => i.isLast = false);
        return newItems;
    }
}

function buildNavigationPathToNewItem(itemsType, parentId, path) {
    const icon = <NoteAdd />;
    const itemName = folderItemTypeName(itemsType);
    const name = `Создание: ${itemName}`;

    const newItem = {
        icon,
        text: name,
        action: 'add',
        itemsType,
        id: null,
        parentId,
        isLast: true,
    }

    let newNavbar = buildNavigationPathToFolder(itemsType, path)
    const newItems = [...newNavbar, newItem];
    newItems.slice(0, newItems.length - 1).forEach(i => i.isLast = false);
    return newItems;
}

function buildNavigationPathToReport(items, itemsType, name, id) {
    const icon = <ListAlt/>;

    const newItem = {
        icon,
        text: name,
        action: null,
        itemsType,
        id,
        parentId: null,
        isLast: true,
    }

    if (items.length > 0) { 
        if(items[items.length - 1].itemsType !== itemsType) {
            const newItems = [...items, newItem];
            newItems.slice(0, newItems.length - 1).forEach(i => i.isLast = false);
            return newItems;
        } else {
            return items
        }
    } else {
        return [newItem]
    }
}

function buildNavigationPathToReportStarter(items, itemsType, name, id) {
    const icon = <LibraryBooks/>;

    const newItem = {
        icon,
        text: name,
        action: null,
        itemsType,
        id,
        parentId: null,
        isLast: true,
    }

    const newItems = [...items, newItem];
    newItems.slice(0, newItems.length - 1).forEach(i => i.isLast = false);
    return newItems;
}



export const navbarReducer = (state = initialState, action) => {
    switch (action.type){
        
        case FOLDER_CONTENT_LOADED:
            if(action.isFolderItemPicker) {
                return state;
            }
            return {
                items : buildNavigationPathToFolder(action.itemsType, action.folderData.path, true)
            }

        case ADD_NAVBAR:
            return { items: [{
                text : action.text,
                icon : null,
                itemsType: action.itemsType,
                id: null,
                isLast: true,
            }]}

        case VIEW_ITEM_NAVBAR:
            return {
                items: buildNavigationPathToViewItem(action.itemsType, action.name, action.id, action.parentId, action.path)
            };

        case EDIT_ITEM_NAVBAR:
            return {
                items: buildNavigationPathToEditItem(state.items, action.itemsType, action.name, action.id, action.parentId, action.path)
            };

        case ADD_ITEM_NAVBAR:
            return {
                items: buildNavigationPathToNewItem(action.itemsType, action.parentId, action.path)
            };

        case ADD_REPORT_NAVBAR:
            return {
                items: buildNavigationPathToReport(state.items, action.itemsType, action.name, action.id)
            };

        case ADD_REPORT_STARTER_NAVBAR:
            return {
                items: buildNavigationPathToReportStarter(state.items, action.itemsType, action.name, action.id)
            };

        case ASM_DATA_LOADED:
            if (action.actionFor === 'view') {
                return {
                    items: buildNavigationPathToViewItem('asm', action.data.name, action.data.id, null, null)
                }
            }
            return {
                items: buildNavigationPathToEditItem(state.items, 'asm', action.data.name, action.data.id, null, null)
            }
            
        default:
            return state
    }
}