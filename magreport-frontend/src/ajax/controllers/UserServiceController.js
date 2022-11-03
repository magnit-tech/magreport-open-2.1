const CONTROLLER_URL = '/user-services';
const METHOD = 'POST';

const CHECK_PERMISSION = CONTROLLER_URL + '/check-permission';
const GET_DOMAIN_LIST = CONTROLLER_URL + '/get-domain-list';

export default function UserController(dataHub){

    this.checkPermission = function (id, callback){
        const body = {
			folderType: "REPORT_FOLDER",
			id
		};

        return dataHub.requestService(CHECK_PERMISSION, METHOD, body, callback);
    }

    this.getDomainList = function (callback){
        const body = {};
        return dataHub.requestService(GET_DOMAIN_LIST, METHOD, body, callback);
    }

}


