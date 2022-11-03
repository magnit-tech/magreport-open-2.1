const CONTROLLER_URL = '/olap';
const METHOD = 'POST';

const GET_JOB_METADATA = '/report-job/get-metadata';
const GET_CUBE = CONTROLLER_URL + '/get-cube';
const GET_FIELD_VALUES = CONTROLLER_URL + '/get-field-values';
const GET_INFO_CUBES = CONTROLLER_URL + '/get-info-cubes';
const GET_LOG_INFO = CONTROLLER_URL + '/get-log-info';

//Configuration
const GET_CURRENT_CONFIG = CONTROLLER_URL + '/configuration/get-current';
const SAVE_CONFIG = CONTROLLER_URL + '/configuration/report-add';
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

    this.getCube = (olapRequest, callback) => {
        return dataHub.requestService(GET_CUBE, METHOD, olapRequest, callback);
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