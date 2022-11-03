import React from 'react';

import { PivotCSS } from '../../PivotCSS';

import { Button, TextField, Box, Typography, DialogActions} from '@material-ui/core';

// icons
import Icon from '@mdi/react'
import { mdiDeleteSweep } from '@mdi/js';

/**
	* @param {Boolean} props.sortingDialogAfterTools - boolean-значение для определения был ли клик по кнопке "Сортировка" из панели задач
	* @param {*} props.onCancel - function - callback отмены/закрытия модального окна
	* @param {*} props.onDelete - function - callback удаление сортировки
**/


export default function CurrentSortWithoutActionBtns(props){

	const classes = PivotCSS();

	const areThereSortingValues = Object.keys(props.sortingValues).length !== 0

	const {rowSort, columnSort} = props.sortingValues

  	return (
        <>
			<div className={classes.SD_root_without_action}>
				{ !areThereSortingValues
					?
						<Box className={classes.CD_wrapperList}>
							<Typography>Сортировка не задана</Typography>
						</Box>
						
					:
					<>
						<Box className={classes.SD_inputText_without_action}>
							<TextField
								id="sortBy"
								label="Сортировка по строке:"
								value={rowSort.data[0].order !== '' ? rowSort.name : ''}
								InputProps={{
									readOnly: true,
								}}
								fullWidth
								multiline
								rows = {rowSort.name.length > 40 ? 2 : 1}
								variant="outlined"
								disabled={rowSort.data[0].order === ''}
							/>
						</Box>

						<Box className={classes.SD_inputText_without_action}>
							<TextField
								id="sortBy"
								label="Сортировка по столбцу:"
								value={columnSort.data[0].order !== '' ? columnSort.name : ''}
								InputProps={{
									readOnly: true,
								}}
								fullWidth
								multiline
								rows = {columnSort.name.length > 40 ? 2 : 1}
								variant="outlined"
								disabled={columnSort.data[0].order === ''}
							/>
						</Box>

						<div>
							<Button color="secondary" onClick={() => props.onDelete()}>
								<Icon path={mdiDeleteSweep} size={0.9} color="red"/>
								<span className={classes.SD_deleteSortingBtnLabel}>Очистить всю сортировку</span>
							</Button>
						</div>
					</>
				}
			</div>

			<DialogActions>
				<Button autoFocus color="primary" onClick={() => props.onCancel(false)}>
					Отменить
				</Button>
			</DialogActions>
		</>
  );
}