import React from 'react';

import Draggable from 'react-draggable';

import { Paper, Dialog, DialogTitle } from '@material-ui/core';

import CurrentSortWithoutActionBtns from './CurrentSortWithoutActionBtns'
import CurrentSort from './CurrentSort'
import NewSort from './NewSort'

/**
	* @param {Boolean} props.open - boolean-значение отображения модального окна
	* @param {Object} props.cellData - объект данных, сгенерированных в PivotPanel
	* @param {Object} props.sortingValues - глобальный объект сортировки
	* @param {Boolean} props.sortingDialogAfterTools - boolean-значение для определения был ли клик по кнопке "Сортировка" из панели задач
	* @param {*} props.onCancel - function - callback отмены/закрытия модального окна
	* @param {*} props.onDelete - function - callback удаление сортировки
	* @param {*} props.onSave - function - callback сохранения сортировки
**/

//Перетаскивание модального окна
function PaperComponent(props) {
    return (
		<Draggable handle="#drag-title" cancel={'[class*="MuiDialogContent-root"]'}>
			<Paper {...props}/>
		</Draggable>
    );
}


export default function SortingDialog(props){

	const cellData = props.cellData
	const sortingValues = props.sortingValues
	const sortingDialogAfterTools = props.sortingDialogAfterTools

	const { rowSort, columnSort } = props.sortingValues

	const areThereSortingValues = Object.keys(props.sortingValues).length !== 0

	const isCellRowNamesDifferent = areThereSortingValues && JSON.stringify(rowSort.rowName) === JSON.stringify(cellData.rowName)
	const isCellColumnNamesDifferent = areThereSortingValues && JSON.stringify(columnSort.columnName) === JSON.stringify(cellData.columnName)


	function NewSortComponent() {
		return (
			<NewSort
				cellData = {cellData}
				onSave = {(obj) => props.onSave(obj)}
				onCancel = {() => props.onCancel(false)}
				onDelete = {() => props.onDelete()}
			/>
		)
	}
	function CurrentSortComponent({rowDifferent, columnDifferent}) {
		return (
			<CurrentSort
				cellData = {cellData}
				sortingValues = {sortingValues}
				rowDifferent = {rowDifferent}
				columnDifferent = {columnDifferent}
				onSave = {(obj) => props.onSave(obj)}
				onCancel = {() => props.onCancel(false)}
				onDelete = {() => props.onDelete()}
			/>
		)
	}

	function CurrentOrNewSort() {
		
		if (rowSort.data[0].metricId !== cellData.index && columnSort.data[0].metricId !== cellData.index) {
			return <NewSortComponent/>
		} else {
			if (isCellRowNamesDifferent || isCellColumnNamesDifferent) {
				return <CurrentSortComponent rowDifferent={!isCellRowNamesDifferent} columnDifferent={!isCellColumnNamesDifferent}/>
			}
			return <NewSortComponent/>
		}
	}


  	return (
        <Dialog
            open={props.open}
            PaperComponent={PaperComponent}
            aria-labelledby="drag-title"
        >
            <DialogTitle style={{ cursor: 'move' }} id="drag-title"> {props.sortingDialogAfterTools ? 'Текущая сортировка' : 'Сортировка'} </DialogTitle>

			{sortingDialogAfterTools 
				?
				<CurrentSortWithoutActionBtns
					sortingValues = {sortingValues}
					onCancel = {() => props.onCancel(false)}
					onDelete = {() => props.onDelete()}
				/>
				:
				( areThereSortingValues ? <CurrentOrNewSort/> : <NewSortComponent/>)
			}

      </Dialog>
  );
}