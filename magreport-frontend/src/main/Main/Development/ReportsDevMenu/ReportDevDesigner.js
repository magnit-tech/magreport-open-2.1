import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { useSnackbar } from 'notistack';

import { useNavigate, useParams, useLocation } from 'react-router-dom';

// components
import Backdrop from '@material-ui/core/Backdrop';
import { CircularProgress } from '@material-ui/core';

// local
import DesignerPage from '../Designer/DesignerPage';
import DesignerTextField from '../Designer/DesignerTextField';
import DesignerFolderItemPicker from '../Designer/DesignerFolderItemPicker'
import ReportFields from './ReportFields'
import ReportFiltersTab from './ReportFilters/ReportFiltersTab'
import PageTabs from 'components/PageTabs/PageTabs';
import ReportTemplates from './ReportTemplates'
import DataLoader from 'main/DataLoader/DataLoader';
import {FolderItemTypes} from 'main/FolderContent/FolderItemTypes';
import StyleConsts from '../../../../StyleConsts';

// dataHub
import dataHub from 'ajax/DataHub';

// functions
import { setRandomCodeForEmptyCode, getFilters, checkChanges, sortByOrdinal } from "./reportFunctions";

// actions
import {actionFiltersLoaded, actionFiltersAdd, actionFiltersGroupAdd, actionFiltersDelete, 
    actionFiltersGroupChange, actionFiltersChange, actionFiltersChangeOrdinal, actionMoveBefore, actionMoveInto
} from 'redux/actions/developer/actionReportFilters'
import {actionLoaded, actionLoadedFailed} from 'redux/actions/developer/actionReportTemplates'
import { editItemNavbar, addItemNavbar } from "redux/actions/navbar/actionNavbar";

// styles
import {DesignerCSS} from '../../Development/Designer/DesignerCSS';

// utils
import ReorderList from 'utils/ReorderList'

/**
 * @callback onExit
 */

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
 * Компонент для создания и редактирования отчета
 * @param {Object } props - параметры компонента
 * @param {Object} props.reportFiltersGroup - корневая группа фильтров отчета
 * @param {actionLoaded} props.actionLoaded - action, вызываемый при загрузке данных отчета
 * @param {actionLoadedFailed} props.actionLoadedFailed - action, вызываемый при ошибке загрузки данных отчета
 * @param {actionFiltersLoaded} props.actionFiltersLoaded - action, вызываемый при ошибке загрузки данных отчета
 * @param {actionFiltersAdd} props.actionFiltersAdd - action, вызываемый при добавлении фильтра в отчет
 * @param {actionFiltersChange} props.actionFiltersChange - action, вызываемый при изменении фильтра в отчете
 * @param {actionFiltersChangeOrdinal} props.actionFiltersChangeOrdinal - action, вызываемый при изменении позиции фильтра в отчете
 * @param {actionFiltersGroupAdd} props.actionFiltersGroupAdd - action, вызываемый при добавлении группы фильтров в отчет
 * @param {actionFiltersGroupChange} props.actionFiltersGroupChange - action, вызываемый при изменении группы фильтров в отчете
 * @param {actionFiltersDelete} props.actionFiltersDelete - action, вызываемый при удалении фильтра из отчета
 * @param {actionMoveBefore} props.actionMoveBefore - action, вызываемый при перемещении фильтра перед другим фильтром
 * @param {actionMoveInto} props.actionMoveInto - action, вызываемый при перемещении фильтра внутрь группы
 */
function ReportDevDesigner(props){

    const { id, folderId } = useParams()
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        if(!id) {
            dataHub.reportController.getFolder(folderId, handleFoldersLoaded)
        }
    }, []) // eslint-disable-line

    const { enqueueSnackbar } = useSnackbar();

    const classes = DesignerCSS();

    const [fieldValues, setFieldValues] = useState(null)

    const [data, setData] = useState({
        reportDataSetId: null,
        reportDescription: '',
        reportName: '',
        reportRequirementsLink: '',
        reportFields: [],
        reportId: id
    });

    const [selectedDataset, setSelectedDataset] = useState([]);
    
    const fieldLabels = {
        reportDataSetId : "Набор данных",
        reportDescription : "Описание отчета",
        reportName : "Название отчета",
        reportRequirementsLink : "Ссылка на реестр требований",
        reportFields : {
            dataSetFieldId: "Поле набора данных",
            name: "Название поля",
            description: "Описание поля",
            visible: "Отображаемое"
        },
    }

    const [pagename, setPagename] = useState(id ? "Редактирование отчета" : "Создание отчета");
    // const [loadedFilters, setLoadedFilters] = useState({})

    const [uploading, setUploading] = useState(false);
    const [errorField, setErrorField] = useState({});
    const [needSave, setNeedSave] = useState(id ? false : true);

    let loadFunc;
    let loadFuncDataSet;
    let loadParams = [];
    
    
    if(id){
        loadFunc = dataHub.reportController.get;
        loadParams = [id, undefined];
        loadFuncDataSet=dataHub.datasetController.get;
    }

    /*
        Data loading
    */

    function handleFoldersLoaded({ok, data}) {
        if(ok) {
            props.addItemNavbar('reportsDev', folderId, data.path)
        }
    }

    function handleDataLoaded(loadedData){

        setRandomCodeForEmptyCode(loadedData.filterGroup);
        
        setData({
            ...data,
            reportDataSetId : loadedData.dataSetId,
            reportDescription : loadedData.description,
            reportName : loadedData.name,
            reportRequirementsLink : loadedData.requirementsLink,
        });
        setFieldValues(sortByOrdinal(loadedData.fields))
        props.actionFiltersLoaded(loadedData, id)
        setPagename("Редактирование отчета: " + loadedData.name);

        if (id) {
            props.editItemNavbar('reportsDev', loadedData.name, id, folderId, loadedData.path)
        }
    }

    /*
        Field editing
    */

    function handleChange(key, value){
        setData({...data, [key]: value});
        setErrorField({...errorField, [key]: false});
    }

    function handleFieldMove(oldIndex, newIndex) {
        let newFieldValues = ReorderList(fieldValues, oldIndex, newIndex);
        setFieldValues(newFieldValues);
    }

    function handleFieldDrop(index){
        setFieldValues(fieldValues.filter((item, i) => i !== index));
    }    

    /*
        Save and cancel
    */

    function handleSave(callbackFunc){
        
        let errors = {};
        let errorExists = false;
        
        // Проверка корректности заполнения полей
        Object.entries(data)
            .filter( ([fieldName, fieldValue]) => 
                ( 
                    fieldName !== "reportId" && 
                    fieldName !== "reportRequirementsLink" &&
                    (fieldValue === null || (typeof fieldValue === "string" && fieldValue.trim() === "") ) 
                ))
            .reverse()
            .forEach( ([fieldName, fieldValue]) => 
                {
                    errors[fieldName] = true;
                    enqueueSnackbar("Недопустимо пустое значение в поле " + fieldLabels[fieldName], {variant : "error"});
                    errorExists = true;
                } );

        if(errorExists){
            setErrorField(errors);
        }
        else{
            setUploading(true);
            // if (folderId === null || folderId === undefined){
            //     const itemInfo = dataHub.localCache.getItemData(FolderItemTypes.reportsDev, id)
            //     if (itemInfo){
            //         folderId = itemInfo.folderId
            //     }
            // }
            if(!id && needSave){
                dataHub.reportController.add(
                    data.reportDataSetId,
                    data.reportDescription,
                    folderId, 
                    null, 
                    data.reportName,
                    data.reportRequirementsLink,
                    callbackFunc);
            }
            else{
                let reportFilters = props.reportFiltersGroup.get(0);
                let invalid = false;
                let groupsObj = null;
                if(reportFilters.childGroups.length > 0 || reportFilters.filters.length > 0){
                   let res = getFilters({...reportFilters}, props.reportFiltersGroup);
                   invalid = res.invalid;
                   groupsObj = res.groupsObj;
                }

                if(invalid){
                    setUploading(false);
                    enqueueSnackbar("Фильтры содержат некорректные значения", {variant : "error"});
                }
                else {
                    dataHub.reportController.edit(
                        data.reportId,
                        data.reportName,
                        data.reportDescription,
                        data.reportRequirementsLink,
                        folderId, 
                        fieldValues,
                        groupsObj,
                        callbackFunc);
                }
            }
        }
    }

    function handleAddEditAnswer(magrepResponse){
        setUploading(false);
        if(magrepResponse.ok){
            location.state ? navigate(location.state) : navigate(`/reportsDev/${folderId}`)
            enqueueSnackbar("Отчет успешно сохранен", {variant : "success"});
        }
        else{
            let actionWord = id ? "обновлении" : "создании";
            enqueueSnackbar("Ошибка при " + actionWord + " объекта: " + magrepResponse.data, {variant : "error"});
        }
    }

    function handleChangeDataset(id, datasetData, folderId){
        setData({...data, reportDataSetId: id})
        setErrorField({...errorField, reportDataSetId: false});
        setSelectedDataset(datasetData)
    }

    function handleChangeTabs(){
        if (needSave){  
            handleSave(handleSaveResponse)

            function handleSaveResponse(m){
                if (m.ok){
                    setData({...data,  reportId : m.data.id})
                    setFieldValues(sortByOrdinal(m.data.fields))
                    props.actionFiltersLoaded(m.data, m.data.id)
                    setNeedSave(false)
                }
                else {
                    enqueueSnackbar("Ошибка при создании объекта: " + m.data, {variant : "error"});
                }
                setUploading(false);
            }
        }
    }

    function handleFieldChange(index, field, value){
        const errorText = checkChanges(field, value)
        if (!errorText) {
            setFieldValues(fieldValues.map((f, i) => {
                if (i === index){
                    f[field] = value
                }
                return f
            }))
        }
        else {
            enqueueSnackbar(errorText, {variant : "error"});
        }
    }

    let tabs = []
    
    tabs.push({
        tablabel: "Заголовки",
        tabcontent:  uploading ? <CircularProgress /> :
            <DesignerPage 
                onSaveClick={()=>{handleSave(handleAddEditAnswer)}}
                onCancelClick={() => location.state ? navigate(location.state) : navigate(`/reportsDev/${folderId}`)}
            >
                <DesignerTextField
                    label = {fieldLabels.reportName}
                    minWidth = {StyleConsts.designerTextFieldMinWidth}
                    value = {data.reportName}
                    onChange = {data => {handleChange('reportName', data)}}
                    displayBlock
                    fullWidth
                    error = {errorField.reportName}
                />

                <DesignerTextField
                    label = {fieldLabels.reportDescription}
                    minWidth = {StyleConsts.designerTextFieldMinWidth}
                    value = {data.reportDescription}
                    onChange = {data => {handleChange('reportDescription', data)}}
                    displayBlock
                    fullWidth
                    error = {errorField.reportDescription}
                />
                
                <DesignerFolderItemPicker
                    label = {fieldLabels.reportDataSetId}
                    minWidth = {StyleConsts.designerTextFieldMinWidth}
                    value = {selectedDataset.name ?  selectedDataset.schemaName + '.' + selectedDataset.name + ' (' + selectedDataset.dataSource.name  +')' : null}
                    itemType = {FolderItemTypes.dataset}
                    onChange = {handleChangeDataset}
                    displayBlock
                    fullWidth
                    disabled={!!data.reportId}
                    error = {errorField.reportDataSetId}
                />

                <DesignerTextField
                    label = {fieldLabels.reportRequirementsLink}
                    minWidth = {StyleConsts.designerTextFieldMinWidth}
                    value = {data.reportRequirementsLink}
                    onChange = {data => {handleChange('reportRequirementsLink', data)}}
                    displayBlock
                    fullWidth
                    error = {errorField.reportRequirementsLink}
                />
            </DesignerPage>
    });

    tabs.push({
        tablabel: "Поля",
        tabdisabled: !(data.reportName && data.reportDescription && data.reportDataSetId) ,
        tabcontent:  uploading ? <CircularProgress /> :
            <DesignerPage 
                onSaveClick={()=>{handleSave(handleAddEditAnswer)}}
                onCancelClick={() => location.state ? navigate(location.state) : navigate(`/reportsDev/${folderId}`)}
                twoColumn = {true}
            >
                <ReportFields 
                    fields={fieldValues}
                    dataset={selectedDataset}
                    onFieldChange={handleFieldChange}
                    onFieldMove={handleFieldMove}
                    onFieldDrop={handleFieldDrop}
                />
            </DesignerPage>
    });

    tabs.push({
        tablabel: "Фильтры",
        tabdisabled: !(data.reportName && data.reportDescription && data.reportDataSetId) ,
        tabcontent:  uploading ? <CircularProgress /> :
            <DesignerPage 
                onSaveClick={()=>{handleSave(handleAddEditAnswer)}}
                onCancelClick={() => location.state ? navigate(location.state) : navigate(`/reportsDev/${folderId}`)}
            >
                <ReportFiltersTab 
                    childGroupsMap={props.reportFiltersGroup}
                    reportId={data.reportId}
                    reportFields={fieldValues}
                    onAddFilter={props.actionFiltersAdd}
                    onAddChildGroup={props.actionFiltersGroupAdd}
                    onChangeFiltersGroup={props.actionFiltersGroupChange}
                    onChangeFilterField={props.actionFiltersChange}
                    onChangeOrdinal={props.actionFiltersChangeOrdinal}
                    onDelete={props.actionFiltersDelete}
                    onMoveBefore={props.actionMoveBefore}
                    onMoveInto={props.actionMoveInto}
                    datasetType ={selectedDataset.typeId}
                />
            </DesignerPage>
    });
    
    tabs.push({
        tablabel: "Шаблоны отчетов",
        tabdisabled: !(data.reportName && data.reportDescription && data.reportDataSetId) ,
        tabcontent:  uploading ? <CircularProgress /> :
            <DesignerPage 
                onSaveClick={()=>{handleSave(handleAddEditAnswer)}}
                onCancelClick={() => location.state ? navigate(location.state) : navigate(`/reportsDev/${folderId}`)}
            >
                <ReportTemplates 
                    reportId={id}
                />
            </DesignerPage>
    });

    return(
        <DataLoader
            loadFunc = {loadFunc}
            loadParams = {loadParams}
            onDataLoaded = {handleDataLoaded}
            onDataLoadFailed = {message => enqueueSnackbar(`Не удалось получить информацию об отчете: ${message}`, {variant : "error"})}
        >
            <DataLoader
                loadFunc = {loadFuncDataSet}
                loadParams = {[data.reportDataSetId]}
                onDataLoaded = {setSelectedDataset}
                onDataLoadFailed = {message => enqueueSnackbar(`Не удалось получить информацию об отчете: ${message}`, {variant : "error"})}
            >
                <DataLoader
                    loadFunc = {dataHub.excelTemplateController.get}
                    loadParams = {[id]}
                    onDataLoaded = {data => props.actionLoaded(id, data)}
                    onDataLoadFailed = {data => props.actionLoadedFailed(id, data)}
                >
                    <div className={classes.rel}> 
                        <div className={classes.abs}>
                            <PageTabs
                                tabsdata={tabs}
                                onTabChange={handleChangeTabs}
                                pageName={pagename}
                            />
                            <Backdrop className={classes.backdrop} open={uploading}>
                                <CircularProgress color="inherit"/>
                            </Backdrop>
                        </div>
                    </div>
                </DataLoader>
            </DataLoader>
        </DataLoader>
    );
}

const mapStateToProps = state => {
    return {
        reportFiltersGroup: state.reportFilters.childGroups,
    }
}

const mapDispatchToProps = {
    actionFiltersLoaded,
    actionFiltersAdd, 
    actionFiltersGroupAdd,
    actionFiltersDelete,
    actionFiltersGroupChange,
    actionFiltersChange,
    actionFiltersChangeOrdinal,
    actionMoveBefore, 
    actionMoveInto,
    actionLoaded, 
    actionLoadedFailed,
    editItemNavbar,
    addItemNavbar
}

export default connect(mapStateToProps, mapDispatchToProps)(ReportDevDesigner);