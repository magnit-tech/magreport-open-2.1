import React, { useState, useEffect } from "react";

import { useNavigate, useParams, useLocation } from 'react-router-dom';

import { useDispatch } from "react-redux";
import { editItemNavbar, addItemNavbar } from "redux/actions/navbar/actionNavbar";

import DataLoader from "main/DataLoader/DataLoader";
import dataHub from "../../../../ajax/DataHub";
import {useSnackbar} from "notistack";
import DesignerTextField from "../../Development/Designer/DesignerTextField";
import DesignerPage from "../../Development/Designer/DesignerPage";
import TagsPanel from "./TagsPanel";
import {Button, Dialog, DialogContent, TextField} from "@material-ui/core";
import StyleTextPanel from "./StyleTextPanel";
import {DesignerCSS} from "../../Development/Designer/DesignerCSS";
import DesktopWindowsIcon from '@material-ui/icons/DesktopWindows';
import clsx from "clsx";
import Grid from "@material-ui/core/Grid";


/**
 * Компонент просмотра расписаний
 * @param {Object} props - параметры компонента
 * @return {JSX.Element}
 * @constructor
 */
export default function ServerMailTemplateDesigner(props) {

    const { id, folderId } = useParams()
    const navigate = useNavigate();
    const location = useLocation();

    const dispatch = useDispatch()

    useEffect(() => {
        if(!id) {
            dataHub.serverMailTemplateController.getFolder(folderId, handleFoldersLoaded)
        }
    }, []) // eslint-disable-line

    const {enqueueSnackbar} = useSnackbar();
    const [data, setData] = useState({});
    const classes = DesignerCSS();

    const [selectTag, updateTag] = useState(null)
    const [styleTag, updateStyleTag] = useState(null)
    const [listTags, setListTags] = useState([])
    const [viewResult, updateViewResult] = useState(false)


    /*
        Data loading
    */

    function handleFoldersLoaded({ok, data}) {
        if(ok) {
            dispatch(addItemNavbar('systemMailTemplates', folderId, data.path))
        }
    }

    function actionLoaded(loadData) {
        setData(loadData)
        getTags(loadData.type)
        if (id) {
            dispatch(editItemNavbar('systemMailTemplates', loadData.name, id, folderId, loadData.path))
        }
    }

    function actionFailedLoaded(message) {
        enqueueSnackbar(`При загрузке данных произошла ошибка: ${message}`, {variant: "error"});
    }

    function handleChange(key, value) {
        setData({
            ...data,
            [key]: value
        });

    }

    function selectedTag(tag) {
        updateTag(tag)
    }

    function selectedStyleTag(tag) {
        updateStyleTag(tag)
    }

    function handleSave() {
        dataHub.serverMailTemplateController
            .editMailTemplate(
                data.id,
                data.name,
                data.description,
                data.subject,
                data.body,
                magRepResponse => handleEdited(magRepResponse)
            )
    }

    function handleEdited(response) {

        if (!response.ok) {
            enqueueSnackbar(`При обновлении шаблона возникла ошибка: ${response.data}`,
                {variant: "error"});
        } else {
            location.state ? navigate(location.state) : navigate(`/systemMailTemplates/${folderId}`)
            enqueueSnackbar("Шаблон письма успешно сохранен", {variant : "success"});
        }
    }

    function insertTag(target) {
        if (target.selectionStart === target.selectionEnd) {
            if (selectTag !== null) {
                let message = target.value.slice(0, target.selectionStart) + selectTag + target.value.slice(target.selectionStart)
                handleChange(target.id, message)
            }
        } else {
            if (styleTag !== null && target.id !== "subject") {
                let message = target.value.slice(0, target.selectionStart) +
                    "<" + styleTag + ">" +
                    target.value.slice(target.selectionStart, target.selectionEnd) +
                    "</" + styleTag + ">" +
                    target.value.slice(target.selectionEnd)
                handleChange(target.id, message)
            }
        }
    }

    function getTags(type) {
        switch (type) {
            case "SCHEDULE" :
             setListTags(schedules.concat(errors))
                break;
            default :
               setListTags(errors)
        }
    }

    function changeFlow() {
        updateViewResult(!viewResult)
    }

    let schedules = [
        {label: "Имя отчета", value: "{reportName}", state : false},
        {label: "ID отчета", value: "{reportId}", state : false},
        {label: "ID задания", value: "{reportJobId}", state : false},
        {label: "ID задания по расписанию", value: "{taskId}", state : false},
        {label: "Название задания по расписанию", value: "{taskName}", state : false},
        {label: "Ссылка на продление подписки", value: "{prolongationLink}", state : false},
        {label: "Описание задания по расписанию", value: "{taskDescription}", state : false},
        {label: "Дата окончания подписки", value: "{expiredDate}", state : false},
        {label: "Ссылка на скачивание файла", value: "{fileLink}", state: false}
    ];

    let errors = [
        {label: "Текущая дата", value: "{currentDataTime}", state : false},
        {label: "Текст ошибки", value: "{textError}", state : false},
        {label: "stackTrace", value: "{stackTrace}", state : false}
    ]


    return (<DataLoader
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
        <DesignerPage
            onSaveClick={handleSave}
            onCancelClick={() => location.state ? navigate(location.state) : navigate(`/systemMailTemplates/${folderId}`)}
            name={'Редактирование письма отправки: ' + data.code}
        >
            <DesignerTextField
                value={data.name}
                label={'Название'}
                type={'text'}
                onChange={value => handleChange("name", value)}
            />

            <DesignerTextField
                value={data.description}
                label={'Описание'}
                type={'text'}
                onChange={value => handleChange("description", value)}
            />

            <TextField
                className={clsx(classes.field, {[classes.displayBlock]: props.displayBlock})}
                style={{minWidth: props.minWidth}}
                id={"subject"}
                value={data.subject}
                label={'Тема письма'}
                type={'text'}
                variant="outlined"
                onChange={value => handleChange("subject", value.target.value)}
                onClick={click => insertTag(click.target)}
            />

            <TextField
                className={clsx(classes.field, {[classes.displayBlock]: props.displayBlock})}
                style={{minWidth: props.minWidth}}
                id={"body"}
                value={data.body}
                label={'Тело письма'}
                type={'text'}
                multiline={true}
                minRows={6}
                variant="outlined"
                onChange={value => handleChange("body", value.target.value)}
                onClick={click => insertTag(click.target)}
            />

            <Grid
                container
                direction="row"
                alignItems="stretch">
                <StyleTextPanel onClick={selectedStyleTag}/>
                <Button
                    type="submit"
                    variant="contained"
                    size="small"
                    color="primary"
                    onClick={changeFlow}>
                    <DesktopWindowsIcon/>
                </Button>

            </Grid>
            <TagsPanel
                data={listTags}
                onClick={selectedTag}/>

        </DesignerPage>

        <Dialog open={viewResult}
               >
            <DialogContent id="subject-mail-template"> <b>Тема сообщения:</b> {data.subject} </DialogContent>
            <DialogContent id="body-mail-template">
                <b>Текст сообщения:</b>
                <div dangerouslySetInnerHTML={{__html: data.body}}/>
            </DialogContent>
            <Button
                type="submit"
                variant="contained"
                size="small"
                color="primary"
                onClick={changeFlow}>
                OK
            </Button>
        </Dialog>
</DataLoader>

)


}