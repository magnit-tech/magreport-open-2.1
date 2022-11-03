import React, { useRef, useState } from 'react';

import { PivotCSS } from '../../PivotCSS';

import { FormGroup, FormControlLabel, TextField, RadioGroup, Radio, Box, Tooltip, IconButton, Button, DialogActions } from '@material-ui/core';

// icons
import Icon from '@mdi/react'
import { mdiClose } from '@mdi/js';

/**
	* @param {Object} props.cellData - объект данных, сгенерированных в PivotPanel
	* @param {*} props.onCancel - function - callback отмены/закрытия модального окна
	* @param {*} props.onDelete - function - callback удаление сортировки
	* @param {*} props.onSave - function - callback сохранения сортировки
**/


export default function NewSorting(props){

	const classes = PivotCSS();

	const cellData = props.cellData

	const rowName = cellData.rowName;
	const columnName = cellData.columnName;

	// Создание полного имени для cellData с использованием кортежа имен строк\столбцов
	const CDrowName = rowName.length > 0 ? `${cellData.rowName.join(' - ')} - ${cellData.metricName}` : cellData.metricName
	const CDcolumnName = columnName.length > 0 ? `${cellData.columnName.join(' - ')} - ${cellData.metricName}` : cellData.metricName

	// Задаем имена для инпутов
	const fullMetricNameWithRowNames = useRef(CDrowName)
	const fullMetricNameWithColumnNames = useRef(CDcolumnName)

	// State для Radio-buttons
	const [newRowSortingOrder, setNewRowSortingOrder] = useState('')
	const [newColumnSortingOrder, setNewColumnSortingOrder] = useState('')

	// Сохранение
	const handleSaveSorting = () => {

		let sortObjForSave = {
			rowSort: {
				name: fullMetricNameWithRowNames.current,
				rowName: rowName,
				data: [
                    {
                        "metricId": cellData.index,
                        "order": newRowSortingOrder,
                        "tuple": rowName
                    }
                ] 
			},
			columnSort: {
				name: fullMetricNameWithColumnNames.current,
				columnName: columnName,
				data: [
                    {
                        "metricId": cellData.index,
                        "order": newColumnSortingOrder,
                        "tuple": columnName
                    }
                ] 
			}
		}

		props.onSave(sortObjForSave)
	};

  	return (
        <>
			<div className={classes.SD_root}>
				<Box style={{ marginBottom: '35px'}}>
					<TextField
						id="sortBy"
						label="Сортировка по строке:"
						value={fullMetricNameWithRowNames.current}
						InputProps={{
							readOnly: true,
						}}
						fullWidth
						multiline
						rows = {fullMetricNameWithRowNames.current.length > 40 ? 2 : 1}
						variant="outlined"
					/>

					<div>
						<FormGroup style={{flexWrap: 'nowrap', flexDirection: 'row'}}>
							<RadioGroup 
								value={newRowSortingOrder} 
								onChange={(e) => setNewRowSortingOrder(e.target.value)} 
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
									onClick={() => setNewRowSortingOrder('')}
								/>
							</Tooltip>
						</FormGroup>
					</div>
				</Box>

				<Box>
					<TextField
						id="sortBy"
						label="Сортировка по столбцу:"
						value={fullMetricNameWithColumnNames.current}
						InputProps={{
							readOnly: true,
						}}
						fullWidth
						multiline
						rows = {fullMetricNameWithColumnNames.current.length > 40 ? 2 : 1}
						variant="outlined"
					/>

					<div>
						<FormGroup style={{flexWrap: 'nowrap', flexDirection: 'row'}}>
							<RadioGroup 
								value={newColumnSortingOrder} 
								onChange={(e) => setNewColumnSortingOrder(e.target.value)} 
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
									onClick={() => setNewColumnSortingOrder('')}
								/>
							</Tooltip>
						</FormGroup>
					</div>
				</Box>
			</div>

			<DialogActions>
				<Button color="primary" onClick={handleSaveSorting} >
					Сохранить
				</Button>
				<Button autoFocus color="primary" onClick={() =>props.onCancel(false)}>
					Отменить
				</Button>
			</DialogActions>
		</>
  );
}