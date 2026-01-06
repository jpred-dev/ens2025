package customexception;

public class LabelException extends CustomException {

	private static final long serialVersionUID = 1L;

	public LabelException() { }
	
	public LabelException(String message) {
		this.message = message;
	}
	
	
	@Override
	public String getMessage() {
		StringBuilder sb = new StringBuilder("");
		
		if (line != null)
			sb.append("Error en línea " + (line+1) + ": ");
		
		if (message != null)
			sb.append(message);
		else
			sb.append("Error de etiqueta.");
		
		return sb.toString();
	}

}
