import React from "react";

import { ViewerCSS } from './ViewerCSS';

import { Link } from "react-router-dom";


import Card from "@material-ui/core/Card";
import CardContent from "@material-ui/core/CardContent";
import CardHeader from "@material-ui/core/CardHeader";
import Typography from "@material-ui/core/Typography";
import IconButton from "@material-ui/core/IconButton";
import Table from "@material-ui/core/Table";
import TableCell from "@material-ui/core/TableCell";
import TableRow from "@material-ui/core/TableRow";
import {Pageview} from "@material-ui/icons"
import TableBody from '@material-ui/core/TableBody';

import {folderItemTypeName} from "main/FolderContent/FolderItemTypes";

/**
 * Карточка, отображающая дочерний объект в просмотрщике объектов
 * @param {Object} props - параметры компонента
 * @param {Number} props.id - ID объекта
 * @param {String} props.itemType - тип объекта из FolderItemTypes
 * @param {String} props.name - имя объекта (отображается в заголовке)
 * @param {Object} props.extraData - дополнительные данные, которые нужно отобразить на карточке
 * @param {Array} props.children - вложенные элементы
 * @return {JSX.Element}
 * @constructor
 */

export default function ViewerChildCard(props) {

    const classes = ViewerCSS();

    //TODO: alternative extra data handling and display?
    const itemTypeName = folderItemTypeName(props.itemType);
    const extraRows = [];
    const extraData = props.extraData || {};
    for (let extraKey in extraData) {
        if (!extraData.hasOwnProperty(extraKey))
            continue;
        if (extraKey.toLowerCase() === "id")
            continue;
        const extraValue = extraData[extraKey];
        extraRows.push(
            <TableRow key={extraKey}>
                <TableCell>
                    <Typography variant="caption">{extraKey}: {extraValue}</Typography>
                </TableCell>
            </TableRow>);
    }

    return (
        <Card
            className={classes.field}
        >
        <CardHeader
            title={props.name || ""}
            subheader={itemTypeName}
            action={
                <Link 
                    to={`/ui/${props.itemType}${props.parentFolderId ? `/${props.parentFolderId}/view/${props.id}` : `/${props.id}`}`} 
                    target="_blank" 
                    rel="noopener noreferrer"
                >
                    <IconButton aria-label="viewItem">
                        <Pageview/>
                    </IconButton>
                </Link>
            }
        />
        <CardContent>
            <Table size="small">
                <TableBody>
                    <TableRow key="id">
                        <TableCell>
                            <Typography variant="caption">id: {props.id}</Typography>
                        </TableCell>
                    </TableRow>
                {extraRows}
                </TableBody>
            </Table>
            {props.children}
        </CardContent>
    </Card>)
}