import React, { useRef } from 'react';
import ReactDOM from 'react-dom';

import clsx from 'clsx';
import Measure from 'react-measure';
import { Scrollbars } from 'react-custom-scrollbars';

// magreport
import {AggFunc} from '../../FolderContent/JobFilters/JobStatuses';
import {PivotCSS} from './PivotCSS';

// icons
import Icon from '@mdi/react'
import { mdiArrowLeftCircle, mdiArrowRightCircle, mdiArrowUpCircle, mdiArrowDownCircle } from '@mdi/js';

/**
 * @param {*} props.tableData - данные таблицы
 * @param {*} props.pivotConfiguration - конфигурация сводной
 * @param {*} props.onDimensionValueFilter - колбэк по фильтрации по значению измерения (возникает при нажатии на это значение в таблице)
 * @param {*} props.onmetricValueFilter - колбэк по фильтрации по значению метрики (возникает при нажатии на это значение в таблице)
 * @param {*} props.onChangeInnerTableSize - колбэк по изменению внутреннего размера таблицы
 */
export default function(props){

    const styles = PivotCSS();
    const ScrollbarsRef = useRef(null);


    /*
        Некоторые важные значения из объекта конфигурации для сокращения обозначений
    */
    const metricPlacement = props.pivotConfiguration.columnsMetricPlacement ? "COLUMNS" : "ROWS";

    /*
        *************************************************************************
        Подготовка данных для отображения
        Сначала подготавливается таблица с необъединёнными ячейками
        Потом при необходимости происходит merge ячеек с одинаковыми значениями
        *************************************************************************
    */

    // На сколько умножается высота строк для измерений
    let rowsMetricFactor = metricPlacement === "ROWS" ? Math.max(props.tableData.metrics.length, 1) : 1;

    // На сколько умножается ширина столбцов измерений
    let colsMetricFactor = metricPlacement === "COLUMNS" ? Math.max(props.tableData.metrics.length, 1) : 1;

    /*
        Инфтсрументарий для формирования строк
    */

    function printBlankLeftTopIfNeeded(tableRow, leftCornerCellWidth, leftCornerCellHeight) {
        if(leftCornerCellWidth > 0 && leftCornerCellHeight > 0){
            tableRow.push({
                data : "",
                type : "leftTopCorner",
                colSpan : leftCornerCellWidth,
                rowSpan : leftCornerCellHeight
            });
        }        
    }

    function printColumnDimension(tableRow, dimNum) {
        // Название измерения по столбцам
        tableRow.push({
            data : props.tableData.columnDimensionsFields[dimNum].fieldName,
            type : "dimensionName",
            colSpan : 1,
            rowSpan : 1
        });
        // Значения измерения по столбцам
        for(let j = 0; j < props.tableData.columnDimensionsValues.length; j++){
            tableRow.push({
                data : props.tableData.columnDimensionsValues[j][dimNum],
                fieldId : props.tableData.columnDimensionsFields[dimNum].fieldId,
                original: props.tableData.columnDimensionsFields[dimNum].original,
                type : "dimensionValue",
                colSpan : colsMetricFactor,
                rowSpan : 1
            });
        }
    }

    function printRowDimensionNames(tableRow) {
        // Названия измерений по строкам
        for(let f of props.tableData.rowDimensionsFields){
            tableRow.push({
                data : f.fieldName,
                fieldId : f.fieldId,
                type : "dimensionName",
                colSpan : 1,
                rowSpan : 1
            });
        }
    }

    function printRowDimensionValues(tableRow, rowNum) {
        for(let j = 0; j < props.tableData.rowDimensionsValues[rowNum].length; j++){
            tableRow.push({
                data : props.tableData.rowDimensionsValues[rowNum][j],
                fieldId : props.tableData.rowDimensionsFields[j].fieldId,
                original: props.tableData.rowDimensionsFields[j].original,
                type : "dimensionValue",
                colSpan : 1,
                rowSpan : rowsMetricFactor
            });
        }
    }

    function printMetricNamesRow(tableRow) {
        for(let j = 0; j < props.tableData.columnDimensionsValues.length; j++){
            for(let mIndex in props.tableData.metrics){
                let m = props.tableData.metrics[mIndex];
                tableRow.push({
                    fieldIndex : mIndex,
                    data : m.metricNewName !== '' ? m.metricNewName : AggFunc.get(m.aggregationType) + " " + m.metricName,
                    type : "metricName",
                    colSpan : 1,
                    rowSpan : 1
                });
            }
        }        
    }

    function printMetricInRow(tableRow, metricNum, rowNum) {
        tableRow.push({
            fieldIndex : metricNum,
            data : props.tableData.metrics[metricNum].metricNewName !=='' ? props.tableData.metrics[metricNum].metricNewName : AggFunc.get(props.tableData.metrics[metricNum].aggregationType) + " " + props.tableData.metrics[metricNum].metricName,
            type : "metricName",
            style: props.pivotConfiguration.fieldsLists.metricFields[metricNum]?.formatting || '',
            colSpan : 1,
            rowSpan : 1
        });
        for(let j = 0; j < props.tableData.columnDimensionsValues.length; j++){
            tableRow.push({
                fieldId : props.pivotConfiguration.fieldsLists.metricFields[metricNum]?.fieldId,
                index: metricNum,
                data : props.pivotConfiguration.fieldsLists.metricFields[metricNum]?.changeData(props.tableData.metrics[metricNum].values[j][rowNum]) || props.tableData.metrics[metricNum].values[j][rowNum],
                type : "metricValues",
                style: props.pivotConfiguration.fieldsLists.metricFields[metricNum]?.formatting || '',
                conditionalFormatting: props.pivotConfiguration.fieldsLists.metricFields[metricNum]?.conditionalFormatting,
                colSpan : 1,
                rowSpan : 1,
                rowNum,
                rowName: props.tableData.rowDimensionsValues[rowNum],
                columnName: props.tableData.columnDimensionsValues[j],
                indexInRow: j,
                metricName: AggFunc.get(props.tableData.metrics[metricNum].aggregationType) + " " + props.tableData.metrics[metricNum].metricName,
                aggFuncName: props.tableData.metrics[metricNum].aggregationType,
                dataType: props.tableData.metrics[metricNum].dataType
            });
        }
    }

    function printAllMetricInColumnsValues(tableRow, rowNum) {
        for(let j = 0; j < props.tableData.columnDimensionsValues.length; j++){
            for(let m = 0; m < props.tableData.metrics.length; m++){
                
                tableRow.push({
                    fieldId:  props.tableData.metrics[m].fieldId,
                    index: m,
                    data : props.pivotConfiguration.fieldsLists.metricFields[m]?.changeData(props.tableData.metrics[m].values[j][rowNum]) || props.tableData.metrics[m].values[j][rowNum],
                    type : "metricValues",
                    style: props.pivotConfiguration.fieldsLists.metricFields[m]?.formatting,
                    conditionalFormatting: props.pivotConfiguration.fieldsLists.metricFields[m]?.conditionalFormatting,
                    colSpan : 1,
                    rowSpan : 1,
                    rowNum,
                    rowName: props.tableData.rowDimensionsValues[rowNum],
                    columnName: props.tableData.columnDimensionsValues[j],
                    indexInRow: j,
                    metricName: AggFunc.get(props.tableData.metrics[m].aggregationType) + " " + props.tableData.metrics[m].metricName,
                    aggFuncName: props.tableData.metrics[m].aggregationType,
                    dataType: props.tableData.metrics[m].dataType
                });
            }
        }
    }

    /*
        Формируем строки
    */

    // Количество строк в заголовочной части таблицы (атрибуты по столбцам и строка названий метрик)
    let nHeaderRows = 0;

    // Размеры пустой ячейки левого верхнего угла
    let leftCornerCellWidth = 0;
    let leftCornerCellHeight = 0;

    // Массив строк таблицы
    let tableRows = [];
    let rowNum = -1;

    if(props.tableData.columnDimensionsFields.length > 0 && props.tableData.rowDimensionsFields.length > 0){
        if(metricPlacement === "ROWS"){
            nHeaderRows = props.tableData.columnDimensionsFields.length;
            leftCornerCellWidth = props.tableData.rowDimensionsFields.length;
            leftCornerCellHeight = props.tableData.columnDimensionsFields.length - 1;

            // Строки с измерениями по столбцам кроме последней
            for(let i = 0; i < props.tableData.columnDimensionsFields.length - 1; i++){
                tableRows.push([]);
                rowNum++;
                if(i === 0){
                    printBlankLeftTopIfNeeded(tableRows[rowNum], leftCornerCellWidth, leftCornerCellHeight);
                }
                printColumnDimension(tableRows[rowNum], i);
            }

            // Последняя строка измерений по столбцам содержит в начале названия измерений по строкам
            tableRows.push([]);
            rowNum++;
            printRowDimensionNames(tableRows[rowNum]);
            printColumnDimension(tableRows[rowNum], rowNum);
            
            // Далее строки со значениями измерений по столбцам в начале, затем название метрики и затем значения данной метрики
            for(let i = 0; i < props.tableData.rowDimensionsValues.length; i++){
                tableRows.push([]);
                rowNum++;
                printRowDimensionValues(tableRows[rowNum], i);
                for(let m = 0; m < props.tableData.metrics.length; m++){
                    if(m > 0){
                        tableRows.push([]);
                        rowNum++;
                    }
                    printMetricInRow(tableRows[rowNum], m, i);
                }
            }
        }
        else{
            nHeaderRows = props.tableData.columnDimensionsFields.length + 1;
            leftCornerCellWidth = props.tableData.rowDimensionsFields.length - 1;
            leftCornerCellHeight = props.tableData.columnDimensionsFields.length;

            let rowNum = -1;

            // Строки с измерениями по столбцам
            for(let i = 0; i < props.tableData.columnDimensionsFields.length; i++){
                tableRows.push([]);
                rowNum++;
                if(i === 0){
                    printBlankLeftTopIfNeeded(tableRows[rowNum], leftCornerCellWidth, leftCornerCellHeight);
                }
                printColumnDimension(tableRows[rowNum], i);
            }

            // Строка с названиями измерений по строкам и с названиями метрик
            tableRows.push([]);
            rowNum++;
            printRowDimensionNames(tableRows[rowNum]);
            printMetricNamesRow(tableRows[rowNum]);

            // Далее строки со значениями измерений по столбцам в начале, затем метрики по столбцам
            for(let i = 0; i < props.tableData.rowDimensionsValues.length; i++){
                tableRows.push([]);
                rowNum++;
                printRowDimensionValues(tableRows[rowNum], i);
                printAllMetricInColumnsValues(tableRows[rowNum], i);
            }
        }
    }
    else if(props.tableData.columnDimensionsFields.length > 0){
        // Только измерения по столбцам
        if(metricPlacement === "ROWS"){
            nHeaderRows = props.tableData.columnDimensionsFields.length;
            // Строки с измерениями по столбцам
            for(let i = 0; i < props.tableData.columnDimensionsFields.length; i++){
                tableRows.push([]);
                rowNum++;
                printColumnDimension(tableRows[rowNum], i);
            }
            for(let m = 0; m < props.tableData.metrics.length; m++){
                tableRows.push([]);
                rowNum++;
                printMetricInRow(tableRows[rowNum], m, 0);
            }
        }
        else{
            nHeaderRows = props.tableData.columnDimensionsFields.length + 1;
            // Строки с измерениями по столбцам
            for(let i = 0; i < props.tableData.columnDimensionsFields.length; i++){
                tableRows.push([]);
                rowNum++;
                printColumnDimension(tableRows[rowNum], i);
            }
            if(props.tableData.metrics.length > 0)
            {
                // Строка с названиями метрик
                tableRows.push([]);
                rowNum++;
                printBlankLeftTopIfNeeded(tableRows[rowNum], 1, 2);            
                printMetricNamesRow(tableRows[rowNum]);
                tableRows.push([]);
                rowNum++;
                printAllMetricInColumnsValues(tableRows[rowNum], 0);     
            }
        }
    }
    else if(props.tableData.rowDimensionsFields.length > 0){
        // Только измерения по строкам
        if(metricPlacement === "ROWS"){
            nHeaderRows = 1;
            // Строка с названиями измерений по строкам и с названиями метрик
            tableRows.push([]);
            rowNum++;
            printRowDimensionNames(tableRows[rowNum]);
            // Далее строки со значениями измерений по столбцам в начале, затем название метрики и затем значения данной метрики
            for(let i = 0; i < props.tableData.rowDimensionsValues.length; i++){
                tableRows.push([]);
                rowNum++;
                printRowDimensionValues(tableRows[rowNum], i);
                for(let m = 0; m < props.tableData.metrics.length; m++){
                    if(m > 0){
                        tableRows.push([]);
                        rowNum++;
                    }
                    printMetricInRow(tableRows[rowNum], m, i);
                }
            }
        }
        else{
            nHeaderRows = 1;
            tableRows.push([]);
            rowNum++;
            printRowDimensionNames(tableRows[rowNum]);
            printMetricNamesRow(tableRows[rowNum]);

            // Далее строки со значениями измерений по столбцам в начале, затем метрики по столбцам
            for(let i = 0; i < props.tableData.rowDimensionsValues.length; i++){
                tableRows.push([]);
                rowNum++;
                printRowDimensionValues(tableRows[rowNum], i);
                printAllMetricInColumnsValues(tableRows[rowNum], i);
            }
        }
    }
    else{
        // Только метрики
        if(props.tableData.metrics.length > 0){
            if(metricPlacement === "ROWS"){
                nHeaderRows = 0;
                for(let m = 0; m < props.tableData.metrics.length; m++){
                    tableRows.push([]);
                    rowNum++;
                    printMetricInRow(tableRows[rowNum], m, 0);
                }
            }
            else{
                nHeaderRows = 1;
                tableRows.push([]);
                rowNum++;
                printRowDimensionNames(tableRows[rowNum]);
                printMetricNamesRow(tableRows[rowNum]);

                // Далее - значения метрик
                tableRows.push([]);
                rowNum++;
                printAllMetricInColumnsValues(tableRows[rowNum], 0);
            }
        }
    }     

    if(props.pivotConfiguration.mergeMode){
        mergeCells(tableRows, props.tableData.columnDimensionsFields.length, nHeaderRows, colsMetricFactor, rowsMetricFactor);
    }

    /*
    ******************
        Обработчики
    ******************
    */
    function handleDimensionValueCellClick(fieldId, original, fieldValue) {
        props.onDimensionValueFilter(fieldId, original ? 'REPORT_FIELD' : 'DERIVED_FIELD', fieldValue);
    }

    function handleMetricValueCellClick(fieldId, index, fieldValue) {
        props.onMetricValueFilter(fieldId, index, fieldValue);
    }

    function handleContextClick(event, type, cell){
        props.onContextMenu(event, type, cell);
    }

    function handleClickOnAddSortingArrow(position, order, {cell}) {
        const fullMetricNameWithRowNames = cell.rowName.length > 0 ? `${cell.rowName.join(' - ')} - ${cell.metricName}` : cell.metricName
        const fullMetricNameWithColumnNames = cell.columnName.length > 0 ? `${cell.columnName.join(' - ')} - ${cell.metricName}` : cell.metricName

        if (position === 'row') {
            const checkColumnSort = props.sortingValues.columnSort ? {...props.sortingValues.columnSort} : {
                name: fullMetricNameWithColumnNames,
                columnName: cell.columnName,
                data: [
                    {
                        "metricId": cell.index,
                        "order": '',
                        "tuple": cell.columnName
                    }
                ] 
            }

            props.onAddSorting({
                rowSort: {
                    name: fullMetricNameWithRowNames,
                    rowName: cell.rowName,
                    data: [
                        {
                            "metricId": cell.index,
                            "order": order,
                            "tuple": cell.rowName
                        }
                    ] 
                },
                columnSort: checkColumnSort
            })
        } else {
            const checkRowSort = props.sortingValues.rowSort ? {...props.sortingValues.rowSort} : {
                name: fullMetricNameWithRowNames,
                rowName: cell.rowName,
                data: [
                    {
                        "metricId": cell.index,
                        "order": '',
                        "tuple": cell.rowName
                    }
                ] 
            }

            props.onAddSorting({
                rowSort: checkRowSort,
                columnSort: {
                    name: fullMetricNameWithColumnNames,
                    columnName: cell.columnName,
                    data: [
                        {
                            "metricId": cell.index,
                            "order": order,
                            "tuple": cell.columnName
                        }
                    ] 
                }
            })
        }
    }

    function handleClickOnRemoveSortingArrow(position) {

        if (position === 'row') {
            const changeRowSortOrder = {...props.sortingValues.rowSort, data: [{...props.sortingValues.rowSort.data[0], order: ''}]}

            props.onAddSorting({
                rowSort: changeRowSortOrder,
                columnSort: {...props.sortingValues.columnSort}
            })
        } else {
            const changeColumnSortOrder = {...props.sortingValues.columnSort, data: [{...props.sortingValues.columnSort.data[0], order: ''}]}

            props.onAddSorting({
                rowSort: {...props.sortingValues.rowSort},
                columnSort: changeColumnSortOrder
            })
        }
    }

    function handleShowArrows(id, cell) {

        let rowSortValue = null
        let columnSortValue = null

        const {rowSort, columnSort} = props.sortingValues

        if (rowSort && (JSON.stringify(rowSort.rowName) === JSON.stringify(cell.rowName)) && (rowSort.data[0].metricId === cell.index)) {
            if (rowSort.data[0].order === 'Ascending') {
                rowSortValue = 'rowAscending'
            } else if (rowSort.data[0].order === 'Descending') {
                rowSortValue = 'rowDescending'
            }
        } 

        if (columnSort && (JSON.stringify(columnSort.columnName) === JSON.stringify(cell.columnName)) && (columnSort.data[0].metricId === cell.index)) {
            if (columnSort.data[0].order === 'Ascending') {
                columnSortValue = 'columnAscending'
            } else if(columnSort.data[0].order === 'Descending'){
                columnSortValue = 'columnDescending'
            }
        }

        let layout =                                                   
            <div className={'metricValueCellArrowsWrapp'}>
            { rowSortValue !== 'rowAscending' && <Icon path={mdiArrowRightCircle} size={0.7} key='rowAscending' className={styles.metricValueCellArrow} onClick={() => handleClickOnAddSortingArrow('row', 'Ascending', {cell})}/>}
            { rowSortValue !== 'rowDescending' && <Icon path={mdiArrowLeftCircle} size={0.7} key='rowDescending' className={styles.metricValueCellArrow} onClick={() => handleClickOnAddSortingArrow('row', 'Descending', {cell})}/>}
            { columnSortValue !== 'columnAscending' && <Icon path={mdiArrowDownCircle} size={0.7} key='columnAscending' className={styles.metricValueCellArrow} onClick={() => handleClickOnAddSortingArrow('column', 'Ascending', {cell})}/>}
            { columnSortValue !== 'columnDescending' && <Icon path={mdiArrowUpCircle} size={0.7} key='columnDescending' className={styles.metricValueCellArrow} onClick={() => handleClickOnAddSortingArrow('column', 'Descending', {cell})}/>}
            </div>    

        ReactDOM.render(layout, document.getElementById(`${id}`))
    }

    /*
    ******************
        Стили, компоненты
    ******************
    */

    const IconArrows = ({cell}) => {

        let arr = []

        const {rowSort, columnSort} = props.sortingValues

        if (rowSort && (JSON.stringify(rowSort.rowName) === JSON.stringify(cell.rowName)) && (rowSort.data[0].metricId === cell.index)) {
            if (rowSort.data[0].order === 'Ascending') {
                arr.push(
                    <Icon path={mdiArrowRightCircle} size={0.7} color="green" key={Math.random()}  onClick={() => handleClickOnRemoveSortingArrow('row')}/>
                )
            } else if (rowSort.data[0].order === 'Descending') {
                arr.push(
                    <Icon path={mdiArrowLeftCircle} size={0.7} color="green" key={Math.random()} onClick={() => handleClickOnRemoveSortingArrow('row')}/>
                )
            }
        } 

        if (columnSort && (JSON.stringify(columnSort.columnName) === JSON.stringify(cell.columnName)) && (columnSort.data[0].metricId === cell.index)) {
            if (columnSort.data[0].order === 'Ascending') {
                arr.push(
                    <Icon path={mdiArrowDownCircle} size={0.7} color="green" key={Math.random()} onClick={() => handleClickOnRemoveSortingArrow('column')}/>
                )
            } else if(columnSort.data[0].order === 'Descending'){
                arr.push(
                    <Icon path={mdiArrowUpCircle} size={0.7} color="green" key={Math.random()} onClick={() => handleClickOnRemoveSortingArrow('column')}/>
                )
            }
        }

        let layout = Object.keys(props.sortingValues).length !== 0 && arr  

        
        return layout 
    }

    const conditionalFormatting = (cell) => {

        if (cell.type === "metricValues" && (cell.conditionalFormatting && cell.conditionalFormatting.length > 0)) {
            if (cell.conditionalFormatting.length === 1) {
                return {backgroundColor: cell.conditionalFormatting[0].color}
            } 
            
            const cellData = Number(cell.data.replace(/\s/g,'').replace('%', ''))

            for (let i = 0; i < cell.conditionalFormatting.length; i++) {
                if (cellData < Number(cell.conditionalFormatting[i].valueTo)) {
                    return { backgroundColor: cell.conditionalFormatting[i].color}
                }
            }
        }
        
        return {}
    }

    const cellDataStyle = (cell) => {

        let styleObj = {
            margin: '2px', fontSize: '14px', fontFamily: 'Arial', fontWeight: cell.type === "dimensionName" || cell.type === "metricName" ? "bold" : "medium"
        }

        if (cell.type === "metricValues" && cell.fieldId) {

            if (cell.conditionalFormatting && cell.conditionalFormatting.length > 0) {
                
                if (cell.conditionalFormatting.length === 1) {
                    styleObj = {   
                        margin: '2px',
                        height: cell.conditionalFormatting[0].fontSize ? `${cell.conditionalFormatting[0].fontSize + 5}px` : 'auto',
                        fontWeight: cell.conditionalFormatting[0].fontStyle === 'bold' ? 'bold' : '400',
                        fontStyle: cell.conditionalFormatting[0].fontStyle === 'italic' ? 'italic' : 'inherit',
                        textDecoration: cell.conditionalFormatting[0].fontStyle === 'underline' ? 'underline' : 'none',
                        fontSize: cell.conditionalFormatting[0].fontSize + 'px',
                        color: cell.conditionalFormatting[0].fontColor,
                        whiteSpace: 'nowrap'
                    }
                } else {
                    const cellData = Number(cell.data.replace(/\s/g,'').replace('%', ''))
    
                    for (let i = 0; i < cell.conditionalFormatting.length; i++) {
                        if (cellData < Number(cell.conditionalFormatting[i].valueTo)) {
                            styleObj = {   
                                margin: '2px',
                                height: cell.conditionalFormatting[i].fontSize ? `${cell.conditionalFormatting[i].fontSize + 5}px` : 'auto',
                                fontWeight: cell.conditionalFormatting[i].fontStyle === 'bold' ? 'bold' : '400',
                                fontStyle: cell.conditionalFormatting[i].fontStyle === 'italic' ? 'italic' : 'inherit',
                                textDecoration: cell.conditionalFormatting[i].fontStyle === 'underline' ? 'underline' : 'none',
                                fontSize: cell.conditionalFormatting[i].fontSize + 'px',
                                color: cell.conditionalFormatting[i].fontColor,
                                whiteSpace: 'nowrap'
                            }
                        }
                    }
                }
                
                
            } else if(cell.hasOwnProperty('style') && cell.style) {
               cell.style.filter((styleObj) => (styleObj.aggFuncName === cell.aggFuncName)).map((formatting) => {
                    styleObj = {   
                        margin: '2px',
                        height: formatting.fontSize ? `${formatting.fontSize + 5}px` : 'auto',
                        fontWeight: formatting.fontStyle === 'bold' ? 'bold' : '400',
                        fontStyle: formatting.fontStyle === 'italic' ? 'italic' : 'inherit',
                        textDecoration: formatting.fontStyle === 'underline' ? 'underline' : 'none',
                        fontSize: formatting.fontSize + 'px',
                        color: formatting.fontColor,
                        whiteSpace: 'nowrap'
                    }
                })
            }

        }

        return styleObj
    }

    return(
        <div className={clsx(styles.pivotTable)}>
             <Scrollbars 
                ref={ScrollbarsRef}
            >
                {/*Без Measure скролл не реагирует на изменение размера других компонентов*/}
                
                <table style={{borderCollapse: 'collapse'}} className={styles.table}>
                    <Measure
						bounds
						onResize={contentRect => {
							props.onChangeInnerTableSize({ dimensions: contentRect.bounds });
						}}
					>
					{({ measureRef }) => {
                        let lastDimIndex = props.pivotConfiguration.rowFrom%2 === 0 ? true: false;
						return (
						<tbody  ref={measureRef} >
                            {tableRows.map((r, ind) => {
                                lastDimIndex = r[0] ? (r[0].type === "dimensionValue" 
                                            || (r[0].type === "metricName" && props.pivotConfiguration.fieldsLists.rowFields.length === 0) ? !lastDimIndex: lastDimIndex) : lastDimIndex;
                                return (
                                <tr key = {ind}>
                                    {r.filter((cell) => (cell.colSpan > 0 && cell.rowSpan > 0)).map( (cell, i) => {
                                        const id = Math.random()
                                        return (
                                            <td key = {i} colSpan = {cell.colSpan} rowSpan = {cell.rowSpan}
                                                onContextMenu={ (event) => { handleContextClick(event, cell.type, cell) } }
                                                className = {clsx({
                                                    [styles.cell] : true,
                                                    [styles.tr1] : lastDimIndex  && (cell.type === "metricValues" /*|| (cell.type === "dimensionValue" && cell.rowSpan === 1)*/),
                                                    [styles.tr2] : !lastDimIndex  && (cell.type === "metricValues" /*|| (cell.type === "dimensionValue" && cell.rowSpan === 1)*/),
                                                    [styles.tr3] : cell.type === "dimensionValue",
                                                    [styles.leftTopCell] : cell.type === "leftTopCorner",
                                                    [styles.blanc] : cell.type === "blanc",
                                                    [styles.dimensionNameCell] : cell.type === "dimensionName",
                                                    [styles.dimensionValueCell] : cell.type === "dimensionValue",
                                                    [styles.metricNameCell] : cell.type === "metricName",
                                                    [styles.metricValueCell] : cell.type === "metricValues",
                                                })}
                                                style={conditionalFormatting(cell)}
                                                onMouseEnter={cell.type === "metricValues" ? () => handleShowArrows(id, cell) : () => {}}
                                            >
                                                { cell.type === 'metricValues' &&
                                                    <div style={{ display: 'flex', justifyContent: 'end'}}> 
                                                        <div id={id}></div>
                                                        <div>
                                                            <IconArrows cell={cell}/>
                                                        </div>
                                                    </div>
                                                }
                                                <div 
                                                    onClick = {cell.type === "dimensionValue" ? () => {handleDimensionValueCellClick(cell.fieldId, cell.data)}
                                                    : cell.type === "metricValues"  ? () => {handleMetricValueCellClick(cell.fieldId, cell.index, cell.data)}
                                                    : () => {}}
                                                >
                                                    <div style = { cellDataStyle(cell) }>
                                                        {cell.data}
                                                    </div>
                                                </div>
                                            </td>
									    )
                                    })}
								</tr> )
							})}
						</tbody>
						)}
					}
				    </Measure>
      
                </table>
            </Scrollbars>
        </div>
        
    )
}

function mergeCells(tableRows, columnDimensionsNum, nHeaderRows, colsMetricFactor, rowsMetricFactor){

    // merge columns
    let colSpan = 1;
    for(let i = 0; i < columnDimensionsNum - 1; i++){
        for(let j = tableRows[i].length-1; j >= 0 ; j--){
            if(tableRows[i][j].type === "dimensionValue"){
                let jUp = j;
                if(i === 1 && tableRows[0][0].type === "leftTopCorner" && tableRows[0][0].rowSpan > 1){
                    jUp = j + 1;
                }
                if( (j > 1 || ( i === 0 && tableRows[0][0].type === "leftTopCorner" && j > 2) ) && 
                    tableRows[i][j].data === tableRows[i][j-1].data &&
                    (i === 0 || tableRows[i - 1][jUp].colSpan === 0)){
                    tableRows[i][j].colSpan = 0;
                    colSpan++;
                }
                else{
                    tableRows[i][j].colSpan = colSpan * colsMetricFactor;
                    colSpan = 1;
                }
            }
        }
    }

    // merge rows
    let rowSpan = new Array(); // eslint-disable-line
    for(let i = tableRows.length - rowsMetricFactor; i >= nHeaderRows; i -= rowsMetricFactor){
        for(let j = 0; j < tableRows[i].length; j++){
            if(tableRows[i][j].type === "dimensionValue"){
                if(j >= rowSpan.length){
                    rowSpan.push(1);
                }
                if( i > nHeaderRows &&
                    tableRows[i][j].data === tableRows[i - rowsMetricFactor][j].data &&
                    (j === 0 || tableRows[i][j-1].rowSpan === 0)){
    
                    rowSpan[j]++;
                    tableRows[i][j].rowSpan = 0;
                }
                else{
                    tableRows[i][j].rowSpan = rowSpan[j] * rowsMetricFactor;
                    rowSpan[j] = 1;
                }  
            }
        }
    }
}
