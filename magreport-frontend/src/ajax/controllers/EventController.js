const CONTROLLER_URL = '/event';
const METHOD = 'POST';

const REGISTER_EVENT_URL = CONTROLLER_URL + '/register';

export const eventNames = {
    moveFieldToColumnDimension : "Сводная.Изменение уровня.Столбцы.Добавление поля",
    moveFieldFromColumnDimension : "Сводная.Изменение уровня.Столбцы.Удаление поля",
    moveFieldToRowDimension : "Сводная.Изменение уровня.Строки.Добавление поля",
    moveFieldFromRowDimension : "Сводная.Изменение уровня.Строки.Удаление поля",  
    moveFieldToMetric : "Сводная.Метрики.Добавление метрики",  
    moveFieldFromMetric : "Сводная.Метрики.Удаление метрики"
}

export default function EventController(dataHub){

    this.register = function(typeEvent, additionally, callback){

        const body = {
            typeEvent : typeEvent,
            additionally : additionally
        }

        return dataHub.requestService(REGISTER_EVENT_URL, METHOD, body, callback ? callback : () => {});

    }

}