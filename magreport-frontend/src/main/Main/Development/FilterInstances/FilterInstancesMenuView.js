import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';

import { useParams, useNavigate, useLocation, useSearchParams } from 'react-router-dom'

// dataHub
import dataHub from 'ajax/DataHub';

// actions
import {actionFolderLoaded, actionFolderLoadFailed, actionDeleteItemClick, actionAddFolder,
    actionEditFolder, actionDeleteFolderClick, actionSearchClick, actionChangeParentFolder, actionCopyFolder, actionMoveFolderItem, actionCopyFolderItem, actionSortClick} from 'redux/actions/menuViews/folderActions';

// components
import DataLoader from '../../..//DataLoader/DataLoader';
import FolderContent from '../../../FolderContent/FolderContent';
import SidebarItems from '../../Sidebar/SidebarItems'

import DependencyViewer from '../DependencyViewer';

import CircularProgress from '@material-ui/core/CircularProgress';

function FilterInstancesMenuView(props){

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

    const folderItemsType = SidebarItems.development.subItems.filterInstances.folderItemType;
    const showAddBtn = (searchParams.get("isRecursive") === 'true' || !id) ? false : true;
    

    function handleFolderClick(folderId) {
        navigate(`/ui/filterInstance/${folderId}`)
    }
    function handleItemClick(filterInstanceId) {
        if (id){
            navigate(`/ui/filterInstance/${id}/view/${filterInstanceId}`, locationPreviousHistory)
        } else {
            let path = props.state.filteredFolderData ? props.state.filteredFolderData.filterInstances.find(i => i.id === filterInstanceId).path : props.state.currentFolderData.filterInstances.find(i => i.id === filterInstanceId).path;
            navigate(`/ui/filterInstance/${path[path.length - 1].id}/view/${filterInstanceId}`, locationPreviousHistory)
        }
    }
    function handleEditItemClick(filterInstanceId) {
        if (id) {
            navigate(`/ui/filterInstance/${id}/edit/${filterInstanceId}`, locationPreviousHistory)
        } else {
            let path = props.state.filteredFolderData ? props.state.filteredFolderData.filterInstances.find(i => i.id === filterInstanceId).path : props.state.currentFolderData.filterInstances.find(i => i.id === filterInstanceId).path;
            navigate(`/ui/filterInstance/${path[path.length - 1].id}/edit/${filterInstanceId}`, locationPreviousHistory)
        }
    }
    function handleAddItemClick() {
        navigate(`/ui/filterInstance/${id}/add`, locationPreviousHistory)
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
    function handleDependenciesClick(filterInstanceId) {
        searchParams.set('dependency', filterInstanceId);
        setSearchParams(searchParams);

        setDependency({...dependency, loader: true})
        dataHub.filterInstanceController.getDependencies(Number(filterInstanceId), handleLoadedDependency)
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
                        loadFunc = {dataHub.filterInstanceController.getFolder}
                        loadParams = {id ? [Number(id)] : [null]}
                        reload = {reload}
                        isSearchLoading = {state.isSearchLoading}
                        onDataLoaded = {(data) => handleDataLoaded(data)}
                        onDataLoadFailed = {(message) => {props.actionFolderLoadFailed(folderItemsType, message)}}
                    >
                        <FolderContent
                            itemsType = {folderItemsType}
                            data = {state.filteredFolderData ? state.filteredFolderData : state.currentFolderData}
                            searchParams = {state.searchParams || {}}
                            sortParams = {state.sortParams || {}}
                            showAddFolder = {showAddBtn}
                            showAddItem = {showAddBtn}

                            onFolderClick = {handleFolderClick}
                            onItemClick={handleItemClick}
                            onEditItemClick={handleEditItemClick}
                            onDependenciesClick = {handleDependenciesClick}
                            onAddItemClick={handleAddItemClick}

                            onAddFolder = {(name, description) => {props.actionAddFolder(folderItemsType, state.currentFolderData.id, name, description)}}
                            onEditFolderClick = {(folderId, name, description) => {props.actionEditFolder(folderItemsType, state.currentFolderData.id, folderId, name, description)}}
                            onDeleteFolderClick = {(folderId) => {props.actionDeleteFolderClick(folderItemsType, state.currentFolderData.id, folderId)}}
                            onDeleteItemClick = {(filterInstanceId) => {props.actionDeleteItemClick(folderItemsType, state.currentFolderId, filterInstanceId)}}
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
    actionAddFolder,
    actionEditFolder,
    actionDeleteFolderClick,
    actionDeleteItemClick,
    actionSearchClick,
    actionChangeParentFolder,
    actionCopyFolder,
    actionMoveFolderItem,
    actionCopyFolderItem,
    actionSortClick
}

export default connect(mapStateToProps, mapDispatchToProps)(FilterInstancesMenuView);
