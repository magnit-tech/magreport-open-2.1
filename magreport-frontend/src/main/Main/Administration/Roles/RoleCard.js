import React from 'react';
import { connect } from 'react-redux';

import { Link } from "react-router-dom";

// components
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import DeviceHubIcon from '@material-ui/icons/DeviceHub';
import PageviewIcon from '@material-ui/icons/Pageview';
import IconButton from '@material-ui/core/IconButton';
import DeleteIcon from '@material-ui/icons/Delete';
import EditIcon from '@material-ui/icons/Edit';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Checkbox from '@material-ui/core/Checkbox';

//actions
import { showAlertDialog, hideAlertDialog } from 'redux/actions/UI/actionsAlertDialog'
import { actionRolesChangeWriteRights } from 'redux/actions/admin/actionRoles'
import { foldersLoading } from 'redux/actions/sidebar/actionFolderTree';

function RoleCard(props){

    const canWrite = props.showCheckboxRW ? props.data.permissions.indexOf("WRITE") > -1 : false

    const handleClickDelete = event => {
        
        event.stopPropagation();
        props.showAlertDialog('Исключить роль?', null, null, handleDelete)
    }

    function handleDelete(answer){
        if (answer){
            props.onDelete(props.data.id)
        }
        props.hideAlertDialog()
    }

    function handleClick(event){
        props.onChangeSelectedRole(props.data.id)
        event.stopPropagation();
    }

    function handleChangeWriteRights(event){
        event.stopPropagation();
        props.actionRolesChangeWriteRights(props.index, !canWrite)
    }    

    return (
        <ListItem  
            button 
            onClick={handleClick}
            selected={props.isSelected}
        >
        <ListItemIcon>
            <DeviceHubIcon/>
        </ListItemIcon>
        <ListItemText 
            primary={props.data.name }
            secondary= {props.data.description ? props.data.description : props.data.role?.description }
        />
        {
            props.showCheckboxRW &&
            <FormControlLabel
                value="start"
                control={
                    <Checkbox 
                        color="primary" 
                        checked={canWrite}
                        onClick={handleChangeWriteRights}
                    />}
                label="RW"
                labelPlacement="start"
            />
        }
        {
            props.showViewButton ?
                <Link 
                    to={`/ui/roles/${props.data.typeId}/view/${props.data.id}`} 
                    target="_blank" 
                    rel="noopener noreferrer"
                >
                    <ListItemIcon>
                        <IconButton 
                            aria-label="view" 
                            color="primary" 
                        >
                            <PageviewIcon />
                        </IconButton>
                    </ListItemIcon>
                </Link>
            : ""
        }
        {
            props.showEditButton ?
            <Link 
                to={`/ui/roles/${props.data.typeId}/edit/${props.data.id}`} 
                target="_blank" 
                rel="noopener noreferrer"
            >
                <ListItemIcon>
                    <IconButton 
                        aria-label="edit-role" 
                        color="primary"
                    >
                        <EditIcon />
                    </IconButton>
                </ListItemIcon>
            </Link>

            : ""
        }
        {
            props.showDeleteButton ?
            <ListItemIcon>
                <IconButton 
                    aria-label="Удалить" 
                    color="primary" 
                    onClick={handleClickDelete}
                >
                    <DeleteIcon />
                </IconButton>
            </ListItemIcon>
            : ""
        }


            
    </ListItem>
    )
}

const mapDispatchToProps = {
    showAlertDialog, 
    hideAlertDialog,
    actionRolesChangeWriteRights,
    foldersLoading,
}

export default connect(null, mapDispatchToProps)(RoleCard);