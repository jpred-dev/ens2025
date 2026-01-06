package ens2025.console.consoleexception;

public class InvalidParameterNumberException extends ConsoleException {
	private static final long serialVersionUID = 1L;
	private int argNum;
	private int argNumMax;

	public InvalidParameterNumberException(int argNum) {
		this.argNum = argNum;
		this.argNumMax = 0;
	}
	
	public InvalidParameterNumberException(int argMin, int argMax) {
		this.argNum = argMin;
		this.argNumMax = argMax;
	}
	
	@Override
	public String getMessage() {
		if (argNum == 0 && argNumMax == 0)
			return "ERROR: Este comando debe invocarse sin argumentos.";
		if (argNumMax == 0)
			return "ERROR: Este comando debe invocarse estrictamente con " + argNum +" argumento(s).";
		return "ERROR: Este comando debe invocarse con entre " + argNum + " y " + argNumMax + " argumentos.";
	}
}
