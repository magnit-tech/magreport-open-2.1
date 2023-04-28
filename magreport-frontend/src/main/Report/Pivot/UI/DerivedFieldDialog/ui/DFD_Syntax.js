import React, { useState } from 'react';
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

import { DFD_exampleList, DFD_languageConstructs } from '../lib/DFD_syntaxText';

// icons
import CloseIcon from '@material-ui/icons/Close';

/**
 * @param {Boolean} props.open - отображение справочника
 * @param {Array} props.allFieldsAndExpressions - список всех доступных функций производных полей
**/


export default function DerivedFieldDialogSyntax(props) {
	const classes = PivotCSS();

	const { open } = props;

	const languageConstructsList = () => (
		<ul className={classes.DFD_syntaxList}>
			{DFD_languageConstructs.map(item => (
				<li key={item.name}>
					<strong>{item.name}</strong><span dangerouslySetInnerHTML={{__html: item.descr}}/>
					{ item.code && <pre className={clsx(classes.DFD_syntaxTextCode, 'MuiPaper-root')}>{item.code}</pre> }
					{ item.addition && <p dangerouslySetInnerHTML={{__html: item.addition}}/>}
				</li>
			))}
		</ul>
	);

	const exampleList = () => (
		<div className={classes.DFD_syntaxAccGuideWrapp}>
			{DFD_exampleList.map(item => (
				<Accordion key={item.name}>
					<AccordionSummary expandIcon={<ExpandMoreIcon />} >
						<div className={classes.DFD_accGuide}>
							<Typography className={classes.DFD_accTitleGuide}> {item.name} </Typography>
						</div>
					</AccordionSummary>
					<AccordionDetails className={classes.DFD_accContentWrapperGuide} >
						<p 
							className={classes.DFD_syntaxCodeDescr} 
							dangerouslySetInnerHTML={{__html: item.descr}}
						/>
						<div className={classes.DFD_syntaxExampleCodeWrapp}>
							<span><i>Конструкция:</i></span>
							<pre className={classes.DFD_syntaxExampleCode}>{item.code}</pre>
						</div>	
						{ item.addition && 
							<p 
								className={classes.DFD_syntaxCodeDescr} 
								dangerouslySetInnerHTML={{__html: item.addition}}
							/>
						}
						{ item.additionalСode && 
							<div className={classes.DFD_syntaxExampleCodeWrapp}>
								<span><i>Конструкция:</i></span>
								<pre className={classes.DFD_syntaxExampleCode}>{item.code}</pre>
							</div>	
						}
					</AccordionDetails>
				</Accordion>
			))}

		</div>
	);

	const [languageOpen, setLanguageOpen] = useState(false);
	const [examplesOpen, setExamplesOpen] = useState(false);

	return (
		<div className={clsx(classes.DFD_guide, { active: open }, 'syntax')}>
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
			<h3 className={classes.DFD_titleGuide}>Синтаксис языка</h3>

			<div className={classes.DFD_syntaxWrapp}>
				<div>	
					<div className={classes.DFD_syntaxAccTitle} 
						onClick={() => setLanguageOpen(!languageOpen)}
					>
						<h3>Описание языка</h3>
						<span 
						className={clsx(classes.DFD_syntaxAccArrow, {'active': languageOpen})}
						> ⮝ </span>
					</div>
					<div className={clsx(classes.DFD_syntaxAccContent, {'active': languageOpen})}>
						<p>
							Язык формул Maglang позволяет задавать формулы для вычисления
							производных полей. Производные поля можно представлять себе, как
							вычисляемые столбцы, добавляемые в исходную таблицу сформированного
							набора данных отчёта -- при этом в каждой строке в соответствующую
							ячейку столбца записывается значение, вычисленное по соответствующей
							формуле по значениям других ячеек в данной строке таблицы. Для
							описания формул используются следующие языковые конструкции:
						</p>
						{languageConstructsList()}
					</div>
				</div>
				<div>
					<div className={classes.DFD_syntaxAccTitle} 
						onClick={() => setExamplesOpen(!examplesOpen)}
					>
						<h3>Примеры</h3>
						<span 
						className={clsx(classes.DFD_syntaxAccArrow, {'active': examplesOpen})}
						> ⮝ </span>
					</div>
					<div className={clsx(classes.DFD_syntaxAccContent, {'active': examplesOpen})}>
						{exampleList()}
					</div>
				</div>
			</div>
		</div>
	);
}
