package com.ncert.survey.exception;

//@ResponseStatus(value = HttpStatus.NON_AUTHORITATIVE_INFORMATION, reason = "Signature Validation Failed")
//203
public class SignatureValidationFailed extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1591816129924604536L;
	private String method;

	public SignatureValidationFailed() {
	}

	public SignatureValidationFailed(String message) {
		super(message);
	}

	public SignatureValidationFailed(Throwable cause) {
		super(cause);
	}

	public SignatureValidationFailed(String message, String inMethod) {
		super(message);
		method = inMethod;
	}

	public SignatureValidationFailed(String message, Throwable cause) {
		super(message, cause);
	}

	public String getMethod() {
		return method;
	}
}
