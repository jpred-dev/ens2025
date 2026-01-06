package customexception;

public class MemoryIndexOutOfBoundsException extends CustomException {
	private static final long serialVersionUID = 1L;

	
	public MemoryIndexOutOfBoundsException() { }
	
	public MemoryIndexOutOfBoundsException(String message) {
		super(message);
	}
	
	@Override
	public String getMessage() {
		StringBuilder sb = new StringBuilder("");

		if (line != null)
			sb.append("Error en línea " + line + ": ");
		else
			sb.append("Error: ");
		
		if (message != null)
			sb.append(message);
		else
			sb.append("Sobrepasado límite de memoria.");
		
		return sb.toString();
	}
}
