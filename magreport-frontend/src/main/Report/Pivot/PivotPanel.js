import React, { useState, useRef, useEffect } from 'react';
import { connect } from 'react-redux';

import { useSearchParams } from 'react-router-dom'

import { DragDropContext } from  'react-beautiful-dnd';
import Measure from 'react-measure';
import Dialog from '@material-ui/core/Dialog';
import { Box, Typography, CircularProgress, Menu, MenuItem } from '@material-ui/core';
import Grid from '@material-ui/core/Grid';
import { useSnackbar } from 'notistack';

//actions
import { showAlertDialog, hideAlertDialog } from 'redux/actions/UI/actionsAlertDialog'
import { setAggModalParams } from 'redux/actions/olap/olapAction.js'

// magreport
import dataHub from 'ajax/DataHub'
import DataLoader from 'main/DataLoader/DataLoader'
import PivotFieldsList from './PivotFieldsList'
import PivotTable from './PivotTable'
import PivotTools from './PivotTools'
import {PivotCSS} from './PivotCSS'
import TableRangeControl from './TableRangeControl'
import PivotFilterModal from './PivotFilterModal'
import PivotMetricModal from './PivotMetricModal'     

//dataobjects
import PivotDataProvider from './dataobjects/PivotDataProvider'
import {TableData} from './dataobjects/TableData'
import OlapConfig from './dataobjects/OlapConfig';
import PivotConfiguration from './dataobjects/PivotConfiguration';
import {FilterObject} from './dataobjects/FilterObject';

//UI
import ConfigDialog from './UI/ConfigDialog';
import ConfigSaveDialog from './UI/ConfigSaveDialog';
import ShareJobDialog from './UI/ShareJob/ShareJobDialog';
import SortingDialog from './UI/SortingDialog/index';
import FormattingDialog from './UI/FormattingDialog';
import ConditionalFormattingDialog from './UI/ConditionalFormattingDialog';

//utils
import validateSaveConfig from 'utils/validateSaveConfig';
import DerivedFieldDialog from './UI/DerivedFieldDialog/DerivedFieldDialog';


/**
 * @param {Number} props.jobId - id задания
 * @param {Number} props.reportId - id отчёта
 * @param {Number} props.folderId - id разработческой папки в которой находится отчет
 * @param {String} props.jobOwnerName - login владельца отчета
 * @param {*} props.fullScreen - признак, является ли режим отображения сводной полноэкранным\
 * @param {*} props.onRestartReportClick - function() - callback перезапуска отчёта на панели сводной
 * @param {*} props.onViewTypeChange - function() - callback смена вида с сводной на простую таблицу
 * @param {*} props.onFullScreen - function - callback полноэкранный режим
*/

function PivotPanel(props){

    const styles = PivotCSS();
    const { enqueueSnackbar } = useSnackbar();

    const [searchParams, setSearchParams] = useSearchParams();

    useEffect(() => {
        // Проверка пользователя на разработчика отчета
        dataHub.userController.whoAmI(({data}) => {
            if (data.name === props.jobOwnerName) {
                setShowShareToolBtn(true)
            }
        })
        // Проверка прав пользователя на разработческую папку в которой находится отчет
        dataHub.userServiceController.checkPermission(props.folderId, ({data}) => {
            if (data.authority === 'WRITE') isReportDeveloper.current = true
        })

    }, [props.jobOwnerName, props.folderId])

    const [ showShareToolBtn, setShowShareToolBtn ] = useState(false);

    /*
        Конфигурация самого инструмента
    */

    // Видимость панелей с полями
    const [fieldsVisibility, setFieldsVisibility] = useState(true);

    function handleFieldsVisibility(value){
        setFieldsVisibility(value);
    }

    //Выводить только неиспользуемые поля или все поля
    const [onlyUnused, setOnlyUnused] = useState(true);

    function handleOnlyUnusedClick() {
        setOnlyUnused(!onlyUnused);
    }

    // Полноэкранный режим
    const [pivotFullScreen, setPivotFullScreen] = useState(false);

    function handleFullScreen(value){
        setPivotFullScreen(value);
    }

    const oldAndNewConfiguration = useRef({
        newFieldIndex : 0,
        oldConfiguration: null,
        newConfiguration: null
    })


    // модальное окно задания фильтра
    const [filterModalOpen, setFilterModalOpen] = useState(false);
    const [metricModalOpen, setMetricModalOpen] = useState(false);
    const [filterModalStyle, setFilterModalStyle] = useState('');

    // модальное окно создания нового поля
    const [createFieldDialogOpen, setCreateFieldDialogOpen] = useState(false);

    // Индекс поля для которого настраивается фильтрация в списке полей фильтрации
    const [filterFieldIndex, setFilterFieldIndex] = useState(undefined);

    // Индекс редактируемой метрики
    const [metricFieldIndex, setMetricFieldIndex] = useState(undefined);

    /*
        Данные и отображение данных
    */

    // Объект конфигурации
    const [pivotConfiguration, setPivotConfiguration] = useState(new PivotConfiguration());

    // Предоставление данных для таблицы - dataProvider
    const dataProviderRef = useRef(new PivotDataProvider(props.jobId, handleTableDataReady, handleTableDataLoadFailed/*, initialColumnCount, initialRowCount*/));

    // загрузка данных таблицы: 0 - данные загружены успешно, 1 - идёт загрузка, 2 - загрузка завершена с ошибкой
    const [tableDataLoadStatus, setTableDataLoadStatus] = useState(0);

    const [tableDataLoadErrorMessage, setTableDataLoadErrorMessage] = useState("");

    // запрос данных и данные для отображения
    const [tableData, setTableData] = useState(new TableData());

    // Размер области для сводной таблицы
    const [tableSize, setTableSize] = useState({
        dimensions: {
            width: -1,
            height: -1,
            left: 0
        }
    }) 
    
    // Размер сводной таблицы
    const [innerTableSize, setInnerTableSize] = useState({
        dimensions: {
            width: 0,
            height: 0,
            left: 0
        }
    })
    
    function handleChangeInnerTableSize(value){

        if (value.dimensions.width >0 && value.dimensions.height >0 ){
            let cc = columnCount; 
            let rc = rowCount;
            if (tableSize.dimensions.width - value.dimensions.width < 0 && columnCount >3) {
                cc = columnCount - 1;
            }

            if (/*tableSize.dimensions.height - value.dimensions.height <0 &&* rowCount >3*/ true){
                let avgHeight = value.dimensions.height/(rowCount + 1 + pivotConfiguration.fieldsLists.columnFields.length)
                let h = Math.abs(value.dimensions.height - tableSize.dimensions.height);
                
                if (innerTableSize.dimensions.height < value.dimensions.height && value.dimensions.height > tableSize.dimensions.height){
                    
                    rc = rowCount - Math.floor(h/avgHeight);
                    rc=Math.max(3, Math.min( rc, rowCount));
                    //Высота увеличилась и нет места для увеличение

                } else if (innerTableSize.dimensions.height > value.dimensions.height && value.dimensions.height < tableSize.dimensions.height){
                    //Высота уменьшилась и есть место
                    rc = rowCount + Math.floor(h/avgHeight);
                }

                if (rc<3) {rc=3}
            }

            if ((columnCount !== cc || rowCount !== rc ) && tableDataLoadStatus !== 1){
                setInnerTableSize(value);
                setColumnCount(cc);
                setRowCount(rc);
                  if(!dataProviderRef.current.changeWindow(pivotConfiguration.columnFrom, cc, pivotConfiguration.rowFrom, rc)){
                    //setTableDataLoadStatus(1);
                   // console.log('handleChangeInnerTableSize');
                 //   console.log('1: ' + tableDataLoadStatus)
                }
            }
        }
    }

    const [columnCount, setColumnCount] = useState(0);
    const [rowCount, setRowCount] =useState(0);

    /*
        **************************************
        Загрузка данных
        **************************************
    */

    function handleMetadataLoaded(data){
        let fieldIdToNameMapping = new Map();
        let derivedFieldIdToNameMapping = new Map();
        for(let v of data.fields){
            fieldIdToNameMapping.set(v.id, v.name);
        }
        for(let v of data.derivedFields){
            derivedFieldIdToNameMapping.set(v.id, v.name);
        }        
        dataProviderRef.current.setFieldIdToNameMapping(fieldIdToNameMapping);

        let newConfiguration = new PivotConfiguration(pivotConfiguration);
        newConfiguration.create({}, data);

        const configId = searchParams.get('configId')

        if(configId) {
            // Загрузка определенной конфигурации
            dataHub.olapController.getCurrentConfig(props.jobId, ({ok, data}) => ok && handleGetChoosenCurrentConfig(data, newConfiguration, configId))
        } else {
            // Загрузка текущей конфигурации
            dataHub.olapController.getCurrentConfig(props.jobId, ({ok, data}) => ok && handleGetCurrentConfig(data, newConfiguration))
        }
    }

    function handleTableDataReady(newTableData){
        setTableDataLoadStatus(0);
        setTableData(newTableData);
        setTableDataLoadErrorMessage(null);
    }

    function handleTableDataLoadFailed(message){
        setTableDataLoadStatus(2);
        setTableDataLoadErrorMessage(message);
    }

    /*
        **************************************
        Конфигурация
        **************************************
    */

    // Объект OLAP-конфигурации
    const configOlap = useRef(new OlapConfig());

    // Обновление DataLoader
    const [resetComponentKey, setResetComponentKey] = useState(false);
    const resetDataLoader = () => setResetComponentKey(!resetComponentKey);

    const [avaibleConfigs, setAvaibleConfigs] = useState(null);

    const [otherDerivedFields, setOtherDerivedFields] = useState([])

    const [showConfigDialog, setShowConfigDialog] = useState(false);
    const [showConfigSaveDialog, setShowConfigSaveDialog] = useState(false);

    const isReportDeveloper = useRef(false)

    // Взаимодействие с окнами конфигураций 
    function handleSetConfigDialog(action){
        switch(action) {

            case 'openConfigDialog':
                handleGetAvailableConfigs('ConfigDialog')
                break;

            case 'closeConfigDialog':
                setShowConfigDialog(false)
                break;

            case 'openConfigSaveDialog':
                handleGetAvailableConfigs('ConfigSaveDialog')
                break;

            case 'closeConfigSaveDialog':
                setShowConfigSaveDialog(false)
                break;

            default:
                return false
        }
    }

    // Получение текущей конфигурации
    function handleGetCurrentConfig(responseData, newConfiguration){
        configOlap.current.createLists(responseData)
        
        if (responseData.olapConfig.data.length > 0) {
            const configData = JSON.parse(responseData.olapConfig.data)
            const { columnFrom, columnCount, rowFrom, rowCount } = configData

            // Проверка на валидацию сохраненных полей конфигурации olapConfig с полями из Metadata
            validateSaveConfig(configData.fieldsLists, newConfiguration.fieldsLists.allFields, newConfiguration.fieldsLists.derivedFields, (validate, otherDeriverdFields) => {

                if (validate) {
                    newConfiguration.restore(configData);
                    if (columnFrom>columnCount || rowFrom>rowCount ) {
                        newConfiguration.setColumnFrom(0);
                        newConfiguration.setRowFrom(0);
                    }
                    
                    let sortingValuesAreValide = true
    
                    if (!newConfiguration.sortOrder?.rowSort && !newConfiguration.sortOrder?.columnSort) {
                        sortingValuesAreValide = false
                    }
    
                    dataProviderRef.current.loadDataForNewFieldsLists(newConfiguration.fieldsLists, newConfiguration.filterGroup, newConfiguration.metricFilterGroup, sortingValuesAreValide ? newConfiguration.sortOrder : {}, newConfiguration.columnFrom, columnCount, newConfiguration.rowFrom, rowCount);
        
                    oldAndNewConfiguration.current = {
                        oldConfiguration: new PivotConfiguration(pivotConfiguration),
                        newConfiguration: new PivotConfiguration(newConfiguration)
                    }
    
                    setSortingValues(sortingValuesAreValide ? newConfiguration.sortOrder : {})
                    setOtherDerivedFields(otherDeriverdFields)
    
                } else {
                    enqueueSnackbar('Не удалось загрузить конфигурацию. Поля в конфигурации не соответсвуют отчету', {variant : "error"});
                }
            })


        }

        setSearchParams({view: 'pivot'})

        setPivotConfiguration(newConfiguration);
    }

    // Получение выбранной конфигурации
    function handleGetChoosenCurrentConfig(responseData, newConfiguration, configId){
        configOlap.current.createLists(responseData)
        
        dataHub.olapController.getChoosenConfig(configId, ({ok, data}) => { if(ok) {
            const configData = JSON.parse(data.olapConfig.data),
                    { columnFrom, columnCount, rowFrom, rowCount } = configData

            // Проверка на валидацию сохраненных полей конфигурации olapConfig с полями из Metadata
            const isSaveConfigValide = !validateSaveConfig(configData.fieldsLists,  newConfiguration.fieldsLists.allFields, newConfiguration.fieldsLists.derivedFields)

            if (isSaveConfigValide) {
                newConfiguration.restore(configData);
                if (columnFrom>columnCount || rowFrom>rowCount ){
                    newConfiguration.setColumnFrom(0);
                    newConfiguration.setRowFrom(0);
                }
                
                let sortingValuesAreValide = true

                if (!newConfiguration.sortOrder?.rowSort && !newConfiguration.sortOrder?.columnSort) {
                    sortingValuesAreValide = false
                }

                dataProviderRef.current.loadDataForNewFieldsLists(newConfiguration.fieldsLists, newConfiguration.filterGroup, newConfiguration.metricFilterGroup, sortingValuesAreValide ? newConfiguration.sortOrder : {}, newConfiguration.columnFrom, columnCount, newConfiguration.rowFrom, rowCount);
    
                oldAndNewConfiguration.current = {
                    oldConfiguration: new PivotConfiguration(pivotConfiguration),
                    newConfiguration: new PivotConfiguration(newConfiguration)
                }

                setSortingValues(sortingValuesAreValide ? newConfiguration.sortOrder : {})
                configOlap.current.loadChosenConfig(data.olapConfig.data, (ok) => {
                    if (ok) {
                        handleSetConfigDialog('closeConfigDialog')
                        enqueueSnackbar('Конфигурация "' + data.olapConfig.name + '" успешно загружена' , {variant : "success"});
                    } else {
                        enqueueSnackbar('Не удалось загрузить конфигурацию "' + data.olapConfig.name + '". Некорректные данные.', {variant : "error"});
                    }
                })

            } else {
                enqueueSnackbar('Не удалось загрузить конфигурацию. Поля в конфигурации не соответсвуют отчету', {variant : "error"});
            }
        }})

        setSearchParams({view: 'pivot'})

        setPivotConfiguration(newConfiguration);
    }

    // Cохранение текущей конфигурации
    function handleSaveCurrentConfig(pivotConfigForSave){
        configOlap.current.saveCurrentConfig(pivotConfigForSave)
    }

    // Cохранение конфигурации (не текущей)
    function handleSaveConfig(obj){

        const { type } = obj
        
        // Cохранение новой конфигурации
        if ( type === 'saveNewConfig') {
            return configOlap.current.saveNewConfig(obj, props.reportId, (ok, name) => {
                if (ok) {
                    handleSetConfigDialog('closeConfigSaveDialog')
                    return enqueueSnackbar('Конфигурация "' + name + '" успешно сохранена' , {variant : "success"});
                }
    
                return enqueueSnackbar('Не удалось сохранить конфигурацию "' + name + '"', {variant : "error"});
            })
        }

        // Cохранение выбранной конфигурации
        return configOlap.current.saveExistingConfig(obj, props.reportId, (ok, name) => {
            if (ok) {
                handleSetConfigDialog('closeConfigSaveDialog')
                return enqueueSnackbar('Конфигурация "' + name + '" успешно обновлена' , {variant : "success"});
            }

            return enqueueSnackbar('Не удалось обновить конфигурацию "' + name + '"', {variant : "error"});
        })
    }

    // Получение списка доступных конфигураций пользователю
    function handleGetAvailableConfigs(type){
        let configsArr = [];

        dataHub.olapController.getAvailableConfigs(props.jobId, ({ok, data}) => {
            if (ok) {

                if (type === 'ConfigDialog') {
                    setAvaibleConfigs(data)
                    return setShowConfigDialog(true)
                } else if (type === 'ConfigSaveDialog') {

                    if(isReportDeveloper.current === true) {
                        for (let itemKey in data) {
                            if(itemKey === 'sharedJobConfig') {
                                let res = []

                                configsArr.forEach(element => res.push(element.olapConfig.name));

                                data[itemKey].forEach(item => !res.includes(item.olapConfig.name) && configsArr.push(item))
                            } else {
                                data[itemKey].forEach(item => configsArr.push(item))
                            }
                        }
                        setAvaibleConfigs(configsArr)
                    } else {
                        for (var key in data) {
                            if (key !== 'sharedJobConfig') {
                                data[key].map(item => configsArr.push(item))
                            }
                        }
                        setAvaibleConfigs(configsArr)
                    }

                    return setShowConfigSaveDialog(true)
                } 

                for (let itemKey in data) {
                    data[itemKey].map(item => configsArr.push(item))
                }

                return setAvaibleConfigs(configsArr)
            } 

        })
    }

    // Удаляем конфигурацию
    function handleDeleteConfig({id, name, type}) {
        if (type === 'ConfigDialog') {
            dataHub.olapController.deleteConfig(id, ({ok}) => {

                if (ok) {
                    handleGetAvailableConfigs(type);
                    return enqueueSnackbar('Конфигурация "' + name + '" успешно удалена' , {variant : "success"})
                }
    
                return enqueueSnackbar('Не удалось удалить конфигурацию "' + name + '"', {variant : "error"});
            })
        } else if (type === 'errorChooseConfig') {
            return dataHub.olapController.deleteConfig(id, ({ok}) => { ok && handleSetConfigDialog('closeConfigDialog')})
        } else {
            return dataHub.olapController.deleteConfig(id, () => { resetDataLoader() })
        }   
    }

    // При выборе определенной конфигураций и записываем olapConfigData в текущую конфигурацию => обновляем DataLoader
    function handleLoadCertainConfig({data, name, reportOlapConfigId}) {

        validateSaveConfig(JSON.parse(data).fieldsLists, pivotConfiguration.fieldsLists.allFields, pivotConfiguration.fieldsLists.derivedFields, (validate, otherDeriverdFields) => {
            if (validate) {
                setTableDataLoadStatus(1);
                setSearchParams({view: 'pivot'})
                setOtherDerivedFields(otherDeriverdFields)
                return configOlap.current.loadChosenConfig(data, (ok) => {
                    if (ok) {
                        resetDataLoader()
                        handleSetConfigDialog('closeConfigDialog')
                        return enqueueSnackbar('Конфигурация "' + name + '" успешно загружена' , {variant : "success"});
                    }
                    return enqueueSnackbar('Не удалось загрузить конфигурацию "' + name + '". Некорректные данные.', {variant : "error"});
                })
            }
    
            enqueueSnackbar('Не удалось загрузить конфигурацию. Поля в конфигурации не соответсвуют отчету', {variant : "error"})
            return configOlap.current.loadChosenConfig(reportOlapConfigId, (ok) => {})
        })


    }

    // Сохраняем по умолчанию для отчета/задания
    function handleSaveConfigByDefault(item) {

        const { reportOlapConfigId } = item


        dataHub.olapController.setDefault(reportOlapConfigId, ({ok}) => {

            if (ok) {
                handleGetAvailableConfigs("ConfigDialog");
                return enqueueSnackbar('Конфигурация "' + item.olapConfig.name + '" успешно обновлена!' , {variant : "success"});
            }

            return enqueueSnackbar('Не удалось обновить конфигурацию "' + item.olapConfig.name + '"', {variant : "error"});
        })
    }

    // Очистить всю сводную
    function handleClearAllOlap() {
        props.showAlertDialog('Очистить всю конфигурацию?', null, null, handleConfirmClearAllOlap)
    }

    // Очистить всю сводную после подтверждения
    function handleConfirmClearAllOlap(answer){
        if (answer){
            oldAndNewConfiguration.current.newConfiguration.clearAll()
            setPivotConfiguration(oldAndNewConfiguration.current.newConfiguration);
            handleSaveCurrentConfig(oldAndNewConfiguration.current.newConfiguration.stringify())
            resetDataLoader()
        }
        props.hideAlertDialog()
    }

    // Экспорт сводной в Excel
    function handleExportToExcel() {
        const payload = {
            cubeRequest: {
              jobId: props.jobId,
              columnFields: pivotConfiguration.fieldsLists.columnFields.map( (v) => ({fieldId: v.fieldId, fieldType: dataHub.olapController.getFieldType(v.original)})),
              rowFields: pivotConfiguration.fieldsLists.rowFields.map( (v) => ({fieldId: v.fieldId, fieldType: dataHub.olapController.getFieldType(v.original)})),
              metrics: pivotConfiguration.fieldsLists.metricFields.map( (v) => ({ "field": {fieldId: v.fieldId, fieldType: dataHub.olapController.getFieldType(v.original)}, 
                                                                                    aggregationType : v.aggFuncName }) ),
              metricPlacement: pivotConfiguration.columnsMetricPlacement === true ? "COLUMNS" : "ROWS",
              filterGroup: pivotConfiguration.filterGroup,
              metricFilterGroup: pivotConfiguration.metricFilterGroup,
              columnsInterval: {
                from: pivotConfiguration.columnFrom,
                count: 42
              },
              rowsInterval: {
                from: pivotConfiguration.rowFrom,
                count: 150
              },
              allFieldIds: pivotConfiguration.fieldsLists.allFields.map(item => item.id)
            },
            configuration: configOlap.current.configData.reportOlapConfigId,
            stylePivotTable: false
        }

        if(pivotConfiguration.sortOrder?.columnSort && pivotConfiguration.sortOrder.columnSort.data[0].order.trim() !== '') {
            payload.cubeRequest.columnSort = [pivotConfiguration.sortOrder.columnSort.data[0]]
        }

        if(pivotConfiguration.sortOrder?.rowSort && pivotConfiguration.sortOrder.rowSort.data[0].order.trim() !== '') {
            payload.cubeRequest.rowSort = [pivotConfiguration.sortOrder.rowSort.data[0]]
        }

        enqueueSnackbar("Запущен экспорт в Excel. Формирование файла может происходить достаточно ДОЛГО " +
        "в виду необходимости его криптографической обработки в целях информационной безопасности", {variant : "info"});
        dataHub.olapController.createExcelPivotTable(payload, handleExcelFileResponseNew)
    }

    function handleExcelFileResponseNew(resp){
        if (resp.ok){
            const url = resp.data.urlFile + resp.data.token
            const link = document.createElement('a');
            link.href = url;
            document.body.appendChild(link);
            link.click();
            link.parentNode.removeChild(link);
        }
        else {
            enqueueSnackbar("Не удалось получить файл с сервера", { variant: 'error'});
        }
    }



    /*
        **************************************
        Поделиться заданием
        **************************************
    */
   
    const [shareJobDialogOpen, setShareJobDialogOpen] = useState(false);
    const [usersForShareJobDialog, setUsersForShareJobDialog] = useState([]);
    const [usersWithAccessForShareJobDialog, setUsersWithAccessForShareJobDialog] = useState([]);
    
    // Взаимодействие с ShareJobDialog и получение списка всех пользователей и пользователей имеющих доступ к заданию
    function handleShareJobDialog(value) {
        
        if (value === true) {
            dataHub.reportJobController.getUsersJob(props.jobId, ({ok, data}) => {
                if (ok) {
                    let newArrForUsersWithAccess = data.map(user => user.id)
                    setUsersWithAccessForShareJobDialog(newArrForUsersWithAccess)

                    return dataHub.userController.users( ({data}) => {

                        setUsersForShareJobDialog(data)
                        setShareJobDialogOpen(value);
                    })
                }
                enqueueSnackbar('Не удалось открыть окно. Попробуйте позже', {variant : "error"});
            })
        } else {
            setShareJobDialogOpen(value);
        }
    }

    // Сохранение списка пользователей, с которыми поделились этим заданием
    function handleSaveShareJob(listOfSelectedUsers) {
        
        dataHub.reportJobController.share(props.jobId, listOfSelectedUsers, ({ok}) => {

            if (ok) {
                setShareJobDialogOpen(false);
                return enqueueSnackbar('Список пользователей, с которыми поделились этим заданием успешно сохранен' , {variant : "success"});
            }

            return enqueueSnackbar('Не удалось сохранить список пользователей, с которыми поделились этим заданием', {variant : "error"});
        
        })
    }
    
    // Сохраняем "Общий доступ"
    function handleSaveGeneralAccess(eventValue, item) {

        const { reportOlapConfigId } = item

        dataHub.olapController.reportShare(reportOlapConfigId, eventValue, ({ok}) => {

            if (ok) {
                handleGetAvailableConfigs("ConfigDialog");
                return enqueueSnackbar('Общий доступ конфигурации "' + item.olapConfig.name + '" успешно обновлен' , {variant : "success"});
            }

            return enqueueSnackbar('Не удалось обновить общий доступ конфигурации "' + item.olapConfig.name + '"', {variant : "error"});
        })
    }


    /*
    **************************************
        Размещение метрик и слияние ячеек
    **************************************
    */

    function handleSetMetricPlacement(placeColumn){
        let newPivotConfiguration = new PivotConfiguration(pivotConfiguration);
        newPivotConfiguration.columnsMetricPlacement = placeColumn;
        setPivotConfiguration(newPivotConfiguration);
        handleSaveCurrentConfig(newPivotConfiguration.stringify())
    }

    function handleSetMergeMode(mergeMode){
        let newPivotConfiguration = new PivotConfiguration(pivotConfiguration);
        newPivotConfiguration.mergeMode = mergeMode;
        setPivotConfiguration(newPivotConfiguration);
        handleSaveCurrentConfig(newPivotConfiguration.stringify())
    }    

    function handleDragEnd(result){

        const { destination, source, draggableId } = result; // eslint-disable-line

        if (destination &&
                (destination.droppableId !== source.droppableId 
                || destination.index !== source.index 
                || destination.droppableId === "metricFields"
                || destination.droppableId === "filterFields")
            )
        {
            
            // Формируем новый объект конфигурации
            let newPivotConfiguration = new PivotConfiguration(pivotConfiguration);
            let destIndex = newPivotConfiguration.dragAndDropField(source.droppableId, destination.droppableId, source.index, destination.index);

            // Обрабатываем особенности использования полей в некоторых списках
            if(destination.droppableId === "metricFields"){
                // Если поле помещено в список метрик - выставляем метрику по умолчанию и открываем диалоговое окно выбора агрегирующей функции
                // Кроме того запоминаем старые списки полей, чтобы можно было восстановить их в случае отказа

                oldAndNewConfiguration.current = {
                    newFieldIndex : destIndex,
                    oldConfiguration: new PivotConfiguration(pivotConfiguration),
                    newConfiguration: new PivotConfiguration(newPivotConfiguration)
                }

                let dataType = pivotConfiguration.fieldsLists[source.droppableId][source.index].type;

                setTimeout(() => {
                    props.setAggModalParams({open: true, index: destIndex, type: 'add', dataType: dataType});
                }, 300)

                setPivotConfiguration(newPivotConfiguration);
            }
            else if(destination.droppableId === "filterFields"){ 
                setFilterFieldIndex(destIndex);
                // Сохраняем старую конфигурацию для отмены и открываем модальное окно задания фильтров
                oldAndNewConfiguration.current = {
                    newFieldIndex : destIndex,
                    oldConfiguration: new PivotConfiguration(pivotConfiguration),
                    newConfiguration: new PivotConfiguration(newPivotConfiguration)
                }
                setFilterModalOpen(true);

                setPivotConfiguration(newPivotConfiguration);
            }
            else if ((source.droppableId === 'filterFields' || source.droppableId === 'metricFields')&& (destination.droppableId === 'unusedFields'||destination.droppableId === 'allFields')){
                newPivotConfiguration.replaceFilter()

                setPivotConfiguration(newPivotConfiguration);
            }

            // Если меняется состав полей сводной - обнуляем начальные столбец и строку
            if( !( destination.droppableId === 'filterFields' || destination.droppableId === "metricFields" 
                || (destination.droppableId === "unusedFields" && source.droppableId === "unusedFields") ) ) {
                newPivotConfiguration.setColumnFrom(0);
                newPivotConfiguration.setRowFrom(0);
                setTableDataLoadStatus(1);
                let cc = Math.ceil(tableSize.dimensions.width/85) - 1 - newPivotConfiguration.fieldsLists.rowFields.length ;
                let rc = Math.ceil(tableSize.dimensions.height/20) + 1 - newPivotConfiguration.fieldsLists.columnFields.length;

                if (destination.droppableId !== 'unusedFields' || source === 'filterFields'){
                    rc = Math.max(Math.min(rc, rowCount), 0);
                }
                setColumnCount(cc);
                setRowCount(rc);
                newPivotConfiguration.setColumnAndRowCount(cc, rc);
                newPivotConfiguration.changeSortOrder({});
                dataProviderRef.current.loadDataForNewFieldsLists(newPivotConfiguration.fieldsLists, newPivotConfiguration.filterGroup, newPivotConfiguration.metricFilterGroup, {}, 0, cc, 0, rc);
            
                oldAndNewConfiguration.current = {
                    newFieldIndex : destIndex,
                    oldConfiguration: new PivotConfiguration(pivotConfiguration),
                    newConfiguration: new PivotConfiguration(newPivotConfiguration)
                }
                handleSaveCurrentConfig(newPivotConfiguration.stringify())
                setSortingValues({})

                setPivotConfiguration(newPivotConfiguration);
            }  
        }
    }

    function onRemoveFieldClick(droppableId, index) {
        handleDragEnd({
            destination: {droppableId: 'unusedFields', index: 0},
            source : {droppableId: droppableId, index: index}
        })
    }

    /*
        ***********************************************
        Модальное окно выбора агрегирующей функции
        ***********************************************
    */
      
    // Закрытие модального окна без выбора метрики
    function handleAggModalClose(){
        if (props.aggModalParams.type === 'add'){
            setPivotConfiguration(oldAndNewConfiguration.current.oldConfiguration);
        }
        props.setAggModalParams({...props.aggModalParams, open: false});

    }

    // Выбор определенной агригирующей функции
    function handleChooseAggForMetric(funcName, index){
        // Формируем новый объект конфигурации
        oldAndNewConfiguration.current = {
            newFieldIndex : index,
            oldConfiguration: new PivotConfiguration(pivotConfiguration),
            newConfiguration: new PivotConfiguration(pivotConfiguration)
        }

        oldAndNewConfiguration.current.newConfiguration.setAggMetricByIndex(funcName, index);
        oldAndNewConfiguration.current.newConfiguration.setColumnAndRowCount(columnCount, rowCount);
        oldAndNewConfiguration.current.newConfiguration.setAggMetricForFormatting(index);
        //При изменении функции метрики удалить фильтры по ней

        let f = oldAndNewConfiguration.current.oldConfiguration.fieldsLists.metricFields[index];
        if (f.aggFuncName !== ""){
            oldAndNewConfiguration.current.newConfiguration.dropFiltersByMetric(f.fieldId, f.aggFuncName);
        }

        // Т.к. для фильтров по метрикам нужен индекс метрики,
        // который определяется по fieldId и aggFuncName,
        // то при изменении метрик нужно пересчитать фильтры
        oldAndNewConfiguration.current.newConfiguration.replaceFilter();
        setPivotConfiguration(new PivotConfiguration(oldAndNewConfiguration.current.newConfiguration));
        handleSaveCurrentConfig(oldAndNewConfiguration.current.newConfiguration.stringify());
        props.setAggModalParams({...props.aggModalParams, open: false});
        setTableDataLoadStatus(1);
        dataProviderRef.current.loadDataForNewFieldsLists(oldAndNewConfiguration.current.newConfiguration.fieldsLists, oldAndNewConfiguration.current.newConfiguration.filterGroup, oldAndNewConfiguration.current.newConfiguration.metricFilterGroup, oldAndNewConfiguration.current.newConfiguration.sortOrder, oldAndNewConfiguration.current.newConfiguration.columnFrom, columnCount, oldAndNewConfiguration.current.newConfiguration.rowFrom, rowCount);
    }

    /*
        ***************************************************
        Перемещение окна отображения по строкам и столбцам
        ***************************************************
    */

    function handleColumnFromChange(newColumnFrom) {

        let newConfiguration = new PivotConfiguration(pivotConfiguration);
        newConfiguration.setColumnFrom(newColumnFrom);
        setPivotConfiguration(newConfiguration);
        handleSaveCurrentConfig(newConfiguration.stringify());

        if(!dataProviderRef.current.changeWindow(newColumnFrom, columnCount, newConfiguration.rowFrom, rowCount)){
            setTableDataLoadStatus(1);
        }
    }

    function handleRowFromChange(newRowFrom) {

        let newConfiguration = new PivotConfiguration(pivotConfiguration);
        newConfiguration.setRowFrom(newRowFrom);;
        setPivotConfiguration(newConfiguration);
        handleSaveCurrentConfig(newConfiguration.stringify());

        if(!dataProviderRef.current.changeWindow(newConfiguration.columnFrom, columnCount, newRowFrom, rowCount)){
            setTableDataLoadStatus(1);
        }
    }
    /*
        ***************************************************
        Переименование метрик
        ***************************************************
    */
    
    // Закрытие модального окна без переименования метрики
    function handleMetricModalClose(){
            setPivotConfiguration(oldAndNewConfiguration.current.oldConfiguration);
            setMetricModalOpen(false);
    }

    // Вызывается при нажатии на метрику
    function handleMetricFieldButtonClick(event, i){
        let newPivotConfiguration = new PivotConfiguration(pivotConfiguration);

        oldAndNewConfiguration.current = {
            newFieldIndex : i,
            oldConfiguration: new PivotConfiguration(pivotConfiguration),
            newConfiguration: new PivotConfiguration(newPivotConfiguration)
        }

        setMetricFieldIndex(i);

        setMetricModalOpen(true);

        setFilterModalStyle({
            top: event.screenY,
            left: event.screenX,
            transform: `translate(0%, -20%)`,
        })
    }
    
    // Задание описания метрики
    function handleRenameMetric(newName){
        oldAndNewConfiguration.current.newConfiguration.setNewNameByFieldIndex('metricFields', oldAndNewConfiguration.current.newFieldIndex, newName);
        setPivotConfiguration(new PivotConfiguration(oldAndNewConfiguration.current.newConfiguration));

        setMetricModalOpen(false);
		setTableDataLoadStatus(1);
		dataProviderRef.current.loadDataForNewFieldsLists(oldAndNewConfiguration.current.newConfiguration.fieldsLists, oldAndNewConfiguration.current.newConfiguration.filterGroup, oldAndNewConfiguration.current.newConfiguration.metricFilterGroup, {}, 0, columnCount, 0, rowCount);
		handleSaveCurrentConfig(oldAndNewConfiguration.current.newConfiguration.stringify())
    }


    /*
        ***************************************************
        Фильтрация значений
        ***************************************************
    */

    // Вызывается по нажатию на значение измерения в таблице
    function handleDimensionValueFilter(fieldId, fieldType, fieldValue) {
        if (!(pivotConfiguration.fieldsLists.findFilterFieldIdByFieldIdValueFunc(fieldId, fieldValue))){
            let val = fieldValue === '' ? [] : [fieldValue];
			let newConfiguration = new PivotConfiguration(pivotConfiguration);
            let filterObject = new FilterObject({field: {fieldId: fieldId, fieldType: fieldType}, values: val});
			newConfiguration.setFieldFilterByFieldId(fieldId, filterObject);
			newConfiguration.replaceFilter();
			newConfiguration.changeSortOrder({});
            newConfiguration.setColumnFrom(0)
            newConfiguration.setRowFrom(0);
			setPivotConfiguration(newConfiguration);
			setTableDataLoadStatus(1);
			dataProviderRef.current.loadDataForNewFieldsLists(newConfiguration.fieldsLists, newConfiguration.filterGroup, newConfiguration.metricFilterGroup, {}, 0, columnCount, 0, rowCount);
			handleSaveCurrentConfig(newConfiguration.stringify())
            setSortingValues({});
        }

        
    }
    // Вызывается по нажатию на значение метрики в таблице
    function handleMetricValueFilter(fieldId, metricIndex, fieldValue, dataType){
        if (!(pivotConfiguration.fieldsLists.findFilterFieldIdByFieldIdValueFunc(fieldId, fieldValue, metricIndex))){
            let val = fieldValue === '' ? [] : 
                [dataType === 'INTEGER' ? parseInt(fieldValue.replace(/\s+/g, '').trim()):
                 dataType === 'DOUBLE'  ? parseFloat(fieldValue.replace(/\s+/g, '').trim()):
                 fieldValue
                ];
			let newConfiguration = new PivotConfiguration(pivotConfiguration);
			let filterObject = new FilterObject({fieldId: fieldId, values: val});
			newConfiguration.setFieldFilterByFieldIdAndFunc(fieldId, metricIndex, filterObject);
			newConfiguration.replaceFilter();
			newConfiguration.changeSortOrder({});
            newConfiguration.setColumnFrom(0)
            newConfiguration.setRowFrom(0);
			setPivotConfiguration(newConfiguration);
			setTableDataLoadStatus(1);
			dataProviderRef.current.loadDataForNewFieldsLists(newConfiguration.fieldsLists, newConfiguration.filterGroup, newConfiguration.metricFilterGroup, {}, 0, columnCount, 0, rowCount);
			handleSaveCurrentConfig(newConfiguration.stringify())
			setSortingValues({})
        }
    }

    // Закрытие модального окна без выбора значения фильтра
    function handleFilterModalClose(){
        setPivotConfiguration(oldAndNewConfiguration.current.oldConfiguration);
        setFilterModalOpen(false);
    }

    // Вызывается по подтверждению задания объекта фильтрации в модальном окне
    function handleSetFilterValue(filterObject, newName){

        if (filterObject.values.length > 0 || filterObject.filterType === "BLANK"){
            oldAndNewConfiguration.current.newConfiguration.setNewNameByFieldIndex('filterFields', oldAndNewConfiguration.current.newFieldIndex, newName);
            oldAndNewConfiguration.current.newConfiguration.setFieldFilterByFieldIndex(oldAndNewConfiguration.current.newFieldIndex, filterObject);
            oldAndNewConfiguration.current.newConfiguration.setColumnFrom(0);
            oldAndNewConfiguration.current.newConfiguration.setRowFrom(0);
            oldAndNewConfiguration.current.newConfiguration.replaceFilter();
            oldAndNewConfiguration.current.newConfiguration.changeSortOrder({});
            setPivotConfiguration(new PivotConfiguration(oldAndNewConfiguration.current.newConfiguration));
            setFilterModalOpen(false);
            setTableDataLoadStatus(1);
            dataProviderRef.current.loadDataForNewFieldsLists(oldAndNewConfiguration.current.newConfiguration.fieldsLists, oldAndNewConfiguration.current.newConfiguration.filterGroup, oldAndNewConfiguration.current.newConfiguration.metricFilterGroup, {}, 0, columnCount, 0, rowCount);            
            handleSaveCurrentConfig(oldAndNewConfiguration.current.newConfiguration.stringify());
            setSortingValues({})
        } else {
            setPivotConfiguration(new PivotConfiguration(oldAndNewConfiguration.current.oldConfiguration));
            setFilterModalOpen(false);
        }
    }

    // Вызывается по нажатию кнопки фильтрации на поле в зоне фильтров
    function handleFilterFieldButtonClick(event, i){

        let newPivotConfiguration = new PivotConfiguration(pivotConfiguration);
        newPivotConfiguration.replaceFilter(i);
        newPivotConfiguration.setColumnFrom(0)
        newPivotConfiguration.setRowFrom(0);
        oldAndNewConfiguration.current = {
            newFieldIndex : i,
            oldConfiguration: new PivotConfiguration(pivotConfiguration),
            newConfiguration: new PivotConfiguration(newPivotConfiguration)
        }
        setFilterFieldIndex(i);

        setFilterModalOpen(true);

        setFilterModalStyle({
            top: event.screenY,
            left: event.screenX,
            transform: `translate(0%, -20%)`,
        })
    }

    function handleFilterFieldButtonOffClick(event, i){
        let newPivotConfiguration = new PivotConfiguration(pivotConfiguration);
        newPivotConfiguration.fieldsLists.filterFields[i].setIsOff();
        newPivotConfiguration.replaceFilter();
        newPivotConfiguration.setColumnFrom(0)
        newPivotConfiguration.setRowFrom(0);
        oldAndNewConfiguration.current = {
            newFieldIndex : i,
            oldConfiguration: new PivotConfiguration(pivotConfiguration),
            newConfiguration: new PivotConfiguration(newPivotConfiguration)
        }

        setPivotConfiguration(newPivotConfiguration);
        setTableDataLoadStatus(1);
        dataProviderRef.current.loadDataForNewFieldsLists(oldAndNewConfiguration.current.newConfiguration.fieldsLists, oldAndNewConfiguration.current.newConfiguration.filterGroup, oldAndNewConfiguration.current.newConfiguration.metricFilterGroup, {}, 0, columnCount, 0, rowCount);            
        handleSaveCurrentConfig(newPivotConfiguration.stringify());
    }

    function handleMetricFieldContextClick(event, i){
        let dataType = pivotConfiguration.fieldsLists.metricFields[i].type;
        props.setAggModalParams({open: true, index: i, type: 'change', dataType: dataType});
    }
    /*
        ***************************************************
        Context Menu
        ***************************************************
    */
    const [contextMenu, setContextMenu] = useState([]);
    const [showContextMenu, setShowContextMenu] = useState(false);
    const [contextPosition, setContextPosition] = useState({
        mouseX: null,
        mouseY: null,
    });
    const [contextItemsValue, setContextItemsValue] = useState({});

    const handleContextClick = (event, type, values) => {
        if (type === 'metricValues') {
            event.preventDefault();

            let contextItems = [];

            contextItems.push(
                <MenuItem key="sorting" onClick={() => handleOpenSpecificDialog('sorting')}> Сортировка </MenuItem>,
                <MenuItem key="formatting" onClick={() => handleOpenSpecificDialog('formatting')}> Форматирование </MenuItem>
            );

            if (values.dataType === "INTEGER" || values.dataType === "DOUBLE") {
                contextItems.push(
                    <MenuItem key="conditionalFormatting" onClick={() => handleOpenSpecificDialog('conditionalFormatting')}> Условное форматирование </MenuItem>
                );
            }

            setContextMenu(contextItems);
        
            setContextPosition({
                mouseX: event.clientX - 2,
                mouseY: event.clientY - 4,
            });

            setShowContextMenu(true)
            setContextItemsValue(values)
        }

        return false
    };

    const handleContextClose = () => {
        setContextPosition({
            mouseX: null,
            mouseY: null,
        });
        setShowContextMenu(false)
    }

    // Определяем по клику в context-menu и открываем нужное
    const handleOpenSpecificDialog = (action) => {

        if(action) {
            handleContextClose();

            switch(action) {
                case 'conditionalFormatting':
                    return setConditionalFormattingDialog(true)
                case 'formatting': 
                    return setFormattingDialog(true)
                case 'sorting': 
                    return setSortingDialog(true)
                default: 
                    return false 
            }
        }
    }

    /*
        ***************************************************
        Форматирование
        ***************************************************
    */

    //Диалоговое окно форматирования
    const [formattingDialog, setFormattingDialog] = useState(false);

    //Получаем весь объект форматирование и изменяем
    function handleChangeFieldDataStyle(data){
        let newPivotConfiguration = new PivotConfiguration(pivotConfiguration);
        newPivotConfiguration.changeFieldDataStyle(data);
        setPivotConfiguration(newPivotConfiguration);
        setFormattingDialog(false)
        handleSaveCurrentConfig(newPivotConfiguration.stringify())
    }

    /*
        ***************************************************
        Условное форматирование
        ***************************************************
    */

    const [conditionalFormattingDialog, setConditionalFormattingDialog] = useState(false);

    function handleAddConditionalFormatting(index, array){
        let newPivotConfiguration = new PivotConfiguration(pivotConfiguration);
        newPivotConfiguration.addConditionalFormatting(index, array);
        setPivotConfiguration(newPivotConfiguration);
        setConditionalFormattingDialog(false)
        handleSaveCurrentConfig(newPivotConfiguration.stringify())
    }
    /*
        ***************************************************
        Сортировка
        ***************************************************
    */

    const [sortingDialog, setSortingDialog] = useState(false);
    const [sortingDialogAfterTools, setSortingDialogAfterTools] = useState(false);
    const [sortingValues, setSortingValues] = useState({});

    // Отображение SortingDialog после нажатия на кнопку в панели задач
    function handleShowSortingDialog(value){
        setSortingDialogAfterTools(value)
        setSortingDialog(value)
    }

    // Добавляем сортировку
    function handleAddMetricsSort(valuesToSort){
        
        const {rowSort, columnSort} = valuesToSort

        setTableDataLoadStatus(1);
        dataProviderRef.current.loadDataForNewFieldsLists(oldAndNewConfiguration.current.newConfiguration.fieldsLists, pivotConfiguration.filterGroup, pivotConfiguration.metricFilterGroup, valuesToSort, 0, columnCount, 0, rowCount)

        let newPivotConfiguration = new PivotConfiguration(pivotConfiguration);

        if (rowSort.data[0].order !== '') newPivotConfiguration.setColumnFrom(0)
        if (columnSort.data[0].order !== '') newPivotConfiguration.setRowFrom(0);
        newPivotConfiguration.changeSortOrder(valuesToSort);

        setPivotConfiguration(newPivotConfiguration);
        handleSaveCurrentConfig(newPivotConfiguration.stringify())

        handleShowSortingDialog(false)
        setSortingValues((rowSort.data[0].order === '' && columnSort.data[0].order === '') ? {} : valuesToSort)

    }

    // Удаляем сортировку
    function handleDeleteMetricsSort(){
        setSortingValues({})
        handleShowSortingDialog(false)

        setTableDataLoadStatus(1);
        dataProviderRef.current.loadDataForNewFieldsLists(oldAndNewConfiguration.current.newConfiguration.fieldsLists, pivotConfiguration.filterGroup, pivotConfiguration.metricFilterGroup, {}, 0, columnCount, 0, rowCount)

        let newPivotConfiguration = new PivotConfiguration(pivotConfiguration);
        newPivotConfiguration.changeSortOrder({});
        setPivotConfiguration(newPivotConfiguration);
        handleSaveCurrentConfig(newPivotConfiguration.stringify())
    }

    /*
        ***************************************************
        Производные поля
        ***************************************************
    */



    function handleDerivedFieldCloseAndUpdate(bool){

        let derivedFields = new Map()
        let columnFields = pivotConfiguration.fieldsLists.columnFields;
        let rowFields = pivotConfiguration.fieldsLists.rowFields;
        let metricFields = pivotConfiguration.fieldsLists.metricFields;
        let filterFields = pivotConfiguration.fieldsLists.filterFields;

        let newPivotConfiguration = new PivotConfiguration(pivotConfiguration);

        function checkFieldsInConfig(fieldArr, derivedFields) {
            let arr = []

            fieldArr.forEach(fieldData => {
                if(!fieldData.original) {
                    const field = derivedFields.get(fieldData.id)
                    if(field) arr.push({...fieldData, fieldName: field, name: field})
                } else {
                    arr.push(fieldData)
                }
            })

            return arr
        }

        if(bool){
            dataHub.derivedFieldController.getAllDerivedFields(props.reportId, otherDerivedFields, ({ok, data}) => {
                data.forEach(item => derivedFields.set(item.id, item.name))

                if(columnFields.length > 0) {
                    newPivotConfiguration.fieldsLists.columnFields = checkFieldsInConfig(pivotConfiguration.fieldsLists.columnFields, derivedFields);
                }

                if(rowFields.length > 0) {
                    newPivotConfiguration.fieldsLists.rowFields = checkFieldsInConfig(pivotConfiguration.fieldsLists.rowFields, derivedFields);
                }

                if(metricFields.length > 0) {
                    newPivotConfiguration.fieldsLists.metricFields = checkFieldsInConfig(pivotConfiguration.fieldsLists.metricFields, derivedFields);
                }

                if(filterFields.length > 0) {
                    newPivotConfiguration.fieldsLists.filterFields = checkFieldsInConfig(pivotConfiguration.fieldsLists.filterFields, derivedFields);
                }

                configOlap.current.saveCurrentConfig(newPivotConfiguration.stringify(), ({ok}) => {
                    if(ok) {
                        resetDataLoader()
                    }
                })
            })
        }

        setCreateFieldDialogOpen(false);
    }

    function result(){ 
        return (
            <DragDropContext
                onDragEnd = {handleDragEnd}
            >
                <div className={styles.gragDropDiv}>
                    <Grid container className={styles.dragDropGridContainer}>
                        <Grid item xs={1} className={styles.gridForFilters}>
                            {fieldsVisibility &&
                                <PivotFieldsList
                                    name = "Фильтры"
                                    droppableId = "filterFields"
                                    fields = {pivotConfiguration.fieldsLists.filterFields}
                                    direction = "vertical"
                                    onClick = {(event, i) => handleFilterFieldButtonClick(event, i)}
                                    onContextClick = {(event, i) => handleFilterFieldButtonOffClick(event, i)}
                                    onRemoveFieldClick = {(i) => onRemoveFieldClick("filterFields", i)}
                                />
                            }
                        </Grid>
                        <Grid item xs={11} className={styles.gridForTools}>
                            <PivotTools
                                columnsMetricPlacement = {pivotConfiguration.columnsMetricPlacement}
                                onMetricPlacementChange = {handleSetMetricPlacement}
                                mergeMode = {pivotConfiguration.mergeMode}
                                fullScreen = {pivotFullScreen}
                                onMergeModeChange = {handleSetMergeMode}
                                onRestartReportClick = {() => props.onRestartReportClick()}
                                onViewTypeChange = {props.onViewTypeChange}
                                onFullScreen = {handleFullScreen}
                                fieldsVisibility = {fieldsVisibility}
                                onFieldsVisibility = {handleFieldsVisibility}
                                onConfigDialog = {handleSetConfigDialog}
                                onShareJobDialog = {handleShareJobDialog}
                                showShareToolBtn = {showShareToolBtn}
                                onSortingDialog = {handleShowSortingDialog}
                                onClearAllOlap = {handleClearAllOlap}
                                onExportToExcel = {handleExportToExcel}
                                onShowCreateFieldDialogue = {(open) => setCreateFieldDialogOpen(open)}
                            />
                            {fieldsVisibility &&
                                <PivotFieldsList
                                    name = "Неиспользуемые поля"
                                    droppableId = {onlyUnused ? "unusedFields": "allFields"}
                                    fields = {onlyUnused ? pivotConfiguration.fieldsLists.unusedFields: pivotConfiguration.fieldsLists.allFields}
                                    direction = "horizontal"
                                    onlyUnused = {onlyUnused}
                                    onOnlyUnusedClick = {handleOnlyUnusedClick}
                                />
                            }

                            {/* {fieldsVisibility &&
                                <PivotFieldsList
                                    name = "Производные поля"
                                    droppableId = {"derivedFields"}
                                    fields = {pivotConfiguration.fieldsLists.derivedFields}
                                    direction = "horizontal"
                                />
                            } */}

                            {fieldsVisibility &&
                                <PivotFieldsList
                                    name = "Столбцы"
                                    droppableId = "columnFields"
                                    fields = {pivotConfiguration.fieldsLists.columnFields}
                                    direction = "horizontal"
                                    onRemoveFieldClick = {(i) => onRemoveFieldClick("columnFields", i)}
                                />
                            }
                            {pivotConfiguration.columnsMetricPlacement && fieldsVisibility &&
                                <PivotFieldsList
                                    name = "Метрики"
                                    droppableId = "metricFields"
                                    fields = {pivotConfiguration.fieldsLists.metricFields}
                                    direction = "horizontal"
                                    onClick = {(event, i) => handleMetricFieldButtonClick(event, i)}
                                    onContextClick = {(event, i) => handleMetricFieldContextClick(event, i)}
                                    onChooseAggForMetric = {(funcName, index) => handleChooseAggForMetric(funcName, index)}
                                    onCloseAggModal = {handleAggModalClose}
                                    onRemoveFieldClick = {(i) => onRemoveFieldClick("metricFields", i)}
                                />
                            }
                            <TableRangeControl
                                orientation = "horizontal"
                                total = {tableData.totalColumns}
                                position = {pivotConfiguration.columnFrom}
                                count = {columnCount}
                                onPositionChange = {handleColumnFromChange}
                            />
                        </Grid>
                        <Grid item xs={1} className={styles.verticalListTableCell}>
                            <Box display="flex" flexDirection="row">
                                {fieldsVisibility &&
                                    <PivotFieldsList
                                        name = "Строки"
                                        droppableId = "rowFields"
                                        fields = {pivotConfiguration.fieldsLists.rowFields}
                                        direction = "vertical"
                                        onRemoveFieldClick = {(i) => onRemoveFieldClick("rowFields", i)}
                                    />
                                }
                                {!pivotConfiguration.columnsMetricPlacement && fieldsVisibility &&
                                    <PivotFieldsList
                                        name = "Метрики"
                                        droppableId = "metricFields"
                                        fields = {pivotConfiguration.fieldsLists.metricFields}
                                        direction = "vertical"
                                        onClick = {(event, i) => handleMetricFieldButtonClick(event, i)}
                                        onContextClick = {(event, i) => handleMetricFieldContextClick(event, i)}
                                        onChooseAggForMetric = {(funcName, index) => handleChooseAggForMetric(funcName, index)}
                                        onCloseAggModal = {handleAggModalClose}
                                        onRemoveFieldClick = {(i) => onRemoveFieldClick("metricFields", i)}
                                    />
                                }
                                <TableRangeControl
                                    orientation = "vertical"
                                    total = {tableData.totalRows}
                                    position = {pivotConfiguration.rowFrom}
                                    count = {rowCount}
                                    onPositionChange = {handleRowFromChange}
                                />
                            </Box>
                        </Grid>
                        <Measure
                            bounds
                            onResize={contentRect => {      

                                let cc = Math.ceil(contentRect.bounds.width/85) - 1 - pivotConfiguration.fieldsLists.rowFields.length ;
                                let rc = Math.ceil(contentRect.bounds.height/14) + 1 - pivotConfiguration.fieldsLists.columnFields.length;
                                if (tableSize.dimensions.height >= contentRect.bounds.height){
                                    rc = Math.max(3, Math.min(rc, rowCount));
                                    
                                }
                                if (tableSize.dimensions.width >= contentRect.bounds.width){
                                    cc = Math.min(cc, columnCount);
                                }

                                setTableSize({ dimensions: contentRect.bounds });

                                if ((cc !== columnCount || rc !== rowCount) && tableDataLoadStatus !==1){
                                    setColumnCount(cc);
                                    setRowCount(rc);
                                    // if(!dataProviderRef.current.changeWindow(pivotConfiguration.columnFrom, cc, pivotConfiguration.rowFrom, rc)){
                                    //     setTableDataLoadStatus(1);
                                    // }
                                
                                }
                                
                            }}
                        >
                            {({ measureRef }) => {
                                return (
                                        <Grid ref={measureRef}item xs={11} className={styles.gridMeasure}>
                                            {tableDataLoadStatus === 1 ?
                                                <CircularProgress/>
                                                    :
                                             tableDataLoadStatus === 2 ?
                                                <Typography>{tableDataLoadErrorMessage}</Typography>
                                                    :
                                                <PivotTable
                                                    onContextMenu={handleContextClick}
                                                    tableData = {tableData}
                                                    pivotConfiguration = {pivotConfiguration}
                                                    onDimensionValueFilter = {handleDimensionValueFilter}
                                                    onMetricValueFilter = {handleMetricValueFilter}
                                                    onChangeInnerTableSize = {handleChangeInnerTableSize}
                                                    sortingValues = {sortingValues}
                                                    onAddSorting = {handleAddMetricsSort}
                                                /> 
                                             }
                                        </Grid>
                            )}}
                                
                        </Measure>
                    </Grid>
                </div>
                <Menu
                    keepMounted
                    open={showContextMenu}
                    onClose={handleContextClose}
                    anchorReference="anchorPosition"
                    anchorPosition={
                    contextPosition.mouseY !== null && contextPosition.mouseX !== null
                        ? { top: contextPosition.mouseY, left: contextPosition.mouseX }
                        : undefined
                    }
                >   
                    {contextMenu}
                </Menu>
            </DragDropContext>
        )
    }

    return (
        <div  style={{display: 'flex', flex: 1}}>
            <DataLoader
                loadFunc = {dataHub.olapController.getJobMetadataExtended}
                loadParams = {[props.jobId, props.reportId]}
                onDataLoaded = {handleMetadataLoaded}
                key = {resetComponentKey}
            >
                {pivotFullScreen ?
                    <Dialog fullScreen open={pivotFullScreen} classes={{paper: styles.dialog}}>
                        {result()}
                    </Dialog>
                    :
                    <div style={{display: 'flex', flex: 1}} > 
                        {result()} 
                    </div>
                }
            </DataLoader>
            <PivotFilterModal
                open = {filterModalOpen}
                style = {filterModalStyle}
                jobId = {props.jobId}
                field = {pivotConfiguration.fieldsLists.filterFields[filterFieldIndex]}
                fieldsLists = {pivotConfiguration.fieldsLists}
                filterGroup = {oldAndNewConfiguration.current.newConfiguration?.filterGroup} //{pivotConfiguration.filterGroup}
                metricFilterGroup = {pivotConfiguration.metricFilterGroup}
                onClose = {handleFilterModalClose}
                onOK = {handleSetFilterValue}
            />
            <PivotMetricModal
                open = {metricModalOpen}
                style = {filterModalStyle}
                jobId = {props.jobId}
                field = {pivotConfiguration.fieldsLists.metricFields[metricFieldIndex]}
                fieldsLists = {pivotConfiguration.fieldsLists}
                onClose = {handleMetricModalClose}
                onOK = {handleRenameMetric}
            />
            
            {formattingDialog && 
                <FormattingDialog
                    open = {formattingDialog}
                    cellData = {contextItemsValue}
                    onCancel = {() => setFormattingDialog(false)}
                    onSaveFormatting = {handleChangeFieldDataStyle}
                />
            }
            {conditionalFormattingDialog && 
                <ConditionalFormattingDialog
                    open = {conditionalFormattingDialog}
                    cellData = {contextItemsValue}
                    onCancel = {() => setConditionalFormattingDialog(false)}
                    onSaveConditionalFormatting = {handleAddConditionalFormatting}
                />
            }
            {sortingDialog && 
                <SortingDialog
                    open = {sortingDialog}
                    cellData = {contextItemsValue}
                    sortingValues = {sortingValues}
                    sortingDialogAfterTools = {sortingDialogAfterTools}
                    onSave = {handleAddMetricsSort}
                    onCancel = {() => handleShowSortingDialog(false)}
                    onDelete = {handleDeleteMetricsSort}
                />
            }
            {showConfigDialog &&
                <ConfigDialog
                    open = {showConfigDialog}
                    configs = {avaibleConfigs}
                    onCancel = {handleSetConfigDialog}
                    onDelete = {handleDeleteConfig}
                    onChooseConfig = {handleLoadCertainConfig}
                    onMakeDefault = {handleSaveConfigByDefault}
                    onChangeGeneralAccess = {handleSaveGeneralAccess}
                />
            }
            {showConfigSaveDialog &&
                <ConfigSaveDialog
                    open = {showConfigSaveDialog}
                    configs = {avaibleConfigs}
                    isReportDeveloper = {isReportDeveloper.current}
                    onCancel = {handleSetConfigDialog}
                    onSave = {handleSaveConfig}
                />
            }
            {shareJobDialogOpen && 
                <ShareJobDialog
                    open = {shareJobDialogOpen}
                    jobOwnerName = {props.jobOwnerName}
                    users = {usersForShareJobDialog}
                    usersWithAccess = {usersWithAccessForShareJobDialog}
                    onCancel = {handleShareJobDialog}
                    onSave = {handleSaveShareJob}
                />
            }
            {createFieldDialogOpen &&
                <DerivedFieldDialog
                    open = {createFieldDialogOpen}
                    jobId = {props.jobId}
                    reportId = {props.reportId}
                    isReportDeveloper = {isReportDeveloper.current}
                    otherDerivedFields = {otherDerivedFields}
                    onCancel = {(bool) => handleDerivedFieldCloseAndUpdate(bool)}
                />
            }
        </div>
    )     
}

const mapStateToProps = state => {
    return {
        aggModalParams: state.olap.aggModalParams,
    }
}

const mapDispatchToProps = {
    showAlertDialog, 
    hideAlertDialog,
    setAggModalParams
}

export default connect(mapStateToProps, mapDispatchToProps)(PivotPanel);