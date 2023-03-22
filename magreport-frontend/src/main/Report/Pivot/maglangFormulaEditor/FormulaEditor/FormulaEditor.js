import React, {useRef, useState, useCallback, useMemo} from "react";
import CodeMirror from '@uiw/react-codemirror';
import { createTheme } from '@uiw/codemirror-themes';
import {completeFromList} from "@codemirror/autocomplete";
import {LanguageSupport} from '@codemirror/language';
import { MagreportLanguage } from "../maglang/maglang.js";
import {tags as t } from '@lezer/highlight';

import "./FormulaEditor.css"

import { Box, FormControl, MenuItem, Select, TextField, InputBase } from "@material-ui/core";
import { withStyles } from '@material-ui/core/styles';

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
 * @param {*} props.derivedFields - массив объектов производных полей отчёта {fieldId, fieldName, fieldDesc, fieldOwner, valueType}
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

const SelectInput = withStyles((theme) => ({
	root: {
		  maxWidth: '100px',
	},
	input: {
		borderRadius: 4,
		position: 'relative',
		backgroundColor: theme.palette.background.paper,
		border: '1px solid #ced4da',
		fontSize: 14,
		padding: '5px 20px 5px 7px',
		transition: theme.transitions.create(['border-color', 'box-shadow']),
		'&:focus': {
			borderRadius: 4,
			borderColor: '#80bdff',
			boxShadow: '0 0 0 0.2rem rgba(0,123,255,.25)',
		},
	},
}))(InputBase);

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
    // const derivedFieldsCompletionList = props.derivedFields.map((v) => ({label: v.fieldName, type: "variable", detail: v.fieldOwner + ": " + v.fieldDesc}));

    const derivedFieldsCompletionList = () => {
      let result = []

      props.publicFields.forEach((value, key) => {
        result.push({label: key, type: "variable", detail: key})
      })

      props.ownFields.forEach((value, key) => {
        if(props.publicFields.get(key)) {
          result.push({label: `${key}(${value.userName})`, type: "variable", detail: `${key}(${value.userName})`})
        } else {
          result.push({label: key, type: "variable", detail: key})
        }
      })

      props.otherFields.forEach((value, key) => {
        if(props.publicFields.get(key) || props.ownFields.get(key)) {
          result.push({label: `${key}(${value.userName})`, type: "variable", detail: `${key}(${value.userName})`})
        } else {
          result.push({label: key, type: "variable", detail: key})
        }
      })
      
      return result
    }

    const completionList = functionsCompletionList.concat(originalFieldsCompletionList.concat(derivedFieldsCompletionList()));

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

        props.publicFields.forEach((value, key) => {
          mIdtoName.set(value.id, key); 
          mNametoId.set(key, value.id)
        })

        props.ownFields.forEach((value, key) => {
          if(props.publicFields.get(key)) {
            mIdtoName.set(value.id, `${key}(${value.userName})`); 
            mNametoId.set(`${key}(${value.userName})`, value.id)
          } else {
            mIdtoName.set(value.id, key); 
            mNametoId.set(key, value.id)
          }
        })

        props.otherFields.forEach((value, key) => {
          if(props.publicFields.get(key) || props.ownFields.get(key)) {
            mIdtoName.set(value.id, `${key}(${value.userName})`); 
            mNametoId.set(`${key}(${value.userName})`, value.id)
          } else {
            mIdtoName.set(value.id, key); 
            mNametoId.set(key, value.id)
          }
        })

        return [mIdtoName, mNametoId];
      }, [props.publicFields, props.ownFields, props.otherFields]);       

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
          let pattern = /(\[[^\[\]]+\])|(\[\[[^\[\]]+\]\])/g;
    
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
      return new RegExp(`\\b(SUBSTR|STRLEN)\\b`);
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
          textToSave : replaceFunctionNameWithId( replaceNameWithId(value) ),
          treeRoot : root,
          errorList : errorList
        });

    }, [props.onChange, createOutputTree]); // eslint-disable-line



    // Создание списка c MenuItems для селекта "Размер шрифта"
    const menuItems = () => {
      const numbers = [8, 9, 10, 12, 14, 16, 20, 24, 28, 32]
      const arr = numbers.map(i =>  <MenuItem key={i} value={i}>{i}</MenuItem> )

      return arr
    }

    const ITEM_HEIGHT = 48;
    const ITEM_PADDING_TOP = 8;
    const MenuProps = {
      PaperProps: {
        style: {
          maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
          width: 250,
        },
      },
    };

    return (
        <div className="FormulaEditor">
							<Box className="FormulaEditor__fontSize">
								<Box whiteSpace="nowrap" className="FormulaEditor__fontSizeSelect">Размер шрифта:</Box>
								<FormControl>
									<Select
										id="fontSizeSelect"
										value={props.fontSize}
										onChange={(e) => props.onChangeFontSize(e.target.value)}
										input={<SelectInput />}
										MenuProps={MenuProps}
									>
										{menuItems()}
									</Select>
								</FormControl>
							</Box>
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