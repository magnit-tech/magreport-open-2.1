import React, {useState} from 'react';
import TextField from '@material-ui/core/TextField';
import Autocomplete from '@material-ui/lab/Autocomplete';
import { CircularProgress } from '@material-ui/core';
import dataHub from '../../ajax/DataHub';

function AsyncAutocomplete(props){

    const [entityToAddList, setEntityToAddList] = useState([]);
    const [openAsyncEntity, setOpenAsyncEntity] = React.useState(false);
    const [optionsAsyncEntity, setOptionsAsyncEntity] = React.useState([]);
   // const [defaultDomain, setDefaultDomain] = React.useState('');
    let defaultDomain = React.useRef('')
    if (defaultDomain.current === '' && (props.typeOfEntity === "domainGroup" || props.typeOfEntity === "user")) {dataHub.userServiceController.getDomainList(handleGetDomainList)};

    const loadingAsync = openAsyncEntity && optionsAsyncEntity.length === 0;

    function handleOnInputChange(e, value){
        if ((value.length >= 3) && (props.typeOfEntity === "domainGroup")) { dataHub.adController.getDomainGroups (0, [props.domainName], value, handleDomainGroups); }
    }

    function handleDomainGroups(magrepRespone){
        if(magrepRespone.ok){
            if (props.typeOfEntity === "domainGroup" || props.typeOfEntity === "user"){
                setOptionsAsyncEntity(magrepRespone.data.map(i=> (i.domainName === defaultDomain.current ? '': i.domainName+'\\')+i.groupName));
                setEntityToAddList(magrepRespone.data.map(i => {return {domainId: i.domainId, name: i.groupName}}))
            } else {
                setOptionsAsyncEntity(magrepRespone.data.map(i=> i.groupName));
                setEntityToAddList(magrepRespone.data.map(i => {return {name: i.groupName}}))
            }
        }
    }

    function handleGetDomainList(magrepRespone){
        if(magrepRespone.ok){
            defaultDomain.current = magrepRespone.data.filter(i=>i.isDefault).map(item=>item.name)[0];
        } else {
            defaultDomain.current = null
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
                    }

                    function handleRoles(magrepResponse){
                        sortEntity(magrepResponse)
                    }

                    function handleUsers(magrepResponse){
                        sortEntity(magrepResponse)
                    }

                    function sortEntity(magrepResponse){
                        if (magrepResponse.ok && active){
                            let entityTmp = magrepResponse.data.filter(props.filterOfEntity ? props.filterOfEntity : ()=>{return true}).sort(
                                function (a, b) {
                                    let aa = (a.domain?.name === defaultDomain.current || props.typeOfEntity === "role" ? '': a.domain?.name+'\\')+a.name,
                                        bb = (b.domain?.name === defaultDomain.current || props.typeOfEntity === "role" ? '': b.domain?.name+'\\')+b.name;
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
                                entity.push((i.domain?.name === defaultDomain.current || props.typeOfEntity === "role" ? '': i.domain?.name+'\\')+i.name);
                            };

                            setEntityToAddList(entityTmp);
                        }
                    };

                }
                else {
                    for (let i of entityToAddList){
                        entity.push((i.domain?.name === defaultDomain.current || props.typeOfEntity === "role" ? '': i.domain?.name+'\\')+i.name);
                    };
                };
                setOptionsAsyncEntity(entity);
            })();
            
            return () => {
                active = false;
            };
        }, [loadingAsync] // eslint-disable-line
    );

    React.useEffect(() => {
        if (!openAsyncEntity) {
            setOptionsAsyncEntity([]);
        }
        }, [openAsyncEntity]
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

export default AsyncAutocomplete;   