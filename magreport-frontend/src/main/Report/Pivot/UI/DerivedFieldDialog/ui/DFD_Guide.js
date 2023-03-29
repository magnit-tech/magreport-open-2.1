import React from 'react';
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

	function handleCopyFunctionName(e, name) {
		e.stopPropagation();
		navigator.clipboard.writeText(name);
		enqueueSnackbar(`Название функции успешно скопировано в буфер обмен`, {
			variant: 'success',
		});
	}

	const list = () => (
		<div className={classes.DFD_accWrapperGuide}>
			{functionsList.map(item => (
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
					<AccordionDetails>
						<TextField
							label='Описание'
							placeholder='Описание'
							multiline
							rows={2}
							InputLabelProps={{
								shrink: true,
							}}
							variant='outlined'
							value={item.functionDesc}
							style={{ width: '100%' }}
						/>
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
			{list('right')}
		</div>
	);
}
