import React, {useState} from 'react';
import {connect} from "react-redux";
import {useSnackbar} from 'notistack';

import { useParams, useNavigate, useLocation } from 'react-router-dom'

// local
import ViewerPage from 'main/Main/Development/Viewer/ViewerPage';
import ViewerTextField from 'main/Main/Development/Viewer/ViewerTextField';
import ViewerChildCard from "main/Main/Development/Viewer/ViewerChildCard";
import ReportFieldsViewer from './ReportFieldsViewer';
import ReportTemplatesViewer from './ReportTemplatesViewer'
import ReportFiltersViewerTab from './ReportFilters/ReportFiltersViewerTab'
import PageTabs from 'components/PageTabs/PageTabs';
import DataLoader from 'main/DataLoader/DataLoader';
import {ViewerCSS} from "main/Main/Development/Viewer/ViewerCSS";
import {FolderItemTypes} from 'main/FolderContent/FolderItemTypes';
import {sortByOrdinal } from "./reportFunctions";

// dataHub
import dataHub from 'ajax/DataHub';

// actions
import {actionLoaded, actionLoadedFailed} from "redux/actions/developer/actionReportTemplates";
import { viewItemNavbar } from "redux/actions/navbar/actionNavbar";

// functions
import {createViewerPageName} from "../Viewer/viewerHelpers";


/**
 * @callback actionLoaded
 * @param {Number} id
 * @param {Object} data
 */

/**
 * @callback actionLoadedFailed
 * @param {Number} id
 * @param {Object} data
 */
/**
 * Компонент просмотра объекта-отчета
 * @param {Object } props - параметры компонента
 * @param {actionLoaded} props.actionLoaded - action, вызываемый при загрузке данных отчета
 * @param {actionLoadedFailed} props.actionLoadedFailed - action, вызываемый при ошибке загрузки данных отчета
 */

function ReportDevViewer({actionLoaded = f=>f, actionLoadedFailed = f=>f, viewItemNavbar = f=>f}) {

    const { id, folderId } = useParams()
    const navigate = useNavigate();
    const location = useLocation();

    const {enqueueSnackbar} = useSnackbar();
    const classes = ViewerCSS();

    const [data, setData] = useState({});
    const [dataSet, setDataSet] = useState({});
    const [fieldValues, setFieldValues] = useState(null);

    function handleDataLoaded(loadedData) {
        setData({
            ...data,
            ...loadedData,
        });
        setFieldValues(sortByOrdinal(loadedData.fields));
        viewItemNavbar('reportsDev', loadedData.name, id, folderId, loadedData.path)
    }

    function handleDataSetLoaded(loadedData) {
        setDataSet({
            ...dataSet,
            ...loadedData
        })
    }

    function handleDataLoadFailed(message) {
        enqueueSnackbar(`При получении данных возникла ошибка: ${message}`,
            {variant: "error"});
    }

    // constructing component
    // tabs
    const tabs = []

    // headers tab
    tabs.push({
        tablabel: "Заголовки",
        tabcontent:
            <div className={classes.viewerTabPage}>
                <ViewerTextField
                    label={"Название отчета"}
                    value={data.name}
                />

                <ViewerTextField
                    label={"Описание отчета"}
                    value={data.description}
                />

                <ViewerChildCard
                    id={dataSet.id}
                    parentFolderId={dataSet.dataSource?.folderId}
                    itemType={FolderItemTypes.dataset}
                    name={dataSet.name}
                />

                <ViewerTextField
                    label={"Ссылка на реестр требований"}
                    value={data.requirementsLink}
                />
            </div>
    });

    // fields tab
    tabs.push({
        tablabel: "Поля",
        tabcontent:
            <div className={classes.viewerTabPage}>
                <ReportFieldsViewer
                    fields={fieldValues}
                    dataSet={dataSet}
                />
            </div>
    });

    // filters tab
    tabs.push({
        tablabel: "Фильтры",
        tabcontent:
            <div className={classes.viewerTabPage}>
                <ReportFiltersViewerTab
                    childGroupInfo={data.filterGroup || {}}
                    reportId={id}
                    reportFields={data.fields || []}
                />
            </div>
    });

    // report templates tab
    tabs.push({
        tablabel: "Шаблоны отчетов",
        tabcontent:
            <div className={classes.viewerTabPage}>
                <ReportTemplatesViewer
                    reportId={id}
                />
            </div>
    });

    return (
        <DataLoader
            loadFunc={dataHub.reportController.get}
            loadParams={[id, undefined]}
            onDataLoaded={handleDataLoaded}
            onDataLoadFailed={handleDataLoadFailed}
        >
            <DataLoader
                loadFunc={dataHub.datasetController.get}
                loadParams={[data.dataSetId]}
                onDataLoaded={handleDataSetLoaded}
                onDataLoadFailed={handleDataLoadFailed}
            >
                <DataLoader
                    loadFunc={dataHub.excelTemplateController.get}
                    loadParams={[id]}
                    onDataLoaded={loadedData => actionLoaded(id, loadedData)}
                    onDataLoadFailed={loadedData => actionLoadedFailed(id, loadedData)}
                >
                <ViewerPage
                    id={data.id}
                    folderId = {folderId}
                    itemType={FolderItemTypes.reportsDev}
                    onOkClick={() => location.state ? navigate(location.state) : navigate(`/ui/reportsDev/${folderId}`)}
                    disabledPadding={true}
                >
                    <PageTabs
                        tabsdata={tabs}
                        pageName={createViewerPageName(FolderItemTypes.reportsDev, data.name)}
                    />
                </ViewerPage>
                </DataLoader>
            </DataLoader>
        </DataLoader>
    );
}

const mapDispatchToProps = {
    actionLoaded,
    actionLoadedFailed,
    viewItemNavbar
}

export default connect(null, mapDispatchToProps)(ReportDevViewer);
