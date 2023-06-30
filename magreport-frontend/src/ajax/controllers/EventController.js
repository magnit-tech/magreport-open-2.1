const CONTROLLER_URL = '/event';
const METHOD = 'POST';

const REGISTER_EVENT_URL = CONTROLLER_URL + '/register';
const PIVOT_REGISTER_EVENT_URL = CONTROLLER_URL + '/pivot-register';

export const eventTypes = {

    fieldDragAndDrop : "DragAndDrop поля сводной",
    removeFieldByDelButton : "Удаление поля крестиком",
    addFilterByDimensionClick : "Добавление фильтра кликом по измерению",
    addFilterByMetricClick : "Добавление фильтра кликом по метрике",
    changeViewToPlain : "Переход в плоскую таблицу",
    changeMetricPlacement : "Изменение расположения метрик",
    changeMergeMode : "Изменение режима слияния",
    changeShowAllFields : "Изменение режима показа всех полей",
    restartReport : "Перезапуск отчёта",
    changeFullscreen : "Изменение полноэкранного режима",
    changeFieldPanelsVisibility : "Изменение видимости панелей с полями",
    openConfigDialog : "Открытие списка конфигураций",
    openSaveConfigDialog : "Открытие окна сохранения конфигураций",
    saveConfig : "Сохранение конфигурации",
    openShareDialog : "Открытия окна расшаривания задания",
    saveShareList : "Сохранение списка пользователей с доступом к заданию",
    savePublicConfiguration : "Предоставление и снятие общего доступа к конфигурации",
    openSortingDialog : "Открытие окна сортировки",
    exportToExcel : "Экспорт в Excel",
    openCreateDerivedFieldsEditor : "Открытие редактора производных полей",
    openTableContextMenu : "Открытие контекстного меню таблицы",
    openTableContextDialog : "Открытие окна диалога контекстного меню",
    addMetricSort : "Добавление сортировки по метрике",
    scrollColumns : "Прокрутка столбцов",
    scrollRows : "Прокрутка строк",
    editFilter : "Редактирование фильтра",
    editMetricFunction : "Редактирование функции метрики",
    renameMetric : "Переименование метрики",
    editMetric : "Редактирование метрики",
    switchFilterOnOff : "Включение и выключение фильтра"
}

export default function EventController(dataHub){

    this.register = function(typeEvent, additionally, callback){

        const body = {
            typeEvent : typeEvent,
            additionally : additionally
        }

        return dataHub.requestService(REGISTER_EVENT_URL, METHOD, body, callback ? callback : () => {});

    }

    this.pivotRegister = function(event, reportId, jobId, callback){

        let eventString;

        if(typeof(event) === "object"){
            eventString = JSON.stringify(event);
        }
        else{
            eventString = event.toString();
        }

        const body = {
            reportId : reportId,
            jobId: jobId,
            event: eventString
        }

        return dataHub.requestService(PIVOT_REGISTER_EVENT_URL, METHOD, body, callback ? callback : () => {});
    }

}