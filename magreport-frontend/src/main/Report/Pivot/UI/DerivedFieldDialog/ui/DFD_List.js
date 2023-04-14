import React, { useState, useMemo, useEffect } from "react";
import clsx from "clsx";

import { PivotCSS } from '../../../PivotCSS';

import { connect } from 'react-redux';
import {showAlertDialog, hideAlertDialog} from 'redux/actions/UI/actionsAlertDialog';

import { List, ListItem, ListItemText, ListItemSecondaryAction, IconButton, Typography, Button, Tooltip } from '@material-ui/core';
import { withStyles } from '@material-ui/core/styles';

// icons
import DeleteIcon from '@material-ui/icons/Delete';
import HelpIcon from '@material-ui/icons/Help';
import SearchIcon from '@material-ui/icons/Search';



/**
	* @param {Array} props.editedDerivedFields - список всех доступных производных полей
    * @param {Number | String} props.activeIndex - id текущего поля
    * @param {*} props.onAddNew - function - callback добавления нового поля
	* @param {*} props.onChoose - function - callback выбора поля для редактирования
	* @param {*} props.onDelete - function - callback удаления поля
**/

const HtmlTooltip = withStyles((theme) => ({
	tooltip: {
		backgroundColor: '#f5f5f9',
		color: 'rgba(0, 0, 0, 0.87)',
		maxWidth: 'auto',
		fontSize: theme.typography.pxToRem(12),
		border: '1px solid #dadde9',
	},
}))(Tooltip);

function DerivedFieldDialogList(props){

	const { editedDerivedFields, activeIndex } = props

	const classes = PivotCSS();

	const [list, setList] = useState([]);

	const [searchValue, setSearchValue] = useState('');

	const disabledNewFieldButton = useMemo(() => editedDerivedFields.some(o => o.id === 'new'), [editedDerivedFields]);

	// Поиск производных полей
	useEffect(() => {
		setList(editedDerivedFields)
		if (searchValue.trim() === '') {
			setList(editedDerivedFields);
		} else {
			setList(
				editedDerivedFields.filter(item => item.name.toLowerCase().search(searchValue.toLowerCase()) !== -1)
			);
		}
	}, [searchValue, editedDerivedFields]);

	// Вопрос при нажатие на "удалить"
	function handleAskToDelete(field) {
		props.showAlertDialog(`Вы действительно хотите удалить производное поле "${field.name || 'Новое поле'}"?`, null, null, answer => handleDeletingAnswer(field.id, answer))
	}

	// Удаление при успешном ответе
	function handleDeletingAnswer(id, answer){
        if (answer) props.onDelete(id)
        props.hideAlertDialog()
    }

	// Добавление нового поля и прокрутка вниз
	function handleAddNewField() {
		props.onAddNew()
		
		setTimeout(() => {
			document.querySelector('#new').scrollIntoView({
				behavior: 'smooth',
				block: 'start'
			});
		}, 500)
	} 

	return (
		<div className={classes.DFD_list}>	
			<div className={classes.DFD_searchGuideInList}>
				<input
					className={classes.DFD_searchInputGuide}
					type='text'
					value={searchValue}
					onChange={event => setSearchValue(event.target.value)}
					placeholder={'Поиск производных полей'}
				/>
				<span className={classes.DFD_searchBtnGuide}>
					<SearchIcon />
				</span>
			</div>
			<List id="block" className={clsx(classes.DFD_listBlock, 'MuiPaper-root')}>
				{list.length === 0 && 
					<p style={{textAlign: 'center', fontSize: '16px'}}>Список полей пуст</p>
				}
				{list.map((field) => {

					const options = { year: 'numeric', month: 'numeric', day: 'numeric' };

					return (
						<ListItem button id={field.id} key={field.id} selected={activeIndex === field.id} onClick={() => props.onChoose(field.id)}>
							<ListItemText
								primary={`${(!field.isCorrect || !field.isFormulaCorrect || field.needSave) ? '*' : ''}${field.originalName || 'Новое поле'}`}
								className = {clsx('', { 
									[classes.DFD_menuItemRed]: !field.isCorrect || !field.isFormulaCorrect || !!field?.errorMessage, 
									[classes.DFD_menuItemBold]: !field.isCorrect || !field.isFormulaCorrect || field.needSave || !!field?.errorMessage
								})}
								secondary={
									<>
										<Typography
											component="span"
											variant="body2"
										>
											{field.modified ? new Date(field.modified).toLocaleString('ru', options) : ''}
										</Typography>
										{ (!field.isPublic && field.userName) &&
											<>
											{" — "}
											<Typography
												component="span"
												variant="body2"
											>
												{field.userName}
											</Typography>
											</>
										}
									</>
								}
							/>

							{field.owner && <ListItemSecondaryAction>
								{(!field.isCorrect || !field.isFormulaCorrect || field.needSave || !!field?.errorMessage) && <IconButton edge="end">
									<HtmlTooltip
										title={
											<ul className={classes.DFD_listTooltipMenu}>
												{field.needSave && <li> - Необходимо сохранить данное поле</li>}
												{!field.isCorrect && <li> - Ошибка в название поля</li>}
												{!field.isFormulaCorrect && <li> - Ошибка в редакторе поля</li>}
												{!!field.errorMessage && <li> - Ошибка при сохранение поля</li>}
											</ul>
										}
									>	
										<HelpIcon/>
									</HtmlTooltip>
								</IconButton> }

								<Tooltip title = 'Удалить поле'>
									<IconButton edge="end" aria-label="delete" onClick={() => handleAskToDelete(field)}>
										<DeleteIcon />
									</IconButton>
                    			</Tooltip>
							</ListItemSecondaryAction>}
							
						</ListItem>
					)
				})}
			</List>
			<Button 
				variant="contained" 
				color="primary"
				disabled = {disabledNewFieldButton}
				className={classes.DFD_listBtn}
				onClick={() => handleAddNewField()}
			>
				Создать новое поле
			</Button>
		</div>
	)
}

const mapDispatchToProps = {
    showAlertDialog, 
    hideAlertDialog,
}

export default connect(null, mapDispatchToProps)(DerivedFieldDialogList)