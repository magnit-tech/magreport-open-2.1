import React, { useState, useRef, useEffect, useMemo } from "react";

import { PivotCSS } from '../../../PivotCSS';

import dataHub from "ajax/DataHub";
import useDebounce from "../lib/useDebounce";

import { Button, DialogActions, FormControlLabel, Switch, TextField } from '@material-ui/core';

import FormulaEditor from "../../../maglangFormulaEditor/FormulaEditor/FormulaEditor";
import DerivedFieldDialogModal from "./DFD_Modal";

import { buildServerExression, checkForDifferenceFromOriginalField } from "../lib/DFD_functions";
import clsx from "clsx";

/**
    * @param {Number} props.reportId - id отчёта
    * @param {Number | String} props.activeIndex - id текущего поля
    * @param {Boolean} props.isReportDeveloper - обладает ли пользователь правами разработчика данного отчёта
    * @param {Array} props.allFieldsAndExpressions - список всех доступных производных полей
    * @param {Array} props.loadedDerivedFields - список всех доступных производных полей (не изменные)
	* @param {Array} props.editedDerivedFields - список всех доступных производных полей (измененные, добавлены новые поля в объекты)
    * @param {*} props.onEdit - function - callback 
	* @param {*} props.onSave - function - callback 
**/

export default function DerivedFieldDialogForm(props){

    const { reportId, activeIndex, isReportDeveloper, allFieldsAndExpressions, loadedDerivedFields, editedDerivedFields } = props

    const classes = PivotCSS();

    const [currentField, setCurrentField] = useState({
        name: '',
        description: '',
        expressionText: '',
        isPublic: false,
        errorMessage: '',
        fontSize: 16
    });

	const debouncedSearchTerm = useDebounce(currentField.name, 500);
    const timeout = useRef()


	useEffect(() => {
        if (activeIndex || activeIndex === 0) {
            if(activeIndex === 'new') {
                setCurrentField(editedDerivedFields[editedDerivedFields.length - 1])
            } else {
                let item = editedDerivedFields.filter((item) => item.id === activeIndex)[0]
                setCurrentField(item)
            }
        }

        
	}, [activeIndex, editedDerivedFields])

	useEffect(
		() => {
            if (debouncedSearchTerm && currentField.originalName !== debouncedSearchTerm) {
                dataHub.derivedFieldController.checkName(reportId, false, debouncedSearchTerm, ({data}) => {
                    if (data) {
                        const correctItem = {...currentField, isCorrect: true}
                        setCurrentField(correctItem)
                        debouncePostObjToSave(correctItem)
                    } else {
                        const incorrectItem = {...currentField, isCorrect: false}
                        setCurrentField(incorrectItem)
                        debouncePostObjToSave(incorrectItem)
                    }
                })
            } else {
                const correctItem = {...currentField, isCorrect: true}
                setCurrentField(correctItem)
                debouncePostObjToSave(correctItem)
            }
		},
		[debouncedSearchTerm, reportId] // eslint-disable-line
	);

    const disabledSaveButton = useMemo(() => {
        if(currentField.needSave) {
            return (currentField.isCorrect && currentField.isFormulaCorrect) ? false : true
        } else {
            return true
        }
    }, [currentField]);


    // Изменение названия поля
	function handleChangeName(name) {
        let item = {...currentField, name: name}
        item['needSave'] = currentField.id !== 'new' ? checkForDifferenceFromOriginalField(item, loadedDerivedFields) : true
        setCurrentField(item)
	}

    // Изменение описания поля
	function handleChangeDesc(desc) {
        let item = {...currentField, description: desc}
        item['needSave'] = currentField.id !== 'new' ? checkForDifferenceFromOriginalField(item, loadedDerivedFields) : true

        setCurrentField(item)
        debouncePostObjToSave(item)
	}

    // Изменение "Общего назначения"
	function handleChangePublic(value) {
        let item = {...currentField, isPublic: value}
        item['needSave'] = currentField.id !== 'new' ? checkForDifferenceFromOriginalField(item, loadedDerivedFields) : true

        setCurrentField(item)
        debouncePostObjToSave(item)
	}

    // Изменения формулы
    const handleFormulaChange = (compilationResult) => {
        let item = {...currentField}
        item["isFormulaCorrect"] = compilationResult.success
        item["expression"] = buildServerExression(compilationResult.treeRoot)
        item["expressionText"] = compilationResult.textToSave
        item['needSave'] = currentField.id !== 'new' ? checkForDifferenceFromOriginalField(item, loadedDerivedFields) : true

        setCurrentField(item)
		debouncePostObjToSave(item)
	};

    // Отправка в родителя измененный список полей и само поле
    function debouncePostObjToSave(item) {
        if(item.id) {
            clearTimeout(timeout.current)

            timeout.current = setTimeout(() => {
                const newList = editedDerivedFields.map(o => {
                    if (o.id === item.id) {
                        return item;
                    }
                    return o;
                });
    
                props.onEdit(newList, item)
            }, 600)
        }
    }

    function handleChangeFontSize(value) {
        let item = {...currentField}
        item["fontSize"] = value

        setCurrentField(item)
		debouncePostObjToSave(item)
    }

	return (
		<div className={clsx(classes.DFD_form, 'MuiPaper-root')}>

            { activeIndex === null && <DerivedFieldDialogModal/> }

            <div>
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
                    value={currentField.name}
                    disabled={!currentField.owner}
                    onChange={(event) => handleChangeName(event.target.value)}
                    error={ currentField.name.replace(/\s/g,"") === "" || !currentField.isCorrect}
                />
                {(currentField.name.replace(/\s/g,"") !== "" && !currentField.isCorrect) && <p className={classes.DFD_formErrorText}>Название поля должно быть уникальным!</p>}
            </div>

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
                value={currentField.description}
                disabled={!currentField.owner}
                onChange={(event) => handleChangeDesc(event.target.value)}
            />

            <FormulaEditor
                key={currentField.id}
                height = "200px"
                fontSize = {currentField.fontSize}
                disabled={!currentField.owner}
                initialCode={currentField.expressionText}
                functions = {allFieldsAndExpressions.functionsList}
                originalFields = {allFieldsAndExpressions.originalFieldsList}
                derivedFields = {allFieldsAndExpressions.derivedFieldsList}
                onChange = {handleFormulaChange}
                onChangeFontSize = {(value) => handleChangeFontSize(value)}
                error = {currentField.errorMessage}
            />

            <DialogActions style={{justifyContent: 'space-between'}}>
                { isReportDeveloper &&
                    <FormControlLabel
                        control={<Switch checked={currentField.isPublic} onChange={(event) => handleChangePublic(event.target.checked)} name="checkedA" />}
                        label="Общего назначения"
                    />
                }

				<Button 
                    variant="contained" 
                    color="primary" 
                    disabled = {disabledSaveButton}
                    onClick={() => props.onSave(currentField)}
				>
					Сохранить
				</Button>
			</DialogActions>
		</div>
	)
}