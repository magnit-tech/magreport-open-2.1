import React, {useState} from "react";
import {useSnackbar} from "notistack";

import { useParams, useNavigate, useLocation } from 'react-router-dom'

import { useDispatch } from "react-redux";
import { viewItemNavbar } from "redux/actions/navbar/actionNavbar";

//local
import dataHub from "ajax/DataHub";
import DataLoader from "main/DataLoader/DataLoader";
import PageTabs from "components/PageTabs/PageTabs";
import {FolderItemTypes} from "main/FolderContent/FolderItemTypes";
import ViewerPage from "main/Main/Development/Viewer/ViewerPage";
import ViewerChildCard from "main/Main/Development/Viewer/ViewerChildCard";
import ViewerFieldMapping from "main/Main/Development/Viewer/ViewerFieldMapping";
import SecurityFilterRoles from "./SecurityFilterRoles";
import {ViewerCSS} from "main/Main/Development/Viewer/ViewerCSS";

import {createViewerTextFields, createViewerPageName} from "main/Main/Development/Viewer/viewerHelpers";

/**
 * Компонент просмотра фильтра безопасности
 * @param {Object} props - параметры компонента
 * @constructor
 */

export default function SecurityFilterViewer(props) {

    const classes = ViewerCSS();

    const { id, folderId } = useParams()
    const navigate = useNavigate();
    const location = useLocation();

    const dispatch = useDispatch()

    const {enqueueSnackbar} = useSnackbar();

    const [data, setData] = useState({});


    function handleDataLoaded(loadedData) {
        setData(loadedData);   
        dispatch(viewItemNavbar('securityFilters', loadedData.name, id, folderId, loadedData.path))
    }

    function handleDataLoadFailed(message) {
        enqueueSnackbar(`При получении данных Фильтра безопасности возникла ошибка: ${message}`,
            {variant: "error"});
    }

    // edit check
    let userInfo = dataHub.localCache.getUserInfo();

    let isAdmin = userInfo.isAdmin
    let isDeveloper = userInfo.isDeveloper
    let authority = data.authority

    let hasRWRight = isAdmin || (isDeveloper && authority === "WRITE");

    // build component

    const detailsData = [
        {label: "Название", value: data.name},
        {label: "Описание", value: data.description},
    ];

    let filterInstanceCard = data.filterInstance ? (
        <ViewerChildCard
            id={data.filterInstance.id}
            parentFolderId={data.filterInstance.folderId}
            itemType={FolderItemTypes.filterInstance}
            name={data.filterInstance.name}
        />
    ) : "";

    const detailsTab = {
        tablabel: "Детали фильтра",
        tabcontent: (
            <div className={classes.viewerTabPage}>
                {createViewerTextFields(detailsData)}
                {filterInstanceCard}
            </div>
        )
    };

    const filterInstanceFieldsById = data.filterInstance ?
        Object.fromEntries(data.filterInstance.fields.map(f => [f.id, f.name]))
        : {};

    const dataSetsCards = [];
    for(let dataSet of data.dataSets || []) {
        const dataSetFieldsById = Object.fromEntries(dataSet.dataSet.fields.map(f => [f.id, f.name]));
        const fieldMappings = dataSet.fields.map(f => (
            <ViewerFieldMapping
                key={f.dataSetFieldId}
                leftLabel={"Поле экземпляра"}
                leftValue={filterInstanceFieldsById[f.filterInstanceFieldId]}
                rightLabel={"Поле набора данных"}
                rightValue={dataSetFieldsById[f.dataSetFieldId]}
            />
        ));
        dataSetsCards.push(
            <ViewerChildCard
                key={dataSet.dataSet.id}
                id={dataSet.dataSet.id}
                parentFolderId={dataSet.dataSet.folderId}
                itemType={FolderItemTypes.dataset}
                name={dataSet.dataSet.name}
            >
                <div>
                    {fieldMappings}
                </div>
            </ViewerChildCard>
        );
    }

    const dataSetsTab = {
        tablabel: "Наборы данных",
        tabcontent: (
            <div className={classes.viewerTabPage}>
                {dataSetsCards}
            </div>
        )
    };

/*    const roleCards = [];
    for(let roleSetting of data.roleSettings || []) {
        const role = roleSetting.role || {};
        const tuples = roleSetting.tuples || [];
        const tupleMappings = [];

        tuples.forEach((tuple) => {
            (tuple.values || []).forEach((value) => {
                tupleMappings.push(
                    <ViewerFieldMapping
                        leftLabel={"Поле экземпляра"}
                        leftValue={filterInstanceFieldsById[value.fieldId]}
                        rightLabel={"Значение"}
                        rightValue={value.value}
                    />
                );
            })
        });

        roleCards.push(
            <ViewerChildCard
                id={role.id}
                itemType={FolderItemTypes.roles}
                name={role.name}
            >
                {tupleMappings}
            </ViewerChildCard>
        );
    }
*/
    const rolesSettingsTab = {
        tablabel: "Привязка роли",
        //tabdisabled: data.securityFilterName && data.securityFilterDescription && selectedFilterInstance ? false: true,
        tabcontent:
            <SecurityFilterRoles 
                securityFilterId={id}
                filterInstance={{...data.filterInstance}}
                onExit={props.onExit}
                mode='view'
            />
    };

    const children =
        <PageTabs
            pageName={createViewerPageName(FolderItemTypes.securityFilters, data.name)}
            tabsdata={[
                detailsTab,
                dataSetsTab,
                rolesSettingsTab,
            ]}
        />;

    return (
        <DataLoader
            loadFunc={dataHub.securityFilterController.get}
            loadParams={[id]}
            onDataLoaded={handleDataLoaded}
            onDataLoadFailed={handleDataLoadFailed}
        >
            <ViewerPage
                id={data.id}
                folderId = {folderId}
                itemType={FolderItemTypes.securityFilters}
                disabledPadding={true}
                onOkClick={() => location.state ? navigate(location.state) : navigate(`/securityFilters/${folderId}`)}
                readOnly={!hasRWRight}
            >
                {children}
            </ViewerPage>
        </DataLoader>
    );
}
