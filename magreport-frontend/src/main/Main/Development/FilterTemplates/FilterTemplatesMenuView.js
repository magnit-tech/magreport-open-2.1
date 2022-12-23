import React from 'react';
import { connect } from 'react-redux';

import { useParams, useNavigate, useLocation } from 'react-router-dom'

// dataHub
import dataHub from 'ajax/DataHub';

// actions
import {
    actionFolderLoaded,
    actionFolderLoadFailed,
    actionSearchClick
} from 'redux/actions/menuViews/folderActions';

// components
import DataLoader from '../../../DataLoader/DataLoader';
import FolderContent from '../../../FolderContent/FolderContent';
import SidebarItems from '../../Sidebar/SidebarItems'

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
 * @callback actionSearchClick
 * @param {String} folderItemsType - тип объекта из FolderItemsType
 * @param {Number} currentFolderId - идентификатор текущей папки
 * @param {Object} searchParams - параметры поиска
 */

/**
 * Компонент просмотра шаблонов фильтров
 * @param {Object} props - свойства компонента
 * @param {Object} props.state - привязанное состояние со списком расписаний
 * @param {actionFolderLoaded} props.actionFolderLoaded - действие, вызываемое при успешной загрузке данных
 * @param {actionFolderLoadFailed} props.actionFolderLoadFailed - действие, вызываемое в случае ошибки при получении данных
 * @param {actionSearchClick} props.actionSearchClick - действие, вызываемое при нажатии на кнопку поиска
 * @return {JSX.Element}
 * @constructor
 */

function FilterTemplatesMenuView(props){

    const {id} = useParams()
    const navigate = useNavigate()
    const location = useLocation()

    let state = props.state;

    let reload = {needReload : state.needReload};
    let folderItemsType = SidebarItems.development.subItems.filterTemplates.folderItemType;

    
    function handleFolderClick(folderId) {
        navigate(`/ui/filterTemplate/${folderId}`)
    }
    function handleItemClick(filterTemplateId) {
        navigate(`/ui/filterTemplate/${id}/view/${filterTemplateId}`, {state: location.pathname})
    }


    return(
        <div style={{display: 'flex', flex: 1}}>
            <DataLoader
                loadFunc = {dataHub.filterTemplateController.getFolder}
                loadParams = {id ? [Number(id)] : [null]}
                reload = {reload}
                onDataLoaded = {(data) => {props.actionFolderLoaded(folderItemsType, data)}}
                onDataLoadFailed = {(message) => {props.actionFolderLoadFailed(folderItemsType, message)}}
            >
                <FolderContent
                    itemsType = {folderItemsType}
                    showAddFolder = {false}
                    showAddItem = {false}
                    showItemControls={false}
                    data = {state.currentFolderData}
                    searchParams = {state.searchParams || {}}

                    onFolderClick = {handleFolderClick}
                    onItemClick={handleItemClick}
                    // onEditItemClick={handleEditItemClick}
                    // onDependenciesClick = {handleDependenciesClick}
                    // onAddItemClick={handleAddItemClick}

                    onSearchClick ={searchParams => {props.actionSearchClick(folderItemsType, state.currentFolderId, searchParams)}}
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
    actionSearchClick
}

export default connect(mapStateToProps, mapDispatchToProps)(FilterTemplatesMenuView);
