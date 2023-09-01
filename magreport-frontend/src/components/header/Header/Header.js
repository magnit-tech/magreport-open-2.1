import React, { useState } from 'react';
import clsx from 'clsx';
import Draggable from 'react-draggable';
import { useAuth } from 'router/useAuth';

import { HeaderCSS } from './HeaderCSS';

import ConfigLocal from '../../../ajax/config/Config-local';
import ConfigProd from '../../../ajax/config/Config-prod';

import HollydayPanel from './HollydayPanel';
import isHollyday from '../../../HollydayFunctions';

// redux
import { connect } from 'react-redux';
import {
	setLightTheme,
	setDarkTheme,
} from '../../../redux/actions/admin/actionThemeDesign';

// mui
import { AppBar, Toolbar, Typography, IconButton, Tooltip } from '@material-ui/core';
import { Button, Dialog, DialogActions, DialogContent, DialogTitle, Paper, Menu, MenuItem } from '@material-ui/core';

// components 
import HeaderHelp from '../ui/HeaderHelp';

// icons
import LogoIcon from '../LogoIcon/LogoIcon';
import ExitToAppIcon from '@material-ui/icons/ExitToApp';
import HelpIcon from '@material-ui/icons/Help';
import ImportContactsIcon from '@material-ui/icons/ImportContacts';
import Brightness5Icon from '@material-ui/icons/Brightness5';
import Brightness4Icon from '@material-ui/icons/Brightness4';


function PaperComponent(props) {
	return (
		<Draggable
			handle='#draggable-dialog-title'
			cancel={'[class*="MuiDialogContent-root"]'}
		>
			<Paper {...props} />
		</Draggable>
	);
}

function Header(props) {
	const classes = HeaderCSS();

	const { user, signout } = useAuth();

	const config =
		(process.env.NODE_ENV === 'production'
			? new ConfigProd().HOST_BASE_URL
			: new ConfigLocal().HOST_BASE_URL) + '/user-manual.pdf';

	const themeLightness = props.themeLightness;
	const tooltipTitle = props.themeLightness ? 'Светлый фон' : 'Тёмный фон';

	function handleThemeClick() {
		localStorage.setItem('isDarkTheme', !themeLightness);
		if (themeLightness) {
			props.setLightTheme();
		} else {
			props.setDarkTheme();
		}
	}

	const [openAbout, setOpenAbout] = React.useState(false);
	const [openHelp, setOpenHelp] = useState(false);

	const handleClickOpenAbout = () => {
		setOpenAbout(true);
		setAnchorEl(null);
	};

	const handleCloseAbout = () => {
		setOpenAbout(false);
	};

	const [anchorEl, setAnchorEl] = React.useState(null);
	const [anchorEl2, setAnchorEl2] = React.useState(null);

	return (
		<AppBar
			position='static'
			className={clsx(classes.appBar, {
				[classes.appBarHeight]: isHollyday() === -1,
				[classes.appBarHeightHollyday]: isHollyday() >= 0
			})}
		>
			<HollydayPanel />

			<Menu
				id='simple-menu'
				anchorEl={anchorEl}
				keepMounted
				open={Boolean(anchorEl)}
				onClose={() => setAnchorEl(null)}
			>
				<MenuItem onClick={handleClickOpenAbout}>О программе</MenuItem>
				<MenuItem onClick={() => window.open(`${config}`)}
				>
					Руководство пользователя
				</MenuItem>
			</Menu>

			<Dialog
				open={openAbout}
				onClose={handleCloseAbout}
				PaperComponent={PaperComponent}
				aria-labelledby='draggable-dialog-title'
			>
				<DialogTitle
					style={{ cursor: 'move' }}
					id='draggable-dialog-title'
				></DialogTitle>
				<DialogContent>
					<div style={{ display: 'flex', flexDirection: 'row' }}>
						<div className={classes.catFinger} />
						<div style={{ margin: '32px' }}>
							<Typography variant='h6'> МАГРЕПОРТ </Typography>
							<Typography variant='h6'> Версия: {props.version}</Typography>
						</div>
					</div>
				</DialogContent>
				<DialogActions>
					<Button
						autoFocus
						variant='contained'
						onClick={handleCloseAbout}
						color='primary'
					>
						Понятно
					</Button>
				</DialogActions>
			</Dialog>

			<HeaderHelp
				open={openHelp}
				onClose={() => setOpenHelp(!openHelp)}
			/>

			<Toolbar variant='dense' className={classes.iconIndent}>
				<LogoIcon />
				<Typography className={classes.logoText}>МАГРЕПОРТ</Typography>
				<Tooltip title={'Помощь'}>
					<IconButton onClick={() => setOpenHelp(true)}>
						<HelpIcon className={classes.iconButton}></HelpIcon>
					</IconButton>
				</Tooltip>
				<Tooltip title={'Справка'}>
					<IconButton onClick={(event) => setAnchorEl(event.currentTarget)}>
						<ImportContactsIcon className={classes.iconButton}></ImportContactsIcon>
					</IconButton>
				</Tooltip>
				<Tooltip title={tooltipTitle}>
					<IconButton onClick={handleThemeClick}>
						{themeLightness ? (
							<Brightness5Icon className={classes.iconButton} />
						) : (
							<Brightness4Icon className={classes.iconButton} />
						)}
					</IconButton>
				</Tooltip>
				<Typography
					id="userNameLabel"
					variant='overline'
					className={classes.userNameClass}
				>
					{user.current ? user.current.name : ''}
				</Typography>
				{user.current?.name ? (
					<Tooltip title='Выйти'>
						<IconButton
							className={classes.iconButton}
							aria-label='logout'
							onClick={() => signout()}
						>
							<ExitToAppIcon />
						</IconButton>
					</Tooltip>
				) : (
					''
				)}
			</Toolbar>
		</AppBar>
	);
}

const mapStateToProps = state => {
	return {
		themeLightness: state.themesMenuView.darkTheme,
	};
};

const mapDispatchToProps = {
	setLightTheme,
	setDarkTheme,
};

export default connect(mapStateToProps, mapDispatchToProps)(Header);