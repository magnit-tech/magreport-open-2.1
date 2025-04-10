import React from 'react';
import {connect} from 'react-redux';

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
 * @param {Number} themeId - идентификатор расписания
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
function ThemesMenuView(props) {

    const state = props.state;

    let loadFunc = dataHub.themeController.getAll;

    let reload = {needReload: state.needReload};
    let folderItemsType = FolderItemTypes.theme;
    let isSortingAvailable = true;


    return (
        <div style={{display: 'flex', flex: 1}}>
            <DataLoader
                loadFunc={loadFunc}
                loadParams={[]}
                reload={reload}
                onDataLoaded={(data) => {
                    props.actionFolderLoaded(folderItemsType, {themes: data, childFolders: []}, isSortingAvailable)
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
                    // onItemClick={(themeId) => {
                    //     props.actionItemClick(folderItemsType, themeId)
                    // }}
                    // onAddItemClick={() => {
                    //     props.actionAddItemClick(folderItemsType)
                    // }}
                    // onEditItemClick={(themeId) => {
                    //     props.actionEditItemClick(folderItemsType, themeId)
                    // }}
                    onDeleteItemClick={(themeId) => {
                        props.actionDeleteItemClick(folderItemsType, null, themeId)
                    }}
                    onSearchClick ={searchParams => {props.actionSearchClick(folderItemsType, [], searchParams)}}
                    contextAllowed
                    sortParams = {state.sortParams || {}}
                    onSortClick ={sortParams => {props.actionSortClick(folderItemsType, state.currentFolderId, sortParams)}}
                />
            </DataLoader>
        </div>
    )

    // let component;

    // if (state.flowState === FLOW_STATE_BROWSE_FOLDER) {
    //     component = (
    //         <DataLoader
    //             loadFunc={loadFunc}
    //             loadParams={[]}
    //             reload={reload}
    //             onDataLoaded={(data) => {
    //                 props.actionFolderLoaded(folderItemsType, {themes: data, childFolders: []}, isSortingAvailable)
    //             }}
    //             onDataLoadFailed={(message) => {
    //                 props.actionFolderLoadFailed(folderItemsType, message)
    //             }}
    //         >
    //             <FolderContent
    //                 itemsType={folderItemsType}
    //                 showAddFolder={false}
    //                 showAddItem={true}
    //                 data = {state.filteredFolderData ? state.filteredFolderData : state.currentFolderData}
    //                 searchParams={state.searchParams || {}}
    //                 // onItemClick={(themeId) => {
    //                 //     props.actionItemClick(folderItemsType, themeId)
    //                 // }}
    //                 // onAddItemClick={() => {
    //                 //     props.actionAddItemClick(folderItemsType)
    //                 // }}
    //                 // onEditItemClick={(themeId) => {
    //                 //     props.actionEditItemClick(folderItemsType, themeId)
    //                 // }}
    //                 onDeleteItemClick={(themeId) => {
    //                     props.actionDeleteItemClick(folderItemsType, null, themeId)
    //                 }}
    //                 onSearchClick ={searchParams => {props.actionSearchClick(folderItemsType, [], searchParams)}}
    //                 contextAllowed
    //                 sortParams = {state.sortParams || {}}
    //                 onSortClick ={sortParams => {props.actionSortClick(folderItemsType, state.currentFolderId, sortParams)}}
    //             />
                
    //         </DataLoader>
    //     );
    // } else if (state.flowState === themesMenuViewFlowStates.themeDesigner) {
    //     component = <ThemeDesigner
    //         mode={designerMode}
    //         themeId={state.editThemeId}
    //         onExit={handleExit}
    //     />

    //     /*
    //     component = <ScheduleDesigner
    //         mode={designerMode}
    //         themeId={state.editthemeId}
    //         onExit={handleExit}
    //     />;
    // */} else if (state.flowState === themesMenuViewFlowStates.themeViewer) {/*
    //     component = <ScheduleViewer
    //         themeId={state.viewthemeId}
    //         onOkClick={handleExit}
    //     />;
    // */ } else {
    //     component = <div>Неизвестное состояние</div>;
    // }

    // return (
    //     <div style={{display: 'flex', flex: 1}}>
    //         {component}
    //     </div>
    // );
}

const mapStateToProps = state => {
    return {
        state: state.themesMenuView
    };
}

const mapDispatchToProps = {
    actionFolderLoaded,
    actionFolderLoadFailed,
    actionDeleteItemClick,
    actionSearchClick,
    actionSortClick
};

export default connect(mapStateToProps, mapDispatchToProps)(ThemesMenuView);
