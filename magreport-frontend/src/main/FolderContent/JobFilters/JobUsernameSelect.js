import React, {useState} from 'react';
import TextField from '@material-ui/core/TextField';
import Autocomplete from '@material-ui/lab/Autocomplete';
import { CircularProgress } from '@material-ui/core';

// styles 
import { JobUsernameSelectCSS } from './JobFiltersCSS'

/**
  * Компонент выпадающего списка пользователей
  * 
  * @param {props} user - список пользователей, для отображения в фильтре, если выбраны
  * @param {props} label - заголовок поля
  * @param {props} onDataLoad - Объект для доступа к БД
  * @param {props} onChange - функция выбранное значение
  * @returns {Component} React component
  */
function JobUsernameSelect(props){

   const classes = JobUsernameSelectCSS()

    const [entityToAddList, setEntityToAddList] = useState([]);
    const [openAsyncEntity, setOpenAsyncEntity] = React.useState(false);
    const [optionsAsyncEntity, setOptionsAsyncEntity] = React.useState([]);

    const loadingAsync = openAsyncEntity && optionsAsyncEntity.length === 0;

    function handleOnChange(e, value){
        props.onChange(value);
    }

    // eslint-disable-next-line react-hooks/exhaustive-deps
    React.useEffect(() => {
            let active = true;

            if (!loadingAsync) {
                return undefined;
            }

            (async () => {
                let entity = [];
                if (entityToAddList.length === 0){
                    props.onDataLoad(handleAllUsers);

                    function handleAllUsers(magrepResponse){
                        if (magrepResponse.ok && active){
                            let entityTmp = magrepResponse.data.sort(
                                function (a, b) {
                                    if (a.name < b.name) {
                                        return -1;
                                    }
                                    if (a.name > b.name) {
                                        return 1;
                                    }
                                    return 0;
                                }
                            );

                            for (let i of entityTmp){
                                entity.push({id: i.id, name: i.name});
                            };

                            setEntityToAddList(entityTmp);
                        }
                    };

                }
                else {
                    for (let i of entityToAddList){
                        entity.push({id: i.id, name: i.name});
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
            id="datasourceTypes"
            size="small"
            open={openAsyncEntity}
            onOpen={() => {
                setOpenAsyncEntity(true);
            }}
            onClose={() => {
                setOpenAsyncEntity(false);
            }}
            getOptionLabel = {option => option.name}
            getOptionSelected={(option, value) => option.id === value.id}
            className={classes.formControl}
            options={optionsAsyncEntity}
            loading={loadingAsync}
            value={props.user ? props.user : []}
            noOptionsText={"Нет элементов"}
            onChange={handleOnChange}
            multiple
            renderInput={params => (
                <TextField
                    {...params}
                    id="textuserType"
                    variant="filled"
                    label={props.label}
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

export default JobUsernameSelect;