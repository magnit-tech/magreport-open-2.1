import React, { useState, useEffect } from 'react';
import { useSnackbar } from 'notistack';
import { useDispatch } from 'react-redux';
import { addNavbar } from 'redux/actions/navbar/actionNavbar';
import PropTypes from 'prop-types';

// styles 
import { UsersCSS} from "./UsersCSS";

// dataHub
import dataHub from 'ajax/DataHub';

// componenets
import { useTheme } from '@material-ui/core/styles';
import Button from '@material-ui/core/Button';
import BlockIcon from '@material-ui/icons/Block';
import LockIcon from '@material-ui/icons/Lock';
import LockOpenIcon from '@material-ui/icons/LockOpen';
import Checkbox from '@material-ui/core/Checkbox';
import Hidden from '@material-ui/core/Hidden';
import IconButton from '@material-ui/core/IconButton';
import clsx from 'clsx';
import Tooltip from '@material-ui/core/Tooltip';
import ExitToAppIcon from '@material-ui/icons/ExitToApp';
import Icon from '@mdi/react';
import InputAdornment from '@material-ui/core/InputAdornment';
import { mdiWeb } from '@mdi/js';
import { Card, List, Toolbar, Typography, InputBase, TextField, TablePagination } from '@material-ui/core';
import SearchIcon from '@material-ui/icons/Search';
import MenuItem from '@material-ui/core/MenuItem';
import FormControl from '@material-ui/core/FormControl';
import AddIcon from '@material-ui/icons/Add';
import FirstPageIcon from '@material-ui/icons/FirstPage';
import KeyboardArrowLeft from '@material-ui/icons/KeyboardArrowLeft';
import KeyboardArrowRight from '@material-ui/icons/KeyboardArrowRight';
import LastPageIcon from '@material-ui/icons/LastPage';
// local
import DataLoader from '../../../DataLoader/DataLoader';
import UserCard from './UserCard';
import AsyncAutocomplete from '../../../../main/AsyncAutocomplete/AsyncAutocomplete';

export default function UserList(props) {
	const classes = UsersCSS();

	const { enqueueSnackbar } = useSnackbar();

	const dispatch = useDispatch();

	useEffect(() => {
		if (!props.roleId) {
			dispatch(addNavbar('Пользователи', 'users'));
		}
	}, [dispatch, props.roleId]);

	const [userPage, setUserPage] = useState(0);
	const [rowsPerPage, setRowsPerPage] = useState(50);
   // const [viewAll, setViewAll] = useState(false)

	const [selectedUser, setSelectedUser] = useState(
		props.selectedUser !== -1 ? props.selectedUser : -1
	);
	const [checkedAllUsers, setCheckedAllUsers] = React.useState(false);
	const [userFilterValue, setUserFilterValue] = useState('');
/*  const [needUserScroll, setNeedUserScroll] = useState(
		Boolean(props.needUserScroll)
	);
*/
	const [domainsList, setDomainsList] = useState([]);
	const [defaultDomain, setDefaultDomain] = useState('');
	const [domain, setDomain] = useState({});
	const [selectedUserToAdd, setSelectedUserToAdd] = useState("");

    function filterUsers(){
        let filteredList = []
        const filterStr = userFilterValue.toLowerCase();
        let filteredByDomainList = props.items.filter(i=>i.domain.id===domain.value);
        
        for (let i in filteredByDomainList) {
            if (filteredByDomainList[i].name.toLowerCase().indexOf(filterStr) > -1) {
                filteredList.push(filteredByDomainList[i])
            }
        }
        
        const countUsers = filteredList.length
       // if (!viewAll){
            filteredList = filteredList.splice((userPage)*rowsPerPage, rowsPerPage)
       // }
        return {filteredList, countUsers}
    }

    function getCheckedUserNames(){
        let users = []
        for (let u of props.items) {
            if (u.blockUserCheck) users.push(u.id)
        }
        return users
    }

    function handleCheckedAll(){
        props.onAllUsersChecked(!checkedAllUsers)
        setCheckedAllUsers(!checkedAllUsers);
    }

    function handleFilterUser (e) {
        setUserPage(0)
        setUserFilterValue(e.target.value);
    }

    function handleSelectUser(id, index) {
        //setNeedUserScroll(false);
        setSelectedUser(id);
        
        if (props.onSelectUser) props.onSelectUser(id)
    }

    const listItems=[]
    const {filteredList, countUsers} = filterUsers()
 
    filteredList.forEach((i, index) => 
        listItems.push(
            <UserCard
                id = {i.id}
                key={i.id} 
                index={index}
                itemsType={props.itemsType}
                userDesc={i} 
                defaultDomain = {domain.value}
                roleId={props.roleId}
                isSelected={selectedUser===i.id}
                onSelectedUser={handleSelectUser}
                enableRoleReload={props.enableRoleReload}
                showDeleteButton={props.showDeleteButton}
                showCheckbox={props.showCheckbox ? props.showCheckbox : false}
            />
        )
    )
/*
    useEffect(() => {
        let userIndex = props.items.findIndex(item => item.id === props.selectedUser);

        if (userIndex !==-1) {setUserPage(Math.ceil((userIndex/50)-1))};

        var scrolledElement = document.getElementById(props.selectedUser);

        if (props.selectedUser !== -1 && props.from === "UserDesigner" && scrolledElement){
          scrolledElement.scrollIntoView({block: "start", behavior: "smooth"});
        }
        setNeedUserScroll(false);
	}, [needUserScroll]); // eslint-disable-line
*/

    function handleDataLoaded(data){
		let dom = data.filter(i => i.isDefault === true).map(i => {return {value: i.id, name: i.name}})[0];
		let domList = data.sort((a,b) => b.isDefault - a.isDefault).slice();
        setDomainsList(domList);
		setDomain(dom);
		setDefaultDomain(data.filter(i => i.isDefault).map(item => item.name)[0]);
	}

	function handleOnChangeAddUserText(value) {
		setSelectedUserToAdd(value);
	}

	function handleAddUserToRole() {
		for (let u of props.items) {
			if (u.id === selectedUserToAdd.id) {
				enqueueSnackbar('Пользователю уже назначена эта роль!', {
					variant: 'error',
				});
				return;
			}
		}
		dataHub.roleController.addUsers(
			props.roleId,
			[selectedUserToAdd.id],
			handleAddUserToRoleResponse
		);
	}
	
	function handleAddUserToRoleResponse(magrepResponse){
        if (magrepResponse.ok) {
            enqueueSnackbar("Пользователь добавлен!", {variant : "success"});
            props.onNeedReload()
        }
        else {
            enqueueSnackbar("Не удалось добавить пользователя", {variant : "error"});
        }
    }


	function TablePaginationActions(props) {

      //  const classes = ReportDataCSS();
        const theme = useTheme();
        const { count, page, rowsPerPage, onPageChange} = props;

        const handleFirstPageButtonClick = event => {
            onPageChange(event, 0);
        };

        const handleBackButtonClick = event => {
            onPageChange(event, page - 1);
        };

        const handleNextButtonClick = event => {
            onPageChange(event, page + 1);
        };

        const handleLastPageButtonClick = event => {
            onPageChange(event, Math.max(0, Math.ceil(count / rowsPerPage) - 1));
        };

        const handleCustomPage = e => {
            if (e.key === 'Enter') {
                let page_num = parseInt(e.target.value);
                let page_count = Math.ceil(count / rowsPerPage);
                if (!isNaN(page_num) && page_num <= page_count && page_num!==0){
                    onPageChange(e, page_num-1);
                }
            }            
        };

        return (
            <div className={classes.pagination} display="block">
                <Tooltip title="К первой странице">
                    <span>
                        <IconButton className={classes.iconButton}
                            size="small"
                            onClick={handleFirstPageButtonClick}
                            disabled={page === 0}
                            aria-label="first page"
                        >
                            {theme.direction === 'rtl' ? <LastPageIcon /> : <FirstPageIcon />}
                        </IconButton>
                    </span>
                </Tooltip>
                <IconButton className={classes.iconButton}
                    size="small"
                    onClick={handleBackButtonClick}
                    disabled={page === 0}
                    aria-label="previous page"  
                >
                    {theme.direction === 'rtl' ? <KeyboardArrowRight /> : <KeyboardArrowLeft />}
                </IconButton>
                <InputBase className={classes.pageNumber}
                    variant="outlined"
                    size="small"                    
                    defaultValue={userPage+1}
                    onKeyDown={handleCustomPage}
                    inputProps={{ style: {textAlign: 'center'}}}              
                />
                <IconButton className={classes.iconButton}
                    size="small"
                    onClick={handleNextButtonClick}
                    disabled={page >= Math.ceil(count / rowsPerPage) - 1}
                    aria-label="next page"
                >
                    {theme.direction === 'rtl' ? <KeyboardArrowLeft /> : <KeyboardArrowRight />}
                </IconButton>
                <Tooltip title="К последней странице">
                    <span>
                        <IconButton className={classes.iconButton}
                            size="small"
                            onClick={handleLastPageButtonClick}
                            disabled={page >= Math.ceil(count / rowsPerPage) - 1}
                            aria-label="last page"
                        >
                            {theme.direction === 'rtl' ? <FirstPageIcon /> : <LastPageIcon />}
                        </IconButton>
                    </span>
                </Tooltip>
                
            </div>
        );
        
    }

    TablePaginationActions.propTypes = {
        count: PropTypes.number.isRequired,
        onPageChange: PropTypes.func.isRequired,
        page: PropTypes.number.isRequired,
        rowsPerPage: PropTypes.number.isRequired
	};
	
    return (
        <DataLoader
            loadFunc = {dataHub.userServiceController.getDomainList}
            loadParams = {[]}
            onDataLoaded = {handleDataLoaded}
            onDataLoadFailed = {(message) => enqueueSnackbar(`При загрузке данных произошла ошибка: ${message}`, {variant: "error"})}
        >
			<Card elevation={3} className={classes.userListCard}>
				<Toolbar
					position='fixed'
					className={clsx({
						[classes.userAddPanel]: props.showDeleteButton,
						[classes.titlebar]: !props.showDeleteButton,
					})}
					variant='dense'
				>
					{!props.showDeleteButton && (
						<Typography className={classes.title} variant='h6'>
							Пользователи
						</Typography>
					)}
					{props.showDeleteButton && (
						<div className={classes.userAutocompleteDiv}>
							<AsyncAutocomplete
								className={classes.domainAutocomplete}
								size='small'
								defaultDomain={defaultDomain}
								disabled={false}
								typeOfEntity={'user'}
								// filterOfEntity = {(item) => item.status !== "ARCHIVE"}
								onChange={handleOnChangeAddUserText}
							/>

							<Button
								className={classes.addButton}
								color='primary'
								variant='outlined'
								disabled={false}
								onClick={handleAddUserToRole}
							>
								<AddIcon />
								Добавить
							</Button>
						</div>
					)}
					<div className={classes.selectDiv}>
						<FormControl variant='outlined' size='small'>
							<TextField
								classes={{
									root: clsx({
										[classes.domainSelectDark]: props.showDeleteButton,
										[classes.domainSelectLight]: !props.showDeleteButton,
									}),
								}}
								size='small'
								id='outlined-select-currency'
								select
								value={domain.value}
								name={domain.name}
								onChange={e => {
									setDomain(e.target);
								}}
								variant='outlined'
								InputProps={{
									startAdornment: (
										<Tooltip title='Домен' placement='top'>
											<InputAdornment
												position='start'
												className={classes.domainTooltip}
											>
												<Icon path={mdiWeb} size={0.9} />
											</InputAdornment>
										</Tooltip>
									),
								}}
							>
								{domainsList.map((i, index) => (
									<MenuItem
										key={i.id}
										value={i.id}
										ListItemClasses={{
											button: clsx({ [classes.menuList]: index === 0 }),
										}}
									>
										{' '}
										{i.name}
									</MenuItem>
								))}
							</TextField>
						</FormControl>
						<div
							className={clsx(classes.search, {
								[classes.searchLight]: !props.showDeleteButton,
							})}
						>
							<div className={classes.searchIcon}>
								<SearchIcon />
							</div>
							<InputBase
								placeholder='Поиск…'
								classes={{
									root: classes.inputRoot,
									input: classes.inputInput,
								}}
								onChange={handleFilterUser}
								value={userFilterValue}
							/>
						</div>
					</div>
				</Toolbar>
				{props.showControlButtons ? (
					<span className={classes.spanBtn}>
						<div>
							<Checkbox
								onChange={handleCheckedAll}
								checked={checkedAllUsers}
								color='primary'
							/>
						</div>
						<Hidden only={['xs', 'sm', 'md']}>
							<Button
								className={classes.usersBtn}
								color='primary'
								startIcon={<LockIcon color='secondary' />}
								onClick={() =>
									props.onManageUsers('DISABLED', getCheckedUserNames())
								}
							>
								<Typography noWrap className={classes.btnText}>
									Lock
								</Typography>
							</Button>
							<Button
								className={classes.usersBtn}
								color='primary'
								startIcon={<LockOpenIcon />}
								onClick={() =>
									props.onManageUsers('ACTIVE', getCheckedUserNames())
								}
							>
								<Typography noWrap className={classes.btnText}>
									Unlock
								</Typography>
							</Button>
							<Button
								className={classes.usersBtn}
								color='primary'
								startIcon={<BlockIcon color='secondary' />}
								onClick={() =>
									props.onManageUsers('LOGGOFF', getCheckedUserNames())
								}
							>
								<Typography noWrap className={classes.btnText}>
									Logoff
								</Typography>
							</Button>
							<Button
								className={classes.usersBtn}
								color='primary'
								startIcon={<ExitToAppIcon color='secondary' />}
								onClick={() => props.onManageUsers('LOGGOFFALL', [])}
							>
								<Typography noWrap className={classes.btnText}>
									Logoff All
								</Typography>
							</Button>
						</Hidden>
						<Hidden only={['lg', 'xl']}>
							<Tooltip title='Заблокировать'>
								<IconButton
									color='secondary'
									onClick={() =>
										props.onManageUsers('DISABLED', getCheckedUserNames())
									}
								>
									<LockIcon />
								</IconButton>
							</Tooltip>
							<Tooltip title='Разблокировать'>
								<IconButton
									color='primary'
									onClick={() =>
										props.onManageUsers('ACTIVE', getCheckedUserNames())
									}
								>
									<LockOpenIcon />
								</IconButton>
							</Tooltip>
							<Tooltip title='Завершить сессию'>
								<IconButton
									color='secondary'
									onClick={() =>
										props.onManageUsers('LOGGOFF', getCheckedUserNames())
									}
								>
									<BlockIcon />
								</IconButton>
							</Tooltip>
							<Tooltip title='Завершить сессии всех пользователей'>
								<IconButton
									color='secondary'
									onClick={() => props.onManageUsers('LOGGOFFALL', [])}
								>
									<ExitToAppIcon />
								</IconButton>
							</Tooltip>
						</Hidden>
					</span>
				) : (
					''
				)}
				<div className={classes.userListRelative}>
					<List
						data-testid='users_list'
						dense
						className={clsx(classes.userListBox, {
							[classes.userListBoxRole]: props.from !== 'UserDesigner',
						})}
					>
						{listItems}
					</List>
				</div>
				<div className={classes.bottomButtons}>
					<TablePagination 
                        rowsPerPageOptions={[10, 25, 50, 100, 500, 1000]}
                        labelRowsPerPage=""
                        labelDisplayedRows={({ from, to, count }) => `${from}-${to} из ${count}`}
                        component="div"
                        count={countUsers}
                        rowsPerPage={rowsPerPage}
                        page={userPage}
                        ActionsComponent={TablePaginationActions}
                        backIconButtonProps={{
                            'aria-label': 'previous page',
                        }}
                        nextIconButtonProps={{
                            'aria-label': 'next page',
                        }}
                        onPageChange={(e, newPage) => setUserPage(newPage)}
                        onRowsPerPageChange={(e)=> setRowsPerPage(e.target.value)}                   
                    />
				</div>
			</Card>
		</DataLoader>
	);
}
