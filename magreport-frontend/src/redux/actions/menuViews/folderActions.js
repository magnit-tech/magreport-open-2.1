import store from 'redux/store';
import dataHub from 'ajax/DataHub';
import {dataHubItemController, folderItemTypeName, FolderItemTypes} from 'main/FolderContent/FolderItemTypes';

import {
    FOLDER_CONTENT_LOAD_FAILED,
    FOLDER_CONTENT_LOADED,
    FOLDER_CONTENT_ITEM_DELETED,
    FOLDER_CONTENT_ITEM_DELETE_FAILED,
    FOLDER_CONTENT_ADD_FOLDER,
    FOLDER_CONTENT_ADD_FOLDER_FAILED,
    FOLDER_CONTENT_FOLDER_ADDED,
    FOLDER_CONTENT_EDIT_FOLDER,
    FOLDER_CONTENT_FOLDER_EDITED,
    FOLDER_CONTENT_EDIT_FOLDER_FAILED,
    FOLDER_CONTENT_FOLDER_DELETED,
    FOLDER_CONTENT_DELETE_FOLDER_FAILED,
    HIDEALERTDIALOG,
    SHOWALERTDIALOG,
    FOLDER_CONTENT_SEARCH_CLICK,
    FOLDER_CONTENT_SORT_CLICK,
    FOLDER_CONTENT_SEARCH_LOADING,
    FOLDER_CONTENT_SEARCH_RESULTS_LOADED,
    FOLDER_CONTENT_SEARCH_RESULTS_FAILED,
    FOLDERS_TREE_CHANGE_PARENT_STARTED, 
    FOLDERS_TREE_PARENT_CHANGED, 
    FOLDERS_TREE_PARENT_CHANGED_FAIL,
    SCHEDULE_TASK_RUN_OK,
    SCHEDULE_TASK_RUN_FAILED,
    FOLDER_CONTENT_CHANGE_PARENT_FOLDER,
    FOLDER_CONTENT_PARENT_FOLDER_CHANGED,
    FOLDER_CONTENT_PARENT_FOLDER_CHANGED_FAIL,
    FOLDER_CONTENT_MOVE_ITEM,
    FOLDER_CONTENT_ITEM_MOVED,
    FOLDER_CONTENT_ITEM_MOVED_FAILED,
    FOLDER_CONTENT_COPY_ITEM,
    FOLDER_CONTENT_ITEM_COPIED,
    FOLDER_CONTENT_ITEM_COPIED_FAILED,
    FOLDER_CONTENT_COPY_FOLDER,
    FOLDER_CONTENT_FOLDER_COPIED,
    FOLDER_CONTENT_FOLDER_COPIED_FAILED
} from 'redux/reduxTypes';

export function actionFolderLoadFailed (itemsType, errorMessage){
    return {
        type : FOLDER_CONTENT_LOAD_FAILED,
        itemsType : itemsType,
        errorMessage : errorMessage
    }
}

export function actionFolderLoaded(itemsType, folderData, isSortingAvailable=false, isFolderItemPicker=false, isSearchLoading = false){
    return {
        type : FOLDER_CONTENT_LOADED,
        itemsType : itemsType,
        folderData : folderData,
        isSortingAvailable,
        isFolderItemPicker,
        isSearchLoading
    }
}

function handleItemDeleted(itemType, parentFolderId, itemData, magrepResponse){
    if(magrepResponse.ok){
        store.dispatch({
            type : FOLDER_CONTENT_ITEM_DELETED,
            itemType : itemType,
            parentFolderId : parentFolderId,
            itemData : itemData
        });
    }
    else{
        store.dispatch({
            type: FOLDER_CONTENT_ITEM_DELETE_FAILED,
            itemType : itemType,
            parentFolderId : parentFolderId,
            itemData : itemData,
            errorMessage : magrepResponse.data
        });
    }
}

function handleDeleteItemAlertAnswer(answer, entityType, entity){

    if(answer){
        let serviceCallFunction = {};
        if (entity.itemType === FolderItemTypes.scheduleTasks){
            serviceCallFunction = dataHubItemController(entity.itemType).taskDelete;
        } else {
            serviceCallFunction = dataHubItemController(entity.itemType).delete;
        }
        
        const itemData = dataHub.localCache.getItemData(entity.itemType, entity.itemId);
        if (entity.itemType === FolderItemTypes.reports){
            serviceCallFunction(entity.parentFolderId, entity.itemId, 
                magrepResponse => {handleItemDeleted(entity.itemType, entity.parentFolderId, itemData, magrepResponse)});
        }
        else {
            serviceCallFunction(entity.itemId, 
                (magrepResponse) => {handleItemDeleted(entity.itemType, entity.parentFolderId, itemData, magrepResponse)});
        }
    }

    store.dispatch({
        type : HIDEALERTDIALOG
    });
}


export function actionDeleteItemClick(itemType, parentFolderId, itemId){

    let itemTypeName = folderItemTypeName(itemType);
    let itemName = dataHub.localCache.getItemData(itemType, itemId).name;

    return {
        type : SHOWALERTDIALOG,
        title : "Удалить " + itemTypeName + " " + itemName + "?",
        entityType : itemType,
        entity : {itemType, parentFolderId, itemId},
        callback : handleDeleteItemAlertAnswer
    }
}

function handleScheduleTaskRun(itemType, entity, magrepResponse){
    if(magrepResponse.ok){
        store.dispatch({
            type : SCHEDULE_TASK_RUN_OK,
            itemType : itemType,
            id : entity.id,
            name: entity.name
        });
    }
    else{
        store.dispatch({
            type: SCHEDULE_TASK_RUN_FAILED,
            itemType : itemType,
            id: entity.id,
            name: entity.name,
            errorMessage : magrepResponse.data
        });
    }
}

function handleScheduleTaskRunAlertAnswer(answer, entityType, entity){

    if(answer){
        let serviceCallFunction = dataHubItemController(FolderItemTypes.scheduleTasks).taskRun;
        
      //  const itemData = dataHub.localCache.getItemData(FolderItemTypes.scheduleTasks, id);
        
        serviceCallFunction(entity.id,
            (magrepResponse) => {handleScheduleTaskRun(FolderItemTypes.scheduleTasks, entity, magrepResponse)});

    }

    store.dispatch({
        type : HIDEALERTDIALOG
    });
}

export function actionScheduleTaskRunClick(itemType, id){

    let itemName = dataHub.localCache.getItemData(itemType, id).name;
    return {
        type: SHOWALERTDIALOG,
        title: "Запустить отчёт на расписании " + itemName + " вне очереди?",
        entityType: itemType,
        entity: {id: id, name: itemName},
        callback: handleScheduleTaskRunAlertAnswer
    }
}

function handleFolderAdded(itemsType, parentFolderId, magrepResponse){
    let type = magrepResponse.ok ? FOLDER_CONTENT_FOLDER_ADDED : FOLDER_CONTENT_ADD_FOLDER_FAILED;
    let data = magrepResponse.data;

    store.dispatch({
        type: type,
        itemsType : itemsType,
        parentFolderId : parentFolderId,
        data : data
    });
}

export function actionAddFolder(itemsType, parentFolderId, folderName, folderDescription){
    const serviceCallFunction = dataHubItemController(itemsType).addFolder;

    serviceCallFunction(parentFolderId, folderName, folderDescription, (magrepResponse) => {handleFolderAdded(itemsType, parentFolderId, magrepResponse)});

    return {
        type: FOLDER_CONTENT_ADD_FOLDER,
        itemsType : itemsType,
        parentFolderId : parentFolderId,
        folderName: folderName, 
        folderDescription : folderDescription
    }
}

function handleFolderEdited(itemsType, parentFolderId, folderId, magrepResponse){
    let type = magrepResponse.ok ? FOLDER_CONTENT_FOLDER_EDITED : FOLDER_CONTENT_EDIT_FOLDER_FAILED;
    let data = magrepResponse.data;

    store.dispatch({
        type: type,
        itemsType : itemsType,
        parentFolderId : parentFolderId,
        folderId: folderId,
        data : data,
        folderData : data
    });
}

export function actionEditFolder(itemsType, parentFolderId, folderId, folderName, folderDescription){
    const serviceCallFunction = dataHubItemController(itemsType).renameFolder;


    serviceCallFunction(folderDescription, folderId, folderName, magrepResponse => {handleFolderEdited(itemsType, parentFolderId, folderId, magrepResponse)});

    return {
        type: FOLDER_CONTENT_EDIT_FOLDER,
        itemsType: itemsType,
        parentFolderId: parentFolderId,
        folderId: folderId,
        folderName: folderName, 
        folderDescription: folderDescription
    }
}

function handleFolderDeleted(itemsType, parentFolderId, folderData, magrepResponse){
    let type = magrepResponse.ok ? FOLDER_CONTENT_FOLDER_DELETED : FOLDER_CONTENT_DELETE_FOLDER_FAILED;

    store.dispatch({
        type: type,
        itemsType : itemsType,
        parentFolderId : parentFolderId,
        folderData: folderData,
        errorMessage : (magrepResponse.ok === false) && magrepResponse.data
    });
}

function handleDeleteFolderAlertAnswer(answer, entityType, entity){

    if(answer){
        const serviceCallFunction = dataHubItemController(entity.itemsType).deleteFolder;
        const folderData = dataHub.localCache.getFolderData(entity.itemsType, entity.folderId);
        serviceCallFunction(entity.folderId, 
            (magrepResponse) => {handleFolderDeleted(entity.itemsType, entity.parentFolderId, folderData, magrepResponse)});
    }

    store.dispatch({
        type : HIDEALERTDIALOG
    });
}

export function actionDeleteFolderClick(itemsType, parentFolderId, folderId){

    let folderName = dataHub.localCache.getFolderData(itemsType, folderId).name;
    return {
        type : SHOWALERTDIALOG,
        title : "Удалить каталог " + folderName + "?",
        entityType : itemsType,
        entity : {itemsType, parentFolderId, folderId},
        callback : handleDeleteFolderAlertAnswer
    }
}

export function actionSearchClick(itemsType, folderId, searchParams){
    if (searchParams.isRecursive){
        const serviceCallFunction = dataHubItemController(itemsType).search;
        serviceCallFunction("CONTAINS", searchParams.isRecursive, folderId, searchParams.searchString, magrepResponse => {handleFolderSearch(itemsType, searchParams, magrepResponse)});
        return {
            type: FOLDER_CONTENT_SEARCH_LOADING,
            itemsType: itemsType,
        }
    }
    else {
        return {
            type: FOLDER_CONTENT_SEARCH_CLICK,
            itemsType,
            searchParams
        } 
    }
}

export function actionSortClick(itemsType, folderId, sortParams){
    return {
        type: FOLDER_CONTENT_SORT_CLICK,
        itemsType,
        sortParams
    }
}

function handleFolderSearch(itemsType, searchParams, magrepResponse){
    let type = magrepResponse.ok ? FOLDER_CONTENT_SEARCH_RESULTS_LOADED : FOLDER_CONTENT_SEARCH_RESULTS_FAILED;
    let data = magrepResponse.data;
    
    store.dispatch({
        type,
        itemsType,
        data,
        searchParams,
        errorMessage: magrepResponse.ok === false && magrepResponse.data 
    });
}

export function actionChangeParentFolder(itemsType, folderId, parentFolderId){

    const serviceCallFunction = dataHubItemController(itemsType).changeParentFolder;
    
    serviceCallFunction(folderId, parentFolderId, magrepResponse => handleParentFolderChanged(itemsType, folderId, parentFolderId, magrepResponse));
    
    return {
        type: FOLDER_CONTENT_CHANGE_PARENT_FOLDER,
        itemsType,
        folderId,
        parentFolderId,
    }
}

function handleParentFolderChanged(itemsType, folderId, parentFolderId, magrepResponse){
    const type = magrepResponse.ok ? FOLDER_CONTENT_PARENT_FOLDER_CHANGED : FOLDER_CONTENT_PARENT_FOLDER_CHANGED_FAIL
    const data = magrepResponse.data;

    store.dispatch({
        type,
        itemsType,
        folderId,
        parentFolderId,
        data
    });
}

export function actionChangeParentFolderTree(itemsType, folderId, parentFolderId, inRoot){

    const serviceCallFunction = dataHubItemController(itemsType).changeParentFolder;
    if (inRoot){
        parentFolderId = null
    }
    
    serviceCallFunction(folderId, parentFolderId, magrepResponse => handleParentFolderTreeChanged(itemsType, folderId, parentFolderId, magrepResponse));
    
    return {
        type: FOLDERS_TREE_CHANGE_PARENT_STARTED,
        itemsType,
        folderId,
        parentFolderId,
    }
}

function handleParentFolderTreeChanged(itemsType, folderId, parentFolderId, magrepResponse){
    const type = magrepResponse.ok ? FOLDERS_TREE_PARENT_CHANGED : FOLDERS_TREE_PARENT_CHANGED_FAIL

    store.dispatch({
        type,
        itemsType,
        folderId,
        parentFolderId,
        errorMessage: magrepResponse.ok === false && magrepResponse.data 
    });
}

export function actionMoveFolderItem(itemsType, destFolderId, objIds, textForSnackbar){

    const serviceCallFunction = dataHubItemController(itemsType).moveObj;
    
    serviceCallFunction(destFolderId, objIds, magrepResponse => handleMoveFolderItem(itemsType, destFolderId, objIds, magrepResponse, textForSnackbar));
    
    return {
        type: FOLDER_CONTENT_MOVE_ITEM,
        itemsType,
        destFolderId, 
        objIds,
    }
}

function handleMoveFolderItem(itemsType, destFolderId, objIds, magrepResponse, textForSnackbar){
    const type = magrepResponse.ok ? FOLDER_CONTENT_ITEM_MOVED : FOLDER_CONTENT_ITEM_MOVED_FAILED

    store.dispatch({
        type,
        itemsType,
        destFolderId, 
        objIds,
        textForSnackbar
    });
}

export function actionCopyFolderItem(itemsType, destFolderId, objIds, textForSnackbar){

    const serviceCallFunction = dataHubItemController(itemsType).copyObj;
    
    serviceCallFunction(destFolderId, objIds, magrepResponse => handleCopyFolderItem(itemsType, destFolderId, objIds, magrepResponse, textForSnackbar));
    
    return {
        type: FOLDER_CONTENT_COPY_ITEM,
        itemsType,
        destFolderId, 
        objIds,
    }
}

function handleCopyFolderItem(itemsType, destFolderId, objIds, magrepResponse, textForSnackbar){
    const type = magrepResponse.ok ? FOLDER_CONTENT_ITEM_COPIED : FOLDER_CONTENT_ITEM_COPIED_FAILED

    store.dispatch({
        type,
        itemsType,
        destFolderId, 
        objIds,
        textForSnackbar
    });
}

export function actionCopyFolder(itemsType, destFolderId, folderIds){

    const serviceCallFunction = dataHubItemController(itemsType).copyFolder;
    
    serviceCallFunction(destFolderId, folderIds, magrepResponse => handleCopyFolder(itemsType, destFolderId, folderIds, magrepResponse));
    
    return {
        type: FOLDER_CONTENT_COPY_FOLDER,
        itemsType,
        destFolderId,
        folderIds
    }
}

function handleCopyFolder(itemsType, destFolderId, folderIds, magrepResponse){
    const type = magrepResponse.ok ? FOLDER_CONTENT_FOLDER_COPIED : FOLDER_CONTENT_FOLDER_COPIED_FAILED
    const data = magrepResponse.data[0];

    store.dispatch({
        type,
        itemsType,
        destFolderId,
        folderIds,
        data
    });
}