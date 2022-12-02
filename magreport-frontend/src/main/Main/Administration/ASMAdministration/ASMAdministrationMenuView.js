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


function ASMAdministrationMenuView(props){

    const navigate = useNavigate()

    const state = props.state;
    const needReload = state.needReload;
    // const designerMode = state.designerMode;

    const loadFunc = dataHub.asmController.getAll;

    function handleAddItemClick() {
        props.actionAsmAddItemClick();
        navigate(`/asm/add`)
    }

    return (
        <div  style={{display: 'flex', flex: 1}}>
            {
                state.flowState === asmAdministrationMenuViewFlowStates.externalSecurityList ?
                    <DataLoader
                        loadFunc={loadFunc}
                        loadParams={[]}
                        reload={{needReload}}
                        onDataLoaded={(data) => props.actionAsmListLoaded(data)}
                        onDataLoadFailed={(error) => props.actionAsmListLoadFailed(error)}
                    >
                        <ExternalSecurityList
                            data={state.data}
                            onAddAsmClick={handleAddItemClick}
                        />

                    </DataLoader>

                    // : state.flowState === asmAdministrationMenuViewFlowStates.externalSecurityViewer ?
                    // <ExternalSecurityViewer
                    //     asmId={state.viewASMId}
                    // />
                    // : state.flowState === asmAdministrationMenuViewFlowStates.externalSecurityDesigner ?
                    // <ExternalSecurityDesigner
                    //     designerMode={designerMode}
                    //     asmId={state.editASMId}
                    // />
                    : <div>Неизвестное состояние</div>
            }
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
