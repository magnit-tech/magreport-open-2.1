import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';

import { useNavigate, useLocation, useSearchParams } from 'react-router-dom'

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

    const state = props.state;

    const navigate = useNavigate()
    const location = useLocation()
    const [searchParams, setSearchParams] = useSearchParams();
    const locationPreviousHistory = { state: location.pathname + location.search }

    const [reload, setReload] = useState({needReload : state.needReload});

    useEffect(() => {
        setReload({needReload: true})
    }, [searchParams, state.needReload])

    let folderItemsType = FolderItemTypes.scheduleTasks;
    let isSortingAvailable = true;


    function handleItemClick(scheduleTaskId) {
        navigate(`/ui/scheduleTasks/view/${scheduleTaskId}`, locationPreviousHistory)
    }
    function handleEditItemClick(scheduleTaskId) {
        navigate(`/ui/scheduleTasks/edit/${scheduleTaskId}`, locationPreviousHistory)
    }
    function handleAddItemClick() {
        navigate(`/ui/scheduleTasks/add`)
    }
    function handleSearchItems(params) {
        const { searchString, isRecursive } = params

        if (searchString.trim() === '') {
            setSearchParams({})
        } else {
            setSearchParams({search: searchString, isRecursive: isRecursive ?? false})
        }
    }

    async function handleDataLoaded(data) {
        await props.actionFolderLoaded(folderItemsType, {scheduleTasks: data, childFolders: []}, isSortingAvailable, false, !!searchParams.get("search"))

        if(searchParams.get("search")) {
            const actionSearchParams = {
                open: true,
                searchString: searchParams.get("search"),
                isRecursive: searchParams.get("isRecursive") === 'true' ? true : false,
            }

            await props.actionSearchClick(folderItemsType, state.currentFolderId, actionSearchParams)
        }
    }

    return (
        <div style={{display: 'flex', flex: 1}}>
            <DataLoader
                loadFunc={dataHub.scheduleController.taskGetAll}
                loadParams={[]}
                reload={reload}
                isSearchLoading = {state.isSearchLoading}
                onDataLoaded = {(data) => handleDataLoaded(data)}
                onDataLoadFailed={(message) => { props.actionFolderLoadFailed(folderItemsType, message) }}
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
                    onSearchClick = {handleSearchItems}
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