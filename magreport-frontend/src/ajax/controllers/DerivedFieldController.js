import MagrepResponse from "ajax/MagrepResponse";

const CONTROLLER_URL = '/derived-field';
const METHOD = 'POST';

const GET_JOB_METADATA = '/report-job/get-metadata';

const FIELD_ADD = CONTROLLER_URL + '/add';
const GET_DERIVED_FIELDS = CONTROLLER_URL + '/get-by-report';
const EXPRESSIONS_GET_ALL =  CONTROLLER_URL + '/expressions/get-all';


export default function DerivedFieldController(dataHub){

    this.add = (reportId, fieldName, fieldDesc, expression, expressionText, callback) => {
        let body = {
            reportId: reportId,
            isPublic: false,
            name: fieldName,
            description: fieldDesc,
            expression: expression,
            expressionText: expressionText
        }

        return dataHub.requestService(FIELD_ADD, METHOD, body, callback); 
    }

    this.expressionsGetAll = (callback) => {
        return dataHub.requestService(EXPRESSIONS_GET_ALL, METHOD, {}, callback);
    }

    this.getFieldsAndExpressions = (jobId, reportId, callback) => {

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

        const getExpressionsRequest = {
            serviceUrl: EXPRESSIONS_GET_ALL,
            method: METHOD,
            body: {
            }
        }        

        let requestId;

        const middlewareCallback = (responses) => {
            let ok = responses[0].ok && responses[1].ok && responses[2].ok;
            let data;
            if(ok){
                data = responses[0].data;
                data.derivedFields = responses[1].data.map((f) => ({id: f.id, name: f.name, description: f.description, userName: f.userName, type: "DOUBLE"}));
                data.expressions = responses[2].data;
            }
            else{
                data = responses.filter((r) => !r.ok).join("; ");
            }

            callback(new MagrepResponse(ok, data, requestId));
        }

        requestId = dataHub.doMultipleRequests([getMetadataRequest, getDerivedFieldRequest, getExpressionsRequest], middlewareCallback);
        return requestId;
    }    
}