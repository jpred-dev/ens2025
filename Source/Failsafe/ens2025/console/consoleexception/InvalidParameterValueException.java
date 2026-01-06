package ens2025.console.consoleexception;

public class InvalidParameterValueException extends ConsoleException {

	private static final long serialVersionUID = 1L;

	public InvalidParameterValueException(String m) {
		this.subMessage = m;
	}

	@Override
	public String getMessage() {
		return this.subMessage;
	}

}
