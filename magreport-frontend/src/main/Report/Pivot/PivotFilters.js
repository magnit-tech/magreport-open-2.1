import React, { useState } from 'react';
import clsx from 'clsx';
import FormControl from '@material-ui/core/FormControl';
import Select from '@material-ui/core/Select';
import MenuItem from '@material-ui/core/MenuItem';
import Link from '@material-ui/core/Link';
import Typography from '@material-ui/core/Typography';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Checkbox from '@material-ui/core/Checkbox';
// local
import PivotFiltersItem from './PivotFiltersItem';
import {PivotCSS} from './PivotCSS';
import {FilterObject} from './dataobjects/FilterObject';

/**
 * 
 * @param {*} props.jobId - id задания
 * @param {*} props.field - поле с фильтрацией
 * @param {*} props.fieldsLists - срисок полей (нужен для фильтрации списка значений)
 * @param {*} props.filterGroup - группа фильтров для измерений
 * @param {*} props.metricFilterGroup - группа фильтров для метрик
 * @param {*} props.onChangeValues - колбэк при изменении значений фильтра
 * 
 */

function PivotFilters(props){

    const classes = PivotCSS();
    const filterType = [
        //source: d - dimension, m - metric

        {id: "EQUALS",      name: "Равно",    type: ['STRING', 'INTEGER', 'DOUBLE', 'DATE', 'TIMESTAMP'], source: 'd/m'},
        {id: "IN_LIST",     name: "В списке", type: ['STRING', 'INTEGER', 'DATE', 'TIMESTAMP'], source: 'd'},
        {id: "CONTAINS_CI", name: "Содержит (без учёта регистра)", type: ['STRING'], source: 'd'},
        {id: "CONTAINS_CS", name: "Содержит (с учётом регистра)",  type: ['STRING'], source: 'd'},
        {id: "GREATER" ,    name: "Больше", type: ['INTEGER', 'DOUBLE', 'DATE'/*, 'TIMESTAMP'*/], source: 'd/m'},
        {id: "LESSER" ,     name: "Меньше", type: ['INTEGER', 'DOUBLE', 'DATE'/*, 'TIMESTAMP'*/], source: 'd/m'},
        {id: "GREATER_OR_EQUALS" , name: "Больше или равно", type: ['INTEGER', 'DOUBLE', 'DATE'/*, 'TIMESTAMP'*/], source: 'd/m'},
        {id: "LESSER_OR_EQUALS" ,  name: "Меньше или равно", type: ['INTEGER', 'DOUBLE', 'DATE'/*, 'TIMESTAMP'*/], source: 'd/m'},
        {id: "BETWEEN",     name: "Между (включительно)", type: ['INTEGER', 'DOUBLE'/*, 'DATE', 'TIMESTAMP'*/], source: 'd/m'},
        {id: "BLANK",       name: "Пусто", type: ['STRING', 'INTEGER', 'DOUBLE', 'DATE', 'TIMESTAMP'], source: 'd/m'}
    ];

    const [filterObject, setFilterObject] = useState(new FilterObject({
        field: props.field ? {fieldId: props.field.fieldId, fieldType: props.field.original ? 'REPORT_FIELD' : 'DERIVED_FIELD'} : {},
        filterType: props.field?.filter?.filterType,
        invertResult: props.field?.filter?.invertResult,
        values: props.field?.filter?.values,
        canRounding: props.field?.filter?.canRounding ?? false
    }));

    function handleChangeFilterValues(value){
        let filter = new FilterObject(filterObject);
        filter.setValues(value);
        setFilterObject(filter);
        props.onChangeValues(filter);
    }

    function handleChangeFilterType(filterType){
        let filter = new FilterObject(filterObject);
        filter.setFilterType(filterType);
        if (filterType === "BLANK") {
            filter.setValues([]);
        }
        setFilterObject(filter);
        props.onChangeValues(filter);
    }

    function handleChangeInvertResult(){
        let filter = new FilterObject(filterObject);
        filter.setInvertResult(!filter.invertResult);
        setFilterObject(filter);
        props.onChangeValues(filter);
    }

    function handleCanRounding(canR){
        let filter = new FilterObject(filterObject);
        filter.setCanRounding(canR);
        setFilterObject(filter);
        props.onChangeValues(filter);
    }

    return (
        <div className={classes.pivotFiltersRoot}>
            <div className={classes.divLinkNo}> 
                <Typography>
                    <Link href="#" 
                        className ={clsx({
                            [classes.invertResult]: filterObject.invertResult,
                            [classes.notInvertResult]: (filterObject.invertResult === false)
                        })}  
                        onClick={handleChangeInvertResult}
                    >
                        НЕ
                    </Link>
                </Typography>
                <FormControl className={classes.formControl} size="small">
                    <Select
                        value = {filterObject.filterType}
                        children={ filterType.filter(i => i.type.findIndex(
                                it => (it === props.field?.type && props.field?.aggFuncName === '') 
                                || (it === props.field?.dataType && props.field?.aggFuncName !== '')) !== -1 
                            && ((props.field?.aggFuncName === '' && i.source === 'd' )
                                || (props.field?.aggFuncName !== '' && i.source === 'm')
                                || i.source === 'd/m')).map((value, index) =>
                            <MenuItem key={index} value={value.id}>{value.name}</MenuItem>) 
                        }
                        onChange={event =>  handleChangeFilterType(event.target.value)}
                        inputProps={{
                            name: 'Тип фильтра',
                            id: 'filter-type',
                        }}
                    />
                </FormControl>
            </div>
            {props.field?.aggFuncName === "" && props.field?.type ===  'DOUBLE' && filterObject.filterType !== 'BLANK' &&
                <FormControlLabel
                    control={<Checkbox checked={filterObject.canRounding} onChange={(e) => handleCanRounding(e.target.checked)} name="canRounding"/>}
                    label="Использовать округление"
                />
            }
            <PivotFiltersItem
                jobId = {props.jobId}
                field = {props.field}
                fieldsLists = {props.fieldsLists}
                filterObject = {filterObject}
                filterGroup = {props.filterGroup}
                metricFilterGroup = {props.metricFilterGroup}
                filterType = {filterObject.filterType}
                onChangeValues={handleChangeFilterValues}
            />
        </div>
    )
}  

export default PivotFilters