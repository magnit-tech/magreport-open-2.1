import React from 'react';
import clsx from 'clsx';
import { connect } from 'react-redux';
import {FolderItemTypes} from  '../FolderItemTypes';
//redux



import {hideJobDialog} from 'redux/actions/jobs/actionJobs';

// components
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';

import {ItemCardDialogViewerCSS} from './ModalWindowsCSS';
/**
 * 
 * @param {*} props.id - id задания
 * @param {*} props.open - признак открытия окна
 * @param {*} props.sqlQuery - Текст запроса
 * @param {*} props.onCLose - функция при закрытии окна
 */
function ItemCardDialogViewer(props){

    const classes = ItemCardDialogViewerCSS();
    const options = { year: 'numeric', month: 'numeric', day: 'numeric', hour: 'numeric', minute: 'numeric', second: 'numeric'};

    const handleClose = event => {
        event.stopPropagation()
        props.hideJobDialog(props.itemsType)
    }

    return (
        <div>
        {(props.itemsType === FolderItemTypes.job || props.itemsType === FolderItemTypes.userJobs) &&
            <Dialog
                maxWidth = {props.data.sqlQuery ? 'md' : false}
                open={props.open}
                onClose={handleClose}
                aria-labelledby="SQL-dialog-title"
                aria-describedby="SQL-dialog-description"
            >
                <DialogTitle id="SQL-dialog-title">{props.titleName}</DialogTitle>
                <DialogContent className = {clsx({[classes.flx]: props.data.history})}>
               
                    {    props.data.sqlQuery ?
                        <DialogContentText id="SQL-dialog-description">  {props.data.sqlQuery} </DialogContentText>
                        :
						<TableContainer>
							<Table stickyHeader size="small">
								<TableHead>
								    <TableRow>
									    <TableCell>Статус</TableCell>
									    <TableCell align="right">Состояние</TableCell>
									    <TableCell align="right">Дата/время</TableCell>
								    </TableRow>
								</TableHead>
								<TableBody>
								    {props.data.history?.map((row) => (
									    <TableRow key={row.status}>
									        <TableCell component="th" scope="row">
										        {row.status}
									    </TableCell>
									    <TableCell align="right">{row.state}</TableCell>
									    <TableCell align="right">{new Date(row.created).toLocaleString('ru', options)}</TableCell>
									    </TableRow>
								    ))}
								</TableBody>
							</Table>
						</TableContainer>
                    }
                
                </DialogContent>
                <DialogActions>
                    <Button className={classes.closeBtn}
                        color="primary"
                        autoFocus
                        type="submit"
                        variant="contained"
                        size="small"
                        onClick={handleClose}
                    >
                        Закрыть
                    </Button>
                </DialogActions>
            </Dialog>
        }
    </div>
    )
}

const mapStateToProps = state => {
    return {
        open : state.jobDialog.open,
        itemsType: state.jobDialog.itemsType,
        data: state.jobDialog.data,
        titleName: state.jobDialog.titleName
        
    }
}

const mapDispatchToProps = {
    hideJobDialog
}

export default connect(mapStateToProps, mapDispatchToProps)(ItemCardDialogViewer);