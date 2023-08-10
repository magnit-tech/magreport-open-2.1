import React from 'react';
import {useState, useRef} from 'react';

// material-ui

import Grid from '@material-ui/core/Grid';
import AddCircleOutlineIcon from '@material-ui/icons/AddCircleOutline';
import DeleteIcon from '@material-ui/icons/Delete';
import IconButton from '@material-ui/core/IconButton';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Checkbox from '@material-ui/core/Checkbox';

// dataHub
import dataHub from 'ajax/DataHub';
import DataLoader from 'main/DataLoader/DataLoader';

//local
import {FolderItemTypes} from 'main/FolderContent/FolderItemTypes';
import DesignerFolderItemPicker from '../../Designer/DesignerFolderItemPicker'
import DesignerSelectField from '../../Designer/DesignerSelectField';
import DesignerTextField from '../../Designer/DesignerTextField';
import {addTokenInputNewNameField, convertTokenInputLocalToFilterData, convertTokenInputFilterToLocalData} from "./converters";

/**
 * @callback onChange
 * @param {Object} filterInstance - Экземпляр фильтра
 * @param {boolean} errorExists - флаг наличия ошибок заполнения
 */
/**
 * Компонент для редактирования полей шаблона фильтра TOKEN_INPUT
 * @param {Object} props - свойства компонента
 * @param {Object} props.filterInstanceData - объект filterInstance
 * @param {onChange} props.onChange - callback для изменения значений полей
 */
export default function TokenInputFields(props){

    let {localData, errorFields} = convertTokenInputFilterToLocalData(props.filterInstanceData);

    const [datasetData, setDatasetData] = useState(null);
    const datasetFieldsNameMap = useRef(new Map());

    let loadFuncDataset;

    if(props.filterInstanceData.dataSetId){
        loadFuncDataset = dataHub.datasetController.get;
    }

    function buildDatasetFieldsNameMap(datasetData){
        datasetFieldsNameMap.current = new Map();
        for(let f of datasetData.fields){
            datasetFieldsNameMap.current.set(f.id, f.name);
        }
    }

    function handleDatasetLoaded(datasetData){
        buildDatasetFieldsNameMap(datasetData);
        setDatasetData(datasetData);
    }

    function handleChangeDataset(id, datasetData, folderId){
        buildDatasetFieldsNameMap(datasetData);
        setDatasetData(datasetData);
        localData = {
            ...localData,
            dataSetId : id
        }
        handleChangeData();
    }

    function handleChangeField(i, fieldName, value){
        let parts = fieldName.split('.');
        if(parts[1] === 'id'){
            let oldDatasetFieldName = datasetFieldsNameMap.current.get(localData.datasetFields[i][parts[1]]);
            let newDatasetFieldName = datasetFieldsNameMap.current.get(value);
            let currentFieldName = localData.datasetFields[i].name;
            let currentFieldDescription = localData.datasetFields[i].description;
            if(currentFieldName.trim() === '' || currentFieldName === oldDatasetFieldName){
                localData.datasetFields[i].name = newDatasetFieldName;
            }
            if(currentFieldDescription.trim() === '' || currentFieldDescription === oldDatasetFieldName){
                localData.datasetFields[i].description = newDatasetFieldName;
            }            
        }
        localData.datasetFields[i][parts[1]] = value;
      
        handleChangeData();
    }

    function handleChangeData(){
        let {filterInstanceData, errors} = convertTokenInputLocalToFilterData(props.filterInstanceData, localData);
        props.onChange(filterInstanceData, errors);
    }

    function handleDataLoadFailed(message){

    }

    function handleAddNameField(){
        let {dtsFlds, errFlds} = addTokenInputNewNameField(localData.datasetFields, errorFields)
        localData.datasetFields = dtsFlds
        errorFields = errFlds
        handleChangeData()
    }

    function handleDelNameField(i)
    {
        let dtsFlds = localData.datasetFields.filter((item, index) => index !== i)
        let errFlds = errorFields.filter((item, index) => index !== i)
        localData.datasetFields = dtsFlds
        errorFields = errFlds
        handleChangeData()
    }

    let fieldsGrid = []
    for (let i = 0; i < localData.datasetFields.length; i++){
        let f = localData.datasetFields[i]
        fieldsGrid.push(
            <Grid container key = {i}>
                <Grid item xs={3} style={{paddingRight: '16px'}}>
                    <DesignerTextField
                        key = { f.type + i}
                        label = {"Название поля " + f.type}
                        value = {f.name}
                        fullWidth
                        displayBlock
                        onChange = {(value) => {handleChangeField(i, f.type + '.name',value)}}
                        error = {errorFields.find(i=>i.type === f.type).name}
                    />
                </Grid>

                <Grid item xs={3} style={{paddingRight: '16px'}}>
                    <DesignerTextField
                        label = {"Описание поля "+ f.type}
                        value = {f.description}
                        fullWidth
                        displayBlock
                        onChange = {(value) => {handleChangeField(i, f.type + '.description',value)}}
                        error = {errorFields.find(i=>i.type === f.type).description}
                    />
                </Grid>                                          
            
                <Grid item xs={3}>
                    <DesignerSelectField
                        label = "Поле ID набора данных"
                        data = {datasetData ? datasetData.fields : []}
                        value = {f.id}
                        fullWidth
                        displayBlock
                        onChange = {(value) => {handleChangeField(i, f.type + '.id',value)}}
                        error = {errorFields.find(i=>i.type === f.type).id}
                    />
                </Grid>
                
                <Grid item xs={2}>
                    {   f.type === "NAME_FIELD" &&
                        <div style={{marginLeft: '16px'}}>
                            <FormControlLabel
                                control={
                                    <Checkbox 
                                        key = {"showField" + i}
                                        size = "small"
                                        checked = {f.showField}
                                        onChange={(e)=>handleChangeField(i, f.type + ".showField", e.target.checked)} 
                                        name="show" 
                                    />
                                }
                                label="Показывать"
                            />

                            <FormControlLabel
                                control={
                                    <Checkbox
                                        key = {"searchByField" + i}
                                        size = "small"
                                        checked = {f.searchByField}
                                        onChange={(e)=>handleChangeField(i, f.type + ".searchByField", e.target.checked)} 
                                        name="search" 
                                    />
                                }
                                label="Искать по полю"
                            />
                        </div>
                    }
                </Grid>
                <Grid item xs={1}>
                    { i>1 &&
                        <IconButton
                           // size = "small"
                            aria-label="delete"
                            color="primary"
                            onClick={() => handleDelNameField(i)}
                        >
                            <DeleteIcon />
                        </IconButton>
                    }
                </Grid>
            </Grid>
        )
    }

    return(
        <div>

            <DataLoader
                loadFunc = {loadFuncDataset}
                loadParams = {[props.filterInstanceData.dataSetId]}
                onDataLoaded = {handleDatasetLoaded}
                onDataLoadFailed = {handleDataLoadFailed}
                disabledScroll = {true}
            >
                <DesignerFolderItemPicker
                    label = "Набор данных"
                    value = {datasetData && datasetData.name ? 'id: ' + datasetData.id +', ' + datasetData.schemaName + '.' + datasetData.name + ' (' + datasetData.dataSource.name  +')' : null}
                    itemType = {FolderItemTypes.dataset}
                    onChange = {handleChangeDataset}
                    displayBlock
                    fullWidth
                    error = {errorFields.dataSetId}
                />

                {datasetData && 
                    <div>
                        {
                            fieldsGrid
                        }
                        <IconButton
                            aria-label="add"
                            color="primary"
                            onClick={handleAddNameField}
                        >
                            <AddCircleOutlineIcon fontSize='large'/>
                        </IconButton>
                    </div>
                    
                }             

            </DataLoader>

        </div>
    )
}