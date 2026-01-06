package customexception;

public class DivideByZeroException extends CustomException {

	public DivideByZeroException() { }

	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		StringBuilder sb = new StringBuilder("Error");
		
		if (line != null)
			sb.append(" en línea " + (line+1));
		
		sb.append(": Se ha intentado dividir entre cero.");
		return sb.toString();
	}

}
