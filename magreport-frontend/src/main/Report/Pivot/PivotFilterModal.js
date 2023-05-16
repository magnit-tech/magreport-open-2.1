import React, { useState, useEffect } from 'react';
import Button from '@material-ui/core/Button';
import clsx from 'clsx';
import Dialog from '@material-ui/core/Dialog';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';
import DialogActions from '@material-ui/core/DialogActions';
import Paper from '@material-ui/core/Paper';
import Draggable from 'react-draggable';
import TextField from '@material-ui/core/TextField';
// local
import {AggFunc} from '../../FolderContent/JobFilters/JobStatuses';
import {PivotCSS} from './PivotCSS';
import PivotFilters from './PivotFilters';
import {FilterObject} from './dataobjects/FilterObject'; 

function PaperComponent(props) {
    return (
      <Draggable handle="#draggable-dialog-title" cancel={'[class*="MuiDialogContent-root"]'}>
        <Paper {...props}/>
      </Draggable>
    );
  }

/**
 * 
 * @param {*} props.open - признак открытого диалогового окна
 * @param {*} props.style - объект стиля
 * @param {*} props.jobId - id задания
 * @param {*} props.field - поле с фильтрацией
 * @param {*} props.fieldsLists - список полей
 * @param {*} props.filterGroup - группа фильтров для измерений
 * @param {*} props.metricFilterGroup - группа фильтров для метрик
 * @param {*} props.onClose - колбэк по закрытию без сохранения
 * @param {*} props.onOK - колбэк по закрытию с сохранением
 * 
 * 
 * @returns 
 */
export default function PivotFilterModal(props){
    const classes = PivotCSS();
    const [filterValues, setFilterValues] = useState({}); //new FilterObject({field: {fieldId: props.field?.fieldId, fieldType: props.field?.original ? 'REPORT_FIELD' : 'DERIVED_FIELD'}, ...props.field?.filter}));
    const [filterType, setFilterType] = useState(props.field?.filter?.filterType ?? 'EQUAL');
    const [newName, setNewName] = useState(props.field?.newName) ;

    function handleChangeFilterValues(value){
        setFilterValues(value);
        setFilterType(value.filterType);
    }

    useEffect(() => {
        setFilterValues(new FilterObject({field: {fieldId: props.field?.fieldId, fieldType: props.field?.original ? 'REPORT_FIELD' : 'DERIVED_FIELD'}, ...props.field?.filter}));
        setFilterType(props.field?.filter?.filterType ?? 'EQUAL');
        setNewName(props.field?.newName)
    },[props.open]) // eslint-disable-line


    function handleOk(value, newName){
        props.onOK(value, newName);
    }

    function handleClose(){
        props.onClose();
    }

    return (
        <Dialog
            open={props.open}
            onClose={props.onClose}
            PaperComponent={PaperComponent}
            aria-labelledby="draggable-dialog-title"
            maxWidth = {false}
            PaperProps={{ classes: {root: clsx({
                [classes.inListDialog] : filterType === 'IN_LIST'
            })}}}
        >
            <DialogTitle style={{ cursor: 'move' }} id="draggable-dialog-title" >
                Фильтрация
            </DialogTitle>
            <DialogContent style={{display: 'flex', flex: 1, flexDirection: 'column'}}>
            <TextField
                id="fieldName"
                label={"по " + (props.field?.aggFuncName ? "метрике" : "измерению")}
                variant="outlined"
                size="small"
                style={{margin: '0px 16px 8px 36px', width: '408px'}}
                defaultValue={(props.field?.aggFuncName ? AggFunc.get(props.field?.aggFuncName) + ' ' : '') + props.field?.fieldName}
                InputProps={{readOnly: true}}
            />
            <TextField
                id="fieldName"
                label={'описание '  + (props.field?.aggFuncName ? "метрики" : "измерения")}
                variant="outlined"
                size="small"
                multiline
                style={{margin: '0px 16px 8px 36px', width: '408px'}}
                defaultValue = {newName}
                onChange = {(e)=>setNewName(e.target.value)}
            />
                
            <PivotFilters
                jobId = {props.jobId}
                field = {props.field}
                fieldsLists = {props.fieldsLists}
                filterGroup = {props.filterGroup}
                metricFilterGroup = {props.metricFilterGroup}
                onChangeValues={ handleChangeFilterValues}
            />
            </DialogContent>
            <DialogActions>
                <div className={classes.btnsArea}>
                    <Button color="primary" size="small" variant="outlined" onClick = {()=>handleOk(filterValues, newName)}> OK </Button> 
                    <Button color="primary" size="small" variant="outlined" onClick = {()=>handleClose()} className={classes.cancelBtn}> Отменить </Button>
                </div>
            </DialogActions>
        </Dialog>

    )
}