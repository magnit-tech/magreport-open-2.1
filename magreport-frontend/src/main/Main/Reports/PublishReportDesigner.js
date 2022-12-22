import React, { useState } from 'react';
import { useSnackbar } from 'notistack';

import { useNavigate, useParams } from 'react-router-dom';

// components 
import { CircularProgress } from '@material-ui/core';

// local
import DesignerFolderBrowser from '../Development/Designer/DesignerFolderBrowser'

// dataHub
import dataHub from 'ajax/DataHub';

import {FolderItemTypes} from 'main/FolderContent/FolderItemTypes';

// styles
import { PublishReportDesignerCSS } from '../Development/Designer/DesignerCSS'


export default function PublishReportDesigner(){

    const { folderId } = useParams()
    const navigate = useNavigate();

    const classes = PublishReportDesignerCSS();

    const { enqueueSnackbar } = useSnackbar();

    const [dialogOpen, setDialogOpen] = useState(true)

    const [uploading, setUploading] = useState(false);
    
    function handleChange(reportId){
        setUploading(true);
        dataHub.folderController.addReport(folderId, reportId, handleAddAnswer);    
    }

    function handleAddAnswer(magrepResponse){
        setDialogOpen(false)
        setUploading(false);
        if(magrepResponse.ok){
            enqueueSnackbar("Отчет успешно опубликован", {variant : "success"});
        }
        else{
            enqueueSnackbar("Ошибка при публикации объекта: " + magrepResponse.data, {variant : "error"});
        }
        navigate(`/reports/${folderId}`)
    }

    const handleClose = () => {
        setDialogOpen(false)
        navigate(`/reports/${folderId}`)
    }
    
    return(
        <div>
            {uploading ? <div className={classes.container}><CircularProgress/></div> :
            <div className={classes.container}>
                <h3 className={classes.headerColor}>Выберите отчет для публикации</h3>
                <DesignerFolderBrowser 
                    dialogOpen={dialogOpen}
                    itemType={FolderItemTypes.reportsDev}
                    onChange={handleChange}
                    onClose={handleClose}
                />
            </div>
            }
        </div>
    );    

}

