import {
    FOLDER_CONTENT_LOAD_FAILED, 
    FOLDER_CONTENT_LOADED,
    FOLDER_CONTENT_FOLDER_ADDED,
    FOLDER_CONTENT_FOLDER_EDITED,
    FOLDER_CONTENT_FOLDER_DELETED,
    FOLDER_CONTENT_ITEM_ADDED,
    FOLDER_CONTENT_ITEM_EDITED,
    FOLDER_CONTENT_ITEM_DELETED,
    FOLDER_CONTENT_SEARCH_CLICK, 
    FOLDER_CONTENT_SORT_CLICK,
    FOLDER_CONTENT_SEARCH_RESULTS_LOADED,
    FOLDER_CONTENT_PARENT_FOLDER_CHANGED,
    FOLDER_CONTENT_FOLDER_COPIED,
    FOLDER_CONTENT_ITEM_MOVED,
    FOLDER_CONTENT_ITEM_COPIED,
    FAVORITES_ADD_START, 
    FAVORITES_ADDED, 
    FAVORITES_DELETE_START, 
    FAVORITES_DELETED,
    JOBS_FILTER,
    TASK_SWITCHED
} from 'redux/reduxTypes';
import {FolderItemTypes} from 'main/FolderContent/FolderItemTypes';
// import {FLOW_STATE_BROWSE_FOLDER} from './menuViews/flowStates';    

const getItemName = itemsType => {
    return  itemsType === FolderItemTypes.reports ? "reports" :
            itemsType === FolderItemTypes.favorites ? "reports" :
            itemsType === FolderItemTypes.job || itemsType === FolderItemTypes.userJobs ? "jobs" :
            itemsType === FolderItemTypes.datasource ? "dataSources" :
            itemsType === FolderItemTypes.dataset ? "dataSets" :
            itemsType === FolderItemTypes.filterTemplate ? "filterTemplates" :
            itemsType === FolderItemTypes.filterInstance ? "filterInstances" :
            itemsType === FolderItemTypes.reportsDev ? "reports" :
            itemsType === FolderItemTypes.roles ? "roles" :
            itemsType === FolderItemTypes.securityFilters ? "securityFilters" :
            itemsType === FolderItemTypes.schedules ? "schedules" :
            itemsType === FolderItemTypes.scheduleTasks ? "scheduleTasks" :
            itemsType === FolderItemTypes.systemMailTemplates ? "systemMailTemplates":
            itemsType === FolderItemTypes.theme ? "themes" :
            itemsType === FolderItemTypes.cubes ? "cubes" :
            "";
    
}

const initialState = {
	currentFolderId : null,
    needReload : true,
    currentFolderData : null,
    folderContentLoadErrorMessage : null,
}

export function folderDataReducer(state = initialState, action, sidebarItem, folderItemsType){
    switch(action.type){
        
        case FOLDER_CONTENT_LOADED:
			delete state.searchParams;

			let itemsName = getItemName(action.itemsType),
				sortParams = JSON.parse(localStorage.getItem("sortParams")),
				childFolders = action.folderData.childFolders,
				items = action.folderData[itemsName];

			if(!sortParams) {
				const defaultSortParams = { key: 'name', direction: 'ascending' };
				localStorage.setItem("sortParams", JSON.stringify(defaultSortParams));
				sortParams = defaultSortParams;
			}
				
			if (action.isSortingAvailable && (childFolders || items)) {
				if (childFolders && childFolders.length > 0) {
					childFolders.sort((a, b) => {
						if (a[sortParams.key] < b[sortParams.key]) {
							return sortParams.direction === 'ascending' ? -1 : 1;
						}
						if (a[sortParams.key] > b[sortParams.key]) {
							return sortParams.direction === 'ascending' ? 1 : -1;
						}
							return 0;
					});
				}

				if (items && items.length > 0) {
					items.sort((a, b) => {
						if (a[sortParams.key] < b[sortParams.key]) {
							return sortParams.direction === 'ascending' ? -1 : 1;
						}
						if (a[sortParams.key] > b[sortParams.key]) {
							return sortParams.direction === 'ascending' ? 1 : -1;
						}
							return 0;
					});
				}

				const filteredFolderData = {
					...action.folderData,
					childFolders,
					[itemsName]: items
				}

				return {
					...state,
					needReload : false,
					currentFolderData : action.folderData,
					filteredFolderData,
					sortParams
				}} else {
					return {
						...state,
						needReload : false,
						currentFolderData : action.folderData,
					}
				}

        case FOLDER_CONTENT_LOAD_FAILED:
            return{
                ...state,
                needReload : false,
                folderContentLoadErrorMessage : action.errorMessage
            }

        case FOLDER_CONTENT_FOLDER_ADDED:
        case FOLDER_CONTENT_FOLDER_EDITED:
        case FOLDER_CONTENT_FOLDER_DELETED:
            return{
                ...state,
                needReload : true
            } 

        case FOLDER_CONTENT_ITEM_ADDED:
        case FOLDER_CONTENT_ITEM_EDITED:
        case FOLDER_CONTENT_ITEM_DELETED:
            return{
                ...state,
                needReload : true
            }

        case FOLDER_CONTENT_SEARCH_CLICK:
            let findStr = action.searchParams.searchString.toLowerCase().trim()
            let sortParamsForSearch = state.sortParams
            let itemsNameForSearch = getItemName(action.itemsType)

            if(!sortParamsForSearch) {
                const defaultSortParams = { key: 'name', direction: 'ascending' };
                localStorage.setItem("sortParams", JSON.stringify(defaultSortParams));
                sortParamsForSearch = defaultSortParams;
            }

            if (findStr.length > 0){
                const childFolders = state.currentFolderData.childFolders.filter(folder => folder.name.toLowerCase().includes(findStr)),
                    items = state.currentFolderData[itemsNameForSearch].filter(item => item.name.toLowerCase().includes(findStr));

                if (childFolders.length > 0) {
                    childFolders.sort((a, b) => {
                        if (a[sortParamsForSearch.key] < b[sortParamsForSearch.key]) {
                            return sortParamsForSearch.direction === 'ascending' ? -1 : 1;
                        }
                        if (a[sortParamsForSearch.key] > b[sortParamsForSearch.key]) {
                            return sortParamsForSearch.direction === 'ascending' ? 1 : -1;
                        }
                            return 0;
                    });
                }

                if (items.length > 0) {
                    items.sort((a, b) => {
                        if (a[sortParamsForSearch.key] < b[sortParamsForSearch.key]) {
                            return sortParamsForSearch.direction === 'ascending' ? -1 : 1;
                        }
                        if (a[sortParamsForSearch.key] > b[sortParamsForSearch.key]) {
                            return sortParamsForSearch.direction === 'ascending' ? 1 : -1;
                        }
                            return 0;
                    });
                }

                const filteredFolderData = {
                    ...state.currentFolderData,
                    childFolders,
                    [itemsNameForSearch]: items
                }

                return {...state, filteredFolderData, searchParams: action.searchParams}
            }
            else {
                const childFolders = state.currentFolderData.childFolders,
                    items = state.currentFolderData[itemsNameForSearch];

                if (childFolders.length > 0) {
                    childFolders.sort((a, b) => {
                        if (a[sortParamsForSearch.key] < b[sortParamsForSearch.key]) {
                            return sortParamsForSearch.direction === 'ascending' ? -1 : 1;
                        }
                        if (a[sortParamsForSearch.key] > b[sortParamsForSearch.key]) {
                            return sortParamsForSearch.direction === 'ascending' ? 1 : -1;
                        }
                            return 0;
                    });
                }

                if (items.length > 0) {
                    items.sort((a, b) => {
                        if (a[sortParamsForSearch.key] < b[sortParamsForSearch.key]) {
                            return sortParamsForSearch.direction === 'ascending' ? -1 : 1;
                        }
                        if (a[sortParamsForSearch.key] > b[sortParamsForSearch.key]) {
                            return sortParamsForSearch.direction === 'ascending' ? 1 : -1;
                        }
                            return 0;
                    });
                }
                
                const filteredFolderData = {
                    ...state.currentFolderData,
                    childFolders,
                    [itemsNameForSearch]: items
                }

                delete state.searchParams
                return {...state, filteredFolderData}
            }

        case FOLDER_CONTENT_SORT_CLICK:
            localStorage.setItem("sortParams", JSON.stringify(action.sortParams));

            let itemsNameForSort = getItemName(action.itemsType),
                key = action.sortParams.key,
                direction = action.sortParams.direction,
                childFoldersForSort = null,
                itemsForSort = null;

            if (state.searchParams) {
                childFoldersForSort = [...state.filteredFolderData.childFolders];
                itemsForSort = [...state.filteredFolderData[itemsNameForSort]]
            } else {
                childFoldersForSort = [...state.currentFolderData.childFolders];
                itemsForSort = [...state.currentFolderData[itemsNameForSort]]
            }
            

            if (childFoldersForSort.length > 0) {
                childFoldersForSort.sort((a, b) => {
                    if (a[key] < b[key]) {
                        return direction === 'ascending' ? -1 : 1;
                    }
                    if (a[key] > b[key]) {
                        return direction === 'ascending' ? 1 : -1;
                    }
                        return 0;
                });
            }

            if (itemsForSort.length > 0) {
                itemsForSort.sort((a, b) => {
                    if (a[key] < b[key]) {
                        return direction === 'ascending' ? -1 : 1;
                    }
                    if (a[key] > b[key]) {
                        return direction === 'ascending' ? 1 : -1;
                    }
                        return 0;
                });
            }

            const filteredFolderData = {
                ...state.currentFolderData,
                childFolders: childFoldersForSort,
                [itemsNameForSort]: itemsForSort
            }

            return {...state, filteredFolderData, sortParams: action.sortParams}

        case FOLDER_CONTENT_SEARCH_RESULTS_LOADED:
            const findStrResultsLoaded = action.searchParams.searchString.toLowerCase().trim()
            if (findStrResultsLoaded.length > 0){
                let itemsName = getItemName(action.itemsType)
                const childFolders = action.data.folders.map(f => {
                    return {
                        id: f.folder.id,
                        name: f.folder.name,
                        description: f.folder.name,
                        created: f.folder.created,
                        modified: f.folder.modified,
                        path: f.path
                    }
                })
                const objects = action.data.objects.map(o => {return {...o['element'], path: o['path'] }} );
                const filteredFolderData = {
                    childFolders,
                    [itemsName]: objects
                }
                return {...state, filteredFolderData, searchParams: action.searchParams}
            }
            else {
                delete state.filteredFolderData
                delete state.searchParams
                return {...state}
            }

        case FOLDER_CONTENT_PARENT_FOLDER_CHANGED:
            return{
                ...state,
                needReload : true
            }

        case FOLDER_CONTENT_FOLDER_COPIED:
            return{
                ...state,
                needReload : true
            }

        case FOLDER_CONTENT_ITEM_MOVED:
            return{
                ...state,
                needReload : true
            }

        case FOLDER_CONTENT_ITEM_COPIED:
            return{
                ...state,
                needReload : true
            }

        case FAVORITES_ADD_START:
        case FAVORITES_DELETE_START:
            return{
                ...state,
                needReload : true
            }
        
        case FAVORITES_ADDED:
        case FAVORITES_DELETED:
            let folderData = {...state.currentFolderData}
            if (state.searchParams){
                folderData = {...state.filteredFolderData}
            }
            
            // const reportIndex = folderData.reports.findIndex(r => r.id === action.reportId)
            const reportIndex = action.index
            let report = {...folderData.reports[reportIndex]}
            report.favorite = !report.favorite
            
            if (action.favorite && action.itemsType === FolderItemTypes.favorites){
                folderData.reports.splice(reportIndex, 1)
            }
            else {
                folderData.reports.splice(reportIndex, 1, report)
            }
            if (state.searchParams){
                return {...state, filteredFolderData: folderData}
            }
            return {...state, currentFolderData: folderData}

        case JOBS_FILTER:
            let newStateJF = {}
                if (action.filters.isCleared){
                    newStateJF = {...state}
                    delete newStateJF.filters
                }
                else {
                    newStateJF = {
                        ...state, filters: action.filters
                    }
                }
                return newStateJF
        case TASK_SWITCHED:
                        let newStateTS = {...state}
                        const fullArr = [...state.currentFolderData.scheduleTasks]
                        const filteredArr = [...state.filteredFolderData.scheduleTasks]
        
                        let newTask = {};
        
                        if (state.searchParams){
                            newTask = {...filteredArr[action.taskIndex], status: action.status}
                            let indexFullArr = fullArr.findIndex(item => item.id === action.taskId)
                            filteredArr.splice(action.taskIndex, 1, newTask)
                            fullArr.splice(indexFullArr, 1, newTask)
                            
                        }
                        else {
                            newTask = {...fullArr[action.taskIndex], status: action.status}
                            fullArr.splice(action.taskIndex, 1, newTask)
                            filteredArr.splice(action.taskIndex, 1, newTask)
                        }
                        newStateTS.filteredFolderData.scheduleTasks = filteredArr;
                        newStateTS.currentFolderData.scheduleTasks = fullArr;
                        return newStateTS
        default:
            return state;
    }    
}