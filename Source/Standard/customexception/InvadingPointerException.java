package customexception;

public class InvadingPointerException extends CustomException {

	private static final long serialVersionUID = 1L;

	public InvadingPointerException() { }
	
	public InvadingPointerException(String message) {
		this.message = message;
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
			sb.append("Invasión de puntero detectada.");
		
		return sb.toString();
	}

}
