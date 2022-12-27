const CONTROLLER_URL = '/derived-field';
const METHOD = 'POST';
const FIELD_ADD = CONTROLLER_URL + '/add';

export default function DerivedFieldController(dataHub){

    this.add = (reportId, fieldName, fieldDesc, expression, callback) => {
        let body = {
            reportId: reportId,
            name: fieldName,
            description: fieldDesc,
            expression: expression
        }

        return dataHub.requestService(FIELD_ADD, METHOD, body, callback); 
    }
}