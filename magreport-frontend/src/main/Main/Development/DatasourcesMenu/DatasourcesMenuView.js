import React from 'react';
import { connect } from 'react-redux';
import {useNavigateBack} from "components/Navbar/navbarHooks";

import { useParams, useNavigate, useLocation } from 'react-router-dom'

// dataHub
import dataHub from 'ajax/DataHub';

// actions
import {actionFolderLoaded, actionFolderLoadFailed, actionFolderClick, 
        actionItemClick, actionEditItemClick, actionDeleteItemClick, actionAddFolder, actionAddItemClick,
        actionEditFolder, actionDeleteFolderClick, actionGetDependencies, actionSearchClick, actionChangeParentFolder, actionCopyFolder, actionMoveFolderItem, actionCopyFolderItem, actionSortClick} from 'redux/actions/menuViews/folderActions';
import actionSetSidebarItem from 'redux/actions/sidebar/actionSetSidebarItem';

// const
import {FLOW_STATE_BROWSE_FOLDER, datasourcesMenuViewFlowStates} from 'redux/reducers/menuViews/flowStates';
import { folderItemTypeSidebarItem } from 'main/FolderContent/folderItemTypeSidebarItem';

// components
import DataLoader from '../../../DataLoader/DataLoader';
import FolderContent from '../../../FolderContent/FolderContent';
import SidebarItems from '../../Sidebar/SidebarItems'
import DatasourceViewer from "./DatasourceViewer";
import DatasourceDesigner from './DatasourceDesigner';
import DependencyViewer from  '../DependencyViewer';

function DatasourcesMenuView(props){

    const navigateBack = useNavigateBack();

    const {id} = useParams()
    const navigate = useNavigate()
    const location = useLocation()

    const state = props.state;
    // let designerMode = props.state.editDatasourceId === null ? 'create' : 'edit';

    // let loadFunc = dataHub.datasourceController.getFolder;

    let reload = {needReload : state.needReload};
    let folderItemsType = SidebarItems.development.subItems.datasources.folderItemType;
    let isSortingAvailable = true;

    function handleExit() {
        navigateBack();
    }

    function handleDependencyPathClick(argFolderItemsType, folderId){
        props.actionSetSidebarItem(folderItemTypeSidebarItem(argFolderItemsType));
        props.actionFolderClick(argFolderItemsType, folderId)
    }

    function handleFolderClick(folderId) {
        props.actionFolderClick(folderItemsType, folderId)
        navigate(`/datasource/${folderId}`)
    }
    function handleItemClick(datasourceId) {
        props.actionItemClick(folderItemsType, datasourceId)
        navigate(`/datasource/${id}/view/${datasourceId}`, {state: location.pathname})
    }
    function handleEditItemClick(datasourceId) {
        props.actionEditItemClick(folderItemsType, datasourceId)
        navigate(`/datasource/${id}/edit/${datasourceId}`, {state: location.pathname})
    }
    function handleDependenciesClick(datasourceId) {
        props.actionGetDependencies(folderItemsType, datasourceId)
        // navigate(`/datasource/dependencies/${datasourceId}`)
    }
    function handleAddItemClick(folderItemsType) {
        props.actionAddItemClick(folderItemsType)
        navigate(`/datasource/${id}/add`, {state: location.pathname})
    }


    return(
        <div style={{display: 'flex', flex: 1}}>
        {
        state.flowState === FLOW_STATE_BROWSE_FOLDER ?
            (
            <DataLoader
                loadFunc = {dataHub.datasourceController.getFolder}
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
                    // onFolderClick = {(folderId) => {props.actionFolderClick(folderItemsType, folderId)}}
                    // onItemClick = {(datasourceId) => {props.actionItemClick(folderItemsType, datasourceId)}}
                    // onEditItemClick = {(datasourceId) => {props.actionEditItemClick(folderItemsType, datasourceId)}}
                    // onDependenciesClick = {datasourcetId => props.actionGetDependencies(folderItemsType, datasourcetId)}
                    // onAddItemClick = {() => {props.actionAddItemClick(folderItemsType)}}
                    
                    onFolderClick = {handleFolderClick}
                    onItemClick={handleItemClick}
                    onEditItemClick={handleEditItemClick}
                    onDependenciesClick = {handleDependenciesClick}
                    onAddItemClick={handleAddItemClick}

                    onEditFolderClick = {(folderId, name, description) => {props.actionEditFolder(folderItemsType, state.currentFolderData.id, folderId, name, description)}}
                    onAddFolder = {(name, description) => {props.actionAddFolder(folderItemsType, state.currentFolderData.id, name, description)}}
                    onDeleteFolderClick = {(folderId) => {props.actionDeleteFolderClick(folderItemsType, state.currentFolderData.id, folderId)}}
                    onDeleteItemClick = {(datasourceId) => {props.actionDeleteItemClick(folderItemsType, state.currentFolderId, datasourceId)}}
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
            )
        // : state.flowState === datasourcesMenuViewFlowStates.datasourceViewer ?
        // <DatasourceViewer
        //     datasourceId={state.viewDatasourceId}
        //     onOkClick={handleExit}
        // />
        // : state.flowState === datasourcesMenuViewFlowStates.datasourceDesigner ?
        // <DatasourceDesigner
        //     mode = {designerMode}
        //     datasourceId = {state.editDatasourceId}
        //     folderId = {state.currentFolderId}
        //     onExit = {handleExit}
        // />
        : state.flowState === datasourcesMenuViewFlowStates.datasourceDependenciesView ?
            <DependencyViewer 
                itemsType={folderItemsType}
                itemId={state.datasourceId}
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
        state : state.datasourcesMenuView
    }
}

const mapDispatchToProps = {
    actionFolderLoaded,
    actionFolderLoadFailed,
    actionFolderClick,
    actionItemClick,
    actionEditItemClick,
    actionDeleteItemClick,
    actionAddFolder,
    actionAddItemClick,
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

export default connect(mapStateToProps, mapDispatchToProps)(DatasourcesMenuView);
