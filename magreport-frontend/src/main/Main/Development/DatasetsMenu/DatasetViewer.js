import React, {useState} from "react";
import {useSnackbar} from "notistack";

import { useParams, useNavigate, useLocation } from 'react-router-dom'

import { useDispatch } from "react-redux";
import { viewItemNavbar } from "redux/actions/navbar/actionNavbar";

import dataHub from "ajax/DataHub";
import DataLoader from "main/DataLoader/DataLoader";

import PageTabs from "components/PageTabs/PageTabs";

import {FolderItemTypes} from "main/FolderContent/FolderItemTypes";

import ViewerPage from "main/Main/Development/Viewer/ViewerPage";
import ViewerChildCard from "main/Main/Development/Viewer/ViewerChildCard";
import ViewerTable from "main/Main/Development/Viewer/ViewerTable";
import {ViewerCSS} from "main/Main/Development/Viewer/ViewerCSS";

import {createViewerTextFields,
    createViewerPageName} from "main/Main/Development/Viewer/viewerHelpers";


export default function DatasetViewer() {

    const classes = ViewerCSS();

    const { id, folderId } = useParams()
    const navigate = useNavigate();
    const location = useLocation();

    const dispatch = useDispatch()

    const {enqueueSnackbar} = useSnackbar();

    const [data, setData] = useState({});

    const [typeById, setTypeById] = useState({});


    function handleDataLoaded(loadedData) {
        setData(loadedData);
        dispatch(viewItemNavbar('dataset', loadedData.name, id, folderId, loadedData.path))
    }

    function handleTypesLoaded(loadedData) {
        setTypeById(Object.fromEntries(loadedData.map(t => [t.id, t.name])));
    }

    function handleDataLoadFailed(message) {
        enqueueSnackbar(`При получении набора данных возникла ошибка: ${message}`,
            {variant: "error"});
    }

    // settings tab
    const settingsPreDataSourceData = [
        {label: "Название набора данных", value: data.name},
        {label: "Описание", value: data.description},
    ];

    let dataSourceCard = data.dataSource ? (
        <ViewerChildCard
            id={data.dataSource.id}
            parentFolderId={data.dataSource.folderId}
            itemType={FolderItemTypes.datasource}
            name={data.dataSource.name}
        />
    ) : "";

    const settingsPostDataSourceData = [
        {label: "Каталог", value: data.catalogName},
        {label: "Схема", value: data.schemaName},
        {label: "Объект", value: data.objectName},
        {label: "Тип объекта", value: typeById[data.typeId]},
    ];

    const settingsTab = {
        tablabel: "Настройки",
        tabcontent: (
            <div className={classes.viewerTabPage}>
                {createViewerTextFields(settingsPreDataSourceData)}
                {dataSourceCard}
                {createViewerTextFields(settingsPostDataSourceData)}
            </div>
        )
    };

    // fields tab
    const fieldsTableColumns = [
        {label: "ID поля", key: "id"},
        {label: "Название поля", key: "name"},
        {label: "Тип поля", key: "typeName"},
        {label: "Описание", key: "description"},
    ];
    const fieldsTableRows = data.fields;

    const fieldsTab = {
        tablabel: "Поля",
        tabcontent: (
            <div className={classes.viewerTabPage}>
                <ViewerTable
                    key={Math.random()}
                    columns={fieldsTableColumns}
                    rows={fieldsTableRows}
                    checkIsValidRow={(row) => row.isValid}
                />
            </div>
        )
    };

    // edit check
    let userInfo = dataHub.localCache.getUserInfo();

    let isAdmin = userInfo.isAdmin
    let isDeveloper = userInfo.isDeveloper
    let authority = data.authority

    let hasRWRight = isAdmin || (isDeveloper && authority === "WRITE");

    // component
    return (
        <DataLoader
            loadFunc={dataHub.datasetController.get}
            loadParams={[id]}
            onDataLoaded={handleDataLoaded}
            onDataLoadFailed={handleDataLoadFailed}
        >
            <DataLoader
                loadFunc = {dataHub.datasetController.getTypes}
                loadParams = {[]}
                onDataLoaded = {handleTypesLoaded}
                onDataLoadFailed = {handleDataLoadFailed}
            >
                <ViewerPage
                    id={data.id}
                    name={data.name}
                    folderId = {folderId}
                    itemType={FolderItemTypes.dataset}
                    disabledPadding={true}
                    onOkClick={() => location.state ? navigate(location.state) : navigate(`/dataset/${folderId}`)}
                    readOnly={!hasRWRight}
                >
                    <PageTabs
                        pageName={createViewerPageName(FolderItemTypes.dataset, data.name)}
                        tabsdata={[
                            settingsTab,
                            fieldsTab,
                        ]}
                    />
                </ViewerPage>
            </DataLoader>
        </DataLoader>
    );
}
