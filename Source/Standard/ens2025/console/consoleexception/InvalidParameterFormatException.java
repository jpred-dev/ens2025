package ens2025.console.consoleexception;

public class InvalidParameterFormatException extends ConsoleException {

	private static final long serialVersionUID = 1L;

	public InvalidParameterFormatException(String m) {
		this.subMessage = m;
	}
	
	@Override
	public String getMessage() {
		return this.subMessage;
	}

}
