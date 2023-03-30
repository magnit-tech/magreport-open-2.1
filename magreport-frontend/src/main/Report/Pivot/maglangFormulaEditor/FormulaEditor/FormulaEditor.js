import React, {useRef, useState, useCallback, useMemo} from "react";
import CodeMirror from '@uiw/react-codemirror';
import { createTheme } from '@uiw/codemirror-themes';
import {completeFromList} from "@codemirror/autocomplete";
import {LanguageSupport} from '@codemirror/language';
import { MagreportLanguage } from "../maglang/maglang.js";
import {tags as t } from '@lezer/highlight';

import "./FormulaEditor.css"

import { TextField } from "@material-ui/core";

import {createOutputNode, processOutputChildren} from "./createOutputNode";

// Примеры highlight смотреть здесь:
// https://github.com/codemirror/highlight/blob/main/src/highlight.ts 

/*
  Правила преобразования отображаемой строки формулы в сохранямую и обратно:
  Названия полей и производных полей преобразуются в __#ID поля, где ID - числовое значение ID поля. Пример:
    [__#115] <-> [Продажи, руб]
  
  Названия функций преобразуются в __F#ID, где ID - числовое значение ID функции. Пример:
    __F#9 <-> SUBSTR
*/

/**
 * 
 * @param {*} props.height
 * @param {*} props.disabled
 * @param {*} props.initialCode - исходный текст формулы с id вместо названий
 * @param {*} props.functions - массив описаний функций: {functionId, functionName, functionDesc, functionSignature}
 * @param {*} props.originalFields - массив объектов исходных полей отчёта {fieldId, fieldName, fieldDesc, valueType}
 * @param {*} props.derivedFields - массив объектов производных полей отчёта {fieldId, fieldName, fieldDesc, valueType}
 * @param {*} props.fontSize - размер шрифта
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

export default function FormulaEditor(props){

    const editor = useRef();
    const [errorMessages, setErrorMessages] = useState("");

    /*
      Theme
    */
    const codeEditorTheme = createTheme({
        theme: 'light',
        settings: {
          background: '',
          foreground: props.disabled ? 'grey' : '#75baff',
          caret: '#5d00ff',
          selection: '#036dd626',
          selectionMatch: '#036dd626',
          lineHighlight: '#8a91991a',
          gutterBackground: 'inherit',
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
    const derivedFieldsCompletionList = props.derivedFields.map((v) => ({label: v.fieldName, type: "variable", detail: v.fieldDesc}));

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

    const [originalFieldIdToName, originalFieldNameToId] = useMemo( () => {
        let mIdtoName = new Map();
        let mNametoId = new Map();
        props.originalFields.forEach((f) => {mIdtoName.set(f.fieldId, f.fieldName); mNametoId.set(f.fieldName, f.fieldId)});
        return [mIdtoName, mNametoId];
      }, [props.originalFields]);

    const [derivedFieldIdToName, derivedFieldNameToId] = useMemo( () => {

        let mIdtoName = new Map();
        let mNametoId = new Map();

        props.derivedFields.forEach((value) => {
          mIdtoName.set(value.fieldId, value.fieldName); 
          mNametoId.set(value.fieldName, value.fieldId)
        })

        return [mIdtoName, mNametoId];
      }, [props.derivedFields]);       

    /*
      Decode input code from IDs to names
    */
      // ---------------------------------------
      // Orignial and derived fields
      // ---------------------------------------

    function replaceIdWithName(code){
        let pattern = /(\[__#\d+\])|(\[\[__#\d+\]\])/g;
  
        function replacer(match, ...arg){
          if(match[0] === '[' && match[1] === '[')
          {
            let id = Number(match.slice(5,-2));
            if(isNaN(id)){
              return match;
            }
            else{
              let name = derivedFieldIdToName.get(id);
              return '[[' + (name ? name : "__#" + id) + ']]';
            }
          }
          else
          {
            let id = Number(match.slice(4,-1));
            if(isNaN(id)){
              return match;
            }
            else{
              let name = originalFieldIdToName.get(id);
              return '[' + (name ? name : ("__#" + id) ) + ']';
            }  
          }
        }

        return code.replace(pattern, replacer);
    }

    function replaceNameWithId(code){
          let pattern = /(\[[^\[\]]+\])|(\[\[[^\[\]]+\]\])/g; // eslint-disable-line
    
          function replacer(match, ...arg){
            if(match[0] === '[' && match[1] === '[')
            {
              let name = match.slice(2,-2);
              let id = derivedFieldNameToId.get(name);

              return '[[' + (id ? ("__#" + id) : name) + ']]';
            }
            else
            {
              let name = match.slice(1,-1);
              let id = originalFieldNameToId.get(name);

              return '[' + (id ? ("__#" + id) : name) + ']';
            }
          }
    
          return code.replace(pattern, replacer);
      }

    // ----------------------------
    // Functions
    // ----------------------------

    let functionNamePattern = useMemo (() => {
      let funcNamesString = props.functions.map(v => v.functionName).join("|");
      console.log(funcNamesString)
      return new RegExp(`\\b(${funcNamesString})\\b`); 
    }, [props.functions]); // eslint-disable-line

    let [functionIdToNameReplacer, functionNameToIdReplacer] = useMemo(()=>{

      let functionIdToNameReplacer = (match, ...arg) => {
        let id = Number(match.slice(4));

        if(isNaN(id)){
          return match;
        }
        else{
          let name = functionIdToName.get(id);
          return (name ? name : match);
        }
      };

      let functionNameToIdReplacer = (match, ...arg) => {
        let id = functionNameToId.get(match);
        return id ? ("__F#" + id) : match;
      };

      return [functionIdToNameReplacer, functionNameToIdReplacer]
    }, [props.functions]); // eslint-disable-line

      
    function replaceFunctionIdWithName(code){
      let pattern = /(__F#\d+)/g;
      return code.replace(pattern, functionIdToNameReplacer);
    }


    function replaceFunctionNameWithId(code){
      return code.replace(functionNamePattern, functionNameToIdReplacer);
    }

    
    const code = useMemo(() => replaceFunctionIdWithName( replaceIdWithName(props.initialCode) ), [props.initialCode]); // eslint-disable-line
  
    /*
    ********************************************************
      Compile and build output tree onChange callback
    ********************************************************
    */


    let createOutNode = useCallback((syntNode, code, errorList) => {
        return createOutputNode(syntNode, code, errorList, originalFieldNameToId, derivedFieldNameToId, functionNameToId)
      }, [functionNameToId, originalFieldNameToId, derivedFieldNameToId]);

    let processOutChildren = useCallback((parent, children) => {
        return processOutputChildren(parent, children);
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
          textToSave : replaceFunctionNameWithId( replaceNameWithId(value) ),
          treeRoot : root,
          errorList : errorList
        });

    }, [props.onChange, createOutputTree]); // eslint-disable-line

    return (
        <div className="FormulaEditor">
            <CodeMirror
              className="CodeMirror"
              style={{ fontSize: props.fontSize + "px" }}
              ref={editor}
              value={code}
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
              rows={3}
              variant="outlined"
              value={errorMessages}
              disabled={props.disabled}
              error={!!errorMessages}
            />

        </div>
      );
}