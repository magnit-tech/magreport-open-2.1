import React, {useState} from 'react';
import TextField from '@material-ui/core/TextField';
import Autocomplete from '@material-ui/lab/Autocomplete';
import { CircularProgress } from '@material-ui/core';
import dataHub from '../../ajax/DataHub';

export default function AsyncAutocomplete(props){

    const [entityToAddList, setEntityToAddList] = useState([]);
    const [openAsyncEntity, setOpenAsyncEntity] = React.useState(false);
    const [optionsAsyncEntity, setOptionsAsyncEntity] = React.useState([]);
    const [namePart, setNamePart] = React.useState(null);

    const loadingAsync = openAsyncEntity; // && optionsAsyncEntity.length === 0;

    function handleOnInputChange(e, value){
        if (value.length >= 3 && props.typeOfEntity === "domainGroup") {
            setNamePart(value)
        }
    }

    function handleOnChange(e, value){
        let entity;
        for (let i of entityToAddList){
            if (i.name === value || i.domain?.name + '\\' + i.name === value){
                entity = i;
            };
        };
        props.onChange(entity);
    }

    React.useEffect(() => {
            let active = true;
            if (!loadingAsync) {
                return undefined;
            }


            (async () => {
                let entity = [];
                if (entityToAddList.length === 0){
                    if (props.typeOfEntity === "user"){
                        dataHub.userController.users(handleUsers);
                    }
                    else if(props.typeOfEntity === "role"){
                        dataHub.roleController.getAll(handleRoles);
                    }
                    else if(props.typeOfEntity === "domainGroup"){
                        dataHub.adController.getDomainGroups(0, props.domainName, namePart, handleDomainGroups);
                    }

                    function handleRoles(magrepResponse){
                        sortEntity(magrepResponse)
                    }

                    function handleUsers(magrepResponse){
                        sortEntity(magrepResponse)
                    }


                    function handleDomainGroups(magrepResponse){
                        sortEntity(magrepResponse)
                    }

                    function sortEntity(magrepResponse){
                        if (magrepResponse.ok && active){
                            let entityTmp = magrepResponse.data.filter(props.filterOfEntity ? props.filterOfEntity : ()=>{return true});
                            switch  (props.typeOfEntity){
                                case 'user':
                                    entityTmp = entityTmp.map(i=>{return {id: i.id, domainName: i.domain?.name, name: i.name}});
                                    break;
                                case 'role':
                                    entityTmp = entityTmp.map(i=>{return {id: i.id, domainName: i.domain?.name, name: i.name}});
                                    break;
                                case 'domainGroup':
                                    entityTmp =  entityTmp.map(i=>{return {id: i.domainId, domainName: i.domainName, name: i.groupName}});
                                    break;
                                default:  
                                    break;
                            }
                            
                            entityTmp.sort(
                                function (a, b) {
                                    let aa = (a.domainName === props.defaultDomain || props.typeOfEntity === "role" ? '' : a.domainName + '\\') + a.name ,
                                        bb = (b.domainName === props.defaultDomain || props.typeOfEntity === "role" ? '' : b.domainName + '\\') + b.name ;
                                    if (aa < bb) {
                                        return -1;
                                    }
                                    if (aa > bb) {
                                        return 1;
                                    }
                                    return 0;
                                }
                            );

                            for (let i of entityTmp){
                                entity.push((i.domainName === props.defaultDomain || i.domainName === props.defaultDomain || props.typeOfEntity === "role" ? '': i.domainName +'\\')+i.name);
                            };

                            setEntityToAddList(entityTmp);
                        }
                    };

                }
                else {
                    for (let i of entityToAddList){
                        entity.push((i.domain?.name === props.defaultDomain || props.typeOfEntity === "role" ? '': i.domain?.name+'\\')+i.name);
                    };
                };
                setOptionsAsyncEntity(entity);
            })();
            
            return () => {
                active = false;
            };
        }, [loadingAsync, namePart, props.domainName] // eslint-disable-line
    );

    React.useEffect(() => {
        if (!openAsyncEntity) {
            setOptionsAsyncEntity([]);
        }
        }, [openAsyncEntity, namePart, props.domainName]
    );

    React.useEffect(() => {
            setEntityToAddList([]);
        }, [namePart, props.domainName]
    );

    return (
        <Autocomplete
            className = {props.className}
            id="asynchronousRoleListToAdd"
            size = {props.size ?? 'medium'}
            key={props.resetAutocomplete}
            disabled = {props.disabled}
            open={openAsyncEntity}
            onOpen={() => {
                setOpenAsyncEntity(true);
            }}
            onClose={() => {
                setOpenAsyncEntity(false);
            }}
            getOptionSelected={(option, value) => option.name === value.name}
            options={optionsAsyncEntity}
            loading={loadingAsync}
            onChange={handleOnChange}
            onInputChange={handleOnInputChange}
            renderInput={params => (
                <TextField
                    {...params}
                    label={props.typeOfEntity === "user" ? "Пользователи": (props.typeOfEntity === "domainGroup" ? "Доменные группы" : "")}
                    fullWidth
                    variant="outlined"
                    InputProps={{
                        ...params.InputProps,
                        endAdornment: (
                        <React.Fragment>
                            {loadingAsync ? <CircularProgress color="inherit" size={20} /> : null}
                            {params.InputProps.endAdornment}
                        </React.Fragment>
                        ),
                    }}
                />
            )}
        />
    );
}