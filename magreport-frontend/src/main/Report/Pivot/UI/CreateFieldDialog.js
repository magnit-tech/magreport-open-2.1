import React, {useState, useRef} from "react";

import { PivotCSS } from '../PivotCSS';

import { Paper, Dialog, DialogTitle, DialogActions, Button, TextField} from '@material-ui/core';

import Draggable from 'react-draggable';
import FormulaEditor from "../maglangFormulaEditor/FormulaEditor/FormulaEditor";


/**
	* @param {Boolean} props.open - boolean-значение отображения модального окна
	* @param {*} props.onCancel - function - callback отмены/закрытия модального окна
	* @param {*} props.onSave - function - callback сохранения поля
**/

//Перетаскивание модального окна
function PaperComponent(props) {
    return (
		<Draggable handle="#drag-title" cancel={'[class*="MuiDialogContent-root"]'}>
			<Paper {...props} />
		</Draggable>
    );
}

export default function CreateFieldDialog(props){

    const classes = PivotCSS();

    const [isFormulaCorrect, setIsFormulaCorrect] = useState(false);
    const [fieldName, setFieldName] = useState("");
    const [fieldDesc, setFieldDesc] = useState("");

    // Семантическое дерево формулы
    const formulaTreeRoot = useRef(null);

    /*
        Построение семнатического дерева формулы в серверной форме
    */
    function buildServerFormulaObject(treeNode){
        return null;
    }

    return (
        <Dialog
        open={props.open}
        PaperComponent={PaperComponent}
        aria-labelledby="drag-title"
        >

            <DialogTitle style={{ cursor: 'move' }} id="drag-title"> Производное поле </DialogTitle>

            <TextField
						required
						error={ fieldName.replace(/\s/g,"") === "" ? true : false }
						id="newFieldName"
						label="Название"
						placeholder="Введите название производного поля"
						className={classes.CSD_nameField}
						InputLabelProps={{
							shrink: true,
						}}
						variant="outlined"
						value={fieldName}
						onChange={(event) => setFieldName(event.target.value)}
			/>
			<TextField
						id="newConfigDescription"
						label="Описание"
						placeholder="Введите описание производного поля"
						multiline
						rows={5}
						className={classes.CSD_descriptionField}
						InputLabelProps={{
							shrink: true,
						}}
						variant="outlined"
						value={fieldDesc}
						onChange={(event) => setFieldDesc(event.target.value)}
			/>

            <FormulaEditor
                height = "100px"
                initialCode={""}
                functions = {[]}
                originalFields = {[]}
                derivedFields = {[]}
                onChange = {() => {}}
            />

            <DialogActions>
				<Button 
					color="primary" 
					disabled = { isFormulaCorrect ? false : true }
					onClick={() => props.onSave({
                        fieldName : fieldName,
                        fieldDesc : fieldDesc,
                        fieldFormula : buildServerFormulaObject(formulaTreeRoot.current)
                    })}
				>
					Сохранить
				</Button>
				<Button 
					color="primary" 
					onClick={() => props.onCancel('closeConfigSaveDialog')}
				>
					Отменить
				</Button>
			</DialogActions>

        </Dialog>
    )
}