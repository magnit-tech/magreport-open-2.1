import React from 'react';
import clsx from 'clsx';

import { PivotCSS } from '../../../PivotCSS';

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

/**
 * @param {Boolean} props.open - отображение справочника
 * @param {Array} props.allFieldsAndExpressions - список всех доступных функций производных полей
 **/

export default function DerivedFieldDialogSyntax(props) {
	const classes = PivotCSS();

	const { open } = props;

	const derivedFieldsList = () => (
		<div className={classes.DFD_accWrapperGuide}>
			<Accordion key={1}>
				<AccordionSummary
					expandIcon={<ExpandMoreIcon />}
					aria-controls='panel1a-content'
					id='panel1a-header'
				>
					<div className={classes.DFD_accGuide}>
						<Typography className={classes.DFD_accTitleGuide}>
							IF_ELIF_ELSE
						</Typography>
						<Tooltip title='Скопировать название функции' placement='top'>
							<IconButton
								size='small'
								edge='end'
								aria-label='copy'
								// onClick={e => handleCopyFunctionName(e, item.functionName)}
							>
								<FileCopyOutlinedIcon />
							</IconButton>
						</Tooltip>
					</div>
				</AccordionSummary>
				<AccordionDetails
					className={classes.DFD_accContentWrapperGuide}
				></AccordionDetails>
			</Accordion>
		</div>
	);

	return (
		<div className={clsx(classes.DFD_guide, { active: open })}>
			<div className={classes.DFD_closeGuide}>
				<Tooltip title='Закрыть' placement='left'>
					<IconButton
						edge='end'
						aria-label='close'
						onClick={() => props.onToggleDerivedFunctionGuied(false)}
					>
						<CloseIcon />
					</IconButton>
				</Tooltip>
			</div>
			<h3 className={classes.DFD_titleGuide}>Синтаксис языка:</h3>
			{/* {derivedFieldsList()} */}
			<p style={{ textAlign: 'center' }}>В процессе разработки...</p>
		</div>
	);
}
