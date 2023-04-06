import React, { useState, useEffect } from 'react';
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';
import DialogActions from '@material-ui/core/DialogActions';
import Paper from '@material-ui/core/Paper';
import Draggable from 'react-draggable';

import DesignerTextField from '../../Main/Development/Designer/DesignerTextField';

// local
import {AggFunc} from '../../FolderContent/JobFilters/JobStatuses';
import {PivotCSS} from './PivotCSS';

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
export default function PivotMetricModal(props){
    const classes = PivotCSS();
    const [newName, setNewName] = useState(props.field?.newName) ;

    useEffect(() => {
        setNewName(props.field?.newName)
    },[props.open]) // eslint-disable-line


    function handleOk(newName){
        props.onOK(newName);
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
        >
            <DialogTitle style={{ cursor: 'move' }} id="draggable-dialog-title" >
                Метрика
            </DialogTitle>
            <DialogContent style={{display: 'flex', flex: 1, flexDirection: 'column'}}>
            <DesignerTextField
                id="fieldName"
                label={"функция"}
                variant="outlined"
                size="small"
                margin = '0px 8px 8px'
                minWidth = '408px'
                value={(props.field?.aggFuncName ? AggFunc.get(props.field?.aggFuncName) + ' ' : '')}
                disabled
            />
            <DesignerTextField
                id="fieldName"
                label={"название"}
                variant="outlined"
                size="small"
                margin = '0px 8px 8px'
                minWidth = '408px'
                value={props.field?.fieldName}
                disabled
            />
            <DesignerTextField
                id="fieldName"
                label={'описание'}
                variant="outlined"
                size="small"
                multiline
                margin = '0px 8px 8px'
                minWidth = '408px'
                value = {newName}
                clearable
                onChange = {(val)=>setNewName(val)}
            />
            </DialogContent>
            <DialogActions>
                <div className={classes.btnsArea}>
                    <Button color="primary" size="small" variant="outlined" onClick = {()=>handleOk(newName)}> OK </Button> 
                    <Button color="primary" size="small" variant="outlined" onClick = {()=>handleClose()} className={classes.cancelBtn}> Отменить </Button>
                </div>
            </DialogActions>
        </Dialog>

    )
}