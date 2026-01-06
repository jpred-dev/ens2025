package customexception;

public class InvalidCharacterException extends CustomException {

	private static final long serialVersionUID = 1L;

	public InvalidCharacterException() { }
	
	public InvalidCharacterException(String message) {
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
			sb.append("carácter no válido.");
		
		return sb.toString();
	}

}
