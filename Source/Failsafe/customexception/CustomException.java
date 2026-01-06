package customexception;

import data.RData;

public abstract class CustomException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	protected Integer line;
	protected String message;
	

	public CustomException() {
		super();
		line = null;
		message = null;
	}
	
	public CustomException(String message) {
		super();
		this.message = message;
		line = null;
	}
	
	
	//Se redeclara el método de obtención de mensaje de la superclase por un método abstracto.
	//De esta manera, se fuerza a cada excepción heredada a implementar una función que construya el mensaje
	//en función de la información recabada.
	@Override
	public abstract String getMessage();
	
	public void setLine(RData d) {
		line = d.getValue();
	}
	
	public void setLine(Integer i) {
		line = i;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
}
