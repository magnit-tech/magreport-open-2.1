import React, { useState, useEffect } from 'react';

import { LoginFormCSS } from '../LoginForm/LoginFormCSS'

import dataHub from 'ajax/DataHub';


import { Button } from '@material-ui/core';


export default function FailLoginLink() {
	const classes = LoginFormCSS();

	const [configData, setConfigData] = useState({
		loginHelpMessage: 'Ошибка при входе?',
		loginHelpLink: 'sopr_magreport@magnit.ru'
	})

	useEffect(() => {
		dataHub.serverSettings.getFolder('LOGIN_CONFIG', ({ ok, data }) => {
			if (ok) {
				console.log(data.folders[0].parameters);
				setConfigData({
					loginHelpMessage: data.folders[0].parameters[0].value ?? 'Ошибка при входе?',
					loginHelpLink: data.folders[0].parameters[1].value ?? 'sopr_magreport@magnit.ru'
				})
			}
		})
	}, [])

	return (
		<div className={classes.failLoggin}>
			<Button
				color="secondary"
				href={`mailto:${configData.loginHelpLink}?subject=Магрепорт: ошибка при входе`}
			>
				{configData.loginHelpMessage}
			</Button>
		</div>
	);
}
