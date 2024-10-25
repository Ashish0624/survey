package com.ncert.survey.utils;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ncert.survey.costants.APPServiceCode;

public class ApiError {
	@JsonIgnore
	private HttpStatus status;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime timestamp;
	// Add JsonIgnore for Audit
	@JsonIgnore
	private String message;
	// Add JsonIgnore for Audit
	@JsonIgnore
	private String debugMessage;
	// Add JsonIgnore for Audit
	@JsonIgnore
	private String requestDesc;
	// Add JsonIgnore for Audit

	@JsonIgnore
	private APPServiceCode serviceCode;

	private ApiError() {
		super();
		timestamp = LocalDateTime.now();
		serviceCode = APPServiceCode.NP_SERVICE_999;
	}

	/*
	 * public ApiError( HttpStatus status ) { this(); this.status = status; }
	 */
	public ApiError(HttpStatus status, Throwable ex, String requestDesc) {
		this();
		this.status = status;
		this.message = "Unexpected error";
		this.debugMessage = ex.getLocalizedMessage();
		this.requestDesc = requestDesc;
	}

	public ApiError(HttpStatus status, String message, String requestDesc) {
		this();
		this.status = status;
		this.message = message;
		this.requestDesc = requestDesc;
	}

	public ApiError(HttpStatus status, String message, Throwable ex, String requestDesc) {
		this();
		this.status = status;
		this.message = message;
		this.debugMessage = ex.getLocalizedMessage();
		this.requestDesc = requestDesc;
	}

	/*
	 * public ApiError( HttpStatus status, String message ) { this(); this.status =
	 * status; this.message = message; }
	 */
	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDebugMessage() {
		return debugMessage;
	}

	public void setDebugMessage(String debugMessage) {
		this.debugMessage = debugMessage;
	}

	public String getRequestDesc() {
		return requestDesc;
	}

	public void setRequestDesc(String requestDesc) {
		this.requestDesc = requestDesc;
	}

	public APPServiceCode getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(APPServiceCode serviceCode) {
		this.serviceCode = serviceCode;
	}

}
