import React, {useState, useEffect } from 'react';
import { connect } from 'react-redux';

import { useLocation, useNavigate } from 'react-router-dom'

// dataHub
import dataHub from 'ajax/DataHub';

// redux
import {actionFolderLoaded, actionFolderLoadFailed} from 'redux/actions/menuViews/folderActions';
import {actionFilterJobs, actionJobCancel, showSqlDialog, actionShowStatusHistory} from 'redux/actions/jobs/actionJobs';

// components
import DataLoader from 'main/DataLoader/DataLoader';
import FolderContent from 'main/FolderContent/FolderContent';
import SidebarItems from '../../Sidebar/SidebarItems';
import {dateCorrection} from '../../../../../src/utils/dateFunctions'


function UsersJobsMenuView(props){

    const navigate = useNavigate()
    const location = useLocation()

    let params = [props.filters?.periodStart ?? null, 
        props.filters?.periodEnd ?? null, 
        props.filters?.selectedStatuses ?? null,
        props.filters?.user ?? null,
        props.filters?.reportIds ?? null
    ]
    const [reload, setReload] = useState({needReload : false});

    let folderItemsType = SidebarItems.admin.subItems.userJobs.folderItemType;

    let now  =  new Date();
    let midNight = new Date();

    midNight.setHours(0);
    midNight.setMinutes(0);
    midNight.setSeconds(0);

    useEffect(()=>{
        props.actionFilterJobs(folderItemsType, {periodStart: dateCorrection(midNight, false), periodEnd:  dateCorrection(now, false) })
    }, []); // eslint-disable-line

    function handleRefreshFolder(){
        setReload({needReload : true})
    }

    function handleItemClick(jobId) {
        navigate(`/ui/report/${jobId}`)
    }

    function handleReportRunClick(reportId, jobId) {
        navigate(`/ui/report/starter/${reportId}?jobId=${jobId}`, {state: location.pathname})
    }

    return (
        <div  style={{display: 'flex', flex: 1}}>
            <DataLoader
                loadFunc = {dataHub.reportJobController.getAllUsersJobs}
                loadParams = {params}
                reload = {reload}
                onDataLoaded = {data => {props.actionFolderLoaded(folderItemsType, data)}}
                onDataLoadFailed = {message => {props.actionFolderLoadFailed(folderItemsType, message)}}
            >
                <FolderContent
                    itemsType = {folderItemsType}
                    data = {props.currentFolderData}
                    filters = {props.filters}
                    showAddFolder = {false}
                    showAddItem = {false}
                    showItemControls = {false}
                    pagination = {true}
                    dialog = {props.dialog}
                    onItemClick = {handleItemClick}
                    onReportRunClick = {handleReportRunClick}
                    onFilterClick = {filters => {props.actionFilterJobs(folderItemsType, filters)}}
                    onRefreshClick = {handleRefreshFolder}
                    onJobCancelClick = {(jobIndex, jobId) => {props.actionJobCancel(folderItemsType, jobIndex, jobId)}}
                    onShowSqlDialogClick = {props.showSqlDialog}
                    onShowHistoryStatusClick = {props.actionShowStatusHistory}
                />

            </DataLoader>
        </div>
    )
}

const mapStateToProps = state => {
    return {
        state : state.folderData,
        currentFolderData : state.folderData.currentFolderData,
        filters : state.folderData.filters
    }
}

const mapDispatchToProps = {
    actionFolderLoaded,
    actionFolderLoadFailed,
    actionFilterJobs,
    actionJobCancel,
    showSqlDialog,
    actionShowStatusHistory
}

export default connect(mapStateToProps, mapDispatchToProps)(UsersJobsMenuView);
