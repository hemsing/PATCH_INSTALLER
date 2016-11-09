--------------------------------------------------------------------------
-- * BMC Software, Inc.
-- * Confidential and Proprietary
-- * Copyright (c) BMC Software, Inc. 2016
-- * All Rights Reserved.
--------------------------------------------------------------------------


set echo off;
set termout off;
set trimout on;
set trimspool on;
set pagesize 50000;
set markup html on;
set echo on;
alter session set nls_date_format='YYYY-MM-DD HH24:MI:SS';

BEGIN
execute immediate 'drop table node_came_offline';
EXCEPTION
WHEN OTHERS THEN
null;
END;
/
spool fix_inactive_instance_v4.html;
--select systimestamp from dual;
prompt 
prompt All the parameters
prompt 

create table node_came_offline as 
select distinct node from node_status_history nsh where  
nsh.timestamp>(timing.convert_oracle_time(sysdate-((240/60)/24)-drmop_util.get_gmtoffset_day)) 
and nsh.status=40 
/

create index idx_node on node_came_offline(node)
/

break on Col1
compute sum of params on Col1
/
create or replace view vw_inactive_instance as 
select null Col1, 
n.displaytext Element, 
ai.guid AppclassGuid,
am.displaytext AMDisplaytext,
ai.displaytext AIDisplaytext,
timing.convert_unix_time(max_time) MaxCollection ,
vw_collection.params from 
		application_instance ai,
		node n, 
		application_metadata am,
		websdk_account wa,
		(
		select ai2.guid,max(pia2.LATESTDATATIME) max_time,count(*) params  from 
				application_instance ai2,parameter_instance pi2,parameter_instance_agent pia2 
				where 
					pi2.applicationinstance=ai2.guid 
					and pi2.guid=pia2.guid
					and ai2.discoverydefault='F' 
					and ai2.configstate='ADDED'
					and pi2.configstate='ADDED'
					group by ai2.guid
					having 
					sum(DECODE(STATUSALERT,'unknown',0,1))=0 and
					sum(nvl2(statusvalue,1,0))=0 
					)vw_collection
		where 
			n.guid  NOT in (select node from node_came_offline) and 
			vw_collection.guid=ai.guid
			and ai.discoverydefault='F' 
			and am.discovered='T'
			and n.collectionstate='ENABLED'
			and (n.inblackout='F' or n.inblackout is null)
			and ai.configstate='ADDED'
			and n.configstate='ADDED'
			and ai.applicationname = am.applicationname 
			and ai.solutionversion = am.solutionversion 
			and ai.solutionname = am.solutionname 
			and n.guid=ai.node
			--ACS is okay, ACS is collecting for at least 4 hours
			and ai.rootapplication in (select ai.rootapplication from 
				parameter_instance_agent pia,
				parameter_instance pi, 
				application_instance ai,
				node n
			where	pi.guid=pia.guid 
				and n.guid=ai.node
				and n.collectionstate='ENABLED'
				and n.istemplate='F'
				and n.INBLACKOUT='F'
				and ai.rootapplication =ai.guid
				and pi.applicationinstance=ai.guid
				and pi.configstate='ADDED'
				and pi.istemplate='F'
				and pia.statusalert='ok'
				and pia.statusvalue='true'
				and pi.definitionname='root_application_collection_status'
				and pia.LATESTDATATIME>timing.convert_oracle_time((sysdate-1)-drmop_util.get_gmtoffset_day)
			)
			and n.customer=wa.guid
/

select Col1, 
Element, 
AppclassGuid,
AMDisplaytext,
AIDisplaytext,
MaxCollection ,
params from 
vw_inactive_instance
order by Element,AIDisplaytext,MaxCollection
/

DECLARE
sql1 varchar2(150);
table_name varchar2(150);
BEGIN

table_name:='AI_BK_'||to_char(sysdate,'YYYY_MM_DD_HH24_MI_SS');
sql1:='create table '||table_name||' as select * from application_instance ';
execute immediate sql1;

table_name:='PI_BK_'||to_char(sysdate,'YYYY_MM_DD_HH24_MI_SS');
sql1:='create table '||table_name||' as select * from parameter_instance ';
execute immediate sql1;

END;
/

update application_instance set configstate='REMOVED' where guid in 
(select AppclassGuid from vw_inactive_instance)
/
commit
/

spool off;
drop table node_came_offline
/
set echo off;
set termout on;
set markup html off;
