package customexception;

public class EmptyFileException extends CustomException {
	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		return "El fichero está vacío";
	}

}
