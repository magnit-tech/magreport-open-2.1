import React from 'react';
import { LoginPageCSS } from './LoginPageCSS'
import clsx from 'clsx';

function isHollyday(){
    let newDate = new Date()
    let date = newDate.getDate();
    let month = newDate.getMonth() + 1;
    if ((month === 12 && date>9) || (month === 1 && date <15))
        {
            if (date%2 === 0) return 0
            if (date%5 === 0) return 1
            return 2
        }
         
    else return -1
}

function LoginCat(){
    const classes = LoginPageCSS();
    return (
        <div className={clsx({
            [classes.loginCat]: isHollyday() === -1, 
            [classes.newYearLoginCat]: isHollyday() === 0,
            [classes.newYearLoginCat1]: isHollyday() === 1,
            [classes.newYearLoginCat2]: isHollyday() === 2,
        })}></div>
    )
}

export default LoginCat;