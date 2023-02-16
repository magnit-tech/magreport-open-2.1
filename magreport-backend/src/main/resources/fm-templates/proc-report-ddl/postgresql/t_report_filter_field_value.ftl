<#-- @ftlvariable name="schema" type="java.lang.String" -->
CREATE TABLE ${schema}.T_REPORT_FILTER_FIELD_VALUE (
    JOB_ID INTEGER,
    report_filter_field_id INTEGER,
    TUPLE_ID INTEGER,
    VARCHAR_VALUE VARCHAR(255),
    DATE_VALUE DATE,
    INTEGER_VALUE INTEGER,
    DOUBLE_VALUE NUMERIC
);
