import React from 'react';
import { connect } from 'react-redux';

import { useParams, useNavigate, useLocation } from 'react-router-dom'

// dataHub
import dataHub from 'ajax/DataHub';

// actions
import {actionFolderLoaded, actionFolderLoadFailed, actionDeleteItemClick, actionAddFolder,
        actionEditFolder, actionDeleteFolderClick, actionSearchClick, actionChangeParentFolder, actionCopyFolder, actionSortClick} from 'redux/actions/menuViews/folderActions';

// components
import DataLoader from '../../../DataLoader/DataLoader';
import FolderContent from '../../../FolderContent/FolderContent';
import SidebarItems from '../../Sidebar/SidebarItems'


function SecurityFiltersMenuView(props){

    const {id} = useParams()
    const navigate = useNavigate()
    const location = useLocation()

    const state = props.state;

    let reload = {needReload : state.needReload};
    let folderItemsType = SidebarItems.admin.subItems.securityFilters.folderItemType;
    let isSortingAvailable = true;


    function handleFolderClick(folderId) {
        navigate(`/securityFilters/${folderId}`)
    }
    function handleItemClick(securityFilterId) {
        navigate(`/securityFilters/${id}/view/${securityFilterId}`, {state: location.pathname})
    }
    function handleEditItemClick(securityFilterId) {
        navigate(`/securityFilters/${id}/edit/${securityFilterId}`, {state: location.pathname})
    }
    function handleAddItemClick(folderItemsType) {
        navigate(`/securityFilters/${id}/add`, {state: location.pathname})
    }

    return(
        <div  style={{display: 'flex', flex: 1}}>
            <DataLoader
                loadFunc = {dataHub.securityFilterController.getFolder}
                loadParams = {id ? [Number(id)] : [null]}
                reload = {reload}
                onDataLoaded = {(data) => {props.actionFolderLoaded(folderItemsType, data, isSortingAvailable)}}
                onDataLoadFailed = {(message) => {props.actionFolderLoadFailed(folderItemsType, message)}}
            >
                <FolderContent
                    itemsType = {folderItemsType}
                    showAddFolder = {true}
                    showAddItem = {true}
                    data = {state.filteredFolderData ? state.filteredFolderData : state.currentFolderData}
                    searchParams = {state.searchParams || {}}
                    sortParams = {state.sortParams || {}}

                    onFolderClick = {handleFolderClick}
                    onItemClick={handleItemClick}
                    onEditItemClick={handleEditItemClick}
                    onAddItemClick={handleAddItemClick}

                    // onAddFolder = {(name, description) => {props.actionAddFolder(folderItemsType, state.currentFolderData.id, name, description)}}

                    onEditFolderClick = {(folderId, name, description) => {props.actionEditFolder(folderItemsType, state.currentFolderData.id, folderId, name, description)}}
                    onDeleteFolderClick = {(folderId) => {props.actionDeleteFolderClick(folderItemsType, state.currentFolderData.id, folderId)}}
                    onDeleteItemClick = {(securityFilterId) => {props.actionDeleteItemClick(folderItemsType, state.currentFolderId, securityFilterId)}}
                    onSearchClick ={searchParams => {props.actionSearchClick(folderItemsType, state.currentFolderId, searchParams)}}
                    onSortClick ={sortParams => {props.actionSortClick(folderItemsType, state.currentFolderId, sortParams)}}
                    contextAllowed
                    copyAndMoveAllowed
                    notAllowedForItems
                    onChangeParentFolder={(itemsType, folderId, parentFolderId) => props.actionChangeParentFolder(itemsType, folderId, parentFolderId)}
                    onCopyFolder = {(itemsType, destFolderId, folderIds) => props.actionCopyFolder(itemsType, destFolderId, folderIds)}
                />
            </DataLoader>
        </div>
    )
}

const mapStateToProps = state => {
    return {
        state : state.folderData
    }
}

const mapDispatchToProps = {
    actionFolderLoaded,
    actionFolderLoadFailed,
    actionDeleteItemClick,
    actionAddFolder,
    actionEditFolder,
    actionDeleteFolderClick,
    actionSearchClick,
    actionChangeParentFolder,
    actionCopyFolder,
    actionSortClick
}

export default connect(mapStateToProps, mapDispatchToProps)(SecurityFiltersMenuView);
