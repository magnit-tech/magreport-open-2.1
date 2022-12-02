import React, {useState} from "react";
import {useSnackbar} from "notistack";

import { useParams, useNavigate } from 'react-router-dom'

// dataHub
import dataHub from "ajax/DataHub";

// components
import {CircularProgress} from "@material-ui/core";

// local components
import DataLoader from "main/DataLoader/DataLoader";
import PageTabs from "main/PageTabs/PageTabs";
import DesignerPage from "main/Main/Development/Designer/DesignerPage";
import DesignerTextField from "main/Main/Development/Designer/DesignerTextField";
import ScheduleParameters from "./ScheduleParameters";

/**
 * Компонент создания и редактирования расписаний
 * @return {JSX.Element}
 * @constructor
 */
export default function ScheduleDesigner() {

    const {id} = useParams()
    const navigate = useNavigate();

    const {enqueueSnackbar} = useSnackbar();

    const [uploading, setUploading] = useState(false);

    const [name, setName] = useState();
    const [description, setDescription] = useState();
    const [scheduleType, setScheduleType] = useState();
    const [parameters, setParameters] = useState({});
    // const [tasks, setTasks] = useState([]);

    const [nameError, setNameError] = useState(true);
    const [descriptionError, setDescriptionError] = useState(true);
    const [parametersErrors, setParametersErrors] = useState({scheduleTypeId: true})

    const pageName = id ? "Редактирование расписания" : "Создание расписания";

    let loadFunc;
    let loadParams = [];

    if (id) {
        loadFunc = dataHub.scheduleController.get;
        loadParams = [id];
    }

    function hasErrors() {
        let result = nameError || descriptionError;
        Object.entries(parametersErrors).forEach(([_, value]) => result = result || value);
        return result;
    }

    function handleChangeName(newName) {
        setName(newName);
        setNameError(!Boolean(newName));
    }

    function handleChangeDescription(newDescription) {
        setDescription(newDescription);
        setDescriptionError(!Boolean(newDescription));
    }

    function handleChangeParameters(newParameters = {}, newErrors = {scheduleTypeId: true}) {
        setParameters(newParameters);
        setParametersErrors(newErrors);
    }

    function handleDataLoaded(loadedData) {
        // setId(loadedData.id);
        setScheduleType(loadedData.type);
        handleChangeName(loadedData.name);
        handleChangeDescription(loadedData.description);
        //handleChangeTasks(loadedData.tasks);
        handleChangeParameters(loadedData);
    }

    function handleDataLoadFailed(message) {
        enqueueSnackbar(`При получении данных возникла ошибка: ${message}`,
            {variant: "error"});
    }

    function handleSave() {
        if(hasErrors()) {
            enqueueSnackbar(`Форма содержит ошибки`, {variant: "error"});
            return;
        }
        if (!id) {
            dataHub.scheduleController.add(
                name,
                description,
                parameters,
                magResponse => handleAddedEdited(magResponse)
            );
            setUploading(true);
        } else {
            dataHub.scheduleController.edit(
                id,
                name,
                description,
                parameters,
                magRepResponse => handleAddedEdited(magRepResponse)
            );
            setUploading(true);
        }
    }

    function handleAddedEdited(magRepResponse) {
        
        if (magRepResponse.ok) {
            if (id) {
                navigate(`/schedules/view/${id}`)
            } else {
                navigate(`/schedules`)
            }
                
            enqueueSnackbar("Расписание успешно сохранено", {variant : "success"});
        } else {
            setUploading(false);
            const actionWord = id ? "обновлении" : "создании";
            enqueueSnackbar(`При ${actionWord} возникла ошибка: ${magRepResponse.data}`,
                {variant: "error"});
        }
    }

    // building component
    const tabs = [];

    // general
    tabs.push({
        tablabel: "Общие",
        tabcontent: uploading ? <CircularProgress/> :
            <DesignerPage
                onSaveClick={handleSave}
                onCancelClick={() => navigate(-1)}
            >
                <DesignerTextField
                    label="Название"
                    value={name}
                    onChange={handleChangeName}
                    displayBlock
                    fullWidth
                    error={nameError}
                />
                <DesignerTextField
                    label="Описание"
                    value={description}
                    onChange={handleChangeDescription}
                    displayBlock
                    fullWidth
                    error={descriptionError}
                />
                <ScheduleParameters
                    scheduleType={scheduleType}
                    data={parameters}
                    errors={parametersErrors}
                    onChange={handleChangeParameters}
                />
            </DesignerPage>
    })

    return <DataLoader
        loadFunc={loadFunc}
        loadParams={loadParams}
        onDataLoaded={handleDataLoaded}
        onDataLoadFailed={handleDataLoadFailed}
    >
        <PageTabs
            tabsdata={tabs}
            pageName={pageName}
        />
    </DataLoader>
}
