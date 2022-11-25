import React from 'react';

import { Outlet } from 'react-router-dom'

import { MainCSS } from './MainCSS'

import Sidebar from './Sidebar/Sidebar';

import Header from '../../components/header/Header/Header';
import Navbar from '../../components/Navbar/Navbar'
import SnackbarInfo from '../../components/SnackbarInfo/SnackbarInfo';
import AlertDialog from '../../components/AlertDialog/AlertDialog';

// import { connect } from 'react-redux';
// import { showSnackbar } from '../../redux/actions/actionSnackbar';
// import { showAlertDialog, hideAlertDialog } from '../../redux/actions/actionsAlertDialog';



export default function Main(props){

    const classes = MainCSS();

    return (
        <main className={classes.main}>
            <Sidebar />
            <div className={classes.workspace}>
                <Header version={props.version}/>
                <Navbar />

                <Outlet/>
                    
                <SnackbarInfo />
                <AlertDialog />
            </div>
        </main>
    );
}

// const mapStateToProps = state => {
//     return {
//         currentSidebarItemKey: state.currentSidebarItemKey
//     }
// }

// const mapDispatchToProps = {
//     showAlertDialog,
//     hideAlertDialog,
//     showSnackbar,
// }

// export default connect(null, null)(Main);
