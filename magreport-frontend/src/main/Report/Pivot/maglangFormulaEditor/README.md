# Язык формул Maglang

## Разработка языка

Разработка грамматики языка и инструментов преобразования синтаксического дерева разбора для удобного формата ведётся в отдельном проекте magreport-formulas-compiler (не публичный проект). Данный проект построен на шаблонном проекте https://github.com/codemirror/lang-example

В рамках этого проекта ведётся разработка файлов:

- src\main\Report\Pivot\maglangFormulaEditor\grammar\syntax.grammar - грамматика языка (см. https://lezer.codemirror.net/docs/guide/)
- src\main\Report\Pivot\maglangFormulaEditor\FormulaEditor\createOutputNode.js - функции обработки синтаксического дерева разбора и формирования промежуточного дерева для передачи во внешний обработчик
- src\main\Report\Pivot\maglangFormulaEditor\grammar\highlighting.js - цветовое выделение синтаксиса (см. https://lezer.codemirror.net/docs/ref/#highlight.tags)

Они редактируются там, компилируется язык в файл

    src\main\Report\Pivot\maglangFormulaEditor\maglang\maglang.js

и после этого копируются в этот проект.

После этого нужно соответсвующим образом отредактировать функции в

    src\main\Report\Pivot\UI\DerivedFieldDialog\lib\DFD_functions.js


## Описание языка

### Общее описание грамматики языка

Язык формул Maglang позволяет задавать формулы для вычисления производных полей. Производные поля можно представлять себе, как вычисляемые столбцы, добавляемые в исходную таблицу сформированного набора данных отчёта -- при этом в каждой строке в соответствующую ячейку столбца записывается значение, вычисленное по соответствующей формуле по значениям других ячеек в данной строке таблицы. Для описания формул используются следующие языковые конструкции:

- *выражения* -- часть формулы, которая может быть вычислена как числовое, логическое, строковое значение или значение момента времени (даты или даты со временем); вся формула также представляет собой некоторое выражение;
- *константы* -- числовые, символьные и логические литералы; числовые литералы записываются в обычной форме записи с целой частью, дробной частью и экспонентой, нулевую целую часть можно опускать (3, -2.57, .18, 2E3, -3.5e4); целочисленные литералы представляются в системе через 64-битный целочисленный знаковый тип, вещественные -- через 64-битный тип чисел с плавающей запятой; символьные литералы записываются в виде строк, заключённых в двойные кавычки ("понедельник"); логически литералы записываются при помощи ключевых слов **True** и **False**; литералов для задания моментов времени (даты и даты со временем) не предусмотрено -- для их задания необходимо использовать соответствующие функции;
- *арифметические выражения* -- для числовых выражений доступно вычисление арифметических операций: сложение (+), вычитание (-), умножение (*), вещественное деление (/), целочисленное деление (то есть, целая часть от деления: //), вычисление остатка от деления (%) и унарный минус (знак "-" перед выражением); при вычислении арифметических выражений действуют обычные правила приоритета операций, для управления порядком вычислений можно использовать круглые скобки;
- *исходные поля отчёта* -- при вычислении выражений можно использовать значения полей, исходно присутствующих в отчёте, для этого, названия этих полей заключаются в одинарные квадратные скобки (например, [Продажи], [Потери], [Остаток]);
- *производные поля* -- при вычислении выражений можно использовать значения других производных полей (при этом главное, чтобы не было рекурсивных зависимостей в вычислениях), для этого, названия этих полей заключаются в двойные квадратные скобки (например, [[Маржинальность]], [[Относительные потери]]); имена производных полей, создаваемых конкретным пользователем обязаны быть уникальными, однако глобальная уникальность не требуется, также уникальными обязаны быть имена общедоступных производных полей -- производных полей, созданных разработчиком отчёта и объявленных, как общедоступные; при работе с производными полями пользователю доступны поля из текущей области видимости производных полей, которая включает в себя общедоступные производные поля, производные поля, созданные данным пользователем, и производные поля, присутствующие в данной конфигурации сводной таблицы; если в области видимости производных полей возникает дублирование имен, то более приоритетные поля остаются просто со своим именем, а к имени менее приоритетных через знак ";" (точка с запятой) добавляется логин пользователя, создавшего поле, приоритет полей от большего к меньшему: общедоступные производные поля, производные поля данного пользователя, производные поля других пользователей;
- *функции* -- при вычислении выражений могут использоваться функции из библиотеки функций, аргументами функций служат выражения, аргументы записываются через запятую;
- *условный оператор* -- условный оператор имеет вид:

        if(логическое выражение 1){
            выражение 1
        }
        elif(логическое выражение 2){
            выражение 2
        }
        elif(логическое выражение 3){
            выражение 3
        }
        ...
        elif(логическое выражение N){
            выражение N
        }
        else{
            выражение по умолчанию
        }

при вычислении его возвращаемого значения происходит последовательное вычисление логических выражений вплоть до первого верного, при этом возвращается значение соответствующего выражения; если все условные выражения ложны -- возвращается значение выражения по умолчанию; блоки **elif** в условном операторе могут отсутствовать, блоки **if** и **else** обязательны;

- *логические выражения* -- логические выражения формируются при помощи *операций сравнения* и *логических операций*;
- *операции сравнения* -- стандартные операции меньше (<), больше (>), меньше либо равно (<=), больше либо равно (>=), равно (=), не равно (!= или <>);
- *логические операции* -- стандартные логические операции: логическое И (and), логическое ИЛИ (or), логическое ИСКЛЮЧАЮЩЕЕ ИЛИ (xor) и логическое ОТРИЦАНИЕ (not) -- последняя операция требует взятия в скобки инвертируемого логического выражения (например, "not ([Продажи] < [Потери] and [Остаток] > 0)").

### Примеры

#### Номер месяца
Пусть у нас в отчёте есть поле [**Дата**], но нет номера месяца в году. Создадим производное поле [[**Месяц из даты**]]:

    MONTH_FROM_DATE([Дата])

#### Название месяца
Пусть у нас есть производное поле [[**Месяц из даты**]], которое даёт числовое выражение, но нам нужно иметь название месяца. Создадим производное поле [[**Название месяца**]]:

    if([[Месяц из даты]] = 1){
        "Январь"
    }
    elif([[Месяц из даты]] = 2){
        "Февраль"
    }
    elif([[Месяц из даты]] = 3){
        "Март"
    }
    elif([[Месяц из даты]] = 4){
        "Апрель"
    }
    elif([[Месяц из даты]] = 5){
        "Май"
    }
    elif([[Месяц из даты]] = 6){
        "Июнь"
    }
    elif([[Месяц из даты]] = 7){
        "Июль"
    }
    elif([[Месяц из даты]] = 8){
        "Август"
    }
    elif([[Месяц из даты]] = 9){
        "Сентябрь"
    }
    elif([[Месяц из даты]] = 10){
        "Октябрь"
    }
    elif([[Месяц из даты]] = 11){
        "Ноябрь"
    }
    else{
        "Декабрь"
    }

#### Номер квартала года
Пусть у нас есть производное поле [[**Месяц из даты**]], но нет номера квартала года. Создадим производное поле [[**Квартал**]]:

    ([[Месяц из даты]] - 1) // 3 + 1

Нетрудно понять, что с учётом целочисленного деления данная формула даёт номер квартала года.

#### Номер года в двузначном формате
Пусть у нас в отчёте есть поле [**Дата**], но нет номера года. Нам нужен номер года в двузначном формате. Создадим производное поле:

    YEAR_FROM_DATE([Дата]) % 100

#### Категория по обороту
Пусть у вас есть подневные продажи товаров на магазинах и вы хотите классифицировать эти данные по объёму продаж в рублях. Вы можете создать производное поле [[**Категория по обороту**]]:

    if([Оборот] < 100){
        "Оборот 1: < 100"
    }
    elif([Оборот] < 300){
        "Оборот 2: 100 - 300"
    }
    elif([Оборот] < 500){
        "Оборот 3: 300 - 500"
    }
    elif([Оборот] < 1000){
        "Оборот 4: 500 - 1000"
    }
    elif([Оборот с учетом скидок, руб] < 10000){
        "Оборот 5: 1000 - 10000"
    }
    else{
        "Оборот 6: > 10000"
    }

Обратите внимание на именование введённых категорий: "Оборот 1...", "Оборот 2...", и т.д. - выбрана такая система наименований, которая даёт нужный порядок этих значений при лексикографической сортировке. После создания этого производного поля его можно использовать, например, для вычисления количества товарных позиций в каждой из категорий в данный день на данном магазине или для вычисления суммарного оборота по каждой категории.

#### Средняя цена
Пусть у вас есть подневные продажи товаров на магазинах, в том числе данные по оборотам в рублях и штуках, но нет данных по цене, и вы хотите вычислять такой показатель, как средняя цена, усредняя её по различным измерениям. Базовую формулу для производного поля [[**Средняя цена**]] вы можете записать следующим образом:

    [Оборот, руб] / [Оборот, шт]

По такой формуле поле будет вычислено для тех строк, для которых [Оборот, шт] не равен 0. При дальнейшей агрегации (например, по метрике "среднее значение") полученное производное поле будет принимать числовое значение для тех множеств, которое не содержать нулевые значения [Оборот, шт]. Для других же оно будет выдавать значение NaN. Если нули в значениях [Оборот, шт] не редкость, то на высоких уровнях агрегации вероятность появления NaN весьма высока. Поэтому к знаменателю формулы следует применить функцию ZERO_TO_NULL, превращающую нулевые значения в значения NULL, и, как следствие -- значение самого производного поля для таких значений тоже в NULL. При агрегации такие значения не учитываются и дают возможность вычислить агрегат по нормально вычисленным значениям производного поля. Итоговая формула:

    [Оборот с учетом скидок, руб] / ZERO_TO_NULL([Оборот, шт])

#### Время выполнения отчёта на БД
Пусть у вас отчёт по статистике выполнения отчётов в Магрепорт и в нём есть поля [**Время старта выполнения**] и [**Время получения данных**], а вас интересует время выполнения отчёта в секундах - то есть разница между двумя этими моментами времени. Каждое из этих полей может содержать NULL-значение (выполнение отчёта могло завершиться аварийно и не перейти в соответствующую стадию). Чтобы не учитывать такие значения при агрегации вы хотите в этом случае интересующее вас значение разности учитывать тоже как NULL. Получаем такую формулу:

    if( IS_NULL([Время старта выполнения])=True or
        IS_NULL([Время получения данных])=True){
            NULL_VALUE(GET_TYPE(0.0))
    }
    else{
        MILLSEC_INTERVAL(
            [Время старта выполнения],
            [Время получения данных]
        ) 
            / 1000
    }

Обратите внимание на конструкцию **NULL_VALUE(GET_TYPE(0))**: здесь мы возвращаем NULL-значение, имеющее нужный нам тип. Чтобы не задумываться о том, как правильно называется в Магрепорт тип чисел с плавающей точкой (он называется **DOUBLE**), мы используем функцию, вычисляющую название типа GET_TYPE. В данном случае функция применятся к константе, но она так же может применяться к полю отчёта или производному полю для анализа типа значения этого поля.