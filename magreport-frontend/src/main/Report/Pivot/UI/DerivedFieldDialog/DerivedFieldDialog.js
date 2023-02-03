import React, {useState, useRef, useCallback} from "react";

import { PivotCSS } from '../../PivotCSS';

import { Paper, Dialog, DialogTitle, DialogActions, Button } from '@material-ui/core';

import Draggable from 'react-draggable';

import clsx from "clsx";

import dataHub from "ajax/DataHub";
import FormulaEditor, {nodeType} from "../../maglangFormulaEditor/FormulaEditor/FormulaEditor";
import DataLoader from "main/DataLoader/DataLoader";

// Components
import DerivedFieldDialogList from "./DerivedFieldDialogList";
import DerivedFieldDialogForm from "./DerivedFieldDialogForm";


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

export default function DerivedFieldDialog(props){

    // const classes = PivotCSS();

    const [activeDerivedField, setActiveDerivedField] = useState(null)

    const [objToSave, setObjToSave] = useState({
		fieldName: '',
		fieldDesc: '',
		expression: '',
		expressionText: '',
        isFormulaCorrect: false
	})

    function handleEditObjectToSave(obj) {
        setObjToSave(obj)
    }

    return (
        <Dialog
            open={props.open}
            PaperComponent={PaperComponent}
            aria-labelledby="drag-title"
        >

            <DialogTitle style={{ cursor: 'move' }} id="drag-title"> Производные поля </DialogTitle>

            <div style={{display: 'flex', backgroundColor: 'darkgrey'}}>

                <DerivedFieldDialogList 
                    reportId={props.reportId} 
                    onChoose = {(obj) => setActiveDerivedField(obj)}
                    onDelete = {(id) => console.log(id)}
                />

                <DerivedFieldDialogForm 
                    jobId={props.jobId} 
                    reportId={props.reportId} 
                    activeDerivedField={activeDerivedField}
                    onEdit = {(obj) => handleEditObjectToSave(obj)}
                />

            </div>

            <DialogActions>
				<Button 
					color="primary" 
                    disabled = { (objToSave.isFormulaCorrect && objToSave.fieldName.trim().length > 0) ? false : true }
                    onClick={() => props.onSave(objToSave)}
                    // onClick={() => console.log(objToSave)}
				>
					Сохранить
				</Button>
				<Button 
					color="primary" 
                    disabled = { (objToSave.isFormulaCorrect && objToSave.fieldName.trim().length > 0) ? false : true }
                    onClick={() => props.onSave(objToSave)}
                    // onClick={() => console.log(objToSave)}
				>
					Сохранить все и выйти
				</Button>
				<Button 
					color="primary" 
					onClick={() => props.onCancel()}
				>
					Отменить
				</Button>
			</DialogActions>

        </Dialog>
    )
}