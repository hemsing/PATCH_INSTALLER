package com.bmc.util.os;

import java.io.IOException;

public interface OperatingSystem {

	public  String executeCommand(String command) throws IOException;
	public  String executeCommand(String command,boolean splitCommands) throws IOException;
	public  boolean startService(String serviceName);
	public  boolean startService(String serviceName,String validateProcessText);
	public	boolean stopService(String serviceName);
	public  boolean stopService(String serviceName,String validateProcessText);	
	public  boolean killProcess(String processName);
	public  boolean killProcess(String processName, String processFilter);
	public  String resolveEnvVars(String input) ;
	
}
