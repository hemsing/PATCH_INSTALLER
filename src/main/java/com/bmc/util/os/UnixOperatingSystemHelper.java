package com.bmc.util.os;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.bmc.util.io.GenericFileHelper;

public class UnixOperatingSystemHelper extends GenericOperatingSystemHelper implements OperatingSystem {

	@Override
	public boolean startService(String serviceName) {
		return startService(serviceName);
	}

	@Override
	public boolean stopService(String serviceName) {

		return stopService(serviceName,"");
	}
	
	@Override
	public boolean startService(String serviceName, String validateProcessText) {
		logger.debug("Starting service with name="+serviceName +" and  validateProcessText="+validateProcessText);
		boolean returnVal = false;
		try {
			String returnValue = executeCommand(serviceName);
			if (returnValue.startsWith("FAILED:-")) {
				returnVal = false;
			}
			returnVal=isProcessRunning(validateProcessText);
			
		} catch (IOException exception) {
			logger.fatal("Error in startService " + exception, exception);
			returnVal = false;

		}
		return returnVal;

	}
	
	private boolean isProcessRunning(String validateProcessText) throws IOException
	{
		String grepText=validateProcessText;
		if(grepText!=null&&!grepText.isEmpty())
		{
			if(!grepText.startsWith("grep"))
			{
				grepText="grep "+grepText+"|grep -v grep";
			}
			
			String processStatus=executeCommand("ps -aef | "+grepText);
			logger.info("isProcessRunning"+processStatus+"isProcessRunning");
			
			if(processStatus!=null&&processStatus.contains(validateProcessText))		
			{
				logger.debug("Found process is running containing text "+validateProcessText);
					return true;
			}
			else
			{
				logger.debug("Could NOT found process is running containing text "+validateProcessText);
				return false;
			}
		}
		else
		{
			//logger.warn("Invalid validateProcessText "+validateProcessText);
			return true;
		}
		
	}

	@Override
	public boolean stopService(String serviceName,String validateProcessText) {
		logger.debug("Stopping service with name="+serviceName +" and  validateProcessText="+validateProcessText);
		boolean returnVal = false;
		try {
			String returnValue = executeCommand(serviceName);
			if (returnValue.startsWith("FAILED:-")) {
				returnVal = false;
			}
			
			if(validateProcessText!=null&&!validateProcessText.isEmpty())
			{
			returnVal=!isProcessRunning(validateProcessText);
			}
			else
			{
				returnVal=true;
			}
		} catch (IOException exception) {
			logger.fatal("Error in startService " + exception, exception);
			returnVal = false;

		}
		return returnVal;
	}

	@Override
	public boolean killProcess(String processName) {

		return killProcess(processName, "");
	}

	@Override
	public boolean killProcess(String processName, String processFilter) {
		long time = System.currentTimeMillis();
		String temp_shell_file = "temp_execution_" + time + ".sh";
		String temp_process_kill_shell_file = "temp_execution_proess_kill_" + time + ".sh";

		String command = "";
		// String[] command_sequence = { "ps", "-aef", "|", "grep",
		// processName,"|","grep","-v","\"grep\"", processName,
		// "|","awk","'","print","\"kill","-9","\"","$1","}","'",">","process_kill.sh"};

		command = "ps -aef | grep " + processName + " | grep -v \"grep " + processName
				+ "\" | awk ' { print \" kill -9 \" $2 } ' > " + temp_process_kill_shell_file;

		if (null != processFilter && !processFilter.isEmpty()) {
			command = "ps -aef | " + processFilter + " | grep " + processName + " | grep -v \"grep " + processName
					+ "\"| awk '{ print \"kill -9 \" $1 }'>" + temp_process_kill_shell_file;
		}

		try {
			logger.info("Executing " + command);
			GenericFileHelper.replaceAndWriteToFile(temp_shell_file, command);

			executeCommand("sh " + temp_shell_file);
			executeCommand("sh " + temp_process_kill_shell_file);

			GenericFileHelper.setSuppressLoggings(true);
			GenericFileHelper.deleteFile(temp_shell_file);
			GenericFileHelper.deleteFile(temp_process_kill_shell_file);
			GenericFileHelper.setSuppressLoggings(false);
			logger.info("Executed " + command + " successfully.");
			// executeCommands(new String[]{"sh proess_kill.sh"});
		} catch (IOException ae) {
			logger.info("Error in " + command + " execution ." + ae, ae);
			ae.printStackTrace();
			return false;
		}

		return true;
	}

	@Override
	public String executeCommand(String command) throws IOException {
		long time = System.currentTimeMillis();
		String temp_shell_file = "temp_execution_" + time + ".sh";

		String resultValue = "";
		try {

			if (command!=null&&!command.contains("temp_execution_"))
				logger.info("Executing " + command);

			GenericFileHelper.replaceAndWriteToFile(temp_shell_file, command);

			resultValue = executeCommand("/bin/sh " + temp_shell_file, false);

			GenericFileHelper.setSuppressLoggings(true);
			GenericFileHelper.deleteFile(temp_shell_file);
			GenericFileHelper.setSuppressLoggings(false);

			if (!command.contains("temp_execution_"))
				logger.info("Executed " + command + " successfully.");

		} catch (IOException ae) {
			logger.info("Error in " + command + " execution ." + ae, ae);
			ae.printStackTrace();
			return "FAILED:- " + ae.getStackTrace().toString();
		}

		return resultValue;
	}

	@Override
	public String resolveEnvVars(String input) {
		// TODO Auto-generated method stub
		return null;
	}

	static Logger logger = Logger.getLogger(UnixOperatingSystemHelper.class);

	
}
