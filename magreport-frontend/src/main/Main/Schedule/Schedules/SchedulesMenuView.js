import React from 'react';
import {connect} from 'react-redux';

import { useNavigate } from 'react-router-dom'

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

    const navigate = useNavigate()

    const state = props.state;

    let reload = {needReload: state.needReload};
    let folderItemsType = FolderItemTypes.schedules;
    let isSortingAvailable = true;

    function handleItemClick(scheduleId) {
        navigate(`/ui/schedules/view/${scheduleId}`)
    }
    function handleEditItemClick(scheduleId) {
        navigate(`/ui/schedules/edit/${scheduleId}`)
    }
    function handleAddItemClick() {
        navigate(`/ui/schedules/add`)
    }

    return (
        <div style={{display: 'flex', flex: 1}}>
            <DataLoader
                loadFunc={dataHub.scheduleController.getAll}
                loadParams={[]}
                reload={reload}
                onDataLoaded={(data) => {
                    props.actionFolderLoaded(folderItemsType, {schedules: data, childFolders: []}, isSortingAvailable)
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

                    onDeleteItemClick={scheduleId => { props.actionDeleteItemClick(folderItemsType, null, scheduleId) }}
                    onSearchClick ={searchParams => { props.actionSearchClick(folderItemsType, [], searchParams) }}
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
