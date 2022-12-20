* alert - _данные для Alert_
    * data
        * open - _признак открытия окна_
        * title - _заголовок окна_
        * text - _текст сообщения_
        * buttons - _массив кнопок. Например, [{'text':'OK','onClick':handleDialogClose}]_
        * callback - _ссылка на функцию, которая отработает при закрытии окна_

* alertDialog - _данные для AlertDialog_
    * data
        * open - _признак открытия окна_
        * title - _заголовок окна_
        * entity - _сущность_        
        * entityType - _тип сущности_
        * callback(answer, entity, entityType)

* folderData - _данные предствления папок для всех страниц, которые используют folderData_
    * needReload - _перезагрузка текущего компонента_
    * currentFolderId - _id текущей папки. null для корневой папки_
    * currentFolderData - _объект data текущей папки (соответствует объекту, получаемому от get-folder)_
    * folderContentLoadErrorMessage - _сообщение об ошибке загрузки содержимого папки_
    * sortParams - _объект сортировки_