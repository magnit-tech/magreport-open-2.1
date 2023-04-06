// Типы временных узлов
const tempNodeType = {
  comment : 0,
  ignore : 1,
  functionName : 2,
  elseExpression : 3,
  elifExpression : 4,
  compareOp : 5,
  logicOp : 6
}

export const nodeType = {
  unknown : -1,
  formulaRoot : 0,
  numLiteral : 1,
  stringLiteral : 2,
  booleanLiteral : 3,
  originalField : 4,
  derivedField : 5,
  arithmSum : 6,
  arithmSubtraction : 7,
  arithmProduct : 8,
  arithmFraction : 9,
  arithmIntDivision : 10,
  arithmModulo : 11,
  unaryArithmMinus : 12,
  functionCall : 13,
  ifExpression : 14,
  compareExpression : 15,
  logicOr : 16,
  logicAnd : 17,
  logicXor : 18,
  logicNot : 19
}

// Формирование узлов выходного дерева
export function createOutputNode(syntNode, code, errorList, originalFieldNameToIdMap, derivedFieldNameToIdMap, functionNameToIdMap){
    let outNode;

    if(syntNode.name === "Formula"){
      outNode = {
        nodeType: nodeType.formulaRoot
      }
    }
    else if(syntNode.name === "Number")
    {
      outNode = {
          nodeType: nodeType.numLiteral,
          value: Number(code.substring(syntNode.from, syntNode.to))
      }
    }
    else if(syntNode.name === "String"){
      outNode = {
        nodeType: nodeType.stringLiteral,
        value: code.substring(syntNode.from, syntNode.to)
      }
    }
    else if(syntNode.name === "BooleanLiteral"){
      outNode = {
        nodeType: nodeType.booleanLiteral,
        value: (code.substring(syntNode.from, syntNode.to) === "True")
      }
    }
    else if(syntNode.name === "UnaryMinus")
    {
      outNode = {
          nodeType: nodeType.unaryArithmMinus
      }
    }
    else if(syntNode.name === "Sum")
    {
      outNode = {
          nodeType: nodeType.arithmSum
      }
    }
    else if(syntNode.name === "Subtraction")
    {
      outNode = {
          nodeType: nodeType.arithmSubtraction
      }
    }        
    else if(syntNode.name === "Product")
    {
      outNode = {
          nodeType: nodeType.arithmProduct
      }
    }        
    else if(syntNode.name === "Fraction")
    {
      outNode = {
          nodeType: nodeType.arithmFraction
      }
    }
    else if(syntNode.name === "IntegerDivision")
    {
      outNode = {
          nodeType: nodeType.arithmIntDivision
      }
    }
    else if(syntNode.name === "IntegerModulo")
    {
      outNode = {
          nodeType: nodeType.arithmModulo
      }
    }    
    else if(syntNode.name === "OriginalFieldName"){
      let fieldName = code.substring(syntNode.from, syntNode.to);
      let fieldId = originalFieldNameToIdMap.get(fieldName);
      outNode = {
        nodeType: nodeType.originalField,
        fieldName: fieldName,
        fieldId: fieldId
      }
      if(fieldId === undefined){
        outNode.isError = true;
        outNode.errorMessage = "Неизвестное имя поля: " + fieldName;
      }
    }
    else if(syntNode.name === "DerivedFieldName"){
      let fieldName = code.substring(syntNode.from, syntNode.to);
      let fieldId = derivedFieldNameToIdMap.get(fieldName);
      outNode = {
        nodeType: nodeType.derivedField,
        fieldName: fieldName,
        fieldId: derivedFieldNameToIdMap.get(fieldName)
      }
      if(fieldId === undefined){
        outNode.isError = true;
        outNode.errorMessage = "Неизвестное имя производного поля: " + fieldName;
      }          
    }        
    else if(syntNode.name === "FunctionCallExpression")
    {
      outNode = {
          nodeType: nodeType.functionCall
      }
    }
    else if(syntNode.name === "FunctionName")
    {
      let functionName = code.substring(syntNode.from, syntNode.to);
      let functionId = functionNameToIdMap.get(functionName);
      outNode = {
          temp : true,
          tempNodeType : tempNodeType.functionName,
          functionName : functionName,
          functionId : functionNameToIdMap.get(functionName)
      }
      if(functionId === undefined){
        outNode.isError = true;
        outNode.errorMessage = "Неизвестное имя функции: " + functionName;
      }
    }
    else if(syntNode.name === "LineComment")
    {
      outNode = {
          temp : true,
          tempNodeType : tempNodeType.comment
      }
    }
    else if(syntNode.name === "IfExpression"){
      outNode = {
        nodeType: nodeType.ifExpression
      }
    }
    else if(syntNode.name === "ElseExpression"){
      outNode = {
        temp : true,
        tempNodeType: tempNodeType.elseExpression
      }
    }     
    else if(syntNode.name === "ElifExpression"){
      outNode = {
        temp : true,
        tempNodeType: tempNodeType.elifExpression
      }
    }  
    else if(syntNode.name === "CompareExpression"){
      outNode = {
        nodeType: nodeType.compareExpression
      }
    }
    else if(syntNode.name === "CompareOp"){
      outNode = {
        temp: true,
        tempNodeType : tempNodeType.compareOp,
        operation: code.substring(syntNode.from, syntNode.to)
      }
    }
    else if(syntNode.name === "LogicOr"){
      outNode = {
        nodeType: nodeType.logicOr
      }
    }
    else if(syntNode.name === "LogicAnd"){
      outNode = {
        nodeType: nodeType.logicAnd
      }
    }
    else if(syntNode.name === "LogicXor"){
      outNode = {
        nodeType: nodeType.logicXor
      }
    }
    else if(syntNode.name === "LogicNot"){
      outNode = {
        nodeType: nodeType.logicNot
      }
    }
    else if(syntNode.name.slice(-7) === "Keyword"){
      outNode = {
        temp: true,
        tempNodeType: tempNodeType.ignore
      }
    }
    else {
      outNode = {
        nodeType: nodeType.unknown
      }
    }

    if(!outNode.isError){
      outNode.isError = syntNode.type.isError;
      if(syntNode.type.isError){
        outNode.errorMessage = "Ошибочная синтаксическая конструкция: " + code.substring(syntNode.from, syntNode.to);
      }
    }
    outNode.nodeName = syntNode.name;

    if(outNode.isError){
      outNode.thisNodeError = true;
      
      errorList.push({
         errorMessage : outNode.errorMessage,
         from : syntNode.from,
         to : syntNode.to
      })
    }

    return outNode;

}

// Пост-обработка сформированного узла
export function processOutputChildren (parent, children){

    parent.children = [];
    for(let child of children){
      if(child.temp){
        if(child.tempNodeType === tempNodeType.functionName){// Обработка временного узла для имени функции
          parent.functionName = child.functionName;
        }
        else if(child.tempNodeType === tempNodeType.compareOp){// Обработка временного узла для операции сравнения
          parent.operation = child.operation;
        }
        else if(child.tempNodeType === tempNodeType.elifExpression){ 
          // Обработка elif-выражения - сам elif-узел ликвидируется, а его дочерние узлы 
          // добавляются в дочерние родительского if-узла
          parent.children.push(child.children[0]);
          parent.children.push(child.children[1]);
        }
        else if(child.tempNodeType === tempNodeType.elseExpression){
            // Обработка else-выражения - сам else-узел ликвидируется, а его дочерний узел 
            // добавляется в дочерние родительского if-узла        
          parent.children.push(child.children[0]);
        }
      }      
      else{
        parent.children.push(child);
      }
      parent.isError = parent.isError || child.isError;

      // Сокращение вложенности в некоторых специальных случаях

      if(parent.nodeType === nodeType.booleanExpression && parent.children.length === 1 && parent.children[0].nodeType === nodeType.booleanLiteral){
        // Если booleanExpression содержит единственный booleanLiteral - преобразовываем его в booleanLiteral
        parent.nodeType = nodeType.booleanLiteral;
        parent.nodeName = parent.children[0].nodeName;
        parent.value = parent.children[0].value;
        parent.children = [];
      }

      if(parent.nodeType === nodeType.unaryArithmMinus && parent.children.length === 1 && parent.children[0].nodeType === nodeType.numLiteral){
        // Если узел - это просто унарный минус преобразуем его в литерал с противоположным знаком
        parent.nodeType = nodeType.numLiteral;
        parent.nodeName = parent.children[0].nodeName;
        parent.value = -parent.children[0].value;
        parent.children = [];
      }
    }
}
