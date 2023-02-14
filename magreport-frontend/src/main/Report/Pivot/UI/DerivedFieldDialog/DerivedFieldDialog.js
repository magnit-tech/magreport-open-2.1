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
import { loadAllFields, loadFieldsAndExpressions, saveNewField, saveEditField, deleteField} from './lib/DFD_functions'
import { useCallback } from "react";
import { useMemo } from "react";


/**
	* @param {Boolean} props.open - boolean-значение отображения модального окна
    * @param {*} props.jobId - id задания
    * @param {*} props.reportId - id отчёта
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

    const { open, jobId, reportId } = props

    const classes = PivotCSS();
    const { enqueueSnackbar } = useSnackbar();
    const { user } = useAuth()

    const [loading, setLoading] = useState(false)

    const [loadedDerivedFields, setLoadedDerivedFields] = useState([])

    const [editedDerivedFields, setEditedDerivedFields] = useState([])

    const [activeIndex, setActiveIndex] = useState(null)

    const listOfChangedFields = useRef([])

    const allFieldsAndExpressions = useRef([])

    const thereHaveBeenChanges = useRef(false)

    useEffect(() => handleLoadAllFields(), [open]) // eslint-disable-line

    const disabledSaveAllButton = useMemo(() => {
        let result = true 
        
        if (editedDerivedFields.length > 0 && listOfChangedFields.current.length > 0) {
            listOfChangedFields.current.forEach(item => {
                if(!item.isCorrect || !item.isFormulaCorrect) {
                    result = false
                }
            })
        }

        return result

    }, [editedDerivedFields, listOfChangedFields]);

    // Загрузка текущих производных полей
    function handleLoadAllFields() {
        setLoading(true)

        loadAllFields(reportId, user.current.name, (data, editedData) => {
            setLoadedDerivedFields(data)
            setEditedDerivedFields(editedData)
            handleLoadFieldsAndExpressions()
        })
    }
    // Загрузка текущих производных полей
    function handleLoadFieldsAndExpressions() {
        loadFieldsAndExpressions(jobId, reportId, (obj) => {
            allFieldsAndExpressions.current = obj
            setLoading(false)
        })
    }


    const handleEditObjectToSave = useCallback((list, obj) => {

        const cond = listOfChangedFields.current.some(function(e){ 
            return e.id === obj.id;
        });

        if (cond) {
            listOfChangedFields.current = listOfChangedFields.current.map(o => {
                if (o.id === obj.id) {
                  return obj;
                }
                return o;
            });
        } else {
            listOfChangedFields.current.push(obj)
        }

        setEditedDerivedFields(list)
    }, []);

    
    // Сохранение нового поля
    function handleSaveNewField(obj) {
        saveNewField(obj, reportId, user.current.name, listOfChangedFields.current, (ok, data, originalData, str, variant, changedList) => {
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
        saveEditField(obj, reportId, user.current.name, listOfChangedFields.current, (ok, data, originalData, str, variant, changedList) => {
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

    // Добавление нового поля
    function handleAddNewField() {
        let newObj = {
            id: 'new',
            name: '',
            description: '',
            expression: '',
            expressionText: '',
            isCorrect: false,
            isFormulaCorrect: false,
            needSave: true,
        }

        setActiveIndex('new')
        setEditedDerivedFields([...editedDerivedFields, newObj])
        listOfChangedFields.current.push(newObj)
    }

    // Удаление
    function handleDelete(id) {
        if(id === 'new') {
            setEditedDerivedFields(editedDerivedFields.filter((item) => item.id !== 'new'))
            listOfChangedFields.current = listOfChangedFields.current.filter((item) => item.id !== 'new')
            setActiveIndex(null)
        } else {
            deleteField(id, reportId, user.current.name, listOfChangedFields.current, (ok, data, originalData, str, variant, changedList) => {
                if(ok) {
                    enqueueSnackbar(str, variant)
                    setEditedDerivedFields(data)
                    setLoadedDerivedFields(originalData)
                    setActiveIndex(id === activeIndex ? null : activeIndex)
                    listOfChangedFields.current = changedList
                    thereHaveBeenChanges.current = true
                } else {
                    enqueueSnackbar(str, variant)
                }
            })
        }
    }

    // При нажатие на кнопку "отменить"
    function handleClose() {
        let needAsk = false
        editedDerivedFields.forEach(el => {
            if(el.needSave) {
                needAsk = true
            }
        })
    
        if(needAsk) {
            props.showAlertDialog(`Внимание, есть несохраненные поля. Вы действительно хотите выйти?`, null, null, answer => handleCloseAnswer(answer))
        } else {
            handleCloseAnswer(true)
        }
    }
    function handleCloseAnswer(answer){
        if (answer) props.onCancel(thereHaveBeenChanges.current)
        props.hideAlertDialog()
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
                            onAddNew = {() => handleAddNewField()}
                            onChoose = {(index) => setActiveIndex(index)}
                            onDelete = {(id) => handleDelete(id)}
                        />

                        <DerivedFieldDialogForm 
                            reportId = {reportId}
                            activeIndex={activeIndex}
                            allFieldsAndExpressions = {allFieldsAndExpressions.current}
                            loadedDerivedFields = {loadedDerivedFields}
                            editedDerivedFields = {editedDerivedFields}
                            onEdit = {handleEditObjectToSave}
                            onSave = {(obj) => obj.id === 'new' ? handleSaveNewField(obj) : handleSaveEditField(obj)}
                        />
                    </>
                }
            </div>

            <DialogActions>
				{/* <Button
					color="primary"
                    disabled = {disabledSaveAllButton} 
				>
					Сохранить все и выйти
				</Button> */}
				<Button 
					color="primary" 
					onClick={() => handleClose()}
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