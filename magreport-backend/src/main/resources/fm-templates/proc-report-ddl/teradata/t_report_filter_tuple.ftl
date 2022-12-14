<#-- @ftlvariable name="schema" type="java.lang.String" -->
CREATE MULTISET TABLE ${schema}.T_REPORT_FILTER_TUPLE,
NO FALLBACK,
NO BEFORE JOURNAL,
NO AFTER JOURNAL,
CHECKSUM = DEFAULT,
DEFAULT MERGEBLOCKRATIO
(
    JOB_ID INTEGER,
    TUPLE_ID INTEGER,
    REPORT_FILTER_NAME VARCHAR(255) CHARACTER SET UNICODE CASESPECIFIC
) NO PRIMARY INDEX ;
