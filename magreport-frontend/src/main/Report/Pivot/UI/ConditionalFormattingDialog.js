import React, { useState } from 'react';

import clsx from "clsx";

import { PivotCSS } from '../PivotCSS';

import Draggable from 'react-draggable';

import { Dialog, DialogActions, DialogTitle, Paper, Box, TextField, Button } from "@material-ui/core";

// icons
import Icon from '@mdi/react'
import { mdiDeleteSweep } from '@mdi/js';

// Color Picker
import { CompactPicker } from 'react-color';

/**
	* @param {Boolean} props.open - boolean-значение отображения модального окна
	* @param {Object} props.cellData - объект данных, сгенерированных в PivotPanel
	* @param {*} props.onCancel - function - callback отмены/закрытия модального окна
	* @param {*} props.onSaveConditionalFormatting - function - callback сохранения условного форматирования
**/

//Перетаскивание модального окна
function PaperComponent(props) {
    return (
		<Draggable handle="#dragg-title" cancel={'[class*="MuiDialogContent-root"]'}>
			<Paper {...props} style={{width: '500px'}}/>
		</Draggable>
    );
}


export default function FormattingDialog(props){

	const classes = PivotCSS();

	const cellData = props.cellData

	const [active, setActive] = useState(null)

	const [errorValueFrom, setErrorValueFrom] = useState(null)
	const [errorValueTo, setErrorValueTo] = useState(null)

	const [ranges, setRanges] = useState( cellData?.conditionalFormatting.length > 0 ? cellData.conditionalFormatting :
	[
		{
			id: 1,
			color: '#008000',
			valueFrom: 'Infinity',
			valueTo: 'Infinity',
		},
	])

	const [isOpen, setOpen] = useState(false);

	// Цвет шрифта
	const handleChangeFontColor = (hex) => {
		const arr = ranges.map(item => {
			if(item.id === active.id) {
				return { ...item, color: hex }
			} else {
				return item
			}
		})
		setActive({...active, color: hex})
		setRanges(arr)
	}

	function computedValue(value) {
		if(value === 'Infinity') {
			if(active.valueFrom === 'Infinity') {
				return 0
			}
				return +active.valueFrom + 100
		} else if (value === 0) {
			if(active.valueFrom === 'Infinity') {
				return -100
			}
				return (Number(active.valueFrom) + Number(active.valueTo)) / 2
		} else {
			if(active.valueFrom === 'Infinity') {
				if(value > 0) {
					return +value / 2
				}
				return +value * 2
			} else {
				return (Number(active.valueFrom) + Number(active.valueTo)) / 2
			}
		}
	}

	function computedValueFrom() {
		const indexActive = ranges.findIndex(i => i.id === active.id)

		if(ranges[indexActive + 1]) {
			return ranges[indexActive + 1].valueFrom
		}

		return 'Infinity'
	}

	// Добавление
	function handleAdd() {
		
		const newRanges = ranges.map((range) => (
			range.id === active.id
			  ? { ...range, valueTo: computedValue(active.valueTo)}
			  : range
		));

		const newRangesObj = {
			...active,
			id: Math.random(),
			valueFrom: computedValue(active.valueTo),
			valueTo: computedValueFrom(),
		}

		newRanges.splice(ranges.findIndex(i => i.id === active.id) + 1, 0, newRangesObj);

		setRanges(newRanges)

		setActive({...active, valueTo: computedValue(active.valueTo)})
	}

	// Удаление
	function handleDelete() {
		let newArray = ranges.filter(item => item.id !== active.id)

		if(ranges.findIndex(i => i.id === active.id) === 0) {
			newArray[ranges.findIndex(i => i.id === active.id)].valueFrom = active.valueFrom
		} else {
			newArray[ranges.findIndex(i => i.id === active.id) - 1].valueTo = active.valueTo
		}

		setRanges(newArray)
		setActive(null)
	}

	// Изменение поля "От:"
	function handleChangeValueFrom(event) {
		setErrorValueTo(null)
		setErrorValueFrom(null)
		const value = Number(event.target.value)
		const valueTo = active.valueTo
		const valueFrom = ranges[ranges.findIndex(i => i.id === active.id) - 1].valueFrom 

		if (value <= valueFrom && valueFrom !== 'Infinity') {
			setErrorValueFrom((<span>Макс. значение: {valueFrom}</span>))
		} else if(value >= valueTo && valueTo !== 'Infinity') {
			setErrorValueFrom((<span>Мин. значение: {valueTo}</span>))
		} else {
			const arr = ranges.map(item => {
				if (item.id === active.id) {
					return { ...item, valueFrom: value }
				} else {
					return item
				}
			})
			arr[ranges.findIndex(i => i.id === active.id) - 1].valueTo = value
			setActive({...active, valueFrom: value})
			setRanges(arr)
		}
	}

	// Изменение поля "До:"
	function handleChangeValueTo(event) {
		setErrorValueTo(null)
		setErrorValueFrom(null)
		const value = Number(event.target.value)
		const valueFrom = active.valueFrom
		const valueTo = ranges[ranges.findIndex(i => i.id === active.id) + 1].valueTo

		if (value <= valueFrom && valueFrom !== 'Infinity') {
			setErrorValueTo((<span>Мин. значение: {valueFrom}</span>))
		} else if(value >= valueTo && valueTo !== 'Infinity') {
			setErrorValueTo((<span>Макс. значение: {valueTo}</span>))
		} else {
			const arr = ranges.map(item => {
				if (item.id === active.id) {
					return { ...item, valueTo: value }
				} else {
					return item
				}
			})
			arr[ranges.findIndex(i => i.id === active.id) + 1].valueFrom = value
			setActive({...active, valueTo: value})
			setRanges(arr)
		}
	}

  	return (
        <Dialog
            open={props.open}
            PaperComponent={PaperComponent}
            aria-labelledby="dragg-title"
        >
            <DialogTitle style={{ cursor: 'move' }} id="dragg-title"> Условное форматирование </DialogTitle>

			<Box className={classes.FD_metrcisNameWrapper}>
				<Box className={classes.FD_metrcisName}>
					<TextField
						id="sortBy"
						label="Условное форматирование метрики:"
						value={cellData.metricName}
						InputProps={{
							readOnly: true,
						}}
						fullWidth
						multiline
						rows = {cellData.metricName.length > 37 ? 2 : 1}
						variant="outlined"
					/>
				</Box>
			</Box>

			<Box className={classes.FD_root} style={{padding: '24px'}}>
				<Box className={classes.CFD_wrapper}>
					{ranges.map(item => {
						return (
						<Box 
							key={item.id}
							className={clsx(classes.CFD_item, active?.id === item.id && 'active', (!!errorValueFrom || !!errorValueTo) && classes.CFD_errorChoiceBlock)} 
							style={{backgroundColor: item.color}}
							onClick={() => (!!errorValueFrom || !!errorValueTo) ? '' : setActive(item)}
						>
						</Box>
						)
					})}
				</Box>

				{ active && 
					<Box>
						<Box className={classes.CFD_choiceBlock}>
							<span>Значения:</span>
							<Box className={classes.CFD_values}>
								{ active.valueFrom === 'Infinity' 
								?
									<TextField 
										label="От:" 
										type="text" 
										value="-&#8734;"
										variant="outlined" 
										margin="dense"
										className={classes.textField}
										onChange={handleChangeValueFrom}
										disabled
									/>
								:
									<Box className={classes.CFD_errorValues}>
										<TextField 
											error={!!errorValueFrom}
											label="От:" 
											type="number" 
											value={active.valueFrom} 
											variant="outlined" 
											margin="dense"
											className={classes.textField}
											onChange={handleChangeValueFrom}
										/>	
										{errorValueFrom}
									</Box>
								}

								{ active.valueTo === 'Infinity' 
								?
									<TextField 
										label="До:" 
										type="text" 
										value="+&#8734;" 
										variant="outlined" 
										margin="dense"
										className={classes.textField}
										onChange={handleChangeValueTo}
										disabled
									/>
								:	
									<Box className={classes.CFD_errorValues}>
										<TextField
											error={!!errorValueTo}
											label="До:" 
											type="number" 
											value={active.valueTo} 
											variant="outlined" 
											margin="dense"
											className={classes.textField}
											onChange={handleChangeValueTo}
										/>	
										{errorValueTo}
									</Box>	
								}
							</Box>
						</Box>

						<Box className={clsx(classes.FD_fontColorSection, classes.CFD_fontColor)}>
							<span>Задний фон:</span>
							<Box
								className={classes.FD_fontColorWrapper}
								onClick={() => setOpen(!isOpen)}
							>
								<Box 
									className={classes.FD_fontColorCircle}
									style={{ background: active.color }}
								/>
								{isOpen && 
									<CompactPicker
										color={active.color}
										onChangeComplete={(color) => handleChangeFontColor(color.hex)}
									/>
								}
							</Box>
						</Box>

						<Box className={classes.CFD_actionBtns}>
							{ ranges.length > 1 && <button className={classes.CFD_deleteBtn} onClick={handleDelete}>Удалить</button> }
							<button className={classes.CFD_divideBtn} onClick={handleAdd} disabled={ranges.length >= 10 }>Разделить</button>
						</Box>

					</Box>
				}

				{ cellData.conditionalFormatting.length > 0 && 
					<Box className={classes.SD_deleteSortingBtn} style={{marginTop: '30px'}}>
						<Button color="secondary" onClick={() => props.onSaveConditionalFormatting(cellData.index, [])} >
							<Icon path={mdiDeleteSweep} size={0.9} color="red"/>
							<span className={classes.SD_deleteSortingBtnLabel}>Очистить форматирование</span>
						</Button>
					</Box>
				}

			</Box>

			<DialogActions>
				<Button 
					variant="contained"
					color="primary" 
					disabled={!!errorValueFrom || !!errorValueTo}
					onClick={() => props.onSaveConditionalFormatting(cellData.index, ranges)} 
				>
					Сохранить
				</Button>
				<Button variant="contained" color="primary" onClick={() => props.onCancel()}>
					Отменить
				</Button>
			</DialogActions>
      	</Dialog>
  	);
}