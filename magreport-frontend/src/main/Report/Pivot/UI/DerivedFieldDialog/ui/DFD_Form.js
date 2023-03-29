import React, { useState, useRef, useEffect, useMemo } from 'react';
import clsx from 'clsx';

import { PivotCSS } from '../../../PivotCSS';

import {
	Button,
	DialogActions,
	FormControlLabel,
	Switch,
	TextField,
} from '@material-ui/core';

import FormulaEditor from '../../../maglangFormulaEditor/FormulaEditor/FormulaEditor';
import DerivedFieldDialogModal from './DFD_Modal';
import DerivedFieldDialogFontSize from './DFD_FontSize';

import {
	buildServerExression,
	checkForDifferenceFromOriginalField,
	fieldNamevalidation,
} from '../lib/DFD_functions';

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

export default function DerivedFieldDialogForm(props) {
	const {
		reportId,
		activeIndex,
		isReportDeveloper,
		allFieldsAndExpressions,
		loadedDerivedFields,
		editedDerivedFields,
		publicFields,
		ownFields,
		otherFields,
	} = props;

	const classes = PivotCSS();

	const [currentField, setCurrentField] = useState({
		name: '',
		description: '',
		expressionText: '',
		isPublic: false,
		errorMessage: '',
		fontSize: 16,
	});

	// const debouncedSearchTerm = useDebounce(currentField.name, 300);
	const timeout = useRef();

	const [nameErrorMsg, setNameErrorMsg] = useState(null);

	useEffect(() => {
		if (activeIndex || activeIndex === 0) {
			if (activeIndex === 'new') {
				setCurrentField(editedDerivedFields[editedDerivedFields.length - 1]);
			} else {
				let item = editedDerivedFields.filter(
					item => item.id === activeIndex
				)[0];
				setCurrentField(item);
			}
		}
	}, [activeIndex, editedDerivedFields]);

	useEffect(
		() => {
			if (currentField.name.trim() === '') {
				const item = { ...currentField, isCorrect: false };
				setCurrentField(item);
				debouncePostObjToSave(item);
				setNameErrorMsg('Введите название поля!');
			} else if (currentField.originalName !== currentField.name) {
				fieldNamevalidation(
					currentField.isPublic,
					currentField.name,
					currentField,
					publicFields,
					ownFields,
					(item, msg) => {
						setCurrentField(item);
						debouncePostObjToSave(item);
						setNameErrorMsg(msg);
					}
				);
			} else {
				const correctItem = { ...currentField, isCorrect: true };
				setCurrentField(correctItem);
				debouncePostObjToSave(correctItem);
			}
		},
		[currentField.name, reportId] // eslint-disable-line
	);

	const derivedFieldsCompletionList = useMemo(() => {
      let result = []

      props.publicFields.forEach((value, key) => {
        result.push({label: key, type: "variable", detail: key, id: value.id})
      })

      props.ownFields.forEach((value, key) => {
        if(props.publicFields.get(key)) {
          result.push({label: `${key}(${value.userName})`, type: "variable", detail: `${key}(${value.userName})`, id: value.id})
        } else {
          result.push({label: key, type: "variable", detail: key, id: value.id})
        }
      })

      props.otherFields.forEach((value, key) => {
        if(props.publicFields.get(key) || props.ownFields.get(key)) {
          result.push({label: `${key}(${value.userName})`, type: "variable", detail: `${key}(${value.userName})`, id: value.id})
        } else {
          result.push({label: key, type: "variable", detail: key, id: value.id})
        }
      })
      
      return result
    }, [props.publicFields, props.ownFields, props.otherFields]);

	const disabledSaveButton = useMemo(() => {
		if (currentField.needSave) {
			return currentField.isCorrect && currentField.isFormulaCorrect
				? false
				: true;
		} else {
			return true;
		}
	}, [currentField]);

	// Изменение названия поля
	function handleChangeName(name) {
		let item = { ...currentField, name: name };
		item['needSave'] =
			currentField.id !== 'new'
				? checkForDifferenceFromOriginalField(item, loadedDerivedFields)
				: true;
		setCurrentField(item);
	}

	// Изменение описания поля
	function handleChangeDesc(desc) {
		let item = { ...currentField, description: desc };
		item['needSave'] =
			currentField.id !== 'new'
				? checkForDifferenceFromOriginalField(item, loadedDerivedFields)
				: true;

		setCurrentField(item);
		debouncePostObjToSave(item);
	}

	// Изменение "Общего назначения"
	function handleChangePublic(value) {
		let item = { ...currentField, isPublic: value };
		item['needSave'] =
			currentField.id !== 'new'
				? checkForDifferenceFromOriginalField(item, loadedDerivedFields)
				: true;

		setCurrentField(item);
		debouncePostObjToSave(item);
	}

	// Изменения формулы
	const handleFormulaChange = compilationResult => {
		let item = { ...currentField };
		item['isFormulaCorrect'] = compilationResult.success;
		item['expression'] = buildServerExression(compilationResult.treeRoot);
		item['expressionText'] = compilationResult.textToSave;
		item['needSave'] =
			currentField.id !== 'new'
				? checkForDifferenceFromOriginalField(item, loadedDerivedFields)
				: true;

		setCurrentField(item);
		debouncePostObjToSave(item);
	};

	// Отправка в родителя измененный список полей и само поле
	function debouncePostObjToSave(item) {
		if (item.id) {
			clearTimeout(timeout.current);

			timeout.current = setTimeout(() => {
				const newList = editedDerivedFields.map(o => {
					if (o.id === item.id) {
						return item;
					}
					return o;
				});

				props.onEdit(newList, item);
			}, 600);
		}
	}

	function handleChangeFontSize(value) {
		let item = { ...currentField };
		item['fontSize'] = value;

		setCurrentField(item);
		debouncePostObjToSave(item);
	}

	return (
		<div className={clsx(classes.DFD_form, 'MuiPaper-root')}>
			{activeIndex === null && <DerivedFieldDialogModal />}

			<div>
				<TextField
					required
					label='Название'
					id='newFieldName'
					placeholder='Введите название производного поля'
					className={classes.CSD_nameField}
					InputLabelProps={{
						shrink: true,
					}}
					variant='outlined'
					value={currentField.name}
					disabled={!currentField.owner}
					onChange={event => handleChangeName(event.target.value)}
					error={currentField.id && !currentField.isCorrect}
				/>
				{currentField.id && !currentField.isCorrect && (
					<p className={classes.DFD_formErrorText}>{nameErrorMsg}</p>
				)}
			</div>

			<TextField
				label='Описание'
				id='newConfigDescription'
				placeholder='Введите описание производного поля'
				multiline
				rows={2}
				className={classes.CSD_descriptionField}
				InputLabelProps={{
					shrink: true,
				}}
				variant='outlined'
				value={currentField.description}
				disabled={!currentField.owner}
				onChange={event => handleChangeDesc(event.target.value)}
			/>

			<DerivedFieldDialogFontSize
				fontSize={currentField.fontSize}
				showDerivedFunctionGuied={props.showDerivedFunctionGuied}
				onChangeFontSize={value => handleChangeFontSize(value)}
				onOpenDerivedFunctionGuied={() =>
					props.onToggleDerivedFunctionGuied(true)
				}
			/>

			<FormulaEditor
				publicFields={publicFields}
				ownFields={ownFields}
				otherFields={otherFields}
				key={currentField.id}
				height='100px'
				fontSize={currentField.fontSize}
				disabled={!currentField.owner}
				initialCode={currentField.expressionText}
				functions={allFieldsAndExpressions.functionsList}
				originalFields={allFieldsAndExpressions.originalFieldsList}
				derivedFields={derivedFieldsCompletionList}
				onChange={handleFormulaChange}
			/>

			<p className={classes.DFD_errorMessage}> {currentField.errorMessage} </p>

			<DialogActions style={{ justifyContent: 'space-between' }}>
				{isReportDeveloper && (
					<FormControlLabel
						disabled={!currentField.owner}
						control={
							<Switch
								checked={currentField.isPublic}
								onChange={event => handleChangePublic(event.target.checked)}
								name='checkedA'
							/>
						}
						label='Общего назначения'
					/>
				)}

				<Button
					variant='contained'
					color='primary'
					disabled={disabledSaveButton}
					onClick={() => props.onSave(currentField)}
				>
					Сохранить
				</Button>
			</DialogActions>
		</div>
	);
}
