
import React from 'react';
import AddCircleOutlineIcon from '@material-ui/icons/AddCircleOutline';
import IconButton from '@material-ui/core/IconButton';
import DeleteIcon from '@material-ui/icons/Delete';
//Local
import UnboundedValueFields from './UnboundedValueFields';
//Styles
import {TupleListFieldsCSS} from '../FilterInstanceCSS';

export default function TupleListFields(props){

    const classes = TupleListFieldsCSS();

    let resultFields = [];
    
    function handleChange(fields, level){
        let index = props.fields.findIndex(i=>i.level === level);
        let newFields = props.fields.slice();
        newFields.splice(index, 2, fields[0], fields[1]);
        props.onChange(newFields);
    }

    for (let i = 1; i <= props.fields.length/2; i++) {
        resultFields.push(
            <div key={i} className={classes.flexCenter}>
                <UnboundedValueFields
                    readOnly = {props.readOnly}
                    fields = {props.fields.filter(v => v.level === i)}
                    onChange = {(fields) => handleChange(fields, i)}
                />
                {
                    !props.readOnly &&
                        <IconButton
                            className={classes.delBtn}
                            aria-label="add"
                            color="primary"
                            onClick={() => handleChangeLevelClick('remove', i)}
                        >
                            <DeleteIcon/>
                        </IconButton>
                }
            </div>
        )
    }

  function handleChangeLevelClick(operation, level){

        let newFields = props.fields.slice();
        if (operation === 'add'){
            let newEmptyLevelId = {
                type: 'ID_FIELD',
                level: props.fields.length > 0 ? props.fields[props.fields.length-1].level+1 : 1,
                name : '',
                description : ''
            };
            let newEmptyLevelCode = {
                type: 'CODE_FIELD',
                level: props.fields.length > 0 ? props.fields[props.fields.length-1].level+1 : 1,
                name : '',
                description : ''
            };
            newFields.push(newEmptyLevelId);
            newFields.push(newEmptyLevelCode);
            
        }
        else {
            let index = newFields.findIndex(i => i.level === level);
            newFields.splice(index, 2);
        }
        props.onChange(newFields, []);
    } 

    return (
        <div>
            {
                resultFields
            }
            {
                !props.readOnly &&
                <div className={classes.flexCenter}>
                    <IconButton
                        aria-label="add"
                        color="primary"
                        onClick={() => handleChangeLevelClick('add', 0)}
                    >
                        <AddCircleOutlineIcon fontSize='large'/>
                    </IconButton>
                </div>
            }
        </div>
    )
}