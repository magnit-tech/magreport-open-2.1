import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';

import { useParams, useNavigate, useLocation, useSearchParams } from 'react-router-dom'

// dataHub
import dataHub from 'ajax/DataHub';

// actions
import {actionFolderLoaded, actionFolderLoadFailed, actionAddFolder, actionEditFolder, actionDeleteFolderClick, actionDeleteItemClick, actionSearchClick, actionSortClick
} from 'redux/actions/menuViews/folderActions';

// components
import DataLoader from '../../../DataLoader/DataLoader';
import FolderContent from '../../../FolderContent/FolderContent';
import SidebarItems from '../../Sidebar/SidebarItems';


function MailTemplatesMenuView(props){

    let state = props.state;

    const {id} = useParams()
    const navigate = useNavigate()
    const location = useLocation()
    const [searchParams, setSearchParams] = useSearchParams();
    const locationPreviousHistory = { state: location.pathname + location.search }

    const [reload, setReload] = useState({needReload : state.needReload});

    let folderItemsType = SidebarItems.admin.subItems.mailTexts.folderItemType;
    let sidebarItemType = SidebarItems.admin.subItems.mailTexts.key;
    let isSortingAvailable = true;

    useEffect(() => {
        setReload({needReload: true})
    }, [searchParams, state.needReload])


    function handleFolderClick(folderId) {
        navigate(`/ui/systemMailTemplates/${folderId}`)
    }
    function handleItemClick(templateId) {
        navigate(`/ui/systemMailTemplates/${id}/view/${templateId}`, locationPreviousHistory)
    }
    function handleEditItemClick(templateId) {
        navigate(`/ui/systemMailTemplates/${id}/edit/${templateId}`, locationPreviousHistory)
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
        await props.actionFolderLoaded(folderItemsType, data, isSortingAvailable, false, !!searchParams.get("search"))

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
                loadFunc = {dataHub.serverMailTemplateController.getMailTemplateType}
                loadParams = {id ? [Number(id)] : [null]}
                reload = {reload}
                isSearchLoading = {state.isSearchLoading}
                onDataLoaded = {(data) => handleDataLoaded(data)}
                onDataLoadFailed = {(message) => {props.actionFolderLoadFailed(folderItemsType, message)}}
            >
                <FolderContent
                    itemsType = {folderItemsType}
                    showAddFolder = {false}
                    showAddItem = {false}
                    searchParams = {state.searchParams || {}}
                    sortParams = {state.sortParams || {}}
                    data = {state.filteredFolderData ? state.filteredFolderData : state.currentFolderData}
                    
                    onFolderClick = {handleFolderClick}
                    onItemClick={handleItemClick}
                    onEditItemClick={handleEditItemClick}

                    onDeleteFolderClick = {(folderId) => {props.actionDeleteFolderClick(folderItemsType, state.currentFolderData.id, folderId)}}
                    onDeleteItemClick = {(roleId) => {props.actionDeleteItemClick(folderItemsType, state.currentFolderId, roleId)}}
                    onEditFolder = {(folderId, name, description) => {props.actionEditFolder(sidebarItemType, folderItemsType, state.currentFolderData.id, folderId, name, description)}}
                    contextAllowed
                    onSearchClick = {handleSearchItems}
                    onSortClick = {sortParams => {props.actionSortClick(folderItemsType, state.currentFolderId, sortParams)}}
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
    actionSortClick,
}

export default connect(mapStateToProps, mapDispatchToProps)(MailTemplatesMenuView);