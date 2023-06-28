import React, {useState, useRef, useEffect} from 'react';

// components
import TextField from '@material-ui/core/TextField';
import IconButton from '@material-ui/core/IconButton';
import ClearIcon from '@material-ui/icons/Clear';
import Tooltip from '@material-ui/core/Tooltip';
import InputAdornment from '@material-ui/core/InputAdornment';
import ReorderIcon from '@material-ui/icons/Reorder';

// local
import InvalidValuesView from './InvalidValuesView'
import FilterStatus from './FilterStatus'
import SeparatorMenu from './SeparatorMenu'

// styles
import { ValueListCSS, AllFiltersCSS } from './FiltersCSS'
import dataHub from 'ajax/DataHub';

import {buildTextFromLastValues, getCodeFieldId} from "utils/reportFiltersFunctions";
import { FormControl, InputLabel, MenuItem, Select } from '@material-ui/core';

/**
 * @callback onChangeFilterValue
 */
/**
 * Компонент настройки фильтра со списком значений у отчета
 * @param {Object} props - свойства компонента
 * @param {Object} props.filterData - данные фильтра (объект ответа от сервиса)
 * @param {Object} props.lastFilterValue - объект со значениями фильтра из последнего запуска (как приходит от сервиса)
 * @param {boolean} props.toggleClearFilter - при изменении значения данного свойства требуется очистить выбор в фильтре
 * @param {boolean} props.unbounded - выбор не ограничен справочником (unbounded = true - значит введённые значения не проверяются по справочнику)
 * @param {onChangeFilterValue} props.onChangeFilterValue - function(filterValue) - callback для передачи значения изменившегося параметра фильтра
 */
function ValueList(props){

    const classes = ValueListCSS();
    const cls = AllFiltersCSS();
    const mandatory = props.filterData.mandatory;
    const toggleFilter = useRef(props.toggleClearFilter);
    const timer = useRef(0);
    const requestId = useRef("")
    const [showInvalidValues, setShowInvalidValues] = useState(false)
    const [invalidValues, setInvalidValues] = useState([])
    const [operationTypeDisabled, setOperationTypeDisabled] = useState(true);
    const [operationType, setOperationType] = useState('IN_LIST');

    /*
        Вычисляем поле фильтра с типом CODE_FIELD
    */
    const codeFieldId = getCodeFieldId(props.filterData);

    const filterName = props.filterData.mandatory ? <span>{props.filterData.name}<i className={cls.mandatory}>*</i></span> : <span>{props.filterData.name}</span>;

    const separatorsArray = [
        {name: 'Точка с запятой',  value: ';', checked: true},
        {name: 'Запятая',  value: ',', checked: true},
        {name: 'Пробел',  value: ' ', checked: true},
        {name: 'Табуляция',  value: '\\t', checked: true},
        {name: 'Перенос строки',  value: '\\n', checked: true},
    ]
    const [textValue, setTextValue] = useState(buildTextFromLastValues('VALUE_LIST', props.lastFilterValue, codeFieldId, ';', props.filterData.fields));
    const [checkStatus, setCheckStatus] = useState(mandatory && !textValue ? "error" : 'success')
    const [separators, setSeparators] = useState(separatorsArray)


    /*
        Вычислить тип операции по предыдущему значению
    */
    useEffect(() => {

        const type = props.filterData.filterReportModes;
        const externalValue = props.externalFiltersValue ? props.externalFiltersValue[props.filterData.code] : null

        if (externalValue) {
            handleTextChanged(externalValue.value && Array.isArray(JSON.parse(externalValue.value)) ? JSON.parse(externalValue.value).join(';') : "")
            if (type.length < 1 || type.length === 2){
                setOperationTypeDisabled(false)
                setOperationType(externalValue.operationType === 'IN_LIST' ? 'IN_LIST' : externalValue.operationType === 'NOT_IN_LIST' ? 'NOT_IN_LIST' : 'IN_LIST')
            } else {
                setOperationTypeDisabled(true)
                return type.length > 0 ? setOperationType([...type]) : setOperationType('IN_LIST');
            }

        } else {
            if (type.length < 1 || type.length === 2){
                setOperationTypeDisabled(false)
                setOperationType('IN_LIST')
            } else {
                setOperationTypeDisabled(true)
                return type.length > 0 ? setOperationType([...type]) : setOperationType('IN_LIST');
            }
        }
        
    }, [props.filterData.filterReportModes, props.externalFiltersValue, props.filterData.code]) // eslint-disable-line

    function getRegexp() {
        let s = ""
        let s2 = ""

        let tb = false
        let space = false
        let n = false

        for(let sp of separators) {
            if (sp.checked && sp.name === "Точка с запятой") {
                s += sp.value
                s2 += sp.value
            }
            if (sp.checked && sp.name === "Запятая") {
                s += sp.value
                s2 += sp.value
            }
            if (sp.checked && sp.name === "Пробел") {
                s += sp.value
                space = true
            }
            if (sp.checked && sp.name === "Табуляция") {
                s += sp.value
                tb = true
            }
            if (sp.checked && sp.name === "Перенос строки") {
                s += sp.value
                n = true
            }
        }
        if (space && tb && n) s = s2 + "\\s"
        
        const exp = new RegExp(`[${s}]+(?=(?:[^"]*"[^"]*")*[^"]*$)`, 'g')
        return exp
    }

    function handleTextChanged(newText){
        let status = props.unbounded ? "success" : "waiting"; 
        if (newText === ""){
            setCheckStatus(mandatory ? "error" : 'success')
        }
        else {
            setCheckStatus(status);
        }

        let re1 = getRegexp()

        let codeList = newText.split(re1).map(elem => elem.trim().replace(/"/g,""))

        for( let i = codeList.length; i--;){
            if ( codeList[i].trim().length === 0) codeList.splice(i, 1); /* удаление пустых значений */
        }

        let values = [];
        for(let code of codeList){
            values.push({
                fieldId: codeFieldId,
                value: code
            });
        }
        let parameters = [{
            values : values
        }];

        let isSelected = (codeList.length > 0) ? true : false; 

        if (isSelected) {
            status = Boolean(props.filterData.maxCountItems) && values.length > props.filterData.maxCountItems ? "limit" : status;
            setCheckStatus(status);

            props.onChangeFilterValue(
                {
                    filterId : props.filterData.id,
                    operationType: operationType === 'IN_LIST' ? 'IS_IN_LIST' : 'IS_NOT_IN_LIST',
                    validation: status,
                    parameters: parameters
                }
            );
            
            if(!props.unbounded && status !=='limit'){
                if (timer.current > 0){
                    clearInterval(timer.current); // удаляем предыдущий таймер
                }
                timer.current = setTimeout(() => {
                    requestId.current = dataHub.filterReportController.checkValues(props.filterData.id, values, m => handleCheckValues(operationType, parameters, m))
                }, 1000);    
            }
        }
        else {
            props.onChangeFilterValue({
                filterId : props.filterData.id,
                operationType: operationType === 'IN_LIST' ? 'IS_IN_LIST' : 'IS_NOT_IN_LIST',
                validation: mandatory ? "error" : 'success',
                parameters: []
            });

            if(!props.unbounded){
                if (timer.current > 0){
                    clearInterval(timer.current); // удаляем предыдущий таймер
                }
                timer.current = setTimeout(() => {
                    setCheckStatus(mandatory ? "error" : 'success')
                }, 1000);    
            }

        }
        setTextValue(newText)
        
    }

    function handleCheckValues(operationType, parameters, resp){
        if (requestId.current === resp.requestId){
            let validation = "error"
            if (resp.ok){
                if (resp.data.values.length){
                    setInvalidValues(resp.data.values)
                }
                else {
                    validation = "success"
                }
            }
            props.onChangeFilterValue(
                {
                    filterId : props.filterData.id,
                    operationType: operationType === 'IN_LIST' ? 'IS_IN_LIST' : 'IS_NOT_IN_LIST',
                    validation,
                    parameters,
                }
            );
            setCheckStatus(validation);
        }
    }

    function handleClearClick(){
        setTextValue("");
        handleTextChanged("");
    }

    function handleChangeSeparators(sArr) {
        setSeparators(sArr)
        handleTextChanged(textValue)
    }

    useEffect(() => {
        if (props.toggleClearFilter !== toggleFilter.current){
            toggleFilter.current = props.toggleClearFilter;
            handleTextChanged("");
        }
    })

    function handleChangeOperationType(type) {
        props.onChangeFilterValue(
            {
                filterId: props.filterData.id,
                operationType: type === 'IN_LIST' ? 'IS_IN_LIST' : 'IS_NOT_IN_LIST',
                validation: mandatory ? "error" : 'success',
                parameters: props.lastFilterValue?.parameters || [],
            }
        );
        setOperationType(type);
    }

    return (
        <div className={classes.valueListWrapper}>
            <FormControl variant="outlined" className={classes.typeSelect} disabled={operationTypeDisabled}>
                <InputLabel id="devRepFiltersSelect">Операция</InputLabel>
                <Select
                    label="Операция"
                    labelId="devRepFiltersSelect"
                    id="devRepFiltersSelect"
                    value={operationType}
                    onChange={e => handleChangeOperationType(e.target.value)}
                >
                    <MenuItem value={'IN_LIST'}>В списке</MenuItem>
                    <MenuItem value={'NOT_IN_LIST'}>Не в списке</MenuItem>
                </Select>
            </FormControl>
            <TextField
                size = "small"
                className={classes.textField}
                id="input-with-icon-textfield"
                label={filterName}
                helperText={props.filterData.maxCountItems>0 ? 'Допустимое кол-во значений: ' + props.filterData.maxCountItems: null}
                value={textValue}
                variant="outlined"
                multiline
                onChange={e => handleTextChanged(e.target.value)}
                InputProps={{
                    endAdornment: (
                        <InputAdornment position="end" className={classes.topInputAdornment}>
                            <SeparatorMenu
                                title ={"Разделители"}
                                separators={separators}
                                onChangeSeparators={handleChangeSeparators}
                            />
                            
                            <Tooltip title="Очистить фильтр" placement="top">
                                <IconButton 
                                    aria-label="clear"
                                    //color='primary'
                                    size='small'
                                    onClick={handleClearClick}
                                >
                                    <ClearIcon fontSize='small' />
                                </IconButton>
                            </Tooltip>
                           
                            <FilterStatus status={checkStatus} />
                          
                            {
                                checkStatus !== 'success' && !!invalidValues.length &&
                               
                                    <Tooltip title="Показать некорректные значения" placement="top">
                                        <IconButton 
                                            aria-label="clear"
                                            size='small'
                                            onClick={() => setShowInvalidValues(true)}
                                        >
                                            <ReorderIcon fontSize='small' />
                                        </IconButton>
                                    </Tooltip>
                               
                            }
                        </InputAdornment>
                       
                    ),
                }}
            />
            {
                showInvalidValues &&
                <InvalidValuesView 
                    values={invalidValues}
                    onClose={() => {setShowInvalidValues(false); /*setInvalidValues([]);*/}}
                />
            }
            
        </div>
    );
}

export default ValueList;