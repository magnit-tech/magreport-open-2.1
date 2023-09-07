import React, { useState } from 'react';

import { PivotCSS } from '../../PivotCSS';

import { DialogActions, Button, FormGroup, FormControlLabel, TextField, RadioGroup, Radio, Box, Tooltip, IconButton } from '@material-ui/core';

// icons
import Icon from '@mdi/react'
import { mdiDeleteSweep, mdiClose } from '@mdi/js';

/**
	* @param {Object} props.cellData - объект данных, сгенерированных в PivotPanel
	* @param {Object} props.sortingValues - глобальный объект сортировки
	* @param {Boolean} props.rowDifferent - отличаются ли строковые значения при клике на ячейку со значениями уже заданной сортировки
	* @param {Boolean} props.columnDifferent - отличаются ли колонночные значения при клике на ячейку со значениями уже заданной сортировки
	* @param {*} props.onCancel - function - callback отмены/закрытия модального окна
	* @param {*} props.onDelete - function - callback удаление сортировки
	* @param {*} props.onSave - function - callback сохранения сортировки
**/

export default function CurrentSort(props){

	const classes = PivotCSS();
	
	const cellData = props.cellData
	const { rowSort, columnSort } = props.sortingValues

	const rowDifferent = props.rowDifferent
	const columnDifferent = props.columnDifferent

	// Создание полного имени для cellData с использованием кортежа имен строк\столбцов
	const CDrowName = cellData.rowName?.length > 0 ? `${cellData.rowName.join(' - ')} - ${cellData.metricName}` : cellData.metricName
	const CDcolumnName = cellData.columnName?.length > 0 ? `${cellData.columnName.join(' - ')} - ${cellData.metricName}` : cellData.metricName

	// Задаем имена для инпутов
	const rowName = rowDifferent ? CDrowName : rowSort.name
	const columnName = columnDifferent ? CDcolumnName : columnSort.name

	// State для Radio-buttons
	const [rowSortingOrder, setRowSortingOrder] = useState(rowDifferent ? '' : rowSort.data[0].order)
	const [columnSortingOrder, setColumnSortingOrder] = useState(columnDifferent ? '' : columnSort.data[0].order)

	// Сохранение
	const handleSaveSorting = () => {

		let sortObjForSave = {
			rowSort: {
				name: rowName,
				rowName: rowDifferent ? cellData.rowName : rowSort.rowName,
				data: [
                    {
                        "metricId": rowSort.data[0].metricId,
                        "order": rowSortingOrder,
                        "tuple": rowDifferent ? cellData.rowName : rowSort.rowName
                    }
                ] 
			},
			columnSort: {
				name: columnName,
				columnName: columnDifferent ? cellData.columnName : columnSort.columnName,
				data: [
                    {
                        "metricId": columnSort.data[0].metricId,
                        "order": columnSortingOrder,
                        "tuple": columnDifferent ? cellData.columnName : columnSort.columnName
                    }
                ] 
			}
		}

		props.onSave(sortObjForSave)
	};

  	return (
        <>
			<div className={classes.SD_root}>
				<Box>
					<TextField
						id="sortBy"
						label="Сортировка по строке:"
						value={rowName}
						InputProps={{
							readOnly: true,
						}}
						fullWidth
						multiline
						rows = {rowName.length > 40 ? 2 : 1}
						variant="outlined"
					/>

					{ !props.sortingDialogAfterTools && 
						<div style={{ padding: '0 0 35px'}}>
							<FormGroup style={{flexWrap: 'nowrap', flexDirection: 'row'}}>
								<RadioGroup 
									value={rowSortingOrder} 
									onChange={(e) => setRowSortingOrder(e.target.value)} 
									style={{flexDirection: 'row'}}
								>
									<FormControlLabel value="Ascending" control={<Radio />} label="По возрастанию"/>
									<FormControlLabel value="Descending" control={<Radio />} label="По убыванию"/>
								</RadioGroup>

								<Tooltip title = 'Очистить' placement="top">
									<FormControlLabel 
										control = {
											<IconButton aria-label="delete">
												<Icon path={mdiClose} size={1} />
											</IconButton>
										}
										onClick={() => setRowSortingOrder('', 'rowSort')}
									/>
								</Tooltip>
							</FormGroup>
						</div>
					}
				</Box>

				<Box>
					<TextField
						id="sortBy"
						label="Сортировка по столбцу:"
						value={columnName}
						InputProps={{
							readOnly: true,
						}}
						fullWidth
						multiline
						rows = {columnName.length > 40 ? 2 : 1}
						variant="outlined"
					/>

					{ !props.sortingDialogAfterTools && 
						<div style={{ padding: '0 0 15px'}}>
							<FormGroup style={{flexWrap: 'nowrap', flexDirection: 'row'}}>
								<RadioGroup 
									value={columnSortingOrder} 
									onChange={(e) => setColumnSortingOrder(e.target.value)} 
									style={{flexDirection: 'row'}}
								>
									<FormControlLabel value="Ascending" control={<Radio />} label="По возрастанию"/>
									<FormControlLabel value="Descending" control={<Radio />} label="По убыванию"/>
								</RadioGroup>
								<Tooltip title = 'Очистить' placement="top">
									<FormControlLabel 
										control = {
											<IconButton aria-label="delete">
												<Icon path={mdiClose} size={1} />
											</IconButton>
										}
										onClick={() => setColumnSortingOrder('')}
									/>
								</Tooltip>
							</FormGroup>
						</div>
					}
				</Box>

				<div className={classes.SD_deleteSortingBtn}>
					<Button color="secondary" onClick={() => props.onDelete()}>
						<Icon path={mdiDeleteSweep} size={0.9} color="red"/>
						<span className={classes.SD_deleteSortingBtnLabel}>Очистить всю сортировку</span>
					</Button>
				</div>
			</div>

			<DialogActions>
				<Button 
					color="primary"
					onClick={handleSaveSorting}
				>
					Сохранить
				</Button>
				<Button autoFocus color="primary" onClick={() =>props.onCancel(false)}>
					Отменить
				</Button>
			</DialogActions>
		</>
  	);
}