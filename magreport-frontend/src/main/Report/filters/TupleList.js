import React, {useState, useRef, useEffect} from 'react';

// components
import TextField from '@material-ui/core/TextField';
import IconButton from '@material-ui/core/IconButton';
import ClearIcon from '@material-ui/icons/Clear';
import Tooltip from '@material-ui/core/Tooltip';
import InputAdornment from '@material-ui/core/InputAdornment';
import ReorderIcon from '@material-ui/icons/Reorder';
import PageviewIcon from '@material-ui/icons/Pageview';

// local
import InvalidValuesView from './InvalidValuesView';
import FilterStatus from './FilterStatus';
import SeparatorMenu from './SeparatorMenu';
import TupleListDialog from './TupleListDialog';

// styles
import { ValueListCSS, AllFiltersCSS } from './FiltersCSS';

import {getCodeFieldsIdArr} from "utils/reportFiltersFunctions";

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
function TupleList(props){

    const classes = ValueListCSS();
    const cls = AllFiltersCSS();
    const fieldsCnt = props.filterData.fields.filter(i=> i.type === 'CODE_FIELD').length;
    const mandatory = props.filterData.mandatory;
    let operationType = getOperationType(props.lastFilterValue);
    const toggleFilter = useRef(props.toggleClearFilter);
    const timer = useRef(0);
    const [showInvalidValues, setShowInvalidValues] = useState(false);
    const [showDialog, setShowDialog] = useState(false);
    const [invalidValues, setInvalidValues] = useState([]);

    /*
        Вычисляем поле фильтра с типом CODE_FIELD
    */
    const codeFieldsIdArr = getCodeFieldsIdArr(props.filterData);

    const filterName = props.filterData.mandatory ? <span>{props.filterData.name}<i className={cls.mandatory}>*</i></span> : <span>{props.filterData.name}</span>;

    const separatorsArray = [
        {name: 'Точка с запятой',  value: ';', checked: false},
        {name: 'Запятая',  value: ',', checked: false},
        {name: 'Пробел',  value: ' ', checked: false},
        {name: 'Табуляция',  value: '\\t', checked: false},
        {name: 'Перенос строки',  value: '\\n', checked: false},
        {name: 'Тире', value: '-', checked: true}
    ];
    const wordSeparatorArray = [
        {name: 'Точка с запятой',  value: ';', checked: true},
        {name: 'Запятая',  value: ',', checked: true},
        {name: 'Пробел',  value: ' ', checked: true},
        {name: 'Табуляция',  value: '\\t', checked: true},
        {name: 'Перенос строки',  value: '\\n', checked: true},
        {name: 'Тире', value: '-', checked: false}
    ];

    const [textValue, setTextValue] = useState('');
    const [rows, setRows] = useState(null)
    const [checkStatus, setCheckStatus] = useState(mandatory && !textValue ? "error" : 'success')
    const [separators, setSeparators] = useState(separatorsArray);
    const [wordSeparators, setWordSeparators] = useState(wordSeparatorArray);

    // Задаём значения по-умолчанию
    useEffect(() => {
        if(!props.externalFiltersValue) {
            setTextValue(buildTextFromLastValues(props.lastFilterValue?.parameters, '-',';'));
            setRows(props.lastFilterValue?.parameters)
        }
    }, []) // eslint-disable-line

    useEffect(() => {
        if (props.toggleClearFilter !== toggleFilter.current){
            toggleFilter.current = props.toggleClearFilter;
            handleTextChanged("");
        }
    })

    function buildTextFromLastValues(params=[], wordSep, sep ) {
        let textValue = "";
        
        for (let p of params) {
            let i =0;
            for (let v of p.values){
                textValue = (i>0 ? textValue + wordSep : textValue) + v.value;
                i=1;
            }
            textValue = textValue + sep;  
        }
        return textValue
    }

    /*
        Вычислить тип операции по предыдущему значению
    */
    function getOperationType(lastParameters){
        let operationType = 'IS_EQUAL';
        if(lastParameters){
            operationType = lastParameters.operationType;
        }
        return operationType;
    }

    function getRegexp(sep) {
        let s = ""
        let s2 = ""

        let tb = false
        let space = false
        let n = false
        
        for(let sp of sep) {
            if (sp.checked && sp.name === "Точка с запятой") {
                s += sp.value
                s2 += sp.value
            }
            if (sp.checked && sp.name === "Запятая") {
                s += sp.value
                s2 += sp.value
            }
            if (sp.checked && sp.name === "Тире") {
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

        let status = "waiting"; 
        if (newText === ""){
            setCheckStatus(mandatory ? "error" : 'success')
        }
        else {
            setCheckStatus(status);
        }

        let parameters =[];

        let re1 = getRegexp(wordSeparators)

        let strList = newText.split(re1).map(elem => elem.trim().replace(/"/g,""));
  
        for( let i = strList.length; i--;){
            if ( strList[i].trim().length === 0) strList.splice(i, 1); /* удаление пустых значений */
        }
        
        for (let str of strList) {
            let values = [];
            let re2 = getRegexp(separators);
            let codeList = str.split(re2).map(elem => elem.trim().replace(/"/g,""));

            for( let i = codeList.length; i--;){
                if ( codeList[i].trim().length === 0) codeList.splice(i, 1); /* удаление пустых значений */
            }

            codeList.forEach((item, index) => {
                values.push({
                    fieldId: codeFieldsIdArr[index],
                    value: item
                });
                
            });
            parameters.push({values : values})
        }

        let invalids =[];
        parameters.forEach(i => {
            if (i.values.length !== fieldsCnt) {
                invalids.push( i.values.map(i => i.value).join('-'));
                setInvalidValues(invalids);
                status='error'
                setCheckStatus(status);
            }
        })
        if (!invalids.length) {status = "success"; setCheckStatus(status); }

        let isSelected = (strList.length > 0) ? true : false; 

        if (isSelected) {
            
            if (timer.current > 0){
                clearInterval(timer.current); // удаляем предыдущий таймер
            }
            timer.current = setTimeout(() => {
                props.onChangeFilterValue(
                    {
                        filterId : props.filterData.id,
                        operationType: operationType,
                        validation: status,
                        parameters: parameters
                    }
                );
            }, 1000);    
            
        }
        else {
            parameters = [];
            status = mandatory ? "error" : 'success';
            props.onChangeFilterValue({
                filterId : props.filterData.id,
                operationType: operationType,
                validation: status,
                parameters: parameters
            });
        }
        
        setTextValue(newText);
        setRows(parameters);
        
    }

    function handleClearClick(){
        setTextValue("");
        handleTextChanged("");
    }

    function handleChangeSeparators(sArr) {
        setSeparators(sArr)
        handleTextChanged(textValue)
    }

    function handleChangeWordSeparators(sArr) {
        setWordSeparators(sArr)
        handleTextChanged(textValue)
    }

    function handleSave(newValue) {    
        handleTextChanged(buildTextFromLastValues(newValue, '-',';'));
        setShowDialog(false);
    }

    return (
        <div>
            <TextField
                size = "small"
                className={classes.textField}
                id="input-with-icon-textfield"
                label={filterName}
                value={textValue}
                variant="outlined"
                multiline
                onChange={e => handleTextChanged(e.target.value)}
                InputProps={{
                    endAdornment: (
                        <InputAdornment position="end" className={classes.topInputAdornment}>
                            <SeparatorMenu
                                title={"Разделитель значений"} 
                                separators={separators}
                                onChangeSeparators={handleChangeSeparators}
                            />
                            <SeparatorMenu
                                title={"Разделитель кортежей"}
                                separators={wordSeparators}
                                onChangeSeparators={handleChangeWordSeparators}
                            />
                            <Tooltip title="Показать значения" placement="top">
                                <IconButton 
                                    aria-label="show"
                                    size='small'
                                    onClick={() => setShowDialog(true)}
                                >
                                    <PageviewIcon fontSize='small' />
                                </IconButton>
                            </Tooltip>

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
                                checkStatus === 'error' && !!invalidValues.length &&
                               
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
                    onClose={() => {setShowInvalidValues(false)}}
                />
            }
            <TupleListDialog
                isOpen={showDialog}
                columns={props.filterData.fields}
                rows={rows}
                onSave={handleSave}
                onClose={()=> setShowDialog(false)}
            />
            
        </div>
    );
}

export default TupleList;