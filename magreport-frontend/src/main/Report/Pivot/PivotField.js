import React, { useRef } from 'react';
import { connect } from 'react-redux';

import { Draggable} from 'react-beautiful-dnd';

// icons
import Icon from '@mdi/react';
import { mdiFilter}  from '@mdi/js';
import { mdiPencil } from '@mdi/js';

import clsx from 'clsx';

import Popover from '@material-ui/core/Popover';
import ListItem from '@material-ui/core/ListItem';
import Box from '@material-ui/core/Box';
import ListItemText from '@material-ui/core/ListItemText';
import IconButton from '@material-ui/core/IconButton';

//local
import {AggFunc} from '../../FolderContent/JobFilters/JobStatuses';
import {PivotCSS} from './PivotCSS';
import { List, Modal } from '@material-ui/core';

/**
 * @param {*} props.listName - список, в котором размещено поле
 * @param {*} props.fieldId - id поля
 * @param {*} props.index - индекс поля в списке поля
 * @param {*} props.fieldName - имя поля
 * @param {*} props.aggFuncName - имя агрегирующей функции (для метрик)
 * @param {*} props.filter - фильтр на поле
 * @param {*} props.filtered - признак наличия фильтрации по полю
 * @param {*} props.isOff - признак включения/выключения поля
 * @param {*} props.onButtonClick - функция вызывается при начатии на кнопку
 * @param {*} props.onButtonOffClick - функция вызывается при начатии на кнопку выключения
 * @returns 
 */

function PivotField(props){

    const {open, index, dataType} = props.aggModalParams

    const styles = PivotCSS();

    const [anchorEl, setAnchorEl] = React.useState(null);

    const parentRef = useRef();
    const rectCordinate = (props.listName === 'metricFields' && parentRef.current) ? parentRef.current.getBoundingClientRect() : ''


    const handlePopoverOpen = (event) => {
        setAnchorEl(event.currentTarget);
    };

    const handlePopoverClose = () => {
        setAnchorEl(null);
    };

    const openPopover = Boolean(anchorEl);

    const handleClick=(e) => {  
        props.onButtonClick(e, props.index);
    }

    const handleOffClick=(e) => {
        e.preventDefault(); 
        e.stopPropagation();
        props.onButtonOffClick(e, props.index);
    }

    // Закрытие модального окна без выбора метрики
    function handleAggModalClose(){
        props.onCloseAggModal()
    }
    // Выбор определенной агригирующей функции
    function handleChooseAggForMetric(id, index){
        props.onChooseAggForMetric(id, index)
    }

    // модальное окно выбора агрегирующей функции
    function AggFuncList(props){
        
        const AggFunc = [
            {id:'SUM', name: 'Сумма', type: ['INTEGER', 'DOUBLE'] }, 
            {id: 'COUNT',  name: 'Количество', type: ['STRING', 'INTEGER', 'DOUBLE', 'DATE', 'TIMESTAMP']}, 
            {id: 'COUNT_DISTINCT',  name: 'Кол-во уникальных', type: ['STRING', 'INTEGER', 'DOUBLE', 'DATE', 'TIMESTAMP']}, 
            {id: 'MIN',  name: 'Мин.', type: ['STRING', 'INTEGER', 'DOUBLE', 'DATE', 'TIMESTAMP']}, 
            {id: 'MAX',  name: 'Макс.', type: ['STRING', 'INTEGER', 'DOUBLE', 'DATE', 'TIMESTAMP']}, 
            {id: 'AVG',  name: 'Средн.', type: ['INTEGER', 'DOUBLE']}
        ];

        const {x, y} = rectCordinate

        const setAggModalStyle = {
            top: y,
            left: x,
            transform: `translate(30%, 20%)`,
        }

        let aggFunc = [];
        for (let f of AggFunc.filter(i=> i.type.find( item => item === props.dataType))){
            aggFunc.push(
                <ListItem 
                    key={f.id}
                    disableGutters 
                    dense 
                    button 
                    onClick = {() => handleChooseAggForMetric(f.id, props.index)}
                >
                    <Box 
                        fontSize={12} 
                        fontWeight={"fontWeightMedium"}
                    >
                        {f.name}
                    </Box>
                </ListItem>
            )
        }
        
        return (
            <Modal
                aria-labelledby="aggFuncList"
                open = {props.open}
                onClose = {handleAggModalClose}
            >
                <div style={setAggModalStyle} className = {styles.modal}>
                    <List>
                        {aggFunc}
                    </List>
                </div>
            </Modal>
        );
    }

    return(
        <Draggable draggableId={props.listName + "-" + props.fieldId.toString() + "-" + props.index} index={props.index}>
            {(provided, snapshot)=>(
                <ListItem
                    //button
                    className={clsx({
                        [styles.offField] : props.isOff,
                        [styles.field] : !props.isOff,
                        [styles.draggingField] : snapshot.isDragging
                    })}
                    {...provided.draggableProps}
                    {...provided.dragHandleProps}
                    innerRef = {provided.innerRef}
                    onClick={(props.listName === 'filterFields' /*|| props.listName === 'metricFields'*/) ? handleClick: ()=>{} }
                    onContextMenu = {props.listName === 'filterFields' ? handleOffClick  : ()=>{}}
                >
                    <Popover
                        id="mouse-over-popover"
                        className={styles.popover}
                        classes={{
                            paper: clsx({
                                [styles.offPaper] : props.isOff,
                                [styles.paper] : !props.isOff
                            })
                        }} 
                        open={openPopover}
                        anchorEl={anchorEl}
                        anchorOrigin={{
                            vertical: 'center',
                            horizontal: 'center',
                        }}
                        transformOrigin={{
                            vertical: 'center',
                            horizontal: 'center',
                        }}
                        onClose={handlePopoverClose}
                        disableRestoreFocus
                    >
                        <div className={styles.popoverDiv}>
                            <Box fontSize={10} fontWeight={"fontWeightMedium"} className={styles.fieldTextHover}>
                                {props.filtered &&
                                    <Icon path={mdiFilter} size={0.5}/> 
                                }
                                {(props.aggFuncName ? AggFunc.get(props.aggFuncName) + ' ' : '') + props.fieldName} 
                            </Box>
                        </div>
                    </Popover>
                    {(props.listName === 'metricFields') &&
                        <div className={styles.divFilterButton}>
                            <IconButton
                                size='small'
                                className={styles.filterButton}
                                onClick={handleClick}
                            >   
                                {props.listName === 'filterFields' ?  <Icon path={mdiFilter} size={0.5}/> :
                                props.listName === 'metricFields'  ? <Icon path={mdiPencil} size={0.5}/> : null
                                }
                            </IconButton>
                        </div>
                    }
                    {/*(props.listName === 'filterFields') &&
                        <div className={styles.divFilterOffButton}>
                            <IconButton
                                size='small'
                                className={styles.filterButton}
                                onClick={handleOffClick}
                            >   
                                <PowerSettingsNewIcon style={{height: '14px', width: '14px'}}/>
                            </IconButton>
                        </div>*/
                    }

                    <ListItemText className={clsx({
                            [styles.listItemOffText]: props.isOff,
                            [styles.listItemText]: !props.isOff
                        })}
                        aria-owns={open ? 'mouse-over-popover' : undefined}
                        aria-haspopup="true"
                        onMouseEnter={handlePopoverOpen}
                        onMouseLeave={handlePopoverClose}
                        ref={parentRef}
                    >                      
                        <Box fontSize={9} fontWeight={"fontWeightMedium"} className={styles.fieldText}>
                            {props.filtered && props.listName !== 'filterFields' &&
                                <Icon path={mdiFilter} size={0.8}/>
                            }
                            {(props.aggFuncName ? AggFunc.get(props.aggFuncName) + ' ' : '') + props.fieldName}
                        </Box>
                            
                    </ListItemText>
                    {
                        (props.listName === "metricFields" && props.index === index) 
                        &&
                        <AggFuncList 
                            dataType = {dataType}
                            index = {index}
                            open = {open}
                        />
                    }
                </ListItem>
          
            )}
        </Draggable>
    )
}

const mapStateToProps = state => {
    return {
        aggModalParams: state.olap.aggModalParams,
    }
}

export default connect(mapStateToProps, null)(PivotField);