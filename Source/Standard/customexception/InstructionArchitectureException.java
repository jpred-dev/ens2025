package customexception;

public class InstructionArchitectureException extends CustomException {

	private static final long serialVersionUID = 1L;

	public InstructionArchitectureException() { }
	
	public InstructionArchitectureException(String message) {
		super(message);
	}
	
	@Override
	public String getMessage() {
		StringBuilder sb = new StringBuilder("");
		
		if (line != null)
			sb.append("Error en la dirección " + line + ": ");
		
		
		if (message != null)
			sb.append(message);
		else
			sb.append("Error en la codificación de la instrucción.");
		
		return sb.toString();
	}

}
