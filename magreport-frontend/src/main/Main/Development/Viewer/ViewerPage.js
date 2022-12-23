import React from 'react'

import {ViewerCSS} from './ViewerCSS';

import { useNavigate, useLocation } from 'react-router-dom'

import clsx from 'clsx';

import Button from '@material-ui/core/Button';
import AppBar from '@material-ui/core/AppBar';
import Paper from '@material-ui/core/Paper';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';


/**
 * @callback onOkClick
 *
 */


/**
 * Компонент для просмотра содержания объектов
 * @param {Object} props - параметры компонента
 * @param {String} [props.pageName=""] - опционально. Имя страницы
 * @param {Number} props.id - ID объекта
 * @param {String} props.itemType - тип объекта из FolderItemTypes
 * @param {String} props.folderId - id папки в которой размещается объект
 * @param {Array} props.children - вложенные компоненты
 * @param {boolean} [props.disabledPadding=false] - опционально. true - отключает отступы внутрь контейнера
 * @param {boolean} [props.readOnly=false] - опционально. true - компонент только для чтения, скрывается кнопка "Редактировать"
 * @param {onOkClick} props.onOkClick - callback, вызываемый при нажатии кнопки "ОК"
 * @returns {JSX.Element}
 * @constructor
 */

 export default function ViewerPage(props) {

    const navigate = useNavigate()
    const location = useLocation();

    const classes = ViewerCSS();

    function onEditClick() {
        navigate(`/ui/${props.itemType}/${props.folderId ? props.folderId + '/' : ''}edit/${props.id}`, {state: location.pathname})
    }

    return (
        <div className={classes.viewerPage}>
            {props.pageName ?
                <AppBar position="static" className={classes.pageTitle} >
                    <Toolbar variant="dense" >
                        <Typography variant="h6">
                            {props.pageName}
                        </Typography>
                    </Toolbar>
                </AppBar>
                :
                ""
            }
            <div className={classes.viewerPageChildren}>
                <div
                    className={clsx(classes.viewerPageAbsolute, {[classes.viewerPageWOPadding]: props.disabledPadding})}>
                    <div className={classes.viewerPage}>
                        {props.children}
                    </div>
                </div>
            </div>
            <Paper elevation={3} className={classes.pageBtnPanel}>
                {
                    !props.readOnly ?
                        <Button
                            className={classes.pageBtn}
                            type="submit"
                            variant="contained"
                            size="small"
                            color="primary"
                            onClick={onEditClick}
                        >
                            Редактировать
                        </Button>
                        : ""
                }
                <Button
                    className={classes.pageBtn}
                    type="submit"
                    variant="contained"
                    size="small"
                    color="primary"
                    onClick={props.onOkClick}
                >
                    Отменить
                </Button>
            </Paper>
        </div>
    );
}

