package com.validate.exception;

public class FileStorageException extends RuntimeException {
	/**
	 *
	 */
	private static final long serialVersionUID = 765277374373793372L;

	public FileStorageException(String message) {
		super(message);
	}

	public FileStorageException(String message, Throwable cause) {
		super(message, cause);
	}
}