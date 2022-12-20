import React from 'react';

import { Outlet } from 'react-router-dom'

import { MainCSS } from './MainCSS'

import Sidebar from './Sidebar/Sidebar';

import Header from '../../components/header/Header/Header';
import Navbar from '../../components/Navbar/Navbar'
import SnackbarInfo from '../../components/SnackbarInfo/SnackbarInfo';
import AlertDialog from '../../components/AlertDialog/AlertDialog';


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