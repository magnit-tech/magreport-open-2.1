import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';

import { useParams, useNavigate, useLocation, useSearchParams } from 'react-router-dom'

// dataHub
import dataHub from 'ajax/DataHub';

// actions
import {actionFolderLoaded, actionFolderLoadFailed, actionDeleteItemClick, actionAddFolder,
        actionEditFolder, actionDeleteFolderClick, actionSearchClick, actionSortClick, actionChangeParentFolder, actionCopyFolder, actionMoveFolderItem, actionCopyFolderItem} from 'redux/actions/menuViews/folderActions';

// components
import DataLoader from '../../../DataLoader/DataLoader';
import FolderContent from '../../../FolderContent/FolderContent';
import SidebarItems from '../../Sidebar/SidebarItems'

import DependencyViewer from '../DependencyViewer';

import CircularProgress from '@material-ui/core/CircularProgress';

function ReportsDevMenuView(props){

    const {id} = useParams()
    const navigate = useNavigate()
    const location = useLocation()

    const [searchParams, setSearchParams] = useSearchParams();

    const [dependency, setDependency] = useState({
        loader: false,
        show: false,
        id: null,
        data: null
    })

    useEffect(() => {
        const datasourceId = searchParams.get('dependency');
        if (datasourceId) {
            handleDependenciesClick(datasourceId)
        } else {
            setDependency({
                loader: false,
                show: false,
                id: null,
                data: null
            })
        }
    }, [searchParams]) // eslint-disable-line

    const state = props.state;

    let reload = {needReload : state.needReload};
    let folderItemsType = SidebarItems.development.subItems.reportsDev.folderItemType;
    let isSortingAvailable = true;


    function handleFolderClick(folderId) {
        navigate(`/ui/reportsDev/${folderId}`)
    }
    function handleItemClick(reportId) {
        navigate(`/ui/report/starter/${reportId}`, {state: location.pathname})
    }
    function handleViewItemClick(reportId) {
        navigate(`/ui/reportsDev/${id}/view/${reportId}`, {state: location.pathname})
    }
    function handleEditItemClick(reportId) {
        navigate(`/ui/reportsDev/${id}/edit/${reportId}`, {state: location.pathname})
    }
    function handleAddItemClick() {
        navigate(`/ui/reportsDev/${id}/add`, {state: location.pathname})
    }

    // Dependency
    function handleDependenciesClick(reportId) {
        setSearchParams({ 'dependency': reportId });
        setDependency({...dependency, loader: true})
        dataHub.reportController.getDependencies(Number(reportId), handleLoadedDependency)
    }
    function handleLoadedDependency({data}) {
        setDependency({show: true, id: data.id, data: data, loader: false})
    }
    function handleCloseDependency() {
        setDependency({ show: false })
        setSearchParams({})
    }

    return(
        <div style={{display: 'flex', flex: 1}}>
            { !dependency.show ?
                dependency.loader ? 
                    <div style={{ width: '100%', display: 'flex', justifyContent: 'center', alignItems: 'center'}}> <CircularProgress /> </div> : 
                    !searchParams.get('dependency') && 
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
                :
                    <DependencyViewer
                        itemsType = {folderItemsType}
                        itemId={dependency.id}
                        data={dependency.data}
                        onExit = {handleCloseDependency}
                    />
            }
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
    actionChangeParentFolder,
    actionCopyFolder,
    actionMoveFolderItem,
    actionCopyFolderItem
}

export default connect(mapStateToProps, mapDispatchToProps)(ReportsDevMenuView);
