package customexception;

public class StackException extends CustomException {
	private static final long serialVersionUID = 1L;

	public StackException() { }
	
	public StackException(String subMessage) {
		this.message = subMessage;
	}
	
	@Override
	public String getMessage() {
		StringBuilder sb = new StringBuilder("");
		
		if (line != null)
			sb.append("Error en la dirección " + line + ": ");
		else
			sb.append("Error: ");
		
		if (message != null)
			sb.append(message);
		else
			sb.append("Comportamiento de pila no permitido.");
		
		return sb.toString();
	}

}
