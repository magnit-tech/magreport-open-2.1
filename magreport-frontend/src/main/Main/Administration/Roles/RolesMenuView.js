import React, { useEffect } from 'react';
import { connect } from 'react-redux';

import { useParams, useNavigate, useLocation, useSearchParams } from 'react-router-dom'

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

    const { id } = useParams()
    const navigate = useNavigate()
    const location = useLocation()

    let state = props.state;

    console.log(location);

    let reload = {needReload : state.needReload};
    let folderItemsType = SidebarItems.admin.subItems.roles.folderItemType;
    let sidebarItemType = SidebarItems.admin.subItems.roles.key;
    let isSortingAvailable = true;

    let searchParams = location.search ? location.search.replace('?search=', '') : ''


    // useEffect(() => {
    //     if(location.search) {
    //         props.actionSearchClick(folderItemsType, state.currentFolderId, {searchString: location.search.replace('?search=', '')})
    //     }
    // }, [location])

    function handleFolderClick(folderId) {
        navigate(`/ui/roles/${folderId}`)
    }

    function handleItemClick(roleId) {
        if(id) {
            navigate(`/ui/roles/${id}/view/${roleId}`, {state: location.pathname})
        } else {
            const data = state.filteredFolderData ? state.filteredFolderData : state.currentFolderData
            const parentId = data.roles.find(item => item.id === roleId).path[0].id

            navigate(`/ui/roles/${parentId}/view/${roleId}`, {state: location.pathname})
        }
    }

    function handleEditItemClick(roleId) {
        if(id) {
            navigate(`/ui/roles/${id}/edit/${roleId}`, {state: location.pathname})

        } else {
            const data = state.filteredFolderData ? state.filteredFolderData : state.currentFolderData
            const parentId = data.roles.find(item => item.id === roleId).path[0].id

            navigate(`/ui/roles/${parentId}/edit/${roleId}`, {state: location.pathname})
        }
    }

    function handleAddItemClick() {
        navigate(`/ui/roles/${id}/add`, {state: location.pathname})
    }

    function handleSearchItems(params) {
        // searchParams => {props.actionSearchClick(folderItemsType, state.currentFolderId, searchParams)}
        // console.log(searchString);
        navigate(`${location.pathname}?search=${params.searchString}`)
        // props.actionSearchClick(folderItemsType, state.currentFolderId, searchParams)
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
                    onSearchClick = {handleSearchItems}
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