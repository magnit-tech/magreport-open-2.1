const CONTROLLER_URL = '/ad';
const METHOD = 'POST';

const AD_GET_DOMAIN_GROUPS_URL = CONTROLLER_URL + '/get-domain-groups';


export default function AdController(dataHub){

    this.getDomainGroups = function (maxResults, domainNames, namePart, callback){
        const body = {
            maxResults,
            domainNames,
            namePart,
        };
        return dataHub.requestService(AD_GET_DOMAIN_GROUPS_URL, METHOD, body, callback);
    }

}

