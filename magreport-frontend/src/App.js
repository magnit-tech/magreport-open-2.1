import React from 'react';

import AppRoutes from 'router/routes';

import { connect } from 'react-redux';

import { ThemeProvider } from '@material-ui/core/styles';
import CssBaseline from "@material-ui/core/CssBaseline";
import { SnackbarProvider } from 'notistack';


function App(props) {

    const appVersion = process.env.REACT_APP_VERSION ? process.env.REACT_APP_VERSION : "???";

    return (
        <ThemeProvider theme={props.theme}>
            <SnackbarProvider maxSnack={10}>
                <CssBaseline/>
                <div>
                    <AppRoutes version={appVersion}/>
                </div>
            </SnackbarProvider>
        </ThemeProvider>
    );

}

const mapStateToProps = state => {
    return {
        theme: state.themesMenuView.theme
    }
}

export default connect(mapStateToProps, null)(App);
