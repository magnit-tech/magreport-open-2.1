import React, { useState } from 'react';

// components
import Paper from '@material-ui/core/Paper';
import DeleteIcon from '@material-ui/icons/Delete';
import IconButton from '@material-ui/core/IconButton';
import Collapse from '@material-ui/core/Collapse';
import ArrowUpwardIcon from '@material-ui/icons/ArrowUpward';
import ArrowDownwardIcon from '@material-ui/icons/ArrowDownward';
import Tooltip from '@material-ui/core/Tooltip';
import ReportIcon from '@material-ui/icons/Report';
import ReportOffIcon from '@material-ui/icons/ReportOff';
import VisibilityIcon from '@material-ui/icons/Visibility';
import VisibilityOffIcon from '@material-ui/icons/VisibilityOff';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import ExpandLessIcon from '@material-ui/icons/ExpandLess';
import Typography from '@material-ui/core/Typography';
import Divider from '@material-ui/core/Divider';
import WarningIcon from '@material-ui/icons/Warning';
import InputAdornment from '@material-ui/core/InputAdornment';
import FlagIcon from '@material-ui/icons/Flag';

// local
import DesignerSelectField from '../../Designer/DesignerSelectField';
import DesignerTextField from '../../Designer/DesignerTextField';

// styles
import {ReportFiltersItemCSS} from '../../Designer/DesignerCSS';
import { FormControl, InputLabel, MenuItem, Select } from '@material-ui/core';
/**
 * @callback onChange
 * @param {Object} filterItem
 */
/**
 * @callback onDeleteItem
 * @param {Object} event
 */
/**
 * @callback onChangeOrdinal
 * @param {Object} event
 */
/**
 * Компонент редактирования фильтра отчета
 * @param {Object} props - свойства компонента
 * @param {Number} props.index - индекс в массиве
 * @param {Boolean} props.disabled - признак блокировки
 * @param {Object} props.filterItem - объект фильтра
 * @param {Array} props.reportFields - поля отчёта
 * @param {onChange} props.onChange - function(filterItem) - изменение фильтра
 * @param {onDeleteItem} props.onDeleteItem - function(event) удаление фильтра
 * @param {onChangeOrdinal} props.onChangeOrdinal - function(event) изменение порядка
 */
export default function ReportFilterItem({index, disabled, filterItem, reportFields, datasetType, onChange, onDeleteItem, onChangeOrdinal}){

    const classes = ReportFiltersItemCSS()

    const [operationType, setOperationType] = useState(getOperationType(filterItem.filterReportModes));

    function getOperationType(type){
        if (type.length < 1 || type.length === 2){
            return 'ALL_IN_LIST';
        } else {
            return type.length > 0 ? [...type] : 'IN_LIST';
        }
    }

    const handleDeleteItems = event => {
        onDeleteItem(event)
    }

    const handleChange = (itemIndex, item, value) => {
        let fieldObj = {
            description: `Маппинг поля ${item.name} экземпляра фильтра в поле отчета с id ${value}`,
            expand: item.expand,
            filterInstanceFieldId: item.filterInstanceFieldId,
            id: item.id,
            name: item.name,
            level: item.level,
            reportFieldId: value,
            ordinal: item.ordinal,
            type: item.type,
            valid: true,
            maxCountItems: null
        }
        let arr = [...filterItem.fields]
        arr.splice(itemIndex, 1, fieldObj)

        // Дикая залепа (костыль) для фильтра DATE_RANGE, RANGE :-)
        if(['DATE_RANGE', 'RANGE'].includes(filterItem.type)){
            for(let i in arr){
                if(arr[i].type === 'ID_FIELD' && arr[i].level > 1){
                    arr[i] = {
                        ...fieldObj,
                        filterInstanceFieldId : arr[i].filterInstanceFieldId,
                        level : arr[i].level
                    }
                }
            }
        }

        let filterInstanceObj = {...filterItem, fields: arr}
        onChange(filterInstanceObj)
    }

    const handleChangeMetadata = (field, value) => {
        onChange({...filterItem, [field]: value})
    }

    function handleChangeOperationType(type) {
        let filterReportModes = []

        if(type === 'IN_LIST' || type === 'NOT_IN_LIST') {
            filterReportModes = [type]
        } else {
            filterReportModes = ['IN_LIST', 'NOT_IN_LIST']
        }

        setOperationType(type);
        handleChangeMetadata('filterReportModes', filterReportModes)
    }

    let isValid = !!filterItem.code && !!filterItem.name && !filterItem.errors && (filterItem.hasOwnProperty('valid') ?  filterItem.valid: true)
    for (let f of filterItem.fields){
        if (f.error){
            isValid = false
            break
        }
    }

    return (
        <Paper className={classes.root} elevation={3} >
            <div className={classes.devRepFilterHeader}> 
                <Tooltip title = {!!filterItem.hidden ? "Скрытый" : "Видимый"}>
                    <IconButton className={classes.btn} onClick={event => handleChangeMetadata("hidden", !filterItem.hidden)}>
                        {!!filterItem.hidden ?<VisibilityOffIcon color="secondary"/> : <VisibilityIcon color="primary"/> }
                    </IconButton>
                    
                </Tooltip>
                <Tooltip title = {!!filterItem.mandatory ? "Обязательный" : "Не обязательный"}>
                    <IconButton className={classes.btn} onClick={event => handleChangeMetadata("mandatory", !filterItem.mandatory)}>
                        {!!filterItem.mandatory ? <ReportIcon color="secondary"/> : <ReportOffIcon color="primary"/> }
                    </IconButton>
                </Tooltip>
                <div className={classes.devRepFilterItemNameBlock}>
                    <Typography variant='h5'> {filterItem.name|| "Название не указано"||" "} </Typography>
                    <Typography varian='body' color='textSecondary'> {filterItem.description + ' (id: ' + filterItem.filterInstanceId + ')'}</Typography>
                </div> 
                {!isValid && 
                    <Tooltip 
                        title={
                            <React.Fragment>
                                <Typography key={index} color="inherit" style={{fontSize:'1em'}}>Проверьте настройки отчета</Typography>
                                { filterItem.errors && Object.values(filterItem.errors).map((e, index) => 
                                        <Typography key={index} color="inherit" style={{fontSize:'1em'}}>{e}</Typography>
                                )}
                            </React.Fragment>
                        }
                        placement="top"
                    >
                        <WarningIcon fontSize='small' color="secondary"/>
                    </Tooltip>}
                <IconButton
                    className={classes.btn}
                    aria-label="delete"
                    color="primary"
                    onClick={event => {handleDeleteItems(event)}}
                >
                    <DeleteIcon fontSize='small' />
                </IconButton>
                
                <IconButton
                    aria-label="up"
                    color="primary"
                    disabled={index === 0} 
                    className={classes.btn}
                    onClick={event=>onChangeOrdinal(index, 'up', event)}
                >
                    <ArrowUpwardIcon fontSize='small' />
                </IconButton>

                <IconButton
                    className={classes.btn}
                    aria-label="down"
                    color="primary"
                    disabled={disabled}
                    onClick={event=>onChangeOrdinal(index, 'down', event)}
                >
                    <ArrowDownwardIcon fontSize='small' />
                </IconButton>

                <IconButton
                    className={classes.btn}
                    onClick={()=>{handleChangeMetadata('expandedCollapse', !filterItem.expandedCollapse)}}
                >
                    {!filterItem.expandedCollapse ? <ExpandLessIcon fontSize='small'/>:<ExpandMoreIcon fontSize='small'/>}
                </IconButton>
            </div>

            <Collapse  in={!filterItem.expandedCollapse} timeout="auto" unmountOnExit className={classes.devRepFiltersClps}>
                <div className={classes.nameAndCode}>
                    <div className={classes.devRepFiltersName}>
                        <DesignerTextField
                            label = "Название фильтра в отчете"
                            value = {filterItem.name} 
                            onChange = {data => {handleChangeMetadata('name', data)}}
                            displayBlock
                            fullWidth
                            size="small"
                            error = {!filterItem.name}
                        />
                    </div>
                    <div className={classes.devRepFilters}>
                        <DesignerTextField
                            label = "Код фильтра"
                            value = {filterItem.code} 
                            onChange = {data => {handleChangeMetadata('code', data)}}
                            displayBlock
                            size="small"
                            error = {!filterItem.code || (filterItem.errors && filterItem.errors.code) }
                        />

                        {filterItem.type === 'VALUE_LIST' &&
                            <FormControl variant="outlined" className={classes.devRepFiltersSelect}>
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
                                    <MenuItem value={'ALL_IN_LIST'}>Оба варианта</MenuItem>
                                </Select>
                            </FormControl>
                        }
                    </div>
                    
                </div>
                {['DATE_RANGE', 'TOKEN_INPUT', 'VALUE_LIST', 'VALUE_LIST_UNBOUNDED'].includes(filterItem.type) &&
                <DesignerTextField
                    label = "Ограничение кол-ва выбранных значений"
                    type = "number"
                    value = {filterItem.maxCountItems} 
                    onChange = {data => {handleChangeMetadata('maxCountItems', data)}}
                    displayBlock
                    fullWidth
                    size="small"
                    //error = {!filterItem.name}
                />
                }
                <div>
                    <Typography variant="body2" color="textSecondary" component="p" gutterBottom>
                        {datasetType === 0 ? 'Сопоставьте поля фильтра с полями отчёта:' : 'Поля фильтра:'}
                    </Typography>
                    <div className={classes.repFieldFilterFieldBlock}>
                        {filterItem.fields.filter(field => (field.type === 'ID_FIELD' && !(['DATE_RANGE', 'RANGE'].includes(filterItem.type) && field.level > 1) )).map((field, itemIndex) =>
                            <div key={field.filterInstanceFieldId} className={classes.repFieldFilterField}> 
                                <DesignerTextField
                                    disabled={true}
                                    value = {field.name}
                                    displayBlock
                                    fullWidth
                                    size="small"
                                    inputProps ={
                                        field.expand ? {
                                        endAdornment: (
                                           <InputAdornment position="end">
                                                <FlagIcon color = 'secondary'/>
                                          </InputAdornment>
                                        ),
                                      } : null}
                                />
                                {datasetType === 0 &&
                                    <Divider orientation="vertical" flexItem  className={classes.divCompare} variant="middle" />
                                        &&
                                    <DesignerSelectField 
                                        key={itemIndex}
                                        // label={field.name}
                                        data={reportFields}
                                        fullWidth
                                        size="small"
                                        value={filterItem.fields[itemIndex] && datasetType === 0 ? filterItem.fields[itemIndex].reportFieldId : null}
                                        onChange = {value => handleChange(itemIndex, field, value)}
                                        error={field.error || !(filterItem.hasOwnProperty('valid') ?  filterItem.valid: true)}
                                       // disabled={Boolean(datasetType)}
                                    />
                                }
                            </div>
                        )}
                    </div>
                </div>
            </Collapse>                    
        </Paper>
    )
};
