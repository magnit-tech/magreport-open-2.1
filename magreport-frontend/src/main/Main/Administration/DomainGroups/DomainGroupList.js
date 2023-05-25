import React, {useState} from 'react';
import { useSnackbar } from 'notistack';
import clsx from 'clsx';
import Tooltip from '@material-ui/core/Tooltip';
import Icon from '@mdi/react';
import { mdiWeb } from '@mdi/js';
import { Card, 
    List,
    Button, FormControl,
    InputAdornment, MenuItem
    } from '@material-ui/core';
import AddIcon from '@material-ui/icons/Add';

// local
import DomainGroupCard from './DomainGroupCard'
import dataHub from 'ajax/DataHub';
import DataLoader from "../../../DataLoader/DataLoader";
import {FolderItemTypes} from 'main/FolderContent/FolderItemTypes';
import AsyncAutocomplete from '../../../../main/AsyncAutocomplete/AsyncAutocomplete';
import IconButton from '@material-ui/core/IconButton';
import DeleteIcon from '@material-ui/icons/Delete';

// styles 
import { DomainGroupCSS, DomainSelect } from "./DomainGroupCSS";

function DomainGroupList(props){
    const classes = DomainGroupCSS();

    const [selectedDomainGroup, setSelectedDomainGroup] = useState(-1);
    const { enqueueSnackbar } = useSnackbar();
    //const [domainGroupFilterValue, setDomainGroupFilterValue] = useState("");
    const [domainsList, setDomainsList] = useState([]);
    const [domain, setDomain] = useState(null);
    const [leftDomain, setLeftDomain] = useState(null);
    const [selectedDomainGroupToAdd, setSelectedDomainGroupToAdd] = useState("");
    const [defaultDomain, setDefaultDomain] = useState("");

    let listItems = []
    let rows = []

    function filterDomainGroups(){
        let filteredByDomainList = props.items.filter(i=>i.domainId===domain);
        return filteredByDomainList.sort((a,b) => a.groupName.localeCompare(b.groupName))
    }

    function handleDeleteDomainGroup (domainGroup) {
        if (props.itemsType === FolderItemTypes.roles){
            dataHub.roleController.deleteDomainGroups(props.roleId, [ domainGroup], handleDeleteDomainGroupFromRoleResponse)
        }
    }

    function handleDeleteDomainGroupFromRoleResponse (magrepResponse) {
        if (magrepResponse.ok) {
            enqueueSnackbar("Доменная группа удалена!", {variant : "success"});
            props.onNeedReload ()
        }
        else {
            enqueueSnackbar("Не удалось удалить доменную группу", {variant : "error"});
        }
    }
    
    for (let i of filterDomainGroups()){
        
        rows.push({
            id: i.domainId + i.groupName, 
            domainName: i.domainName, 
            groupName: i.groupName,
            action: <IconButton 
                aria-label="Удалить" 
                color="primary" 
                onClick={handleDeleteDomainGroup}
            >
            <DeleteIcon />
        </IconButton>
        
        })
        listItems.push(
            <DomainGroupCard 
                key={i.groupName} 
                itemsType={props.itemsType}
                domainGroupDesc={i} 
                defaultDomain = {defaultDomain}
                roleId={props.roleId}
                isSelected={selectedDomainGroup===i}
                setSelectedDomainGroup={setSelectedDomainGroup}
                //enableRoleReload={props.onNeedReload}
                onDeleteDomainGroup={handleDeleteDomainGroup}
                showDeleteButton={props.showDeleteButton}
            />
        )
    }

    function handleDataLoaded(data){
        let dom = data.filter(i => i.isDefault === true).map(i => {return  i.id})[0];
        let domList = data.sort((a,b) => b.isDefault - a.isDefault).slice();
        setDefaultDomain(data.filter(i=>i.isDefault).map(item=>item.id)[0]);
        setDomainsList(domList)
        setDomain(dom);
        setLeftDomain(dom);
    }

    function handleAddDomainGroupToRole(){
        if (selectedDomainGroupToAdd){
            for (let u of props.items /*domainGroupsData.domainGroups*/) {

                if (u.groupName === selectedDomainGroupToAdd.name && u.domainName === selectedDomainGroupToAdd.domainName) {
                    enqueueSnackbar("Доменной группе уже назначена эта роль!", {variant : "error"});
                    return;
                }
            }
            dataHub.roleController.addDomainGroups(props.roleId, [{domainId: selectedDomainGroupToAdd.id, groupName: selectedDomainGroupToAdd.name}], handleAddDomainGroupToRoleResponse)
        } else {
            enqueueSnackbar("Доменная группа не выбрана!", {variant : "error"});
        }
    }

    function handleAddDomainGroupToRoleResponse(magrepResponse){
        if (magrepResponse.ok) {
            enqueueSnackbar("Доменная группа добавлена!", {variant : "success"});
            props.onNeedReload ();
        }
        else {
            enqueueSnackbar("Не удалось добавить доменную группу", {variant : "error"});
        }
    }

    function handleOnChangeAddDomainGroupText(value){
        setSelectedDomainGroupToAdd(value);
    }

    return (
        <DataLoader
            loadFunc = {dataHub.userServiceController.getDomainList}
            loadParams = {[]}
            onDataLoaded = {handleDataLoaded}
            onDataLoadFailed = {(message) => enqueueSnackbar(`При загрузке данных произошла ошибка: ${message}`, {variant: "error"})}
        >
            <Card elevation={3} className={classes.domainGroupList}> 
                <div className={classes.userAddPanel}>
                    {props.showDeleteButton &&
                        <div className={classes.roleAutocompleteDiv}>
                            <FormControl variant='outlined' size='small'>
						        <DomainSelect
                                    id="outlined-select-currency"
                                    size='small'
							        select
							        value={leftDomain}
							        onChange={(e)=>{setLeftDomain(e.target.value)}}
							        variant="outlined"
							        InputProps={{
								        startAdornment: (
									        <Tooltip title='Домен' placement="top">
										        <InputAdornment position="start" className={classes.domainTooltip}>
											        <Icon path={mdiWeb} size={0.9}/>  
										        </InputAdornment>
									        </Tooltip>
								        ),
							        }}
						        >
							        {domainsList.map((i, index)=>
								        <MenuItem key = {i.id} value={i.id} ListItemClasses = {{button: clsx({[classes.menuList]:  index===0 })}}> {i.name}</MenuItem>
							        )}
						        </DomainSelect>
					        </FormControl>
                            <AsyncAutocomplete
                                className={classes.domainAutocomplete}
                                size = 'small'
                                disabled = {false}
                                typeOfEntity = {"domainGroup"}
                                defaultDomain = {defaultDomain}
                                domainName = {domainsList.filter(i=>i.id === leftDomain).map(i=>i.name)}
                                onChange={handleOnChangeAddDomainGroupText}
                            /> 
                            <Button
                                className={classes.addButton}
                                size='small'
                                color="primary"
                                variant="outlined"
                                disabled = {false}
                                onClick={handleAddDomainGroupToRole}
                             >
                                <AddIcon/>Добавить
                            </Button>
                        </div>
                    }
                </div>
                <div style={{display: 'flex', flex: 1, flexDirection: 'column', position: 'relative'}}>
                    <List dense className={classes.domainGroupListBox}>
                        {listItems}
                    </List>
                </div>
            </Card>
        </DataLoader>
    )
}

export default DomainGroupList