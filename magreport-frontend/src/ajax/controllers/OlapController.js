import MagrepResponse from "ajax/MagrepResponse";

const CONTROLLER_URL = '/olap';
const METHOD = 'POST';

const GET_JOB_METADATA = '/report-job/get-metadata';
const GET_DERIVED_FIELDS = '/derived-field/get-by-report'

const GET_CUBE = CONTROLLER_URL + '/get-cube-new';
const GET_FIELD_VALUES = CONTROLLER_URL + '/get-field-values';
const GET_INFO_CUBES = CONTROLLER_URL + '/get-info-cubes';
const GET_LOG_INFO = CONTROLLER_URL + '/get-log-info';
const CREATE_EXCEL_PIVOT_TABLE = CONTROLLER_URL + '/create-excel-pivot-table';

//Configuration
const GET_CURRENT_CONFIG = CONTROLLER_URL + '/configuration/get-current';
const SAVE_CONFIG = CONTROLLER_URL + '/configuration/report-add';
const REPORT_GET = CONTROLLER_URL + '/configuration/report-get';
const DELETE_CONFIG = CONTROLLER_URL + '/configuration/delete';
const GET_AVAILABLE_CONFIGS = CONTROLLER_URL + '/configuration/get-available';
const SET_DEFAULT_CONFIG = CONTROLLER_URL + '/configuration/set-default';
const SAVE_GENERAL_ACCESS = CONTROLLER_URL + '/configuration/report-share';

export default function OlapController(dataHub){

    this.getJobMetadata = (jobId, callback) => {

        const body = {
            jobId : jobId
        }

        return dataHub.requestService(GET_JOB_METADATA, METHOD, body, callback); 
    }

    this.getJobMetadataExtended = (jobId, reportId, callback) => {

        const getMetadataRequest = {
            serviceUrl: GET_JOB_METADATA,
            method: METHOD,
            body: {
                jobId: jobId
            }
        }

        const getDerivedFieldRequest = {
            serviceUrl: GET_DERIVED_FIELDS,
            method: METHOD,
            body: {
                reportId: reportId
            }
        }

        let requestId;

        const middlewareCallback = (responses) => {
            let ok = responses[0].ok && responses[1].ok;
            let data;
            if(ok){
                data = responses[0].data;
                data.derivedFields = responses[1].data.map((f) => ({id: f.id, name: f.name, description: f.description, userName: f.userName, type: "DOUBLE"}));
            }
            else{
                console.log(responses);
                data = responses.filter((r) => !r.ok).join("; ");
            }

            callback(new MagrepResponse(ok, data, requestId));
        }

        requestId = dataHub.doMultipleRequests([getMetadataRequest, getDerivedFieldRequest], middlewareCallback);
        return requestId;
    }

    this.getCube = (olapRequest, callback) => {
        return dataHub.requestService(GET_CUBE, METHOD, olapRequest, callback);
    }

    this.getFieldType = (originalField) => {
        return originalField ? "REPORT_FIELD" : "DERIVED_FIELD";
    }

    this.getFieldValues = (fieldRequest, callback) => {
        return dataHub.requestService(GET_FIELD_VALUES, METHOD, fieldRequest, callback)
    }

    this.getInfoCubes = (callback) => {
        const body = {};
        return dataHub.requestService(GET_INFO_CUBES, METHOD, body, callback);
    }

    this.getLogInfo = (startDate, endDate, callback) => {
        const body = {
            startDate : startDate,
            endDate : endDate
        };

        return dataHub.requestService(GET_LOG_INFO, METHOD, body, callback);
    }

    this.createExcelPivotTable = (body, callback) => {
        return dataHub.requestService(CREATE_EXCEL_PIVOT_TABLE, METHOD, body, callback);
    }

    //Configuration
    this.getCurrentConfig = (jobId, callback) => {

        const body = { 
            jobId 
        }

        return dataHub.requestService(GET_CURRENT_CONFIG, METHOD, body, callback); 
    }

    this.saveConfig = (data, callback) => {

        const body = { 
            ...data
        }

        return dataHub.requestService(SAVE_CONFIG, METHOD, body, callback); 
    }

    this.getChoosenConfig = (id, callback) => {

        const body = { 
            reportOlapConfigId: id
        }

        return dataHub.requestService(REPORT_GET, METHOD, body, callback); 
    }

    this.deleteConfig = (olapConfigId, callback) => {

        const body = { 
            olapConfigId
        }

        return dataHub.requestService(DELETE_CONFIG, METHOD, body, callback); 
    }

    this.getAvailableConfigs = (jobId, callback) => {
        
        const body = { 
            jobId
        }

        return dataHub.requestService(GET_AVAILABLE_CONFIGS, METHOD, body, callback); 
    }

    this.setDefault = (reportOlapConfigId, callback) => {
        
        const body = { 
            reportOlapConfigId
        }

        return dataHub.requestService(SET_DEFAULT_CONFIG, METHOD, body, callback); 
    }

    this.reportShare = (reportOlapConfigId, eventValue, callback) => {
        
        const body = { 
            reportOlapConfigId,
            "share": eventValue
        }

        return dataHub.requestService(SAVE_GENERAL_ACCESS, METHOD, body, callback); 
    }
}