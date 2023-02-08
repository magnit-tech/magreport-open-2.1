import React from 'react';
import { connect } from 'react-redux';

import { useParams, useNavigate, useLocation } from 'react-router-dom'

// dataHub
import dataHub from 'ajax/DataHub';

// actions
import {actionFolderLoaded, actionFolderLoadFailed, actionAddFolder, actionEditFolder, actionDeleteFolderClick, actionDeleteItemClick, 
    actionSearchClick, actionSortClick
} from 'redux/actions/menuViews/folderActions';

// components
import DataLoader from '../../../DataLoader/DataLoader';
import FolderContent from '../../../FolderContent/FolderContent';
import SidebarItems from '../../Sidebar/SidebarItems'


function RolesMenuView(props){

    const {id} = useParams()
    const navigate = useNavigate()
    const location = useLocation()


    let state = props.state;

    let reload = {needReload : state.needReload};
    let folderItemsType = SidebarItems.admin.subItems.roles.folderItemType;
    let sidebarItemType = SidebarItems.admin.subItems.roles.key;
    let isSortingAvailable = true;


    function handleFolderClick(folderId) {
        navigate(`/ui/roles/${folderId}`)
    }

    function handleItemClick(roleId) {
        navigate(`/ui/roles/${id}/view/${roleId}`, {state: location.pathname})
    }

    function handleEditItemClick(roleId) {
        navigate(`/ui/roles/${id}/edit/${roleId}`, {state: location.pathname})
    }

    function handleAddItemClick() {
        navigate(`/ui/roles/${id}/add`, {state: location.pathname})
    }

    
    return(
        <div  style={{display: 'flex', flex: 1}}>
            <DataLoader
                loadFunc = {dataHub.roleController.getType}
                loadParams = {id ? [Number(id)] : [null]}
                reload = {reload}
                onDataLoaded = {(data) => {props.actionFolderLoaded(folderItemsType, data, isSortingAvailable)}}
                onDataLoadFailed = {(message) => {props.actionFolderLoadFailed(folderItemsType, message)}}
            >
                <FolderContent
                    pagination = {true}
                    itemsType = {folderItemsType}
                    showAddFolder = {false}
                    showAddItem = {true}
                    searchParams = {state.searchParams || {}}
                    sortParams = {state.sortParams || {}}
                    data = {state.filteredFolderData ? state.filteredFolderData : state.currentFolderData}

                    onFolderClick = {handleFolderClick}
                    onItemClick={handleItemClick}
                    onEditItemClick={handleEditItemClick}
                    onAddItemClick={handleAddItemClick}

                    onAddFolder = {(name, description) => {props.actionAddFolder(folderItemsType, state.currentFolderData.id, name, description)}}
                    onDeleteFolderClick = {(folderId) => {props.actionDeleteFolderClick(folderItemsType, state.currentFolderData.id, folderId)}}
                    onDeleteItemClick = {(roleId) => {props.actionDeleteItemClick(folderItemsType, state.currentFolderId, roleId)}}
                    onEditFolder = {(folderId, name, description) => {props.actionEditFolder(sidebarItemType, folderItemsType, state.currentFolderData.id, folderId, name, description)}}
                    onSearchClick ={searchParams => {props.actionSearchClick(folderItemsType, state.currentFolderId, searchParams)}}
                    onSortClick ={sortParams => {props.actionSortClick(folderItemsType, state.currentFolderId, sortParams)}}
                    contextAllowed
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
    actionSortClick,
}

export default connect(mapStateToProps, mapDispatchToProps)(RolesMenuView);