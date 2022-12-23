import React from 'react';
import { connect } from 'react-redux';

import { useLocation, useNavigate } from 'react-router-dom'

// dataHub
import dataHub from 'ajax/DataHub';

import { useSnackbar } from 'notistack';

// redux
import {actionFolderLoaded, actionFolderLoadFailed, actionSortClick} from 'redux/actions/menuViews/folderActions';
import {actionJobCancel} from 'redux/actions/jobs/actionJobs';
import {actionAddDeleteFavorites} from 'redux/actions/favorites/actionFavorites'

// components
import DataLoader from 'main/DataLoader/DataLoader';
import FolderContent from 'main/FolderContent/FolderContent';
import SidebarItems from '../Sidebar/SidebarItems';


function FavoritesMenuView(props){

    const navigate = useNavigate()
    const location = useLocation()

    const { enqueueSnackbar } = useSnackbar();

    let state = props.state;
    let reload = {needReload : state.needReload};

    let folderItemsType = SidebarItems.favorites.folderItemType;
    let isSortingAvailable = true;


    function handleItemClick(reportId) {
        navigate(`/ui/report/starter/${reportId}`, {state: location.pathname})
    }

    return(
        <div style={{display: 'flex', flex: 1}}>
            <DataLoader
                loadFunc = {dataHub.reportController.getFavorites}
                loadParams = {[]}
                reload = {reload}
                onDataLoaded = {data => props.actionFolderLoaded(folderItemsType, data, isSortingAvailable)}
                onDataLoadFailed = {(message) => {props.actionFolderLoadFailed(folderItemsType, message)}}
            >
                <FolderContent
                    itemsType = {folderItemsType}
                    data = {state.filteredFolderData ? state.filteredFolderData : state.currentFolderData}
                    filters = {props.filters}
                    showAddFolder = {false}
                    showAddItem = {false}
                    showItemControls = {false}
                    pagination = {false}

                    onFolderClick = {false}
                    onItemClick = {handleItemClick}

                    onJobCancelClick = {(jobIndex, jobId) => props.actionJobCancel(folderItemsType, jobIndex, jobId)}
                    onAddDeleteFavorites = {(index, folderId, reportId, isFavorite) => props.actionAddDeleteFavorites(folderItemsType, index, folderId, reportId, isFavorite, enqueueSnackbar)}
                    contextAllowed
                    sortParams = {state.sortParams || {}}
                    onSortClick ={sortParams => {props.actionSortClick(folderItemsType, state.currentFolderId, sortParams)}}
                />
            </DataLoader>
        </div>
    )
}

const mapStateToProps = state => {
    return {
        state : state.folderData,
        currentFolderData : state.folderData.currentFolderData,
    }
}

const mapDispatchToProps = {
    actionFolderLoaded,
    actionFolderLoadFailed,
    actionJobCancel,
    actionAddDeleteFavorites,
    actionSortClick
}

export default connect(mapStateToProps, mapDispatchToProps)(FavoritesMenuView);
