import React, {useRef, useState, useCallback, useMemo} from "react";
import CodeMirror from '@uiw/react-codemirror';
import { createTheme } from '@uiw/codemirror-themes';
import {completeFromList} from "@codemirror/autocomplete";
import {LanguageSupport} from '@codemirror/language';
import { MagreportLanguage } from "../maglang/maglang.js";
import {tags as t } from '@lezer/highlight';

import "./FormulaEditor.css"
import { TextField } from "@material-ui/core";

// Примеры highlight смотреть здесь:
// https://github.com/codemirror/highlight/blob/main/src/highlight.ts 

/**
 * 
 * @param {*} props.height
 * @param {*} props.disabled
 * @param {*} props.initialCode - исходный текст формулы с id вместо названий
 * @param {*} props.functions - массив описаний функций: {functionId, functionName, functionDesc, functionSignature}
 * @param {*} props.originalFields - массив объектов исходных полей отчёта {fieldId, fieldName, fieldDesc, valueType}
 * @param {*} props.derivedFields - массив объектов производных полей отчёта {fieldId, fieldName, fieldDesc, fieldOwner, valueType}
 * @param {*} props.onChange - function(compilationResult) - массив объектов производных полей отчёта
 *                              compilationResult:
 *                                 success: true | false
 *                                 textToSave: string
 *                                 treeRoot - корень дерево разбора
 *                                 errorList - массив объектов
 *                                          {
 *                                            errorMessage
 *                                            from
 *                                            to
 *                                          }
 * 
 *  
 * @returns 
 */

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

const tempNodeType = {
  comment : 0,
  functionName : 1
}

export default function FormulaEditor(props){

    const editor = useRef();
    const [errorMessages, setErrorMessages] = useState("");

    /*
      Theme
    */
    const codeEditorTheme = createTheme({
        theme: 'light',
        settings: {
          background: '#ffffff',
          foreground: props.disabled ? 'grey' : '#75baff',
          caret: '#5d00ff',
          selection: '#036dd626',
          selectionMatch: '#036dd626',
          lineHighlight: '#8a91991a',
          gutterBackground: '#fff',
          gutterForeground: '#8a919966',
        },
        styles: [
          { tag: t.comment, color: '#787b8099' },
          { tag: t.variableName, color: props.disabled ? 'grey' : '#0080ff' , fontWeight: "bold"},
          { tag: t.propertyName, color: '#00b300' , fontWeight: "bold"},
          { tag: t.function(t.variableName), color: '#862d59' , fontWeight: "normal"},
          { tag: [t.string, t.special(t.brace)], color: '#5c6166' },
          { tag: t.number, color: '#5c6166' },
          { tag: t.operator, color: '#5c6166' },
          { tag: t.angleBracket, color: '#5c6166' },
          { tag: t.tagName, color: '#5c6166' },
          { tag: t.attributeName, color: '#5c6166' },
        ],
      });

    /*
      Completion
    */
    const functionsCompletionList = props.functions.map((v) => ({label: v.functionName, type: "function", detail: v.functionDesc, info: v.functionSignature}));
    const originalFieldsCompletionList = props.originalFields.map((v) => ({label: v.fieldName, type: "variable", detail: v.fieldDesc}));
    const derivedFieldsCompletionList = props.derivedFields.map((v) => ({label: v.fieldName, type: "variable", detail: v.fieldOwner + ": " + v.fieldDesc}));

    const completionList = functionsCompletionList.concat(originalFieldsCompletionList.concat(derivedFieldsCompletionList));

    const completion = MagreportLanguage.data.of({
      autocomplete: completeFromList(
        completionList
      )
    });

    /*
      Mappings between names and ids
    */

    const [functionIdToName, functionNameToId] = useMemo(() => {
        let mIdtoName = new Map();
        let mNametoId = new Map();
        props.functions.forEach((f) => {mIdtoName.set(f.functionId, f.functionName); mNametoId.set(f.functionName, f.functionId)});
        return [mIdtoName, mNametoId];
      }, [props.functions]);

    const [originalFieldIdToName, originalFieldNameToId] = useMemo( () =>{
        let mIdtoName = new Map();
        let mNametoId = new Map();
        props.originalFields.forEach((f) => {mIdtoName.set(f.fieldId, f.fieldName); mNametoId.set(f.fieldName, f.fieldId)});
        return [mIdtoName, mNametoId];
      }, [props.originalFields]);

    const [derivedFieldIdToName, derivedFieldNameToId] = useMemo( () =>{
        let mIdtoName = new Map();
        let mNametoId = new Map();
        props.derivedFields.forEach((f) => {mIdtoName.set(f.fieldId, f.fieldName); mNametoId.set(f.fieldName, f.fieldId)});
        return [mIdtoName, mNametoId];
      }, [props.derivedFields]);       

    /*
      Decode input code from IDs to names
    */

      function replaceIdWithNames(codeWithId){
        let pattern = /(\[\d+\])|(\[\[\d+\]\])/g;
  
        function replacer(match, ...arg){
          if(match[0] === '[' && match[1] === '[')
          {
            let id = Number(match.slice(2,-2));
            if(isNaN(id)){
              return match;
            }
            else{
              return '[[' + derivedFieldIdToName.get(id) + ']]';
            }
          }
          else
          {
            let id = Number(match.slice(1,-1));
            if(isNaN(id)){
              return match;
            }
            else{
              return '[' + originalFieldIdToName.get(id) + ']';
            }  
          }
        }
  
        return codeWithId.replace(pattern, replacer);
      }
  
      const initialCode = useMemo(() => replaceIdWithNames(props.initialCode), [props.initialCode]);

      function replaceNamesWithId(codeWithId){
          let pattern = /(\[[^\[\]]+\])|(\[\[[^\[\]]+\]\])/g;
    
          function replacer(match, ...arg){
            if(match[0] === '[' && match[1] === '[')
            {
              let name = match.slice(2,-2);

              return '[[' + derivedFieldNameToId.get(name) + ']]';
            }
            else
            {
              let name = match.slice(1,-1);

              return '[' + originalFieldNameToId.get(name) + ']';
            }
          }
    
          return codeWithId.replace(pattern, replacer);
      }      
  
    /*
    ********************************************************
      Compile and build output tree onChange callback
    ********************************************************
    */


    let createOutNode = useCallback((syntNode, code, errorList) => {

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
          let fieldId = originalFieldNameToId.get(fieldName);
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
          let fieldId = derivedFieldNameToId.get(fieldName);
          outNode = {
            nodeType: nodeType.derivedField,
            fieldName: fieldName,
            fieldId: derivedFieldNameToId.get(fieldName)
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
          let functionId = functionNameToId.get(functionName);
          outNode = {
              temp : true,
              tempNodeType : tempNodeType.functionName,
              functionName : functionName,
              functionId : functionNameToId.get(functionName)
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
      }, [functionNameToId, originalFieldNameToId, derivedFieldNameToId]);

    let processOutChildren = useCallback((parent, children) => {

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

      }, []);

    let createOutputTree = useCallback((syntNode, code, errorList) => {

        let outNode = createOutNode(syntNode, code, errorList);
        let outChildren = [];
        let syntChild = syntNode.firstChild;
        while(syntChild !== null){
            outChildren.push(createOutputTree(syntChild, code, errorList));
            syntChild = syntChild.nextSibling;
        }
        processOutChildren(outNode, outChildren);
        return outNode;
      }, [processOutChildren, createOutNode]);

    const handleChange = useCallback((value, viewUpdate) => {

        let tree = MagreportLanguage.parser.parse(value);

        let errorList = [];
        let root = createOutputTree(tree.topNode, value, errorList);

        setErrorMessages(errorList.map(e => e.errorMessage).join("\n"));
        
        props.onChange({
          success : !root.isError,
          textToSave : replaceNamesWithId(value),
          treeRoot : root,
          errorList : errorList
        });

    }, [props.onChange, createOutputTree]);

      return (
        <div className="FormulaEditor">
            <CodeMirror
              className="CodeMirror"
              ref={editor}
              value={initialCode}
              height={props.height}
              theme={codeEditorTheme}
              editable={!props.disabled}
              extensions={[new LanguageSupport(MagreportLanguage, [completion])]}
              onChange={handleChange}
            />

            <TextField
              label="Поле ошибок"
              className="errorViewer"
              multiline
              InputProps={{
                readOnly: true,
              }}
              InputLabelProps={{
                shrink: true,
              }}
              rows={5}
              variant="outlined"
              value={errorMessages}
              disabled={props.disabled}
            />

        </div>
      );
}