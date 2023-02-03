import React, {useState, useRef, useCallback, useEffect} from "react";

import { PivotCSS } from '../../PivotCSS';

import { TextField } from '@material-ui/core';

import dataHub from "ajax/DataHub";
import FormulaEditor, {nodeType} from "../../maglangFormulaEditor/FormulaEditor/FormulaEditor";
import DataLoader from "main/DataLoader/DataLoader";

export default function DerivedFieldDialogForm(props){

    const classes = PivotCSS();

    const [fieldName, setFieldName] = useState("");
    const [fieldDesc, setFieldDesc] = useState("");

	useEffect(() => {
		setFieldName(props.activeDerivedField ? props.activeDerivedField.fieldName : "")
		setFieldDesc(props.activeDerivedField ? props.activeDerivedField.fieldDesc : "")
	}, [props.activeDerivedField])



    // Семантическое дерево формулы
    const formulaTreeRoot = useRef(null);

	const objToSave = useRef({
		fieldName: '',
		fieldDesc: '',
		expression: '',
		expressionText: '',
		isFormulaCorrect: false
	})

	function handleChangeName(name) {
		objToSave.current = {...objToSave.current, fieldName: name}
		setFieldName(name)
		handlePostObjToSave()
	}

	function handleChangeDesc(desc) {
		objToSave.current = {...objToSave.current, fieldDesc: desc}
		setFieldDesc(desc)
		handlePostObjToSave()
	}
	
	function handlePostObjToSave() {
		props.onEdit(objToSave.current)
	}

    /*
        Загрузка полей
    */

    const [functionsList, setFunctionsList] = useState([]);
    const [originalFieldsList, setOriginalFieldsList] = useState([]);
    const [derivedFieldsList, setDerivedFieldsList] = useState([]);

    let handleFieldsAndExpressionsLoaded = (data) => {
        let newFunctionsList = data.expressions.map(
            (f) => ({functionId: f.id, functionName: f.name, functionDesc: f.description, functionSignature: ""}));
        let newOriginalFieldsList = data.fields.filter((f) => (f.visible)).map(
                (f) => ({fieldId: f.id, fieldName: f.name, fieldDesc: f.description, valueType: f.type}));
        let newDerivedFieldsList = data.derivedFields.map(
                (f) => ({fieldId: f.id, fieldName: f.name, fieldDesc: f.description, valueType: f.type, fieldOwner: f.userName}));
        setFunctionsList(newFunctionsList);
        setOriginalFieldsList(newOriginalFieldsList);
        setDerivedFieldsList(newDerivedFieldsList);
    }

    /*
        Изменения формулы
    */

    let handleFormulaChange = useCallback((compilationResult) => {
		// formulaTreeRoot.current = compilationResult.treeRoot;
		// textToSave.current = compilationResult.textToSave;
		// setIsFormulaCorrect(compilationResult.success);
		objToSave.current = {...objToSave.current, isFormulaCorrect: compilationResult.success, expression : buildServerExression(compilationResult.treeRoot), expressionText: compilationResult.textToSave}
		handlePostObjToSave()
	}, []);

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
            let type = node.functionName;
            let parameters = [];

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
		<div style={{ padding: '20px', background: 'white', margin: '20px', borderRadius: '8px'}}>
			<DataLoader
                loadFunc = {dataHub.derivedFieldController.getFieldsAndExpressions}
                loadParams = {[props.jobId, props.reportId]}
                onDataLoaded = {handleFieldsAndExpressionsLoaded}
            >
				<TextField
					required
					label="Название"
					id="newFieldName"
					placeholder="Введите название производного поля"
					className={classes.CSD_nameField}
					InputLabelProps={{
						shrink: true,
					}}
					variant="outlined"
					value={fieldName}
					onChange={(event) => handleChangeName(event.target.value)}
					error={ fieldName.replace(/\s/g,"") === "" ? true : false }
				/>
				<TextField
					label="Описание"
					id="newConfigDescription"
					placeholder="Введите описание производного поля"
					multiline
					rows={5}
					className={classes.CSD_descriptionField}
					InputLabelProps={{
						shrink: true,
					}}
					variant="outlined"
					value={fieldDesc}
					onChange={(event) => handleChangeDesc(event.target.value)}
				/>

				<FormulaEditor
					height = "200px"
					initialCode={""}
					functions = {functionsList}
					originalFields = {originalFieldsList}
					derivedFields = {derivedFieldsList}
					onChange = {handleFormulaChange}
				/>
			</DataLoader>
		</div>
	)
}