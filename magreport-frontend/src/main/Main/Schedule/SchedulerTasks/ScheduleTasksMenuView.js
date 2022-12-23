import React from 'react';
import { connect } from 'react-redux';

import { useNavigate } from 'react-router-dom'

// dataHub
import dataHub from 'ajax/DataHub';

import {
    actionFolderLoaded,
    actionFolderLoadFailed,
    actionDeleteItemClick,
    actionSearchClick,
    actionScheduleTaskRunClick,
    actionSortClick
} from 'redux/actions/menuViews/folderActions';

import {actionScheduleTaskSwitch} from 'redux/actions/admin/actionSchedules';

// const
import {FolderItemTypes} from "main/FolderContent/FolderItemTypes";

// local components
import DataLoader from  '../../../DataLoader/DataLoader';
import FolderContent from 'main/FolderContent/FolderContent';


function ScheduleTasksMenuView(props){

    const navigate = useNavigate()

    const state = props.state;

    let reload = {needReload: state.needReload};
    let folderItemsType = FolderItemTypes.scheduleTasks;
    let isSortingAvailable = true;


    function handleItemClick(scheduleTaskId) {
        navigate(`/ui/scheduleTasks/view/${scheduleTaskId}`)
    }
    function handleEditItemClick(scheduleTaskId) {
        navigate(`/ui/scheduleTasks/edit/${scheduleTaskId}`)
    }
    function handleAddItemClick() {
        navigate(`/ui/scheduleTasks/add`)
    }

    return (
        <div style={{display: 'flex', flex: 1}}>
            <DataLoader
                loadFunc={dataHub.scheduleController.taskGetAll}
                loadParams={[]}
                reload={reload}
                onDataLoaded={(data) => {
                    props.actionFolderLoaded(folderItemsType, {scheduleTasks: data, childFolders: []}, isSortingAvailable)
                }}
                onDataLoadFailed={(message) => {
                    props.actionFolderLoadFailed(folderItemsType, message)
                }}
            >
                <FolderContent
                    itemsType={folderItemsType}
                    showAddFolder={false}
                    showAddItem={true}
                    data = {state.filteredFolderData ? state.filteredFolderData : state.currentFolderData}
                    searchParams={state.searchParams || {}} 
                    searchWithoutRecursive

                    onItemClick={handleItemClick}
                    onAddItemClick={handleAddItemClick}
                    onEditItemClick={handleEditItemClick}

                    onDeleteItemClick={(scheduleTaskId) => {
                        props.actionDeleteItemClick(folderItemsType, null, scheduleTaskId)
                    }}
                    onSearchClick ={searchParams => {props.actionSearchClick(folderItemsType, [], searchParams)}}
                    onScheduleTaskRunClick = {(scheduleTaskId) => {props.actionScheduleTaskRunClick(folderItemsType, scheduleTaskId) }} 
                    onScheduleTaskSwitchClick = {(index, scheduleTaskId) => {props.actionScheduleTaskSwitch(folderItemsType, index, scheduleTaskId)}}
                    contextAllowed
                    sortParams = {state.sortParams || {}}
                    onSortClick ={sortParams => {props.actionSortClick(folderItemsType, state.currentFolderId, sortParams)}}
                />  
            </DataLoader>
        </div>
    );
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
    actionSearchClick,
    actionScheduleTaskRunClick,
    actionSortClick,
    actionScheduleTaskSwitch
};

export default connect(mapStateToProps, mapDispatchToProps)(ScheduleTasksMenuView);