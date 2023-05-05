import React from 'react';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import IconButton from '@material-ui/core/IconButton';
import DeleteIcon from '@material-ui/icons/Delete';
import Icon from '@mdi/react';
import { mdiDomain } from '@mdi/js';
// local
import {FolderItemTypes} from 'main/FolderContent/FolderItemTypes';
// styles
import { connect } from 'react-redux';
//actions
import { showAlertDialog, hideAlertDialog } from 'redux/actions/UI/actionsAlertDialog'


function DomainGroupCard(props){
    
    const handleClickDelete = event => {
        event.stopPropagation();
        let text = 'Удалить доменную группу?'
        if (props.itemsType === FolderItemTypes.roles){
            text='Исключить доменную группу из роли?'
        }
        props.showAlertDialog(text, null, null, handleDelete)
    }

    function handleDelete(answer){
        if (answer){
            props.onDeleteDomainGroup({domainId: props.domainGroupDesc.domainId, groupName: props.domainGroupDesc.groupName})
        }
        props.hideAlertDialog()
    }

    function handleClick(event){
        props.setSelectedDomainGroup(props.domainGroupDesc.domainName)
        event.stopPropagation();
    }

    return(
        <ListItem  
            button 
            onClick={handleClick}
            selected={props.isSelected}
        >
           <ListItemIcon>
                <Icon path={mdiDomain} size={1} />
            </ListItemIcon>

            <ListItemText 
                primary={(props.domainGroupDesc.domainId === props.defaultDomain ? '' : props.domainGroupDesc.domainName + '/' ) + props.domainGroupDesc.groupName }
            />
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
    );
}

const mapDispatchToProps = {
    showAlertDialog, 
    hideAlertDialog
}

export default connect(null, mapDispatchToProps)(DomainGroupCard);