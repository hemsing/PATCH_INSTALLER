package com.bmc.util.os;

public class ServiceNotFoundException extends Exception {
	public ServiceNotFoundException(String error)
	{
		super(error);
	}

}
