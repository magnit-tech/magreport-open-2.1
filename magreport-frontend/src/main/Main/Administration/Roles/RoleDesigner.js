import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { useSnackbar } from 'notistack';
import { useParams, useNavigate, useLocation } from 'react-router-dom'
// components
import { CircularProgress } from '@material-ui/core';
import Paper from '@material-ui/core/Paper';
// local
import DesignerPage from '../../Development/Designer/DesignerPage';
import PageTabs from 'components/PageTabs/PageTabs';
import DesignerTextField from '../../Development/Designer/DesignerTextField';
import UserList from '../Users/UserList';
import DomainGroupList from '../DomainGroups/DomainGroupList';
import {FolderItemTypes} from '../../../../main/FolderContent/FolderItemTypes';
import PermittedFoldersList from './PermittedFoldersList';
import DesignerFolderBrowser from '../../Development/Designer/DesignerFolderBrowser';
// dataHub
import dataHub from 'ajax/DataHub';
import DataLoader from 'main/DataLoader/DataLoader';
// styles 
import { RolesCSS } from "./RolesCSS";
// actions
import { actionUsersLoaded, actionUsersLoadFailed } from 'redux/actions/admin/actionUsers';
import { editItemNavbar, addItemNavbar } from "redux/actions/navbar/actionNavbar";


function RoleDesigner(props){

    const {id, folderId} = useParams()
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        if(!id) {
            dataHub.roleController.getFolder(folderId, handleFoldersLoaded)
        }
    }, []) // eslint-disable-line

    const classes = RolesCSS();
    const { enqueueSnackbar } = useSnackbar();

    const [pageName, setPagename] = useState(id ? "Редактирование роли" : "Создание роли");
    const [uploading, setUploading] = useState(false);
    const [reload, setReload] = useState({ needReload: false });
    const [folderBrowserOpen, setFolderBrowserOpen] = useState(false);
    const [readWrite, setReadWrite] = useState(['READ']);

    const [data, setData] = useState({
        roleName : '',
        roleDescription : ''
    });

    const [domainGroupsData, setDomainGroupsData] = useState({
        domainGroups: []
    });

    const [permittedFolders, setPermittedFolders] = useState({loadedData: {}, namesList: []});
    const [selectedPermittedFolder, setSelectedPermittedFolder] = useState(props.selectedFolderType ?? 'userReportFolders');

    const fieldLabels = {
        roleName : "Название",
        roleDescription : "Описание"
    }

    const [errorField, setErrorField] = useState({});

    let loadFunc;
    let loadParams = [];

    if (id){
        loadFunc = dataHub.roleController.get;
        loadParams = [id];
    }

    /*
        Data loading
    */

    function handleFoldersLoaded({ok, data}) {
        if(ok) {
            props.addItemNavbar('roles', folderId, data.path)
        }
    }

    function enableRoleReload(){
        setReload({needReload: true})
    }    

    function handleDataLoaded(loadedData){
        setData({
            ...data,
            roleName : loadedData.name,
            roleDescription : loadedData.description,
        });
        setPagename("Редактирование роли: " + loadedData.name);
        if (id) {
            props.editItemNavbar('roles', loadedData.name, id, folderId, loadedData.path)
        }
    }

    function handleDomainGroupsDataLoaded(loadedData){
        setDomainGroupsData({
            ...domainGroupsData,
            domainGroups : loadedData.domainGroups,
        });
    }

    function handlePermittedFoldersLoaded(loadedData){
        let namesList = Object.keys(loadedData);
        let fullNamesList = []; //[{id: 'all', value: 'Все'}];
        let n = ''; 
        
        for (let name of namesList){
            n = name.toLowerCase().includes('filtertemplate') ?  'Шаблоны фильтров':
                name.toLowerCase().includes('exceltemplate') ? 'Шаблоны Excel': 
                name.toLowerCase().includes('filterinstance') ? 'Экземпляры фильтров':
                name.toLowerCase().includes('securityfilter') ? 'Фильтры безопасности':
                name.toLowerCase().includes('datasource') ? 'Источники данных':
                name.toLowerCase().includes('dataset') ? 'Наборы данных':
                name.toLowerCase().includes('devreport') ? 'Разработка отчётов':
                name.toLowerCase().includes('userreport') ? 'Отчёты':
                name;

            if (name !==n) {fullNamesList.push({id: name, value: n})};
        }

        setPermittedFolders({...permittedFolders, 
            loadedData: loadedData, 
            namesList:  fullNamesList
        })
    }

    const handleChangeSelectedPermittedFolder = (value) => {
        setSelectedPermittedFolder(value);
    };

    function handleChangeName(newName){
        setData({
            ...data,
            roleName : newName
        });
        setErrorField({
            ...errorField,
            roleName : false
        });
    }

    function handleChangeDescription(newDescription){
        setData({
            ...data,
            roleDescription : newDescription
        });
        setErrorField({
            ...errorField,
            roleDescription : false
        });        
    }

    function handleTabChange(tabId){
    }
    
    /*
        Save and cancel
    */

    function handleSave(){
        
        let errors = {};
        let errorExists = false;
        
        // Проверка корректности заполнения полей
        Object.entries(data)
            .filter( ([fieldName, fieldValue]) => 
                ( fieldName !== "roleId" && (fieldValue === null || (typeof fieldValue === "string" && fieldValue.trim() === "") ) ) )
            .reverse()
            .forEach( ([fieldName, fieldValue]) => 
                {
                    errors[fieldName] = true;
                    enqueueSnackbar("Недопустимо пустое значение в поле " + fieldLabels[fieldName], {variant : "error"});
                    errorExists = true;
                } );
        
        if(errorExists){
            setErrorField(errors);
        }
        else{
            if(!id){
                dataHub.roleController.add(
                    Number(id), 
                    Number(folderId),
                    data.roleName, 
                    data.roleDescription,
                    handleAddEditAnswer);
            }
            else{
                dataHub.roleController.edit(
                    Number(id),
                    Number(folderId),
                    data.roleName, 
                    data.roleDescription,
                    handleAddEditAnswer);
            }
            setUploading(true);
        }
    }

    function handleAddEditAnswer(magrepResponse){
        setUploading(false);
        if(magrepResponse.ok){
            location.state ? navigate(location.state) : navigate(`/ui/roles/${folderId}`)
        }
        else{
            let actionWord = id ? "обновлении" : "создании";
            enqueueSnackbar("Ошибка при " + actionWord + " объекта: " + magrepResponse.data, {variant : "error"});
        }
    }

    function handleAddFolderRead(){
        setFolderBrowserOpen(true);
        setReadWrite(['READ']);
    }

    function handleAddFolderWrite(){
        setFolderBrowserOpen(true);
        setReadWrite(['READ', 'WRITE']);
    }

    function handleCloseFolderBrowser(){
        setFolderBrowserOpen(false)
    }

    let SelectedPermittedFolderType = selectedPermittedFolder.includes("filterTemplateFolders") ? 'FILTER_TEMPLATE' :
        selectedPermittedFolder.includes("excelTemplateFolders") ? 'EXCEL_TEMPLATE' : 
        selectedPermittedFolder.includes("filterInstanceFolders") ? 'FILTER_INSTANCE' :
        selectedPermittedFolder.includes("securityFilterFolders") ? 'SECURITY_FILTER' :
        selectedPermittedFolder.includes("datasourceFolders") ? 'DATASOURCE' :
        selectedPermittedFolder.includes("datasetFolders") ? 'DATASET' :
        selectedPermittedFolder.includes("devReportFolders") ? 'REPORT_FOLDER' :
        selectedPermittedFolder.includes("userReportFolders") ? 'PUBLISHED_REPORT' : 'PUBLISHED_REPORT';

    /* Adding a folder role */
    function handleSubmit(folderId, roleType){
        dataHub.roleController.addPermission(folderId, roleType, id, SelectedPermittedFolderType, handleAddRolePermissionAnswer);
    }
    function handleAddRolePermissionAnswer(magrepResponse){
        if (magrepResponse.ok) {
            handleCloseFolderBrowser();
            enableRoleReload();
            enqueueSnackbar("Роль для папки добавлена!", {variant : "success"});
        }
        else {
            enqueueSnackbar("Не удалось добавить роль для папки", {variant : "error"});
        }
    }

    /* Deleting a folder role */
    function handleDeleteRolePermission(folderId){
        dataHub.roleController.deletePermission(folderId, id, SelectedPermittedFolderType, handleDeleteRolePermissionAnswer);
    }
    function handleDeleteRolePermissionAnswer(magrepResponse){
        if (magrepResponse.ok) {
            enableRoleReload();
            enqueueSnackbar("Роль с папки удалена!", {variant : "success"});
        }
        else {
            enqueueSnackbar("Не удалось удалить роль с папки", {variant : "error"});
        }
    }

    let tabs = []    

    tabs.push({tablabel:"Настройки",
        tabcontent:
        <DesignerPage 
            onSaveClick={handleSave}
            onCancelClick={() => location.state ? navigate(location.state) : navigate(`/ui/roles/${folderId}`)}
        >
            <DesignerTextField
                label = {fieldLabels.roleName}
                value = {data.roleName}
                onChange = {handleChangeName}
                displayBlock
                fullWidth
                error = {errorField.roleName}
                disabled = {props.folderName === 'SYSTEM'}
            />
            <DesignerTextField
                label = {fieldLabels.roleDescription}
                value = {data.roleDescription}
                onChange = {handleChangeDescription}
                displayBlock
                fullWidth
                error = {errorField.roleDescription}
            />         
        </DesignerPage>   
    });

    tabs.push({tablabel:"Пользователи",
        tabdisabled: id === null || id === undefined ? true : false,
        tabcontent:
        <div style={{display: 'flex', flex: 1, flexDirection: 'column'}}>
            <Paper elevation={3} className={classes.userListPaper}>
                <DataLoader
                    loadFunc = {dataHub.roleController.getUsers}
                    loadParams = {[id]}
                    reload = {reload}
                    onDataLoaded = {data => props.actionUsersLoaded(data, FolderItemTypes.roles)}
                    onDataLoadFailed = {props.actionUsersLoadFailed}
                >
                    <UserList
                        itemsType={FolderItemTypes.roles}
                        items={props.users.data}
                        showDeleteButton={true}
                        roleId={id}
                        onNeedReload={enableRoleReload}
                    />
                </DataLoader>
            </Paper>
        </div>
    });

    tabs.push({tablabel:"Доменные группы",
        tabdisabled: id === null || id === undefined ? true : false,
        tabcontent:
        <div style={{display: 'flex', flex: 1, flexDirection: 'column'}}>
            <Paper elevation={3} className={classes.userListPaper}>
                <DataLoader
                    loadFunc = {dataHub.roleController.getDomainGroups}
                    loadParams = {[id]}
                    reload = {reload}
                    onDataLoaded = {handleDomainGroupsDataLoaded}
                    onDataLoadFailed = {(message) => enqueueSnackbar(`При получении доменных групп из AD произошла ошибка: ${message}`, {variant: "error"})}
                >
                    <DomainGroupList
                        itemsType={FolderItemTypes.roles}
                        items={domainGroupsData.domainGroups}
                        showDeleteButton={true}
                        roleId={id}
                        onNeedReload={enableRoleReload}
                    />
                </DataLoader>
            </Paper>
        </div>
    });

    let FolderItemType = selectedPermittedFolder.toLowerCase().includes('filtertemplate') ?  FolderItemTypes.filterTemplate:
        selectedPermittedFolder.toLowerCase().includes('exceltemplate') ? FolderItemTypes.excelTemplates: 
        selectedPermittedFolder.toLowerCase().includes('filterinstance') ? FolderItemTypes.filterInstance:
        selectedPermittedFolder.toLowerCase().includes('securityfilter') ? FolderItemTypes.securityFilters:
        selectedPermittedFolder.toLowerCase().includes('datasource') ? FolderItemTypes.datasource:
        selectedPermittedFolder.toLowerCase().includes('dataset') ? FolderItemTypes.dataset:
        selectedPermittedFolder.toLowerCase().includes('devreport') ? FolderItemTypes.reportsDev:
        selectedPermittedFolder.toLowerCase().includes('userreport') ? FolderItemTypes.reports:
        FolderItemTypes.reports;

    if (Number(folderId) === 2 /*FOLDER_ROLES*/){
    tabs.push({tablabel:"Права",
        tabdisabled: id === null || id === undefined ? true : false,
        tabcontent:
        <div style={{display: 'flex', flex: 1, flexDirection: 'column'}}>
            <div>
                {uploading ? <div className={classes.container}><CircularProgress/></div> :
                    <div className={classes.container}>
                        { FolderItemType !== FolderItemTypes.excelTemplates &&
                            <DesignerFolderBrowser 
                                dialogOpen={folderBrowserOpen}
                                itemType={FolderItemType}
                                defaultFolderId = {null}
                                onSubmit={(folderId) => handleSubmit(folderId, readWrite)}
                                onClose={handleCloseFolderBrowser}
                                sortParams = {props.sortParams}
                            />
                        }
                    </div>
                }
            </div>
            <Paper elevation={3} className={classes.userListPaper}>
                <DataLoader
                    loadFunc = {dataHub.roleController.getPertmittedFolders}
                    loadParams = {[id]}
                    reload = {reload}
                    onDataLoaded = {handlePermittedFoldersLoaded}
                    onDataLoadFailed = {(message) => enqueueSnackbar(`При получении списка каталогов, к которым роль имеет доступ, возникла ошибка: ${message}`, {variant: "error"})}
                >
                    <PermittedFoldersList
                        selectedItem={selectedPermittedFolder}
                        items={permittedFolders}
                        editable = {true}
                        onChange={(folderId, roleType) => handleSubmit(folderId, roleType)}
                        onDelete={(folderId) => handleDeleteRolePermission(folderId)}
                        onChangeSelectedPermittedFolder = {handleChangeSelectedPermittedFolder}
                        onAddFolderRead = {handleAddFolderRead}
                        onAddFolderWrite = {handleAddFolderWrite}
                    />
                </DataLoader>
            </Paper>
        </div>
    })};

    return(
        <DataLoader
            loadFunc = {loadFunc}
            loadParams = {loadParams}
            onDataLoaded = {handleDataLoaded}
            onDataLoadFailed = {(message) => enqueueSnackbar(`При загрузке информации о роли произошла ошибка: ${message}`, {variant: "error"})}
        >
            {uploading ? <CircularProgress/> :
                <div className={classes.rel}> 
                    <div className={classes.abs}>
                        <PageTabs
                            tabsdata={tabs} 
                            onTabChange={handleTabChange}
                            pageName={pageName}
                        />
                    </div>
                </div>
            }
        </DataLoader>
    );
}

const mapStateToProps = state => {
    return {
        users: state.users,
        selectedFolderType: state.roles.selectedFolderType
    }
}

const mapDispatchToProps = {
    actionUsersLoaded, 
    actionUsersLoadFailed,
    editItemNavbar, 
    addItemNavbar
}

export default connect(mapStateToProps, mapDispatchToProps)(RoleDesigner);
