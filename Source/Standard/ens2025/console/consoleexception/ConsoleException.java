package ens2025.console.consoleexception;

import customexception.CustomException;

public abstract class ConsoleException extends CustomException {
	private static final long serialVersionUID = 1L;

	protected String subMessage;
	
	public ConsoleException() {
		super();
		subMessage = null;
	}
	
	public ConsoleException(String subMessage) {
		super();
		this.subMessage = subMessage;
	}
	

}
