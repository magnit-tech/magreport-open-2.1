import React, {useState, useRef} from 'react';
import {useSnackbar} from "notistack";
import Grid from '@material-ui/core/Grid';
import List from '@material-ui/core/List';
import Card from '@material-ui/core/Card';
import CardHeader from '@material-ui/core/CardHeader';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import Checkbox from '@material-ui/core/Checkbox';
import Button from '@material-ui/core/Button';
import Divider from '@material-ui/core/Divider';
import Pagination from '@material-ui/lab/Pagination';
import FormControl from '@material-ui/core/FormControl';
import Select from '@material-ui/core/Select';
import MenuItem from '@material-ui/core/MenuItem';
import { CircularProgress } from '@material-ui/core';
import SearchIcon from '@material-ui/icons/Search';
import InputBase from '@material-ui/core/InputBase';


//local
import DataLoader from 'main/DataLoader/DataLoader';
import dataHub from 'ajax/DataHub';
import {PivotCSS} from './PivotCSS';

function not(a, b) {
    return a.filter((value) => b.indexOf(value) === -1);
}

function intersection(a, b) {
    return a.filter((value) => b.indexOf(value) !== -1);
}

function union(a, b) {
    return [...a, ...not(b, a)];
}

export default function TransferList(props) {
    const {enqueueSnackbar} = useSnackbar();
    const classes = PivotCSS();

    const [checked, setChecked] = useState([]);
    const [left, setLeft] = useState([]);
    const [right, setRight] = useState(props.filterValues);

    const rowsPerPage = [10, 100, 200, 500, 1000];
    const [countValues, setCountValues] = useState(1);

    const leftChecked = intersection(checked, left);
    const rightChecked = intersection(checked, right);

    const timer = useRef(0);
    const [page, setPage] = useState(1);
    const [startRow, setStartRow] = useState(0);
    const [rowsNum, setRowsNum] = useState(10);
    const [params, setParams] = useState(buildParams(null, 0));
    const [uploading, setUploading] = useState(true);

    function handleFieldValuesLoaded(data){
        setCountValues(data.countValues) ; // - right.length);
        setLeft(not(data.valueList,  right));
        setUploading(false)
    }

    const handleToggle = (value) => () => {
        const currentIndex = checked.indexOf(value);
        const newChecked = [...checked];

        if (currentIndex === -1) {
            newChecked.push(value);
        } else {
            newChecked.splice(currentIndex, 1);
        }

        setChecked(newChecked);
    };

    const numberOfChecked = (items) => intersection(checked, items).length;

    const handleToggleAll = (items) => () => {
        if (numberOfChecked(items) === items.length) {
            setChecked(not(checked, items));
        } else {
            setChecked(union(checked, items));
        }
    };

    const handleCheckedRight = () => {
        let toRight = checked.filter(i=>!right.find(item=>item === i));
        let arr = right.concat(toRight);
        props.onChange(arr);
        setRight(arr);
        setLeft(not(left, leftChecked));
       // setChecked(not(checked, leftChecked));
    };

    const handleCheckedLeft = () => {
        let arr = not(right, rightChecked);
        props.onChange(arr);
        setLeft(left.concat(rightChecked));
        setRight(arr);
        setChecked(not(checked, rightChecked));
    };

    function buildParams(searchString, startRow) {
        let chGrs = [];
        let chFltr = [];
       // Раскомментировать при необходимости фильтрации по метрикам
       // let metricChGrs = [];
       // let metricFltr = [];

        if (searchString) {
            chFltr.push(searchString)
        }

        if (props.filterGroup?.childGroups.length>0 || props.filterGroup?.filters.length>0) {
            chGrs.push(props.filterGroup);
        }
/*
        if (props.metricFilterGroup?.childGroups.length>0 || props.metricFilterGroup?.filters.length>0) {
            metricChGrs.push(props.metricFilterGroup)
        }
*/
        return (
        {
            jobId: props.jobId,
            fieldId: props.field?.fieldId,
            //columnFields : props.fieldsLists.columnFields.map( (v) => (v.fieldId)),
            //rowFields : props.fieldsLists.rowFields.map( (v) => (v.fieldId)),
            //metrics : props.fieldsLists.metricFields.map( (v) => ({fieldId: v.fieldId, aggregationType : v.aggFuncName}) ),
            filterGroup: {
                operationType: 'AND',
                invertResult: false,
                childGroups: chGrs,
                filters: chFltr
            },
        /*    metricFilterGroup: {
                operationType: 'AND',
                invertResult: false,
                childGroups: metricChGrs,
                filters: metricFltr
            }, */
            from: startRow,
            count: rowsNum 
        })
    }

    function handleChangePage(value){
        let sr = (value - 1) * rowsNum;
        setPage(value);
        setStartRow(sr);
        setParams({...params, from: sr});
    }

    function handleRowsNumChange(value){
        let sr = 0;
        if (startRow !== 0)
        {
            sr = (Math.ceil(startRow/value) -1)  * value;          
            setPage(Math.ceil((sr+value)/value));
        }
        setRowsNum(value);
        setParams({...params, count: value, from: sr});
    }
    function handleSearch(value){
        if (timer.current > 0){
            clearInterval(timer.current); // удаляем предыдущий таймер
        }

        timer.current = setTimeout(() => {        
            setParams(buildParams({ 
                    fieldId: props.field?.fieldId,
                    filterType: 'CONTAINS_CI',
                    invertResult: false,
                    values: [value]
                
            }, 0)); 
        }, 1000);
    }

    const listHeader = (title, items, isSearch) => (
        <CardHeader
            classes={{root: classes.cardHeaderTrList, action:  classes.cardHeaderTrList}}
            titleTypographyProps ={{color: classes.verticalList.borderColor}}
            avatar={
                <Checkbox
                    onClick={handleToggleAll(items)}
                    checked={numberOfChecked(items) === items.length && items.length !== 0}
                    indeterminate={numberOfChecked(items) !== items.length && numberOfChecked(items) !== 0}
                    disabled={items.length === 0}
                    inputProps={{ 'aria-label': 'all items selected' }}
                />
            }
            title={title}
            subheader={`${numberOfChecked(items)}/${items.length} отмечено`}
            action={ isSearch ?
                <div className={classes.SJ_search}>
                    <div className={classes.SJ_searchIcon}>
                        <SearchIcon />
                    </div>
                    <InputBase
                        placeholder="Поиск…"
                        classes={{
                            root: classes.SJ_inputRoot,
                            input: classes.SJ_inputInput,
                        }}
                        onChange={(event) => handleSearch(event.target.value)}
                    />
                </div> :
                null
            }
        />

    );

    const customList = (items) => (
        <List  className={classes.listTrList} 
            dense component="div" role="list"
        >
            {items.map((value) => {
                const labelId = `transfer-list-all-item-${value}-label`;
                return (
                    <ListItem key={value} role="listitem" button onClick={handleToggle(value)}>
                        <ListItemIcon>
                            <Checkbox
                                checked={checked.indexOf(value) !== -1}
                                tabIndex={-1}
                                disableRipple
                                inputProps={{'aria-labelledby': labelId}}
                            />
                        </ListItemIcon>
                        <ListItemText id={labelId} primary={value} />
                    </ListItem>
                );
            })}   
        </List>
    );

    const listPagination = () => (
        <div className={classes.divPag}>
            <FormControl className={classes.formControlTrList} size="small">
                <Select
                    defaultValue = {rowsNum}
                    children={ rowsPerPage.map((value, index) => <MenuItem key={index} value={value}>{value}</MenuItem>) }
                    onChange={(event) =>handleRowsNumChange(event.target.value)}
                    inputProps={{
                        name: 'Строк на странице',
                        id: 'row-per-page',
                    }}
                />
            </FormControl>
            <Pagination 
                count={Math.ceil(((countValues  )/ rowsNum) )} 
                page={page} 
                showFirstButton 
                showLastButton
                onChange={(event, value) => handleChangePage(value)}
            />
        </div>
    );

    return (
            <Grid container
                spacing={2}
                justifycontent="center"
                alignItems="center"
                className={classes.gridRoot}
            >
            {/*    <Grid item xs={12} style={{display: 'flex'}}>
                    <TextField
                        style={{width: '292px'}}
                        label="Поиск" 
                        onChange={(event) => handleSearch(event.target.value)}> 
                    </TextField>
                </Grid>
            */}
                <Grid item xs className={classes.gridColumn}>
                    <Card className={classes.transferHeader}>
                        <div> 
						{listHeader('Выбрать', left, true)}
                        
                        </div>

						<Divider />
						<DataLoader
							loadFunc = {dataHub.olapController.getFieldValues}
							loadParams = {[params]}
							showSpinner
							onDataLoaded = {handleFieldValuesLoaded}
							onDataLoadFailed = {message => {enqueueSnackbar(`Не удалось получить значения поля: ${message}`, {variant : "error"}); setUploading(false)}}
						>
							{
								uploading ?
								<div className={classes.divProgress}><CircularProgress/></div> :
								<div className={classes.listRel}> 
									{customList(left)}
								</div>
							}
						</DataLoader> 
						{listPagination()}
                    </Card> 
                </Grid>
            
                <Grid item xs={1} className={classes.gridBtnColumn}>
                    <Grid container direction="column" alignItems="center">
                        <Button
                            variant="outlined"
                            size="small"
                            className={classes.buttonTrList}
                            onClick={handleCheckedRight}
                            disabled={leftChecked.length === 0}
                            aria-label="move selected right"
                        >
                            &gt;
                        </Button>
                        <Button
                            variant="outlined"
                            size="small"
                            className={classes.buttonTrList}
                            onClick={handleCheckedLeft}
                            disabled={rightChecked.length === 0}
                            aria-label="move selected left"
                        >
                            &lt;
                        </Button>
                    </Grid>
                </Grid>
                <Grid item xs className={classes.gridColumn}>
                    <Card className={classes.transferHeader}>
                        {listHeader('Выбрано', right)}
                        <Divider />
                        <div className={classes.listRel}>
                            {customList(right)}
                        </div>
                    </Card>
                </Grid>
            </Grid>
    );
}