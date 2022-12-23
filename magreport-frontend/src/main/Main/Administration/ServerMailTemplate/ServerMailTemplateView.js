import React, {useState} from "react";

import { useParams, useNavigate, useLocation } from 'react-router-dom'

import { useDispatch } from "react-redux";
import { viewItemNavbar } from "redux/actions/navbar/actionNavbar";

import DataLoader from "main/DataLoader/DataLoader";
import dataHub from "../../../../ajax/DataHub";
import {useSnackbar} from "notistack";
import {ViewerCSS} from "../../Development/Viewer/ViewerCSS";
import ViewerPage from "../../Development/Viewer/ViewerPage";
import {FolderItemTypes} from "../../../FolderContent/FolderItemTypes";
import {createViewerTextFields} from "../../Development/Viewer/viewerHelpers";


export default function ServerMailTemplateView() {

    const { id, folderId } = useParams()
    const navigate = useNavigate();
    const location = useLocation();

    const dispatch = useDispatch()

    const classes = ViewerCSS();

    const {enqueueSnackbar} = useSnackbar();

    const [data, setData] = useState({});


    function actionLoaded(loadData) {
        setData(loadData)
        dispatch(viewItemNavbar('systemMailTemplates', loadData.name, id, folderId, loadData.path))
    }

    function actionFailedLoaded(message) {
        enqueueSnackbar(`При загрузке данных произошла ошибка: ${message}`, {variant: "error"});
    }

    const fieldsData = [
        {label: "Код шаблона", value: data.code},
        {label: "Название", value: data.name},
        {label: "Описание", value: data.description},
        {label: "Тема письма", value: data.subject},
        {label: "Тело письма", value: data.body},

    ];

    // edit check
    let userInfo = dataHub.localCache.getUserInfo();

    let isAdmin = userInfo.isAdmin
    let isDeveloper = userInfo.isDeveloper
    let authority = data.authority

    let hasRWRight = isAdmin || (isDeveloper && authority === "WRITE");

    return (
        <DataLoader
            loadFunc={dataHub.serverMailTemplateController.getMailTemplate}
            loadParams={[id]}
            reload={false}
            onDataLoaded={(data) => {
                actionLoaded(data)
            }}
            onDataLoadFailed={(message) => {
                actionFailedLoaded(message)
            }}
        >

            <ViewerPage
                id={data.id}
                folderId = {folderId}
                itemType={FolderItemTypes.systemMailTemplates}
                disabledPadding={true}
                onOkClick={() => location.state ? navigate(location.state) : navigate(`/ui/systemMailTemplates/${folderId}`)}
                pageName={`Просмотр шаблона письма: ${data.name}`}
                readOnly={!hasRWRight}
            >
                <div className={classes.viewerTabPage}>
                    {createViewerTextFields(fieldsData)}
                </div>
            </ViewerPage>
        </DataLoader>
    )

}


