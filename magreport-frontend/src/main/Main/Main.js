import React from 'react';
import { Outlet } from 'react-router-dom'

import {MainCSS} from './MainCSS'

import Header from '../../components/header/Header/Header';
import SnackbarInfo from '../SnackbarInfo/SnackbarInfo';
import AlertDialog from '../AlertDialog/AlertDialog';
import { connect } from 'react-redux';
import { showSnackbar } from '../../redux/actions/actionSnackbar';
import { showAlertDialog, hideAlertDialog } from '../../redux/actions/actionsAlertDialog';
import Sidebar from './Sidebar/Sidebar';
import SidebarItems from './Sidebar/SidebarItems';
import Navbar from './Navbar/Navbar'



function Main(props){

    const classes = MainCSS();

    const { currentSidebarItemKey } = props;

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

const mapStateToProps = state => {
    return {
        currentSidebarItemKey: state.currentSidebarItemKey
    }
}

const mapDispatchToProps = {
    showAlertDialog,
    hideAlertDialog,
    showSnackbar,
}

export default connect(mapStateToProps, mapDispatchToProps)(Main);
