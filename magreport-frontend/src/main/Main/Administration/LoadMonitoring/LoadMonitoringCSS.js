import makeStyles from "@material-ui/core/styles/makeStyles";

export const LoadMonitoringCSS = makeStyles(theme => ({
    threadsChart: {
        marginTop: '20px',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        gap: '30px'
    },
    jobThreadsChart: {
        width: '80%',
        padding: '20px',
        borderRadius: '20px',
        background: 'white',
        boxShadow: 'rgba(100, 100, 111, 0.2) 0px 7px 29px 0px',
        '@media (max-width: 800px)': {
            width: '90%',
        }
    },
    dataSourceChart: {
        margin: '15px',
        width: '30%',
        height: '300px',
        padding: '20px',
        borderRadius: '20px',
        background: 'white',
        boxShadow: 'rgba(0, 0, 0, 0.24) 0px 3px 8px',
        '@media (max-width: 1150px)': {
            width: '45%',
        },
        '@media (max-width: 800px)': {
            width: '90%',
        },
    },
    dataSourceChartMainWrapp: {
        width: '100%',
        height: '100%',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        marginTop: '30px'
    },
    dataSourceChartWrapp: {
        width: '100%',
        height: '100%',
        display: 'flex',
        flexWrap: 'wrap',
        justifyContent: 'center'
    },
    dataSourceChartSubtitle: {
        textAlign: 'center'
    }
}));