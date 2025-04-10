import React, { useState, useEffect } from 'react';
import { useSnackbar } from 'notistack';

import { useNavigate, useParams, useLocation } from 'react-router-dom';

import { useDispatch } from "react-redux";
import { editItemNavbar, addItemNavbar } from "redux/actions/navbar/actionNavbar";

import Tooltip from '@material-ui/core/Tooltip';
import UpdateIcon from '@material-ui/icons/Update';
import IconButton from '@material-ui/core/IconButton';

// local
import DesignerPage from '../Designer/DesignerPage';
import PageTabs from 'components/PageTabs/PageTabs';
import DesignerTextField from '../Designer/DesignerTextField';
import DesignerSelectField from '../Designer/DesignerSelectField';
import DesignerFolderItemPicker from '../Designer/DesignerFolderItemPicker';
import {FolderItemTypes} from 'main/FolderContent/FolderItemTypes';
import StyleConsts from '../../../../StyleConsts';

// dataHub
import dataHub from 'ajax/DataHub';
import DataLoader from 'main/DataLoader/DataLoader';
import { CircularProgress } from '@material-ui/core';

import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';

// styles
import { DatasetDesignerCSS } from '../Designer/DesignerCSS'


export default function DatasetDesigner(){

    const { id, folderId } = useParams()
    const navigate = useNavigate();
    const location = useLocation();

    const dispatch = useDispatch()

    useEffect(() => {
        if(!id) {
            dataHub.datasetController.getFolder(folderId, handleFoldersLoaded)
        }
    }, []) // eslint-disable-line

    const { enqueueSnackbar } = useSnackbar();

    const classes = DatasetDesignerCSS();

    const [data, setData] = useState({
        datasetName : null,
        datasetDescription : null,
        datasourceId : null,
        datasourceName : null,
        catalogName : "",
        schemaName : null,
        objectName : null,
        datasetTypeId : null
    });

    const [typesData, setTypesData] = useState([]);

    const fieldLabels = {
        datasetName : "Название набора данных",
        datasetDescription : "Описание",
        datasourceId : "Источник данных",
        catalogName : "Каталог (допускается не заполнять)",
        schemaName : "Схема",
        objectName : "Объект",
        datasetTypeId : "Тип объекта"        
    }

    const [pageName, setPagename] = useState(id ? "Редактирование набора данных" : "Создание набора данных");

    const [uploading, setUploading] = useState(false);
    const [errorField, setErrorField] = useState({});

    const [fldRefresh, setFldRefresh] = useState(false);

    let loadFunc;
    let loadParams = [];
    

    if (id) {
        loadFunc = dataHub.datasetController.get;
        loadParams = [id];
    }

    /*
        Data loading
    */

    function handleFoldersLoaded({ok, data}) {
        if(ok) {
            dispatch(addItemNavbar('dataset', folderId, data.path))
        }
    }

    function handleDataLoaded(loadedData){
        setData({
            ...data,
            datasetName : loadedData.name,
            datasetDescription : loadedData.description,
            datasetFields : loadedData.fields,
            datasourceId : loadedData.dataSource.id,
            datasourceName : loadedData.dataSource.name,
            catalogName : loadedData.catalogName,
            schemaName : loadedData.schemaName,
            objectName : loadedData.objectName,
            datasetTypeId : loadedData.typeId
        });
        setPagename("Редактирование набора данных: " + loadedData.name);

        if (id) {
            dispatch(editItemNavbar('dataset', loadedData.name, id, folderId, loadedData.path))
        }
    }

    function handleTypesLoaded(data){
        setTypesData(data.map((v) => ({id: v.id, name: v.name, description: v.description})));
    }

    function handleDataLoadFailed(message){
        
    }    

    /* 
        Data editing 
    */

    function handleChangeName(newName){
        setData({
            ...data,
            datasetName : newName
        });
        setErrorField({
            ...errorField,
            datasetName : false
        });
    }    

    function handleChangeDescription(newDescription){
        setData({
            ...data,
            datasetDescription : newDescription
        });
        setErrorField({
            ...errorField,
            datasetDescription : false
        });        
    }

    function handleChangeDatasource(newDatasourceId, newDatasourceData, newDatasourceFolderId){
        setData({
            ...data,
            datasourceId : newDatasourceId,
            datasourceName : newDatasourceData.name
        });
        setErrorField({
            ...errorField,
            datasourceId : false
        });
    }     

    function handleChangeCatalogName(newCatalogName){
        setData({
            ...data,
            catalogName : newCatalogName
        });
        setErrorField({
            ...errorField,
            catalogName : false
        });
    }

    function handleChangeSchemaName(newSchemaName){
        setData({
            ...data,
            schemaName : newSchemaName
        });
        setErrorField({
            ...errorField,
            schemaName : false
        });
    }

    function handleChangeObjectName(newObjectName){
        setData({
            ...data,
            objectName : newObjectName
        });
        setErrorField({
            ...errorField,
            objectName : false
        });
    }   

    function handleChangeTypeId(newTypeId){
        setData({
            ...data,
            datasetTypeId : newTypeId
        });
        setErrorField({
            ...errorField,
            datasetTypeId : false
        });
    }

    function handleFldRefresh(){
        setFldRefresh(true);
    }

    function handleFldLoaded(loadedData){
        setFldRefresh(false)
        setData({
            ...data,
            datasetFields : loadedData.fields
        });
    }

    function handleTabChange(tabId){
    }

    /*
        Save and cancel
    */

    function handleSave(){
        
        let errors = {};
        let errorExists = false;
        
        // Проверка корректности заполнения полей
        Object.entries(data)
            .filter( ([fieldName, fieldValue]) => 
                ( 
                    fieldName !== "catalogName" && fieldName !== "datasourceName" && 
                    (fieldValue === null || (typeof fieldValue === "string" && fieldValue.trim() === "") ) 
                ) )
            .reverse()
            .forEach( ([fieldName, fieldValue]) => 
                {
                    errors[fieldName] = true;
                    enqueueSnackbar("Недопустимо пустое значение в поле " + fieldLabels[fieldName], {variant : "error"});
                    errorExists = true;
                } );
        
        if (errorExists) {
            setErrorField(errors);
        }
        else {
            if (!id) {
                dataHub.datasetController.add(
                    Number(folderId), 
                    data.datasetName.trim(), 
                    data.datasetDescription.trim(),
                    data.datasourceId,
                    data.catalogName.trim(),
                    data.schemaName.trim(),
                    data.objectName.trim(),
                    data.datasetTypeId,
                    handleAddEditAnswer);
            }
            else {
                dataHub.datasetController.edit(
                    Number(folderId),
                    Number(id),
                    data.datasetName.trim(), 
                    data.datasetDescription.trim(),
                    data.datasourceId,
                    data.catalogName.trim(),
                    data.schemaName.trim(),
                    data.objectName.trim(),
                    data.datasetTypeId,
                    data.datasetFields,
                    handleAddEditAnswer);
            }
            setUploading(true);
        }
    }

    function handleAddEditAnswer(magrepResponse){
        
        if(magrepResponse.ok){
            location.state ? navigate(location.state) : navigate(`/ui/dataset/${folderId}`)
            enqueueSnackbar("Набор данных успешно сохранен", {variant : "success"});
        }
        else{
            setUploading(false);

            let actionWord = id ? "обновлении" : "создании";
            enqueueSnackbar("Ошибка при " + actionWord + " объекта: " + magrepResponse.data, {variant : "error"});
        }
    }

    let tabs = []

    tabs.push({tablabel:"Настройки",
        tabcontent:
        <DesignerPage 
            onSaveClick={handleSave}
            onCancelClick={() => location.state ? navigate(location.state) : navigate(`/ui/dataset/${folderId}`)}
            //name = {pagename}
        >
            <DesignerTextField
                id = "datasetName"
                minWidth = {StyleConsts.designerTextFieldMinWidth}
                label = {fieldLabels.datasetName}
                value = {data.datasetName}
                onChange = {handleChangeName}
                displayBlock
                fullWidth
                error = {errorField.datasetName}
            />

            <DesignerTextField
                id = "datasetDescription"
                minWidth = {StyleConsts.designerTextFieldMinWidth}
                label = {fieldLabels.datasetDescription}
                value = {data.datasetDescription}
                onChange = {handleChangeDescription}
                displayBlock
                fullWidth
                error = {errorField.datasetDescription}
            />
            <DesignerFolderItemPicker
                minWidth = {StyleConsts.designerTextFieldMinWidth}
                label = {fieldLabels.datasourceId}
                value = {(data.datasourceId && data.datasourceName) ? 'id: ' + data.datasourceId + ', ' + data.datasourceName : null}
                itemType = {FolderItemTypes.datasource}
                onChange = {handleChangeDatasource}
                displayBlock
                fullWidth
                error = {errorField.datasourceId}
            />

            <DesignerTextField
                id = "catalogName"
                minWidth = {StyleConsts.designerTextFieldMinWidth}
                label = {fieldLabels.catalogName}
                value = {data.catalogName}
                onChange = {handleChangeCatalogName}
                displayBlock
                fullWidth
                error = {errorField.catalogName}
            />

            <DesignerTextField
                id = "schemeName"
                minWidth = {StyleConsts.designerTextFieldMinWidth}
                label = {fieldLabels.schemaName}
                value = {data.schemaName}
                onChange = {handleChangeSchemaName}
                displayBlock
                fullWidth
                error = {errorField.schemaName}
            />

            <DesignerTextField
                id = "objectName"
                minWidth = {StyleConsts.designerTextFieldMinWidth}
                label = {fieldLabels.objectName}
                value = {data.objectName}
                onChange = {handleChangeObjectName}
                displayBlock
                fullWidth
                error = {errorField.objectName}
            />

            <DesignerSelectField
                id = "idSelectInputLabel"
                minWidth = {StyleConsts.designerTextFieldMinWidth}
                label = {fieldLabels.datasetTypeId}
                data = {typesData}
                value = {data.datasetTypeId}
                onChange = {handleChangeTypeId}
                displayBlock
                fullWidth
                error = {errorField.datasetTypeId}
            />    
        </DesignerPage>
    })

    if(data.datasetFields) {
        tabs.push({tablabel:"Поля",
            tabcontent:
            <div style={{display: 'flex', flex:1, flexDirection: 'column'}}>
                <div className={classes.updateBtn}>
                    <Tooltip title="Обновить поля">
                        <IconButton onClick={handleFldRefresh}
                            size="small"
                        >
                            <UpdateIcon/>
                        </IconButton>
                    </Tooltip>
                </div>
            <div className={classes.verticalScroll}>
            
            <DataLoader
                loadFunc = {fldRefresh ? dataHub.datasetController.refresh : null}
                loadParams = {fldRefresh ? [Number(id)] : []}
                reload = {false}
                onDataLoaded = {handleFldLoaded}
                onDataLoadFailed = {null}
            >
                <div className={classes.tableContainer}>
                <Table size="small" stickyHeader  aria-label="simple table" className={classes.table}>
                    <TableHead>
                        <TableRow>
                            <TableCell align="center" className={classes.tableCellHead}>ID поля</TableCell>
                            <TableCell align="center" className={classes.tableCellHead}>Название поля</TableCell>
                            <TableCell align="center" className={classes.tableCellHead}>Тип поля</TableCell>
                            <TableCell align="center" className={classes.tableCellHead}>Описание</TableCell>
                            {/* <TableCell align="right">Protein&nbsp;(g)</TableCell> */}
                        </TableRow>
                    </TableHead>
                    <TableBody>
                    {data.datasetFields.map(field => (
                        <TableRow key={field.id} className={field.isValid === false ? classes.invalidRow : null}>
                            <TableCell align="center">{field.id}</TableCell>
                            <TableCell align="center">{field.name}</TableCell>
                            <TableCell align="center">{field.typeName}</TableCell>
                            <TableCell align="center">{field.description}</TableCell>
                        </TableRow>
                    ))}
                    </TableBody>
                </Table>
                </div>
            </DataLoader>
            </div>
                
            </div>
        })
    }

    
    return(
        <DataLoader
            loadFunc = {loadFunc}
            loadParams = {loadParams}
            onDataLoaded = {handleDataLoaded}
            onDataLoadFailed = {handleDataLoadFailed}
        >
            <DataLoader
                loadFunc = {dataHub.datasetController.getTypes}
                loadParams = {[]}
                onDataLoaded = {handleTypesLoaded}
                onDataLoadFailed = {handleDataLoadFailed}
            >
                {uploading ? <CircularProgress/> :

                <PageTabs
                    tabsdata={tabs} 
                    onTabChange={handleTabChange}
                    pageName={pageName}
                />
                }
            </DataLoader>

        </DataLoader>
    );    

}

