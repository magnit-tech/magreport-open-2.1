import React, { useState } from "react";

import { connect } from 'react-redux';
import {showAlertDialog, hideAlertDialog} from 'redux/actions/UI/actionsAlertDialog';

import dataHub from "ajax/DataHub";
import DataLoader from "main/DataLoader/DataLoader";

import { List, ListItem, ListItemText, ListItemSecondaryAction, IconButton, Typography } from '@material-ui/core';
import DeleteIcon from '@material-ui/icons/Delete';



function DerivedFieldDialogList(props){

	const [allDerivedFieldsList, setAllDerivedFields] = useState([]);
	const [selectedItem, setSelectedItem] = useState('new');

	function handleMetadataLoaded(data){
        setAllDerivedFields(data)
		console.log(data);
    }

	function handleChooseDerivedField(field) {
		if(field) {
			setSelectedItem(field.id)
			props.onChoose({
				fieldName: field.name,
				fieldDesc: field.description
			})
		} else {
			setSelectedItem('new')
			props.onChoose(null)
		}

	}

	function handleDeleteAnswer(id, answer){
        if (answer) props.onDelete(id)
        props.hideAlertDialog()
    }

	return (
		<div style={{ width: '300px', background: 'white', margin: '20px', borderRadius: '8px'}}>
			<DataLoader
				loadFunc = {dataHub.derivedFieldController.getAllDerivedFields}
				loadParams = {[props.reportId]}
				onDataLoaded = {handleMetadataLoaded}
				
			>	
				<List >
					<ListItem button key={'NewField'} selected={selectedItem === 'new'} onClick={() => handleChooseDerivedField(null)}>
						<ListItemText
							primary='Создать новое поле'
							style={{ textAlign: 'center' }}
						/>
					</ListItem>
					{allDerivedFieldsList.map(field => {
						const options = { year: 'numeric', month: 'numeric', day: 'numeric', hour: 'numeric', minute: 'numeric' };
						return (
							<ListItem button key={field.id} selected={selectedItem === field.id} onClick={() => handleChooseDerivedField(field)}>
								<ListItemText
									primary={field.name}
									secondary={
										<>
											<Typography
												component="span"
												variant="body2"
											>
												{new Date(field.modified).toLocaleString('ru', options)}
											</Typography>
											{ field.userName &&
												<>
												{" — "}
												<Typography
													component="span"
													variant="body2"
												>
													{field.userName}
												</Typography>
												</>
											}
										</>
									}
								/>
								<ListItemSecondaryAction onClick={() => props.showAlertDialog(`Вы действительно хотите удалить производное поле "${field.id}"?`, null, null, answer => handleDeleteAnswer(field.id, answer))}>
									<IconButton edge="end" aria-label="delete">
										<DeleteIcon />
									</IconButton>
								</ListItemSecondaryAction>
							</ListItem>
						)
					})}

				</List>
			</DataLoader>
		</div>

	)
}

const mapDispatchToProps = {
    showAlertDialog, 
    hideAlertDialog,
}

export default connect(null, mapDispatchToProps)(DerivedFieldDialogList)