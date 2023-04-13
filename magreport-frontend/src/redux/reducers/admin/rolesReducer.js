import { ROLE_LIST_LOADED, ROLE_LIST_LOAD_FAILED, ROLE_CHANGE_WRITE_RIGHTS, ROLE_ADD, ROLE_DELETE, ROLE_FILTER, ROLE_SELECTED, ROLE_SELECTED_FOLDER_TYPE } from 'redux/reduxTypes'

const initialState = {
    selectedRole: null,
    data:[]
}

export const rolesReducer = (state = initialState, action) => {
    let arr = []
    let rights = []
    let filteredArr = []
    let newState
    let actualType = '';
    let newType='';
    switch (action.type){
        case ROLE_LIST_LOADED:
            for (let r of action.data){
                r.roleId = r.role.id
                r.id = r.role.id
                r.name = r.role.name
            }
            action.data.sort((a,b) => {
                if (a.role.name < b.role.name){
                    return -1
                }
                else if (a.role.name > b.role.name){
                    return 1
                }
                return 0
            })
            return {
                ...state, data: [...action.data]
            }

        case ROLE_LIST_LOAD_FAILED:
            return {
                ...state, data: action.error
            }

        case ROLE_CHANGE_WRITE_RIGHTS:
            arr = [...state.data]
            rights = ["READ"]
            if (action.value){
                rights.push("WRITE")
            }

            if (state.filteredData){
                filteredArr = [...state.filteredData]
                let roleId = filteredArr[action.index].roleId
                actualType = filteredArr[action.index].type
                newType = actualType === 'U' ? '':
                          actualType === 'I' ? 'I' : 'U';
                let changedRole = {...filteredArr[action.index], permissions: rights, type: newType}
                filteredArr.splice(action.index, 1, changedRole)


                let index = arr.findIndex(item => item.roleId === roleId)
                arr.splice(index, 1, changedRole)
                newState = {...state, data: arr, filteredData: filteredArr}
            }
            else {
                actualType = arr[action.index].type
                newType = actualType === 'U' ? '':
                          actualType === 'I' ? 'I' : 'U';
                arr.splice(action.index, 1, {...arr[action.index], permissions: rights, type: newType})
                newState = {...state, data: arr}
            }
            
            return newState

        case ROLE_ADD:
            arr = [...state.data]
            arr.push(action.role)
            newState = {...state, data: arr}
            if (state.filteredData && action.role.role.name.trim().toLowerCase().includes(state.filteredStr)){
                filteredArr = [...state.filteredData]
                filteredArr.push(action.role)
                newState.filteredData = filteredArr
            }
            return newState

        case ROLE_DELETE:
            arr = [...state.data];
            let deletedRoleIndex = arr.findIndex(item => item.roleId === action.roleId);
            actualType = arr[deletedRoleIndex].type;
            newType = actualType === 'U' ? 'UD':
                      actualType === 'I' ? 'ID' : 
                      actualType === 'D' ? '':
                      actualType === 'ID'? 'I':
                      actualType === 'UD' ? 'U' : 'D';

            arr[deletedRoleIndex].type = newType;

            if (state.filteredData){
                filteredArr = [...state.filteredData];
                deletedRoleIndex = filteredArr.findIndex(item => item.roleId === action.roleId);
                filteredArr[deletedRoleIndex].type = newType;

                newState = {...state, data: arr, filteredData: filteredArr};
            }
            else{
                newState = {...state, data: arr}
            }
            return newState

        case ROLE_FILTER:
            newState = {...state}
            if (action.filterStr){
                arr = [...state.data].filter(item => item.role.name.trim().toLowerCase().includes(action.filterStr))
                newState.filteredData = arr
                newState.filteredStr = action.filterStr
            }
            else {
                delete newState.filteredData
                delete newState.filteredStr
            }
            
            return newState
        
        case ROLE_SELECTED:
            return {...state, selectedRole: action.roleId}

        case ROLE_SELECTED_FOLDER_TYPE:
            return {...state, selectedFolderType: action.folderType}
        default:
            return state
    }
}