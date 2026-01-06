package customexception;

public class ExpressionIntegrityException extends CustomException {

	private static final long serialVersionUID = 1L;

	public ExpressionIntegrityException() { }
	
	public ExpressionIntegrityException(String message) {
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
			sb.append("Expresión aritmética incorrecta.");
		
		return sb.toString();
	}

}
