import React, { useEffect, useRef, useState } from 'react';
import { PivotCSS } from '../../PivotCSS';
import { Card, Checkbox, List, ListItem, ListItemIcon, ListItemText, Toolbar, InputBase, Typography, Tooltip, ListItemSecondaryAction, FormControlLabel, IconButton } from '@material-ui/core';
import SearchIcon from '@material-ui/icons/Search';
import { Pagination } from '@material-ui/lab';
import {useSnackbar} from "notistack";
//Icon-Component
import Icon from '@mdi/react'

// dataHub
import dataHub from 'ajax/DataHub';

// local
import DataLoader from "../../../../DataLoader/DataLoader";



//icons
import { mdiDeleteForever } from '@mdi/js';

/**
    * @param {String} props.type - тип списка пользователей 
	* @param {String} props.title - заголовок списка пользователей
	* @param {Array} props.users - массив всех/выбранных пользователей
	* @param {Array} props.listOfSelectedUsers - массив logins пользователей с кем поделились заданием
	* @param {*} props.onCheckedUsers - function - callback сохранения списка пользователей с кем поделились заданием
**/

// Вспомогательные функции
function not(a, b) {
	return a.filter((value) => b.indexOf(value) === -1);
}
function intersection(a, b) {
	return a.filter((value) => b.indexOf(value) !== -1);
}
function union(a, b) {
	return [...a, ...not(b, a)];
}


export default function ListOfUsers(props){
	const {enqueueSnackbar} = useSnackbar();
	const classes = PivotCSS();

	useEffect(() => {
		setCheckedUsers(props.listOfSelectedUsers)
	}, [props.listOfSelectedUsers])

	//const [domainsList, setDomainsList] = useState([]);
	const [domain, setDomain] = useState({});
	
	// Страницы
	const [userPage, setUserPage] = useState(0)

	// Массив чекбоксов выбранных пользователей
	const [checkedUsers, setCheckedUsers] = useState([]); 

	// Переключение checkbox у определенного пользователя
	const handleToggle = (value) => () => {
		const currentIndex = checkedUsers.indexOf(value);
		const newChecked = [...checkedUsers];
	
		if (currentIndex === -1) {
		  	newChecked.push(value);
		} else {
		  	newChecked.splice(currentIndex, 1);
		}
		
		setCheckedUsers(newChecked);

		props.onCheckedUsers(newChecked)
	};

	// Выбрать всех
	const numberOfChecked = (items) => intersection(checkedUsers, items).length;
	const handleToggleAll = (items) => () => {

		if (numberOfChecked(items) === items.length) {
			setCheckedUsers(not(checkedUsers, items));
			props.onCheckedUsers(not(checkedUsers, items))
		} else {
			setCheckedUsers(union(checkedUsers, items));
			props.onCheckedUsers(union(checkedUsers, items))
		}
	};

	// Поиск
	const [userFilterValue, setUserFilterValue] = useState("");
	function handleFilterUser (e) {
		setUserPage(0)
		setUserFilterValue(e.target.value);
	}

	const users = useRef()

	// Список пользователей
    const { pageListWithFilteredUsers, setListFilteredUsers, countUsers } = filterUsers()
	function filterUsers(){
		
        let listWithAllFilteredUsers = [],
			pageListWithFilteredUsers = [],
			setListFilteredUsers = []

        const filterStr = userFilterValue.toLowerCase()

		let searchArr = filterStr.split(' ')

		if (searchArr.length === 1) {
			users.current = props.users.filter( userObj => {
				let fio = `${userObj.name} ${userObj.lastName} ${userObj.firstName} ${userObj.patronymic}`
	
				const a = searchArr.some(n => fio.toLowerCase().indexOf(n) !== -1)

				if (a) {
					return userObj
				} else {
					return null
				}
			})
		} else {
			users.current = users.current.filter( userObj => {
				let fio = `${userObj.name} ${userObj.lastName} ${userObj.firstName} ${userObj.patronymic}`

				let flag = 0;

				searchArr.forEach( word => {
					if (fio.toLowerCase().indexOf(word) !== -1) {
						flag++
	
					} else {
						flag--
					}
				})

				if (flag === searchArr.length) {
					return userObj
				}
				else return null
			})
		}

		users.current.map( userObj => {
			listWithAllFilteredUsers.push(userObj)
			return setListFilteredUsers.push(userObj.id)
		})
        
        const countUsers = Math.floor(listWithAllFilteredUsers.length / 30)

        pageListWithFilteredUsers = listWithAllFilteredUsers.splice(userPage * 30, 30)

        return { pageListWithFilteredUsers, setListFilteredUsers, countUsers }
	}

	function handleDataLoaded(data){
        let domain = data.filter(i => i.isDefault === true).map(i => {return {value: i.id, name: i.name}})[0];
        //setDomainsList(data);
        setDomain(domain);
    }

  	return (
		<DataLoader
			loadFunc = {dataHub.userServiceController.getDomainList}
			loadParams = {[]}
			onDataLoaded = {handleDataLoaded}
			onDataLoadFailed = {(message) => enqueueSnackbar(`При загрузке данных произошла ошибка: ${message}`, {variant: "error"})}
		>
			<Card elevation={3} className={classes.transferHeader}>
				<Toolbar 
					position="fixed"
					className={classes.SJ_titlebar}
					variant="dense"
					style = {{ flexDirection: 'column'}}
				>
					<Typography variant="h3" className={classes.SJ_title}>{props.title}</Typography>
					{ props.type === 'AllUsers' ? 
						<div className={classes.SJ_titleSubButtons}>
							<Tooltip title = 'Выбрать всех пользователей' placement="top">
								<Checkbox
									onClick={handleToggleAll(setListFilteredUsers)}
									checked={numberOfChecked(setListFilteredUsers) === setListFilteredUsers.length && setListFilteredUsers.length !== 0}
									style={{color: 'white'}}
								/>
							</Tooltip>
							<div className={classes.SJ_search}>
								<div className={classes.SJ_searchIcon}>
									<SearchIcon />
								</div>
								<InputBase
									placeholder="Поиск…"
									classes={{
										root: classes.SJ_inputRoot,
										input: classes.SJ_inputInput,
									}}
									onChange={handleFilterUser}
									value={userFilterValue}
								/>
							</div>
						</div>
						:
						<div className={classes.SJ_titleSubButtons}>
							<div className={classes.SJ_search}>
								<div className={classes.SJ_searchIcon}>
									<SearchIcon />
								</div>
								<InputBase
									placeholder="Поиск…"
									classes={{
										root: classes.SJ_inputRoot,
										input: classes.SJ_inputInput,
									}}
									onChange={handleFilterUser}
									value={userFilterValue}
								/>
							</div>
							<Tooltip title = 'Убрать всех пользователей из списка' placement="top">
								<FormControlLabel 
									control = {
										<IconButton aria-label="delete" className={classes.SJ_iconButton}>
											<Icon path={mdiDeleteForever} size={1} />
										</IconButton>
									}
									onClick={handleToggleAll(setListFilteredUsers)}
								/>
							</Tooltip>
						</div>
					}
				</Toolbar>
				<div className={classes.listRel}> 
					<List 
						dense 
						data-testid="users_list"
						component="div"
						className={classes.listTrList}
					>
						{ props.type === 'AllUsers' ?
							pageListWithFilteredUsers.map((item) => {
								const labelId = `transfer-list-all-item-${item.id}-label`;
								let fullName = (domain.name === item.domain.name ? '' : item.domain.name +'\\') + item.name;
					
								return (
									<ListItem 
										button
										key={item.id} 
										role="listitem" 
										onClick={handleToggle(item.id)}
										disabled={checkedUsers.indexOf(item.id) !== -1}
										style = {{ borderBottom: '1px solid rgba(0, 0, 0, 0.12)' }}
									>
										<ListItemIcon>
											<Checkbox
												checked={checkedUsers.indexOf(item.id) !== -1}
												tabIndex={-1}
												disableRipple
												inputProps={{ 'aria-labelledby': labelId }}
											/>
										</ListItemIcon>
										<ListItemText 
											id = {labelId}
											primary = {fullName}
											secondary={item.lastName && item.firstName ? `${item.lastName} ${item.firstName[0]}. ${item.patronymic[0]}.` : ''}
										/>
									</ListItem>
								);
							})
							:
							pageListWithFilteredUsers.map((item) => {
								const labelId = `transfer-list-all-item-${item.id}-label`;
								let fullName = (domain.name === item.domain.name ? '' : item.domain.name +'\\') + item.name;
								return (
									<ListItem 
										button
										key={item.id} 
										role="listitem"
										style = {{ borderBottom: '1px solid rgba(0, 0, 0, 0.12)' }}
									>
										<ListItemText 
											id = {labelId}
											inset
											primary = {fullName}
											secondary={item.lastName && item.firstName ? `${item.lastName} ${item.firstName[0]}. ${item.patronymic[0]}.` : ''}
										/>
										<ListItemSecondaryAction>
											<Tooltip title="Убрать пользователя из списка"  placement='top'>
												<FormControlLabel 
													control = {
														<IconButton size="medium" aria-label="delete">
															<Icon path={mdiDeleteForever} size={1} />
														</IconButton>
													}
													onClick = {handleToggle(item.id)}
												/>
											</Tooltip>
										</ListItemSecondaryAction>
									</ListItem>
								);
							})
						}
					</List>
				</div>
				<div className={classes.divPag} /*className={classes.SJ_bottomButtons}*/>
					{ countUsers > 1 &&
						<Pagination 
							count = {countUsers} 
							shape = "rounded" 
							size = "small"
							onChange={(event, value) => setUserPage(value)}
						/>
					}
				</div>
			</Card>
		</DataLoader>
  	);
}