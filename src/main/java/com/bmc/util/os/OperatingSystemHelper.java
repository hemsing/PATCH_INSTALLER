package com.bmc.util.os;

import com.bmc.util.misc.SystemProperties;

public class OperatingSystemHelper {

	private static OperatingSystem windowsOsHelper = new WindowsOperatingSystemHelper();
	private static OperatingSystem unixOsHelper = new UnixOperatingSystemHelper();
	
	public static OperatingSystem getInstance() {
		if (isWindows()) {
			return windowsOsHelper;
		} else {
			return unixOsHelper;
		}

	}

	public static void main(String[] arg) {
		
		System.out.print(OperatingSystemHelper.getInstance());

	}

	private static String getName() {
		return SystemProperties.getOsName();
	}

	public static boolean isWindows() {
		return getName().startsWith("Windows");
	}

	public boolean isUnix() {
		return !getName().startsWith("Windows");
	}

	

}
