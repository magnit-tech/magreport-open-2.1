import React from 'react';
import Icon from '@mdi/react'
import { mdiDeleteForever, mdiTable, mdiTableMergeCells, mdiTableSplitCell, mdiTableColumn, 
        mdiTableRow, mdiTableHeadersEyeOff, mdiTableHeadersEye, mdiCog, mdiContentSaveCogOutline, mdiShareAll, mdiSort, mdiMicrosoftExcel,
        mdiApplicationArrayOutline  } from '@mdi/js';
import FormGroup from '@material-ui/core/FormGroup';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Tooltip from '@material-ui/core/Tooltip/Tooltip';
import IconButton from '@material-ui/core/IconButton';
import FullscreenIcon from '@material-ui/icons/Fullscreen';
import FullscreenExitIcon from '@material-ui/icons/FullscreenExit';
/**
 * 
 * @param {*} props.columnsMetricPlacement - true/false
 * @param {*} props.onMetricPlacementChange - function(placeColumn)
 * @param {*} props.mergeMode - true/false
 * @param {*} props.onMergeModeChange - function(mergeMode)
 * @param {*} props.onViewTypeChange - function() - callback смена вида с сводной на простую таблицу
 * @param {*} props.onFullScreen - function() - callback полноэкранный режим
 * @param {*} props.fieldsVisibility - видимость панелей с полями
 * @param {*} props.onFieldsVisibility - function() - callback изменить видимость панелей с полями
 * @param {*} props.onConfigDialog - открытие диалогового окна для Config или ConfigSave, при определенном отправленом значение
 * @param {*} props.onShareJobDialog - открытие диалогового окна для ShareJob
 * @param {*} props.showShareToolBtn - отображение кнопки 'Поделиться заданием' (true/false)
 * @param {*} props.onExportToExcel - function() - callback экспорт в Excel
 * @param {*} props.onShowCreateFieldDialogue - function(true | false) действие по нажатию кнопки "Добавить поле"
 * @returns 
 */
export default function PivotTools(props){

    function handleViewTypeChange(){
        props.onViewTypeChange('plain');
    }

    return(
        <FormGroup row style={{justifyContent: 'space-between', margin: '2px 8px 2px 16px'}}>
            <div>
                <Tooltip title={'Очистить всё'}  placement='top'>
                    <FormControlLabel
                        control={
                            <IconButton
                                size="small"
                                aria-label="fields-visibility-off"
                                onClick={() => props.onClearAllOlap()}
                            >
                                <Icon 
                                    path={mdiDeleteForever}
                                    size={1}
                                />      
                            </IconButton>
                        }
                    />
                </Tooltip>
                <Tooltip title={(props.fieldsVisibility ? 'Скрыть ' : 'Показать ') + 'панели с полями'}  placement='top'>
                    <FormControlLabel
                        control={
                            <IconButton
                                size="small"
                                aria-label="fields-visibility-off"
                                onClick={() =>props.onFieldsVisibility(!props.fieldsVisibility)}
                            >
                                <Icon 
                                    path={props.fieldsVisibility ? mdiTableHeadersEyeOff: mdiTableHeadersEye}
                                    size={1}
                                />      
                            </IconButton>
                        }
                    />
                </Tooltip>
                <Tooltip title={props.columnsMetricPlacement ? "Метрики по строкам": "Метрики по столбцам"}  placement='top'>
                    <FormControlLabel
                        control={
                            <IconButton
                                size="small"
                                aria-label="merge"
                                onClick={() => {props.onMetricPlacementChange(!props.columnsMetricPlacement)} }
                            >
                                <Icon 
                                    path={props.columnsMetricPlacement ? mdiTableColumn: mdiTableRow}
                                    size={1}
                                />
                            </IconButton>
                        }
                    />
                </Tooltip>
                <Tooltip title={(props.mergeMode ? 'Разделить ' : 'Слить ') +'одинаковые' } placement='top'>
                    <FormControlLabel
                        control={
                            <IconButton
                                size="small"
                                aria-label="merge"
                                onClick={() => {props.onMergeModeChange(!props.mergeMode)} }
                            >
                                <Icon 
                                    path={props.mergeMode ? mdiTableSplitCell : mdiTableMergeCells}
                                    size={1}
                                />
                            </IconButton>
                        }
                    />
                </Tooltip>
                <Tooltip title="Простая таблица"  placement='top'>
                    <FormControlLabel
                        control={
                            <IconButton
                                size="small"
                                aria-label="plain-table"
                                onClick={handleViewTypeChange}
                            >
                                <Icon path={mdiTable}
                                    size={1}
                                />
                            </IconButton>
                        }
                    />
                </Tooltip>
                <Tooltip title="Конфигурации"  placement='top'>
                    <FormControlLabel
                        control={
                            <IconButton
                                size="small"
                                aria-label="configDialog"
                                onClick={() => {props.onConfigDialog('openConfigDialog')} }
                            >
                                <Icon path={mdiCog}
                                    size={1}
                                />
                            </IconButton>
                        }
                    />
                </Tooltip>
                <Tooltip title="Сохранить конфигурацию"  placement='top'>
                    <FormControlLabel
                        control={
                            <IconButton
                                size="small"
                                aria-label="configSaveDialog"
                                onClick={() => {props.onConfigDialog('openConfigSaveDialog')} }
                            >
                                <Icon path={mdiContentSaveCogOutline}
                                    size={1}
                                />
                            </IconButton>
                        }
                    />
                </Tooltip>
                {props.showShareToolBtn &&
                    <Tooltip title="Поделиться заданием"  placement='top'>
                        <FormControlLabel
                            control={
                                <IconButton
                                    size="small"
                                    aria-label="shareJobDialog"
                                    onClick={() => {props.onShareJobDialog(true)} }
                                >
                                    <Icon path={mdiShareAll}
                                        size={1}
                                    />
                                </IconButton>
                            }
                        />
                    </Tooltip>
                }
                <Tooltip title="Сортировка"  placement='top'>
                    <FormControlLabel
                        control={
                            <IconButton
                                size="small"
                                aria-label="sortingDialog"
                                onClick={() => {props.onSortingDialog(true)} }
                            >
                                <Icon path={mdiSort}
                                    size={1}
                                />
                            </IconButton>
                        }
                    />
                </Tooltip>
                <Tooltip title="Экспорт в Excel"  placement='top'>
                    <FormControlLabel
                        control={
                            <IconButton
                                size="small"
                                aria-label="sortingDialog"
                                onClick={() => {props.onExportToExcel(true)} }
                            >
                                <Icon path={mdiMicrosoftExcel}
                                    size={1}
                                />
                            </IconButton>
                        }
                    />
                </Tooltip>
                <Tooltip title="Производные поля"  placement='top'>
                    <FormControlLabel
                        control={
                            <IconButton
                                size="small"
                                aria-label="sortingDialog"
                                onClick={() => {props.onShowCreateFieldDialogue(true)} }
                            >
                                <Icon path={mdiApplicationArrayOutline }
                                    size={1}
                                />
                            </IconButton>
                        }
                    />
                </Tooltip>
            </div>
            <div>
                { props.fullScreen ?
					<Tooltip title="Закрыть" placement='left'>
						<IconButton
							size="small"
							aria-label="full-screen-exit"
							onClick={() => props.onFullScreen(false)}
						>
							<FullscreenExitIcon/>
						</IconButton>
					</Tooltip>
					:
					<Tooltip title="Полноэкранный режим"  placement='left'>
						<IconButton
							size="small"
							aria-label="full-screen"
							onClick={() => props.onFullScreen(true)}
						>
							<FullscreenIcon/>
						</IconButton>	
					</Tooltip>
                }
            </div>
        </FormGroup>
    )
}