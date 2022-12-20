import React, {useState} from 'react';
import { connect } from 'react-redux';

import { useLocation, useNavigate } from 'react-router-dom'

import { useAuth } from 'router/useAuth';

// dataHub
import dataHub from 'ajax/DataHub';

// redux
import { actionFolderLoaded, actionFolderLoadFailed } from 'redux/actions/menuViews/folderActions';
import { actionFilterJobs, actionJobCancel, showSqlDialog, actionShowStatusHistory, actionJobAddComment } from 'redux/actions/jobs/actionJobs';

// components
import DataLoader from 'main/DataLoader/DataLoader';
import FolderContent from 'main/FolderContent/FolderContent';

import SidebarItems from '../Sidebar/SidebarItems';


function JobsMenuView(props){
    
    const navigate = useNavigate()
    const location = useLocation()
    
    const { user } = useAuth()

    let params = [props.filters?.periodStart ?? null, 
        props.filters?.periodEnd ?? null, 
        props.filters?.users ?? null, 
        props.filters?.reportIds ?? null, 
        props.filters?.selectedStatuses ?? null
    ];
    
    const [reload, setReload] = useState({needReload : false})

    let folderItemsType = SidebarItems.jobs.folderItemType;


    function handleRefreshFolder(){
        setReload({needReload : true})
    }

    function handleItemClick(jobId) {
        navigate(`/report/${jobId}`)
    }

    function handleReportRunClick(reportId, jobId) {
        navigate(`/report/starter/${reportId}?jobId=${jobId}`, {state: location.pathname})
    }

    function handleCancelClick(folderItemsType, jobIndex, jobId){
        props.actionJobCancel(folderItemsType, jobIndex, jobId);
        setReload({needReload : true})
    }

    return(
        <div  style={{display: 'flex', flex: 1}}>
            <DataLoader
                loadFunc = {dataHub.reportJobController.getMyJobs}
                loadParams = {params}
                reload = {reload}
                onDataLoaded = {(data) => {props.actionFolderLoaded(folderItemsType, data)}}
                onDataLoadFailed = {(message) => {props.actionFolderLoadFailed(folderItemsType, message)}}
            >
                <FolderContent
                    itemsType = {folderItemsType}
                    data = {props.currentFolderData}
                    filters = {props.filters}
                    showAddFolder = {false}
                    showAddItem = {false}
                    showItemControls = {false}
                    pagination = {true}
                    currentUser = {user.current.name}

                    onItemClick = {handleItemClick}
                    onReportRunClick = {handleReportRunClick}

                    onFilterClick = {filters => {props.actionFilterJobs(folderItemsType, filters)}}
                    onJobCancelClick = {(jobIndex, jobId) => handleCancelClick(folderItemsType, jobIndex, jobId)}
                    onRefreshClick = {handleRefreshFolder}
                    onShowSqlDialogClick = {props.showSqlDialog}
                    onShowHistoryStatusClick = {props.actionShowStatusHistory}
                    onJobAddComment = {(jobId, jobIndex, comment) => actionJobAddComment(folderItemsType, jobId, jobIndex, comment)}
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
    actionShowStatusHistory,
    actionJobAddComment
}

export default connect(mapStateToProps, mapDispatchToProps)(JobsMenuView);
