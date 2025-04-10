import React, {useState} from "react";
import {useSnackbar} from "notistack";

import { useParams, useNavigate, useLocation } from 'react-router-dom'

import dataHub from "ajax/DataHub";
import { connect } from 'react-redux';
import { showAlert, hideAlert } from '../../../../redux/actions/UI/actionsAlert'
import { viewItemNavbar } from "redux/actions/navbar/actionNavbar";

import DataLoader from "main/DataLoader/DataLoader";

import {FolderItemTypes} from "main/FolderContent/FolderItemTypes";

import ViewerPage from "main/Main/Development/Viewer/ViewerPage";
import {ViewerCSS} from "main/Main/Development/Viewer/ViewerCSS";

import {createViewerTextFields} from "main/Main/Development/Viewer/viewerHelpers";

import Button from '@material-ui/core/Button';
import Alerts from "components/Alerts/Alerts";

import Icon from '@mdi/react'  
import { mdiCheckDecagram } from '@mdi/js';


/**
 * Компонент просмотра источника данных
 * @param {Object} props - параметры компонента
 * @constructor
 */

function DatasourceViewer(props) {

    const classes = ViewerCSS();

    const { id, folderId } = useParams()
    const navigate = useNavigate();
    const location = useLocation();

    const {enqueueSnackbar} = useSnackbar();

    const [data, setData] = useState({});
    

    function handleDataLoaded(loadedData) {
        setData(loadedData);
        props.viewItemNavbar('datasource', loadedData.name, id, folderId, loadedData.path)
    }

    function handleDataLoadFailed(message) {
        enqueueSnackbar(`При получении источника данных возникла ошибка: ${message}`,
            {variant: "error"});
    }

    function checkConnection(id) {
        dataHub.datasourceController.check(id,  checkConnectionAnswer);
    }

    function checkConnectionAnswer(magrepResponse){

        function callback(){
            props.hideAlert();
        }
        
        const buttons = [{'text':'OK','onClick': callback}]
        
        if(magrepResponse.ok) {
            props.showAlert(<Icon path={mdiCheckDecagram} size={1.5} color="#7CB342"/>, "Подключение успешно", buttons, callback)
        } else {
            props.showAlert("Ошибка", magrepResponse.data, buttons, callback)
        }
    }

    // edit check
    let userInfo = dataHub.localCache.getUserInfo();

    let isAdmin = userInfo.isAdmin
    let isDeveloper = userInfo.isDeveloper
    let authority = data.authority

    let hasRWRight = isAdmin || (isDeveloper && authority === "WRITE");


    // build component
    const fieldsData = [
        {label: "Название", value: data.name},
        {label: "Описание", value: data.description},
        {label: "Тип источника", value: data.type ? data.type.name : ""},
        {label: "Строка подключения", value: data.url},
        {label: "Имя пользователя", value: data.userName},
        {label: "Пароль", value: "*****"},
        {label: "Размер пула коннектов", value: data.poolSize}
    ];

    return (
        <DataLoader
            loadFunc={dataHub.datasourceController.get}
            loadParams={[id]}
            onDataLoaded={handleDataLoaded}
            onDataLoadFailed={handleDataLoadFailed}
        >
            <ViewerPage
                id={data.id}
                name={data.name}
                folderId = {folderId}
                itemType={FolderItemTypes.datasource}
                disabledPadding={true}
                onOkClick={() => location.state ? navigate(location.state) : navigate(`/ui/datasource/${folderId}`)}
                pageName = {`Просмотр источника данных: ${data.name}`}
                readOnly={!hasRWRight}
            >
                <div className={classes.viewerTabPage}>
                    {createViewerTextFields(fieldsData)}
                    <Button
                        className={classes.pageBtnConnection}
                        type="submit"
                        variant="contained"
                        size="small"
                        color="primary"
                        onClick={() => checkConnection(data.id)}
                    >
                        Проверить подключение
                    </Button>
                </div>
            </ViewerPage>
            <Alerts/>
        </DataLoader>
    );
}

const mapDispatchToProps = {
    showAlert,
    hideAlert,
    viewItemNavbar
}

export default connect(null, mapDispatchToProps)(DatasourceViewer);
