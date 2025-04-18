import React, { useState } from 'react';
import clsx from 'clsx';

// material-ui
import FilterListIcon from '@material-ui/icons/FilterList';
import { MuiPickersUtilsProvider, KeyboardDateTimePicker } from '@material-ui/pickers';
import DateFnsUtils from '@date-io/date-fns';
import ruLocale from "date-fns/locale/ru";
import format from "date-fns/format";
import Button from '@material-ui/core/Button';
import ButtonGroup from '@material-ui/core/ButtonGroup';
import Badge from '@material-ui/core/Badge';
import CloseIcon from '@material-ui/icons/Close';
import DoneIcon from '@material-ui/icons/Done';
import AccessTimeIcon from '@material-ui/icons/AccessTime';
import Grid from '@material-ui/core/Grid';
import Tooltip from '@material-ui/core/Tooltip/Tooltip';
import Paper from '@material-ui/core/Paper';
import IconButton from '@material-ui/core/IconButton';
// local
import JobStatusSelect from './JobFilters/JobStatusSelect'
import JobUsernameSelect from './JobFilters/JobUsernameSelect'
import {FolderItemTypes} from './FolderItemTypes';
import Slide from '@material-ui/core/Slide';
import {dateCorrection} from '../../../src/utils/dateFunctions'

// dataHub
import dataHub from 'ajax/DataHub';
// styles
import { TimeSlider, FolderContentCSS } from './FolderContentCSS';
import isHollyday from 'HollydayFunctions';

class RuLocalizedUtils extends DateFnsUtils {
    getCalendarHeaderText(date) {
        return format(date, "LLLL yyyy", { locale: ruLocale });
    }
    getDateTimePickerHeaderText(date) {
        return format(date, "dd.MM", { locale: ruLocale });
    }
    getYearText(date) {
        return format(date, "yyyy", { locale: ruLocale });
    }
    getMonthText(date) {
        return format(date, "LLLL", { locale: ruLocale });
    }
}

export default function FilterPanel(props){

    const marks = [
        {
            value: 1,
            label: '1ч'
        },
        {
            value: 2,
            label: '2ч'
        },
        {
            value: 4,
            label: '4ч'
        },
        {
            value: 6,
            label: '6ч'
        },
        {
            value: 8,
            label: '8ч'
        },
        {
            value: 10,
            label: '10ч'
        },
        {
            value: 12,
            label: '12ч'
        },
        {
            value: 14,
            label: '14ч'
        },
        {
            value: 16,
            label: '16ч'
        },
        {
            value: 18,
            label: '18ч'
        },
        {
            value: 20,
            label: '20ч'
        },
        {
            value: 22,
            label: '22ч'
        },
        {
            value: 24,
            label: '24ч'
        }
      ]; 
    const classes = FolderContentCSS();
    const [panelOpen, setPanelOpen] = useState(false);

    let countFilters = Object.entries(props.filters).reduce((acc, [key, value]) => {
        if (value && value !== null && value.length !== 0  && key !=='isCleared') return acc+1
        else return acc
    }, 0);

    function handleClick(isCleared){
        setPanelOpen(false)
        props.onFilterClick(isCleared)
    }

    function TimeSliderComponent(props) {
        return (
          <span {...props}>
            <AccessTimeIcon/>
          </span>
        );
    }

    function handleSelectChange(type, selectArray){
        props.onFilterChange(type, selectArray)
    }
    
    return (
        <div >
        { 
            !panelOpen 
            ?
            <div className={clsx(classes.filterButton, {[classes.filterBtnTop]: isHollyday() === -1, [classes.filterBtnTopHollyday]: isHollyday() >= 0})}>
                <Badge classes={{badge: classes.badge}} color="secondary" overlap="circular" badgeContent={countFilters}>
                    <Tooltip title = "Фильтры" placement="top"> 
                        <Paper elevation={3} className={clsx(classes.openSearchBtn, {[classes.openSearchBtnHeight]: isHollyday() === -1, [classes.openSearchBtnHeightHollyday]: isHollyday() >= 0})}>
                            <IconButton
                                size="small"
                                aria-label="searchBtn"
                                onClick= {()=> setPanelOpen(!panelOpen)}
                            >
                                <FilterListIcon/>
                            </IconButton>
                        </Paper>
                    </Tooltip>
                </Badge>
            </div>
            :
            <Slide direction="down" in={panelOpen} mountOnEnter unmountOnExit>
				<Paper elevation={3} className={classes.drawerStyles}>    
					<Grid container className = {classes.gridFilter}>
						<Grid item className = {classes.divTime}>
							<div className={classes.datesFilter}>
								<MuiPickersUtilsProvider utils={RuLocalizedUtils} locale={ruLocale}>
									<KeyboardDateTimePicker
										size = 'small'
										className={classes.dtmStyle}
										id="datePickerStart"
										view={["date" | "year" | "month" | "hours" | "minutes"]}
										openTo="hours"
										ampm={false}
										disableFuture
										format="dd.MM.yyyy HH:mm"
										margin="normal"
										inputVariant="filled"
										value={dateCorrection(props.filters.periodStart, true)}
										onChange={date => props.onFilterChange('periodStart', dateCorrection(date, false))}
                                        label="Начало периода"
                                        cancelLabel="ОТМЕНИТЬ"
                                        okLabel="СОХРАНИТЬ"
									/>   
									<KeyboardDateTimePicker
										size = 'small'
										className={classes.dtmStyle}
										id="datePickerEnd"
										view={["date" | "year" | "month" | "hours" | "minutes"]}
										openTo="hours"
										ampm={false}
										disableFuture
										format="dd.MM.yyyy HH:mm"
										margin="normal"
										inputVariant="filled"
										value={dateCorrection(props.filters.periodEnd, true)}
										onChange={date => props.onFilterChange('periodEnd', dateCorrection(date, false))}
                                        label="Конец периода"
                                        cancelLabel="ОТМЕНИТЬ"
                                        okLabel="СОХРАНИТЬ"
									/>
                                    </MuiPickersUtilsProvider>
							</div>
							<TimeSlider
								ThumbComponent = {TimeSliderComponent}
								marks = {marks}
								min = {0}
								max = {25}
								step={1}
                                onChange={(event, value) => props.onFilterChange('updatePeriod', value)}
							>
							</TimeSlider>
						</Grid>
						{ props.itemsType === FolderItemTypes.userJobs &&
							<Grid item>
								<JobUsernameSelect 
                                    user={props.filters.user}
                                    label={"Пользователи"}
                                    onDataLoad={dataHub.userController.users}
									onChange={users => handleSelectChange('user', users)}
								/>
							</Grid>
						}
                        <Grid item>
                            <JobUsernameSelect 
                                label={"Отчёты"}
                                user={props.filters.reportIds}
                                onDataLoad={dataHub.reportJobController.getAllReports}
								onChange={reports => handleSelectChange('reportIds', reports)}
							/>
						</Grid>
						<Grid item className = {classes.itemStatusFilter}>
							<JobStatusSelect 
								selectedStatuses={props.filters.selectedStatuses}
								onChange={statuses => props.onFilterChange('selectedStatuses', statuses)}
							/>
						</Grid>
					</Grid>
					<ButtonGroup variant="outlined" orientation="vertical" style = {{marginTop: "4px"}}>
						<Tooltip title="Очистить фильтр"> 
							<Button   
								onClick={() => {handleClick(true)}}
							>
								<CloseIcon  variant="contained" 
									color="primary"
								/>
							</Button>
						</Tooltip>
						<Tooltip title="Применить фильтр"> 
							<Button
								onClick={() => {handleClick(false)}}
							>
								<DoneIcon variant="contained" 
									color="secondary"/>
							</Button>
						</Tooltip>
					</ButtonGroup>
				</Paper>
            </Slide>
        }
        </div>
    );
}