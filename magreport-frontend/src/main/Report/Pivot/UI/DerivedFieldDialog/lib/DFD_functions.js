
import dataHub from "ajax/DataHub";
import { nodeType } from "../../../maglangFormulaEditor/FormulaEditor/createOutputNode";

// Вспомогательные функции
function getAll(reportId, ownerName, listOfChangedFields, otherDerivedFields, callback) {
	dataHub.derivedFieldController.getAllDerivedFields(reportId, otherDerivedFields, ({ok, data}) => {

		if(ok) {
			let res = [];
			let newField = listOfChangedFields.find(item => item.id === 'new')

			for (const element of data) {
				let obj = listOfChangedFields.find(item => item.id === element.id)
				if(obj) {
					res.push(obj)
				} else {
					element["originalName"] = element.name;
					element["isCorrect"] = true;
					element["isFormulaCorrect"] = true;
					element["needSave"] = false;
					element["owner"] = ownerName === element.userName ? true : false;
					element["fontSize"] = 16;
					res.push(element)
				}
			}
			
			if(newField) res.push(newField)

			return callback(res, data)
		} else {
			return callback([])
		}
	})
}


// Экспортирующиеся функции
export function loadAllFields(reportId, userName, otherDerivedFields, callback) {
	dataHub.derivedFieldController.getAllDerivedFields(reportId, otherDerivedFields, ({ok, data}) => {
		if(ok) {
			const editedData = data.map((el) => {
				el["originalName"] = el.name;
				el["isCorrect"] = true;
				el["isFormulaCorrect"] = true;
				el["needSave"] = false;
				el["owner"] = userName === el.userName ? true : false;
				el["fontSize"] = 16;
				return el;
			})

			return callback(data, editedData)
		}
	})
}

export function loadFieldsAndExpressions(jobId, reportId, otherDerivedFields, callback) {
	dataHub.derivedFieldController.getFieldsAndExpressions(jobId, reportId, otherDerivedFields, ({ok, data}) => {
		if(ok) {
			let functionsList = data.expressions.map(
				(f) => ({functionId: f.id, functionName: f.name, functionDesc: f.description, functionSignature: ""}));
			let originalFieldsList = data.fields.filter((f) => (f.visible)).map(
					(f) => ({fieldId: f.id, fieldName: f.name, fieldDesc: f.description, valueType: f.type}));
			let derivedFieldsList = data.derivedFields.map(
					(f) => ({fieldId: f.id, fieldName: f.name, fieldDesc: f.description, valueType: f.type, fieldOwner: f.userName}));

			return callback({functionsList, originalFieldsList, derivedFieldsList})
		} else {
			return callback({})
		}
	})
}

export function saveNewField(obj, reportId, ownerName, listOfChangedFields, otherDerivedFields, callback) {
	
	let str = '';
	let variant = '';
	const changedList = listOfChangedFields.filter(item => item.id !== obj.id)

	dataHub.derivedFieldController.add(reportId, obj, ({ok}) => {
		if (ok) {
			str = `Новое производное поле "${obj.name}" успешно сохранено`
			variant = {variant: "success"}
		
			let changedListWithoutNew = changedList.filter(item => item.id !== 'new')

			getAll(reportId, ownerName, changedListWithoutNew, otherDerivedFields, (data, originalData) => {
				return callback(true, data, originalData, str, variant, changedListWithoutNew)
			})
		} else {
			str = `Произошла ошибка при сохранение нового производного поля "${obj.name}"`
			variant = {variant: "error"}
			return callback(ok, [], str, variant)
		}
	})
}

export function saveEditField(obj, reportId, ownerName, listOfChangedFields, otherDerivedFields, callback) {
	
	let str = '';
	let variant = '';
	const changedList = listOfChangedFields.filter(item => item.id !== obj.id)

	dataHub.derivedFieldController.edit(reportId, obj, ({ok}) => {
		if (ok) {
			str = `Производное поле "${obj.name}" успешно обновлено`
			variant = {variant: "success"}
			
			getAll(reportId, ownerName, changedList, otherDerivedFields, (data, originalData) => {
				return callback(true, data, originalData, str, variant, changedList)
			})
		} else {
			str = `Произошла ошибка при обновление производного поля "${obj.name}"`
			variant = {variant: "error"}
			return callback(ok, [], [], str, variant)
		}
	})
}

export function deleteField(id, reportId, ownerName, listOfChangedFields, otherDerivedFields, callback) {
	let str = '';
	let variant = '';

	const changedList = listOfChangedFields.filter(item => item.id !== id)

	dataHub.derivedFieldController.delete(id, ({ok}) => {
		if (ok) {
			str = "Производное поле успешно удаленно"
			variant = {variant: "success"}
		
			getAll(reportId, ownerName, changedList, otherDerivedFields, (data, originalData) => {
				return callback(true, data, originalData, str, variant, changedList)
			})
		} else {
			str = "Произошла ошибка при удаление производного поля"
			variant = {variant: "error"}
			return callback(ok, [], str, variant)
		}
	})
}

function saveAllAfterPromise(results, reportId, ownerName, otherDerivedFields, callback) {

	if (results.length > 0) {
		getAll(reportId, ownerName, results, otherDerivedFields, (data, originalData) => {
			return callback(false, data, originalData);
		})
	} else {
		return callback(true);
	}

}

export function saveAllFields(reportId, ownerName, listOfChangedFields, otherDerivedFields, callback) {

	let promises = []
	let str = '';
	let variant = '';

	listOfChangedFields.forEach((field) => {
		if(field.id === 'new') {
			promises.push(
				new Promise(resolve => {
					dataHub.derivedFieldController.add(reportId, field, ({data}) => resolve(data ? {...field, errorMessage: `Ошибка при сохранение: ${data}`} : 'ok'))
				})
			)

		} else {
			promises.push(
				new Promise(resolve => {
					dataHub.derivedFieldController.edit(reportId, field, ({data}) => resolve(data ? {...field, errorMessage: `Ошибка при сохранение: ${data}`} : 'ok'))
				})
			)
			
		}
	})

	Promise.all(promises)
		.then(results  => saveAllAfterPromise(results.filter(i => typeof i === 'object'), reportId, ownerName, otherDerivedFields, (bool, data, originalData) => {
			if(bool) {
				str = 'Все производные поля успешно сохранены'
				variant = {variant: "success"}
				return callback(true, [], str, variant)
			} else {
				str = 'При сохранение некоторых полей возникла ошибка, остальные поля успешно обновлены'
				variant = {variant: "error"}
				return callback(false, data, str, variant, originalData, results.filter(i => typeof i === 'object'))
			}
		}))
		.catch(err => {
			str = `Произошла ошибка. Обратитесь в поддержку. Ошибка: ${err.message}`
			variant = {variant: "error"}
			return callback(false, [], str, variant)
		})
}


// DFD_form
export function checkForDifferenceFromOriginalField(obj, loadedDerivedFields) {
	let result = false

	const originalField = loadedDerivedFields.find(item => item.id === obj.id)

	let arr = ['name', 'description', 'expressionText', 'isPublic']

	for (const key of arr) {
		if(originalField[key] === obj[key]) {
			continue
		} else {
			result = true
			break
		}
		
	}

	return result
}

export function fieldNamevalidation(isPublic, debouncedSearchTerm, currentField, publicFields, ownFields, callback ) {
	let item = {}
	let msg = ''

	if (isPublic) {
		if (publicFields.get(debouncedSearchTerm)) {
			item = {...currentField, isCorrect: false}
			msg = 'Название поля должно быть уникальным среди полей общего назначения!'
		} else {
			item = {...currentField, isCorrect: true}
			msg = ''
		}
	} else {
		if (ownFields.get(debouncedSearchTerm)) {
			item = {...currentField, isCorrect: false}
			msg = 'Название поля должно быть уникальным среди собственных полей!'
		} else {
			item = {...currentField, isCorrect: true}
			msg = ''
		}
	}

	return callback(item, msg)
}

// Построение семнатического дерева формулы в серверной форме 
export function buildServerExression(treeRoot){
	// to format of /derived-field/add
	if(treeRoot === null || treeRoot.isError){
		return null;
	}
	else{
		return getExpressionForNode(treeRoot);
	}
	
}

function getExpressionForNode(node){

	if(node.nodeType === nodeType.formulaRoot){
		if(node.children.length > 0){
			return getExpressionForNode(node.children[0]);
		}
		else{
			return {};
		}
	}
	// CONSTANTS
	else if(node.nodeType === nodeType.numLiteral){
		return {
			type: "CONSTANT_VALUE",
			constantValue: node.value.toString(),
			"constantType": node.numberType
		}
	}
	else if(node.nodeType === nodeType.stringLiteral){
		return {
			type: "CONSTANT_VALUE",
			constantValue: node.value,
			"constantType": "STRING"
		}
	}
	else if(node.nodeType === nodeType.booleanLiteral){
		return {
			type: "CONSTANT_VALUE",
			constantValue: node.value,
			"constantType": "BOOLEAN"
		}
	}
	// Report and derived fields
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
	// Arithmetic
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
	else if(node.nodeType === nodeType.arithmIntDivision){
		return {
			type : "FLOOR",
			parameters : [{
				type: "DIVIDE",
				parameters: [getExpressionForNode(node.children[0]), getExpressionForNode(node.children[1])]
			}]
		}
	}
	else if(node.nodeType === nodeType.arithmModulo){
		return {
			type: "MODULO",
			parameters: [getExpressionForNode(node.children[0]), getExpressionForNode(node.children[1])]
		}
	}
	else if(node.nodeType === nodeType.unaryArithmMinus){
		return {
			type: "SUBTRACT",
			parameters: [{type: "CONSTANT_VALUE", constantValue: "0", "constantType": "INTEGER"}, getExpressionForNode(node.children[0])]
		}
	}
	// function call  
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
	// If expression
	else if(node.nodeType === nodeType.ifExpression){
		let parameters = [];
		for(let i = 0; i < node.children.length; i++){
			parameters.push(getExpressionForNode(node.children[i]));
		}
		return{
			type: "SWITCH",
			parameters : parameters
		}
	}
	// Compare expressions
	else if(node.nodeType === nodeType.compareExpression){
		if(node.operation === "="){
			return{
				type: "EQ",
				parameters: [getExpressionForNode(node.children[0]), getExpressionForNode(node.children[1])]
			}
		}
		else if(node.operation === "<"){
			return{
				type: "LT",
				parameters: [getExpressionForNode(node.children[0]), getExpressionForNode(node.children[1])]
			}
		}
		else if(node.operation === "<="){
			return{
				type: "LTEQ",
				parameters: [getExpressionForNode(node.children[0]), getExpressionForNode(node.children[1])]
			}
		}
		else if(node.operation === ">"){
			return{
				type: "LT",
				parameters: [getExpressionForNode(node.children[1]), getExpressionForNode(node.children[0])]
			}
		}
		else if(node.operation === ">="){
			return{
				type: "LTEQ",
				parameters: [getExpressionForNode(node.children[1]), getExpressionForNode(node.children[0])]
			}
		}
		else if(node.operation === "!=" || node.operation === "<>"){
			return{
				type: "LOGIC_NOT",
				parameters: [
					{
						type: "EQ",
						parameters: [getExpressionForNode(node.children[0]), getExpressionForNode(node.children[1])]
					}
				]
			}
		}
	}
	// Logic expressions
	else if(node.nodeType === nodeType.logicOr){
		return{
			type: "LOGIC_OR",
			parameters: [getExpressionForNode(node.children[0]), getExpressionForNode(node.children[1])]
		}
	}
	else if(node.nodeType === nodeType.logicAnd){
		return{
			type: "LOGIC_AND",
			parameters: [getExpressionForNode(node.children[0]), getExpressionForNode(node.children[1])]
		}
	}
	else if(node.nodeType === nodeType.logicXor){
		return{
			type: "LOGIC_XOR",
			parameters: [getExpressionForNode(node.children[0]), getExpressionForNode(node.children[1])]
		}
	}
	else if(node.nodeType === nodeType.logicNot){
		return{
			type: "LOGIC_NOT",
			parameters: [getExpressionForNode(node.children[0])]
		}
	}
	else{
		return {type: "CONSTANT_VALUE", constantValue: "UKNOWN EXPRESSION", "constantType": "STRING"}
	}                            
}


