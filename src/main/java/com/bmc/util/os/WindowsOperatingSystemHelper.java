package com.bmc.util.os;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.bmc.util.database.impl.SQLScriptRunner;
import com.bmc.util.misc.SystemProperties;
import com.bmc.util.misc.ThreadHelper;

public class WindowsOperatingSystemHelper extends GenericOperatingSystemHelper{
	
	boolean isInvalidUser(String message)
	{
		logger.debug("isInvalidUser"+message);
		
		if(message.contains("Access is denied"))
		return true;
		else 
		return false;
	}
	
	public  boolean startService(String serviceName) {
		return startService(serviceName,"");
	}
	public  boolean startService(String serviceName,String validateProcessText) {
		
		//TODO validateProcessText validate from tasklist to confirm if process started.
		boolean returnValue = false;
		serviceStatus[4] = serviceName;
		stopService[4] = serviceName;
		startService[4] = serviceName;
		startService[4] = serviceName;
		String status = "";
		int attempts=0;
		//Total 2 minutes we will wait for service to start. If it does not message will be thrown.
		try {
			Process p = executeCommands(serviceStatus);

			status = getProcessStatus(p);

			logger.info("status : " + status);
			if ((!status.equals("-1")) && (!status.equals("2")) && (!status.equals("4"))) {

				p = executeCommands(startService);

				do {
					ThreadHelper.sleep(10 * 1000);
					status = getProcessStatus(Runtime.getRuntime().exec(serviceStatus));
					
					String processOutPut=getProcessOutput(p);
					if(isInvalidUser(processOutPut))
					{
						logger.error("Received error "+processOutPut);
						return false;
					}
					if(++attempts>12)
						return false;
					
				} while (!status.equals("4"));
			}
			logger.info("Program executed Sucessfully");

			p = executeCommands(serviceStatus);
			status = getProcessStatus(p);
			if (status.equals("4")) {
				logger.info("Started the service successfully.");
				returnValue = true;
			} else {
				logger.error("Could not start the service.");
				returnValue = false;
			}

		} catch (Exception ex) {
			logger.info("Program executed unsucessfully " + ex.getStackTrace());
			ex.printStackTrace();
			returnValue = false;
		}
		return returnValue;
	}
	public  boolean stopService(String serviceName) {
		return stopService(serviceName,"");
	}
	
	
	public  boolean stopService(String serviceName,String validateProcessText) {
		//TODO validateProcessText validate from tasklist to confirm if process started.
		try {

			URL url = new SQLScriptRunner().getClass().getResource("/conf/os.properties");
			Properties prop = new Properties();
			prop.load(url.openStream());

			// get the property value and print it out
			stop_service_timeout_seconds = Integer.parseInt(prop.getProperty("os.windows.stop.service.timeout.seconds"));
			logger.info("os.windows.stop.service.timeout.seconds for Windows OS is" + (stop_service_timeout_seconds));

		} catch (Exception ae) {
			ae.printStackTrace();
		}
		
		serviceStatus[4] = serviceName;
		stopService[4] = serviceName;
		startService[4] = serviceName;
		setSCPATH();
		boolean returnValue = true;
		String status = "";
		int sleep_time = (stop_service_timeout_seconds/10)*1000;
		int attempts=0;
		while (!status.equals("1")) {
			try {
				attempts++;
				logger.info("Stopping the service "+serviceName+" attempt  " + attempts);
				if (attempts > 10) {
					logger.debug("Stopping the service attempt  "+serviceName+" " + attempts + " failed. Exiting..");
					break;
				}
				logger.info("Stopping the service ."+serviceName+" ");
				Process p = executeCommands(stopService);

				logger.debug("Executing the command " + Arrays.toString(serviceStatus));
				p = executeCommands(serviceStatus);
				logger.debug("Executed the command " + Arrays.toString(serviceStatus));
				status = getProcessStatus(p);
				if (status.equals("1")) {
					logger.info("Stopped the service "+serviceName+" successfully.");
					return true;										
				}
				logger.info("Service "+serviceName+" not stopped waiting for " +sleep_time+" milliseconds..");
				try{Thread.sleep(sleep_time); }catch(Exception ae){}
				
			} catch (IOException e) {
				logger.warn(" Error in stopping "+serviceName+" " + e);
			}
		}

		try {
			Process p = executeCommands(serviceStatus);
			status = getProcessStatus(p);
			if (!status.equals("1")) {
				logger.info("Not able to execute the sc command " + Arrays.toString(stopService));
				logger.info("So killing the process.");
				killProcess("", "Services eq " + serviceName);
			}
			p = executeCommands(serviceStatus);
			status = getProcessStatus(p);
			if (status.equals("1")) {
				logger.info("Stopped the service "+serviceName+" successfully.");
				returnValue = true;
			} else {
				logger.error("Could not stop the service "+serviceName+"");
				returnValue = false;
			}
		} catch (Exception ae) {
			logger.warn(" Error in stopping the service "+serviceName+" " + ae);
		}
		return returnValue;

	}

	static String scPath = "";

	private static String getSCPATH() {
		return scPath;
	}

	private static String setSCPATH() {
		scPath = null;

		try {
			Process pr = Runtime.getRuntime().exec("CMD /c \"echo %systemroot%\"");
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			if ((scPath = stdInput.readLine()) != null) {
				if (scPath.contains("%systemroot"))
					logger.info("Cound not found the env systemroot setting to empty");
			}
			if (scPath == null || scPath.contains("%systemroot")) {
				logger.info("Cound not found the env systemroot setting to empty");
				scPath = "";
			} else {
				scPath = scPath + "\\system32\\";
				logger.info("Found the env systemroot " + scPath);
			}
		} catch (Exception e) {
			logger.fatal("Could not find the systemroot" + e);
		}
		return scPath;

	}

	public static void main(String[] args) {
		try{
		initLogger();
		WindowsOperatingSystemHelper helper= new WindowsOperatingSystemHelper(); 
		//logger.info(executeWindowsCommand("conf\\junit.properties"));
		helper.startService("PlugPlay");
		
		//logger.info("startService="+startService("mcell_beim-prd-psn"));
		helper.stopService("PlugPlay");

		//logger.info("killTask="+killTask("chrome.exe"));
		//logger.info("killTask="+killTask("","Services eq mcell_beim-prd-psn"));
		// killTask("editplus.exe","EditPlus");
		// killTask("","Services eq mcell_beim-prd-psn");
		
		//logger.info(executeWindowsCommand("conf\\junit_windows.bat"));
		
		}catch(Exception ae)
		{
			logger.error("Error in windows execution ", ae);
		}
	}



	private static String getProcessStatus(Process p) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

		String line = "", state = "-1";
		;

		line = reader.readLine();

		List<String> processOutputLine = new ArrayList<String>();
		while (line != null) {
			processOutputLine.add(line);
			if (line.trim().startsWith("STATE")) {
				logger.debug("Line : " + line);
				state = line.trim().substring(line.trim().indexOf(":") + 1, line.trim().indexOf(":") + 4).trim();
				logger.debug("STATE : " + state);
				break;

			}
			line = reader.readLine();
		}

		if (state.equals("-1")) {
			return getProcessStatusMultLang(processOutputLine);
		} else
			return state;
	}

	private static String getProcessStatusMultLang(List<String> processOutputLine) throws IOException {
		String state = "-1";
		;
		try {
			logger.info("Found the status as -1 so trying with getProcessStatusMultiLang: ");
			int i = 0;
			for (String line : processOutputLine) {
				i++;
				logger.info(i + " " + line);

				/*
				 * 4th line is for the STATE. SERVICE_NAME : messenger TYPE : 20
				 * WIN32_SHARE_PROCESS STATE : 4 RUNNING
				 * (STOPPABLE,NOT_PAUSABLE,ACCEPTS_SHUTDOWN) WIN32_EXIT_CODE : 0
				 * (0x0) SERVICE_EXIT_CODE : 0 (0x0) CHECKPOINT : 0x0 WAIT_HINT
				 * : 0x0
				 */

				if (line.trim().indexOf("OpenService FAILED 5") != -1) {
					logger.info("Line : " + line + " Service not found.. ");
					throw new ServiceNotFoundException(
							"Service not found. Make sure you are running with Administrator/owner of service user");

				}
				// Status 1
				if (line.trim().indexOf("STOPPED") != -1 && line.trim().indexOf("1") != -1) {
					logger.debug("Line : " + line);
					state = line.trim().substring(line.trim().indexOf(":") + 1, line.trim().indexOf(":") + 4).trim();
					logger.debug("STATE : " + state);
					break;
				}
				// Status 2
				if (line.trim().indexOf("START_PENDING") != -1 && line.trim().indexOf("2") != -1) {
					logger.debug("Line : " + line);
					state = line.trim().substring(line.trim().indexOf(":") + 1, line.trim().indexOf(":") + 4).trim();
					logger.debug("STATE : " + state);
					break;
				}

				// Status 3
				if (line.trim().indexOf("STOP_PENDING") != -1 && line.trim().indexOf("3") != -1) {
					logger.debug("Line : " + line);
					state = line.trim().substring(line.trim().indexOf(":") + 1, line.trim().indexOf(":") + 4).trim();
					logger.debug("STATE : " + state);
					break;
				}
				// Status 4
				if (line.trim().indexOf("RUNNING") != -1 && line.trim().indexOf("4") != -1) {
					logger.debug("Line : " + line);
					state = line.trim().substring(line.trim().indexOf(":") + 1, line.trim().indexOf(":") + 4).trim();
					logger.debug("STATE : " + state);
					break;
				}

				if (line.trim().startsWith("STATE") || i == 4) {
					logger.debug("Line : " + line);
					if (!line.trim().equals(""))
						state = line.trim().substring(line.trim().indexOf(":") + 1, line.trim().indexOf(":") + 4)
								.trim();

					logger.debug("STATE : " + state);
					break;
				}
			}
		} catch (Exception ae) {
			state = "-1";
			logger.fatal("getProcessStatusMultLang had error  " + ae.getStackTrace() + "Setting  STATE : " + state, ae);

		}
		return state;
	}

	private static final File tmpDir = new File(System.getProperty("java.io.tmpdir"));

	static Logger logger = Logger.getLogger(WindowsOperatingSystemHelper.class);

	static {
	}

	static String[] serviceStatus = { "cmd.exe", "/c", "sc", "query", "SERVICE_NAME" };
	static String[] stopService = { "cmd.exe", "/c", "sc", "stop", "SERVICE_NAME" };
	static String[] startService = { "cmd.exe", "/c", "sc", "start", "SERVICE_NAME" };

	private static String calcDate(long millisecs) {
		SimpleDateFormat date_format = new SimpleDateFormat("MMM dd,yyyy HH:mm");
		Date resultdate = new Date(millisecs);
		return date_format.format(resultdate);
	}

	public  boolean killProcess(String processName) {
		return killProcess(processName, "");
	}

	public  boolean killProcess(String processName, String processFilter) {

		// 0 1 2 3 4 5 6
		String[] killTask = new String[] { "cmd.exe", "/c", "taskkill", "/F", "/IM", processName, "/FI",
				// 7 8 9
				"\"" + processFilter + "\"" };

		if (processName == null || processName.equals(""))
			killTask[4] = killTask[5] = "";

		if (processFilter == null || processFilter.equals(""))
			killTask[6] = killTask[7] ="";

		try {
			logger.info("Stopping " + processName + " with filter " + processFilter);
			Runtime.getRuntime().exec(killTask);
			logger.info("Stopping " + processName + ". Waiting for 2 seconds.." + Arrays.toString(killTask));
			ThreadHelper.sleep(2 * 1000);
			return true;

		} catch (Exception ae) {
			logger.fatal("Could not kill the " + processName + "");
			return false;
		}

		
	}

	
	public static void startServiceUsingNet(String serviceName) {

		try {
			String[] startTask = new String[] { "cmd.exe", "/c", "net", "start", "\"" + serviceName + "\"" };
			logger.info(Arrays.toString(startTask) + " started.");
			Runtime.getRuntime().exec(startTask);
			logger.info(Arrays.toString(startTask) + " finished.");

		} catch (Exception ae) {
			logger.fatal("Could not start "+serviceName+" process service");
		}

		try {
			String[] startTask = new String[] { getSCPATH() + "cmd.exe", "/c", getSCPATH() + "net", "start",
					 "\"" + serviceName + "\"" };
			logger.info(Arrays.toString(startTask) + " started.");
			Runtime.getRuntime().exec(startTask);
			logger.info(Arrays.toString(startTask) + " finished.");

		} catch (Exception ae) {
			logger.fatal("Could not start "+serviceName+" service");
		}

	}

	public static void initLogger() {
		DOMConfigurator.configure(WindowsOperatingSystemHelper.class.getResource("/conf/log4j-config.xml"));
		// Log in console in and log file
		logger.debug("Log4j appender configuration is successful !!");
		logger.info("hello info");
	}

	


	  public String toString()
	  {
	    StringBuilder result = new StringBuilder();
	    result.append( "[" );
	    result.append( "class=" + getClass().getName() );
	    result.append( ",architecture=" + getArchitecture() );
	    result.append( ",name=" + getName() );
	    result.append( ",version=" + getVersion() );
	    result.append( "]" );
	    return result.toString();
	  }
	  public String getArchitecture()
	  {
	    return architecture_;
	  }

	  public String getName()
	  {
	    return name_;
	  }

	  public String getVersion()
	  {
	    return version_;
	  }
	  
	  public WindowsOperatingSystemHelper()
	  {
	    architecture_ = SystemProperties.getOsArch();
	    name_ = SystemProperties.getOsName();
	    version_ = SystemProperties.getOsVersion();
	  }
	  @Override
	  public String executeCommand(String command) throws IOException
	  {
		  return super.executeCommand("cmd.exe /c"+command);
	  }
	  private String architecture_;
	  private String name_;
	  private String version_;
	  private int stop_service_timeout_seconds;
}
