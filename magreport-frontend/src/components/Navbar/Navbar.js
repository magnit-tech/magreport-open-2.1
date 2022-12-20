import React from 'react';

import { NavbarCSS } from './NavbarCSS'

import { useNavigate } from 'react-router-dom';

import { useSelector } from 'react-redux';

import { Breadcrumbs, Chip, Paper } from '@material-ui/core';


function Navbar (){

    const classes = NavbarCSS();

    const navigate = useNavigate()

    const itemsData = useSelector(state => state.navbar.items)

    const items = []

    function handleChipClick(action, itemsType, id, parentId) {
        if (action) {
            navigate(`/${itemsType}${parentId ? `/${parentId}` : ''}/${action}${id || id === '0' ? '/' + id : ''}`)
        } else{
            navigate(`/${itemsType}${id || id === 0 ? '/' + id : ''}`)
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
                onClick={() => handleChipClick(item.action, item.itemsType, item.id, item.parentId)}
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