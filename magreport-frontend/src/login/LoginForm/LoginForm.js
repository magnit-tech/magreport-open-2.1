import React, { useState } from 'react';

import { LoginFormCSS } from './LoginFormCSS'

import { useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from 'router/useAuth';

import { connect } from 'react-redux';
import { showLoader, hideLoader } from '../../redux/actions/UI/actionLoader'
import { showAlert, hideAlert } from '../../redux/actions/UI/actionsAlert'

// mui
import { Avatar, Button, TextField, IconButton, Typography } from '@material-ui/core/';
import { FormControl, CircularProgress, OutlinedInput, InputLabel, InputAdornment } from '@material-ui/core/';

// icons
import Visibility from '@material-ui/icons/Visibility';
import VisibilityOff from '@material-ui/icons/VisibilityOff';
import LockOutlinedIcon from '@material-ui/icons/LockOutlined';

//local
import StyleConsts from 'StyleConsts.js';
import FailLoginLink from 'login/ui/FailLoginLink';


function LoginForm(props) {
    const classes = LoginFormCSS();

    const location = useLocation()
    const navigate = useNavigate()
    const { signin } = useAuth()

    const { loader } = props;

    const [form, setForm] = useState({ login: '', password: '' });
    const [showPassword, setShowPassword] = useState(false);

    const handleClickShowPassword = () => {
        setShowPassword(!showPassword);
    };

    const handleMouseDownPassword = (event) => {
        event.preventDefault();
    };

    function handleChange(e) {
        props.hideAlert();
        setForm({ ...form, [e.target.name]: e.target.value },);
    }

    function handleSubmit(e) {
        e.preventDefault();
        props.showLoader()

        let loginParts = form.login.split("\\");
        let domainName;
        let userName;

        if (loginParts.length > 1) {
            domainName = loginParts[0];
            userName = loginParts[1];
        }
        else {
            userName = loginParts[0];
        }

        if (!localStorage.getItem('drawerWidth')) {
            localStorage.setItem('drawerWidth', StyleConsts.drawerWidth);
        }

        signin(userName, domainName, form.password, () => navigate(location.state?.from?.pathname || '/ui/reports', { replace: true }))
    }

    return (
        <div className={classes.paper}>
            <Avatar className={classes.avatar}>
                <LockOutlinedIcon />
            </Avatar>
            <Typography component="h1" variant="h5">
                Авторизация
            </Typography>
            <form className={classes.form} noValidate>
                <TextField
                    variant="outlined"
                    margin="normal"
                    required
                    fullWidth
                    id="login"
                    label="Логин"
                    name="login"
                    autoFocus
                    value={form.login}
                    onChange={handleChange}
                    error={props.alertData.data.open}
                />
                <FormControl variant="outlined" fullWidth style={{ margin: '16px 0px 8px' }}>
                    <InputLabel htmlFor="password" required error={props.alertData.data.open}>Пароль</InputLabel>
                    <OutlinedInput
                        id="password"
                        name="password"
                        type={showPassword ? 'text' : 'password'}
                        value={form.password}
                        required
                        onChange={handleChange}
                        // autoComplete="current-password"
                        error={props.alertData.data.open}
                        endAdornment={
                            <InputAdornment position="end">
                                <IconButton
                                    style={{ marginLeft: '-8px' }}
                                    aria-label="toggle password visibility"
                                    onClick={handleClickShowPassword}
                                    onMouseDown={handleMouseDownPassword}
                                    edge="end"
                                >
                                    {showPassword ? <Visibility /> : <VisibilityOff />}
                                </IconButton>
                            </InputAdornment>
                        }
                        labelWidth={68}
                    />
                </FormControl>

                <Button
                    id="loginSubmit"
                    type="submit"
                    fullWidth
                    variant="contained"
                    color="primary"
                    className={classes.submit}
                    disabled={loader}
                    onClick={handleSubmit}
                >
                    Войти
                </Button>

                {loader ? <div className={classes.circularProgress}><CircularProgress /></div> : null}

                <FailLoginLink />
            </form>
        </div>
    );
}

const mapStateToProps = state => {
    return {
        loader: state.loader.loader,
        alertData: state.alert
    }
}

const mapDispatchToProps = {
    showAlert,
    hideAlert,
    showLoader,
    hideLoader
}

export default connect(mapStateToProps, mapDispatchToProps)(LoginForm);
