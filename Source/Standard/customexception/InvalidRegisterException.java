package customexception;

public class InvalidRegisterException extends CustomException {

	private static final long serialVersionUID = 1L;

	public InvalidRegisterException() { }
	
	public InvalidRegisterException(String message) {
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		StringBuilder sb = new StringBuilder("");

		if (line != null)
			sb.append("Error en línea " + (line+1));
		else
			sb.append("Error de identificador de registro inválido");
		
		if (message != null)
			sb.append(": " + message);
		else
			sb.append(".");
		
		return sb.toString();
	}

}
