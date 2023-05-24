import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';

import { useNavigate, useLocation, useSearchParams } from 'react-router-dom'

// dataHub
import dataHub from 'ajax/DataHub';

// actions
import {
    actionFolderLoaded,
    actionFolderLoadFailed,
    actionDeleteItemClick,
    actionSearchClick,
    actionSortClick
} from 'redux/actions/menuViews/folderActions';

// const
import {FolderItemTypes} from "main/FolderContent/FolderItemTypes";

// local components
import DataLoader from 'main/DataLoader/DataLoader';
import FolderContent from 'main/FolderContent/FolderContent';


/**
 * @callback actionFolderLoaded
 * @param {String} folderItemsType - тип объекта из FolderItemsType
 * @param {Object} data - полученные данные
 */

/**
 * @callback actionFolderLoadFailed
 * @param {String} folderItemsType - тип объекта из FolderItemsType
 * @param {String} message - сообщение об ошибке
 */

/**
 * @callback actionDeleteItemClick
 * @param {String} folderItemsType - тип объекта из FolderItemsType
 * @param {Number} parentFolderId - идентификатор родительской папки (для расписаний всегда null)
 * @param {Number} scheduleId - идентификатор расписания
 */

/**
 * Компонент просмотра и редактирования расписаний
 * @param {Object} props - свойства компонента
 * @param {Object} props.state - привязанное состояние со списком расписаний
 * @param {actionFolderLoaded} props.actionFolderLoaded - действие, вызываемое при успешной загрузке данных
 * @param {actionFolderLoadFailed} props.actionFolderLoadFailed - действие, вызываемое в случае ошибки при получении данных
 * @param {actionDeleteItemClick} props.actionDeleteItemClick - действие, вызываемое при нажатии кнопки удаления расписания
 * @return {JSX.Element}
 * @constructor
 */

function SchedulesMenuView(props) {

    const state = props.state;

    const navigate = useNavigate()
    const location = useLocation()
    const [searchParams, setSearchParams] = useSearchParams();
    const locationPreviousHistory = { state: location.pathname + location.search }

    const [reload, setReload] = useState({needReload : state.needReload});

    useEffect(() => {
        setReload({needReload: true})
    }, [searchParams, state.needReload])

    let folderItemsType = FolderItemTypes.schedules;
    let isSortingAvailable = true;

    
    function handleItemClick(scheduleId) {
        navigate(`/ui/schedules/view/${scheduleId}`, locationPreviousHistory)
    }
    function handleEditItemClick(scheduleId) {
        navigate(`/ui/schedules/edit/${scheduleId}`, locationPreviousHistory)
    }
    function handleAddItemClick() {
        navigate(`/ui/schedules/add`)
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
        await props.actionFolderLoaded(folderItemsType, {schedules: data, childFolders: []}, isSortingAvailable, false, !!searchParams.get("search"))

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
                loadFunc = {dataHub.scheduleController.getAll}
                loadParams = {[]}
                reload = {reload}
                isSearchLoading = {state.isSearchLoading}
                onDataLoaded = {(data) => handleDataLoaded(data)}
                onDataLoadFailed = {(message) => { props.actionFolderLoadFailed(folderItemsType, message) }}
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

                    onDeleteItemClick={scheduleId => { props.actionDeleteItemClick(folderItemsType, null, scheduleId) }}
                    onSearchClick = {handleSearchItems}
                    contextAllowed
                    sortParams = {state.sortParams || {}}
                    onSortClick ={sortParams => { props.actionSortClick(folderItemsType, state.currentFolderId, sortParams) }}
                />
            </DataLoader>
        </div>
    );
}

const mapStateToProps = state => {
    return {
        state: state.folderData
    };
}

const mapDispatchToProps = {
    actionFolderLoaded,
    actionFolderLoadFailed,
    actionDeleteItemClick,
    actionSearchClick,
    actionSortClick
};

export default connect(mapStateToProps, mapDispatchToProps)(SchedulesMenuView);
