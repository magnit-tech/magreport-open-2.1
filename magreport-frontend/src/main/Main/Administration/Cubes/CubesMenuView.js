import React, {useState} from 'react';
import { connect } from 'react-redux';
import Tabs from '@material-ui/core/Tabs';
import Tab from '@material-ui/core/Tab';    
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import { MuiPickersUtilsProvider, KeyboardDateTimePicker } from '@material-ui/pickers';
import DateFnsUtils from '@date-io/date-fns';
import ruLocale from "date-fns/locale/ru";
import format from "date-fns/format";
// dataHub
import dataHub from 'ajax/DataHub';

// redux
//import {FLOW_STATE_BROWSE_FOLDER, usersJobsMenuViewFlowStates} from 'redux/reducers/menuViews/flowStates';
import {actionFolderLoaded, actionFolderLoadFailed, actionItemClick} from 'redux/actions/menuViews/folderActions';
import {actionFilterJobs, actionJobCancel, showSqlDialog} from 'redux/actions/jobs/actionJobs';
import actionSetSidebarItem from 'redux/actions/sidebar/actionSetSidebarItem';
import {startReport} from 'redux/actions/menuViews/reportActions';

// components
import DataLoader from 'main/DataLoader/DataLoader';
import FolderContent from 'main/FolderContent/FolderContent';
import DesignerTabPanel from 'main/Main/Development/Designer/DesignerTabPanel';

import SidebarItems from '../../Sidebar/SidebarItems';

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

function CubesMenuView(props){

    let now = new Date();
    let from = new Date();
    from.setHours(0,0,0,0);

    const [reloadActive, setReloadActive] = useState({needReload : false});
    const [reloadStats, setReloadStats]   = useState({needReload : false});
    //const [statsDTM, setStatsDTM] = useState({startDTM: from.toISOString(), endDTM: now.toISOString()});
    const [statsStartDTM, setStatsStartDTM] = useState(from.toISOString());
    const [statsEndDTM, setStatsEndDTM] = useState(now.toISOString());

    let folderItemsType = SidebarItems.admin.subItems.cubes.folderItemType;

    function handleRefreshFolderActive(){
        setReloadActive({needReload : true});
        setReloadStats({needReload : false});
    }

    function handleRefreshFolderStats()
    {
        setReloadStats({needReload : true});
        setReloadActive({needReload : false});
    }

    function handleChangeDTM(source, date){
        if (source === 'from'){  
            setStatsStartDTM(date.toISOString())
        } else {
            setStatsEndDTM(date.toISOString())
        }
    }

    // Переключение главных табов
    const [mainTabsValue, setMainTabsValue] = useState(0);

    return (
        <div  style={{display: 'flex', flex: 1, flexDirection: 'column'}}>
             <AppBar position="static" color="transparent" style={{marginTop: '4px'}}>       
                <Toolbar variant="dense">
            <Tabs
                variant="scrollable"
                scrollButtons="auto"
                aria-label="scrollable auto tabs"
                textColor="primary"
                value={mainTabsValue} 
                onChange={(event, newValue) => setMainTabsValue(newValue)}
            >
                <Tab label={'Активные'} key={0}></Tab>
                <Tab label={'Статистика'} key={1}></Tab>
            </Tabs>
            </Toolbar>
            </AppBar>
            <DesignerTabPanel key={0} value={mainTabsValue} index={0}>
                <DataLoader
                    loadFunc = {dataHub.olapController.getInfoCubes}
                    loadParams = {[]}
                    reload = {reloadActive}
                    onDataLoaded = {data => {props.actionFolderLoaded(folderItemsType, {cubes: data, id: null, childFolders: []})}}
                    onDataLoadFailed = {message => {props.actionFolderLoadFailed(folderItemsType, message)}}
                >
                    <FolderContent
                        itemsType = {folderItemsType}
                        data = {props.filteredCubes ? props.filteredCubes : props.currentFolderData}
                        showAddFolder = {false}
                        showAddItem = {false}
                        showItemControls = {false}
                        pagination = {true}
                        onItemClick = {()=> {}}
                        onRefreshClick = {handleRefreshFolderActive}
                    />
                </DataLoader>
            </DesignerTabPanel>
            <DesignerTabPanel key={1} value={mainTabsValue} index={1}>
                <div>
                    <MuiPickersUtilsProvider utils={RuLocalizedUtils} locale={ruLocale}>
						<KeyboardDateTimePicker
                            size = 'small'
                            style={{margin: '16px 8px 8px 16px'}}
						//	className={classes.dtmStyle}
							id="datePickerStart"
							view={["date" | "year" | "month" | "hours" | "minutes"]}
							openTo="hours"
							ampm={false}
							disableFuture
							format="dd.MM.yyyy HH:mm"
							margin="normal"
							inputVariant="filled"
							value={statsStartDTM}
							onChange={date => handleChangeDTM('from', date)}
											label="Начало периода"
											cancelLabel="ОТМЕНИТЬ"
											okLabel="СОХРАНИТЬ"
						/>   
						<KeyboardDateTimePicker
                            size = 'small'
                            style={{margin: '16px 8px 8px'}}
						//	className={classes.dtmStyle}
							id="datePickerEnd"
							view={["date" | "year" | "month" | "hours" | "minutes"]}
							openTo="hours"
							ampm={false}
							disableFuture
							format="dd.MM.yyyy HH:mm"
							margin="normal"
                            inputVariant="filled"
                            minDate = {statsStartDTM}
							value={statsEndDTM}
							onChange={date =>  handleChangeDTM('to', date)}
											label="Конец периода"
											cancelLabel="ОТМЕНИТЬ"
											okLabel="СОХРАНИТЬ"
						/>
					</MuiPickersUtilsProvider>
				</div>
				<DataLoader
					loadFunc = {dataHub.olapController.getLogInfo}
					loadParams = {[statsStartDTM, statsEndDTM]}
					reload = {reloadStats}
					onDataLoaded = {data => {props.actionFolderLoaded(folderItemsType, {cubes: data, id: null, childFolders: []})}}
					onDataLoadFailed = {message => {props.actionFolderLoadFailed(folderItemsType, message)}}
				>
					<FolderContent
						itemsType = {folderItemsType}
						data = {props.filteredCubes ? props.filteredCubes : props.currentFolderData}
						showAddFolder = {false}
						showAddItem = {false}
						showItemControls = {false}
						pagination = {true}
						onItemClick = {()=> {}}
						onRefreshClick = {handleRefreshFolderStats}
					/>
				</DataLoader>
            </DesignerTabPanel>
        </div>
    )
}

const mapStateToProps = state => {
    return {
        state : state.cubesMenuView,
        currentFolderData : state.cubesMenuView.currentFolderData?.cubes.map(item => ({
            id: item.reportJobId,
            created: item.created,
            modified: item.modified,
            userName: item.username ?? item.userName,
            name: item.reportName,
            rowCount:  item.rowCount ,
            reportJobId: item.reportJobId,
            useCount: item.useCount ?? item.requestCount,
            reportId: item.reportId,
            reportJobOwner: item.reportJobOwner
        })),
        filteredCubes : state.cubesMenuView.filteredCubes,
        filters : state.cubesMenuView.filters
    }
}

const mapDispatchToProps = {
    actionFolderLoaded,
    actionFolderLoadFailed,
    actionItemClick,
    startReport,
    actionFilterJobs,
    actionJobCancel,
    actionSetSidebarItem,
    showSqlDialog
}

export default connect(mapStateToProps, mapDispatchToProps)(CubesMenuView);
