import React, { useState, useRef, useEffect } from 'react';
import { connect } from 'react-redux';

import { PivotCSS } from '../PivotCSS';

import Draggable from 'react-draggable';

import { Paper, Dialog, DialogTitle, DialogActions, Button, Box, Typography} from '@material-ui/core';

import CustomList from './CustomList';

//actions
import { showAlertDialog, hideAlertDialog } from 'redux/actions/UI/actionsAlertDialog'

/**
	* @param {Boolean} props.open - boolean-значение отображения модального окна
	* @param {Object} props.configs - объект массивов конфигураций (commonReportConfigs, myJobConfig, myReportConfigs, sharedJobConfig)
	* @param {*} props.onCancel - function - callback отмены/закрытия модального окна
	* @param {*} props.onDelete - function - callback удаления конфигурации
	* @param {*} props.onSave - function - callback сохранения конфигурации
	* @param {*} props.onChooseConfig - function - callback загрузки определенной конфигурации
	* @param {*} props.onMakeDefault - function - callback сохранения конфигурации "По умолчанию"
	* @param {*} props.onChangeGeneralAccess - function - callback сохранения "Общий доступ" у конфигурации
**/

//Перетаскивание модального окна
function PaperComponent(props) {
    return (
		<Draggable handle="#drag-title" cancel={'[class*="MuiDialogContent-root"]'}>
			<Paper {...props}/>
		</Draggable>
    );
}


function ConfigDialog(props){

	const classes = PivotCSS();

	const availableConfigs = props.configs

	useEffect(() => {
		let res = []

		availableConfigs.myJobConfig.forEach(element => {
			res.push(element.olapConfig.name)
		});

		setSharedJobConfigs(availableConfigs.sharedJobConfig.filter( shareItem => !res.includes(shareItem.olapConfig.name)))
	}, [availableConfigs] )

	const [sharedJobConfigs, setSharedJobConfigs] = useState([])

	const isHaveAvailableConfigs = useRef(availableConfigs.myJobConfig.length !== 0 || availableConfigs.myReportConfigs.length !== 0 || availableConfigs.sharedJobConfig.length !== 0 || availableConfigs.commonReportConfigs.length !== 0);

	const deleteItem = useRef(null);
	const chosenConfig = useRef(null);

	// Подтверждение удаления конфигурации
	const handleClickDelete = (event, id, name) => {
        event.stopPropagation();
		
		deleteItem.current = {
			id,
			name,
			type: 'ConfigDialog'
		}

        props.showAlertDialog('Удалить конфигурацию "' + name + '" ?', null, null, handleConfirmDelete)
    }
	function handleConfirmDelete(answer){
        if (answer){
            props.onDelete(deleteItem.current)
        }
        props.hideAlertDialog()
    }

	// Подтверждение выбора конфигурации
	const handleClickChooseConfig = (event, item) => {
        event.stopPropagation();

		chosenConfig.current = {
			data: item.olapConfig.data,
			name: item.olapConfig.name,
			reportOlapConfigId: item.reportOlapConfigId
		}

        props.showAlertDialog('Загрузить конфигурацию "' + item.olapConfig.name + '" ?', null, null, handleConfirmChooseConfig)
    }
	function handleConfirmChooseConfig(answer){
        if (answer){
            props.onChooseConfig(chosenConfig.current)
        }
        props.hideAlertDialog()
    }

  	return (
        <Dialog
            open={props.open}
            PaperComponent={PaperComponent}
            aria-labelledby="drag-title"
        >
			<Box style={{ width: isHaveAvailableConfigs.current ? '570px' : '350px' , overflowY: 'auto'}}>
				<DialogTitle style={{ cursor: 'move' }} id="drag-title"> Доступные конфигурации </DialogTitle>

				<div className={classes.CD_root}>
					{ isHaveAvailableConfigs.current? 
						<>
							{
								availableConfigs.myJobConfig.length > 0 &&
								<Box className={classes.CD_wrapper}>
									<Typography className={classes.CD_subtitle}>Конфигурации задания:</Typography>
									<Box style={{ height: availableConfigs.myJobConfig.length < 4 ? 'auto' : '220px', overflowY: 'auto'}}>
										<CustomList 
											items = {availableConfigs.myJobConfig}
											confirmDelete = {(event, id, name) => handleClickDelete(event, id, name)}
											chooseConfig = {(event, item) => handleClickChooseConfig(event, item)}
											changeGeneralAccess = {(event, reportOlapConfigId) => props.onChangeGeneralAccess(event, reportOlapConfigId)}
										/>
									</Box>
								</Box>
							}
							{
								availableConfigs.myReportConfigs.length > 0 &&
								<Box className={classes.CD_wrapper}>
									<Typography className={classes.CD_subtitle}>Конфигурации отчета:</Typography>
									<Box style={{ height: availableConfigs.myReportConfigs.length < 4 ? 'auto' : '220px', overflowY: 'auto'}}>
										<CustomList 
											items = {availableConfigs.myReportConfigs}
											confirmDelete = {(event, id, name) => handleClickDelete(event, id, name)}
											chooseConfig = {(event, item) => handleClickChooseConfig(event, item)}
											chooseDefault = {(item) => props.onMakeDefault(item)}
											changeGeneralAccess = {(event, item) => props.onChangeGeneralAccess(event, item)}
										/>
									</Box>
								</Box>
							}
							{
								(sharedJobConfigs.length !== 0 || availableConfigs.commonReportConfigs.length !== 0) &&
								<Box className={classes.CD_wrapper}>
									<Typography className={classes.CD_subtitle}>Общедоступные конфигурации:</Typography>
									<Box style={{ height: availableConfigs.myReportConfigs.length < 4 ? 'auto' : '220px', overflowY: 'auto'}}>
										{ sharedJobConfigs.length !== 0 && 
											<CustomList 
												sharedJobs = {true}
												items = {sharedJobConfigs}
												confirmDelete = {(event, id, name) => handleClickDelete(event, id, name)}
												chooseConfig = {(event, item) => handleClickChooseConfig(event, item)}
												chooseDefault = {(item) => props.onMakeDefault(item)}
												changeGeneralAccess = {(event, item) => props.onChangeGeneralAccess(event, item)}
											/>
										}
										{ availableConfigs.commonReportConfigs.length !== 0 && 
											<CustomList 
												sharedJobs = {false}
												items = {availableConfigs.commonReportConfigs}
												confirmDelete = {(event, id, name) => handleClickDelete(event, id, name)}
												chooseConfig = {(event, item) => handleClickChooseConfig(event, item)}
												chooseDefault = {(item) => props.onMakeDefault(item)}
												changeGeneralAccess = {(event, item) => props.onChangeGeneralAccess(event, item)}
											/>
										}
									</Box>
								</Box>
							}
						</>
						:
							<Box className={classes.CD_wrapperList}>
								<Typography>Нет доступных конфигураций</Typography>
							</Box>
					}
				</div>

				<DialogActions>
					<Button 
						color="primary" 
						onClick={() =>props.onCancel('closeConfigDialog')}
					>
						Закрыть
					</Button>
				</DialogActions>
			</Box>
      </Dialog>
  );
}

const mapDispatchToProps = {
    showAlertDialog, 
    hideAlertDialog,
}

export default connect(null, mapDispatchToProps)(ConfigDialog);