import React from 'react';
import { connect } from 'react-redux';

import { useNavigate } from 'react-router-dom'

// local
import dataHub from 'ajax/DataHub';
import { actionAsmListLoaded, actionAsmListLoadFailed, actionAsmAddItemClick } from "redux/actions/admin/actionAsm";
import { asmAdministrationMenuViewFlowStates } from "redux/reducers/menuViews/flowStates";

// components
import DataLoader from "../../../DataLoader/DataLoader";
import ExternalSecurityDesigner from "./ASMDesigner";
import ExternalSecurityViewer from "./ASMViewer";
import ExternalSecurityList from "./ASMList";
import { useState } from 'react';


function ASMAdministrationMenuView(props){

    const navigate = useNavigate()

    // const state = props.state;
    // const needReload = state.needReload;
    // const designerMode = state.designerMode;

    function handleAddItemClick() {
        // props.actionAsmAddItemClick();
        navigate(`/asm/add`)
    }

    const [data, setData] = useState()

    return (
        <div  style={{display: 'flex', flex: 1}}>
            <DataLoader
                loadFunc={dataHub.asmController.getAll}
                loadParams={[]}
                reload={false}
                onDataLoaded={(data) => setData(data)}
                onDataLoadFailed={(error) => setData(error)}
            >
                <ExternalSecurityList
                    data={data}
                    onAddAsmClick={handleAddItemClick}
                />
            </DataLoader>
        </div>
    );
}

const mapStateToProps = state => {
    return {
        state: state.asmAdministrationMenuView
    }
}

const mapDispatchToProps = {
    actionAsmListLoaded,
    actionAsmListLoadFailed,
    actionAsmAddItemClick
}

export default connect(mapStateToProps, mapDispatchToProps)(ASMAdministrationMenuView);
