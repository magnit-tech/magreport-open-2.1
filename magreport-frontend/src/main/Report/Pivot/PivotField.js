import React, { useRef } from 'react';
import { connect } from 'react-redux';

import { Draggable} from 'react-beautiful-dnd';

// icons
import Icon from '@mdi/react';
import IconButton from '@material-ui/core/IconButton';
import { mdiClose } from '@mdi/js';

import clsx from 'clsx';

import Popover from '@material-ui/core/Popover';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';

//local
import {AggFunc} from '../../FolderContent/JobFilters/JobStatuses';
import {PivotCSS} from './PivotCSS';
import { List, Modal } from '@material-ui/core';

/**
 * @param {*} props.listName - список, в котором размещено поле
 * @param {*} props.fieldId - id поля
 * @param {*} props.index - индекс поля в списке поля
 * @param {*} props.fieldName - имя поля
 * @param {*} props.newfieldName - имя поля, заданное пользователем
 * @param {*} props.aggFuncName - имя агрегирующей функции (для метрик)
 * @param {*} props.filter - фильтр на поле
 * @param {*} props.filtered - признак наличия фильтрации по полю
 * @param {*} props.isOff - признак включения/выключения поля
 * @param {*} props.onClick - функция вызывается при начатии на кнопку
 * @param {*} props.onContextClick - функция вызывается при начатии на правую кнопку мыши
 * @param {*} props.onRemoveFieldClick - функция для перемещения полей обратно в неиспользуемые
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
        e.stopPropagation();
        props.onClick(e, props.index);
    }

    const handleContextClick=(e) => {
        e.preventDefault(); 
        e.stopPropagation();
        props.onContextClick(e, props.index);
    }

    const handleRemoveFieldClick=(e) => {
        e.stopPropagation();
        props.onRemoveFieldClick(props.index);
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
                    <div 
                        style={{fontSize: '12px', fontWeight: '500'}}
                    >
                        {f.name}
                    </div>
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
    return (
        <div>
            <Draggable draggableId={props.listName + "-" + props.fieldId.toString() + "-" + props.index} index={props.index}>
                {(provided, snapshot)=>(
                    <ListItem
                        //button
                        className={clsx({
                            [styles.offField] : props.isOff,
                            [styles.field] : !props.isOff,
                            [styles.derivedField] : !props.original,
                            [styles.draggingField] : snapshot.isDragging
                        })}
                        {...provided.draggableProps}
                        {...provided.dragHandleProps}
                        innerRef = {provided.innerRef}
                        onClick={(props.listName === 'filterFields' ||  props.listName === 'metricFields') ? handleClick: ()=>{} }
                        onContextMenu = {(props.listName === 'filterFields' ||  props.listName === 'metricFields') ? handleContextClick: ()=>{}  }
                    >
                        <Popover
                            id="mouse-over-popover"
                            className={styles.popover}
                            classes={{
                                paper: clsx({
                                    [styles.offPaper] : props.isOff,
                                    [styles.paper] : !props.isOff,
                                    [styles.derivedField] : !props.original,
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
                                <div className={styles.fieldTextHover}>
                                    {/*props.filtered &&
                                        <Icon path={mdiFilter} size={0.5}/> 
                                    */}
                                    {props.newfieldName !== '' && (props.listName === 'metricFields' || props.listName === 'filterFields')? props.newfieldName :
                                    (props.aggFuncName ? AggFunc.get(props.aggFuncName) + ' ' : '') + props.fieldName} 
                                </div>
                            </div>
                        </Popover>
                        {(props.listName !== 'allFields' && props.listName !== 'unusedFields' &&
                            <IconButton
                                size='small'
                                className={styles.closeButton}
                                onClick={handleRemoveFieldClick}
                            >   
                                <Icon path={mdiClose} size={0.5}/>
                            </IconButton>
                        )}

            
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
                            <div className={styles.fieldText}>
                                {/*props.filtered && props.listName !== 'filterFields' &&
                                    <Icon path={mdiFilter} size={0.8}/>*/
                                }
                                {props.newfieldName !== '' && (props.listName === 'metricFields' || props.listName === 'filterFields')? props.newfieldName :
                                (props.aggFuncName ? AggFunc.get(props.aggFuncName) + ' ' : '') + props.fieldName}
                            </div>
                                
                        </ListItemText>
                        
                    </ListItem>
              
                )}
            </Draggable>
            {
                (props.listName === "metricFields" && props.index === index) 
                &&
                <AggFuncList 
                    dataType = {dataType}
                    index = {index}
                    open = {open}
                />
            }
        </div>
    )
}

const mapStateToProps = state => {
    return {
        aggModalParams: state.olap.aggModalParams,
    }
}

export default connect(mapStateToProps, null)(PivotField);