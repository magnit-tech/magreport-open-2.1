<#-- @ftlvariable name="" type="ru.magnit.magreportbackend.dto.inner.filter.FilterValueListRequestData" -->
SELECT
${idFieldName()}, ${codeFieldName()}, ${nameFieldName()}
FROM ${schemaName()}.${tableName()}
WHERE ${filterFieldName()} <#if isCaseSensitive()>LIKE<#else>ILIKE</#if>
<#if likenessType() == "STARTS">'${searchValue()}%'
<#elseif likenessType() == "CONTAINS">'%${searchValue()}%'
<#elseif likenessType() == "ENDS">'%${searchValue()}'
</#if><#--
-->GROUP BY
${idFieldName()}, ${codeFieldName()}, ${nameFieldName()}
LIMIT ${maxCount()};