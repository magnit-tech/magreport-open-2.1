import React, { useState, useEffect } from 'react';
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

    const state = props.state;

    const { id } = useParams()
    const navigate = useNavigate()
    const location = useLocation()
    const [searchParams, setSearchParams] = useSearchParams();
    const locationPreviousHistory = { state: location.pathname + location.search }

    const [reload, setReload] = useState({needReload : state.needReload});
    const folderItemsType = SidebarItems.admin.subItems.roles.folderItemType;
    const sidebarItemType = SidebarItems.admin.subItems.roles.key;
    const showAddBtn = searchParams.get("isRecursive") === 'true' ? false : true;

    useEffect(() => {
        setReload({needReload: true})
    }, [searchParams, state.needReload])

    function handleFolderClick(folderId) {
        navigate(`/ui/roles/${folderId}`)
    }
    function handleItemClick(roleId) {
        if(id) {
            navigate(`/ui/roles/${id}/view/${roleId}`, locationPreviousHistory)
        } else {
            const data = state.filteredFolderData ? state.filteredFolderData : state.currentFolderData
            const parentId = data.roles.find(item => item.id === roleId).path[0].id

            navigate(`/ui/roles/${parentId}/view/${roleId}`, locationPreviousHistory)
        }
    }
    function handleEditItemClick(roleId) {
        if(id) {
            navigate(`/ui/roles/${id}/edit/${roleId}`, locationPreviousHistory)

        } else {
            const data = state.filteredFolderData ? state.filteredFolderData : state.currentFolderData
            const parentId = data.roles.find(item => item.id === roleId).path[0].id

            navigate(`/ui/roles/${parentId}/edit/${roleId}`, locationPreviousHistory)
        }
    }
    function handleAddItemClick() {
        navigate(`/ui/roles/${id}/add`, locationPreviousHistory)
    }
    function handleSearchItems(params) {
        const {searchString, isRecursive} = params

        if (searchString.trim() === '') {
            setSearchParams({})
        } else {
            setSearchParams({search: searchString, isRecursive: isRecursive ?? false})
        }

    }

    async function handleDataLoaded(data) {
        await props.actionFolderLoaded(folderItemsType, data, true, false, !!searchParams.get("search"))

        if(searchParams.get("search")) {
            const actionSearchParams = {
                open: true,
                searchString: searchParams.get("search"),
                isRecursive: searchParams.get("isRecursive") === 'true' ? true : false,
            }

            await props.actionSearchClick(folderItemsType, state.currentFolderId, actionSearchParams)
        }
    }
    
    return(
        <div  style={{display: 'flex', flex: 1}}>
            <DataLoader
                loadFunc = {dataHub.roleController.getType}
                loadParams = {id ? [Number(id)] : [null]}
                isSearchLoading = {state.isSearchLoading}
                reload = {reload}
                onDataLoaded = {(data) => handleDataLoaded(data)}
                onDataLoadFailed = {(message) => {props.actionFolderLoadFailed(folderItemsType, message)}}
            >
                <FolderContent
                    pagination = {true}
                    itemsType = {folderItemsType}
                    showAddFolder = {showAddBtn}
                    showAddItem = {showAddBtn}
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