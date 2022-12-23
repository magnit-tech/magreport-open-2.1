import React, {useState} from "react";
import {useSnackbar} from "notistack";

import { useParams, useNavigate } from 'react-router-dom'

import { useDispatch } from "react-redux";
import { viewItemNavbar } from "redux/actions/navbar/actionNavbar";

// dataHub
import dataHub from "ajax/DataHub";

// local components
import DataLoader from "main/DataLoader/DataLoader";
import PageTabs from "components/PageTabs/PageTabs";
import ViewerPage from "main/Main/Development/Viewer/ViewerPage";
import ViewerTextField from "main/Main/Development/Viewer/ViewerTextField";
import ScheduleParametersViewer from "./ScheduleParametersViewer";
import {ViewerCSS} from "main/Main/Development/Viewer/ViewerCSS";

// functions
import {createViewerPageName} from "main/Main/Development/Viewer/viewerHelpers";

// constants
import {FolderItemTypes} from "main/FolderContent/FolderItemTypes";


export default function ScheduleViewer() {
    
    const { id } = useParams()
    const navigate = useNavigate();

    const dispatch = useDispatch()

    const {enqueueSnackbar} = useSnackbar();

    const classes = ViewerCSS();

    const [data, setData] = useState({tasks: []});

    function handleDataLoaded(loadedData) {
        setData(loadedData);
        dispatch(viewItemNavbar('schedules', loadedData.name, id, null, null))
    }

    function handleDataLoadFailed(message) {
        enqueueSnackbar(`При получении данных возникла ошибка: ${message}`,
            {variant: "error"});
    }

    // edit check
    let userInfo = dataHub.localCache.getUserInfo();

    let isAdmin = userInfo.isAdmin
    let isDeveloper = userInfo.isDeveloper
    let authority = data.authority

    let hasRWRight = isAdmin || (isDeveloper && authority === "WRITE");

    // building component
    const tabs = [];

    // general
    tabs.push({
        tablabel: "Общие",
        tabcontent:
            <div className={classes.viewerTabPage}>
                <ViewerTextField
                    label="Название"
                    value={data.name}
                />
                <ViewerTextField
                    label="Описание"
                    value={data.description}
                />
                <ScheduleParametersViewer
                    scheduleType={data.type}
                    data={data}
                />
            </div>
    })

    return <DataLoader
        loadFunc={dataHub.scheduleController.get}
        loadParams={[id]}
        onDataLoaded={handleDataLoaded}
        onDataLoadFailed={handleDataLoadFailed}
    >
        <ViewerPage
            itemType={FolderItemTypes.schedules}
            id={id}
            onOkClick={() => navigate('/ui/schedules')}
            disabledPadding={true}
            readOnly={!hasRWRight}
        >
            <PageTabs
                pageName={createViewerPageName(FolderItemTypes.schedules, data.name)}
                tabsdata={tabs}
            />
        </ViewerPage>

    </DataLoader>
}
