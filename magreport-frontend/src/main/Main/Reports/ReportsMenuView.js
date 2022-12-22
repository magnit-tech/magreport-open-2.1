import React from 'react';
import { connect } from 'react-redux';

import {useParams, useNavigate, useLocation} from 'react-router-dom'

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

    let state = props.state;

    const { id } = useParams()
    const navigate = useNavigate()
    const location = useLocation()
    
    const { enqueueSnackbar } = useSnackbar();

    let reload = {needReload : state.needReload};
    let folderItemsType = SidebarItems.reports.folderItemType;
    let isSortingAvailable = true;

    return(
        <div style={{display: 'flex', flex: 1}}>
            <DataLoader
                loadFunc = {dataHub.folderController.getFolder}
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
                    
                    onFolderClick = {(folderId) => navigate(`/reports/${folderId}`)}
                    onItemClick = {(reportId) => navigate(`/report/starter/${reportId}`, {state: location.pathname})}
                    onAddItemClick={() => navigate(`/reports/${id}/add`)}

                    onAddFolder = {(name, description) => {props.actionAddFolder(folderItemsType, state.currentFolderData.id, name, description)}}
                    onEditFolderClick = {(folderId, name, description) => {props.actionEditFolder(folderItemsType, state.currentFolderData.id, folderId, name, description)}}
                    // onEditItemClick = {(reportId) => {props.actionEditItemClick(folderItemsType, reportId)}}
                    onDeleteFolderClick = {(folderId) => {props.actionDeleteFolderClick(folderItemsType, state.currentFolderData.id, folderId)}}
                    onDeleteItemClick = {(reportId) => {props.actionDeleteItemClick(folderItemsType, state.currentFolderId, reportId)}}
                    onSearchClick ={searchParams => {props.actionSearchClick(folderItemsType, state.currentFolderId, searchParams)}}
                    onAddDeleteFavorites = {(index, folderId, reportId, isFavorite) => props.actionAddDeleteFavorites(folderItemsType, index, folderId, reportId, isFavorite, enqueueSnackbar)}
                    onSortClick ={sortParams => {props.actionSortClick(folderItemsType, state.currentFolderId, sortParams)}}
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
