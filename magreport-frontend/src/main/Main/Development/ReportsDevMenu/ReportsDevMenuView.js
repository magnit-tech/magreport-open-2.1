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

    const state = props.state;

    const {id} = useParams()
    const navigate = useNavigate()
    const location = useLocation()
    const [searchParams, setSearchParams] = useSearchParams();
    const locationPreviousHistory = { state: location.pathname + location.search }

    const [dependency, setDependency] = useState({
        loader: false,
        show: false,
        id: null,
        data: null
    })
    const [reload, setReload] = useState({needReload : state.needReload});

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

        setReload({needReload: true})
    }, [searchParams, state.needReload]) // eslint-disable-line

    const folderItemsType = SidebarItems.development.subItems.reportsDev.folderItemType;
    const showAddBtn = (searchParams.get("isRecursive") === 'true' ) ? false : true;


    function handleFolderClick(folderId) {
        navigate(`/ui/reportsDev/${folderId}`)
    }
    function handleItemClick(reportId) {
        navigate(`/ui/report/starter/${reportId}`, locationPreviousHistory)
    }
    function handleViewItemClick(reportId) {
        if (id) {
            navigate(`/ui/reportsDev/${id}/view/${reportId}`, locationPreviousHistory)
        } else {
            let reportFolderPath = props.state.filteredFolderData.reports.find(i => i.id === reportId).path;
            navigate(`/ui/reportsDev/${reportFolderPath[reportFolderPath.length - 1].id}/view/${reportId}`, locationPreviousHistory)    
        }
    }
    function handleEditItemClick(reportId) {
        if (id) {
            navigate(`/ui/reportsDev/${id}/edit/${reportId}`, locationPreviousHistory)
        } else {
            let reportFolderPath = props.state.filteredFolderData ? props.state.filteredFolderData.reports.find(i => i.id === reportId).path : props.state.currentFolderData.reports.find(i => i.id === reportId).path;
            navigate(`/ui/reportsDev/${reportFolderPath[reportFolderPath.length - 1].id}/edit/${reportId}`, locationPreviousHistory)
        }
    }
    function handleAddItemClick() {
        navigate(`/ui/reportsDev/${id}/add`, locationPreviousHistory)
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

    // Dependency
    function handleDependenciesClick(reportId) {
        searchParams.set('dependency', reportId);
        setSearchParams(searchParams);

        setDependency({...dependency, loader: true})
        dataHub.reportController.getDependencies(Number(reportId), handleLoadedDependency)
    }
    function handleLoadedDependency({data}) {
        setDependency({show: true, id: data.id, data: data, loader: false})
    }
    function handleCloseDependency() {
        setDependency({ show: false })

        searchParams.delete('dependency');
        const newParams = {};
        searchParams.forEach((value, key) => {
            newParams[key] = value;
        });
        setSearchParams(newParams);
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
                        isSearchLoading = {state.isSearchLoading}
                        onDataLoaded = {(data) => handleDataLoaded(data)}
                        onDataLoadFailed = {(message) => {props.actionFolderLoadFailed(folderItemsType, message)}}
                    >
                        <FolderContent
                            itemsType = {folderItemsType}
                            data = {state.filteredFolderData ? state.filteredFolderData : state.currentFolderData}
                            showAddFolder = {showAddBtn}
                            showAddItem = {showAddBtn}
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
                            onSearchClick = {handleSearchItems}
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
