<#-- @ftlvariable name="" type="ru.magnit.magreportbackend.dto.inner.filter.FilterValueListRequestData" -->
SELECT TOP ${maxCount()}
${idField().fieldName}, ${codeField().fieldName}, <#list nameFields() as nameField > <#if nameField.showField> ${nameField.fieldName}<#sep>,</#if></#list>
FROM ${schemaName()}.${tableName()}
WHERE <#list filterFields() as filterField>
    <#if filterField.searchByField >
        ${filterField.getFieldName()} <#if isCaseSensitive()>LIKE<#else>ILIKE</#if>
        <#if likenessType() == "STARTS">'${searchValue()}%'
        <#elseif likenessType() == "CONTAINS">'%${searchValue()}%'
        <#elseif likenessType() == "ENDS">'%${searchValue()}'
        </#if><#sep>OR</#sep>
    </#if>
</#list>
<#--
-->GROUP BY
${idField().fieldName},${codeField().fieldName},  <#list nameFields() as nameField > <#if nameField.showField > ${nameField.fieldName}<#sep>,</#if></#list>;
