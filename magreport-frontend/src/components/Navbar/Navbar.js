import React from 'react';

import {NavbarCSS} from './NavbarCSS'

import { useDispatch, useSelector } from 'react-redux';

import {Breadcrumbs, Chip, Paper} from '@material-ui/core';

function Navbar (){

    const classes = NavbarCSS();

    const dispatch = useDispatch()
    const itemsData = useSelector(state => state.navbar.items)

    const items = []
    
    itemsData.forEach((item, index) => {
        items.push(
            <Chip
                classes={{label: classes.chipLabel}}
                key={index}
                className={classes.chip}
                label={item.text}
                icon={item.icon}
                variant = {item.isLast ? "default" : "outlined"}
                onClick={() => dispatch(item.callbackFunc)}
            />
        )
    })

    return (
        <Paper elevation={0} className = {classes.navbarBlock}>
            <Breadcrumbs separator= {null} aria-label="breadcrumb"
                classes={{separator: classes.sprt}}
            >
                {items}
            </Breadcrumbs>
        </Paper>
    )
}

export default Navbar