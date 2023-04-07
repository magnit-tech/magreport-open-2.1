import React, { useState, useEffect } from 'react';
import clsx from 'clsx';
import { useSnackbar } from 'notistack';

import { PivotCSS } from '../../../PivotCSS';

import {
	Accordion,
	AccordionDetails,
	AccordionSummary,
	IconButton,
	TextField,
	Tooltip,
	Typography,
} from '@material-ui/core';

import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import FileCopyOutlinedIcon from '@material-ui/icons/FileCopyOutlined';
import CloseIcon from '@material-ui/icons/Close';

export default function DerivedFieldDialogGuied(props) {
	const classes = PivotCSS();
	const { enqueueSnackbar } = useSnackbar();

	const { open, functionsList } = props;

	const [list, setList] = useState([]);

	useEffect(() => {
		setList(functionsList.filter(item => item.functionSignature.trim() !== ''));
	}, [functionsList]);

	function handleCopyFunctionName(e, name) {
		e.stopPropagation();
		navigator.clipboard.writeText(name);
		enqueueSnackbar(`Название функции успешно скопировано в буфер обмен`, {
			variant: 'success',
		});
	}

	const derivedFieldsList = () => (
		<div className={classes.DFD_accWrapperGuide}>
			{list.map(item => (
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
			))}
		</div>
	);

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
			{derivedFieldsList()}
		</div>
	);
}
