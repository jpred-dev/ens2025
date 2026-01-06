package customexception;

public class BreakpointEncounteredException extends CustomException {

	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		StringBuilder sb = new StringBuilder("Ejecución detenida");
		
		if (line != null)
			sb.append(" en la dirección " + line);
		
		sb.append(": Se ha encontrado un punto de ruptura.");
		return sb.toString();
	}

}
