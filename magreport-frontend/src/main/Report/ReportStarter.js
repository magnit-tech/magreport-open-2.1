import React, { useMemo, useRef, useState } from 'react';
import { useSnackbar } from 'notistack';

import { useParams, useNavigate, useLocation, useSearchParams } from 'react-router-dom'

import { useDispatch } from 'react-redux';
import { addReportStarterNavbar } from "redux/actions/navbar/actionNavbar";
import SidebarItems from '../Main/Sidebar/SidebarItems';   //'../../   /Main/Sidebar/SidebarItems' ;

// mui
import { Button, IconButton, Tooltip } from '@material-ui/core';
import FileCopyIcon from '@material-ui/icons/FileCopy';

// dataHub
import dataHub from 'ajax/DataHub';

//local
import FilterGroup from './FilterGroup.js';
import DataLoader from 'main/DataLoader/DataLoader';
import ReportJob from './ReportJob.js';
import FilterValues from "./FilterValues";

// styles
import { ReportStarterCSS } from "./ReportCSS";
import { Alert } from '@material-ui/lab';

/**
 * Запуск отчёта
 * @param {*} props.reportId - id отчёта
 * @param {*} props.parameters - фильтры отчета для отчетов на расписании
 * @param {*} props.scheduleTaskId - id задания по расписанию, из которого требуется получить параметры
 * @param {*} props.onDataLoadFunction - функция загрузки отчета
 * @param {*} props.onSave - сохранение (для отчетов на расписании)
 * @param {*} props.onCancel - callback отмены
 * @param {*} props.onSaveScheduleTaskFilterData - для сохранения значений фильтра отчетов на расписании
 */
export default function ReportStarter(props) {
    const classes = ReportStarterCSS();
    const { enqueueSnackbar } = useSnackbar();

    const useParamsId = useParams()?.id
    const id = props.onDataLoadFunction ? props.reportId : Number(useParamsId);

    const navigate = useNavigate();
    const location = useLocation();
    const dispatch = useDispatch();

    const [searchParams, setSearchParams] = useSearchParams(); // eslint-disable-line

    const [flowState, setFlowState] = useState("filters");
    const [reportMetadata, setReportMetadata] = useState({});
    const [reloadReportMetadata, setReloadReportMetadata] = useState({ needReload: false }); // управление перезагрузкой метаданных
    const [reloadRunReport, setReloadRunReport] = useState({ needReload: false }); // управление перезапуском отчёта
    const [toggleClearFilters, setToggleClearFilters] = useState(false); // переключатель очистки фильтра - если изменилось значение, надо очистить фильтры
    const [reportJobId, setReportJobId] = useState(null);
    const [lastParamJobId, setLastParamJobId] = useState(searchParams.get('jobId'));
    // const externalFiltersValue = searchParams.get('externalFiltersValue') ? JSON.parse(searchParams.get('externalFiltersValue')) : null;
    const decodeExternalFiltersValue = decodeURI(searchParams.get('externalFiltersValue'))

    const externalFiltersValue = useMemo(
        () => {
            try {
                JSON.parse(decodeExternalFiltersValue);
            } catch (e) {
                enqueueSnackbar("Передан некороктенный json-объект 'externalFiltersValue' в URL", { variant: "error" });
                return null;
            }
            return JSON.parse(decodeExternalFiltersValue);
        },
        [] // eslint-disable-line
    );

    const lastFilterValues = useRef(new FilterValues()); // Значения параметров предыдущего запуска отчёта

    const filterValues = useRef(new FilterValues()); // Текущий выбор в фильтре

    const addJobParameters = useRef([]);
    const [mandatoryFiltersSet, setMandatoryFiltersSet] = useState(new Set())
    const [mandatoryGroupsMap, setMandatoryGroupsMap] = useState(new Map())
    const [filterToGroupMap, setFilterToGroupMap] = useState(new Map())
    const [excelTemplates, setExcelTemplates] = useState([])
    const [disabledSaveBtn, setDisabledSaveBtn] = useState(false)

    function handleReportMetadataLoaded(data) {
        setExcelTemplates(data.excelTemplates)
        if ((!data.filterGroup || data.filterGroup === null || data.filterGroup.id === null) && !props.woStartButton) {
            handleRun();
        }
        else {
            const idFiltersSet = new Set()
            const groupsMap = new Map()
            const filterToGrpMap = new Map()
            const lastParameters = (props.parameters?.length > 0) ? props.parameters : data.lastParameters

            mandatoryFilters(data.filterGroup, idFiltersSet, groupsMap, filterToGrpMap)
            setMandatoryFiltersSet(idFiltersSet)
            setMandatoryGroupsMap(groupsMap)
            setFilterToGroupMap(filterToGrpMap)

            filterValues.current.buildOnParametersObject(lastParameters, false);
            lastFilterValues.current = new FilterValues();
            lastFilterValues.current.buildOnParametersObject(lastParameters, true);

            for (let p of lastParameters) {
                validateMandatoryGroups({ ...p, validation: "success" }, filterToGrpMap, groupsMap)
            }
        }
        setReportMetadata(data);

        if (props.onSaveScheduleTaskFilterData) {
            props.onSaveScheduleTaskFilterData(filterValues.current.getParameters());
        }

        if (props.checkFilters) {

            const checkMandatoryFiltersResult = filterValues.current.checkMandatoryFilters(mandatoryFiltersSet)
            const checkInvalidValues = filterValues.current.checkInvalidValues()
            const isValidMandatorygroups = checkMandatoryGroups()
            props.checkFilters(checkMandatoryFiltersResult && checkInvalidValues && isValidMandatorygroups)
        }

        const path = new RegExp(SidebarItems.schedule.subItems.scheduleTasks.folderItemType, "g");
        if (location.pathname.match(path) === null) {
            dispatch(addReportStarterNavbar('report/starter', data.name, id))
        }
    }

    function mandatoryFilters(data, filtersSet, groupsMap, filterToGrpMap) {
        mandatoryGroups(data, groupsMap)
        if (data.childGroups) {
            for (let cg of data.childGroups) {
                mandatoryFilters(cg, filtersSet, groupsMap, filterToGrpMap)
            }
        }
        if (data.filters) {
            for (let f of data.filters) {
                if (f.mandatory) {
                    filtersSet.add(f.id)
                }
                filterToGrpMap.set(f.id, data.id)
            }
        }
    }

    function mandatoryGroups(group, groupMap) {
        if (group.mandatory) {
            groupMap.set(group.id, new Set())
        }
    }

    function checkMandatoryGroups() {
        let result = true
        if (mandatoryGroupsMap.size > 0) {
            for (let v of mandatoryGroupsMap.values()) {
                if (v.size === 0) {
                    result = false
                    break
                }
            }
        }
        return result
    }

    function handleRun() {
        const checkMandatoryFiltersResult = filterValues.current.checkMandatoryFilters(mandatoryFiltersSet)
        const checkInvalidValues = filterValues.current.checkInvalidValues()
        const isValidMandatorygroups = checkMandatoryGroups()

        if (checkMandatoryFiltersResult && checkInvalidValues && isValidMandatorygroups) {
            addJobParameters.current = filterValues.current.getParameters();
            setReloadRunReport({ needReload: true });
            setFlowState("reportJob");
        }
        else if (!checkInvalidValues) {
            enqueueSnackbar("В фильтрах заданы некорректные значения, проверьте фильтры.", { variant: "error" });
        }
        else if (!isValidMandatorygroups) {
            enqueueSnackbar("Проверьте корректность заполненных данных в обязательных группах фильтров", { variant: "error" });
        }
        else {
            enqueueSnackbar("Заполните все обязательные фильтры, при этом в иерархиях нельзя выбирать сразу все элементы.", { variant: "error" });
        }
    }

    function handleSaveScheduleTaskFilterData() {
        const checkMandatoryFiltersResult = filterValues.current.checkMandatoryFilters(mandatoryFiltersSet)
        const checkInvalidValues = filterValues.current.checkInvalidValues()
        const isValidMandatorygroups = checkMandatoryGroups()
        if (checkMandatoryFiltersResult && checkInvalidValues && isValidMandatorygroups) {
            addJobParameters.current = filterValues.current.getParameters();
            props.onSave();
        }
        else if (!checkInvalidValues) {
            enqueueSnackbar("В фильтрах заданы некорректные значения, проверьте фильтры.", { variant: "error" });
        }
        else if (!isValidMandatorygroups) {
            enqueueSnackbar("Проверьте корректность заполненных данных в обязательных группах фильтров", { variant: "error" });
        }
        else {
            enqueueSnackbar("Заполните все обязательные фильтры, при этом в иерархиях нельзя выбирать сразу все элементы.", { variant: "error" });
        }
    }

    function handleClearFilters() {
        setToggleClearFilters(!toggleClearFilters);
    }

    function handleCancel() {
        location.state ? navigate(location.state) : navigate('/ui/reports')
    }

    function handleChangeFilterValue(newFilterValue) {
        if (newFilterValue.operationType !== 'IS_IT_SEARCHING') {
            filterValues.current.setFilterValue(newFilterValue);
            validateMandatoryGroups(newFilterValue, filterToGroupMap, mandatoryGroupsMap);
        }
        if (props.onSaveScheduleTaskFilterData) {
            props.onSaveScheduleTaskFilterData(filterValues.current.getParameters());
        }

        if (props.checkFilters) {
            const checkMandatoryFiltersResult = filterValues.current.checkMandatoryFilters(mandatoryFiltersSet);
            const checkInvalidValues = filterValues.current.checkInvalidValues();
            const isValidMandatorygroups = checkMandatoryGroups();
            props.checkFilters(checkMandatoryFiltersResult && checkInvalidValues && isValidMandatorygroups);
            addJobParameters.current = filterValues.current.getParameters();
        }

        let disabledBtn = false;
        for (let v of filterValues.current.values.values()) {
            /*Временная залепа, нужно для ValueList*/
            if (v.hasOwnProperty('validation') && v.validation !== 'success') {
                disabledBtn = true
            }
        }
        setDisabledSaveBtn(disabledBtn)
    }

    function validateMandatoryGroups(filterValue, filterMap, groupMap) {
        if (filterMap.has(filterValue.filterId)) {
            const groupId = filterMap.get(filterValue.filterId)
            const filterIds = groupMap.get(groupId)
            if (filterIds) {
                if (filterValue.parameters.length && filterValue.validation === 'success') {
                    filterIds.add(filterValue.filterId)
                }
                else {
                    filterIds.delete(filterValue.filterId)
                }
                groupMap.set(groupId, filterIds)
                setMandatoryGroupsMap(new Map(groupMap))
            }
        }
    }

    function handleReportStarted(data) {
        navigate(`/ui/report/${data.id}`)

    }

    function handleRestartReportClick(reportId, jobId) {
        setReportJobId(null);
        setLastParamJobId(jobId);
        setReloadReportMetadata({ needReload: true });
        setFlowState("filters");
    }

    function handleCopyExternalFilters() {
        navigator.clipboard.writeText(filterValues.current.getExternalFiltersURL());
        enqueueSnackbar(`URL с заполненными значениями фильтров успешно скопирован в буфер обмен`, {
            variant: 'success',
            autoHideDuration: 1000
        });
    }


    return (
        id
            ?
            flowState === "filters" ?
                <DataLoader
                    loadFunc={props.onDataLoadFunction || dataHub.reportController.get}
                    loadParams={[Number(id), props.scheduleTaskId !== undefined ? props.scheduleTaskId : lastParamJobId]}
                    reload={reloadReportMetadata}
                    onDataLoaded={handleReportMetadataLoaded}
                >
                    {reportMetadata.filterGroup && reportMetadata.filterGroup.id !== null &&
                        <div className={classes.reportStarterRelative}>
                            <div className={classes.reportStarterAbsolute}>
                                <div className={classes.filterRoot}>
                                    <FilterGroup
                                        groupData={reportMetadata.filterGroup}
                                        mandatoryGroups={mandatoryGroupsMap}
                                        externalFiltersValue={externalFiltersValue}
                                        lastFilterValues={lastFilterValues.current}
                                        onChangeFilterValue={handleChangeFilterValue}
                                        toggleClearFilters={toggleClearFilters}
                                    />
                                </div>

                                <div className={classes.buttonContainer}>
                                    {!Boolean(props.woStartButton) ?
                                        <Button variant="contained" color="primary" size="small" className={classes.filterButton} onClick={handleRun} disabled={disabledSaveBtn}> Выполнить </Button>
                                        :
                                        <Button variant="contained" color="primary" size="small" className={classes.filterButton} onClick={handleSaveScheduleTaskFilterData} disabled={disabledSaveBtn}> Сохранить </Button>
                                    }
                                    <Button variant="contained" color="primary" size="small" className={classes.filterButton} onClick={handleClearFilters}> Очистить </Button>
                                    <Button variant="contained" color="primary" size="small" className={classes.filterButton} onClick={handleCancel}> Отменить </Button>
                                    <Tooltip title={'Скопировать URL для данных значений фильтров'}>
                                        <IconButton color="primary" onClick={handleCopyExternalFilters}>
                                            <FileCopyIcon className={classes.iconButton}></FileCopyIcon>
                                        </IconButton>
                                    </Tooltip>
                                </div>

                            </div>
                        </div>
                    }
                </DataLoader>

                : flowState === "reportJob" ?
                    <DataLoader
                        loadFunc={reportJobId === null ? dataHub.reportJobController.add : null}
                        loadParams={[Number(id), addJobParameters.current]}
                        reload={reloadRunReport}
                        onDataLoaded={handleReportStarted}
                    >
                        <ReportJob
                            reportId={Number(id)}
                            jobId={reportJobId}
                            excelTemplates={excelTemplates}
                            onRestartReportClick={handleRestartReportClick}
                        />
                    </DataLoader>

                    : <div>ReportStarter : неизвестное состояние</div>
            :
            <Alert severity="error" className={classes.dataLoaderErrorAlert}>
                {"Ошибка: отсуствует, либо передан некорректный id отчёта"}
            </Alert>
    )

}