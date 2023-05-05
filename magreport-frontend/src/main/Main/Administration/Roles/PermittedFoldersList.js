import React from 'react';
import { connect } from 'react-redux';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import Checkbox from '@material-ui/core/Checkbox';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import FormControl from '@material-ui/core/FormControl';
import IconButton from '@material-ui/core/IconButton';
import DeleteIcon from '@material-ui/icons/Delete';
// components
import { Tooltip } from '@material-ui/core';
import AddIcon from '@material-ui/icons/Add';
import MenuBookIcon from '@material-ui/icons/MenuBook';
import CreateIcon from '@material-ui/icons/Create';
import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
import MenuItem from '@material-ui/core/MenuItem';
import { Card } from '@material-ui/core';
//actions
import { showAlertDialog, hideAlertDialog } from 'redux/actions/UI/actionsAlertDialog';
import { actionRoleSelectFolderType, actionRoleDelete } from 'redux/actions/admin/actionRoles';
// styles 
import { RolesCSS } from "./RolesCSS";

function PermittedFoldersList(props){
    const classes = RolesCSS();

    const foldersArr = Object.keys(props.items.loadedData);
    let listItemsArr = [];

    const handleRWChange = ( event) => {
        let roleType = ['READ'];
        if (event.target.checked) {roleType.push('WRITE')};
        props.onChange(event.target.value, roleType)
    };

    let deletedItem = 0;

    const handleClickDelete = (id) => {
        deletedItem = id;
        props.showAlertDialog('Удалить роль с папки?', null, null, handleAnswerOfDelete)
    }

    function handleAnswerOfDelete(answer){
        if (answer === true){
            props.onDelete(deletedItem);
        }
        props.hideAlertDialog();
    }

    const handleChangeSelectedPermittedFolder = (event) => {
        props.onChangeSelectedPermittedFolder(event.target.value);
        props.actionRoleSelectFolderType(event.target.value)
    };

    if (props.selectedItem === 'all'){
        
        for (let i of foldersArr){
            if (props.items.loadedData[i].length > 0) {

                let ix = 0;

                props.items.loadedData[i].map((item) => {
                    ix += 1;

                    return listItemsArr.push( 
                        <ListItem key={ix} button>
                            <ListItemText 
                                primary = {item.folderName + ' (id: ' + item.folderId + ')'}
                                secondary = {props.items.namesList.find((it, ind, arr) =>it.id === i).value}
                            />
                                <div style={{display: 'flex', alignItems: 'center'}}>
                                    <FormControl component="fieldset">
                                        <FormControlLabel
                                            value={item.folderId}
                                            control={<Checkbox
                                                color="primary" 
                                                disabled={!props.editable}
                                                checked = {item.roleAuthority === 'WRITE'? true: false}
                                                name = {item.folderId.toString()}
                                                onChange={ handleRWChange}
                                            />}
                                            label="RW"
                                            labelPlacement="start"
                                        />
                                    </FormControl>
                                    {props.editable &&
                                    <ListItemIcon>
                                        <IconButton 
                                            aria-label="Удалить" 
                                            color="primary" 
                                            onClick={() => {handleClickDelete(item.folderId)}}
                                        >
                                            <DeleteIcon />
                                        </IconButton>
                                    </ListItemIcon>
                                    }
                                </div>
                        </ListItem>
                    )
                })
            }
        }
    } else if (props.selectedItem && props.items.loadedData[props.selectedItem].length >0){
        props.items.loadedData[props.selectedItem].map((item, index) => {
            return listItemsArr.push(
                <ListItem key={index} button>
                    <ListItemText primary= {item.folderName + ' (id: ' + item.folderId + ')'}/>
                        <div style={{display: 'flex', alignItems: 'center'}}> 
                            <FormControl component="fieldset">
                                <FormControlLabel
                                    value={item.folderId}
                                    control={<Checkbox 
                                            disabled = {!props.editable}
                                            color="primary" 
                                            checked = {item.roleAuthority === 'WRITE'? true: false}
                                            name = {item.folderId.toString()}
                                            onChange={handleRWChange}
                                        />}
                                    label="RW"
                                    labelPlacement="start"
                                />
                            </FormControl>
                            { props.editable &&
                            <ListItemIcon>
                                <IconButton 
                                    aria-label="Удалить" 
                                    color="primary" 
                                    onClick={() => {handleClickDelete(item.folderId)}}
                                >
                                    <DeleteIcon />
                                </IconButton>
                            </ListItemIcon>
                            }
                        </div>
                </ListItem>
            )
        })
    }

    return (
        <Card elevation={3} className={classes.roleList}>
            <div className={classes.userAddPanel}>
                <div className={classes.roleAutocompleteDiv}>
                    <TextField
                        fullWidth
                        size = "small"
                        id="permitted-folders-names-list"
                        select
                        label="Объекты"
                        value={props.selectedItem}
                        onChange={handleChangeSelectedPermittedFolder}
                        variant="outlined"
                    >
                        {props.items.namesList.sort((a,b) => a.value.localeCompare(b.value)).map((item) => (
                            <MenuItem key={item.id} value={item.id}>
                                {item.value}
                            </MenuItem>
                         ))}
                    </TextField> 
                </div>
                {props.onAddFolderRead &&
                <div className={classes.addButtonsRW}>
                    <Tooltip title={'Выдать права на чтение'}>
                        <Button
                            size ='small'
                            color="primary"
                            variant="outlined"
                            disabled = {false}
                            onClick={props.onAddFolderRead}
                        >
                            <MenuBookIcon/>
                            <AddIcon/>
                        </Button>
                    </Tooltip>
                    <Tooltip title={'Выдать права на запись'}>
                        <Button
                            size ='small'
                            color="primary"
                            variant="outlined"
                            disabled = {false}
                            onClick={props.onAddFolderWrite}
                        >
                            <CreateIcon/>
                            <AddIcon/>
                        </Button>
                    </Tooltip>
                </div>
                }
            </div>
            <div className={classes.permittedFoldersListRel}> 
                <List className={classes.permittedFoldersListAbs}>
                    {listItemsArr}
                </List>
            </div>
        </Card>
    )
}

const mapDispatchToProps = {
    showAlertDialog, 
    hideAlertDialog,
    actionRoleSelectFolderType,
    actionRoleDelete
}

export default connect(null, mapDispatchToProps)(PermittedFoldersList);