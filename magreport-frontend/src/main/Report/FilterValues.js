export default function FilterValues() {

    // Хранит объекты выбора для каждого фильтра по ключу - ID фильтра
    // объект выбора: {operationType, parameters}

    this.values = new Map();

    this.setFilterValue = (filterValue) => {
        this.values.set(filterValue.filterId, filterValue);
    }

    this.buildOnParametersObject = (lastParameters, buildForLastParamters) => {
        this.values = new Map();
        for (let p of lastParameters) {
            if (buildForLastParamters || p.filterType !== 'TOKEN_INPUT') {
                // Для фильтра TOKEN_INPUT lastParameters отличается по структуре от parameters и прошлое значение получается
                // обратным вызовом onChangeFilterValue из самого фильтра, в котором правильным образом обрабатывается значение
                // lastParameters, поэтому его lastParamters учитываются только для заполнения прошлого выбора, а не для текущего
                this.setFilterValue(p);
            }
        }
    }

    this.getFilterValue = (filterId) => {
        return this.values.get(filterId);
    }

    this.checkMandatoryFilters = mandaroryFilters => {
        for (let f of mandaroryFilters) {
            const filterVal = this.values.get(f)
            if (!filterVal || filterVal.parameters.length === 0) {
                return false
            }
        }
        return true
    }

    this.checkInvalidValues = () => {
        for (let f of this.values.values()) {
            if (f.validation && f.validation !== 'success') {
                return false
            }
        }
        return true
    }

    this.getParameters = () => {
        let parameters = [];

        this.values.forEach((v) => {
            if (v.parameters.length > 0) {
                parameters.push(v)
            }
        });

        return parameters;
    }

    this.getExternalFiltersParams = (arr) => {
        let resultParams = [];

        arr.forEach((filter) => {
            if(filter.filterCode && filter.filterType) {
                switch(filter.filterType) {
                    case "VALUE_LIST":
                       resultParams.push(`"${filter.filterCode}":{"operationType":"${filter.operationType === 'IS_IN_LIST' ? 'IN_LIST' : 'NOT_IN_LIST'}","value":"[${filter.parameters[0].values.map((item) => item.value)}]"}`);
                       break
                    case "DATE_RANGE":
                       resultParams.push(`"${filter.filterCode}":{"begin_dt":"${filter.parameters[0].values[0].value}","end_dt":"${filter.parameters[0].values[1].value}"}`);
                       break
                    case "RANGE":
                       resultParams.push(`"${filter.filterCode}":{"from":"${filter.parameters[0].values[0].value}","to":"${filter.parameters[0].values[1].value}"}`);
                       break
                    case "SINGLE_VALUE_UNBOUNDED":
                       resultParams.push(`"${filter.filterCode}":{"value":"${filter.parameters[0].values[0].value}"}`);
                       break
                    case "DATE_VALUE":
                       resultParams.push(`"${filter.filterCode}":{"date":"${filter.parameters[0].values[0].value}"}`);
                       break
                    default:
                        return
                }
            }
        })

        return resultParams.join(',')
    }

    this.getExternalFiltersURL = () => {
        const globalUrl = new URL(document.location);

        let resultUrl = globalUrl.origin + globalUrl.pathname
        let externalFiltersParams = this.getParameters().length > 0 ? this.getExternalFiltersParams(this.getParameters()) : ''

        resultUrl += `${externalFiltersParams ? `?externalFiltersValue={${externalFiltersParams}}` : ''}`

        return encodeURI(resultUrl)
    }
}
