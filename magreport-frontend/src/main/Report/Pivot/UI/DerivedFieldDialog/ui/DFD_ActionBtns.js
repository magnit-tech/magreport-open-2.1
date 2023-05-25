import React from 'react';

import { PivotCSS } from '../../../PivotCSS';

import {
	Box,
	FormControl,
	MenuItem,
	Select,
	InputBase,
	Tooltip,
	IconButton,
} from '@material-ui/core';
import { withStyles } from '@material-ui/core/styles';

import MenuBookIcon from '@material-ui/icons/MenuBook';
import CodeIcon from '@material-ui/icons/Code';

const SelectInput = withStyles(theme => ({
	root: {
		maxWidth: '100px',
	},
	input: {
		borderRadius: 4,
		position: 'relative',
		backgroundColor: theme.palette.background.paper,
		border: '1px solid #ced4da',
		fontSize: 14,
		padding: '5px 20px 5px 7px',
		transition: theme.transitions.create(['border-color', 'box-shadow']),
		'&:focus': {
			borderRadius: 4,
			borderColor: '#80bdff',
			boxShadow: '0 0 0 0.2rem rgba(0,123,255,.25)',
		},
	},
}))(InputBase);

export default function DerivedFieldDialogActionBtns(props) {
	const classes = PivotCSS();

	// Создание списка c MenuItems для селекта "Размер шрифта"
	const menuItems = () => {
		const numbers = [8, 9, 10, 12, 14, 16, 20, 24, 28, 32];
		const arr = numbers.map(i => (
			<MenuItem key={i} value={i}>
				{i}
			</MenuItem>
		));

		return arr;
	};

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

	return (
		<Box className={classes.DFD_actionBtns}>
			<Box whiteSpace='nowrap' className={classes.DFD_fontSizeSelect}>
				<span>Размер шрифта редактора:</span>

				<FormControl>
					<Select
						id='fontSizeSelect'
						value={props.fontSize}
						onChange={e => props.onChangeFontSize(e.target.value)}
						input={<SelectInput />}
						MenuProps={MenuProps}
					>
						{menuItems()}
					</Select>
				</FormControl>
			</Box>
			<Tooltip title='Справочник функций' placement='top'>
				<IconButton onClick={() => props.onToogleShowPanels('guide', true)} disabled={props.isGuideVisible}>
					<MenuBookIcon />
				</IconButton>
			</Tooltip>
			<Tooltip title='Синтаксис языка' placement='top'>
				<IconButton onClick={() => props.onToogleShowPanels('syntax', true)} disabled={props.isSyntaxVisible}>
					<CodeIcon />
				</IconButton>
			</Tooltip>
		</Box>
	);
}
