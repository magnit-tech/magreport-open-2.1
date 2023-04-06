# Язык формал Maglang

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
