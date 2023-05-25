import {tags as t } from '@lezer/highlight';

export const highlightingStyles = [
    { tag: t.comment, color: '#787b8099' },
    { tag: t.keyword, color: 'green'},
    { tag: t.variableName, color: '#0080ff' , fontWeight: "bold"},
    { tag: t.propertyName, color: '#00b300' , fontWeight: "bold"},
    { tag: t.function(t.variableName), color: '#862d59' , fontWeight: "normal"},
    { tag: [t.string, t.special(t.brace)], color: '#5c6166' },
    { tag: t.number, color: '#5c6166' },
    { tag: t.operator, color: '#5c6166' },
    { tag: t.angleBracket, color: '#5c6166' },
    { tag: t.tagName, color: '#5c6166' },
    { tag: t.attributeName, color: '#5c6166' },
  ];