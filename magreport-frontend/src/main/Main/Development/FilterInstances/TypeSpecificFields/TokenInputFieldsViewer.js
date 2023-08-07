import React from 'react';
import {useState, useRef} from 'react';

// material-ui

import Grid from '@material-ui/core/Grid';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Checkbox from '@material-ui/core/Checkbox';

// dataHub
import dataHub from 'ajax/DataHub';
import DataLoader from 'main/DataLoader/DataLoader';

//local
import {FolderItemTypes} from 'main/FolderContent/FolderItemTypes';
import {convertTokenInputFilterToLocalData} from "./converters";
import ViewerTextField from "main/Main/Development/Viewer/ViewerTextField";
import ViewerChildCard from "main/Main/Development/Viewer/ViewerChildCard";

/**
 * Компонент для просмотра полей шаблона фильтра TOKEN_INPUT
 * @param {Object} props - свойства компонента
 * @param {Object} props.filterInstanceData - объект filterInstance
 */
export default function TokenInputFieldsViewer(props) {

    let {localData} = convertTokenInputFilterToLocalData(props.filterInstanceData);

    const [datasetData, setDatasetData] = useState({});
    const datasetFieldsNameMap = useRef(new Map());

    const loadFuncDataset = dataHub.datasetController.get;
    const loadParamsDataset = [props.filterInstanceData.dataSetId];

    function buildDatasetFieldsNameMap(datasetData) {
        datasetFieldsNameMap.current = new Map();
        for (let f of datasetData.fields) {
            datasetFieldsNameMap.current.set(f.id, f.name);
        }
    }

    function handleDatasetLoaded(datasetData) {
        buildDatasetFieldsNameMap(datasetData);
        setDatasetData(datasetData);
    }

    function handleDataLoadFailed(message) {

    }

    let fieldsGrid = []
    for (let i = 0; i < localData.datasetFields.length; i++){
        let f = localData.datasetFields[i]

        fieldsGrid.push(
            <Grid container key = {i}>
                <Grid item xs={3} style={{paddingRight: '16px'}}>
                    <ViewerTextField
                        label={"Название поля " + f.type}
                        value={f.name}
                    />
                </Grid>
                <Grid item xs={3} style={{paddingRight: '16px'}}>
                    <ViewerTextField
                        label={"Описание поля " + f.type}
                        value={f.description}
                    />
                </Grid>
                <Grid item xs={4}>
                    <ViewerTextField
                        label="Поле ID набора данных"
                        value={datasetFieldsNameMap.current.get(f.id)}
                    />
                </Grid>
                <Grid item xs={2}>
                    {   f.type === "NAME_FIELD" &&
                        <div style={{marginLeft: '16px'}}>
                            <FormControlLabel
                                control={
                                    <Checkbox 
                                        disabled 
                                        size = "small"
                                        checked = {f.showField} 
                                        name="show" 
                                    />
                                }
                                label="Показывать"
                            />
                            <FormControlLabel
                                control={
                                    <Checkbox
                                        disabled 
                                        size = "small"
                                        checked = {f.searchByField}
                                        name="search" 
                                    />
                                }
                                label="Искать по полю"
                            />
                        </div>
                    }
                </Grid>
            </Grid>
        )
    }
    return (
        <div>
            <DataLoader
                loadFunc={loadFuncDataset}
                loadParams={loadParamsDataset}
                onDataLoaded={handleDatasetLoaded}
                onDataLoadFailed={handleDataLoadFailed}
                disabledScroll={true}
            >
                {
                    <ViewerChildCard
                        id={localData.dataSetId}
                        parentFolderId={datasetData.folderId}
                        name = {datasetData && datasetData.name ?  datasetData.schemaName + '.' + datasetData.name + ' (' + datasetData.dataSource.name  +')' : null}
                        itemType={FolderItemTypes.dataset}
                    />
                }
                {datasetData &&
                <div>
                    {fieldsGrid}
                </div>
                }
            </DataLoader>
        </div>
    );
}
