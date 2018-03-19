package com.fastweb.core.exception;

public class BusinessException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2716952828585857651L;
	private String errorCode;
	private String message;

	public BusinessException() {
	}

	public BusinessException(String message) {
		this.message = message;
	}

	public BusinessException(String errorCode, String message) {
		this.errorCode = errorCode;
		this.message = message;
	}

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
