import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';

import { useParams, useNavigate, useLocation, useSearchParams } from 'react-router-dom'

// dataHub
import dataHub from 'ajax/DataHub';

import { useSnackbar } from 'notistack';

// actions
import { actionFolderLoaded, actionFolderLoadFailed, actionAddFolder, actionEditFolder, 
        actionDeleteFolderClick, actionDeleteItemClick, actionSearchClick, actionChangeParentFolder, actionCopyFolder, actionSortClick } from 'redux/actions/menuViews/folderActions';
import {actionAddDeleteFavorites} from 'redux/actions/favorites/actionFavorites'

// components
import DataLoader from '../../DataLoader/DataLoader';
import FolderContent from '../../FolderContent/FolderContent';
import SidebarItems from '../Sidebar/SidebarItems'


function ReportsMenuView(props){

    const { enqueueSnackbar } = useSnackbar();

    let state = props.state;

    const { id } = useParams()
    const navigate = useNavigate()
    const location = useLocation()
    const [searchParams, setSearchParams] = useSearchParams();
    const locationPreviousHistory = { state: location.pathname + location.search }

    const [reload, setReload] = useState({needReload : state.needReload});

    let folderItemsType = SidebarItems.reports.folderItemType;
    let isSortingAvailable = true;

    useEffect(() => {
        setReload({needReload: true})
    }, [searchParams, state.needReload])

    function handleSearchItems(params) {
        const { searchString, isRecursive } = params

        if (searchString.trim() === '') {
            setSearchParams({})
        } else {
            setSearchParams({search: searchString, isRecursive: isRecursive ?? false})
        }

    }

    async function handleDataLoaded(data) {
        await props.actionFolderLoaded(folderItemsType, data, isSortingAvailable, false, !!searchParams.get("search"))

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
        <div style={{display: 'flex', flex: 1}}>
            <DataLoader
                loadFunc = {dataHub.folderController.getFolder}
                loadParams = {id ? [Number(id)] : [null]}
                reload = {reload}
                isSearchLoading = {state.isSearchLoading}
                onDataLoaded = {(data) => handleDataLoaded(data)}
                onDataLoadFailed = {(message) => {props.actionFolderLoadFailed(folderItemsType, message)}}
            >
                <FolderContent
                    itemsType = {folderItemsType}
                    showAddFolder = {true}
                    showAddItem = {true}
                    data = {state.filteredFolderData ? state.filteredFolderData : state.currentFolderData}
                    searchParams = {state.searchParams || {}}
                    sortParams = {state.sortParams || {}}
                    
                    onFolderClick = {(folderId) => navigate(`/ui/reports/${folderId}`)}
                    onItemClick = {(reportId) => navigate(`/ui/report/starter/${reportId}`, locationPreviousHistory)}
                    onAddItemClick={() => navigate(`/ui/reports/${id}/add`)}

                    onAddFolder = {(name, description) => {props.actionAddFolder(folderItemsType, state.currentFolderData.id, name, description)}}
                    onEditFolderClick = {(folderId, name, description) => {props.actionEditFolder(folderItemsType, state.currentFolderData.id, folderId, name, description)}}
                    // onEditItemClick = {(reportId) => {props.actionEditItemClick(folderItemsType, reportId)}}
                    onDeleteFolderClick = {(folderId) => {props.actionDeleteFolderClick(folderItemsType, state.currentFolderData.id, folderId)}}
                    onDeleteItemClick = {(reportId) => {props.actionDeleteItemClick(folderItemsType, state.currentFolderData.id, reportId)}}
                    onSearchClick = { handleSearchItems }
                    onAddDeleteFavorites = {(index, folderId, reportId, isFavorite) => props.actionAddDeleteFavorites(folderItemsType, index, folderId, reportId, isFavorite, enqueueSnackbar)}
                    onSortClick ={sortParams => {props.actionSortClick(folderItemsType, state.currentFolderData.id, sortParams)}}
                    contextAllowed
                    copyAndMoveAllowed
                    copyAndMoveAllowedForReport
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
    actionAddFolder,
    actionEditFolder,
    actionDeleteFolderClick,
    actionDeleteItemClick,
    actionSearchClick,
    actionAddDeleteFavorites,
    actionChangeParentFolder,
    actionCopyFolder,
    actionSortClick
}

export default connect(mapStateToProps, mapDispatchToProps)(ReportsMenuView);
