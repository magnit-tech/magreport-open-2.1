import restoreObjectData from "utils/restoreObjectData";

export default function FieldData(fieldData){
    /**
     * Поля
     */

    // Из метаданных отчёта:
    this.id = 0;
    this.name = "";
    this.type = '';
    this.visible = true;
    this.dataType = ''

    // Добавленные на уровне сводной свойства:
    this.aggFuncName = "";
    this.filter = null;
    this.isOff = false;
    this.formatting = [
        {
            aggFuncName: '',
            personalSettings: false,
            // Number
            rounding: 0,
            percent: false,
            numberWithSpaces: false,
            // String
            truncat: 0,
            initialStringData: '',
            initialStringLength: 0,
            // Font
            bold: false,
            fontStyle: 'normal',
            fontSize: 14,
            fontColor: ""
        }
    ];
    this.conditionalFormatting = []

    // Обновляем из входящего объекта
    restoreObjectData(this, fieldData);

    /*
        Синонимы для полей:
    */
    this.fieldId = this.id;
    this.fieldName = this.name;

    // Исключение поля
    this.setIsOff = () => {
        this.isOff = !this.isOff;
    }
    this.setIsOffFalse = () => {
        this.isOff = false
    }
    /*
        Методы задания фильтрации и сортировки
    */
    this.setFilter = (filter) => {
        this.filter = filter;
    }

    this.setAggFuncName = (aggFuncName) => {
        this.aggFuncName = aggFuncName;
    };

    // Изменение тип данных метрики
    this.changeDataType = (dataType) => {
        this.dataType = dataType
        // this.type = dataType
    }

    // Изменение данных метрики 
    this.changeData = (data) => {
        this.setAggMetricForFormatting()
        
        if (data && data.trim() !== '') {
            this.formatting.forEach(item => {
                if (item.aggFuncName === this.aggFuncName) {

                    if(this.dataType === 'INTEGER' || this.dataType === 'DOUBLE') {
                        if (item.personalSettings) {

                            if (data === 'null') return data = ' '

                            let generatedData = data.replace('%', '')

                            if (item.percent) {
                                generatedData = Number(generatedData) * 100
                            }

                            if (item.rounding) {
                                generatedData = Number(generatedData).toFixed(item.rounding)
                            }

                            if(item.numberWithSpaces) {
                                if(item.rounding > 0) {
                                    let parts = generatedData.toString().split(".");
                                    parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, " ");
                                    generatedData = parts.join(".");
                                }
                                generatedData = generatedData.toString().replace(/\B(?=(\d{3})+(?!\d))/g, " ");
                            }

                            return data = `${generatedData}${item.percent ? '%' : ''}`

                        } else {
                            if (this.dataType === 'DOUBLE') {
                                item.rounding = 2;
                                return data = Number(data).toFixed(2)
                            } else {
                                item.rounding = 0;
                                return data
                            }
                        }
                    } else if (this.dataType === 'STRING') {
                        if (item.personalSettings) {
                            if(item.initialStringLength > item.truncat) {
                                return data = item.initialStringData.substring(0, item.truncat) + '...'
                            } else {
                                return data = item.initialStringData
                            }
                        } else {
                            item.truncat = data.length;
                            item.initialStringData = data;
                            item.initialStringLength = data.length;
                            return data
                        }
                    }

                }
            })

        }
        return data
    }


    /*
        Форматирование
    */

    // Привязываем форматирование к определенной агрегирующей функции метрики
    this.setAggMetricForFormatting = () => {
        let arrayToCompare = new Set()

        this.formatting.forEach( item => {
            if (item.aggFuncName === '') {
                arrayToCompare.add(this.aggFuncName)
                return item.aggFuncName = this.aggFuncName
            } else {
                arrayToCompare.add(item.aggFuncName)
            }
        })

        if (arrayToCompare.has(this.aggFuncName) || this.aggFuncName === '') {
            return false
        } else {
            const aggFuncName = this.aggFuncName
            this.formatting.push(
                {
                    aggFuncName,
                    personalSettings: false,
                    bold: false,
                    rounding: 0,
                    percent: false
                }
            )
        }
    }

    // Изменение всего объекта форматирования
    this.changeDataStyle = (formatObj) => {
        this.formatting.forEach((item, index) => {
            if (item.aggFuncName === this.aggFuncName) {
                this.formatting[index] = {
                    ...formatObj[index],
                    personalSettings: true
                }
            }
        })
    }

    /*
        Условное форматирование
    */

    this.addConditionalFormatting = (formatArray) => {
        this.conditionalFormatting = formatArray
    }
    
}