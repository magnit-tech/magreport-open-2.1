create view V_ASM_GEO_USER_ROLE_CDC
as
select
    'I' as CHANGE_TYPE,
    'SF_GEO_IVANOV' as ROLE_NAME,
    'ivanov' as USER_NAME;