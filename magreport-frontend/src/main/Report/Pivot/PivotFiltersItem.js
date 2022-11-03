import React, { useState } from 'react';
import 'date-fns';
import TextField from '@material-ui/core/TextField';
import Typography from '@material-ui/core/Typography';
import RuLocalizedUtils from 'utils/RuLocalizedUtils'
import ruLocale from "date-fns/locale/ru";
import {MuiPickersUtilsProvider, KeyboardDatePicker, KeyboardDateTimePicker} from '@material-ui/pickers';

// local
import TransferList from './TransferList';
import {PivotCSS} from './PivotCSS';

/**
 * 
 * @param {*} props.jobId - id задания
 * @param {*} props.field - поле с фильтрацией
 * @param {*} props.fieldsLists - список полей (нужен для фильтрации списка значений)
 * @param {*} props.filterObject - текущий фильтр
 * @param {*} props.filterGroup - группа фильтров для измерений
 * @param {*} props.metricFilterGroup - группа фильтров для метрик
 * @param {*} props.filterType - тип фильтра
 * @param {*} props.onChangeValues - колбэк при изменении значений фильтра
 * 
 */

function PivotFiltersItem(props){
    const classes = PivotCSS();

    const [from, setFrom] = useState(props.filterObject.values[0] ?? '');
    const [to,     setTo] = useState(props.filterObject.values[1] ?? '');

    function handleChangeBetweenValues(source, value){
        if (source === 'from') {
            setFrom(value);
            props.onChangeValues([value, to])
        } else if (source === 'to'){
            setTo(value);
            props.onChangeValues([from, value])
        }
    }

    const handleValueInput = (e) => {
        if (props.field.type === 'DOUBLE'){
            const onlyNums = e.target.value.match(/[-+]?[0-9]*\.?[0-9]*/);
            e.target.value = onlyNums;
          //  props.onChangeValues([onlyNums]);
        } else if (props.field.type === 'INTEGER'){
            const onlyNums = e.target.value.match(/[-+]?[0-9]*/);
            e.target.value = onlyNums;
         //   props.onChangeValues([onlyNums]);
        }
    }

    if (props.filterType === "BLANK" ) {
        return (<div></div>)
    }
    else if (props.filterType === "EQUALS" 
            || props.filterType === "CONTAINS_CI"     || props.filterType === "CONTAINS_CS"
            || props.filterType === "LESSER_OR_EQUALS"|| props.filterType === "GREATER_OR_EQUALS"
            || props.filterType === "GREATER"         || props.filterType === "LESSER") {
        if (props.field?.type === 'DATE') {
            return (
                <MuiPickersUtilsProvider utils={RuLocalizedUtils} locale={ruLocale}>
                    <KeyboardDatePicker
                        size="small"
                        className={classes.equalText}
                        autoOk
                        views={ ["year", "month", "date"]  }
                        openTo="date"
                        variant="inline"
                        format="yyyy-MM-dd"
                        id="dateStart"
                        value={from}
                        onChange={(date) => {props.onChangeValues([date.toISOString().slice(0,10)]); setFrom(date)}}
                            KeyboardButtonProps={{
                                'aria-label': 'change date',
                        }}
                    />
                </MuiPickersUtilsProvider>
            )
        } else if ( props.field?.type === 'TIMESTAMP') {
            return (
                <MuiPickersUtilsProvider utils={RuLocalizedUtils} locale={ruLocale}>
					<KeyboardDateTimePicker
										size = 'small'
										className={classes.dtmStyle}
										id="datePickerStart"
										view={["date" | "year" | "month" | "hours" | "minutes"]}
										openTo="hours"
										ampm={false}
										disableFuture
                                        format="yyyy-MM-dd HH:mm:ss.0"
										margin="normal"
										inputVariant="filled"
										value={from}
										onChange={(date) => {props.onChangeValues([date]); setFrom(date)}}
                                        cancelLabel="ОТМЕНИТЬ"
                                        okLabel="СОХРАНИТЬ"
				    />
                </MuiPickersUtilsProvider>
            )
        } else {
            return (
                <TextField
                    defaultValue = {from}
                    size = "small"
                    multiline
                    className={classes.equalText}
                    onInput = {handleValueInput}
                    onChange={e=>props.onChangeValues([e.target.value])}
                />
            )
        }
    } else if (props.filterType ==="BETWEEN" || props.filterType === "NOT_BETWEEN") {
        return (
            <div>
                <div className={classes.divFlex}> 
					<Typography className={classes.betweenTitle}> ОТ </Typography>
					<TextField 
						defaultValue = {from}
						size = "small"
						multiline
						className={classes.betweenText}
						onInput = {handleValueInput}
						onChange={e=>handleChangeBetweenValues('from',e.target.value)}
					/>
                </div>
                <div className={classes.divFlex}>
					<Typography className={classes.betweenTitle}> ДО </Typography>
					<TextField 
						defaultValue = {to}
						size = "small"
						multiline
						className={classes.betweenText}
						onInput = {handleValueInput}
						onChange={e=>handleChangeBetweenValues('to'  ,e.target.value)}
					/>
                </div>
            </div>
        )
    } else if (props.filterType === "IN_LIST") {
        return (
            <TransferList
                jobId = {props.jobId}
                field = {props.field}
                fieldsLists = {props.fieldsLists}
                filterValues = {props.filterObject.values}
                filterGroup = {props.filterGroup}
                metricFilterGroup = {props.metricFilterGroup}
                onChange={props.onChangeValues}
            />
        )
    } else {
        return (
            <p>
                Неизвестный тип фильтра
            </p>
        )
    }
}

export default PivotFiltersItem
