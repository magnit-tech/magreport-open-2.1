import React, { useEffect } from 'react';
import { connect } from 'react-redux';

import { LoadMonitoringCSS as useStyles } from "./LoadMonitoringCSS";

// local
import dataHub from 'ajax/DataHub';
import { actionFolderLoaded, actionFolderLoadFailed } from 'redux/actions/menuViews/folderActions';
import SidebarItems from '../../Sidebar/SidebarItems';

// components
import DataLoader from "../../../DataLoader/DataLoader";
import { JobThreadsChart } from './ui/jobThreadsChart';
import { DataSourceChart } from './ui/dataSourceChart';


function LoadMonitoringMenuView(props) {

    const classes = useStyles();

    const state = props.state?.currentFolderData;

    const folderItemsType = SidebarItems.admin.subItems.loadMonitoring.folderItemType;

    function generateDataSourceCharts(arr) {
        let result = []

        arr.map((item) => result.push(<DataSourceChart dataSourceData={item} key={item.dataSourceId} />))

        return result
    }

    return (
        <div style={{ display: 'flex', flex: 1 }}>
            <DataLoader
                loadFunc={dataHub.adminController.getCurrentLoad}
                loadParams={[]}
                reload={false}
                onDataLoaded={(data) => props.actionFolderLoaded(folderItemsType, data)}
                onDataLoadFailed={(error) => props.actionFolderLoadFailed(folderItemsType, error)}
            >
                <div className={classes.threadsChart}>
                    {state &&
                        <>
                            <JobThreadsChart
                                title={'Текущая нагрузка на потоки в пуле для обработки заданий'}
                                dataThreads={
                                    {
                                        'totalThreads': state.totalJobCountThreads,
                                        'currentThreads': state.countJobWorkingThreads
                                    }
                                }
                            />
                            <JobThreadsChart
                                title={'Текущая нагрузка на потоки в пуле для выгрузки в Excel'}
                                dataThreads={
                                    {
                                        'totalThreads': state.totalExportCountThreads,
                                        'currentThreads': state.countExportCountThreads
                                    }
                                }
                            />
                        </>
                    }
                </div>
                {(state && state?.dataSourceConnectInfo?.length > 0) &&
                    <div className={classes.dataSourceChartMainWrapp}>
                        <h3 className={classes.dataSourceChartSubtitle}>В разрезе каждого источника данных</h3>
                        <div className={classes.dataSourceChartWrapp}>
                            {generateDataSourceCharts(state.dataSourceConnectInfo)}
                        </div>
                    </div>
                }
            </DataLoader>
        </div>
    );
}

const mapStateToProps = state => {
    return {
        state: state.folderData
    }
}

const mapDispatchToProps = {
    actionFolderLoaded,
    actionFolderLoadFailed
}

export default connect(mapStateToProps, mapDispatchToProps)(LoadMonitoringMenuView);
