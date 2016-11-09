--------------------------------------------------------------------------
-- * BMC Software, Inc.
-- * Confidential and Proprietary
-- * Copyright (c) BMC Software, Inc. 2015
-- * All Rights Reserved.
--------------------------------------------------------------------------
set linesize 500;
set head off;
set trimspool on;
set feedback off;
WHENEVER SQLERROR EXIT;
--drop table agent_patch_storage_11_010;
--drop table agent_patch_info_11_010;
create table agent_patch_storage_11_010 as select * from agent_patch_storage
/
create table agent_patch_info_11_010 as select * from agent_patch_info
/
set termout off;
spool restore_agent_version_2.11.00.000.010.sql;
select 'WHENEVER SQLERROR EXIT;' from dual;
select 'set echo on;' from dual;
select 'spool restore_agent_version_2.11.00.000.010.log;' from dual;
select 'delete from agent_patch_storage;' from dual;
select 'delete from agent_patch_info;' from dual;

select 'insert into agent_patch_info select * from agent_patch_info_11_010;' from dual;
select 'insert into agent_patch_storage select * from agent_patch_storage_11_010;' from dual;



select 'update agent set PATCHSTATUS='''||PATCHSTATUS||''', PATCHVERSION='''||PATCHVERSION||''' where guid='''||guid||''';' from agent;
select 'commit;' from dual;
select 'spool off;' from dual;
select 'exit;' from dual;
spool off;
set termout on;
prompt ===========================
prompt 
prompt Backup is done and restore script is created restore_agent_version_2.11.00.000.010.sql successfully.
prompt ===========================
prompt 
exit;