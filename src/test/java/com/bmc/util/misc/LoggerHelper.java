package com.bmc.util.misc;

import org.apache.log4j.xml.DOMConfigurator;

import com.bmc.util.os.WindowsOperatingSystemHelper;

public class LoggerHelper {
	
	public static void initLogger()
	{
		DOMConfigurator.configure(WindowsOperatingSystemHelper.class.getResource("/conf/log4j-config.xml"));
		
	}

}
