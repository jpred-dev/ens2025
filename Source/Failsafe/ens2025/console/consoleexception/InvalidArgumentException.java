package ens2025.console.consoleexception;

public class InvalidArgumentException extends ConsoleException {

	private static final long serialVersionUID = 1L;

	public InvalidArgumentException(String m) {
		this.subMessage = m;
	}
	
	@Override
	public String getMessage() {
		return this.subMessage;
	}

}
