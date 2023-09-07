import React, { useState, useEffect } from 'react';
import clsx from 'clsx';
import { useSnackbar } from 'notistack';

import { PivotCSS } from '../../../PivotCSS';

import { filterAndSortGuideList } from '../lib/DFD_functions';

import {
	Accordion,
	AccordionDetails,
	AccordionSummary,
	IconButton,
	Tooltip,
	Typography,
} from '@material-ui/core';

import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import FileCopyOutlinedIcon from '@material-ui/icons/FileCopyOutlined';

// icons
import CloseIcon from '@material-ui/icons/Close';
import SearchIcon from '@material-ui/icons/Search';


/**
 * @param {Boolean} props.open - отображение справочника
 * @param {Array} props.allFieldsAndExpressions - список всех доступных функций производных полей
**/

export default function DerivedFieldDialogGuied(props) {
	const classes = PivotCSS();
	const { enqueueSnackbar } = useSnackbar();

	const { open, functionsList } = props;

	const [list, setList] = useState([]);

	useEffect(() => {
		setList(filterAndSortGuideList(functionsList))
	}, [functionsList]);

	function handleCopyFunctionName(e, name) {
		e.stopPropagation();
		navigator.clipboard.writeText(name);
		enqueueSnackbar(`Название функции успешно скопировано в буфер обмен`, {
			variant: 'success',
			autoHideDuration: 1000
		});
	}

	const derivedFieldsList = () => (
		<div className={classes.DFD_accWrapperGuide}>
			{list.length > 0 ? (
				list.map(item => (
					<Accordion key={item.functionId}>
						<AccordionSummary
							expandIcon={<ExpandMoreIcon />}
							aria-controls='panel1a-content'
							id='panel1a-header'
						>
							<div className={classes.DFD_accGuide}>
								<Typography className={classes.DFD_accTitleGuide}>
									{item.functionName}
								</Typography>
								<Tooltip title='Скопировать название функции' placement='top'>
									<IconButton
										size='small'
										edge='end'
										aria-label='copy'
										onClick={e => handleCopyFunctionName(e, item.functionName)}
									>
										<FileCopyOutlinedIcon />
									</IconButton>
								</Tooltip>
							</div>
						</AccordionSummary>
						<AccordionDetails className={classes.DFD_accContentWrapperGuide}>
							<div className={classes.DFD_accContent}>
								<span className={classes.DFD_accContentLabel}>Описание:</span>
								<div
									className={classes.DFD_accContentText}
									dangerouslySetInnerHTML={{ __html: item.functionDesc }}
								/>
							</div>
							<div className={classes.DFD_accContent}>
								<span className={classes.DFD_accContentLabel}>Сигнатура:</span>
								<div
									className={classes.DFD_accContentText}
									dangerouslySetInnerHTML={{ __html: item.functionSignature }}
								/>
							</div>
						</AccordionDetails>
					</Accordion>
				))
			) : (
				<p style={{ textAlign: 'center', fontSize: '16px' }}>
					Список функций пуст!
				</p>
			)}
		</div>
	);

	function handleSearchFunction(value) {
		if (value.trim() === '') {
			setList( filterAndSortGuideList(functionsList));
		} else {
			setList(
				functionsList.filter(
					({ functionName, functionDesc }) =>
						functionName.toLowerCase().search(value.toLowerCase()) !== -1 ||
						functionDesc.split(' ').filter(i => i.toLowerCase().search(value.toLowerCase()) !== -1).length > 0
				)
			);
		}
	}

	return (
		<div className={clsx(classes.DFD_guide, { active: open })}>
			<div className={classes.DFD_closeGuide}>
				<Tooltip title='Закрыть справочник' placement='left'>
					<IconButton
						edge='end'
						aria-label='close'
						onClick={() => props.onToggleDerivedFunctionGuied(false)}
					>
						<CloseIcon />
					</IconButton>
				</Tooltip>
			</div>
			<h3 className={classes.DFD_titleGuide}>Справочник функций:</h3>
			<div className={classes.DFD_searchGuide}>
				<input
					className={classes.DFD_searchInputGuide}
					type='text'
					onChange={event => handleSearchFunction(event.target.value)}
					placeholder={'Поиск функций'}
				/>
				<span className={classes.DFD_searchBtnGuide}>
					<SearchIcon />
				</span>
			</div>
			{derivedFieldsList()}
		</div>
	);
}
