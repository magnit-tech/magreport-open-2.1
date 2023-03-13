import React, {useState, useRef} from 'react';
import { useSnackbar } from 'notistack';
import TextField from '@material-ui/core/TextField';
import Autocomplete from '@material-ui/lab/Autocomplete';
import { CircularProgress } from '@material-ui/core';
import dataHub from '../../ajax/DataHub';

export default function AsyncAutocomplete(props){
    const { enqueueSnackbar } = useSnackbar();
    const timer = useRef(0);

    const [entityToAddList, setEntityToAddList] = useState([]);
    const [openAsyncEntity, setOpenAsyncEntity] = useState(false);
    const [optionsAsyncEntity, setOptionsAsyncEntity] = useState([]);
    const [namePart, setNamePart] = useState(null);
    const [responsed, setResponsed] = useState(true);

    let requestId = useRef('');

    const loadingAsync = openAsyncEntity && !responsed; // && optionsAsyncEntity.length === 0;

    function handleOnInputChange(e, value){
        if (props.typeOfEntity === "domainGroup") {
            if (timer.current > 0){
                clearInterval(timer.current); // удаляем предыдущий таймер
            }
            timer.current = setTimeout(() => {setNamePart(value)}, 1000);
        }
    }

    function handleOnChange(e, value){
        let entity;
        for (let i of entityToAddList){
            if (i.name === value || i.domainName + '\\' + i.name === value){
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
                        requestId.current = dataHub.userController.users(handleUsers);
                    }
                    else if(props.typeOfEntity === "role"){
                        requestId.current = dataHub.roleController.getAll(handleRoles);
                    }
                    else if(props.typeOfEntity === "domainGroup"){
                        requestId.current = dataHub.adController.getDomainGroups(0, props.domainName, (namePart=== '' ? null : namePart), handleDomainGroups);
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
                        if (magrepResponse.ok ){
                            if (active && requestId.current === magrepResponse.requestId){
                                setResponsed(true);
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
                                        let aa = (a.domainName === props.defaultDomain || props.typeOfEntity === "role" || props.typeOfEntity === "domainGroup"? '' : a.domainName + '\\') + a.name ,
                                            bb = (b.domainName === props.defaultDomain || props.typeOfEntity === "role" || props.typeOfEntity === "domainGroup"? '' : b.domainName + '\\') + b.name ;
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
                                    entity.push((i.domainName === props.defaultDomain || props.typeOfEntity === "role" || props.typeOfEntity === "domainGroup" ? '': i.domainName +'\\')+i.name);
                                };

                                setEntityToAddList(entityTmp);
                            }
                        }
                        else {
                            enqueueSnackbar("Ошибка: " + magrepResponse.data, {variant : "error"});
                            setEntityToAddList([]);
                            setOpenAsyncEntity(false);
                        }
                    };

                }
                else {
                    for (let i of entityToAddList){
                        entity.push((i.domain?.name === props.defaultDomain || props.typeOfEntity === "role" || props.typeOfEntity === "domainGroup"? '': i.domain?.name+'\\')+i.name);
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
            setEntityToAddList([]);
            setResponsed(false);
        }, [namePart, props.domainName]
    );

    React.useEffect(() => {
            if (!openAsyncEntity) {
                setOptionsAsyncEntity([]);
            }
        }, [namePart, props.domainName]
    );



    return (
        <Autocomplete
            value = {namePart}
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
                    label = {props.typeOfEntity === "user" ? "Пользователи": (props.typeOfEntity === "domainGroup" ? "Доменные группы" : "")}
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