package com.ncert.survey.exception;

import com.ncert.survey.costants.APPServiceCode;

public class CustomException extends Exception {
	private static final long serialVersionUID = 1L;
	private APPServiceCode appServiceCode;

	public CustomException(String message, Exception ex, APPServiceCode appServiceCode) {
		super(message, ex);
		this.appServiceCode = appServiceCode;
	}

	public APPServiceCode getAppServiceCode() {
		return appServiceCode;
	}

	public void setAppServiceCode(APPServiceCode appServiceCode) {
		this.appServiceCode = appServiceCode;
	}
}
