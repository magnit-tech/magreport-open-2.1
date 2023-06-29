import React from 'react';
import { connect } from 'react-redux';

import { useNavigate } from 'react-router-dom';

// local
import dataHub from 'ajax/DataHub';
import {actionFolderLoaded, actionFolderLoadFailed} from 'redux/actions/menuViews/folderActions';
import SidebarItems from '../../Sidebar/SidebarItems';

// components
import DataLoader from "../../../DataLoader/DataLoader";
import ASMList from "./ASMList";


function ASMAdministrationMenuView(props){

    const navigate = useNavigate()

    function handleAddItemClick() {
        navigate(`/ui/asm/add`)
    }

    const folderItemsType = SidebarItems.admin.subItems.ASMAdministration.folderItemType;

    return (
        <div  style={{display: 'flex', flex: 1}}>
            <DataLoader
                loadFunc={dataHub.asmController.getAll}
                loadParams={[]}
                reload={false}
                onDataLoaded={(data) => props.actionFolderLoaded(folderItemsType, data)}
                onDataLoadFailed={(error) => props.actionFolderLoadFailed(folderItemsType, error)}
            >
                <ASMList
                    data={props.state.currentFolderData}
                    onAddAsmClick={handleAddItemClick}
                />
            </DataLoader>
        </div>
    );
}

const mapStateToProps = state => {
    return {
        state: state.folderData
    }
}

const mapDispatchToProps = {
    actionFolderLoaded,
    actionFolderLoadFailed
}

export default connect(mapStateToProps, mapDispatchToProps)(ASMAdministrationMenuView);
