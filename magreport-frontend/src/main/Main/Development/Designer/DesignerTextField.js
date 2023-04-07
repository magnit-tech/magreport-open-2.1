import React from 'react';
import { TextField } from "@material-ui/core";
import clsx from 'clsx';

import IconButton from '@material-ui/core/IconButton';
import ClearIcon from '@material-ui/icons/Clear';
import Tooltip from '@material-ui/core/Tooltip';
import InputAdornment from '@material-ui/core/InputAdornment';
import {DesignerCSS} from './DesignerCSS';

/**
 * @callback onChange
 * @param {*} value
 */

/**
 * Текстовое поле
 * @param {Object} props - component properties
 * @param {(Number|String)} props.value - text field value
 * @param {String} props.label - text field label
 * @param {String} [props.type="text"] - valid HTML5 input type
 * @param {Boolean} [props.displayBlock=false] - if true, display as block
 * @param {Boolean} [props.fullWidth=false] - if true, the input will take up the full width of container
 * @param {Boolean} [props.multiline=false] - if true, then render text field as textarea
 * @param {(Number|String)} [props.rows=1] - number of rows to display when multiline option is set to true
 * @param {(Number|String)} [props.rowsMax=10] - max number of rows to display when multiline option is set to true
 * @param {Boolean} [props.error=false] - if true, then render text field as invalidated
 * @param {Boolean} [props.disabled=false] - if true, then render field as disabled
 * @param {Boolean} [props.clearable=false] - if true, then show Clear button
 * @param {onChange} props.onChange - callback that is called on text field value change
 * @returns {JSX.Element}
 * @constructor
 */
export default function DesignerTextField(props){

    //const [designerTextValue, setDesignerTextValue] = useState(props.value === 0 || props.value ? props.value : '');

    function handleChange(value){
       // setDesignerTextValue(value);
        props.onChange(value);
    }

    const classes = DesignerCSS(); 

    return (
        <TextField
            className = {clsx(classes.field, {[classes.displayBlock] : props.displayBlock})}
            style={{minWidth: props.minWidth, margin: props.margin}}
            label = {props.label}
            value = {props.value === 0 || props.value ? props.value : ''}
            placeholder = {props.placeholder}
            variant = {props.variant ? props.variant : "outlined"}
            type = {props.type ? props.type : "text"}
            fullWidth = {props.fullWidth}
            multiline = {props.multiline}
            minRows = {props.rows ? props.rows : 1}
            maxRows = {props.rowsMax ? props.rowsMax : 10}
            error = {!!props.error}
            disabled = {props.disabled}
            size={props.size}
          //  InputProps = {props.inputProps}
            InputLabelProps = {props.InputLabelProps}
            helperText = {props.helperText}
            onInput  = {props.onInput ? (event) => props.onInput(event):()=>{} }
            onChange = {event => {props.type === "file" ? handleChange(event) : handleChange(event.target.value)}}
            InputProps={{ ...props.inputProps,
                endAdornment: (
                    props.clearable ? 
                    <InputAdornment position="end" /*className={classes.topInputAdornment}*/>      
                        <Tooltip title="Очистить" placement="top">
                            <IconButton 
                                aria-label="clear"
                                color='primary'
                                size='small'
                                onClick={()=> handleChange('')}
                            >
                                <ClearIcon fontSize='small' />
                            </IconButton>
                        </Tooltip>
                    </InputAdornment>     
                    : null   
                ),
            }}

        />
    )
}