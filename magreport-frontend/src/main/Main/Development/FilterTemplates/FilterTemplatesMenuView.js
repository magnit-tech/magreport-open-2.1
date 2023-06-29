import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';

import { useParams, useNavigate, useLocation, useSearchParams } from 'react-router-dom'

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

    const state = props.state;

    const {id} = useParams()
    const navigate = useNavigate()
    const location = useLocation()
    const [searchParams, setSearchParams] = useSearchParams();
    const locationPreviousHistory = { state: location.pathname + location.search }

    const [reload, setReload] = useState({needReload : state.needReload});
    const folderItemsType = SidebarItems.development.subItems.filterTemplates.folderItemType;

    useEffect(() => {
        setReload({needReload: true})
    }, [searchParams, state.needReload])
    
    function handleFolderClick(folderId) {
        navigate(`/ui/filterTemplate/${folderId}`)
    }
    function handleItemClick(filterTemplateId) {
        navigate(`/ui/filterTemplate/${id}/view/${filterTemplateId}`, locationPreviousHistory)
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
        await props.actionFolderLoaded(folderItemsType, data, false, false, !!searchParams.get("search"))

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
        <div style={{display: 'flex', flex: 1}}>
            <DataLoader
                loadFunc = {dataHub.filterTemplateController.getFolder}
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
                    showItemControls={false}
                    data = {state.filteredFolderData ? state.filteredFolderData : state.currentFolderData}
                    searchParams = {state.searchParams || {}}

                    onFolderClick = {handleFolderClick}
                    onItemClick = {handleItemClick}
                    // onEditItemClick={handleEditItemClick}
                    // onDependenciesClick = {handleDependenciesClick}
                    // onAddItemClick={handleAddItemClick}

                    onSearchClick = {handleSearchItems}
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
