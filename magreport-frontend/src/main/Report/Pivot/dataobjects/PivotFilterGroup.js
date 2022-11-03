export function PivotFilterGroup(filterGroup){

    this.createFilterGroup = (filterGroup) => {
        this.childGroups = filterGroup?.childGroup ?? [];
        this.filters = filterGroup?.filters ?? [];
        this.invertResult = filterGroup?.invertResult ?? false;
        this.operationType = filterGroup?.operationType ?? 'AND'
    }

    this.replaceFilter = (filterFieldsList) => {
        this.filters = filterFieldsList.map(item => item.filter);
    }

    this.createFilterGroup(filterGroup);

}