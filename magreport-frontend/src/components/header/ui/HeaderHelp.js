import React, { useEffect, useState } from 'react';

import { HeaderCSS } from '../Header/HeaderCSS';

// mui
import { Button, Dialog, DialogActions, DialogContent } from '@material-ui/core';

// icons
import EmailIcon from '@material-ui/icons/Email';
import dataHub from 'ajax/DataHub';


export default function HeaderHelp(props) {
	const classes = HeaderCSS();

	const [configEmail, setConfigEmail] = useState(null)

	useEffect(() => {
		if (props.open) {
			dataHub.serverSettings.getFolder('LOGIN_CONFIG', ({ ok, data }) => {
				if (ok) {
					setConfigEmail(data.folders[0].parameters[2].value ?? 'sopr_magreport@magnit.ru')
				}
			})
		}
	}, [props.open, setConfigEmail])

	return (
		<Dialog
			open={props.open}
			onClose={props.onClose}
		>
			<DialogContent>
				<div style={{ display: 'flex', flexDirection: 'row' }}>
					<div className={classes.helpDialogWrapp}>
						<p>По вопросам работы системы пишите на</p>
						<Button
							color="primary"
							variant="outlined"
							href={`mailto:${configEmail}?subject=Магрепорт: вопрос по работе системы`}
						>
							<EmailIcon className={classes.helpDialogIconBtn} />
							email сопровождения
						</Button>
					</div>
				</div>
			</DialogContent>
			<DialogActions>
				<Button
					autoFocus
					variant='contained'
					onClick={props.onClose}
					color='primary'
				>
					Закрыть
				</Button>
			</DialogActions>
		</Dialog>
	)
}