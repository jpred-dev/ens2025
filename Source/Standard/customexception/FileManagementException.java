package customexception;

public class FileManagementException extends CustomException {
	private static final long serialVersionUID = 1L;

	public FileManagementException() { }

	public FileManagementException(String message) {
		super(message);
	}
	
	@Override
	public String getMessage() {
		StringBuilder sb = new StringBuilder("");
		
		if (message != null)
			sb.append(message);
		else
			sb.append("Error en el procesamiento de un fichero.");
		
		return sb.toString();
	}

}
