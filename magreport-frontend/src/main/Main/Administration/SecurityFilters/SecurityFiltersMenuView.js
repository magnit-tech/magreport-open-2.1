import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';

import { useParams, useNavigate, useLocation, useSearchParams } from 'react-router-dom'

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

    const state = props.state;

    const {id} = useParams()
    const navigate = useNavigate()
    const location = useLocation()
    const [searchParams, setSearchParams] = useSearchParams();
    const locationPreviousHistory = { state: location.pathname + location.search }

    const [reload, setReload] = useState({needReload : state.needReload});
    const folderItemsType = SidebarItems.admin.subItems.securityFilters.folderItemType;
    const showAddBtn = searchParams.get("isRecursive") === 'true' ? false : true;

    useEffect(() => {
        setReload({needReload: true})
    }, [searchParams, state.needReload])


    function handleFolderClick(folderId) {
        navigate(`/ui/securityFilters/${folderId}`)
    }
    function handleItemClick(securityFilterId) {
        if (id) {
            navigate(`/ui/securityFilters/${id}/view/${securityFilterId}`, locationPreviousHistory)
        } else {
            let path = props.state.filteredFolderData ? props.state.filteredFolderData.securityFilters.find(i => i.id === securityFilterId).path : props.state.currentFolderData.securityFilters.find(i => i.id === securityFilterId).path;
            navigate(`/ui/securityFilters/${path[path.length - 1].id}/view/${securityFilterId}`, locationPreviousHistory)
        }
    }
    function handleEditItemClick(securityFilterId) {
        if (id) {
            navigate(`/ui/securityFilters/${id}/edit/${securityFilterId}`, locationPreviousHistory)
        } else {
            let path = props.state.filteredFolderData ? props.state.filteredFolderData.securityFilters.find(i => i.id === securityFilterId).path : props.state.currentFolderData.securityFilters.find(i => i.id === securityFilterId).path;
            navigate(`/ui/securityFilters/${path[path.length - 1].id}/edit/${securityFilterId}`, locationPreviousHistory)
        } 
    }
    function handleAddItemClick() {
        navigate(`/ui/securityFilters/${id}/add`, locationPreviousHistory)
    }
    function handleSearchItems(params) {
        const { searchString } = params

        if (searchString.trim() === '') {
            setSearchParams({})
        } else {
            setSearchParams({search: searchString, isRecursive: true})
        }

    }

    async function handleDataLoaded(data) {
        await props.actionFolderLoaded(folderItemsType, data, true, false, !!searchParams.get("search"))

        if(searchParams.get("search")) {
            const actionSearchParams = {
                open: true,
                searchString: searchParams.get("search"),
                isRecursive: true,
            }

            await props.actionSearchClick(folderItemsType, state.currentFolderId, actionSearchParams)
        }
    }

    return(
        <div  style={{display: 'flex', flex: 1}}>
            <DataLoader
                loadFunc = {dataHub.securityFilterController.getFolder}
                loadParams = {id ? [Number(id)] : [null]}
                reload = {reload}
                isSearchLoading = {state.isSearchLoading}
                onDataLoaded = {(data) => handleDataLoaded(data)}
                onDataLoadFailed = {(message) => {props.actionFolderLoadFailed(folderItemsType, message)}}
            >
                <FolderContent
                    itemsType = {folderItemsType}
                    showAddFolder = {showAddBtn}
                    showAddItem = {showAddBtn}
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
                    onSearchClick = {handleSearchItems}
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
