import React, { useState, useEffect } from 'react';

import clsx from "clsx";

import { PivotCSS } from '../PivotCSS';

import { withStyles } from '@material-ui/core/styles';

import Draggable from 'react-draggable';

import { Dialog, DialogActions, DialogTitle, Paper, Box, TextField, Button, IconButton, ButtonGroup, Tooltip, FormControlLabel, FormControl, Select, MenuItem, InputBase, Checkbox } from "@material-ui/core";

// icons
import Icon from '@mdi/react'
import { mdiDeleteSweep, mdiTextBoxRemoveOutline } from '@mdi/js';
// import { mdiChevronLeft, mdiChevronRight } from '@mdi/js';

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
			<Paper {...props} style={{width: '550px'}}/>
		</Draggable>
    );
}

// Создание списка c MenuItems для селекта "Размер шрифта"
const menuItems = () => {
	const arr = []
	for (let i = 1; i < 31; i++) {
		arr.push(<MenuItem key={i} value={i}>{i}</MenuItem>)
	}
	return arr
}

const SelectInput = withStyles((theme) => ({
	root: {
		  maxWidth: '100px',
	},
	input: {
		borderRadius: 4,
		position: 'relative',
		backgroundColor: theme.palette.background.paper,
		border: '1px solid #ced4da',
		fontSize: 16,
		padding: '10px 26px 10px 12px',
		transition: theme.transitions.create(['border-color', 'box-shadow']),
		'&:focus': {
			borderRadius: 4,
			borderColor: '#80bdff',
			boxShadow: '0 0 0 0.2rem rgba(0,123,255,.25)',
		},
	},
}))(InputBase);


export default function FormattingDialog(props){

	const classes = PivotCSS();

	const cellData = props.cellData

	const ITEM_HEIGHT = 48;
	const ITEM_PADDING_TOP = 8;
	const MenuProps = {
		PaperProps: {
			style: {
			maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
			width: 250,
			},
		},
	};

	useEffect(() => {
		let newConditionalArray = cellData.conditionalFormatting

		if (newConditionalArray.length > 0) {
			newConditionalArray.map(function(current) {
				if(!current.hasOwnProperty('rounding')) {
					current.rounding = cellData.style[0].rounding
					current.fontStyle = cellData.style[0].fontStyle
					current.fontSize = cellData.style[0].fontSize
					current.fontColor = cellData.style[0].fontColor || '#000000'
					return current;
				}
				return current
			});
			setRanges(newConditionalArray)
			setActive(newConditionalArray[0])
		} else {
			const item = {
				id: 1,
				color: '#FCDC00',
				valueFrom: 'Infinity',
				valueTo: 'Infinity',
				rounding: cellData.style[0].rounding,
				fontStyle: cellData.style[0].fontStyle,
				fontSize: cellData.style[0].fontSize,
				fontColor: cellData.style[0].fontColor || '#000000',
			}
			setRanges([item])
			setActive(item)
		}

	}, [cellData]) 

	const [active, setActive] = useState(null)

	const [errorValueFrom, setErrorValueFrom] = useState(null)
	const [errorValueTo, setErrorValueTo] = useState(null)

	const [ranges, setRanges] = useState([])

	const [isOpenColor, setOpenColor] = useState(false);
	const [isOpenFontColor, setOpenFontColor] = useState(false);

	function changeActiveAndRanges(field) {
		const arr = ranges.map(item => {
			if (item.id === active.id) {
				return { ...item, ...field }
			} else {
				return item
			}
		})
		setRanges(arr)
		setActive({...active, ...field})
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

		const value = event.target.value 
		const valueTo = active.valueTo
		const valueFrom = ranges[ranges.findIndex(i => i.id === active.id) - 1].valueFrom 

		if (Number(value) <= Number(valueFrom) && valueFrom !== 'Infinity') {
			setErrorValueFrom((<span>Макс. значение: {valueFrom}</span>))
		} else if(Number(value) >= Number(valueTo) && valueTo !== 'Infinity') {
			setErrorValueFrom((<span>Мин. значение: {valueTo}</span>))
		} else if (value === '-' || value === '+' || value.endsWith('.') || value.trim() === '') {
			setErrorValueFrom((<span>Недопустимое значение</span>))
		}

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

	// Изменение поля "До:"
	function handleChangeValueTo(event) {
		setErrorValueTo(null)
		setErrorValueFrom(null)

		const value = event.target.value
		const valueFrom = active.valueFrom
		const valueTo = ranges[ranges.findIndex(i => i.id === active.id) + 1].valueTo

		if (Number(value) <= Number(valueFrom) && valueFrom !== 'Infinity') {
			setErrorValueTo((<span>Мин. значение: {valueFrom}</span>))
		} else if(Number(value) >= Number(valueTo) && valueTo !== 'Infinity') {
			setErrorValueTo((<span>Макс. значение: {valueTo}</span>))
		} else if (value === '-' || value === '+' || value.endsWith('.') || value.trim() === '') {
			setErrorValueTo((<span>Недопустимое значение</span>))
		}

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

	// Вычесление числа десятичных знаков (округление)
	// const handleChangeRounding = (action) => {
	// 	let number = active.rounding;

	// 	if (action === 'decrase' && number > 0) {
	// 		changeActiveAndRanges({rounding: --number} )
	// 	} else if (action === 'incrase' && (number >= 0 && number < 20)) {
	// 		changeActiveAndRanges({rounding: ++number})
	// 	}
	// }

	const handleValueInput = (e) => {
		const onlyNums = e.target.value.replace(/[^-,0-9.]/g, '').replace(/(\..*?)\..*/g, '$1').replace(/^0[^.]/, '0');
		e.target.value = onlyNums;
    }

	// Цвет шрифта - Авто
	const handleSetAutoFontColor = (bool) => {
		if (bool === true) changeActiveAndRanges({fontColor: ''});
		setOpenFontColor(false)
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
							item.color === "inherit" 
								? 
									<Box
										key={item.id}
										className={clsx(classes.CFD_item, active?.id === item.id && 'active', (!!errorValueFrom || !!errorValueTo) && classes.CFD_errorChoiceBlock)}
										onClick={() => (!!errorValueFrom || !!errorValueTo) ? '' : setActive(item)}
									>
										<svg version="1.1" xmlns="http://www.w3.org/2000/svg" viewBox="-500 -500 1000 1000" preserveAspectRatio="xMidYMin slice"

											style={{position: 'absolute', left: '0', top: '0'}}
										> 
											<defs>
												<pattern id="CheckerPattern" patternUnits="userSpaceOnUse" x="0" y="0" width="100" height="100" viewBox="0 0 30 30">
													<rect fill="#FFF" stroke="none" x="0" y="0" height="15" width="15"/>
													<rect fill="#FFF" stroke="none" x="15" y="15" height="15" width="15"/>
													<rect fill="#E5E5E5" stroke="none" x="15" y="0" height="15" width="15"/>
													<rect fill="#E5E5E5" stroke="none" x="0" y="15" height="15" width="15"/>
												</pattern>
											</defs>
											<g>
												<rect fill="url(#CheckerPattern)" stroke="none" x="-500" y="-500" width="1000" height="1000"/>
											</g>
										</svg>
										<span style={{
												position: "inherit", 
												color: item.fontColor, 
												fontSize: item.fontSize, 
												fontStyle: item.fontStyle === 'italic' && item.fontStyle, 
												fontWeight: item.fontStyle === 'bold' && item.fontStyle,
												textDecoration: item.fontStyle === 'underline' && item.fontStyle,
											}}
										>
											123
										</span>
									</Box>
								: 
									<Box 
										key={item.id}
										className={clsx(classes.CFD_item, active?.id === item.id && 'active', (!!errorValueFrom || !!errorValueTo) && classes.CFD_errorChoiceBlock)} 
										style={{backgroundColor: item.color}}
										onClick={() => (!!errorValueFrom || !!errorValueTo) ? '' : setActive(item)}
									>
										<span style={{
												position: "inherit", 
												color: item.fontColor, 
												fontSize: item.fontSize, 
												fontStyle: item.fontStyle === 'italic' && item.fontStyle, 
												fontWeight: item.fontStyle === 'bold' && item.fontStyle,
												textDecoration: item.fontStyle === 'underline' && item.fontStyle,
											}}
										>
											123
										</span>
									</Box>
						)
					})}
				</Box>

				{ active && 
					<Box>
						{/* Значения */}
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
											type="text" 
											value={active.valueFrom} 
											variant="outlined" 
											margin="dense"
											className={classes.textField}
											onInput = {handleValueInput}
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
											type="text" 
											value={active.valueTo} 
											variant="outlined" 
											margin="dense"
											className={classes.textField}
											onInput = {handleValueInput}
											onChange={handleChangeValueTo}
										/>	
										{errorValueTo}
									</Box>	
								}
							</Box>
						</Box>

						{/* Цвет фона */}
						<Box className={classes.FD_wrapperForActionSections}>
							<Box className={classes.FD_fontColorSection}>
								<Box whiteSpace="nowrap"> Цвет фона: </Box>
								<Box
									className={classes.FD_fontColorWrapper}
									onClick={() => setOpenColor(!isOpenColor)}
								>
									{active.color !== 'inherit' 
										? 
										<Box 
											className={classes.FD_fontColorCircle}
											style={{ background: active.color }}
										/>
										:
										<svg version="1.1" xmlns="http://www.w3.org/2000/svg" viewBox="-500 -500 1000 1000" className={classes.FD_fontColorCircleSVG}> 
											<defs>
												<pattern id="CheckerPattern" patternUnits="userSpaceOnUse" x="0" y="0" width="250" height="250" viewBox="0 0 30 30">
													<rect fill="#FFF" stroke="none" x="0" y="0" height="15" width="15"/>
													<rect fill="#FFF" stroke="none" x="15" y="15" height="15" width="15"/>
													<rect fill="#E5E5E5" stroke="none" x="15" y="0" height="15" width="15"/>
													<rect fill="#E5E5E5" stroke="none" x="0" y="15" height="15" width="15"/>
												</pattern>
											</defs>
											<g>
												<rect fill="url(#CheckerPattern)" stroke="none" x="-500" y="-500" width="1000" height="1000"/>
											</g>
										</svg>
									}
									{(isOpenColor && active.color !== 'inherit') && 
										<CompactPicker
											color={active.color}
											onChangeComplete={(color) => changeActiveAndRanges({color: color.hex})}
										/>
									}
								</Box>
							</Box>
							<Box className={classes.FD_autoFontColorSection}>
								<FormControlLabel
									label="Прозрачный"
									control={
										<Checkbox 
											checked={active.color === 'inherit'} 
											onChange={() => changeActiveAndRanges(active.color === 'inherit' ? {color: '#fff'} : {color: 'inherit'})}
										/>
									}
								/>
							</Box>
						</Box>

						<Box>
							{/* Начертание */}
							<Box className={classes.FD_wrapperForActionSections}>
								<Box whiteSpace="nowrap">Начертание:</Box>
								<Box className={classes.FD_inscriptionBtns}>
									<ButtonGroup size="small" aria-label="inscriptionBtnsGroup">
										<Button 
											style={{ fontWeight: 'bold'}}
											className={active.fontStyle === 'bold' && classes.FD_inscriptionBtn}
											onClick={() => changeActiveAndRanges({fontStyle: 'bold'})}
										>
											Ж
										</Button>
										<Button 
											style={{ fontStyle: 'italic'}}
											className={active.fontStyle === 'italic' && classes.FD_inscriptionBtn}
											onClick={() => changeActiveAndRanges({fontStyle: 'italic'})}
										>
											К
										</Button>
										<Button 
											style={{ textDecoration: 'underline'}}
											className={active.fontStyle === 'underline' && classes.FD_inscriptionBtn}
											onClick={() => changeActiveAndRanges({fontStyle: 'underline'})}
										>
											Ч
										</Button>
									</ButtonGroup>
								</Box>
								<Box className={classes.FD_clearInscriptionBtn}>
									<Tooltip title={ 'Очистить начертание' }  placement='top'>
										<FormControlLabel 
											control={
												<IconButton
													aria-label="clearInscription" 
													onClick={() => changeActiveAndRanges({fontStyle: 'normal'})}
												>
													<Icon path={mdiTextBoxRemoveOutline} size={1} />
												</IconButton>
											}
										/>
									</Tooltip>
								</Box>
							</Box>

							{/* Размер: */}
							<Box className={classes.FD_wrapperForActionSections}>
								<Box whiteSpace="nowrap">Размер:</Box>
								<FormControl className={classes.FD_fontSizeSelect}>
									<Select
										id="fontSizeSelect"
										value={active.fontSize}
										onChange={(e) => changeActiveAndRanges({fontSize: e.target.value})}
										input={<SelectInput />}
										MenuProps={MenuProps}
									>
										{menuItems()}
									</Select>
								</FormControl>
							</Box>

							{/* Цвет шрифта */}
							<Box className={classes.FD_wrapperForActionSections}>
								<Box className={classes.FD_fontColorSection}>
									<Box whiteSpace="nowrap">Цвет шрифта:</Box>
									<Box
										className={classes.FD_fontColorWrapper}
										onClick={() => setOpenFontColor(!isOpenFontColor)}
									>
											
										{active.fontColor 
											? 
												<Box 
													className={classes.FD_fontColorCircle}
													style={{ background: active.fontColor }}
												/>
											:
												<svg version="1.1" xmlns="http://www.w3.org/2000/svg" viewBox="-500 -500 1000 1000" className={classes.FD_fontColorCircleSVG}> 
													<defs>
														<pattern id="CheckerPattern" patternUnits="userSpaceOnUse" x="0" y="0" width="250" height="250" viewBox="0 0 30 30">
															<rect fill="#FFF" stroke="none" x="0" y="0" height="15" width="15"/>
															<rect fill="#FFF" stroke="none" x="15" y="15" height="15" width="15"/>
															<rect fill="#E5E5E5" stroke="none" x="15" y="0" height="15" width="15"/>
															<rect fill="#E5E5E5" stroke="none" x="0" y="15" height="15" width="15"/>
														</pattern>
													</defs>
													<g>
														<rect fill="url(#CheckerPattern)" stroke="none" x="-500" y="-500" width="1000" height="1000"/>
													</g>
												</svg>
										}
											
										{isOpenFontColor && 
											<CompactPicker
												color={active.fontColor}
												onChangeComplete={(color) => changeActiveAndRanges({fontColor: color.hex})}
											/>
										}
									</Box>
									<Box className={classes.FD_autoFontColorSection}>
										<FormControlLabel
											control={
												<Checkbox 
													checked={active.fontColor === '' ? true : false} 
													onChange={() => handleSetAutoFontColor(active.fontColor === '' ? false : true)}
												/>
											}
											label="Авто"
										/>
									</Box>
								</Box>
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