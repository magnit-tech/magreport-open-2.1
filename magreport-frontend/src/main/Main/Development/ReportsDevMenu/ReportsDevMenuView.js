import React from 'react';
import {useNavigateBack} from "components/Navbar/navbarHooks";
import { connect } from 'react-redux';

import { useParams, useNavigate, useLocation } from 'react-router-dom'

// dataHub
import dataHub from 'ajax/DataHub';

// actions
import {actionFolderLoaded, actionFolderLoadFailed, actionFolderClick,
        actionItemClick, actionEditItemClick, actionDeleteItemClick, actionAddFolder, actionAddItemClick,
        actionEditFolder, actionDeleteFolderClick, actionGetDependencies, actionSearchClick, actionSortClick, actionChangeParentFolder, actionCopyFolder, actionMoveFolderItem, actionCopyFolderItem} from 'redux/actions/menuViews/folderActions';
import actionSetSidebarItem from 'redux/actions/sidebar/actionSetSidebarItem';
import {actionViewerViewItem} from "redux/actions/actionViewer";

// const

import {FLOW_STATE_BROWSE_FOLDER, reportsDevMenuViewFlowStates} from 'redux/reducers/menuViews/flowStates';
import { folderItemTypeSidebarItem } from 'main/FolderContent/folderItemTypeSidebarItem';
// components
import DataLoader from '../../../DataLoader/DataLoader';
import FolderContent from '../../../FolderContent/FolderContent';
import SidebarItems from '../../Sidebar/SidebarItems'
import ReportDevViewer from './ReportDevViewer';
import ReportDevDesigner from './ReportDevDesigner';
import ReportStarter from 'main/Report/ReportStarter';

import DependencyViewer from '../DependencyViewer';

function ReportsDevMenuView(props){

    const {id} = useParams()
    const navigate = useNavigate()
    const location = useLocation()

    const navigateBack = useNavigateBack();

    const state = props.state;
    // let designerMode = props.state.editReportId === null ? 'create' : 'edit';

    // let loadFunc = dataHub.reportController.getFolder;

    let reload = {needReload : state.needReload};
    let folderItemsType = SidebarItems.development.subItems.reportsDev.folderItemType;
    let isSortingAvailable = true;
    
    function handleExit() {
        navigateBack();
    }

    function handleDependencyPathClick(argFolderItemsType, folderId){
        props.actionSetSidebarItem(folderItemTypeSidebarItem(argFolderItemsType));
        props.actionFolderClick(argFolderItemsType, folderId)
    }

    function handleReportCancel(){
        props.actionFolderClick(folderItemsType, state.currentFolderId);
    }


    function handleFolderClick(folderId) {
        props.actionFolderClick(folderItemsType, folderId)
        navigate(`/reportsDev/${folderId}`)
    }
    function handleItemClick(reportId) {
        props.actionItemClick(folderItemsType, reportId)
        navigate(`/report/starter/${reportId}`, {state: location.pathname})
    }
    function handleViewItemClick(reportId) {
        props.actionItemClick(folderItemsType, reportId)
        navigate(`/reportsDev/${id}/view/${reportId}`, {state: location.pathname})
    }
    function handleEditItemClick(reportId) {
        props.actionEditItemClick(folderItemsType, reportId)
        navigate(`/reportsDev/${id}/edit/${reportId}`, {state: location.pathname})
    }
    function handleDependenciesClick(reportId) {
        props.actionGetDependencies(folderItemsType, reportId)
        // navigate(`/reportsDev/dependencies/${reportId}`)
    }
    function handleAddItemClick(folderItemsType) {
        props.actionAddItemClick(folderItemsType)
        navigate(`/reportsDev/${id}/add`, {state: location.pathname})
    }

    return(
        <div style={{display: 'flex', flex: 1}}>
        {
        state.flowState === FLOW_STATE_BROWSE_FOLDER ?
            (
            <DataLoader
                loadFunc = {dataHub.reportController.getFolder}
                loadParams = {id ? [Number(id)] : [null]}
                reload = {reload}
                onDataLoaded = {(data) => {props.actionFolderLoaded(folderItemsType, data, isSortingAvailable)}}
                onDataLoadFailed = {(message) => {props.actionFolderLoadFailed(folderItemsType, message)}}
            >
                <FolderContent
                    itemsType = {folderItemsType}
                    data = {state.filteredFolderData ? state.filteredFolderData : state.currentFolderData}
                    showAddFolder = {true}
                    showAddItem = {true}
                    searchParams = {state.searchParams || {}}
                    sortParams = {state.sortParams || {}}

                    onFolderClick = {handleFolderClick}
                    onItemClick={handleItemClick}
                    onViewItemClick={handleViewItemClick}
                    onEditItemClick={handleEditItemClick}
                    onDependenciesClick = {handleDependenciesClick}
                    onAddItemClick={handleAddItemClick}

                    // onFolderClick = {(folderId) => {props.actionFolderClick(folderItemsType, folderId)}}
                    // onItemClick = {(reportId) => {props.actionItemClick(folderItemsType, reportId)}}
                    // onEditItemClick = {(reportId) => {props.actionEditItemClick(folderItemsType, reportId)}}
                    // onDependenciesClick = {reportId => props.actionGetDependencies(folderItemsType, reportId)}
                    // onAddItemClick = {() => {props.actionAddItemClick(folderItemsType)}}
                    // onViewItemClick = {(reportId) => props.actionViewerViewItem(folderItemsType, reportId)}



                    onAddFolder = {(name, description) => {props.actionAddFolder(folderItemsType, state.currentFolderData.id, name, description)}}
                    onEditFolderClick = {(folderId, name, description) => {props.actionEditFolder(folderItemsType, state.currentFolderData.id, folderId, name, description)}}
                    onDeleteFolderClick = {(folderId) => {props.actionDeleteFolderClick(folderItemsType, state.currentFolderData.id, folderId)}}
                    onDeleteItemClick = {(reportId) => {props.actionDeleteItemClick(folderItemsType, state.currentFolderId, reportId)}}
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
        // : state.flowState === reportsDevMenuViewFlowStates.reportDevViewer ?
        //     <ReportDevViewer
        //         reportId = {state.viewReportId}
        //         onOkClick = {handleExit}
        //     />
        // : state.flowState === reportsDevMenuViewFlowStates.reportDevDesigner ?
        //     <ReportDevDesigner
        //         mode = {designerMode}
        //         reportId = {state.editReportId}
        //         folderId = {state.currentFolderId}
        //         onExit = {handleExit}
        //     />
        
        // : state.flowState === reportsDevMenuViewFlowStates.startReport ?
        //     <ReportStarter
        //         reportId = {state.reportId}
        //         onDataLoadFunction={dataHub.reportController.get}
        //         onCancel = {handleReportCancel}
        //     />
        : state.flowState === reportsDevMenuViewFlowStates.reportDevDependenciesView ?
        ///<div> Dependencies </div>
            <DependencyViewer
                itemsType = {folderItemsType}
                itemId={state.reportId}
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
        state : state.reportsDevMenuView
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
    actionViewerViewItem,
    actionEditFolder,
    actionDeleteFolderClick,
    actionGetDependencies,
    actionSearchClick,
    actionSortClick,
    actionSetSidebarItem,
    actionChangeParentFolder,
    actionCopyFolder,
    actionMoveFolderItem,
    actionCopyFolderItem
}

export default connect(mapStateToProps, mapDispatchToProps)(ReportsDevMenuView);
