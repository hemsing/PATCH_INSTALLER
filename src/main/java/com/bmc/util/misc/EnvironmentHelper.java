package com.bmc.util.misc;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import org.apache.log4j.Logger;


import com.bmc.util.os.OperatingSystemHelper;

public class EnvironmentHelper {

	public static String getEnvironmentValue(String envVariableName) throws UnknownHostException {
		
		logger.trace("Getting the envVariableName="+envVariableName);
		String returnValue = null;
		if (envVariableName.equals("$HOSTNAME")) {			
			returnValue = getHostname();
			logger.debug("Received the $HOSTNAME="+returnValue);
		}
		if (envVariableName.equals("$FQDN")) {
			returnValue = getFQDN();
			logger.debug("Received the $FQDN="+returnValue);
		}
		if(returnValue==null)
		{
			Map<String, String> env = System.getenv();
	        returnValue=env.get(envVariableName.replace("$", ""));	    
	        logger.debug("Found the the $"+envVariableName+" = "+returnValue);
		}
		if(returnValue==null)
		{
			returnValue="";
			//throw new UnknownHostException(" Not able to find the keyword="+envVariableName);
		}
		return returnValue;
	}

	public static String getFQDN() throws UnknownHostException {

		String result = "";

		try {
			result = InetAddress.getLocalHost().getCanonicalHostName();

		} catch (UnknownHostException uhe) {

			// This might happen if DNS fails. If so, just consume.
		}
		if ((result == null) || (result.trim().equals(""))) {
			if (OperatingSystemHelper.isWindows())
				result = System.getenv().get("COMPUTERNAME") + "." + System.getenv().get("USERDNSDOMAIN");
			else
				// TODO
				result = null;
		}
		if ((result == null) || (result.trim().equals(""))) {
			logger.info("Not able to get the InetAddress.getLocalHost().getCanonicalHostName()");
			throw new UnknownHostException("Not able to get the FQDN");
		}
		return result;

	}

	public static String getHostname() throws UnknownHostException {

		String result = "";

		try {
			result = InetAddress.getLocalHost().getHostName();

		} catch (UnknownHostException uhe) {

			// This might happen if DNS fails. If so, just consume.
		}
		if ((result == null) || (result.trim().equals(""))) {
			logger.info("Not able to get the InetAddress.getLocalHost().getHostName()");
			result = System.getenv().get("hostname");
		}
		if ((result == null) || (result.trim().equals(""))) {
			throw new UnknownHostException("Not able to get the hostname");
		}
		return result;

	}

	static Logger logger = Logger.getLogger(EnvironmentHelper.class);
}

