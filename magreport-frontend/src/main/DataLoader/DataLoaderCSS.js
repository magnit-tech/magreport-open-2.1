import {makeStyles} from '@material-ui/core/styles';


export const DataLoaderCSS = makeStyles(theme => ({
    dataLoaderRoot: {
        display: 'flex', 
        flex: 1, 
        flexDirection: 'column',
    },
    dataLoaderRootWOScroll: {
        overflow: 'unset'
    },
    dataLoaderProgressDiv: {
        display:"flex", 
        flex: 1,
        justifyContent: "center",
    },
    dataLoaderProgress: {
        display: 'flex', 
        alignSelf: 'center'
    },
    dataLoaderErrorAlert: {
        margin: '20px'
    }
}))