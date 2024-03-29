import React, { useState, useRef, useEffect } from 'react';
import 'date-fns';

// date utils
import RuLocalizedUtils from 'utils/RuLocalizedUtils'
import ruLocale from "date-fns/locale/ru";
import {MuiPickersUtilsProvider, KeyboardDatePicker,} from '@material-ui/pickers';
import {dateToStringFormat} from 'utils/dateFunctions'
import FormHelperText from '@material-ui/core/FormHelperText';

// components
import Box from '@material-ui/core/Box';

// local
import PredefinedMenu from "./PredefinedMenu";
import FilterStatus from './FilterStatus'

// styles
import { DatePickersCSS } from "./FiltersCSS";

import {getRangeFields, getRangeFieldsValues} from "utils/reportFiltersFunctions";
import { Typography } from '@material-ui/core';

/**
 * @callback onChangeFilterValue
 */

/**
 * Компонент настройки фильтра диапазона дат отчета
 * @param {Object} props - свойства компонента
 * @param {Object} props.filterData - данные фильтра (объект ответа от сервиса)
 * @param {Object} props.lastFilterValue - объект со значениями фильтра из последнего запуска (как приходит от сервиса)
 * @param {Object} props.externalFiltersValue - параметров фильтров через URL. {"DATE_RANGE_CODE":{"begin_dt":<(дата в формате YYYY-MM-DD): string>,"end_dt":<(дата в формате YYYY-MM-DD): string>}}
 * @param {boolean} props.toggleClearFilter - при изменении значения данного свойства требуется очистить выбор в фильтре
 * @param {onChangeFilterValue} props.onChangeFilterValue - function(filterValue) - callback для передачи значения изменившегося параметра фильтра
 *                                                  filterValue - объект для передачи в сервис в массиве parameters
 * */
export default function DatesRange(props) {
    const classes = DatePickersCSS();

    let valueList = useRef({});

    const [startDate, setStartDate] = useState(null);
    const [endDate, setEndDate] = useState(null);
    const [toggleFilter, setToggleFilter] = useState(false);
    const [checkStatus, setCheckStatus] = useState("error")

    const mandatory = props.filterData.mandatory

    // Вычисляем id полей
    const {
        startFieldId,
        startFieldName,
        endFieldId,
        endFieldName
    } = getRangeFields(props.filterData);


    // Задаём значения по-умолчанию
    useEffect(() => {
        if (valueList.current.startDate === undefined){

            let defaultStartDate, defaultEndDate;

            const externalValue = props.externalFiltersValue ? props.externalFiltersValue[props.filterData.code] : null

            function checkDate(dateString) {
                if(/^\d{4}-\d{2}-\d{2}/.test(dateString)) return dateString;
                return null;
            }

            if(props.externalFiltersValue) {
                defaultStartDate = externalValue ? checkDate(externalValue.begin_dt) : null;
                defaultEndDate = externalValue ? checkDate(externalValue.end_dt) : null;
            } else {
                const { startValue, endValue } = getRangeFieldsValues(props.lastFilterValue, startFieldId, endFieldId);
                defaultStartDate = startValue;
                defaultEndDate = endValue;
            }

            let bd = new Date(defaultStartDate);
            let ed = new Date(defaultEndDate);

            valueList.current.startDate = defaultStartDate;
            valueList.current.endDate = defaultEndDate;
            setCheckStatus(Boolean(props.filterData.maxCountItems) && props.filterData.maxCountItems < ((ed.getTime() - bd.getTime())/(1000 *60*60*24) + 1) ? "limit"
                : defaultStartDate || ! mandatory ? 'success' : 'error')
            setStartDate(defaultStartDate);
            setEndDate(defaultEndDate);
            setValue(defaultStartDate, defaultEndDate);
        }
    }, []) // eslint-disable-line

    useEffect(() => {
        if (props.toggleClearFilter !== toggleFilter){
            valueList.current.startDate = null;
            valueList.current.endDate = null;
            setCheckStatus(mandatory ? 'error' : 'success')
            setStartDate(null);
            setEndDate(null);
            setToggleFilter(props.toggleClearFilter);
            setValue(null, null);
        }
    }, [props.toggleClearFilter]) // eslint-disable-line

    function handleDateChange(date, period){
        if (period === 'start'){
            valueList.current.startDate = date ? dateToStringFormat(date) : null;
            setStartDate(date);
        }
        if (period === 'end'){
            valueList.current.endDate = date ? dateToStringFormat(date) : null;
            setEndDate(date);
        };
        if (!period){
            let {startDate, endDate} = date
            valueList.current.startDate = dateToStringFormat(startDate);
            valueList.current.endDate = dateToStringFormat(endDate);
            setStartDate(startDate);
            setEndDate(endDate);
        };
        if ((valueList.current.startDate && valueList.current.endDate) || (!mandatory && !valueList.current.startDate && !valueList.current.endDate)){
            let bd = new Date(valueList.current.startDate);
            let ed = new Date(valueList.current.endDate);
            setCheckStatus(Boolean(props.filterData.maxCountItems) && props.filterData.maxCountItems < ((ed.getTime() - bd.getTime())/(1000 *60*60*24) + 1) ? "limit" :  'success' );
        }
        else {
            setCheckStatus('error')
        }
        setValue(valueList.current.startDate, valueList.current.endDate); 
    };

    function setValue(st, en){
        let parameters = []
        if (st && en){
            parameters= [
                {
                    values: [{
                        fieldId : startFieldId,
                        value : st
                    },
                    {
                        fieldId : endFieldId,
                        value : en
                    }]
                }
            ]
        }

        let stat = (st && en) || (!mandatory && !st && !en) ? 'success' : 'error';
        if (en && st){
            let bd = new Date(st);
            let ed = new Date(en);
            if (Boolean(props.filterData.maxCountItems) && props.filterData.maxCountItems < ((ed.getTime() - bd.getTime())/(1000 *60*60*24) + 1)){
                stat = 'limit'
            }
        }
        props.onChangeFilterValue({
            filterId : props.filterData.id,
            operationType: "IS_BETWEEN",
            validation:   stat,
            parameters,
            filterType: props.filterData.type.name || props.filterData.type,
            filterCode: props.filterData.code
        });      
    }

    return (
        <MuiPickersUtilsProvider utils={RuLocalizedUtils} locale={ruLocale}>
            <div className={classes.rangeRoot}>
                <Typography> {props.filterData.name}  </Typography>
                <div className={classes.cDiv}>
                    <div className={classes.fieldsDiv}>
                        <Box 
                            display="flex"
                            alignItems="center"
                            justifyContent="center"
                            marginRight = "16px"
                        >
                            <KeyboardDatePicker
                                size="small"
                                autoOk
                                views={["year", "month", "date"]}
                                openTo="date"
                                variant="inline"
                                inputVariant="outlined"
                                format="dd.MM.yyyy"
                                id="dateStart"
                                label={startFieldName}
                                value={startDate}
                                onChange={(date) => handleDateChange(date, "start")}
                                KeyboardButtonProps={{
                                    'aria-label': 'change date',
                                }}
                            />
                        </Box>
                        <Box 
                            display="flex"
                            alignItems="center"
                            justifyContent="center"
                            marginRight = "16px"
                        >
                            <KeyboardDatePicker
                                size="small"
                                autoOk
                                views={["year", "month", "date"]}
                                openTo="date"
                                variant="inline"
                                inputVariant="outlined"
                                format="dd.MM.yyyy"
                                id="dateEnd"
                                label={endFieldName}
                                minDateMessage={`${endFieldName} должна быть не ранее ${startFieldName}`}
                                value={endDate}
                                minDate={startDate}
                                onChange={(date) => handleDateChange(date, "end")}
                                KeyboardButtonProps={{
                                    'aria-label': 'change date',
                                }}
                            />
                        </Box>
                    </div>
                    <PredefinedMenu 
                        filterType = 'DATE_RANGE'
                        onClickValue = {dates => handleDateChange(dates, null)}
                    />
                    <span className={classes.status}>
                        <FilterStatus status={checkStatus} />
                    </span>
                </div>
                {props.filterData.maxCountItems > 0 && <FormHelperText  disabled> Допустимое кол-во значений: {props.filterData.maxCountItems}</FormHelperText>}
            </div>
        </MuiPickersUtilsProvider>
    );
}