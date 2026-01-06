package customexception;

public class InvalidValueException extends CustomException {

	private static final long serialVersionUID = 1L;

	public InvalidValueException() { }
	
	public InvalidValueException(String message) {
		this.message = message;
	}
	
	
	@Override
	public String getMessage() {
		StringBuilder sb = new StringBuilder("");
		
		if (line != null)
			sb.append("Error en línea " + (line+1) + ": ");
		else
			sb.append("Error: ");
		
		if (message != null)
			sb.append(message);
		else
			sb.append("Valor inválido.");
		
		return sb.toString();
	}

}
