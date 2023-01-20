import React, {useRef} from 'react';
import clsx from 'clsx';
import Measure from 'react-measure';
import { Scrollbars } from 'react-custom-scrollbars'
import Box from '@material-ui/core/Box';
import {Droppable} from 'react-beautiful-dnd';
import Tooltip from '@material-ui/core/Tooltip';
import VisibilityIcon from '@material-ui/icons/Visibility';
import VisibilityOffIcon from '@material-ui/icons/VisibilityOff';

// material ui
import List from '@material-ui/core/List';

// magreport
import PivotField from './PivotField';
import {PivotCSS} from './PivotCSS';
import { IconButton } from '@material-ui/core';

/**
 * @param {*} props.droppableId - list name
 * @param {*} props.fields - fields array
 * @param {*} props.direction - list direction
 * @param {*} props.name - название
 * @param {*} props.onButtonClick - функция при нажатии на кнопку поля
 * @param {*} props.onButtonOffClick - функция при нажатии на кнопку выключения поля
 * @param {*} props.onOnlyUnusedClick - функция для выбора отображения всех полей или только неиспользуемых
 * @param {boolean} props.onlyUnused - показывать только неиспользуемые/все поля
 */

export default function PivotFieldsList(props){

    const styles = PivotCSS();
    const ScrollbarsRef = useRef(null);

    return(
                           
        <Droppable 
            droppableId={props.droppableId} 
            direction = {props.direction}
        >
            {(provided, snapshot) => (
                <List
                    className={clsx({
                        [styles.verticalList] : props.direction === "vertical",
                        [styles.horizontalList] : props.direction === "horizontal",
                        [styles.listDraggingOver] : snapshot.isDraggingOver
                    })}
                    innerRef = {provided.innerRef}
                    {...provided.droppableProps}
                >
                    {(props.droppableId === "unusedFields" || props.droppableId === "allFields" ) &&
                        <Tooltip title = {props.onlyUnused ? 'Показать все поля':'Показать только неиспользуемые'} placement = "left">
                            <IconButton className={styles.allFieldsBtn}
                                onClick = {props.onOnlyUnusedClick}
                            >
                                {props.onlyUnused ? <VisibilityOffIcon style={{width: '18px'}}/> :<VisibilityIcon style={{width: '18px'}}/>}
                            </IconButton>
                        </Tooltip>
                    }
                    <Scrollbars 
                        ref={ScrollbarsRef} 
                        autoHeight = {props.direction === "horizontal" ? true : false} 
                        autoHeightMax = {props.direction === "horizontal" ? 220: '100%'}
                        renderThumbHorizontal={({style, ...props}) => 
                            <div {...props}  style= {{...style,
                                position: 'relative',
                                display: 'block',
                                height: '100%',
                                cursor: 'pointer',
                                borderRadius: 'inherit',
                                backgroundColor: 'lightgrey'}}
                            />
                        }
                        renderThumbVertical={({style, ...props})  => 
                            <div {...props} style= {{...style,
                                position: 'relative',
                                display: 'block',
                                width: '100%',
                                cursor: 'pointer',
                                borderRadius: 'inherit',
                                backgroundColor: 'lightgrey'}}
                            />
                        }
                        style={props.direction === "vertical" ?
                        props.fields.length >0 ? {width: ScrollbarsRef.current?.getScrollWidth()}: {minWidth: '50px'} :null}
                    >
                        {/*Без Measure скролл не реагирует на изменение размера других компонентов*/}
                        <Measure
                            bounds
                            scroll
                            onResize={(contentRect) => {
                                ScrollbarsRef.current && ScrollbarsRef.current.forceUpdate();
                            }}
                        >
                            {({ measureRef }) => {
                            return(
                                <div 
                                    ref={measureRef} 
                                    style={props.direction === "horizontal"
                                        ? {display: 'flex', marginBottom: (ScrollbarsRef.current?.getThumbHorizontalWidth()>0) ?  '8px' : 0}
                                        : { marginRight: props.droppableId === 'filterFields' || props.droppableId === 'metricFields'
                                            ? (ScrollbarsRef.current?.getThumbVerticalHeight()>0) ?  '20px' : '12px'
                                            : '0px'
                                        }
                                    }
                                >
                                    {props.fields.length > 0 ?
                                        props.fields.map((v, ind) => {
                                            return ( 
                                                <PivotField
                                                    key = {v.fieldId + "-" + ind}
                                                    listName = {props.droppableId}
                                                    index = {ind}
                                                    fieldId = {v.fieldId}
                                                    fieldName = {v.fieldName}
                                                    aggFuncName = {v.aggFuncName}
                                                    filter = {v.filter}
                                                    filtered = {v.filtered}
                                                    isOff = {v.isOff}
                                                    original = {v.original}
                                                    onButtonClick = {props.onButtonClick}
                                                    onButtonOffClick = {props.onButtonOffClick}
                                                    onChooseAggForMetric = {(funcName, index) => props.onChooseAggForMetric(funcName, index)}
                                                    onCloseAggModal = {props.onCloseAggModal}
                                                />
                                            )
                                        }) :
                                          
                                        <Box 
                                            fontSize={12} 
                                            fontWeight={"fontWeightMedium"} 
                                            className={clsx({
                                                [styles.panelNameVertical]: props.direction === "vertical",
                                                [styles.panelNameHorizontal]: props.direction === "horizontal",
                                            })}
                                        > 
                                            {props.name}

                                        </Box>
                                    }
                                    {provided.placeholder}
                                </div>)}
                            }
                        </Measure>
                    </Scrollbars>                       
                </List>         
            )}
        </Droppable>
    )
}