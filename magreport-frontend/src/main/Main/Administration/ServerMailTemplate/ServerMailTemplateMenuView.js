import React from 'react';
import { connect } from 'react-redux';
import { useNavigateBack} from "components/Navbar/navbarHooks";

import { useParams, useNavigate, useLocation } from 'react-router-dom'

// dataHub
import dataHub from 'ajax/DataHub';

// actions
import {actionFolderLoaded, actionFolderLoadFailed, actionFolderClick, actionItemClick, actionAddFolder,
    actionAddItemClick, actionEditFolder, actionDeleteFolderClick, actionEditItemClick, actionDeleteItemClick, actionSearchClick, actionSortClick
} from 'redux/actions/menuViews/folderActions';
import actionSetSidebarItem from 'redux/actions/sidebar/actionSetSidebarItem';

// components
import DataLoader from '../../../DataLoader/DataLoader';
import FolderContent from '../../../FolderContent/FolderContent';
import SidebarItems from '../../Sidebar/SidebarItems';
//import RoleDesigner from './RoleDesigner';


// states
import {FLOW_STATE_BROWSE_FOLDER, mailTemplateMenuViewFlowStates} from 'redux/reducers/menuViews/flowStates';
import ServerMailTemplateView from "./ServerMailTemplateView";
import ServerMailTemplateDesigner from "./ServerMailTemplateDesigner";

function MailTemplatesMenuView(props){

    const {id} = useParams()
    const navigate = useNavigate()
    const location = useLocation()

    const navigateBack = useNavigateBack();

    let state = props.state;

    let reload = {needReload : state.needReload};
    let folderItemsType = SidebarItems.admin.subItems.mailTexts.folderItemType;
    let sidebarItemType = SidebarItems.admin.subItems.mailTexts.key;
    let isSortingAvailable = true;

    function handleExit() {
            navigateBack();
    }

    function handleFolderClick(folderId) {
        props.actionFolderClick(folderItemsType, folderId)
        navigate(`/systemMailTemplates/${folderId}`)
    }
    function handleItemClick(templateId) {
        props.actionItemClick(folderItemsType, templateId)
        navigate(`/systemMailTemplates/${id}/view/${templateId}`, {state: location.pathname})
    }
    function handleEditItemClick(templateId) {
        props.actionEditItemClick(folderItemsType, templateId)
        navigate(`/systemMailTemplates/${id}/edit/${templateId}`, {state: location.pathname})
    }

    return(
        <div  style={{display: 'flex', flex: 1}}>
            {
                state.flowState === FLOW_STATE_BROWSE_FOLDER ?
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
                            // onFolderClick = {(folderId) => {props.actionFolderClick(folderItemsType, folderId)}}
                            // onItemClick = {(templateId) => {props.actionItemClick(folderItemsType, templateId)}}
                            // onEditItemClick = {(templateId) => {props.actionEditItemClick(folderItemsType, templateId)}}
                            // onAddItemClick = {() => {props.actionAddItemClick(folderItemsType)}}
                            
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
                    // : state.flowState === mailTemplateMenuViewFlowStates.mailTemplateViewer ?
                    // <ServerMailTemplateView
                    //     serverMailTemplateId = {state.viewMailTemplateId}
                    //     onOkClick = {handleExit}
                    //     onEditClick={actionItemClick}
                    // />
                    // : state.flowState === mailTemplateMenuViewFlowStates.mailTemplateDesigner ?
                    //     <ServerMailTemplateDesigner
                    //         serverMailTemplateId = {state.viewMailTemplateId}
                    //         onOkClick = {handleExit}
                    //         onExitClick={handleExit}
                    //         onEditClick={actionItemClick}
                    //     />
                    : <div>Неизвестное состояние</div>
            }
        </div>
    )
}

const mapStateToProps = state => {
    return {
        state : state.mailTemplateMenuView
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
    actionSearchClick,
    actionSortClick,
    actionSetSidebarItem
}

export default connect(mapStateToProps, mapDispatchToProps)(MailTemplatesMenuView);