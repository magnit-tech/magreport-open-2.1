import React, { useRef, useState } from 'react';

import { PivotCSS } from '../PivotCSS';

import Draggable from 'react-draggable';

import { Paper, Dialog, DialogTitle, DialogActions, Button, Box, AppBar, Tabs, Tab, Typography, TextField, FormControl, FormControlLabel, InputLabel, MenuItem, Select, Switch,} from '@material-ui/core';

/**
	* @param {Boolean} props.open - boolean-значение отображения модального окна
	* @param {Object} props.configs - объект существующих конфигураций, доступных пользователю
	* @param {*} props.onCancel - function - callback отмены/закрытия модального окна
	* @param {*} props.onSave - function - callback сохранения конфигурации
**/

//Перетаскивание модального окна
function PaperComponent(props) {
    return (
		<Draggable handle="#drag-title" cancel={'[class*="MuiDialogContent-root"]'}>
			<Paper {...props} />
		</Draggable>
    );
}

//Обертка для children у табов
function TabPanel(props) {
	const { children, value, index, ...other } = props;
  
	return (
		<div
			role="tabpanel"
			hidden={value !== index}
			id={`scrollable-auto-tabpanel-${index}`}
			aria-labelledby={`scrollable-auto-tab-${index}`}
			{...other}
		>
			{value === index && (
				<Box style={{padding: '24px'}}>
					{children}
				</Box>
			)}
		</div>
	);
}


export default function ConfigSaveDialog(props){
	
	const classes = PivotCSS();

	const availableConfigs = props.configs

	// Переключение главных табов
	const [mainTabsValue, setMainTabsValue] = useState(0);
	const handleChangeMainTabs = (event, newValue) => {
		setMainTabsValue(newValue)
		
		if (newValue === 1 && existingConfig.show) {
			return objectToSend.current = {
				type: 'saveExistingConfig',
				...existingConfig,
			}
		} 

		return objectToSend.current = {
			type: 'saveNewConfig',
			...newConfig,
		}

	};

	// Объект конфигурации для отправки в родителя
	const objectToSend = useRef({
		type: 'saveNewConfig',
		name: 'Новая конфигурация',
		description: '',
		jobId: null,
		olapConfigId: '',
		report: null,
		reportOlapConfigId: null,
		saveFor: "job",
		isShare: false,
		isDefault: false
	})

	// Новая конфигурация
	const [newConfig, setNewConfig] = useState({
		name: 'Новая конфигурация',
		description: '',
		saveFor: "job",
		isShare: false,
		isDefault: false
	});

	// Существующая конфигурация
	const [existingConfig, setExistingConfig] = useState({
		show: false,
		name: '',
		description: '',
		jobId: null,
		olapConfigId: '',
		report: null,
		saveFor: "job",
		isShare: false,
		isDefault: false
	});

	// Переключение select
	const handleChange = (event, action) => {
		const e = event.target.value;

		switch(action) {
			case 'nameNewConfig':
				objectToSend.current = {
					...objectToSend.current,
					name: e
				}
				setNewConfig({...newConfig, name: e})
				break;
			case 'descriptionNewConfig':
				objectToSend.current = {
					...objectToSend.current,
					description: e
				}
				setNewConfig({...newConfig, description: e})
				break;	
			case 'saveForNewConfig':
				objectToSend.current = {
					...objectToSend.current,
					saveFor: e,
					isShare: false,
					isDefault: false
				}
				setNewConfig({...newConfig, saveFor: e, isShare: false, isDefault: false})
				break;


			case 'listExistingConfig': {
				const config = availableConfigs.find(obj => obj.olapConfig.id === e),
					{ name, description, id } = config.olapConfig,
					{ jobId, report, reportOlapConfigId, isDefault, isShare, user } = config

				if(user) {
					setExistingConfig(
						{
							...existingConfig, 
							show: true, 
							name, 
							description, 
							jobId, 
							olapConfigId: id, 
							report,
							saveFor: jobId !== null ? 'job' : 'report',
							isShare,
							isDefault
						}
					)
	
					objectToSend.current = {
						type: 'saveExistingConfig',
						name,
						description,
						jobId,
						olapConfigId: id,
						report,
						reportOlapConfigId,
						saveFor: jobId !== null ? 'job' : 'report',
						isShare,
						isDefault
					}
				} else {
					setExistingConfig(
						{
							...existingConfig, 
							show: true, 
							name, 
							description, 
							jobId, 
							olapConfigId: id, 
							report,
							saveFor: 'forEveryone',
							isShare,
							isDefault,
							user
						}
					)
	
					objectToSend.current = {
						type: 'saveExistingConfig',
						name,
						description,
						jobId,
						olapConfigId: id,
						report,
						reportOlapConfigId,
						saveFor: 'forEveryone',
						isShare,
						isDefault
					}
				}

				break;
			}

			case 'nameExistingConfig':
				objectToSend.current = {
					...objectToSend.current,
					name: e
				}
				setExistingConfig({...existingConfig, name: e})
				break;
			case 'descriptionExistingConfig':
				objectToSend.current = {
					...objectToSend.current,
					description: e
				}
				setExistingConfig({...existingConfig, description: e})
				break;
			case 'saveForExistingConfig':
				objectToSend.current = {
					...objectToSend.current,
					saveFor: e,
					isShare: false,
					isDefault: false
				}
				setExistingConfig({...existingConfig, saveFor: e, isShare: false, isDefault: false})
				break;
			default:
				return false
		}	
	};

	const handleChangeSwitcher = (event, action) => {
		const e = event.target.checked;

		switch(action) {
			case 'shareNewConfig':
				objectToSend.current = {
					...objectToSend.current,
					isShare: e,
					isDefault: false
				}
				setNewConfig({...newConfig, isShare: e, isDefault: false})
				break;
			case 'defaultNewConfig':
				objectToSend.current = {
					...objectToSend.current,
					isDefault: e,
					isShare: false
				}
				setNewConfig({...newConfig, isDefault: e, isShare: false})
				break;	
			case 'shareExistingConfig':
				objectToSend.current = {
					...objectToSend.current,
					isShare: e,
					isDefault: false
				}
				setExistingConfig({...existingConfig, isShare: e, isDefault: false})
				break;
			case 'defaultExistingConfig':
				objectToSend.current = {
					...objectToSend.current,
					isDefault: e,
					isShare: false
				}
				setExistingConfig({...existingConfig, isDefault: e, isShare: false})
				break;

			default:
				return false
		}	
	};

  	return (
        <Dialog
            open={props.open}
            PaperComponent={PaperComponent}
            aria-labelledby="drag-title"
        >

            <DialogTitle style={{ cursor: 'move' }} id="drag-title"> Сохранение конфигурации </DialogTitle>

			<div className={classes.FD_root}>

				{/* Main Tabs */}
				<AppBar position="static" color="default">
					<Tabs
						value={mainTabsValue}
						onChange={handleChangeMainTabs}
						indicatorColor="primary"
						textColor="primary"
						variant="scrollable"
						scrollButtons="auto"
						aria-label="mainTabs"
					>
						<Tab label="Новая конфигурация" />
						<Tab label="Существующая конфигурация" />
					</Tabs>
				</AppBar>
				
				{/* Новая конфигурация */}
				<TabPanel value={mainTabsValue} index={0}>
					<TextField
						required
						error={ newConfig.name.replace(/\s/g,"") === "" ? true : false }
						id="newConfigName"
						label="Название"
						placeholder="Введите название конфигурации"
						className={classes.CSD_nameField}
						InputLabelProps={{
							shrink: true,
						}}
						variant="outlined"
						value={newConfig.name}
						onChange={(event) => handleChange(event, 'nameNewConfig')}
					/>
					<TextField
						id="newConfigDescription"
						label="Описание"
						placeholder="Введите описание к конфигурации"
						multiline
						rows={5}
						className={classes.CSD_descriptionField}
						InputLabelProps={{
							shrink: true,
						}}
						variant="outlined"
						value={newConfig.description}
						onChange={(event) => handleChange(event, 'descriptionNewConfig')}
					/>
					<Box className={classes.CSD_actionBtns}>
						<Box className={classes.CSD_wrapperSaveFor}>
							<Typography id="newConfigSaveForLabel" className={classes.CSD_saveFor}>Сохранить для:</Typography>
							<Select
								labelId="newConfigSaveForLabel"
								id="newConfigSaveFor"
								value={newConfig.saveFor}
								onChange={(event) => handleChange(event, 'saveForNewConfig')}
							>
								<MenuItem value="report"> отчёт </MenuItem>
								<MenuItem value="job"> задание </MenuItem>
								{ props.isReportDeveloper && <MenuItem value="forEveryone"> для всех </MenuItem>}
							</Select>
						</Box>
						<FormControlLabel
							style={{margin: '0'}}
							label={
								<Typography style={{fontSize: '14px'}}> { newConfig.saveFor === "job" ? 'Общий доступ' : 'Сделать по умолчанию' }</Typography>	
							}
							labelPlacement="top"
							control = { 
								(
									newConfig.saveFor === "job" ?
										<Switch checked={newConfig.isShare} onChange={(e) => handleChangeSwitcher(e, 'shareNewConfig')} name="shareNewConfig" size="small"/> 
									:
										<Switch checked={newConfig.isDefault} onChange={(e) => handleChangeSwitcher(e, 'defaultNewConfig')} name="defaultNewConfig" size="small"/> 
								)
							}
						/>
					</Box>
				</TabPanel>

				{/* Существующая конфигурация */}
				<TabPanel value={mainTabsValue} index={1}>
					{
						availableConfigs.length > 0 ?
						<>
							<Box className={classes.CSD_wrapperList}>
								<FormControl variant="outlined" className={classes.CSD_formControl}>
									<InputLabel id="selectExistingConfigLabel">Список существующих конфигураций</InputLabel>
									<Select
										labelId="selectExistingConfigLabel"
										id="selectExistingConfig"
										label="Список существующих конфигураций"
										value={existingConfig.olapConfigId}
										onChange={(event) => handleChange(event, 'listExistingConfig')}
									>
										{ availableConfigs.map( item => {
											return (
												<MenuItem 
													key={item.olapConfig.id}
													value={item.olapConfig.id}
												>
													{item.olapConfig.name}
												</MenuItem>
											)
										})}
									</Select>
								</FormControl>
							</Box>

							{ existingConfig.show &&  
								<>
									<TextField
										required
										error={ existingConfig.name.replace(/\s/g,"") === "" ? true : false }
										id="existingConfigName"
										label="Название"
										placeholder="Введите название конфигурации"
										className={classes.CSD_nameFieldExisting}
										InputLabelProps={{
											shrink: true,
										}}
										variant="outlined"
										value={ existingConfig.name}
										onChange={(event) => handleChange(event, 'nameExistingConfig')}
									/>
									<TextField
										id="existingConfigDescription"
										label="Описание"
										placeholder="Введите описание к конфигурации"
										multiline
										rows={5}
										className={classes.CSD_descriptionField}
										InputLabelProps={{
											shrink: true,
										}}
										variant="outlined"
										value={existingConfig.description}
										onChange={(event) => handleChange(event, 'descriptionExistingConfig')}
									/>
									<Box className={classes.CSD_actionBtns}>
										<Box className={classes.CSD_wrapperSaveFor}>
											<Typography id="existingConfigSaveForLabel" className={classes.CSD_saveFor}>Сохранить для:</Typography>
											<Select
												labelId="existingConfigSaveForLabel"
												id="existingConfigSaveFor"
												value={existingConfig.saveFor}
												onChange={(event) => handleChange(event, 'saveForExistingConfig')}
											>
												<MenuItem value="report">отчёт</MenuItem>
												<MenuItem value="job">задание</MenuItem>
												{ props.isReportDeveloper && <MenuItem value="forEveryone"> для всех </MenuItem>}
											</Select>
										</Box>
										<FormControlLabel
											style={{margin: '0'}}
											label={
												<Typography style={{fontSize: '14px'}}> { existingConfig.saveFor === "job" ? 'Общий доступ' : 'Сделать по умолчанию' }</Typography>	
											}
											labelPlacement="top"
											control = { 
												(
													existingConfig.saveFor === "job" ?
														<Switch checked={existingConfig.isShare} onChange={(e) => handleChangeSwitcher(e, 'shareExistingConfig')} name="shareExistingConfig" size="small"/> 
													:
														<Switch checked={existingConfig.isDefault} onChange={(e) => handleChangeSwitcher(e, 'defaultExistingConfig')} name="defaultExistingConfig" size="small"/> 
												)
											}
										/>
									</Box>
								</>
							}
						</>
						:
						<Box className={classes.CD_wrapperList}>
							<Typography>Нет существующих конфигураций</Typography>
						</Box>
					}
				</TabPanel>

			</div>

			<DialogActions>
				<Button 
					color="primary" 
					disabled = { (mainTabsValue === 1 && !existingConfig.show) || objectToSend.current.name.replace(/\s/g,"") === "" ? true : false }
					onClick={() => props.onSave(objectToSend.current)}
				>
					Сохранить
				</Button>
				<Button 
					color="primary" 
					onClick={() => props.onCancel('closeConfigSaveDialog')}
				>
					Отменить
				</Button>
			</DialogActions>

      </Dialog>
  );
}