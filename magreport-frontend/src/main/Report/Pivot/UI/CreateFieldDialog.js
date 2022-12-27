import React, {useState, useRef, useCallback} from "react";

import { PivotCSS } from '../PivotCSS';

import { Paper, Dialog, DialogTitle, DialogActions, Button, TextField} from '@material-ui/core';

import Draggable from 'react-draggable';

import clsx from "clsx";

import dataHub from "ajax/DataHub";
import FormulaEditor, {nodeType} from "../maglangFormulaEditor/FormulaEditor/FormulaEditor";
import DataLoader from "main/DataLoader/DataLoader";


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
        <Paper {...props} />
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
        Загрузка полей
    */

    const [originalFieldsList, setOriginalFieldsList] = useState([]);
    let handleOriginalFieldsLoaded = (data) => {
        let newOriginalFieldsList = data.fields.map((f) => ({fieldId: f.id, fieldName: f.name, fieldDesc: f.description, valueType: f.type}));
        setOriginalFieldsList(newOriginalFieldsList);
    }

    /*
        Изменения формулы
    */

    let handleFormulaChange = useCallback( (compilationResult) => {
            formulaTreeRoot.current = compilationResult.treeRoot;
            setIsFormulaCorrect(compilationResult.success);
        },
        []);

    /*
        Построение семнатического дерева формулы в серверной форме
    */

    function getExpressionForNode(node){
        if(node.nodeType === nodeType.formulaRoot){
            if(node.children.length > 0){
                return getExpressionForNode(node.children[0]);
            }
            else{
                return {};
            }
        }
        else if(node.nodeType === nodeType.numLiteral){
            return {
                type: "CONSTANT_VALUE",
                constantValue: node.value.toString(),
                "constantType": "DOUBLE"
            }
        }
        else if(node.nodeType === nodeType.stringLiteral){
            return {
                type: "CONSTANT_VALUE",
                constantValue: node.value,
                "constantType": "STRING"
            }
        }
        else if(node.nodeType === nodeType.originalField){
            return {
                type: "REPORT_FIELD_VALUE",
                referenceId: node.fieldId
            }
        }      
        else if(node.nodeType === nodeType.derivedField){
            return {
                type: "DERIVED_FIELD_VALUE",
                referenceId: node.fieldId
            }
        }   
        else if(node.nodeType === nodeType.arithmSum){
            return {
                type: "ADD",
                parameters: [getExpressionForNode(node.children[0]), getExpressionForNode(node.children[1])]
            }
        }                   
        else if(node.nodeType === nodeType.arithmSubtraction){
            return {
                type: "SUBTRACT",
                parameters: [getExpressionForNode(node.children[0]), getExpressionForNode(node.children[1])]
            }
        }   
        else if(node.nodeType === nodeType.arithmProduct){
            return {
                type: "MULTIPLY",
                parameters: [getExpressionForNode(node.children[0]), getExpressionForNode(node.children[1])]
            }
        }    
        else if(node.nodeType === nodeType.arithmFraction){
            return {
                type: "DIVIDE",
                parameters: [getExpressionForNode(node.children[0]), getExpressionForNode(node.children[1])]
            }
        }   
        else if(node.nodeType === nodeType.unaryArithmMinus){
            return {
                type: "SUBTRACT",
                parameters: [getExpressionForNode({type: "CONSTANT_VALUE", constantValue: "0", "constantType": "INTEGER"}), getExpressionForNode(node.children[0])]
            }
        }  
        else if(node.nodeType === nodeType.functionCall){
            let type;
            let parameters = [];
            if(node.functionName === "sqrt"){
                type = "SQUARE_ROOT";
            }
            else{
                type = "???";
            }

            for(let child of node.children){
                parameters.push(getExpressionForNode(child));
            }

            return {
                type: type,
                parameters: parameters
            }
        }                            
    }

    function buildServerExression(treeRoot){
        // to format of /derived-field/add
        if(treeRoot === null || treeRoot.isError){
            return null;
        }
        else{
            return getExpressionForNode(treeRoot);
        }
        
    }

    return (
        <Dialog
            open={props.open}
            PaperComponent={PaperComponent}
            aria-labelledby="drag-title"
        >

            <DialogTitle style={{ cursor: 'move' }} id="drag-title"> Производное поле </DialogTitle>

            <DataLoader
                loadFunc = {dataHub.olapController.getJobMetadata}
                loadParams = {[props.jobId]}
                onDataLoaded = {handleOriginalFieldsLoaded}
            >

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
                    height = "200px"
                    initialCode={""}
                    functions = {[{functionId:0, functionName: "sqrt", functionDesc: "Квадратный корень", functionSignature: "sqrt(x)"}]}
                    originalFields = {originalFieldsList}
                    derivedFields = {[]}
                    onChange = {handleFormulaChange}
                />

                <DialogActions>
                    <Button 
                        color="primary" 
                        disabled = { (isFormulaCorrect && fieldName.trim().length > 0) ? false : true }
                        onClick={() => props.onSave({
                            fieldName : fieldName.trim(),
                            fieldDesc : fieldDesc.trim(),
                            expression : buildServerExression(formulaTreeRoot.current)
                        })}
                    >
                        Сохранить
                    </Button>
                    <Button 
                        color="primary" 
                        onClick={() => props.onCancel()}
                    >
                        Отменить
                    </Button>
                </DialogActions>
            </DataLoader>
        </Dialog>
    )
}