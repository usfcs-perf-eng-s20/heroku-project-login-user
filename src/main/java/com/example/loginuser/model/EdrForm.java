package com.example.loginuser.model;

public class EdrForm {
	
	String method;
	String path;
	int processingTimeInMiliseconds;
	String responseCode;
	String serviceName;
	boolean success;
	String timestamp;
	String username;
	
	public EdrForm(String method, String path, int time, String responseCode, String serviceName, boolean success, String timestamp, String username)
	{
		super();
		this.method = method;
		this.path = path;
		this.processingTimeInMiliseconds = time;
		this.responseCode = responseCode;
		this.success = success;
		this.timestamp = timestamp;
		this.username = username;
	}
	
	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getProcessingTimeInMiliseconds() {
		return processingTimeInMiliseconds;
	}

	public void setProcessingTimeInMiliseconds(int processingTimeInMiliseconds) {
		this.processingTimeInMiliseconds = processingTimeInMiliseconds;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
