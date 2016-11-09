If Exists ( SELECT name 
            FROM sysobjects  
            WHERE name = 'LOGGER'
            AND type = 'P')
	DROP PROCEDURE LOGGER
GO 
CREATE PROCEDURE LOGGER
	@p_login_level                            VARCHAR(4000) ,
	@p_err_msg                                VARCHAR(4000) ,
	@p_err_num                                FLOAT                            = 0 
AS 
	BEGIN
		DECLARE @p_login                                  VARCHAR(100) 
		SELECT @p_login  = LTRIM(RTRIM(UPPER(@p_login_level)))
		insert into re_log(ERR_NUM,ERR_MSG) values(@p_err_num,@p_login  +': ' +@p_err_msg)				
	END
GO
 
If Exists ( SELECT name 
            FROM sysobjects  
            WHERE name = 'inttoip'
            AND type = 'FN')
	DROP FUNCTION inttoip
GO 
CREATE FUNCTION inttoip
(
	@ip_address                               INTEGER 
)
RETURNS VARCHAR(4000) 
AS 
	BEGIN
		
		DECLARE @chHexIP                                  VARCHAR(8) 
		-- 1. convert the integer into hexadecimal representation
		
		SELECT @chHexIP  = CONVERT(BINARY(4), @ip_address)
		--  dbms_output.put_line(chHexIP  );
		--  dbms_output.put_line(' length of chHexIP'||length(chHexIP)  );
		
		IF LEN(@chHexIP)< 8 
		BEGIN 
			SELECT @chHexIP  = '''0'' ' + @chHexIP  
		END
   
		-- dbms_output.put_line(chHexIP  );
		-- 2. convert each XX portion back into decimal
		
		
		RETURN CONVERT(NUMERIC(8, 2), SUBSTRING(@chHexIP, 1, 2)) +'.'+CONVERT(NUMERIC(8, 2), SUBSTRING(@chHexIP, 3, 2)) +'.'+CONVERT(NUMERIC(8, 2), SUBSTRING(@chHexIP, 5, 2)) +'.'+CONVERT(NUMERIC(8, 2), SUBSTRING(@chHexIP, 7, 2)) 
	END

GO
 
If Exists ( SELECT name 
            FROM sysobjects  
            WHERE name = 'convert_number_to_date'
            AND type = 'FN')
	DROP FUNCTION convert_number_to_date
GO 
CREATE FUNCTION convert_number_to_date
(
	@nDate                                    FLOAT 
)
RETURNS DATETIME 
AS 
	BEGIN
		
		RETURN CONVERT(DATETIME, '01/01/1970', 101) +(@nDate/86400 )
	END

GO
 -- This procedure will be called every hour to check the DST changes and according to adjust the DST_ADJUSTED_OFFSET
	-- Column in RPT_TIMEZONE_INFO table. This is specifically done to avoid the performance issues while drilling down
	-- in the Reports from daily to hourly and hourly to raw data specifically.
	-- This column is used to convert Time recorded in GMT time to Local timzezone of Reporting engine.
	
	 
If Exists ( SELECT name 
            FROM sysobjects  
            WHERE name = 'update_dst_offset'
            AND type = 'P')
	DROP PROCEDURE update_dst_offset
GO 
CREATE PROCEDURE update_dst_offset
AS 
	BEGIN
		SET NOCOUNT ON
		
		DECLARE @v_temp                                   FLOAT 
		--SELECT @v_temp  = DATEDIFF(minute,REUNIV.Rpt_Adjusted_Date(CONVERT(DATETIME,GETDATE(), 112)),CONVERT(DATETIME,GETDATE(), 112) )
		SET @v_temp = datediff(SECOND,CONVERT(DATETIME, CONVERT(DATE, GETDATE(),120)),REUNIV.Rpt_adjusted_date (CONVERT(DATETIME, CONVERT(DATE, GETDATE(),120))))/ CONVERT(float,3600)/ CONVERT(float,24)
		
		UPDATE  RPT_TIMEZONE_INFO   
		SET	DST_ADJUSTED_OFFSET = @v_temp 
		WHERE  REGION_FLAG  = 'Y' 
		
		
		-- IMPLICIT_TRANSACTIONS is set to OFF
		SET NOCOUNT OFF
	END

GO
EXEC UPDATE_DST_OFFSET
GO
  
If Exists ( SELECT name 
            FROM sysobjects  
            WHERE name = 'fn_check_table_exists'
            AND type = 'P')
	DROP PROCEDURE fn_check_table_exists
GO 
CREATE PROCEDURE fn_check_table_exists
	@tabName                                  VARCHAR(4000) ,
	@return_val                               VARCHAR(4000) OUTPUT 
AS 
	BEGIN
		SET NOCOUNT ON
		
		
		DECLARE @err_num                                  FLOAT 
		DECLARE @err_msg                                  VARCHAR(100) 
		DECLARE @tabCount                                 FLOAT 
		DECLARE @found                                    VARCHAR(3) 
		DECLARE @adv_error INT
		SELECT @found  = 'NO' 
		
		SELECT @tabCount = count(*) from sysobjects where name = '''||tabName||''';
		
		DECLARE @ArgumentAssignment VARCHAR (256)
		SET @ArgumentAssignment = 'tabCount is ' + @tabCount 
		EXEC LOGGER 'DEBUG'  ,  @ArgumentAssignment 
		
		SELECT @adv_error = @@ERROR
		IF @adv_error != 0 
			GOTO Exception1
		IF @tabCount > 0 
		BEGIN 
			SELECT @found  = 'YES' 
		END
   
		
		DECLARE @adv_sqlCode INTEGER
		SELECT @adv_sqlCode = @adv_error
		DECLARE @ArgumentAssignment0 VARCHAR (256)
		SET @ArgumentAssignment0 = 'SQLCODE:' + @adv_sqlCode 
		EXEC LOGGER 'DEBUG'  ,  @ArgumentAssignment0 
		
		SELECT @adv_error = @@ERROR
		IF @adv_error != 0 
			GOTO Exception1
		
		SELECT @return_val = @found
		GOTO ExitLabel1
		Exception1:
			
			IF @adv_error = 54300 
			BEGIN
			
			DECLARE @ArgumentAssignment1 VARCHAR (256)
			SET @ArgumentAssignment1 =  + ' TABLE NOT FOUND' 
			EXEC LOGGER 'DEBUG'  ,  @tabName = @ArgumentAssignment1 
			
			SELECT @adv_error = @@ERROR
			IF @adv_error != 0 
				GOTO Exception1
			SELECT @FOUND  = 'NO' 
			
			END
			ELSE   
			BEGIN
			
			SELECT @err_num  = @adv_sqlCode 
			
			SELECT @adv_sqlCode = @adv_error
			DECLARE @ArgumentAssignment2 VARCHAR (256)
			SET @ArgumentAssignment2 =  + ' TABLE NOT FOUND' 
			EXEC LOGGER 'ERROR'  ,  @tabName = @ArgumentAssignment2  ,  @adv_sqlCode  = @adv_error
			
			SELECT @adv_error = @@ERROR
			IF @adv_error != 0 
				GOTO Exception1
			SELECT @FOUND  = 'NO' 
			
			SELECT @return_val = @FOUND
			
			END
		ExitLabel1:
		SELECT @return_val = @found
		SET NOCOUNT OFF
	END
GO
 
IF Exists ( SELECT name FROM sysobjects WHERE name = 'POPULATE_SQLERRM' AND type = 'P')
            DROP PROCEDURE POPULATE_SQLERRM
GO
 CREATE PROCEDURE POPULATE_SQLERRM
 AS 
  BEGIN 
 
IF Exists ( SELECT name FROM sysobjects WHERE name = 'ADV_SQLERRS' AND type = 'U')
            BEGIN
            DROP TABLE ADV_SQLERRS
            END
      CREATE TABLE ADV_SQLERRS (NAME VARCHAR(100),ERROR_CODE INTEGER, ERROR_MESSAGE VARCHAR(4000)) 
      INSERT INTO ADV_SQLERRS VALUES ('DUP_VAL_ON_INDEX',-1,'ORA-00001: unique constraint (.) violated' ) 
      INSERT INTO ADV_SQLERRS VALUES ('INVALID_CURSOR',-1001,'ORA-01001: invalid cursor') 
      INSERT INTO ADV_SQLERRS VALUES ('INVALID_NUMBER',-1722,'ORA-01722: invalid number') 
      INSERT INTO ADV_SQLERRS VALUES ('ZERO_DIVIDE',-1476,'ORA-01476: divisor is equal to zero') 
      INSERT INTO ADV_SQLERRS VALUES ('TOO_MANY_ROWS',-1422,'ORA-01422: exact fetch returns more than requested number of rows') 
      INSERT INTO ADV_SQLERRS VALUES ('NO_DATA_FOUND',100,'ORA-01403: no data found') 
      INSERT INTO ADV_SQLERRS VALUES ('ACCESS_INTO_NULL',-6530,'ORA-06530: Reference to uninitialized composite') 
      INSERT INTO ADV_SQLERRS VALUES ('CURSOR_ALREADY_OPEN',-6511,'ORA-06511: PL/SQL: cursor already open') 
      INSERT INTO ADV_SQLERRS VALUES ('VALUE_ERROR',-6502,'ORA-06502: PL/SQL: numeric or value error') 
END 
GO

EXEC POPULATE_SQLERRM
GO
IF Exists ( SELECT name FROM sysobjects 
            WHERE name = 'ADV_SET_EXCEPTION'            AND type = 'P')
            DROP PROCEDURE ADV_SET_EXCEPTION
GO
 CREATE PROCEDURE ADV_SET_EXCEPTION ( @name VARCHAR(50) )
 AS
  BEGIN
       DECLARE @context_info_var VARBINARY(128)
       DECLARE @len INTEGER
       SELECT @len = LEN(@name)
       WHILE @len < 30
       BEGIN
               SELECT @name = @name + SPACE(1)
               SELECT @len = @len + 1
       END
       SELECT @context_info_var = CAST (@name AS VARBINARY(128))
       SET CONTEXT_INFO @context_info_var
  END 
GO
 
IF Exists ( SELECT name FROM sysobjects 
            WHERE name = 'ADV_GET_SQLERRM'            AND type = 'FN')
            DROP FUNCTION ADV_GET_SQLERRM
GO
 CREATE FUNCTION ADV_GET_SQLERRM (@name VARCHAR(50) )
  RETURNS VARCHAR(4000)
  AS
  BEGIN
         IF ISNUMERIC(@name) = 1
         BEGIN
                RETURN 'User-Defined Exception'
         END
         DECLARE @context_info_var VARBINARY(128)
         DECLARE @adv_sqlErrm VARCHAR(4000)
         SELECT @context_info_var = CONTEXT_INFO  FROM master.dbo.sysprocesses WHERE SPID=@@SPID
         SELECT @adv_sqlErrm = CAST (@context_info_var AS VARCHAR)
         SELECT @adv_sqlErrm = SUBSTRING(@adv_sqlErrm,1,30)
         SELECT @adv_sqlErrm = RTRIM(@adv_sqlErrm)
         SELECT @adv_sqlErrm = error_message FROM adv_sqlerrs WHERE name = @adv_sqlErrm
         RETURN @adv_sqlErrm
  END
GO
 
IF Exists ( SELECT name FROM sysobjects 
            WHERE name = 'ADV_GET_SQLCODE'            AND type = 'FN')
            DROP FUNCTION ADV_GET_SQLCODE
GO
 CREATE FUNCTION ADV_GET_SQLCODE (@name VARCHAR(50) )
  RETURNS INTEGER
  AS
  BEGIN
         IF ISNUMERIC(@name) = 1
         BEGIN
                 RETURN 1
         END
         DECLARE @context_info_var VARBINARY(128)
         DECLARE @adv_sqlErrm VARCHAR(4000)
         DECLARE @adv_sqlcode INTEGER
         SELECT @context_info_var = CONTEXT_INFO  FROM master.dbo.sysprocesses WHERE SPID=@@SPID
         SELECT @adv_sqlErrm = CAST (@context_info_var AS VARCHAR)
         SELECT @adv_sqlErrm = SUBSTRING(@adv_sqlErrm,1,30)
         SELECT @adv_sqlErrm = RTRIM(@adv_sqlErrm)
         SELECT @adv_sqlcode = error_code FROM adv_sqlerrs WHERE name = @adv_sqlErrm
         RETURN @adv_sqlcode
  END
GO
--Check if materialized views exists or not.
	
	 
--If Exists ( SELECT name 
--            FROM sysobjects  
--            WHERE name = 'fn_check_mv_exists'
--            AND type = 'FN')
--	DROP function fn_check_mv_exists
--GO 
--CREATE function fn_check_mv_exists
--	(@mvName                                   VARCHAR(4000)) 	
--AS 
--	BEGIN
--		SET NOCOUNT ON
--		DECLARE @return_val VARCHAR(100)
--		DECLARE @adv_error INT
--		DECLARE @err_num                                  FLOAT 
--		DECLARE @err_msg                                  VARCHAR(100) 
--		DECLARE @tabCount                                 FLOAT 
--		DECLARE @found                                    VARCHAR(3) 
--		DECLARE @adv_sqlCode INTEGER
--		SELECT @found  = 'NO' 
--		
--		SELECT @tabCount = count(*) from sysobjects where name = '''||tabName||''';
--		DECLARE @ArgumentAssignment VARCHAR (256)
--		SET @ArgumentAssignment = 'tabCount is ' + @tabCount 
--		EXEC LOGGER 'DEBUG'  ,  @ArgumentAssignment 
--		
--		SELECT @adv_error = @@ERROR
--		IF @adv_error != 0 
--			GOTO Exception1
--		IF @tabCount > 0 
--		BEGIN 
--			SELECT @found  = 'YES' 
--		END
--   
--		
--		SELECT @adv_sqlCode = @adv_error
--		DECLARE @ArgumentAssignment0 VARCHAR (256)
--		SET @ArgumentAssignment0 = 'SQLCODE:' + @adv_sqlCode 
--		EXEC LOGGER 'DEBUG'  ,  @ArgumentAssignment0 
--		
--		SELECT @adv_error = @@ERROR
--		IF @adv_error != 0 
--			GOTO Exception1
--		
--		SELECT @return_val = @found
--		GOTO ExitLabel1
--		Exception1:
--			
--			IF @adv_error = 54300 
--			BEGIN
--			
--			DECLARE @ArgumentAssignment1 VARCHAR (256)
--			SET @ArgumentAssignment1 =  + ' MATERIALIZED VIEW NOT FOUND' 
--			EXEC LOGGER 'DEBUG'  ,  @mvName = @ArgumentAssignment1 
--			
--			SELECT @adv_error = @@ERROR
--			IF @adv_error != 0 
--				GOTO Exception1
--			SELECT @FOUND  = 'NO' 
--			
--			END
--			ELSE   
--			BEGIN
--			
--			SELECT @err_num  = @adv_sqlCode 
--			
--			SELECT @adv_sqlCode = @adv_error
--			DECLARE @ArgumentAssignment2 VARCHAR (256)
--			SET @ArgumentAssignment2 =  + ' MATERIALIZED VIEW NOT FOUND' 
--			EXEC LOGGER 'ERROR'  ,  @mvName = @ArgumentAssignment2  ,  @adv_sqlCode = @adv_error
--			
--			SELECT @adv_error = @@ERROR
--			IF @adv_error != 0 
--				GOTO Exception1
--			SELECT @FOUND  = 'NO' 
--			
--			SELECT @return_val = @FOUND
--			
--			END
--		ExitLabel1:
--		SELECT @return_val = @found
--		return @return_val
--		SET NOCOUNT OFF
--		
--	END
--GO
If Exists ( SELECT name 
            FROM sysobjects  
            WHERE name = 'pr_analyze_config_tables'
            AND type = 'P')
	DROP PROCEDURE pr_analyze_config_tables
GO 
CREATE PROCEDURE pr_analyze_config_tables
AS 
	BEGIN
		SET NOCOUNT ON
		
		DECLARE @EXEC_IMMEDIATE_VAR VARCHAR (4000)
		SELECT @EXEC_IMMEDIATE_VAR  = 'UPDATE STATISTICS ATTRIBUTE_KPI' 
		EXECUTE (@EXEC_IMMEDIATE_VAR)
		
		DECLARE @EXEC_IMMEDIATE_VAR1 VARCHAR (4000)
		SELECT @EXEC_IMMEDIATE_VAR1  = 'UPDATE STATISTICS ATTRIBUTE_META' 
		EXECUTE (@EXEC_IMMEDIATE_VAR1)
		
		DECLARE @EXEC_IMMEDIATE_VAR2 VARCHAR (4000)
		SELECT @EXEC_IMMEDIATE_VAR2  = 'UPDATE STATISTICS BPPM_SERVER_CONFIG' 
		EXECUTE (@EXEC_IMMEDIATE_VAR2)
		
		DECLARE @EXEC_IMMEDIATE_VAR3 VARCHAR (4000)
		SELECT @EXEC_IMMEDIATE_VAR3  = 'UPDATE STATISTICS DEVICE_CNTL' 
		EXECUTE (@EXEC_IMMEDIATE_VAR3)
		
		DECLARE @EXEC_IMMEDIATE_VAR4 VARCHAR (4000)
		SELECT @EXEC_IMMEDIATE_VAR4  = 'UPDATE STATISTICS GROUPDESC' 
		EXECUTE (@EXEC_IMMEDIATE_VAR4)
		
		DECLARE @EXEC_IMMEDIATE_VAR5 VARCHAR (4000)
		SELECT @EXEC_IMMEDIATE_VAR5  = 'UPDATE STATISTICS GROUPMOS' 
		EXECUTE (@EXEC_IMMEDIATE_VAR5)
		
		DECLARE @EXEC_IMMEDIATE_VAR6 VARCHAR (4000)
		SELECT @EXEC_IMMEDIATE_VAR6  = 'UPDATE STATISTICS GROUPTREE' 
		EXECUTE (@EXEC_IMMEDIATE_VAR6)
		
		DECLARE @EXEC_IMMEDIATE_VAR7 VARCHAR (4000)
		SELECT @EXEC_IMMEDIATE_VAR7  = 'UPDATE STATISTICS ITEMCFG_DTLS' 
		EXECUTE (@EXEC_IMMEDIATE_VAR7)
		
		DECLARE @EXEC_IMMEDIATE_VAR8 VARCHAR (4000)
		SELECT @EXEC_IMMEDIATE_VAR8  = 'UPDATE STATISTICS ITEM_CFG' 
		EXECUTE (@EXEC_IMMEDIATE_VAR8)
		
		DECLARE @EXEC_IMMEDIATE_VAR9 VARCHAR (4000)
		SELECT @EXEC_IMMEDIATE_VAR9  = 'UPDATE STATISTICS MO_META' 
		EXECUTE (@EXEC_IMMEDIATE_VAR9)
		
		DECLARE @EXEC_IMMEDIATE_VAR10 VARCHAR (4000)
		SELECT @EXEC_IMMEDIATE_VAR10  = 'UPDATE STATISTICS PARAM_CNTL' 
		EXECUTE (@EXEC_IMMEDIATE_VAR10)
		
		DECLARE @EXEC_IMMEDIATE_VAR11 VARCHAR (4000)
		SELECT @EXEC_IMMEDIATE_VAR11  = 'UPDATE STATISTICS PERS_OBJ_INSTANCES' 
		EXECUTE (@EXEC_IMMEDIATE_VAR11)
		
		DECLARE @EXEC_IMMEDIATE_VAR12 VARCHAR (4000)
		SELECT @EXEC_IMMEDIATE_VAR12  = 'UPDATE STATISTICS PERS_OBJ_PARENTS' 
		EXECUTE (@EXEC_IMMEDIATE_VAR12)
		
		DECLARE @EXEC_IMMEDIATE_VAR13 VARCHAR (4000)
		SELECT @EXEC_IMMEDIATE_VAR13  = 'UPDATE STATISTICS SCHEDULED_DOWN_MOS' 
		EXECUTE (@EXEC_IMMEDIATE_VAR13)
		
		DECLARE @EXEC_IMMEDIATE_VAR14 VARCHAR (4000)
		SELECT @EXEC_IMMEDIATE_VAR14  = 'UPDATE STATISTICS SUPPLEMENTARY_FIELD_INFO' 
		EXECUTE (@EXEC_IMMEDIATE_VAR14)
		
		DECLARE @EXEC_IMMEDIATE_VAR15 VARCHAR (4000)
		SELECT @EXEC_IMMEDIATE_VAR15  = 'UPDATE STATISTICS TAGCLASS' 
		EXECUTE (@EXEC_IMMEDIATE_VAR15)
		
		DECLARE @EXEC_IMMEDIATE_VAR16 VARCHAR (4000)
		SELECT @EXEC_IMMEDIATE_VAR16  = 'UPDATE STATISTICS TAGCLASS_MAPPING' 
		EXECUTE (@EXEC_IMMEDIATE_VAR16)

		DECLARE @EXEC_IMMEDIATE_VAR17 VARCHAR (4000)
		SELECT @EXEC_IMMEDIATE_VAR17  = 'UPDATE STATISTICS DEPENDENCY' 
		EXECUTE (@EXEC_IMMEDIATE_VAR17)

		DECLARE @EXEC_IMMEDIATE_VAR18 VARCHAR (4000)
		SELECT @EXEC_IMMEDIATE_VAR18  = 'UPDATE STATISTICS NODE' 
		EXECUTE (@EXEC_IMMEDIATE_VAR18)
		   
		--IF ( UPPER(REUNIV.fn_check_mv_exists('WEEKLYSUMMARYDATA'))= 'YES' ) 
		--BEGIN 
		--	DECLARE @EXEC_IMMEDIATE_VAR17 VARCHAR (4000)
		--	SELECT @EXEC_IMMEDIATE_VAR17  = 'Query not supported' 
		--	EXECUTE (@EXEC_IMMEDIATE_VAR17)
		--END
   
		--IF ( UPPER(REUNIV.fn_check_mv_exists('MONTHLYSUMMARYDATA'))= 'YES' ) 
		--BEGIN 
		--	DECLARE @EXEC_IMMEDIATE_VAR18 VARCHAR (4000)
		--	SELECT @EXEC_IMMEDIATE_VAR18  = 'Query not supported' 
		--	EXECUTE (@EXEC_IMMEDIATE_VAR18)
		--END
   
		--IF ( UPPER(REUNIV.fn_check_mv_exists('QUATERLYSUMMARYDATA'))= 'YES' ) 
		--BEGIN 
		--	DECLARE @EXEC_IMMEDIATE_VAR19 VARCHAR (4000)
		--	SELECT @EXEC_IMMEDIATE_VAR19  = 'Query not supported' 
		--	EXECUTE (@EXEC_IMMEDIATE_VAR19)
		--END
   
		--IF ( UPPER(REUNIV.fn_check_mv_exists('YEARLYSUMMARYDATA'))= 'YES' ) 
		--BEGIN 
		--	DECLARE @EXEC_IMMEDIATE_VAR20 VARCHAR (4000)
		--	SELECT @EXEC_IMMEDIATE_VAR20  = 'Query not supported' 
		--	EXECUTE (@EXEC_IMMEDIATE_VAR20)
		--END
   
		--IF ( UPPER(REUNIV.fn_check_mv_exists('VW_CONFIGDATA'))= 'YES' ) 
		--BEGIN 
		--	DECLARE @EXEC_IMMEDIATE_VAR21 VARCHAR (4000)
		--	SELECT @EXEC_IMMEDIATE_VAR21  = 'Query not supported' 
		--	EXECUTE (@EXEC_IMMEDIATE_VAR21)
		--END
   
		SET NOCOUNT OFF
	END

GO

If Exists ( SELECT name 
            FROM sysobjects  
            WHERE name = 'rebuild_all_indexes'
            AND type = 'P')
	DROP PROCEDURE rebuild_all_indexes
GO 

CREATE PROCEDURE rebuild_all_indexes
AS 
	BEGIN
		SET NOCOUNT ON
		
		DECLARE @TableName varchar(255) 
		DECLARE TableCursor CURSOR FOR 
		 
		SELECT table_name FROM information_schema.tables 
		WHERE table_type = 'base table' and table_name not in ('DATA_GAP','SUMMARIZED_MOS','DAILY_SUMMARIZED_MOS','RETRIEVED_MO_TIMESTAMP','bppm_server_config','DATA_RETENTION')
		 
		OPEN TableCursor 
		FETCH NEXT FROM TableCursor INTO @TableName 
		WHILE @@FETCH_STATUS = 0 
		BEGIN 
				DBCC DBREINDEX(@TableName,' ',90)  
				FETCH NEXT FROM TableCursor INTO @TableName 
		 
		END 
		CLOSE TableCursor 
		DEALLOCATE TableCursor 
		SET NOCOUNT OFF

	END
GO
If Exists ( SELECT name 
            FROM sysobjects  
            WHERE name = 'RE_DAILY_JOB'
            AND type = 'P')
	DROP PROCEDURE RE_DAILY_JOB
GO 
CREATE PROCEDURE RE_DAILY_JOB
AS 
	BEGIN
		SET NOCOUNT ON
		
		DECLARE @err_num                                  FLOAT 
		DECLARE @err_msg                                  VARCHAR(100) 
		DECLARE @valid_day                                FLOAT 
		DECLARE @startDate datetime2 
		DECLARE @endDate datetime2 

		SELECT @valid_day  =  CONVERT(NUMERIC(8, 2), DATEPART(DD, GETDATE()))
		IF @valid_day = 1 or @valid_day = 15 
		BEGIN 
			EXEC LOGGER 'INFO'  ,  'RE_DAILY_JOB: component_status_intervals refreshing the date range  ' 
			set @startDate =DATEADD(day,-365,GETDATE())
			set @endDate=DATEADD(day,+31,GETDATE())
			--truncate the data to the day
			truncate table component_status_intervals
			set @startDate=cast(@startDate As Date)
			set @endDate=cast(@endDate As Date)
			
			WHILE @startDate<= @endDate
			BEGIN
				set @startDate =DATEADD(minute,5,@startDate)
				insert into component_status_intervals values(@startDate)
			END
			
			DECLARE @EXEC_IMMEDIATE_VAR VARCHAR (4000)
			
			SELECT @EXEC_IMMEDIATE_VAR  = 'UPDATE STATISTICS COMPONENT_STATUS_INTERVALS' 
			EXECUTE (@EXEC_IMMEDIATE_VAR)
			
			SELECT @EXEC_IMMEDIATE_VAR  = 'alter index idx_status_intervals on component_status_intervals rebuild' 
			EXECUTE (@EXEC_IMMEDIATE_VAR)
		END	
		SELECT @valid_day  =  datepart(dw,getdate())
		
		IF @valid_day = 2 
		BEGIN 
			EXEC LOGGER 'INFO'  ,  'RE_DAILY_JOB: Rebuilding all indexes started.' 
			EXEC REBUILD_ALL_INDEXES
			EXEC LOGGER 'INFO'  ,  'RE_DAILY_JOB: Rebuilding all indexes completed.' 
		END	
		
		SET NOCOUNT OFF
	END

GO	


If Exists ( SELECT name 
            FROM sysobjects  
            WHERE name = 'refresh_extended_slots'
            AND type = 'P')
	
DROP PROCEDURE refresh_extended_slots

GO

CREATE PROCEDURE refresh_extended_slots
AS
BEGIN
		truncate table VW_EXTENDED_COMPONENT_SLOT_D
		insert into REUNIV.VW_EXTENDED_COMPONENT_SLOT_D 
	select COMPONENT_ID,component,name, ISNULL(value,'[EMPTY_VALUE]')VALUE from
	(	SELECT
				 COMPONENT as COMPONENT_ID,
				 COMPONENT,
				 NAME,
				 VALUE
		FROM  REUNIV.extended_component_slot_d with (nolock)
		UNION ALL
	 	SELECT
				 COMPONENT_ID,
				 COMPONENT_ID,
				 'Category',
				 CATEGORY
		FROM  REUNIV.component_d with (nolock)
		UNION ALL
	 	SELECT
				 COMPONENT_ID,
				 COMPONENT_ID,
				 'Company',
				 COMPANY
		FROM  REUNIV.component_d with (nolock)
		UNION ALL
	 	SELECT
				 COMPONENT_ID,
				 COMPONENT_ID,
				 'Department',
				 DEPARTMENT
		FROM  REUNIV.component_d with (nolock)
		UNION ALL
	 	SELECT
				 COMPONENT_ID,
				 COMPONENT_ID,
				 'ImpactCostPerSec',
				 IMPACT_COST_PER_SEC
		FROM  REUNIV.component_d with (nolock)
		UNION ALL
	 	SELECT
				 COMPONENT_ID,
				 COMPONENT_ID,
				 'ImpactCostUnit',
				 IMPACT_COST_PER_UNIT
		FROM  REUNIV.component_d with (nolock)
		UNION ALL
	 	SELECT
				 COMPONENT_ID,
				 COMPONENT_ID,
				 'ManufacturerName',
				 MANUFACTURER_NAME
		FROM  REUNIV.component_d with (nolock)
		UNION ALL
	 	SELECT
				 COMPONENT_ID,
				 COMPONENT_ID,
				 'Name',
				 NAME
		FROM  REUNIV.component_d with (nolock)
		UNION ALL
	 	SELECT
				 COMPONENT_ID,
				 COMPONENT_ID,
				 'OwnerContact',
				 OWNER_CONTACT
		FROM  REUNIV.component_d with (nolock)
		UNION ALL
	 	SELECT
				 COMPONENT_ID,
				 COMPONENT_ID,
				 'Region',
				 REGION
		FROM  REUNIV.component_d with (nolock)
		UNION ALL
	 	SELECT
				 COMPONENT_ID,
				 COMPONENT_ID,
				 'CityName',
				 CITY_NAME
		FROM  REUNIV.component_d with (nolock)
		UNION ALL
	 	SELECT
				 COMPONENT_ID,
				 COMPONENT_ID,
				 'Site',
				 SITE
		FROM  REUNIV.component_d with (nolock)
		UNION ALL
	 	SELECT
				 COMPONENT_ID,
				 COMPONENT_ID,
				 'Type',
				 TYPE
		FROM  REUNIV.component_d with (nolock)
		UNION ALL
	 	SELECT
				 COMPONENT_ID,
				 COMPONENT_ID,
				 'InsertType',
				 INSERTTYPE
		FROM  REUNIV.component_d with (nolock)
		UNION ALL
	 	SELECT
				 COMPONENT_ID,
				 COMPONENT_ID,
				 'Domain',
				 DOMAIN
		FROM  REUNIV.component_d with (nolock)
		UNION ALL
	 	SELECT
				 COMPONENT_ID,
				 COMPONENT_ID,
				 'Model',
				 MODEL
		FROM  REUNIV.component_d with (nolock)
		UNION ALL
	 	SELECT
				 COMPONENT_ID,
				 COMPONENT_ID,
				 'OSName',
				 OS_NAME
		FROM  REUNIV.component_d with (nolock)
		UNION ALL
	 	SELECT
				 COMPONENT_ID,
				 COMPONENT_ID,
				 'OSType',
				 OS_TYPE
		FROM  REUNIV.component_d with (nolock)
		UNION ALL
	 	SELECT
				 COMPONENT_ID,
				 COMPONENT_ID,
				 'OSProductSuite',
				 OS_PRODUCTSUITE
		FROM  REUNIV.component_d with (nolock)
		UNION ALL
	 	SELECT
				 COMPONENT_ID,
				 COMPONENT_ID,
				 'ImpactedAreas',
				 IMPACTEDAREAS
		FROM  REUNIV.component_d with (nolock)
		UNION ALL
	 	SELECT
				 COMPONENT_ID,
				 COMPONENT_ID,
				 'PrimaryCapability',
				 PRIMARY_CAPABILITY
		FROM  REUNIV.component_d with (nolock)
		UNION ALL
	 	SELECT
				 COMPONENT_ID,
				 COMPONENT_ID,
				 'SiteGroup',
				 SITE_GROUP
		FROM  REUNIV.component_d with (nolock)
		UNION ALL
	 	SELECT
				 COMPONENT_ID,
				 COMPONENT_ID,
				 'SystemName',
				 SYSTEM_NAME
		FROM  REUNIV.component_d with (nolock)
		UNION ALL
	 	SELECT
				 COMPONENT_ID,
				 COMPONENT_ID,
				 'SystemRole',
				 SYSTEM_ROLE
		FROM  REUNIV.component_d with (nolock)
		UNION ALL
	 	SELECT
				 COMPONENT_ID,
				 COMPONENT_ID,
				 'SystemEnvironment',
				 SYSTEMENVIRONMENT
		FROM  REUNIV.component_d with (nolock)
		UNION ALL
	 	SELECT
				 COMPONENT_ID,
				 COMPONENT_ID,
				 'SupportedBy',
				 SUPPORTEDBY
		FROM  REUNIV.component_d with (nolock)
		UNION ALL
	 	SELECT
				 COMPONENT_ID,
				 COMPONENT_ID,
				 'VersionNumber',
				 VERSION_NUMBER
		FROM  REUNIV.component_d with (nolock)
		UNION ALL
	 	SELECT
				 COMPONENT_ID,
				 COMPONENT_ID,
				 'isVirtual',
				 IS_VIRTUAL
		FROM  REUNIV.component_d with (nolock)
		UNION ALL
	 	SELECT
				 COMPONENT_ID,
				 COMPONENT_ID,
				 'OwnerName',
				 OWNER_NAME
		FROM  REUNIV.component_d with (nolock)
		UNION ALL
	 	SELECT
				 COMPONENT_ID,
				 COMPONENT_ID,
				 'Description',
				 DESCRIPTION
		FROM  REUNIV.component_d with (nolock)
		UNION ALL
	 	SELECT
				 COMPONENT_ID,
				 COMPONENT_ID,
				 'mc_creation_time',
				 MC_CREATION_TIME
		FROM  REUNIV.component_d with (nolock)
		) compview
		
END		
GO


exec refresh_extended_slots
GO



If Exists ( SELECT name 
            FROM sysobjects  
            WHERE name = 'RE_HOURLY_JOB'
            AND type = 'P')
	DROP PROCEDURE RE_HOURLY_JOB
GO 
CREATE PROCEDURE RE_HOURLY_JOB
AS 
	BEGIN
		SET NOCOUNT ON
			EXEC LOGGER 'INFO'  ,  'RE_DAILY_JOB: Updating Extended Slots View.' 
			EXEC REUNIV.refresh_extended_slots
			EXEC LOGGER 'INFO'  ,  'RE_DAILY_JOB: Updating Extended Slots View completed.' 
		
		
		SET NOCOUNT OFF
	END

GO
