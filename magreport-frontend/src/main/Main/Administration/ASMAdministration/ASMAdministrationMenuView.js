import React, { useState } from 'react';
import { connect } from 'react-redux';

import { useNavigate } from 'react-router-dom'

// local
import dataHub from 'ajax/DataHub';
import { actionAsmListLoaded, actionAsmListLoadFailed } from "redux/actions/admin/actionAsm";

// components
import DataLoader from "../../../DataLoader/DataLoader";
import ExternalSecurityList from "./ASMList";


function ASMAdministrationMenuView(){

    const navigate = useNavigate()

    function handleAddItemClick() {
        navigate(`/ui/asm/add`)
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
}

export default connect(mapStateToProps, mapDispatchToProps)(ASMAdministrationMenuView);
