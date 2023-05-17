import React from 'react';
import {useState} from 'react';
import PropTypes from 'prop-types';
import RoleCard from './RoleCard'
import {FolderItemTypes} from 'main/FolderContent/FolderItemTypes';
import { 
    Card,
    List,
    Toolbar,
    Typography,
    InputBase,
    TablePagination
} from '@material-ui/core';
import SearchIcon from '@material-ui/icons/Search';

import { useTheme } from '@material-ui/core/styles';
import IconButton from '@material-ui/core/IconButton';
import Tooltip from '@material-ui/core/Tooltip';
import FirstPageIcon from '@material-ui/icons/FirstPage';
import KeyboardArrowLeft from '@material-ui/icons/KeyboardArrowLeft';
import KeyboardArrowRight from '@material-ui/icons/KeyboardArrowRight';
import LastPageIcon from '@material-ui/icons/LastPage';

// styles 
import { RolesCSS } from "./RolesCSS";

function RoleList(props){
    const classes = RolesCSS();

    const [parentsKeySelected, setParentsKeySelected] = useState(-1);
    const [roleFilterValue, setRoleFilterValue] = useState("");

    const [userPage, setUserPage] = useState(0);
	const [rowsPerPage, setRowsPerPage] = useState(50);

    function handleFilterRole (e) {
        setRoleFilterValue(e.target.value);
    }

    function filterRoles(){
        let filteredList = []
        for (let u of props.items) {
            if (u.name.toLowerCase().indexOf(roleFilterValue.toLowerCase()) >= 0) filteredList.push(u)
        }
        const countRoles = filteredList.length
        filteredList = filteredList.splice((userPage)*rowsPerPage, rowsPerPage)
        return {filteredList, countRoles}
    }

    function handleSelectRole(id) {
        setParentsKeySelected(parentsKey)
        if (props.onSelectRole) props.onSelectRole(id)
    }

    function handleDelete(id) {
        if (props.onDelete) props.onDelete(id)
    }

    function handleChangeRW(index, value){
        if (props.onChangeRW) props.onChangeRW(index, value)
    }
    
    let parentsKey = props.parentsKey ? props.parentsKey : -1 // если одна роль есть у нескольких пользователей, при переключении между ними не нужно ее подсвечиывать

    const listItems=[]
    const {filteredList, countRoles} = filterRoles()

    filteredList.forEach((i, index) => {
        listItems.push(
            <RoleCard 
                key={i.id + `_${index}_${props.parentsKey}`}
                index={index}
                data={i}
                isSelected={(props.selectedRole===i.id) && (parentsKey===parentsKeySelected)}
                itemsType={FolderItemTypes.roles}
                showCheckboxRW={props.showCheckboxRW}
                showDeleteButton={props.showDeleteButton}
                showEditButton = {props.showEditButton}
                showViewButton = {props.showViewButton}
                onChangeRW = {handleChangeRW}
                onChangeSelectedRole = {handleSelectRole}
                onDelete={handleDelete}
            />
        )
    })

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
        <div className={classes.roleListFlex}>
            {!props.hideSearh ?
                <Card elevation={3} className={classes.roleList}>
                    <Toolbar position="fixed"
                        className={classes.titlebar}
                        variant="dense"
                    >
                        <Typography className={classes.title} variant="h6">Роли</Typography>
                        <div className={classes.search}>
                            <div className={classes.searchIcon}>
                                <SearchIcon />
                            </div>
                            <InputBase
                                placeholder="Поиск…"
                                classes={{
                                    root: classes.inputRoot,
                                    input: classes.inputInput,
                                }}
                                onChange={handleFilterRole}
                                value={roleFilterValue}
                            />
                        </div>
                    </Toolbar>
                    {props.topElems ? props.topElems : ""}
                    <div className={classes.roleListFlexRelative}>
                        <List  data-testid="roles_list" dense className={classes.roleListBox}>
                            {listItems}
                        </List>
                    </div>
                    {rowsPerPage < countRoles &&
                        <div className={classes.bottomButtons}>
					        <TablePagination 
                                rowsPerPageOptions={[10, 25, 50, 100, 500, 1000]}
                                labelRowsPerPage=""
                                labelDisplayedRows={({ from, to, count }) => `${from}-${to} из ${count}`}
                                component="div"
                                count={countRoles}
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
                    }
                </Card>
                :
                <div  className={classes.roleListFlex}>
                    {props.topElems ? props.topElems : ""}
                    <div className={classes.roleListFlexRelative}>
                        <List  data-testid="roles_list" dense className={classes.roleListBox}>
                            {listItems}
                        </List>
                    </div>
                </div>
            }
        </div>
    )
}

export default RoleList