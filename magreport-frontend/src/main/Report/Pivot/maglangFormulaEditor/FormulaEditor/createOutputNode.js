// Типы временных узлов
const tempNodeType = {
    comment : 0,
    functionName : 1
}

export const nodeType = {
    unknown : -1,
    formulaRoot : 0,
    numLiteral : 1,
    stringLiteral : 2,
    originalField : 3,
    derivedField : 4,
    arithmSum : 5,
    arithmSubtraction : 6,
    arithmProduct : 7,
    arithmFraction : 8,
    unaryArithmMinus : 9,
    functionCall : 10
  }

export function createOutputNode(syntNode, code, errorList, originalFieldNameToIdMap, derivedFieldNameToIdMap, functionNameToIdMap){
    let outNode;


    if(syntNode.name === "Number")
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
    else if(syntNode.name === "Formula"){
      outNode = {
        nodeType: nodeType.formulaRoot
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

export function processOutputChildren (parent, children){

    parent.children = [];
    for(let child of children){
      if(child.temp)
      {
        if(child.tempNodeType === tempNodeType.functionName){
          parent.functionName = child.functionName;
        }
      } 
      else{
        parent.children.push(child);
      }
      parent.isError = parent.isError || child.isError;
    }
}
