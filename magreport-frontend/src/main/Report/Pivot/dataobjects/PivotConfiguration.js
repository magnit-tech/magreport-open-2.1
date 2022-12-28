import {FieldsLists} from './FieldsLists';
import {PivotFilterGroup} from './PivotFilterGroup';
import restoreObjectData from 'utils/restoreObjectData';

/**
 * Объект конфигурации сводной - содержит все метаданные, определяющие вид сводной таблицы
 */
export default function PivotConfiguration(pivotConfiguration){
    
    /*
    **************
    Поля
    **************
    */

    this.fieldsLists = pivotConfiguration ? pivotConfiguration.fieldsLists : new FieldsLists();

    this.columnFrom = pivotConfiguration ? pivotConfiguration.columnFrom : 0;
    this.columnCount = pivotConfiguration ? pivotConfiguration.columnCount : 0;
    this.rowFrom = pivotConfiguration ? pivotConfiguration.rowFrom : 0;
    this.rowCount = pivotConfiguration ? pivotConfiguration.rowCount : 0;

    this.columnsMetricPlacement = pivotConfiguration ? pivotConfiguration.columnsMetricPlacement : false; // метрики размещаются по столбцам

    this.mergeMode = pivotConfiguration ? pivotConfiguration.mergeMode : false; // объединять ли ячейки с одинаковыми значениями

    this.sortOrder = pivotConfiguration ? pivotConfiguration.sortOrder : {}; // объект значений для сортировки метрик

    this.filterGroup = pivotConfiguration ? pivotConfiguration.filterGroup : new PivotFilterGroup();
    this.metricFilterGroup = pivotConfiguration ? pivotConfiguration.metricFilterGroup : new PivotFilterGroup();

    /*
    **************
    **************
    Методы
    **************
    **************
    */

    // Используется для создания конфигурации для новой выгрузки
    // pivotConfiguration - может быть ранее сохранённой конфигурацией или пустым объектом
    // Сначала происходит восстановление конфигурации, затем приведение её в соответсвие с актуальным объектом reportMetada
    // Таким образом поддерживается следующая логика: сохранённый объект конфигурации может содержать набор полей, отличающийся от актуального
    // в этом случае удаляются отсутствиющие в актуальном отчёте поля и добавляются новые
    this.create = (pivotConfiguration, reportMetada) => {
        restoreObjectData(this, pivotConfiguration);
        this.fieldsLists = new FieldsLists(this.fieldsLists);
        this.fieldsLists.updateByFields(reportMetada.fields, reportMetada.derivedFields);
    };

    // Используется для загрузки сохранённой конфигурации
    this.restore = (pivotSaveConfig) => {
        restoreObjectData(this, pivotSaveConfig); // В pivotSaveConfig.fieldsLists отсуствуют allFields
        this.fieldsLists = new FieldsLists(this.fieldsLists);
        this.fieldsLists.actualizeUnusedFields(pivotSaveConfig.fieldsLists);
        this.sortOrder = pivotSaveConfig.sortOrder;
    };

    // Очищаем все поля
    this.clearAll = () => {
        this.fieldsLists = new FieldsLists();
        this.columnFrom = 0;
        this.columnCount = 0;
        this.rowFrom = 0;
        this.rowCount = 0;
        this.columnsMetricPlacement = false;
        this.mergeMode = false;
        this.sortOrder = {};
        this.filterGroup = new PivotFilterGroup();
        this.metricFilterGroup = new PivotFilterGroup();
    }

    /*
    *********************
    Сериализация
    *********************
    */

    function replacer(key, value) {
        if (typeof value === 'object' && value !== null && value.hasOwnProperty('forSave')) {
            return value.forSave()
        } else {
            return value
        }
    }

    this.stringify = () => {
        return JSON.stringify(this, replacer)
    }

    /*
    *********************
    Работа с полями
    *********************
    */

    /*
        Перемещение полей отчёта
    */

    // Перемещение поля при Drag And Drop из одного списка в другой
    // Возвращает индекс места в списке нового поля (может не совпадать в отдельных случаях с dropListFieldIndex)    
    this.dragAndDropField = (dragListName, dropListName, dragListFieldIndex, dropListFieldIndex) => {
        this.fieldsLists = new FieldsLists(this.fieldsLists);
        return this.fieldsLists.dragAndDropField(dragListName, dropListName, dragListFieldIndex, dropListFieldIndex);
    }

    // Получить поле из списка по индексу
    this.getFieldByIndex = (listName, fieldIndex) => {
        return this.fieldsLists.getFieldByIndex(listName, fieldIndex);
    }

    // Задание агрегирующей фукнции для метрики
    this.setAggMetricByIndex = (aggFuncName, index) => {
        this.fieldsLists = new FieldsLists(this.fieldsLists);
        this.fieldsLists.setAggMetricByIndex(aggFuncName, index);
    }

    // Удаление фильтров при изменении метрик
    this.dropFiltersByMetric = (fieldId, aggFuncName) => {
        this.fieldsLists.deleteFilterFieldByMetric(fieldId, aggFuncName);
    }

    // Задание фильтра на поле
    this.setFieldFilterByFieldId = (fieldId, filterObject) => {
        this.fieldsLists = new FieldsLists(this.fieldsLists);
        this.fieldsLists.setFieldFilterByFieldId(fieldId, filterObject);
    }

    // Задание фильтра на метрике 
    this.setFieldFilterByFieldIdAndFunc = (fieldId, index, filterObject) => {
        this.fieldsLists = new FieldsLists(this.fieldsLists);
        this.fieldsLists.setFieldFilterByFieldIdAndFunc(fieldId, index, filterObject);
    }

    // Задание фильтра на поле по индексу в списке фильтров
    this.setFieldFilterByFieldIndex = (index, filterObject) => {
        this.fieldsLists = new FieldsLists(this.fieldsLists);
        this.fieldsLists.setFieldFilterByFieldIndex(index, filterObject);
    }

    // Задание группы фильтров

    this.replaceFilter = (i = null) => {

        this.filterGroup = new PivotFilterGroup();
        this.filterGroup.replaceFilter(this.fieldsLists.filterFields.filter((item, index) => item.aggFuncName === "" && index !== i && !item.isOff));


        this.metricFilterGroup = new PivotFilterGroup();
        this.metricFilterGroup.replaceFilter(this.fieldsLists.filterFields.filter(item => item.aggFuncName !== "" && !item.isOff).map(i =>{
            return (
            {...i, filter: {
                metricId: this.fieldsLists.metricFields.findIndex(m => m.fieldId === i.filter.fieldId && m.aggFuncName === i.aggFuncName),
                filterType: (i.filter.values === '' || i.filter.values === null || i.filter.values.length === 0) ? 'BLANK' : i.filter.filterType, 
                invertResult: i.filter.invertResult, 
                values: i.filter.values,
                rounding: i.formatting.find(item => item.aggFuncName === i.aggFuncName).rounding
            }})
        }));
    }
    
    /*
    *********************
    Диапазон просмотра
    *********************
    */

    this.setColumnFrom = (newColumnFrom) => {
        this.columnFrom = newColumnFrom;
    }
        
    this.setRowFrom = (newRowFrom) => {
        this.rowFrom = newRowFrom;
    }

    this.setColumnAndRowCount = (newColumnCount, newRowCount) => {
        this.columnCount = newColumnCount;
        this.rowCount = newRowCount;
    }

    /*
        ***************
        Форматирование
        ***************
    */

    // Привязываем форматирование к определенной агрегирующей функции метрики
    this.setAggMetricForFormatting = (index) => {
        this.fieldsLists = new FieldsLists(this.fieldsLists);
        this.fieldsLists.setAggMetricForFormatting(index);
    }

    this.changeFieldDataStyle = ({index, style}) => {
        this.fieldsLists = new FieldsLists(this.fieldsLists);
        this.fieldsLists.changeFieldDataStyle(index, style);
    }

    /*
        ***************
        Условное форматирование
        ***************
    */

    this.addConditionalFormatting = (index, array) => {
        this.fieldsLists = new FieldsLists(this.fieldsLists);
        this.fieldsLists.addConditionalFormatting(index, array);
    }

    /*
        ***************
        Сортировка
        ***************
    */

    this.changeSortOrder = (newSortObj) => {
        this.sortOrder = newSortObj;
    }

    /*
        ****************
        Производные поля
        ****************
    */

    this.createDerivedFields = (derivedFields) => {
        this.fieldsLists = new FieldsLists(this.fieldsLists);
        this.fieldsLists.createDerivedFields(derivedFields);
    }
}