import React, { useState, useEffect } from 'react';
import { useSnackbar } from 'notistack';

import { useNavigate, useParams, useLocation } from 'react-router-dom';

import { useDispatch } from "react-redux";
import { editItemNavbar, addItemNavbar } from "redux/actions/navbar/actionNavbar";

// material ui
import { CircularProgress } from '@material-ui/core';

// local
import DesignerPage from '../Designer/DesignerPage';
import DesignerTextField from '../Designer/DesignerTextField';
import DesignerFolderItemPicker from '../Designer/DesignerFolderItemPicker';
import {FolderItemTypes} from 'main/FolderContent/FolderItemTypes';
import StyleConsts from '../../../../StyleConsts';

// dataHub
import dataHub from 'ajax/DataHub';
import DataLoader from 'main/DataLoader/DataLoader';

import UnboundedValueFields from './TypeSpecificFields/UnboundedValueFields';
import ValueListFields from './TypeSpecificFields/ValueListFields';
import HierTreeFields from './TypeSpecificFields/HierTreeFields';
import TokenInputFields from './TypeSpecificFields/TokenInputFields';
import RangeFields from './TypeSpecificFields/RangeFields';
import DateValueFields from './TypeSpecificFields/DateValueFields';
import TupleListFields from './TypeSpecificFields/TupleListFields';


export default function FilterInstanceDesigner(){

    const { id, folderId } = useParams()
    const navigate = useNavigate();
    const location = useLocation();

    const dispatch = useDispatch()

    useEffect(() => {
        if(!id) {
            dataHub.filterInstanceController.getFolder(folderId, handleFoldersLoaded)
        }
    }, []) // eslint-disable-line

    const { enqueueSnackbar } = useSnackbar();

    const [pageName, setPagename] = useState(id ? "Редактирование экземпляра фильтра" : "Создание экземпляра фильтра");

    const [uploading, setUploading] = useState(false);
    const [filterInstanceData, setFilterInstanceData] = useState({
        id: id,
        folderId: folderId,
        templateId: 0,
        name: '',
        code: "",
        description: '',
        fields : []
    });
    const [filterTemplateData, setFilterTemplateData] = useState({
        id : 0,
        name : '',
        type : {
            name : ''
        }
    });

    const [errorFields, setErrorFields] = useState({});

    const fieldLabels = {
        name : "Название",
        code : "Код",
        description : "Описание",
        templateId : "Шаблон фильтра" ,
        fields: "Поля датасета",
    }    

    let loadFunc;
    let loadFuncFilterTemplate;
    let loadParams = [];
    
    if(id){
        loadFunc = dataHub.filterInstanceController.get;
        loadFuncFilterTemplate = dataHub.filterTemplateController.get;
        loadParams = [id];
    }

    function handleFoldersLoaded({ok, data}) {
        if(ok) {
            dispatch(addItemNavbar('filterInstance', folderId, data.path))
        }
    }

    function handleFilterInstanceDataLoaded(filterInstanceData){
        setFilterInstanceData(filterInstanceData);
        setErrorFields({});
        setPagename("Редактирование экземпляра фильтра: " + filterInstanceData.name);
        if (id) {
            dispatch(editItemNavbar('filterInstance', filterInstanceData.name, id, folderId, filterInstanceData.path))
        }
    }

    function handleDataLoadFailed(message){
        enqueueSnackbar("Ошибка загрузки данных: " + message, {variant : "error"});
    }

    function handleFilterTemplateLoaded(filterTemplateData){
        setFilterTemplateData(filterTemplateData);
        
    }

    function handleChangeFilterTemplate(filterTemplateId, filterTemplateData){
        if(filterTemplateId !== filterInstanceData.templateId){
            setFilterTemplateData(filterTemplateData);
            setFilterInstanceData({
                id: id,
                folderId: folderId,
                templateId: filterTemplateId,
                type: filterTemplateData.type,
                name: filterInstanceData.name,
                code: filterInstanceData.code,
                description: filterInstanceData.description,
                fields: []
            });

            setErrorFields({
                name: errorFields.filterInstanceName,
                code: errorFields.filterInstanceCode,
                description: errorFields.filterInstanceDescription,
                templateId : undefined,
                fields : "Не заполнены все необходимые поля фильтра"
            });  
        }
    }

    /*
        Data editing
    */

    function handleChangeField(field, value){
        setFilterInstanceData({
            ...filterInstanceData,
            [field] : value
        });

        setErrorFields({
            ...errorFields,
            [field]: (value === '') ? "Поле " + fieldLabels[field] + " не может быть пустым" : undefined
        });
    }

    function handleChangeUnboundedValueFields(newFields, errors){
        setFilterInstanceData({
            ...filterInstanceData,
            fields : newFields});
        setErrorFields({
            name: errorFields.name,
            code: errorFields.code,
            description: errorFields.description,
            templateId : errorFields.templateId,
            ...errors
        });
    }

    function handleChangeSpecificField(newFilterInstanceData, errors){
        setFilterInstanceData(newFilterInstanceData);
        setErrorFields({
            ...errorFields,
            ...errors
        });
    }

    /*
        Save and cancel
    */

    function handleSave(){

        let errors = errorFields;
        errors.fields = undefined;
        let errorExists = false;

        // Проверка корректности заполнения полей
        Object.entries(filterInstanceData)
            .filter( ([fieldName, fieldValue]) => 
                (   fieldName !== "id" && 
                    (
                        (fieldValue === null && filterInstanceData.type.name !== 'DATE_RANGE' 
                                            && filterInstanceData.type.name !== 'RANGE'
                                            && filterInstanceData.type.name !== 'DATE_VALUE'
                                            && filterInstanceData.type.name !== 'SINGLE_VALUE_UNBOUNDED'
                                            && filterInstanceData.type.name !== 'VALUE_LIST_UNBOUNDED'
                                            && filterInstanceData.type.name !== 'TUPLE_LIST') || 
                        (typeof fieldValue === "string" && fieldValue.trim() === "") ||
                        (fieldName === "templateId" && fieldValue === 0) || 
                        (fieldName === "fields" && fieldValue.length === 0) || 
                        (fieldName === "fields" && fieldValue.filter(obj => !obj.name || !obj.description || 
                            (!obj.dataSetFieldId    && filterInstanceData.type.name !== 'SINGLE_VALUE_UNBOUNDED'
                                                    && filterInstanceData.type.name !== 'RANGE'
                                                    && filterInstanceData.type.name !== 'VALUE_LIST_UNBOUNDED'
                                                    && filterInstanceData.type.name !== 'DATE_RANGE' 
                                                    && filterInstanceData.type.name !== 'RANGE'
                                                    && filterInstanceData.type.name !== 'DATE_VALUE'
                                                    && filterInstanceData.type.name !== 'TUPLE_LIST') ).length > 0)
                    ) 
                ))
            .reverse()
            .forEach( ([fieldName, fieldValue]) => 
                {
                    errors[fieldName] = "Недопустимо пустое значение в поле " + fieldLabels[fieldName] ;
                } );

        for (let key in errors){
            let val = errors[key]
            if (val !== undefined || val === true ){
                errorExists = true
                enqueueSnackbar(val, {variant : "error"});
            }
        }

        if (errorExists){
            setErrorFields(errors)
        } else {
            let func = id ? dataHub.filterInstanceController.edit : dataHub.filterInstanceController.add;
            setUploading(true);
            func(filterInstanceData, handleAddEditResult);
        }
    }

    function handleAddEditResult(magrepResponse){
        if(magrepResponse.ok){
            location.state ? navigate(location.state) : navigate(`/ui/filterInstance/${folderId}`)
            enqueueSnackbar("Экземпляр фильтра успешно сохранен", {variant : "success"});
        }
        else{
            setUploading(false);
            let word = id ? 'редактирования' : 'создания';
            enqueueSnackbar("Ошибка " + word + " экземпляра фильтра: " + magrepResponse.data, {variant : "error"});
        }
    }

    return(
        <DataLoader
            loadFunc = {loadFunc}
            loadParams = {loadParams}
            onDataLoaded = {handleFilterInstanceDataLoaded}
            onDataLoadFailed = {handleDataLoadFailed}
        >
            <DataLoader
                loadFunc = {loadFuncFilterTemplate}
                loadParams = {[filterInstanceData.templateId]}
                onDataLoaded = {handleFilterTemplateLoaded}
                onDataLoadFailed = {handleDataLoadFailed}
            >
                {uploading ? <CircularProgress/> :

                    <DesignerPage 
                        onSaveClick={handleSave}
                        onCancelClick={() => location.state ? navigate(location.state) : navigate(`/ui/filterInstance/${folderId}`)}
                        name = {pageName}
                    >
                        <DesignerTextField
                            minWidth = {StyleConsts.designerTextFieldMinWidth}
                            label = {fieldLabels.name}
                            value = {filterInstanceData.name}
                            onChange = {data => {handleChangeField('name', data)}}
                            //displayBlock
                            fullWidth
                            error = {Boolean(errorFields.name)}
                        />

                        <DesignerTextField
                            minWidth = {StyleConsts.designerTextFieldMinWidth}
                            label = {fieldLabels.code}
                            value = {filterInstanceData.code}
                            onChange = {data => {handleChangeField('code', data)}}
                            //displayBlock
                            fullWidth
                            error = {Boolean(errorFields.code)}
                        />

                        <DesignerTextField
                            minWidth = {StyleConsts.designerTextFieldMinWidth}
                            label = {fieldLabels.description}
                            value = {filterInstanceData.description}
                            onChange = {data => handleChangeField('description', data)}
                           // displayBlock
                            fullWidth
                            error = {Boolean(errorFields.description)}
                        />

                        <DesignerFolderItemPicker
                            minWidth = {StyleConsts.designerTextFieldMinWidth}
                            label = {fieldLabels.templateId}
                            value = {filterTemplateData.name}
                            itemType = {FolderItemTypes.filterTemplate}
                            onChange = {handleChangeFilterTemplate}
                           // displayBlock
                            fullWidth
                            error = {Boolean(errorFields.templateId)}
                        />
                        {
                            filterTemplateData.type.name === 'SINGLE_VALUE_UNBOUNDED' || filterTemplateData.type.name === 'VALUE_LIST_UNBOUNDED'?
                                <UnboundedValueFields
                                    fields = {filterInstanceData.fields}
                                    onChange = {handleChangeUnboundedValueFields}
                                />
                            :
                            filterTemplateData.type.name === 'TUPLE_LIST'?
                                <TupleListFields
                                    fields = {filterInstanceData.fields}
                                    onChange = {handleChangeUnboundedValueFields}
                                />
                            :
                            filterTemplateData.type.name === 'VALUE_LIST' ?
                                <ValueListFields
                                    filterInstanceData = {filterInstanceData}
                                    onChange = {handleChangeSpecificField}
                                />
                            :
                            filterTemplateData.type.name === 'HIERARCHY' || filterTemplateData.type.name === 'HIERARCHY_M2M'?
                                <HierTreeFields
                                    filterTemplateType = {filterTemplateData.type.name}
                                    filterInstanceData = {filterInstanceData}
                                    onChange = {handleChangeSpecificField}
                                />
                            :
                            filterTemplateData.type.name === 'DATE_RANGE' || filterTemplateData.type.name === 'RANGE'?
                                <RangeFields
                                    filterTemplateType = {filterTemplateData.type.name}
                                    filterInstanceData = {filterInstanceData}
                                    onChange = {handleChangeSpecificField}
                                />
                            :
                            filterTemplateData.type.name === 'DATE_VALUE'?
                                <DateValueFields
                                    filterInstanceData = {filterInstanceData}
                                    onChange = {handleChangeSpecificField}
                                />
                            :
                            filterTemplateData.type.name === 'TOKEN_INPUT'?
                                <TokenInputFields
                                    filterInstanceData = {filterInstanceData}
                                    onChange = {handleChangeSpecificField}
                                />
                            :                            
                                <div></div>
                        }
                    </DesignerPage>
                }
            </DataLoader>
        </DataLoader>
    );
}
