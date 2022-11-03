import React from 'react';

import { PivotCSS } from '../PivotCSS';

import { FormControlLabel, IconButton, List, ListItem, ListItemSecondaryAction, ListItemText, Switch, Tooltip, Typography } from '@material-ui/core';

//Icon-Component
import Icon from '@mdi/react'

//icons
import { mdiDeleteForever, mdiFileStar } from '@mdi/js';

/**
	* @param {Array} props.items - массив существующих конфигураций, доступных пользователю
	* @param {*} props.confirmDelete - function - callback удаления конфигурации
	* @param {*} props.chooseConfig - function - callback загрузки определенной конфигурации
	* @param {*} props.chooseDefault - function - callback сохранения конфигурации "По умолчанию"
	* @param {*} props.changeGeneralAccess - function - callback сохранения "Общий доступ" у конфигурации
**/


export default function CustomList(props){
	
	const classes = PivotCSS();

	return (
		<List dense component="div" role="list">
			
			{props.items.map((item) => {
				const labelId = `item-${item.olapConfig.id}-label`;
				const options = { year: 'numeric', month: 'numeric', day: 'numeric', hour: 'numeric', minute: 'numeric' };

				return (
					<ListItem 
						button 
						role="listitem"
						key={item.olapConfig.id} 
						className={classes.CL_listItem}
					>

						<ListItemText 
							id={labelId}
							primary={item.olapConfig.name}
							secondary={
								<>
									<Typography
										component="span"
										variant="body2"
									>
										{new Date(item.modified).toLocaleString('ru', options)}
									</Typography>
									{ item.user &&
										<>
										{" — "}
										<Typography
											component="span"
											variant="body2"
										>
											{item.user.name}
										</Typography>
										</>
									}
								</>
							}
							onClick={(event) => props.chooseConfig(event, item)}   
						/>

						{ !props.sharedJobs && 
							<ListItemSecondaryAction>
								<Tooltip title="Удалить" placement='top'>
									<FormControlLabel 
										control = {
											<IconButton size="small" aria-label="delete" >
												<Icon path={mdiDeleteForever} size={1} />
											</IconButton>
										}
										onClick = {(event) => props.confirmDelete(event, item.olapConfig.id, item.olapConfig.name)}
									/>
								</Tooltip>
								{ item.report && 
									<Tooltip title={ item.isDefault ? 'По умолчанию' : 'Сделать по умолчанию' }  placement='top'>
										<FormControlLabel 
											control={
												<IconButton 
													size="small" 
													aria-label="makeDefault" 
													color={item.isDefault ? 'secondary' : 'default'}
													onClick = {() => props.chooseDefault(item)}
												>
													<Icon path={mdiFileStar} size={1} />
												</IconButton>
											}
										/>
									</Tooltip>
								}
								{ !item.report && 
									<FormControlLabel
										style={{margin: '0'}}
										label={
											<Typography style={{fontSize: '14px'}}>Общий доступ</Typography>	
										}
										labelPlacement="top"
										control = { <Switch checked={item.isShare} onChange={(event) => props.changeGeneralAccess(event.target.checked, item)} name="generalAccess" size="small"/> }
									/>
								}
							</ListItemSecondaryAction>
						}

					</ListItem>
				);
			})}
	  	</List>
	);
}