import React from "react";
import {connect} from "react-redux";

import { useParams, useNavigate } from 'react-router-dom'

// data
import DataLoader from "main/DataLoader/DataLoader";
import dataHub from "ajax/DataHub";

// local
import PageTabs from 'components/PageTabs/PageTabs';
import ViewerPage from "main/Main/Development/Viewer/ViewerPage";
import ViewerTextField from "main/Main/Development/Viewer/ViewerTextField";
import ViewerChildCard from "main/Main/Development/Viewer/ViewerChildCard";
import {ViewerCSS} from "main/Main/Development/Viewer/ViewerCSS";

import ExternalSecuritySourceViewer from "./ASMSecuritySourceViewer";
import {actionAsmDataLoaded, actionAsmDataLoadFailed} from "redux/actions/admin/actionAsm";
import {hideAlertDialog, showAlertDialog} from "redux/actions/UI/actionsAlertDialog";
import { viewItemNavbar } from "redux/actions/navbar/actionNavbar";

import {FolderItemTypes} from "main/FolderContent/FolderItemTypes";

// functions
import {createViewerPageName} from "main/Main/Development/Viewer/viewerHelpers";

/**
 * @callback actionAsmDataLoaded
 * @param {Object} data
 */
/**
 * @callback actionAsmDataLoadFailed
 * @param {String} error
 */
/**
 * @callback showAlertDialog
 */
/**
 * @callback hideAlertDialog
 */
/**
 * Компонент просмотра объекта ASM
 * @param {Object} props - свойства компонента
 * @param {Object} props.state - asmDesigner State
 * @param {actionAsmDataLoaded} props.actionAsmDataLoaded - callback, добавляет в state успешно загруженные данных
 * @param {actionAsmDataLoadFailed} props.actionAsmDataLoadFailed - callback, добавляет в state загруженные данные
 * @param {showAlertDialog} props.showAlertDialog - показать диалоговое окно
 * @param {hideAlertDialog} props.hideAlertDialog - скрыть диалоговое окно
 * @returns {JSX.Element}
 * @constructor
 */
function ASMViewer(props) {

    const {id} = useParams()
    const navigate = useNavigate();

    const loadFunc = dataHub.asmController.get;
    const loadParams = [id];

    const classes = ViewerCSS();

    const state = props.state;

    const data = state.responseData || {};
    const fieldMappings = state.fieldMappings;

    const roleType = data.roleType || {};
    const name = data.name;
    const description = data.description;
    const securitySources = data.sources || [];

    let tabs = []

    tabs.push({
        tablabel: 'Детали объекта ASM',
        tabcontent:
            <div className={classes.viewerTabPage}>
                <ViewerChildCard
                    id={roleType.id}
                    name={roleType.name}
                    itemType={FolderItemTypes.roles}
                />
                <ViewerTextField
                    label="Название"
                    value={name}
                />
                <ViewerTextField
                    label="Описание"
                    value={description}
                />
            </div>
    })

    securitySources.forEach((securitySource, index) => {
        tabs.push({
            tablabel: "Настройка " + securitySource.sourceType,
            tabcontent:
                <ExternalSecuritySourceViewer
                    key={index}
                    securitySource={securitySource}
                    fieldMappings={fieldMappings[index]}
                />});
    })

    return (
        <DataLoader
            loadFunc={loadFunc}
            loadParams={loadParams}
            onDataLoaded={(loadedData) => props.actionAsmDataLoaded(loadedData, 'view')}
            onDataLoadFailed={(error) => props.actionAsmDataLoadFailed(error)}
        >
            <ViewerPage
                id={id}
                itemType={FolderItemTypes.asm}
                disabledPadding={true}
                onOkClick={() => navigate('/asm')}
                >
                <PageTabs
                    tabsdata={tabs}
                    pageName={createViewerPageName(FolderItemTypes.asm, name)}
                />
            </ViewerPage>
        </DataLoader>
    );
}

const mapStateToProps = (state) => {
    return {
        state: state.asmDesigner
    }
}

const mapDispatchToProps = {
    actionAsmDataLoaded,
    actionAsmDataLoadFailed,
    showAlertDialog,
    hideAlertDialog,
    viewItemNavbar
}

export default connect(mapStateToProps, mapDispatchToProps)(ASMViewer);