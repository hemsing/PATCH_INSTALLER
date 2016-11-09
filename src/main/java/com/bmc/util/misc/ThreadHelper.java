package com.bmc.util.misc;

public class ThreadHelper {
	public static void sleep(int milliseconds)
	{
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			//e.printStackTrace();
		}
	}

}
