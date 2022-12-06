import React from 'react';
import { connect } from 'react-redux';
import {useNavigateBack} from "components/Navbar/navbarHooks";

import { useParams, useNavigate, useLocation } from 'react-router-dom'

// dataHub
import dataHub from 'ajax/DataHub';

// actions
import {actionFolderLoaded, actionFolderLoadFailed, actionFolderClick, actionItemClick, actionAddFolder, 
    actionAddItemClick, actionEditItemClick, actionDeleteItemClick, actionEditFolder, actionDeleteFolderClick, 
    actionGetDependencies, actionSearchClick, actionChangeParentFolder, actionCopyFolder, actionMoveFolderItem, actionCopyFolderItem, actionSortClick
} from 'redux/actions/menuViews/folderActions';
import actionSetSidebarItem from 'redux/actions/sidebar/actionSetSidebarItem';

// components
import DataLoader from '../../../DataLoader/DataLoader';
import FolderContent from '../../../FolderContent/FolderContent';
import SidebarItems from '../../Sidebar/SidebarItems';
import DatasetViewer from "./DatasetViewer";
import DatasetDesigner from './DatasetDesigner';
import DependencyViewer from '../DependencyViewer'

import { folderItemTypeSidebarItem } from 'main/FolderContent/folderItemTypeSidebarItem';

// state const
import {FLOW_STATE_BROWSE_FOLDER, datasetsMenuViewFlowStates} from 'redux/reducers/menuViews/flowStates';

function DatasetsMenuView(props){

    const {id} = useParams()
    const navigate = useNavigate()
    const location = useLocation()

    let folderItemsType = SidebarItems.development.subItems.datasets.folderItemType;

    const navigateBack = useNavigateBack(SidebarItems.development.subItems.datasets.key);

    let state = props.state;

    let reload = {needReload : state.needReload};

    let isSortingAvailable = true;

    function handleDependencyPathClick(argFolderItemsType, folderId){
        props.actionSetSidebarItem(folderItemTypeSidebarItem(argFolderItemsType));
        props.actionFolderClick(argFolderItemsType, folderId)
    }

    function handleExit(){
        navigateBack();
    }

    function handleFolderClick(folderId) {
        props.actionFolderClick(folderItemsType, folderId)
        navigate(`/dataset/${folderId}`)
    }
    function handleItemClick(datasetId) {
        props.actionItemClick(folderItemsType, datasetId)
        navigate(`/dataset/${id}/view/${datasetId}`, {state: location.pathname})
    }
    function handleEditItemClick(datasetId) {
        props.actionEditItemClick(folderItemsType, datasetId)
        navigate(`/dataset/${id}/edit/${datasetId}`, {state: location.pathname})
    }
    function handleDependenciesClick(datasetId) {
        props.actionGetDependencies(folderItemsType, datasetId)
        // navigate(`/dataset/dependencies/${datasetId}`)
    }
    function handleAddItemClick(folderItemsType) {
        props.actionAddItemClick(folderItemsType)
        navigate(`/dataset/${id}/add`, {state: location.pathname})
    }

    return(
        <div style={{display: 'flex', flex: 1}}>
        {
            state.flowState === FLOW_STATE_BROWSE_FOLDER ?
            <DataLoader
                loadFunc = {dataHub.datasetController.getFolder}
                loadParams = {id ? [Number(id)] : [null]}
                reload = {reload}
                onDataLoaded = {(data) => {props.actionFolderLoaded(folderItemsType, data, isSortingAvailable)}}
                onDataLoadFailed = {(message) => {props.actionFolderLoadFailed(folderItemsType, message)}}
            >
                <FolderContent
                    itemsType = {folderItemsType}
                    data = {state.filteredFolderData ? state.filteredFolderData : state.currentFolderData}
                    searchParams = {state.searchParams || {}}
                    sortParams = {state.sortParams || {}}
                    showAddFolder = {true}
                    showAddItem = {true}

                    onFolderClick = {handleFolderClick}
                    onItemClick={handleItemClick}
                    onEditItemClick={handleEditItemClick}
                    onDependenciesClick = {handleDependenciesClick}
                    onAddItemClick={handleAddItemClick}

                    // onFolderClick = {(folderId) => {props.actionFolderClick(folderItemsType, folderId)}}
                    // onItemClick = {(datasetId) => {props.actionItemClick(folderItemsType, datasetId)}}
                    // onEditItemClick = {(datasetId) => {props.actionEditItemClick(folderItemsType, datasetId)}}
                    // onDependenciesClick = {datasetId => props.actionGetDependencies(folderItemsType, datasetId)}
                    // onAddItemClick = {() => {props.actionAddItemClick(folderItemsType)}}


                    onAddFolder = {(name, description) => {props.actionAddFolder(folderItemsType, state.currentFolderData.id, name, description)}}
                    onEditFolderClick = {(folderId, name, description) => {props.actionEditFolder(folderItemsType, state.currentFolderData.id, folderId, name, description)}}
                    onDeleteFolderClick = {(folderId) => {props.actionDeleteFolderClick(folderItemsType, state.currentFolderData.id, folderId)}}
                    onDeleteItemClick = {(datasetId) => {props.actionDeleteItemClick(folderItemsType, state.currentFolderId, datasetId)}}
                    onSearchClick ={searchParams => {props.actionSearchClick(folderItemsType, state.currentFolderId, searchParams)}}
                    onSortClick ={sortParams => {props.actionSortClick(folderItemsType, state.currentFolderId, sortParams)}}
                    contextAllowed
                    copyAndMoveAllowed
                    onChangeParentFolder={(itemsType, folderId, parentFolderId) => props.actionChangeParentFolder(itemsType, folderId, parentFolderId)}
                    onCopyFolder = {(itemsType, destFolderId, folderIds) => props.actionCopyFolder(itemsType, destFolderId, folderIds)}
                    onMoveFolderItem = {(itemsType, destFolderId, objIds, textForSnackbar) => props.actionMoveFolderItem(itemsType, destFolderId, objIds, textForSnackbar)}
                    onCopyFolderItem = {(itemsType, destFolderId, objIds, textForSnackbar) => props.actionCopyFolderItem(itemsType, destFolderId, objIds, textForSnackbar)}
                />
            </DataLoader>
            // : state.flowState === datasetsMenuViewFlowStates.datasetViewer ?
            // <DatasetViewer
            //     datasetId = {state.viewDatasetId}
            //     onOkClick = {handleExit}
            // />
            // : state.flowState === datasetsMenuViewFlowStates.datasetDesigner ?
            // <DatasetDesigner
            //     mode = {designerMode}
            //     datasetId = {state.editDatasetId}
            //     folderId = {state.currentFolderId}
            //     onExit = {handleExit}
            // />
            : state.flowState === datasetsMenuViewFlowStates.datasetDependenciewView ?
            <DependencyViewer 
                itemSType = {folderItemsType}
                itemId={state.datasetId}
                data={state.dependenciesData}
                onLinkPathClick={handleDependencyPathClick}
                onExit = {() => props.actionFolderClick(folderItemsType, state.currentFolderId)}
            />
            : <div>Неизвестное состояние</div>            
        }
        </div>
    )
}

const mapStateToProps = state => {
    return {
        state : state.datasetsMenuView
    }
}

const mapDispatchToProps = {
    actionFolderLoaded,
    actionFolderLoadFailed,
    actionFolderClick,
    actionItemClick,
    actionAddFolder,
    actionAddItemClick,
    actionEditItemClick, 
    actionDeleteItemClick,
    actionEditFolder,
    actionDeleteFolderClick,
    actionGetDependencies,
    actionSetSidebarItem,
    actionSearchClick,
    actionChangeParentFolder,
    actionCopyFolder,
    actionMoveFolderItem,
    actionCopyFolderItem,
    actionSortClick
}

export default connect(mapStateToProps, mapDispatchToProps)(DatasetsMenuView);
