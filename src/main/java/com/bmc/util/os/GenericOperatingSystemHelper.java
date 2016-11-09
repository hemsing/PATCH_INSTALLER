package com.bmc.util.os;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

abstract public class GenericOperatingSystemHelper implements OperatingSystem {
	
	public String executeCommand(String command) throws IOException {
		
		return executeCommand(command,true);
		
	}
	public String getProcessOutput(Process p) throws IOException
	{
		StringBuilder processOutput= new StringBuilder();
		BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
		BufferedReader stderror = new BufferedReader(new InputStreamReader(p.getErrorStream()));

		String line = stdout.readLine();
		while (line != null) {
			processOutput.append("\n" + line);
			logger.debug(line);
			line = stdout.readLine();
		}

		line = stderror.readLine();
		processOutput.append("\n");
		while (line != null) {
			processOutput.append(line);
			logger.debug(line);
			line = stdout.readLine();
		}	
		return processOutput.toString();
	}

	public String executeCommand(String command,boolean splitCommands) throws IOException {

		//TODO temp_execution_ is a temporary batch and shell created. We are not loggin the entry in that.
		StringBuilder processOutput = new StringBuilder();

		try {

			Process p;
			if(splitCommands)
			{
				p= executeCommands((command).split(" "));
			}
			else
			{
				p=executeCommands(new String[]{command});
			}

			BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader stderror = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			String line = stdout.readLine();
			while (line != null) {
				processOutput.append("\n" + line);
				logger.debug(line);
				line = stdout.readLine();
			}

			line = stderror.readLine();
			processOutput.append("\n");
			while (line != null) {
				processOutput.append(line);
				logger.debug(line);
				line = stdout.readLine();
			}
			
			if(!command.contains("temp_execution_"))
			logger.info("Executed command successfully ");
			
		} catch (IOException ae) {
			logger.error("Not able to execute the command " + command, ae);
			throw ae;
		}
		
		return processOutput.toString();

	}

	public  Process executeCommands(String[] command) throws IOException {
		
		Process p; 
		if(command.length>1)
		{
			logger.info("Generic command sent to Operating system Executing " + Arrays.toString(command));
			p = Runtime.getRuntime().exec(command);
		}
		else
		{
			if(!command[0].contains("temp_execution_"))
			logger.info("Generic command sent to Operating system Executing " + (command[0]));
			
			p = Runtime.getRuntime().exec(command[0]);
		}		
		
		return p;
	}

	public  String resolveEnvVars(String input) {
		if (null == input) {
			return null;
		}

		Pattern p = Pattern.compile("\\%(\\w+)\\%");
		Matcher m = p.matcher(input);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			String envVarName = null == m.group(1) ? m.group(2) : m.group(1);
			String envVarValue = System.getenv(envVarName);
			m.appendReplacement(sb, null == envVarValue ? "" : Matcher.quoteReplacement(envVarValue));
		}
		m.appendTail(sb);
		return sb.toString();
	}

	static Logger logger = Logger.getLogger(GenericOperatingSystemHelper.class);
}
