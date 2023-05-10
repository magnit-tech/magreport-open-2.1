
import React , {useState} from 'react';

// components
import TextField from '@material-ui/core/TextField';
import IconButton from '@material-ui/core/IconButton';
import ClearIcon from '@material-ui/icons/Clear';
import Tooltip from '@material-ui/core/Tooltip';
import InputAdornment from '@material-ui/core/InputAdornment';
import ReorderIcon from '@material-ui/icons/Reorder';

// local
import InvalidValuesView from '../../../Report/filters/InvalidValuesView';
import FilterStatus from '../../../Report/filters/FilterStatus';
import SeparatorMenu from '../../../Report/filters/SeparatorMenu';

// styles
import {DesignerCSS} from './DesignerCSS';

/**
 * @callback onChange
 * @param {*} value
 */

/**
 * Текстовое поле с выбором разделителя
 * @param {Object} props - component properties
 * @param {Array} props.value - text field value
 * @param {Array} props.invalidValues - array of invalid values
 * @param {String} props.status - status
 * @param {String} props.label - text field label
 * @param {String} props.source - the source
 * @param {Boolean} [props.mandatory=false] - if true, the vakue is mandatory
 * @param {Boolean} [props.displayBlock=false] - if true, display as block
 * @param {Boolean} [props.fullWidth=false] - if true, the input will take up the full width of container
 * @param {Boolean} [props.multiline=false] - if true, then render text field as textarea
 * @param {Boolean} [props.disabled=false] - if true, then render field as disabled
 * @param {onChange} props.onChange - callback that is called on text field value change
 * @param {Boolean} [props.error=false] - if true, then render text field as invalidated
 * @returns {JSX.Element}
 * @constructor
 */
export default function DesignerTextFieldWithSeparator(props){

    const classes = DesignerCSS(); 

    const separatorsArray = [
        {name: 'Точка с запятой',  value: ';', checked: true},
        {name: 'Запятая',  value: ',', checked: true},
        {name: 'Пробел',  value: ' ', checked: true},
        {name: 'Табуляция',  value: '\\t', checked: true},
        {name: 'Перенос строки',  value: '\\n', checked: true},
    ];

    const [textValue, setTextValue] = useState(buildTextFromLastValues(props.value));
    const [separators, setSeparators] = useState(separatorsArray);
    const [showInvalidValues, setShowInvalidValues] = useState(false);
    

    function handleChangeSeparators(sArr) {
        setSeparators(sArr);
    }

    /*
        Получить строку по предыдующим значениям параметров
    */

    function buildTextFromLastValues(lastParameters){
        return lastParameters.join(';')
    }

    function getRegexp() {
        let s = ""
        let s2 = ""

        let tb = false
        let space = false
        let n = false

        for(let sp of separators) {
            if (sp.checked && sp.name === "Точка с запятой") {
                s += sp.value
                s2 += sp.value
            }
            if (sp.checked && sp.name === "Запятая") {
                s += sp.value
                s2 += sp.value
            }
            if (sp.checked && sp.name === "Пробел") {
                s += sp.value
                space = true
            }
            if (sp.checked && sp.name === "Табуляция") {
                s += sp.value
                tb = true
            }
            if (sp.checked && sp.name === "Перенос строки") {
                s += sp.value
                n = true
            }
        }
        if (space && tb && n) s = s2 + "\\s"
        
        const exp = new RegExp(`[${s}]+(?=(?:[^"]*"[^"]*")*[^"]*$)`, 'g')
        return exp
    }

    function handleTextChanged(newText){   
        setTextValue(newText);

        if (newText === ""){
            props.onChange(props.source, [], "success");
        }
        else {

            let re1 = getRegexp()

            let emailList = newText.split(re1).map(elem => elem.trim().replace(/"/g,""));

            for( let i = emailList.length; i--;){
                if ( emailList[i].trim().length === 0) emailList.splice(i, 1); /* удаление пустых значений */
            }

            let values = [];
            for(let code of emailList){
                values.push(code);
            }

            let isSelected = (emailList.length > 0) ? true : false; 

            if (isSelected) {
                props.onChange(props.source, values, "waiting");
            }
            else {
                props.onChange(props.source, [], "success");
            }   
        }
    }

    return (
        <div>
            <TextField
                size = {props.size}
                className={classes.field}
                id="input-with-icon-textfield"
                label={props.label}
                value={textValue}
                variant="outlined"
                multiline ={props.multiline}
                disabled = {props.disabled}
                displayblock = {props.displayBlock}
                fullWidth = {props.fullWidth}
                onChange={e => handleTextChanged(e.target.value)}
                error = {props.error}
                InputProps={{
                    endAdornment: (
                        <InputAdornment position="end" className={classes.topInputAdornment}>
                            <SeparatorMenu
                                title={"Разделители"}
                                separators={separators}
                                onChangeSeparators={handleChangeSeparators}
                            />
                                
                            <Tooltip title="Очистить" placement="top">
                                <IconButton 
                                    aria-label="clear"
                                    color='primary'
                                    size='small'
                                    onClick={()=> handleTextChanged("")}
                                >
                                    <ClearIcon fontSize='small' />
                                </IconButton>
                            </Tooltip>

                            <FilterStatus status={props.status} />
                          
                            {
                                props.status === 'error' && !!props.invalidValues.length &&
                               
                                    <Tooltip title="Показать некорректные значения" placement="top">
                                        <IconButton 
                                            aria-label="clear"
                                            size='small'
                                            onClick={() => setShowInvalidValues(true)}
                                        >
                                            <ReorderIcon fontSize='small' />
                                        </IconButton>
                                    </Tooltip>
                               
                            }
                        </InputAdornment>        
                    ),
                }}
            />
            {
                showInvalidValues &&
                <InvalidValuesView 
                    values={props.invalidValues}
                    onClose={() => {setShowInvalidValues(false)}}
                />
            }
        </div>
    );
}