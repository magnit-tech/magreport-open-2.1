import React, { useEffect, useRef, useState } from 'react';

import { PivotCSS } from '../PivotCSS';

import Draggable from 'react-draggable';

import { withStyles } from '@material-ui/core/styles';

import { Dialog, DialogActions, DialogTitle, Paper, AppBar, Tabs, Tab, Box, TextField, Checkbox, Button, ButtonGroup, IconButton, Tooltip, FormControlLabel, FormControl, MenuItem, Select, InputBase } from "@material-ui/core";

//Icon-Component
import Icon from '@mdi/react'

//icons
import { mdiChevronLeft, mdiChevronRight, mdiTextBoxRemoveOutline } from '@mdi/js';

// Color Picker
import { CompactPicker } from 'react-color';

/**
	* @param {Boolean} props.open - boolean-значение отображения модального окна
	* @param {Object} props.cellData - объект данных, сгенерированных в PivotPanel
	* @param {*} props.onCancel - function - callback отмены/закрытия модального окна
	* @param {*} props.onSaveFormatting - function - callback сохранения форматирования
**/

//Перетаскивание модального окна
function PaperComponent(props) {
    return (
		<Draggable handle="#dragg-title" cancel={'[class*="MuiDialogContent-root"]'}>
			<Paper {...props} />
		</Draggable>
    );
}

//Обертка для children у табов
function TabPanel(props) {
	const { children, value, index, ...other } = props;
  
	return (
		<div
			role="tabpanel"
			hidden={value !== index}
			id={`scrollable-auto-tabpanel-${index}`}
			aria-labelledby={`scrollable-auto-tab-${index}`}
			{...other}
		>
			{value === index && (
				<Box style={{padding: props.rightcontent ? '0' : '24px'}}>
					{children}
				</Box>
			)}
		</div>
	);
}


export default function FormattingDialog(props){
	
	const classes = PivotCSS();

	const dataType = props.cellData.dataType

	const propsFormattingObj = useRef(JSON.parse(JSON.stringify(props.cellData)))

	useEffect(() => {
		const aggFuncName = props.cellData.aggFuncName
		
		props.cellData.style.forEach((item, index) => {
			if(item.aggFuncName === aggFuncName) {
				indexInStyleArray.current = index

				setInputNumber(item.rounding)
				setPercentValue(item.percent)
				setNumberWithSpaces(item.numberWithSpaces)

				if( !item.hasOwnProperty('fontStyle') ) {
					propsFormattingObj.current.style[index].fontStyle = 'normal';
				}
				if( !item.hasOwnProperty('fontSize') ) {
					propsFormattingObj.current.style[index].fontSize = 14;
				}
				if( !item.hasOwnProperty('fontColor') ) {
					propsFormattingObj.current.style[index].fontColor = "";
				}

				setFontStyle(item.fontStyle ?? 'normal')
				setFontSize(item.fontSize ?? 14)
				setFontColor(item.fontColor ?? "")
			}
		})

	}, [props.cellData])

	const indexInStyleArray = useRef(0)

	const [mainTabsValue, setMainTabsValue] = useState(0);
	
	// Переключение главных табов
	const handleChangeMainTabs = (e, newValue) => setMainTabsValue(newValue)


	/*
        *********** Раздел "Цифры" ***********
    */

	const [inputNumber, setInputNumber] = useState(0);
	const [percentValue, setPercentValue] = useState(false);
	const [numberWithSpaces, setNumberWithSpaces] = useState(false);

	// Вычисляемое значение в компоненте "Образец"
	const computedExampleNumber = () => {
		const exampleNumber = 5
		let n = exampleNumber.toFixed(inputNumber)

		if (percentValue) {
			return (n * 100) + '%'
		}

		if (numberWithSpaces) {
			if(inputNumber > 0) {
				let parts = n.toString().split(".");
				parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, " ");
				return parts.join(".");
			}
			return n.toString().replace(/\B(?=(\d{3})+(?!\d))/g, " ");
		}

		return n
	}

	// Вычесление числа десятичных знаков (округление)
	const handleChangeRounding = (action) => {
		let number = inputNumber;

		if (action === 'decrase' && number > 0) {
			number--
			propsFormattingObj.current.style[indexInStyleArray.current].rounding = number;
			return setInputNumber(number)
		} else if (action === 'incrase' && (number >= 0 && number < 20)) {
			number++
			propsFormattingObj.current.style[indexInStyleArray.current].rounding = number;
			return setInputNumber(number)
		}
	}
	
	// Вычесление процентного вида (%)
	const handleChangeSwitcher = (event) => {
		const e = event.target.checked;
		propsFormattingObj.current.style[indexInStyleArray.current].percent = e;
		setPercentValue(e)	
	};

	// Разделение числа на разряды
	const handleNumberWithSpaces = (event) => {
		const e = event.target.checked;
		propsFormattingObj.current.style[indexInStyleArray.current].numberWithSpaces = e;
		setNumberWithSpaces(e)	
	};




	/*
		*********** Раздел "Строки" ***********
	*/

	const [stringLength, setStringLength] = useState(props.cellData.data.replace('...','').length);


	// Вычисляемое значение в компоненте "Образец"
	const computedExampleString = () => {
		const initialStringLength = propsFormattingObj.current.style[indexInStyleArray.current].initialStringLength
		const initialStringData = propsFormattingObj.current.style[indexInStyleArray.current].initialStringData

		if(initialStringLength > stringLength) {
			return initialStringData.substring(0, stringLength) + '...'
		} else {
			return initialStringData
		}
	}

	// Вычесление количество знаков для обрезания строки
	const handleStringTruncation = (action) => {
		const initialStringLength = propsFormattingObj.current.style[indexInStyleArray.current].initialStringLength
		let truncat = stringLength;

		if (action === 'decrase' && truncat > 0) {
			truncat--
			propsFormattingObj.current.style[indexInStyleArray.current].truncat = truncat;
			return setStringLength(truncat)
		} else if (action === 'incrase' && (truncat >= 0 && truncat < initialStringLength)) {
			truncat++
			propsFormattingObj.current.style[indexInStyleArray.current].truncat = truncat;
			return setStringLength(truncat)
		}
	}



	/*
        *********** Раздел "Шрифт" ***********
    */

	const [fontStyle, setFontStyle] = useState('normal')
	const [fontSize, setFontSize] = useState(14)
	const [fontColor, setFontColor] = useState("");

	const [autoFontColor, setAutoFontColor] = useState(false);

	const [isOpen, setOpen] = useState(false);

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

	// Customized Example Font Input
	const ExampleFontInput = withStyles({
		root: {
			'& input': {
				textAlign: 'center',
				fontWeight: fontStyle === 'bold' ? 'bold' : '400',
				fontStyle: fontStyle === 'italic' ? 'italic' : 'inherit',
				textDecoration: fontStyle === 'underline' ? 'underline' : 'none',
				fontSize: fontSize + 'px',
				color: fontColor
			},
		},
	})(TextField);

	// Style for Select menu and children items
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

	// Создание списка c MenuItems для селекта "Размер шрифта"
	const menuItems = () => {
		const arr = []

		for (let i = 1; i < 31; i++) {
			arr.push(<MenuItem key={i} value={i}>{i}</MenuItem>)
		}

		return arr
	}

	// Начертание шрифта
	const handleChangeInscription = (e, action) => {
		e.preventDefault();

		switch(action) {
			case 'normal':
				propsFormattingObj.current.style[indexInStyleArray.current].fontStyle = action;
				return setFontStyle(action)
			case 'bold':
				propsFormattingObj.current.style[indexInStyleArray.current].fontStyle = action;
				return setFontStyle(action)
			case 'italic':
				propsFormattingObj.current.style[indexInStyleArray.current].fontStyle = action;
				return setFontStyle(action)
			case 'underline':
				propsFormattingObj.current.style[indexInStyleArray.current].fontStyle = action;
				return setFontStyle(action)
			default:
				propsFormattingObj.current.style[indexInStyleArray.current].fontStyle = 'normal';
				return fontStyle.current = 'normal'
		}
	}

	// Размер шрифта
	const handleChangeFontSize = (targetValue) => {
		propsFormattingObj.current.style[indexInStyleArray.current].fontSize = targetValue;
		setFontSize(targetValue)
	}

	// Цвет шрифта
	const handleChangeFontColor = (hex) => {
		propsFormattingObj.current.style[indexInStyleArray.current].fontColor = hex;
		setFontColor(hex)
	}

	// Цвет шрифта - Авто
	const handleSetAutoFontColor = (bool) => {
		if (bool === true) propsFormattingObj.current.style[indexInStyleArray.current].fontColor = '';
		setAutoFontColor(bool)
	}

  	return (
        <Dialog
            open={props.open}
            PaperComponent={PaperComponent}
            aria-labelledby="dragg-title"
        >
            <DialogTitle style={{ cursor: 'move' }} id="dragg-title"> Форматирование </DialogTitle>

			<Box className={classes.FD_metrcisNameWrapper}>
				<Box className={classes.FD_metrcisName}>
					<TextField
						id="sortBy"
						label="Форматирование метрики:"
						value={propsFormattingObj.current.metricName}
						InputProps={{
							readOnly: true,
						}}
						fullWidth
						multiline
						rows = {propsFormattingObj.current.metricName.length > 37 ? 2 : 1}
						variant="outlined"
					/>
				</Box>
			</Box>

			<Box className={classes.FD_root}>

				<AppBar position="static" color="default">
					<Tabs
						value={mainTabsValue}
						onChange={handleChangeMainTabs}
						indicatorColor="primary"
						textColor="primary"
						variant="scrollable"
						scrollButtons="auto"
						aria-label="scrollable auto tabs example"
					>
						{ (dataType === 'INTEGER' || dataType === 'DOUBLE') && <Tab label="Цифры" />}
						{ dataType === 'STRING' && <Tab label="Строки" />}
						<Tab label="Шрифт"/>
						<Tab label="Выравнивание" disabled/>
					</Tabs>
				</AppBar>
				
				{/* "Цифры" */}
				{ (dataType === 'INTEGER' || dataType === 'DOUBLE') && <TabPanel value={mainTabsValue} index={0}>
					<TextField
						id="exampleForNumbers"
						label="Образец:"
						value={computedExampleNumber()}
						InputProps={{ 
							readOnly: true,
						}}
						fullWidth
						variant="outlined"
					/>
					<Box className={classes.FD_wrapperForActionSections}>
						<Box whiteSpace="nowrap">Число десятичных знаков:</Box>

						<Box className={classes.FD_inputNumberWithArrows}>
							<IconButton 
								size="small" 
								aria-label="decrase" 
								onClick={() => handleChangeRounding("decrase")}
							>
								<Icon path={mdiChevronLeft} size={1} />
							</IconButton>
							<input 
								type='text' 
								className={classes.FD_inputForNumber}
								value={inputNumber}
								readOnly
							/>
							<IconButton 
								size="small" 
								aria-label="incrase" 
								onClick={() => handleChangeRounding("incrase")}
							>
								<Icon path={mdiChevronRight} size={1} />
							</IconButton>
						</Box>
					</Box>
					<Box className={classes.FD_wrapperForPercent}>
						<Box whiteSpace="nowrap">Процентный вид (%):</Box>
						<Checkbox
							checked={percentValue}
							onChange={(e) => handleChangeSwitcher(e)}
							inputProps={{ 'aria-label': 'primary checkbox' }}
						/>
					</Box>
					<Box className={classes.FD_wrapperForPercent}>
						<Box whiteSpace="nowrap">Разрядный вид:</Box>
						<Checkbox
							checked={numberWithSpaces}
							onChange={(e) => handleNumberWithSpaces(e)}
							inputProps={{ 'aria-label': 'primary checkbox' }}
						/>
					</Box>
				</TabPanel>}

				{/* "Строки" */}
				{ dataType === 'STRING' && <TabPanel value={mainTabsValue} index={0}>
					<TextField
						id="exampleForNumbers"
						label="Образец:"
						value={computedExampleString()}
						InputProps={{ 
							readOnly: true,
						}}
						fullWidth
						variant="outlined"
					/>
					<Box className={classes.FD_wrapperForActionSections}>
						<Box whiteSpace="nowrap">Количество знаков:</Box>

						<Box className={classes.FD_inputNumberWithArrows}>
							<IconButton 
								size="small" 
								aria-label="decrase" 
								onClick={() => handleStringTruncation("decrase")}
							>
								<Icon path={mdiChevronLeft} size={1} />
							</IconButton>
							<input 
								type='text' 
								className={classes.FD_inputForNumber}
								value={stringLength}
								readOnly
							/>
							<IconButton 
								size="small" 
								aria-label="incrase" 
								onClick={() => handleStringTruncation("incrase")}
							>
								<Icon path={mdiChevronRight} size={1} />
							</IconButton>
						</Box>
					</Box>
				</TabPanel>}

				{/* "Шрифт" */}
				<TabPanel value={mainTabsValue} index={1}>
					<ExampleFontInput
						id="exampleForFont"
						label="Образец:"
						value={'AaBbБбЯя | 012345'}
						InputProps={{ 
							readOnly: true,
						}}
						fullWidth
						variant="outlined"
					/>

					<Box className={classes.FD_wrapperForActionSections}>
						<Box whiteSpace="nowrap">Начертание:</Box>
						<Box className={classes.FD_inscriptionBtns}>
							<ButtonGroup size="small" aria-label="inscriptionBtnsGroup">
								<Button 
									style={{ fontWeight: 'bold'}}
									className={fontStyle === 'bold' && classes.FD_inscriptionBtn}
									onClick={(e) => handleChangeInscription(e, 'bold')}
								>
									Ж
								</Button>
								<Button 
									style={{ fontStyle: 'italic'}}
									className={fontStyle === 'italic' && classes.FD_inscriptionBtn}
									onClick={(e) => handleChangeInscription(e, 'italic')}
								>
									К
								</Button>
								<Button 
									style={{ textDecoration: 'underline'}}
									className={fontStyle === 'underline' && classes.FD_inscriptionBtn}
									onClick={(e) => handleChangeInscription(e, 'underline')}
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
											onClick={(e) => handleChangeInscription(e, 'normal')}
										>
											<Icon path={mdiTextBoxRemoveOutline} size={1} />
										</IconButton>
									}
								/>
							</Tooltip>
						</Box>
					</Box>

					<Box className={classes.FD_wrapperForActionSections}>
						<Box whiteSpace="nowrap"> Размер:</Box>
						<FormControl className={classes.FD_fontSizeSelect}>
							<Select
								id="fontSizeSelect"
								value={fontSize}
								onChange={(e) => handleChangeFontSize(e.target.value)}
								input={<SelectInput />}
								MenuProps={MenuProps}
							>
								{menuItems()}
							</Select>
						</FormControl>
					</Box>

					<Box className={classes.FD_wrapperForActionSections}>
						<Box className={classes.FD_fontColorSection}>
							<Box whiteSpace="nowrap"> Цвет: </Box>
							<Box
								className={classes.FD_fontColorWrapper}
								onClick={() => setOpen(autoFontColor ? isOpen : !isOpen)}
							>
								{fontColor 
									? 
									<Box 
										className={autoFontColor ? classes.FD_fontColorCircleNotAlowed : classes.FD_fontColorCircle}
										style={{ background: fontColor }}
									/>
									:
									<svg version="1.1" xmlns="http://www.w3.org/2000/svg" viewBox="-500 -500 1000 1000" className={autoFontColor ? classes.FD_fontColorCircleSVGNotAlowed : classes.FD_fontColorCircleSVG}> 
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
								{(isOpen && !autoFontColor) && 
									<CompactPicker
										color={fontColor}
										onChangeComplete={(color) => handleChangeFontColor(color.hex)}
									/>
								}
							</Box>
						</Box>
						<Box className={classes.FD_autoFontColorSection}>
							<FormControlLabel
								control={
									<Checkbox checked={autoFontColor} onChange={() => handleSetAutoFontColor(!autoFontColor)}/>
								}
								label="Авто"
							/>
						</Box>
					</Box>
				</TabPanel>

				<TabPanel value={mainTabsValue} index={2}>
					Item Three
				</TabPanel>

			</Box>

			<DialogActions>
				<Button color="primary" onClick={() => props.onSaveFormatting(propsFormattingObj.current)}>
					Сохранить
				</Button>
				<Button color="primary" onClick={() => props.onCancel()}>
					Отменить
				</Button>
			</DialogActions>

      </Dialog>
  );
}