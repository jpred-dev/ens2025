package customexception;

public class DataParsingException extends CustomException {

	private static final long serialVersionUID = 1L;

	public DataParsingException() { }
	
	public DataParsingException(String message) {
		super(message);
	}


	@Override
	public String getMessage() {
		StringBuilder sb = new StringBuilder("Error de procesamiento de datos");
		
		if (line != null)
			sb.append(" en línea " + line);
		
		if (message != null)
			sb.append(": " + message);
		
		return sb.toString();
	}

}
