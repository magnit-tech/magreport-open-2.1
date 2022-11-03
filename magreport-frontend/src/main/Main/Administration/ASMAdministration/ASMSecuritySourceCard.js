import React, {useState} from "react";
import {connect} from "react-redux";
import {useSnackbar} from "notistack";

import Card from "@material-ui/core/Card";
import IconButton from "@material-ui/core/IconButton";
import AddCircle from "@material-ui/icons/AddCircle";
import Checkbox from '@material-ui/core/Checkbox';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import FormControl from '@material-ui/core/FormControl';

// components
import DesignerTextField from "main/Main/Development/Designer/DesignerTextField";
import DesignerFolderItemPicker from "main/Main/Development/Designer/DesignerFolderItemPicker";
import DesignerSelectField from "main/Main/Development/Designer/DesignerSelectField";
import DesignerFolderBrowser from "main/Main/Development/Designer/DesignerFolderBrowser";
import ASMSecurityFilterCard from "./ASMSecurityFilterCard";


// local
//import {ASMCSS as useStyles} from "./ASMCSS";
import {FolderItemTypes} from "main/FolderContent/FolderItemTypes";
import {
    actionAsmDesignerAddSecuritySourceField,
    actionAsmDesignerAddSecuritySourceSecurityFilter,
    actionAsmDesignerChangeSecuritySourceData,
    actionAsmDesignerSetSecuritySourceDataSet,
    actionAsmDesignerSetSecuritySourceField,
    actionAsmDesignerDeleteSecuritySourceField,
    actionAsmDesignerChangeRootData
} from "redux/actions/admin/actionAsmDesigner";
import {
    selectDataTypeName,
    selectDataSet,
    selectDataValue,
    selectSecuritySourceDataValue,
    selectSecuritySourceErrorValue,
    selectSourceSecurityFilters,
    selectSecuritySourceFieldErrorValue
} from "redux/reducers/admin/asmDesigner/asmDesignerSelectors";
import {
    DATASET_FIELD_ID,
    DESCRIPTION,
    FIELD_TYPE,
    FIELDS,
    FILTER_VALUE_FIELD,
    NAME, PERMISSION_SOURCE, USER_MAP_SOURCE,
    POST_SQL,
    PRE_SQL,
    SOURCE_TYPE,
    IS_DEFAULT_DOMAIN,
    DOMAIN_NAME_FIELD
} from "utils/asmConstants";

/**
 * @callback changeData
 * @param {Number} index
 * @param {String} key
 * @param {*} value
 */

/**
 * @callback setDataSet
 * @param {Number} index
 * @param {Object} dataSet
 */

/**
 * @callback setField
 * @param {Number} sourceIndex
 * @param {Number} fieldIndex
 * @param {{fieldType, dataSetFieldId?: 0, filterInstanceFields?: []}} oldField
 * @param {{fieldType, dataSetFieldId?: 0, filterInstanceFields?: []}} newField
 */

/**
 * @callback addField
 * @param {Number} sourceIndex
 * @param {{fieldType, dataSetFieldId?: 0, filterInstanceFields?: []}} field
 */

/**
 * @callback addSecurityFilter
 * @param {Number} sourceIndex
 * @param {Object} securityFilter
 */

/**
 * Карточка с Security Source из объекта ASM
 * @param {Object} props.state - asmDesigner state object
 * @param {Number} props.index - индекс security source в State
 * @param {changeData} props.changeData - меняет значение поля source в state
 * @param {setDataSet} props.setDataSet - устанавливает dataSet в state для данного Source
 * @param {setField} props.setField - меняет выбор поля
 * @param {addField} props.addField - добавляет новое поле
 * @param {addSecurityFilter} props.addSecurityFilter - добавляет новый SecurityFilter и связанный с ним FilterInstance
 * @returns {JSX.Element}
 * @constructor
 */
function ASMSecuritySourceCard(props) {
    const {enqueueSnackbar} = useSnackbar();
    //const classes = useStyles();

    const state = props.state;

    const index = props.index;

    const selectDataVal = (key) => selectSecuritySourceDataValue(state, index, key);
    const selectErrorValue = (key) => selectSecuritySourceErrorValue(state, index, key);

    const dataSet = selectDataSet(state, index);
    const securityFilters = selectSourceSecurityFilters(state, index);

    const sourceType = selectDataVal(SOURCE_TYPE);
    const name = selectDataVal(NAME);
    const description = selectDataVal(DESCRIPTION);
    const fields = selectDataVal(FIELDS);
    const preSql = selectDataVal(PRE_SQL);
    const postSql = selectDataVal(POST_SQL);
    const isDefaultDomain = selectDataValue(state, IS_DEFAULT_DOMAIN);

    let dataSetFields = [];
    if ("fields" in dataSet) {
        dataSetFields.push({id: 0, name: "<Удалить>"});

        dataSet.fields.forEach((field) => {
            dataSetFields.push({
                id: field.id,
                name: `${field.name} (${selectDataTypeName(state, field.typeId)})`
            });
        });
    }


    const [securityFilterDialogOpened, setSecurityFilterDialogOpened] = useState(false);

    let fieldItems = [];

    let securityFilterItems = [];


    function handleAddSecurityFilterClick() {
        setSecurityFilterDialogOpened(true);
    }

    function handleCloseSecurityFilterDialog() {
        setSecurityFilterDialogOpened(false);
    }

    function handleSelectSecurityFilterClick(securityFilterId, securityFilter) {
        if (securityFilters.indexOf(securityFilterId) > -1) {
            enqueueSnackbar(`Фильтр безопасности ${securityFilter.name} уже добавлен.`, {variant: "error"});
            return;
        }

        props.addSecurityFilter(index, securityFilter);
        setSecurityFilterDialogOpened(false);
    }

    fields.forEach((field, fieldIndex) => {
        const isError = selectSecuritySourceFieldErrorValue(state, index, fieldIndex, DATASET_FIELD_ID);
        if (field[FIELD_TYPE] !== 'DOMAIN_NAME_FIELD' || isDefaultDomain === false){

        fieldItems.push(
            <div key={fieldIndex}>
                <DesignerSelectField
                    fullWidth
                    label={field[FIELD_TYPE]}
                    value={field[DATASET_FIELD_ID] ? field[DATASET_FIELD_ID] : ""}
                    data={dataSetFields}
                    error={isError}
                    onChange={(dataSetFieldId) => props.setField(index, fieldIndex, field, {...field, dataSetFieldId})}
                />
            </div>
        );
        }
    });

    securityFilters.forEach((securityFilter, securityFilterIndex) => {
        securityFilterItems.push(
            <ASMSecurityFilterCard
                key={securityFilterIndex}
                sourceIndex={index}
                securityFilterIndex={securityFilterIndex}
            />
        );
    });

    function handleIsDefaultDomainChecked(checked){
        props.changeRootData(IS_DEFAULT_DOMAIN, checked);
        let fieldIndex = fields.findIndex(i => i.fieldType === DOMAIN_NAME_FIELD);
        let dataSetFieldId = 0;
        if (checked === false){
            props.addField(index, {fieldType: DOMAIN_NAME_FIELD, dataSetFieldId})
        } else {
            props.delField(index, fieldIndex );
        }
    }

    return (
        <div /*className={classes.panelSpacing}*/>
            <DesignerTextField
                label="Название"
                value={name}
                //displayBlock
                fullWidth
                onChange={value => props.changeData(index, NAME, value)}
                error={selectErrorValue(NAME)}
            />
            <DesignerTextField
                label="Описание"
                value={description}
                //displayBlock
                fullWidth
                onChange={value => props.changeData(index, DESCRIPTION, value)}
                error={selectErrorValue(DESCRIPTION)}
            />
            <DesignerFolderItemPicker
                label={"Набор данных"}
                value={dataSet ? dataSet.name : null}
                itemType={FolderItemTypes.dataset}
                onChange={(itemId, item) => props.setDataSet(index, item)}
                displayBlock
                fullWidth
                error={selectErrorValue(DATASET_FIELD_ID)}
                disabled={false}
            />
            <DesignerTextField
                label="PreSQL"
                value={preSql}
                displayBlock
                fullWidth
                multiline
                onChange={value => props.changeData(index, PRE_SQL, value)}
                error={selectErrorValue(PRE_SQL)}
            />
            <DesignerTextField
                label="PostSQL"
                value={postSql}
                displayBlock
                fullWidth
                multiline
                onChange={value => props.changeData(index, POST_SQL, value)}
                error={selectErrorValue(POST_SQL)}
            />
            {sourceType === USER_MAP_SOURCE &&
                <FormControl>
                    <FormControlLabel
                                            //value={item.folderId}
                        control={<Checkbox
                            color="primary" 
                                                //disabled={false}
                            checked = {isDefaultDomain}
                                             //   name = {item.folderId.toString()}
                            onChange={(e) => handleIsDefaultDomainChecked(e.target.checked)}
                        />}
                        label="Домен по-умолчанию"
                        labelPlacement="start"
                    />
                </FormControl>
            }

            {fieldItems}

            {sourceType === PERMISSION_SOURCE &&
                <div>
                    <div>
                        <DesignerSelectField
                            fullWidth
                            label={FILTER_VALUE_FIELD}
                            value={""}
                            data={dataSetFields}
                            onChange={dataSetFieldId => props.addField(index, {fieldType: FILTER_VALUE_FIELD, dataSetFieldId})}
                        />
                    </div>
                    {securityFilterItems}
                    <Card>
                        <DesignerFolderBrowser
                            itemType={FolderItemTypes.securityFilters}
                            dialogOpen={securityFilterDialogOpened}
                            onChange={handleSelectSecurityFilterClick}
                            onClose={handleCloseSecurityFilterDialog}
                        />
                    </Card>
                    <div style={{textAlign: 'center'}}>
                    <IconButton
                        aria-label="add"
                        color="primary"
                        onClick={handleAddSecurityFilterClick}
                        disabled={false}
                    >
                        <AddCircle
                            fontSize='large'
                        />
                    </IconButton>
                    </div>
                </div>
            }
        </div>
    );
}

const mapStateToProps = (state) => {
    return {
        state: state.asmDesigner
    }
}

const mapDispatchToProps = {
    changeRootData: actionAsmDesignerChangeRootData,
    changeData: actionAsmDesignerChangeSecuritySourceData,
    setDataSet: actionAsmDesignerSetSecuritySourceDataSet,
    setField: actionAsmDesignerSetSecuritySourceField,
    addField: actionAsmDesignerAddSecuritySourceField,
    delField: actionAsmDesignerDeleteSecuritySourceField,
    addSecurityFilter: actionAsmDesignerAddSecuritySourceSecurityFilter
}

export default connect(mapStateToProps, mapDispatchToProps)(ASMSecuritySourceCard);