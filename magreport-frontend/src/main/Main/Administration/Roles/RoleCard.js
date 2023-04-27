import React from 'react';
import clsx from 'clsx';
import { Link } from "react-router-dom";

// components
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import DeviceHubIcon from '@material-ui/icons/DeviceHub';
import PageviewIcon from '@material-ui/icons/Pageview';
import IconButton from '@material-ui/core/IconButton';
import DeleteIcon from '@material-ui/icons/Delete';
import ReplayIcon from '@material-ui/icons/Replay';
import EditIcon from '@material-ui/icons/Edit';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Checkbox from '@material-ui/core/Checkbox';
import { RolesCSS } from "./RolesCSS";

function RoleCard(props){
    const classes = RolesCSS();

    const canWrite = props.showCheckboxRW ? props.data.permissions.indexOf("WRITE") > -1 : false

    const handleClickDelete = event => {
        event.stopPropagation();
        props.onDelete(props.data.id);
    }

    function handleClick(event){
        props.onChangeSelectedRole(props.data.id)
        event.stopPropagation();
    }

    function handleChangeWriteRights(event){
        event.stopPropagation();
        props.onChangeRW(props.index, !canWrite);
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
            primaryTypographyProps = {
                props.data.type === 'I' ? {color: 'error'}: 
                (props.data.type || '').includes('D') ? {color: 'textSecondary'}:
                {color: 'initial'}
            }
            primary={props.data.name }
            secondary= {props.data.description ? props.data.description : props.data.role?.description }
        />
        {
            props.showCheckboxRW && !(props.data.type || '').includes('D') &&
            <FormControlLabel
                className = {clsx({[classes.rwLabelChanged]: props.data.type === 'U'})}
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
                    aria-label= { !(props.data.type || '').includes('D') ? "Удалить" : "Вернуть"}
                    color="primary" 
                    onClick={handleClickDelete}
                >
                    { !(props.data.type || '').includes('D') ? <DeleteIcon /> : <ReplayIcon />}
                </IconButton>
            </ListItemIcon>
            : ""
        }


            
    </ListItem>
    )
}

export default RoleCard;