create view V_ASM_GEO_ROLE_RIGHTS
as
select
	CHANGE_TYPE,
	ROLE_NAME,
	region_code,
	municipality,
	settlement_id
from
(
	select
	    'I' as CHANGE_TYPE,
	    'SF_GEO_IVANOV' as ROLE_NAME,
	    23 as region_code,
	    'Город Краснодар' as municipality,
	    25233 as settlement_id
	from
		prd_vd_dm.V_DUMMY
	union all
	select
	    'I' as CHANGE_TYPE,
	    'SF_GEO_IVANOV' as ROLE_NAME,
	    77 as region_code,
	    'Центральный' as municipality,
	    null as settlement_id
	from
		prd_vd_dm.V_DUMMY
	union all
	select
	    'I' as CHANGE_TYPE,
	    'SF_GEO_IVANOV' as ROLE_NAME,
	    78 as region_code,
	    cast(null as varchar(256)) as municipality,
	    null as settlement_id
	from
		prd_vd_dm.V_DUMMY
) S;