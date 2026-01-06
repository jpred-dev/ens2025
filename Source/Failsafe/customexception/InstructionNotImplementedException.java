package customexception;

public class InstructionNotImplementedException extends CustomException {
	private static final long serialVersionUID = 1L;

	public InstructionNotImplementedException() { }
	
	public InstructionNotImplementedException(String message) {
		super(message);
	}
	
	@Override
	public String getMessage() {
		StringBuilder sb = new StringBuilder("Error");
		
		if (line != null)
			sb.append(" en la línea " + line);
		
		sb.append(": ");
		
		if (message != null)
			sb.append(message);
		else
			sb.append("instrucción no implementada.");
		
		return sb.toString();
	}
}
