import FieldData from "./FieldData";

/**
 * Списки полей сводной в разных областях
 * Елементами являются объекты FieldData
 */
export function FieldsLists(fieldsLists){

    // Создаёт пустые наборы полей
    this.createLists = (fieldsLists) => {
        this.allFields = fieldsLists ? Array.from(fieldsLists.allFields) : [];
        this.unusedFields = fieldsLists ? Array.from(fieldsLists.unusedFields) :[];
        this.derivedFields = fieldsLists ? Array.from(fieldsLists.derivedFields) : [];
        this.rowFields = fieldsLists ? Array.from(fieldsLists.rowFields) :[];
        this.columnFields = fieldsLists ? Array.from(fieldsLists.columnFields) :[];
        this.metricFields = fieldsLists ? Array.from(fieldsLists.metricFields) :[];
        this.filterFields = fieldsLists ? Array.from(fieldsLists.filterFields) :[];
    }    

    this.createLists(fieldsLists);

    this._arrayElementConstructor = {
        allFields : FieldData,
        unusedFields : FieldData,
        derivedFields : FieldData,
        rowFields : FieldData,
        columnFields : FieldData,
        metricFields : FieldData,
        filterFields : FieldData
    }

    // Создаёт списки полей по метаданным отчёта
    this.createByFields = (reportFields, derivedFields) => {
        this.createLists();
        for(let f of reportFields){
            if(f.visible){
                let fd = new FieldData(f);
                this.allFields.push(fd);
                this.unusedFields.push(fd);    
            }
        }
        this.createDerivedFields(derivedFields);
    }

    this.createDerivedFields = (derivedFields) => {
        this.derivedFields = [];
        for(let f of derivedFields){
            let fd = new FieldData(f);
            fd.original = false;
            this.derivedFields.push(fd);  
        }
    }

    // Обновляет списки полей по метадынным отчёта - удаляет отсутствующие поля и добавляет новые
    this.updateByFields = (reportFields, derivedFields) => {
        // TODO
        // ЗАГЛУШКА!!!
        this.createByFields(reportFields, derivedFields);
    }

    // Обновляет списки полей из сохраненной конфигурации
    this.actualizeUnusedFields = ( fieldsLists ) => {

        let arrWithUsedFieldIds = new Set(),
            newUnusedArray = [];

        for (let key in fieldsLists) {

            if(fieldsLists[key].length !== 0 && Array.isArray(fieldsLists[key])) {
                fieldsLists[key].map( fieldItem => arrWithUsedFieldIds.add(fieldItem.id) )
            }
        }

        newUnusedArray = this.unusedFields.filter( ( { fieldId } ) => !arrWithUsedFieldIds.has(fieldId) )

        this.unusedFields = newUnusedArray

    }

    /*
    *********************
    Сериализация
    *********************
    */

    this.forSave = () => {
        let saveCopy = new FieldsLists(this)

        delete saveCopy.allFields;
        delete saveCopy.unusedFields;

        return saveCopy
    }

    /*
    ************************
        Операции с полями
    ************************
    */

    /*
        Внутренняя часть
    */

    // Удалить из списка listName поле с данным fieldId за исключением поля с индексом exceptIndex
    this.removeFieldByFieldId = (listName, fieldId, exceptIndex) => {
        let newList = [];

        for(let i = 0; i < this[listName].length; i++){
            if(this[listName][i].fieldId !== fieldId || i === exceptIndex){
                newList.push(this[listName][i]);
            }
        }
        
        this[listName] = newList;
    }

    // Удалить из списка listName поле с данным индексом index
    this.removeFieldByIndex = (listName, index) => {
        this[listName].splice(index, 1);
    }

    // Найти поле в списке listName с данным fieldId
    this.findFieldByFieldId = (listName, fieldId) => {
        for(let i = 0; i < this[listName].length; i++){
            if(this[listName][i].fieldId === fieldId){
                return {
                    field: this[listName][i],
                    index: i
                }
            }
        }
        return undefined;
    }

    this.findFilterFieldIdByFieldIdValueFunc = (fieldId, value, metricIndex = null) => {
        let funcName = metricIndex === null ? '' : this.metricFields[metricIndex].aggFuncName;
        for(let i = 0; i < this.filterFields.length; i++){
            if(this.filterFields[i].fieldId === fieldId
                && this.filterFields[i].aggFuncName === funcName 
                && this.filterFields[i].filter.filterType === 'EQUALS'
                && this.filterFields[i].filter.values[0] === value){
                return true
            }
        }
        return false;
    }

    // Проверить присутсвие поля с данным fieldId в любом функциональном списке
    this.fieldIdExistsInAnyFunctionalList = (fieldId) => {
        for(let listName of ["rowFields", "columnFields", "metricFields", "filterFields"]){
            if(this.findFieldByFieldId(listName, fieldId)){
                return true;
            }
        }
        return false;
    }

    // Вставить поле в список listName
    this.insertFieldInList = (listName, index, field) => {
        this[listName].splice(index, 0, field);
    }

    /*
        Интерфейсная часть
    */

    // Получить поле из списка по индексу
    this.getFieldByIndex = (listName, fieldIndex) => {
        return this[listName][fieldIndex];
    }

    // Если удалена метрика, то удаляем и фильтр
    this.deleteFilterFieldByMetric = (fieldId, aggFuncName) => {
        //Если есть аналогичная метрика, то фильтр не удаляем
        if (this.metricFields.findIndex(i => i.fieldId === fieldId && i.aggFuncName === aggFuncName) === -1){
            //иначе находим фильтры и удаляем их
            for (let i = 0; i < this.filterFields.length; i++) {
                if (this.filterFields[i].fieldId === fieldId && this.filterFields[i].aggFuncName === aggFuncName){
                    this.removeFieldByIndex('filterFields', i)
                }
            }
        }
    }

    // Перемещение поля при Drag And Drop из одного списка в другой
    // Возвращает индекс места в списке нового поля (может не совпадать в отдельных случаях с dropListFieldIndex)
    this.dragAndDropField = (dragListName, dropListName, dragListFieldIndex, dropListFieldIndex) => {

        let destIndex;

        if(dragListName !== dropListName
            || dragListFieldIndex !== dropListFieldIndex
            || dropListName === "metricFields"
            || dropListName === "filterFields")
        {
            destIndex = dropListFieldIndex;
            let movingField = new FieldData(this[dragListName][dragListFieldIndex]);
            // Обнуляем фильтр
            movingField.setFilter({});
            movingField.setIsOffFalse({});

            if ((dragListName === "metricFields" && dropListName === "metricFields"  && dragListFieldIndex === dropListFieldIndex) ||
                (dragListName === "filterFields" && dropListName === "filterFields"  && dragListFieldIndex === dropListFieldIndex)
                // Если поле взято из списка метрик(или фильтров) и помещено на то же место,
                // или поле взято из Всех полей, то считаем, 
                // что требуется дублирование поля в списке метрик (или фильтров) - удаление не требуется
                
            ){
                destIndex = dropListFieldIndex + 1;
            }
            else if( !( (dragListName === "filterFields" && (dropListName === "columnFields" || dropListName === "rowFields" || dropListName === "metricFields") )
                        || (dropListName === "filterFields" && (dragListName === "columnFields" || dragListName === "rowFields" || dragListName === "metricFields") )
                ))
            {
                // Иначе если это не перемещение между измерениями и фильтрами - удаляем поле из списка-источника, 
                if(dragListName === 'allFields'){
                    let fieldInUnused = this.findFieldByFieldId('unusedFields', movingField.fieldId)
                    if (typeof fieldInUnused !== 'undefined') {
                        this.removeFieldByIndex('unusedFields', fieldInUnused.index);
                    }
                } else if (dragListName === 'metricFields' && (dropListName === 'unusedFields' || dropListName === 'allFields')) {
                    this.removeFieldByIndex(dragListName, dragListFieldIndex);
                    this.deleteFilterFieldByMetric(movingField.fieldId, movingField.aggFuncName);
                } else {
                    this.removeFieldByIndex(dragListName, dragListFieldIndex);
                }     
            }

            // Если поле помещено в columnFields, то его нужно удалить из rowFields и наоборот 
            // (такая ситуация может возникнуть, когда из метрик поле)
            if(dropListName === "columnFields"){
                this.removeFieldByFieldId("rowFields", movingField.fieldId);
            }
            else if(dropListName === "rowFields"){
                this.removeFieldByFieldId("columnFields", movingField.fieldId);
            }

            // Удалить аггрегирующую функцию, кроме случаев, когда помещаем поле в фильтр
            if( movingField.aggFuncName !== "" && dropListName !== 'filterFields'){
                movingField.setAggFuncName("")
            }

            // Добавляем поле в список-прёмник
            if(dropListName === "metricFields" || dropListName === "filterFields"){
                this.insertFieldInList(dropListName, destIndex, movingField);
            }
            else if(dropListName === "unusedFields"){
                if(!this.fieldIdExistsInAnyFunctionalList(movingField.fieldId)){
                    this.insertFieldInList(dropListName, destIndex, movingField);
                }
            }
            else { let newIndex = this.findFieldByFieldId(dropListName, movingField.fieldId)
                //Если такое поле уже есть, то возвращаем индекс существующего поля
                if(newIndex === undefined){
                    this.insertFieldInList(dropListName, destIndex, movingField);
                } else {
                    destIndex = newIndex.index;
                }   
            }             
        }

        return destIndex;
    }

    // Задание агрегирующей функции для метрики
    this.setAggMetricByIndex = (aggFuncName, index) => {
        this.metricFields[index] = new FieldData(this.metricFields[index]);
        this.metricFields[index].aggFuncName = aggFuncName;
    }

    // Задание фильтра на поле
    this.setFieldFilterByFieldId = (fieldId, filterObject) => {
        this.removeFieldByFieldId("unusedFields", fieldId);
        let findResult = this.findFieldByFieldId("filterFields", fieldId);

      //  if(findResult === undefined){
            findResult = this.findFieldByFieldId("allFields", fieldId);
            let newField = new FieldData(findResult.field);
            newField.setFilter(filterObject);
            this.filterFields.push(newField);
      /*  }
        else{
            this.filterFields[findResult.index] = new FieldData(this.filterFields[findResult.index]);
            this.filterFields[findResult.index].setFilter(filterObject);
        }*/
    }

    // Задание фильтра на метрике
    this.setFieldFilterByFieldIdAndFunc = (fieldId, fieldIndex, filterObject) => {
        this.removeFieldByFieldId("unusedFields", fieldId);
        let findResult = this.metricFields[fieldIndex];
        let newField = new FieldData(findResult);
        newField.setFilter(filterObject);
        this.filterFields.push(newField);
    }

    // Задание фильтра на поле по индексу в списке фильтров
    this.setFieldFilterByFieldIndex = (index, filterObject) => {
        this.filterFields[index] = new FieldData(this.filterFields[index]);
        this.filterFields[index].setFilter(filterObject);
    }    

    // Передаем тип данных метрики
    this.setDataType = (fieldIndex, dataType) => {
        let field = new FieldData(this.metricFields[fieldIndex]);
        field.changeDataType(dataType)
        this.metricFields[fieldIndex] = field;
    }

    /*
        *********************
        Форматирование
        *********************
    */

    this.setAggMetricForFormatting = (index) => {
        let field = new FieldData(this.metricFields[index]);
        field.setAggMetricForFormatting();
        this.metricFields[index] = field;
    }
    
    this.changeFieldDataStyle = (index, formatObj) => {
        let field = new FieldData(this.metricFields[index]);
        field.changeDataStyle(formatObj)
        this.metricFields[index] = field;
    }

    this.changeFieldDataStyle = (index, formatObj) => {
        let field = new FieldData(this.metricFields[index]);
        field.changeDataStyle(formatObj)
        this.metricFields[index] = field;
    }

    /*
        *********************
        Условное форматирование
        *********************
    */

    this.addConditionalFormatting = (index, formatArray) => {
        let field = new FieldData(this.metricFields[index]);
        field.addConditionalFormatting(formatArray)
        this.metricFields[index] = field;
    }
}