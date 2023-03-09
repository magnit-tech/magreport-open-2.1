import React from 'react';
import { LoginPageCSS } from './LoginPageCSS'
import clsx from 'clsx';
import isHollyday from  '../../HollydayFunctions';

function LoginCat(){
    const classes = LoginPageCSS();
    return (
        <div className={clsx({
            [classes.loginCat]: isHollyday() === -1, 
            [classes.newYearLoginCat]: isHollyday() === 0,
            [classes.newYearLoginCat1]: isHollyday() === 1,
            [classes.newYearLoginCat2]: isHollyday() === 2,
            [classes.marthLoginCat]: isHollyday() === 3,
            [classes.aprilLoginCat]: isHollyday() === 4
        })}></div>
    )
}

export default LoginCat;