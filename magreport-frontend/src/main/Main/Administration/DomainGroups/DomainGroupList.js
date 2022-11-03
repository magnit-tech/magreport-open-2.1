import React, {useState} from 'react';
import { useSnackbar } from 'notistack';
import clsx from 'clsx';
import Tooltip from '@material-ui/core/Tooltip';
import Icon from '@mdi/react';
import { mdiWeb } from '@mdi/js';
import { Card, 
    List, InputBase,
    Button, FormControl,
    InputAdornment, MenuItem
    } from '@material-ui/core';
import SearchIcon from '@material-ui/icons/Search';
import AddIcon from '@material-ui/icons/Add';

// local
import DomainGroupCard from './DomainGroupCard'
import dataHub from 'ajax/DataHub';
import DataLoader from "../../../DataLoader/DataLoader";
import {FolderItemTypes} from 'main/FolderContent/FolderItemTypes';
import AsyncAutocomplete from '../../../../main/AsyncAutocomplete/AsyncAutocomplete';

// styles 
import { DomainGroupCSS, DomainSelect } from "./DomainGroupCSS";

function DomainGroupList(props){
    const classes = DomainGroupCSS();

    const [selectedDomainGroup, setSelectedDomainGroup] = useState(-1);
    const { enqueueSnackbar } = useSnackbar();
    const [domainGroupFilterValue, setDomainGroupFilterValue] = useState("");
    const [domainsList, setDomainsList] = useState([]);
    const [domain, setDomain] = useState({});
    const [selectedDomainGroupToAdd, setSelectedDomainGroupToAdd] = useState("");

    const listItems=[]

    function filterDomainGroups(){

        let filteredList = [];
        const filterStr = domainGroupFilterValue.toLowerCase();
        let filteredByDomainList = props.items.filter(i=>i.domainId===domain.value);
        for (let i in filteredByDomainList) {
            if (filteredByDomainList[i].groupName.toLowerCase().indexOf(filterStr) > -1) {
                filteredList.push(filteredByDomainList[i])
            }
        }
        return filteredList
    }

    function handleFilterDomainGroup (e) {
        setDomainGroupFilterValue(e.target.value);
    }

    function handleDeleteDomainGroup (domainGroup) {
        if (props.itemsType === FolderItemTypes.roles){
            dataHub.roleController.deleteDomainGroups(props.roleId, [ domainGroup], handleDeleteDomainGroupFromRoleResponse)
        }
        else {
            
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
        listItems.push(
            <DomainGroupCard 
                key={i.groupName} 
                itemsType={props.itemsType}
                domainGroupDesc={i} 
                defaultDomain = {domain.value}
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
        let domain = data.filter(i => i.isDefault === true).map(i => {return {value: i.id, name: i.name}})[0];
        let domList = data.sort((a,b) => b.isDefault - a.isDefault).slice();
        setDomainsList(domList);
		setDomain(domain);        
    }

    function handleAddDomainGroupToRole(){
        for (let u of props.items /*domainGroupsData.domainGroups*/) {
            if (u === selectedDomainGroupToAdd.name) {
                enqueueSnackbar("Доменной группе уже назначена эта роль!", {variant : "error"});
                return;
            }
        }
        dataHub.roleController.addDomainGroups(props.roleId, [{domainId: selectedDomainGroupToAdd.domainId, groupName: selectedDomainGroupToAdd.name}], handleAddDomainGroupToRoleResponse)
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
                            <AsyncAutocomplete
                                className={classes.domainAutocomplete}
                                size = 'small'
                                disabled = {false}
                                typeOfEntity = {"domainGroup"}
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
                    <div className={classes.selectDiv}>
                        <FormControl variant='outlined' size='small'/* className={classes.formControl}*/>
						    <DomainSelect
                                id="outlined-select-currency"
                                size='small'
							    select
							    value={domain.value}
							    name={domain.name}
							    onChange={(e)=>{setDomain(e.target)}}
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
                        <div  className={classes.search}>
                            <div className={classes.searchIcon}>
                                <SearchIcon />
                            </div>
                            <InputBase
                                placeholder="Поиск…"
                                classes={{
                                    root: classes.inputRoot,
                                    input: classes.inputInput,
                                }}
                                onChange={handleFilterDomainGroup}
                                value={domainGroupFilterValue}
                            />
                        </div>
                    </div>
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