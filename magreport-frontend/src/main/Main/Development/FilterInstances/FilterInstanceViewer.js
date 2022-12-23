import React, {useState} from "react";
import {useSnackbar} from "notistack";

import { useParams, useNavigate, useLocation } from 'react-router-dom';

import { useDispatch } from "react-redux";
import { viewItemNavbar } from "redux/actions/navbar/actionNavbar";

import dataHub from "ajax/DataHub";
import DataLoader from "main/DataLoader/DataLoader";

import ViewerPage from "main/Main/Development/Viewer/ViewerPage";

import {createViewerTextFields, createViewerPageName} from "main/Main/Development/Viewer/viewerHelpers";
import ViewerChildCard from "../Viewer/ViewerChildCard";
import ValueListFieldsViewer from "./TypeSpecificFields/ValueListFieldsViewer";
import HierTreeFieldsViewer from "./TypeSpecificFields/HierTreeFieldsViewer";
import RangeFieldsViewer from "./TypeSpecificFields/RangeFieldsViewer";
import DateValueFieldsViewer from "./TypeSpecificFields/DateValueFieldsViewer";
import TokenInputFieldsViewer from "./TypeSpecificFields/TokenInputFieldsViewer";
import UnboundedValueFields from './TypeSpecificFields/UnboundedValueFields';
import TupleListFields from './TypeSpecificFields/TupleListFields';
import {FolderItemTypes} from "../../../FolderContent/FolderItemTypes";


export default function FilterInstanceViewer() {

    const { id, folderId } = useParams()
    const navigate = useNavigate();
    const location = useLocation();

    const dispatch = useDispatch()

    const {enqueueSnackbar} = useSnackbar();

    const [data, setData] = useState({});

    function handleFilterInstanceDataLoaded(loadedData) {
        setData({
            ...data,
            ...loadedData,
        });
        dispatch(viewItemNavbar('filterInstance', loadedData.name, id, folderId, loadedData.path))
    }

    function handleFilterTemplateDataLoaded(loadedData) {
        setData({
            ...data,
            filterTemplate: loadedData,
        });
    }

    function handleDataLoadFailed(message) {
        enqueueSnackbar(`При получении данных возникла ошибка: ${message}`,
            {variant: "error"});
    }

    // build component

    const children = [];

    const detailsData = [
        {label: "Название", value: data.name},
        {label: "Код", value: data.code},
        {label: "Описание", value: data.description},
    ];

    children.push(...createViewerTextFields(detailsData));

    children.push(
        data.filterTemplate ? (
            <ViewerChildCard
                key={data.filterTemplate.id}
                id={data.filterTemplate.id}
                parentFolderId={data.filterTemplate.folderId}
                itemType={FolderItemTypes.filterTemplate}
                name={data.filterTemplate.name}
            />
        ) : ""
    );

    const filterTemplateType = data.filterTemplate ? data.filterTemplate.type.name : "";
    let fieldsComponent = "";
    
    if(filterTemplateType === 'VALUE_LIST') {

        fieldsComponent = 
            <ValueListFieldsViewer
                key={Math.random()}
                filterInstanceData={data}
            />;
    } else if(filterTemplateType === 'HIERARCHY'
        || filterTemplateType === 'HIERARCHY_M2M') {

        fieldsComponent = 
            <HierTreeFieldsViewer
                key={Math.random()}
                filterTemplateType={filterTemplateType}
                filterInstanceData={data}
            />
    } else if(filterTemplateType === 'DATE_RANGE'
        || filterTemplateType === 'RANGE') {

        fieldsComponent = 
            <RangeFieldsViewer
                key={Math.random()}
                filterTemplateType={filterTemplateType}
                filterInstanceData={data}
            />;
    } else if(filterTemplateType === 'DATE_VALUE') {

        fieldsComponent = 
            <DateValueFieldsViewer
                key={Math.random()}
                filterInstanceData={data}
            />;
    } else if( filterTemplateType === 'TOKEN_INPUT') {

        fieldsComponent = 
            <TokenInputFieldsViewer
                key={Math.random()}
                filterInstanceData={data}
            />;
    } else if ( filterTemplateType === 'SINGLE_VALUE_UNBOUNDED'){
        fieldsComponent = 
            <UnboundedValueFields
                key={Math.random()}    
                readOnly = {true}
                fields={data.fields}
            />;
    } else if ( filterTemplateType === 'TUPLE_LIST'){
        fieldsComponent = 
            <TupleListFields
                key={Math.random()}    
                readOnly = {true}
                fields={data.fields}
            />;
    }
    children.push(fieldsComponent);

    // edit check
    let userInfo = dataHub.localCache.getUserInfo();

    let isAdmin = userInfo.isAdmin
    let isDeveloper = userInfo.isDeveloper
    let authority = data.authority

    let hasRWRight = isAdmin || (isDeveloper && authority === "WRITE");

    return (
        <DataLoader
            loadFunc={dataHub.filterInstanceController.get}
            loadParams={[id]}
            onDataLoaded={handleFilterInstanceDataLoaded}
            onDataLoadFailed={handleDataLoadFailed}
        >
            <DataLoader
                loadFunc={dataHub.filterTemplateController.get}
                loadParams={[data.templateId]}
                onDataLoaded={handleFilterTemplateDataLoaded}
                onDataLoadFailed={handleDataLoadFailed}
            >
                <ViewerPage
                    pageName={createViewerPageName(FolderItemTypes.filterInstance, data.name)}
                    id={data.id}
                    folderId = {folderId}
                    itemType={FolderItemTypes.filterInstance}
                    onOkClick={() => location.state ? navigate(location.state) : navigate(`/ui/filterInstance/${folderId}`)}
                    readOnly={!hasRWRight}
                >
                    {children}
                </ViewerPage>
            </DataLoader>
        </DataLoader>
    );
}
