package com.fastweb.core.exception;

public class CrudRepositoryException extends RuntimeException{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2716952828585857651L;
	private String errorCode;
	private String message;

	public CrudRepositoryException() {
	}

	public CrudRepositoryException(String message) {
		this.message = message;
	}

	public CrudRepositoryException(String errorCode, String message) {
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
