import React, {useState, useRef, useEffect} from "react";

import { PivotCSS } from '../../PivotCSS';

import { useAuth } from "router/useAuth";

import { useSnackbar } from 'notistack';

import { connect } from 'react-redux';
import {showAlertDialog, hideAlertDialog} from 'redux/actions/UI/actionsAlertDialog';

// Components
import DerivedFieldDialogList from "./ui/DFD_List";
import DerivedFieldDialogForm from "./ui/DFD_Form";
import { Paper, Dialog, DialogTitle, DialogActions, Button, CircularProgress } from '@material-ui/core';

// functions
import { loadAllFields, loadFieldsAndExpressions, saveNewField, saveEditField, deleteField, saveAllFields} from './lib/DFD_functions'
import { useCallback } from "react";
import { useMemo } from "react";


/**
	* @param {Boolean} props.open - boolean-значение отображения модального окна
    * @param {*} props.jobId - id задания
    * @param {*} props.reportId - id отчёта
    * @param {*} props.isReportDeveloper - обладает ли пользователь правами разработчика данного отчёта 
	* @param {*} props.onCancel - function - callback отмены/закрытия модального окна
	* @param {*} props.onSave - function - callback сохранения поля
**/

//Перетаскивание модального окна
function PaperComponent(props) {
    return (
		/*<Draggable handle="#drag-title" cancel={'[class*="MuiDialogContent-root"]'}>
			<Paper {...props} />
		</Draggable>*/
        <Paper {...props}  className="hi"/>
    );
}

function DerivedFieldDialog(props){

    const { open, jobId, reportId, isReportDeveloper, otherDerivedFields } = props

    const classes = PivotCSS();
    const { enqueueSnackbar } = useSnackbar();
    const { user } = useAuth()

    const [loading, setLoading] = useState(false)

    const [loadedDerivedFields, setLoadedDerivedFields] = useState([])

    const [editedDerivedFields, setEditedDerivedFields] = useState([])

    const [activeIndex, setActiveIndex] = useState(null)

    const listOfChangedFields = useRef([])

    const allFieldsAndExpressions = useRef([])

    const publicFields = useRef(new Map())
    const ownFields = useRef(new Map())
    const otherFields = useRef(new Map())

    const thereHaveBeenChanges = useRef(false)

    useEffect(() => onLoadAllFields(), [open]) // eslint-disable-line

    useEffect(() =>{
        const publicFieldsArray = loadedDerivedFields.filter(item => item.isPublic)
        const ownFieldsArray = loadedDerivedFields.filter(item => (!item.isPublic && item.userName === user.current.name))
        const otherFieldsArray = loadedDerivedFields.filter(item => (!item.isPublic && item.userName !== user.current.name))

        publicFieldsArray.forEach(field => publicFields.current.set(field.name, field))
        ownFieldsArray.forEach(field => ownFields.current.set(field.name, field))
        otherFieldsArray.forEach(field => otherFields.current.set(field.name, field))

    }, [loadedDerivedFields, user]) 

    const disabledSaveAllButton = useMemo(() => {
        let result = new Set()
        
        if (editedDerivedFields.length > 0 && listOfChangedFields.current.length > 0) {
            listOfChangedFields.current.forEach(item => {
                if(!item.isCorrect || !item.isFormulaCorrect) {
                    result.add(true)
                }
            })
        } else {
            result.add(true)
        }

        return result.has(true)

    }, [editedDerivedFields, listOfChangedFields]);


    /* 
        **** Loaded functions ***** 
    */
    // Загрузка текущих производных полей
    function onLoadAllFields() {
        setLoading(true)

        loadAllFields(reportId, user.current.name, otherDerivedFields, (data, editedData) => {
            setLoadedDerivedFields(data)
            setEditedDerivedFields(editedData)
            onLoadFieldsAndExpressions()
        })
    }
    // Загрузка всех полей и выражений
    function onLoadFieldsAndExpressions() {
        loadFieldsAndExpressions(jobId, reportId, otherDerivedFields, (obj) => {
            allFieldsAndExpressions.current = obj
            setLoading(false)
        })
    }


    /*
        **** Secondary functions ****
    */
    // Сохранение в список изменений (listOfChangedFields)
    const onEditObjectToSave = useCallback((list, obj) => {

        const ref = listOfChangedFields.current
        const cond = ref.some(e => e.id === obj.id)

        if (cond) {

            if(obj.needSave) {
                listOfChangedFields.current = ref.map(o => {
                    if (o.id === obj.id) {
                      return obj;
                    }
                    return o;
                });
            } else {
                listOfChangedFields.current = ref.filter(item => item.id !== obj.id)
            }

        } else {
            if(obj.needSave) listOfChangedFields.current.push(obj)
        }

        setEditedDerivedFields(list)
    }, []);

    // Добавление нового поля
    function onAddNewField() {
        let newObj = {
            id: 'new',
            name: '',
            description: '',
            expression: '',
            expressionText: '',
            isPublic: false,
            isCorrect: false,
            isFormulaCorrect: false,
            needSave: true,
            owner: true,
            fontSize: 16
        }

        setActiveIndex('new')
        setEditedDerivedFields([...editedDerivedFields, newObj])
        listOfChangedFields.current.push(newObj)
    }
    
    // При нажатие на кнопку "отменить"
    function onClose() {
        let needAsk = false
        editedDerivedFields.forEach(el => {
            if(el.needSave) {
                needAsk = true
            }
        })
    
        if(needAsk) {
            props.showAlertDialog(`Внимание, есть несохраненные поля. Вы действительно хотите выйти?`, null, null, answer => onCloseAnswer(answer))
        } else {
            onCloseAnswer(true)
        }
    }
    function onCloseAnswer(answer){
        if (answer) props.onCancel(thereHaveBeenChanges.current)
        props.hideAlertDialog()
    }


    /* 
        **** Handle functions ***** 
    */
    // Сохранение нового поля
    function handleSaveNewField(obj) {
        saveNewField(obj, reportId, user.current.name, listOfChangedFields.current, otherDerivedFields, (ok, data, originalData, str, variant, changedList) => {
            if(ok) {
                enqueueSnackbar(str, variant)
                setEditedDerivedFields(data)
                setLoadedDerivedFields(originalData)
                setActiveIndex(data[data.length - 1].id)
                listOfChangedFields.current = changedList
                thereHaveBeenChanges.current = true
            } else {
                enqueueSnackbar(str, variant)
            }
        })
    }

    // Сохранение отредактированого поля
    function handleSaveEditField(obj) {
        saveEditField(obj, reportId, user.current.name, listOfChangedFields.current, otherDerivedFields, (ok, data, originalData, str, variant, changedList) => {
            if(ok) {
                enqueueSnackbar(str, variant)
                setEditedDerivedFields(data)
                setLoadedDerivedFields(originalData)
                listOfChangedFields.current = changedList
                thereHaveBeenChanges.current = true
            } else {
                enqueueSnackbar(str, variant)
            }
        })
    }

    // Сохранение всех полей
    function handleSaveAllFields() {
        saveAllFields(reportId, user.current.name, listOfChangedFields.current, otherDerivedFields, (ok, data, str, variant, originalData, changedList) => {
            if(ok) {
                enqueueSnackbar(str, variant)
                props.onCancel(true)
            } else {
                enqueueSnackbar(str, variant)
                if(data.length > 0) {
                    setEditedDerivedFields(data)
                    setLoadedDerivedFields(originalData)
                    listOfChangedFields.current = changedList
                    thereHaveBeenChanges.current = true
                }
            }
        })
    }

    // Удаление
    function handleDelete(id) {
        if(id === 'new') {
            setEditedDerivedFields(editedDerivedFields.filter((item) => item.id !== 'new'))
            listOfChangedFields.current = listOfChangedFields.current.filter((item) => item.id !== 'new')
            setActiveIndex(null)
        } else {
            deleteField(id, reportId, user.current.name, listOfChangedFields.current, otherDerivedFields, (ok, data, originalData, str, variant, changedList) => {
                if(ok) {
                    enqueueSnackbar(str, variant)
                    setActiveIndex(id === activeIndex ? null : activeIndex)
                    setEditedDerivedFields(data)
                    setLoadedDerivedFields(originalData)
                    listOfChangedFields.current = changedList
                    thereHaveBeenChanges.current = true
                } else {
                    enqueueSnackbar(str, variant)
                }
            })
        }
    }


    return (
        <Dialog
            open={open}
            PaperComponent={PaperComponent}
            aria-labelledby="drag-title"
        >
            <DialogTitle id="drag-title"> Производные поля </DialogTitle>

            <div className={classes.DFD_main}>
                
                { loading ? <CircularProgress /> 
                    : <>
                        <DerivedFieldDialogList
                            editedDerivedFields = {editedDerivedFields}
                            activeIndex = {activeIndex}
                            onAddNew = {() => onAddNewField()}
                            onChoose = {(index) => setActiveIndex(index)}
                            onDelete = {(id) => handleDelete(id)}
                        />

                        <DerivedFieldDialogForm 
                            reportId = {reportId}
                            activeIndex={activeIndex}
                            isReportDeveloper={isReportDeveloper}
                            allFieldsAndExpressions = {allFieldsAndExpressions.current}
                            loadedDerivedFields = {loadedDerivedFields}
                            editedDerivedFields = {editedDerivedFields}
                            publicFields = {publicFields.current}
                            ownFields = {ownFields.current}
                            otherFields = {otherFields.current}
                            onEdit = {onEditObjectToSave}
                            onSave = {(obj) => obj.id === 'new' ? handleSaveNewField(obj) : handleSaveEditField(obj)}
                        />
                    </>
                }
            </div>

            <DialogActions>
				<Button
					color="primary"
                    disabled = {disabledSaveAllButton}
                    onClick={() => handleSaveAllFields()}
				>
					Сохранить все и выйти
				</Button>
				<Button 
					color="primary" 
					onClick={() => onClose()}
				>
					Отменить
				</Button>
			</DialogActions>

        </Dialog>
    )
}

const mapDispatchToProps = {
    showAlertDialog, 
    hideAlertDialog,
}

export default connect(null, mapDispatchToProps)(DerivedFieldDialog)