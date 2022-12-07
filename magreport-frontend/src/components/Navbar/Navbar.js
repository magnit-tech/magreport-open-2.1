import React from 'react';

import {NavbarCSS} from './NavbarCSS'

import { useLocation, useNavigate } from 'react-router-dom';

import { useDispatch, useSelector } from 'react-redux';

import { Breadcrumbs, Chip, Paper } from '@material-ui/core';


function Navbar (){

    const classes = NavbarCSS();

    const navigate = useNavigate()
    const location = useLocation()

    const dispatch = useDispatch()
    const itemsData = useSelector(state => state.navbar.items)

    const items = []

    function handleChipClick(itemsType, id) {
        // dispatch(item.callbackFunc)

        if (location.pathname.indexOf(itemsType) === -1) {
            navigate(`/${itemsType}${id ? '/' + id : ''}`)

        }
    }
    
    

    itemsData.forEach((item, index) => {
        items.push(
            <Chip
                classes={{label: classes.chipLabel}}
                key={index}
                className={classes.chip}
                label={item.text}
                icon={item.icon}
                variant = {item.isLast ? "default" : "outlined"}
                onClick={() => handleChipClick(item.itemsType, item.id)}
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