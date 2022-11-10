import React, { useState, useRef, useEffect } from 'react';

import { PivotCSS } from '../../PivotCSS';

import Draggable from 'react-draggable';

import { Paper, Dialog, DialogTitle, DialogActions, Grid, Button } from '@material-ui/core';
import DialogContent from '@material-ui/core/DialogContent';

import ListOfUsers from './ListOfUsers';

/**
	* @param {Boolean} props.open - boolean-значение отображения модального окна
	* @param {String} props.jobOwnerName - login владельца отчета
	* @param {Array} props.users - массив всех пользователей
	* @param {Array} props.usersWithAccess - массив пользователей с кем поделились заданием
	* @param {*} props.onCancel - function - callback отмены/закрытия модального окна
	* @param {*} props.onSave - function - callback сохранения списка пользователей с кем поделились заданием
**/

//Перетаскивание модального окна
function PaperComponent(props) {
    return (
		<Draggable handle="#drag-title" cancel={'[class*="MuiDialogContent-root"]'}>
			<Paper {...props} style={{maxWidth: 'none'}}/> 
		</Draggable>
    );
}


export default function ShareJobDialog(props){

	const classes = PivotCSS();

	useEffect(() => {
		if (props.usersWithAccess.length >0) {
		handleCheckedUsers(props.usersWithAccess)
		}
	}, [props.usersWithAccess])

	// Массив отмеченных логинов имеющих доступ
	const [selectedUsersLogin, setSelectedUsersLogin] = useState([])

	// Список всех пользователей 
	const listOfUsers = useRef(props.users.filter(item => item.name !== props.jobOwnerName)); 

	// Список пользователей имеющих доступ к заданию
	const [listOfUsersWithAccess, setListOfUsersWithAccess] = useState([]); 

	// Создаем список пользователей имеющих доступ из массива отмеченных логинов
	const handleCheckedUsers = ((arraySelectedUsers) => {

		let mapArraySelectedUsers = new Set(arraySelectedUsers),
			newArr = listOfUsers.current.filter(user =>mapArraySelectedUsers.has(user.id))

			setSelectedUsersLogin(arraySelectedUsers)

		return setListOfUsersWithAccess(newArr)
	});

  	return (
        <Dialog
            open={props.open}
            PaperComponent={PaperComponent}
			aria-labelledby="drag-title"
			PaperProps={{ classes: {root: classes.inListDialog}}}
        >
			<DialogTitle style={{ cursor: 'move', padding: '16px 24px 0px', marginBottom: '-8px' }} id="drag-title"> Поделиться заданием </DialogTitle>
			<DialogContent style={{display: 'flex', flex: 1, flexDirection: 'column'}}>
				<Grid
					container
					spacing={2}
					justifyContent="center"
					alignItems="center"
					className={classes.gridRoot}
				>
					<Grid 
						item
						xs={6}
						className={classes.gridColumn}
						//className={classes.SJ_gridItem}
					>
						<ListOfUsers 
							type = 'AllUsers'
							title = 'Все пользователи:'
							users = {listOfUsers.current}
							listOfSelectedUsers = {selectedUsersLogin}
							onCheckedUsers = {handleCheckedUsers}
						/>
					</Grid>

					<Grid 
						item
						xs={6}
						className={classes.gridColumn}
						//className={classes.SJ_gridItem}
					>
						<ListOfUsers 
							type = 'UsersWithAccess'
							title = 'Пользователи, имеющие доступ:'
							users = {listOfUsersWithAccess}
							listOfSelectedUsers = {selectedUsersLogin}
							onCheckedUsers = {handleCheckedUsers}
						/>
					</Grid>
					
				</Grid>
			</DialogContent>
			<DialogActions>
				<div className={classes.btnsArea}>
				<Button 
					size="small" 
					variant="outlined"
					color="primary" 
					disabled = {selectedUsersLogin === props.usersWithAccess}
					onClick={() =>props.onSave(listOfUsersWithAccess.map(user => {return {userName: user.name, domain: user.domain.name}}))}
				>
					Сохранить
				</Button>
				<Button
					className={classes.cancelBtn}
					size="small" 
					variant="outlined"
					color="primary" 
					onClick={() =>props.onCancel(false)}
				>
					Отменить
				</Button>
				</div>
			</DialogActions>
      </Dialog>
  );
}



