import React from 'react';
import { connect } from 'react-redux';

import { useParams, useNavigate, useLocation } from 'react-router-dom'

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

    const {id} = useParams()
    const navigate = useNavigate()
    const location = useLocation()

    let state = props.state;

    let reload = {needReload : state.needReload};
    let folderItemsType = SidebarItems.admin.subItems.mailTexts.folderItemType;
    let sidebarItemType = SidebarItems.admin.subItems.mailTexts.key;
    let isSortingAvailable = true;


    function handleFolderClick(folderId) {
        navigate(`/ui/systemMailTemplates/${folderId}`)
    }
    function handleItemClick(templateId) {
        navigate(`/ui/systemMailTemplates/${id}/view/${templateId}`, {state: location.pathname})
    }
    function handleEditItemClick(templateId) {
        navigate(`/ui/systemMailTemplates/${id}/edit/${templateId}`, {state: location.pathname})
    }

    return(
        <div  style={{display: 'flex', flex: 1}}>
            <DataLoader
                loadFunc = {dataHub.serverMailTemplateController.getMailTemplateType}
                loadParams = {id ? [Number(id)] : [null]}
                reload = {reload}
                onDataLoaded = {(data) => {props.actionFolderLoaded(folderItemsType, data, isSortingAvailable)}}
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
                    onSearchClick ={searchParams => {props.actionSearchClick(folderItemsType, state.currentFolderId, searchParams)}}
                    onSortClick ={sortParams => {props.actionSortClick(folderItemsType, state.currentFolderId, sortParams)}}
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