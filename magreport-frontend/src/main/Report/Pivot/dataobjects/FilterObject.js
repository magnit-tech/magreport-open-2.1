export function FilterObject(filterObject){

    const numOfDecPlaces = x => x==null ? 0 : ( (x.toString().includes('.')) ? (x.toString().split('.').pop().length) : (0) );

    this.field = filterObject.field;
    this.values  = filterObject.values ?? [];
    this.filterType = filterObject.filterType ?? (this.values.length > 0 ? 'EQUALS' : 'BLANK');
    this.invertResult = filterObject.invertResult ?? false;
    this.rounding = (filterObject.rounding || this.values.length === 0) ? filterObject.rounding : this.values.map(i => numOfDecPlaces(i)).reduce((acc, cur) => acc > cur ? acc : cur);
    this.canRounding = filterObject.canRounding ?? false;

    this.setValues = (values) => {
        this.values = values;
        this.rounding = values.length === 0 ? 0 : this.values.map(i => numOfDecPlaces(i)).reduce((acc, cur) => acc > cur ? acc : cur);
    }

    this.setFilterType = (filterType) => {
        this.filterType = filterType
        if (filterType === 'BETWEEN' && this.values.length !== 2){
            this.values = [this.values[0], null]
        }
        else if (filterType !== 'IN_LIST' && this.values.length > 1){
            this.values = [this.values[0]]
        }
        else if (filterType === 'IN_LIST'){
            this.values = []
        }
    }

    this.setInvertResult = (invertResult) => {
        this.invertResult = invertResult
    }

    this.setRounding = (rounding) => {
        this.rounding = rounding
    }

    this.setCanRounding = (canRounding) => {
        this.canRounding = canRounding;
    }
}