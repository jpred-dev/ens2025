package settings;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import customexception.FileManagementException;
import data.RData;
import data.RWData;

public class Settings {
	//Constantes de configuración por defecto.
	private static final StackGrowth STACKGROWTH_DEFAULT = StackGrowth.NEGATIVE;
	private static final NumericalRepresentation NUMREP_DEFAULT = NumericalRepresentation.DEC;
	private static final boolean CODEINVASION_DEFAULT = false;
	private static final boolean STACKINVASION_DEFAULT = false;
	private static final boolean RESETREGISTERS_DEFAULT = true;
	private static final RData CODESTART_DEFAULT = new RWData(0);
	private static final RData CODEEND_DEFAULT = new RWData(0);
	private static final RData STACKSTART_DEFAULT = new RWData(RWData.MAX_UNSIGNED_VALUE);
	private static final String DEFAULTFILENAME = "ens2025.cfg";
	private static final boolean OVERFLOW_DEFAULT = false;
	private static final boolean UNDERFLOW_DEFAULT = false;
	
	//Atributos.
	private StackGrowth stackGrowth;
	private boolean checkPCInvadingStack;
	private boolean checkSPInvadingCode;
	private boolean resetRegistersWithNewRun;
	private boolean checkOverflow;
	private boolean checkUnderflow;
	private NumericalRepresentation numRep;
	private RWData codeStart;
	private RWData codeEnd;
	private RWData stackStart;
	
	public Settings() {
		stackGrowth = STACKGROWTH_DEFAULT;
		numRep = NUMREP_DEFAULT;
		checkPCInvadingStack = STACKINVASION_DEFAULT;
		checkSPInvadingCode = CODEINVASION_DEFAULT;
		checkOverflow = OVERFLOW_DEFAULT;
		checkUnderflow = UNDERFLOW_DEFAULT;
		resetRegistersWithNewRun = RESETREGISTERS_DEFAULT;
		codeStart = new RWData(CODESTART_DEFAULT);
		codeEnd = new RWData(CODEEND_DEFAULT);
		stackStart = new RWData(STACKSTART_DEFAULT);
	}
	
	public void setToDefault() {
		stackGrowth = STACKGROWTH_DEFAULT;
		numRep = NUMREP_DEFAULT;
		checkPCInvadingStack = STACKINVASION_DEFAULT;
		checkSPInvadingCode = CODEINVASION_DEFAULT;
		checkOverflow = OVERFLOW_DEFAULT;
		checkUnderflow = UNDERFLOW_DEFAULT;
		resetRegistersWithNewRun = RESETREGISTERS_DEFAULT;
		codeStart.assign(CODESTART_DEFAULT);
		codeEnd.assign(CODEEND_DEFAULT);
		stackStart.assign(STACKSTART_DEFAULT);
	}
	
	public void recalcStackStart() {
		RData preCodeSize;
		RWData postCodeSize;
		
		//Asignación de tamaños a las zonas anterior y posterior al código.
		preCodeSize = codeStart.getImmutableCopy();
		postCodeSize = new RWData(RWData.MAX_UNSIGNED_VALUE);
		postCodeSize.subtract(codeEnd);
		
		//Se comprueba el crecimiento de pila.
		//Si es negativo, se elige la zona de mayor tamaño y se sitúa al final de la misma.
		switch(stackGrowth) {
			case NEGATIVE:
				//Se prioriza la zona anterior al código en caso de igual tamaño para evitar invasión de zona de código por el SP.
				if(preCodeSize.isGreaterEqual(postCodeSize)) {
					stackStart.assign(codeStart);
					stackStart.subtract(1);
				}
				else
					stackStart.assign(RWData.MAX_UNSIGNED_VALUE);
				break;
			//Si es positivo, se elige la zona de mayor tamaño y se sitúa al inicio de la misma.
			case POSITIVE:
				//Se prioriza la zona posterior al código en caso de igual tamaño para evitar invasión de zona de código por el SP.
				if(postCodeSize.isGreaterEqual(preCodeSize)) {
					stackStart.assign(codeEnd);
					stackStart.add(1);
				}
				else
					stackStart.assign(0);
				break;

			default:
				break;
		}
	}
	
	public void setStackGrowth(StackGrowth sg) {
		this.stackGrowth = sg;
	}
	
	public void setCheckPCInvadingStack(boolean set) {
		checkPCInvadingStack = set;
	}
	
	public void setCheckSPInvadingCode(boolean set) {
		checkSPInvadingCode = set;
	}
	
	public void setCheckOverflow(boolean set) {
		checkOverflow = set;
	}
	
	public void setCheckUnderflow(boolean set) {
		checkUnderflow = set;
	}
	
	public void setResetRegistersWithNewRun(boolean set) {
		resetRegistersWithNewRun = set;
	}
	
	public void setNumericalRepresentation(NumericalRepresentation nr) {
		numRep = nr;
	}
	
	public StackGrowth getStackGrowth() {
		return stackGrowth;
	}
	
	public boolean getCheckPCInvadingStack() {
		return checkPCInvadingStack;
	}
	
	public boolean getCheckSPInvadingCode() {
		return checkSPInvadingCode;
	}
	
	public boolean getCheckOverflow() {
		return checkOverflow;
	}
	
	public boolean getCheckUnderflow() {
		return checkUnderflow;
	}
	
	public boolean getResetRegistersWithNewRun() {
		return resetRegistersWithNewRun;
	}
	
	public NumericalRepresentation getNumericalRepresentation() {
		return numRep;
	}
	
	public void setCodeStart(RData d) {
		codeStart.assign(d);
	}
	
	public void setCodeEnd(RData d) {
		codeEnd.assign(d);
	}
	
	public void setStackStart(RData d) {
		stackStart.assign(d);
	}
	
	public RData getCodeStart() {
		return codeStart;
	}
	
	public RData getCodeEnd() {
		return codeEnd;
	}
	
	public RData getStackStart() {
		return stackStart;
	}
	
	//Funciones de carga y guardado de configuración
	public void loadSettingsSequence() {
		//Si no se encuentra el fichero de configuración o ha habido error en su lectura, usan las opciones por defecto.
		try {
			loadSettingsFromFile(DEFAULTFILENAME);
		} catch (IOException e) {
			setToDefault();
			throw new FileManagementException("Error de lectura del fichero de configuración. Iniciando con configuración por defecto.");
		}
	}
	
	public void loadSettingsFromFile(String name) throws IOException {
		List<String> lines = Files.readAllLines(Paths.get(name));
		String[] tokens;
		
		//Primero se inicia la configuración a los valores por defecto por si al fichero le falta alguna opción.
		setToDefault();
		
		for (String l : lines) {
			tokens = l.split("=");
			//Se sigue una aproximación benévola a la lectura: se descartan líneas erróneas sin interrumpir el procesado.
			if (tokens.length != 2)
				continue;
			
			switch(tokens[0]) {
				case "CrecimientoPila":
					StackGrowth sg = StackGrowth.getFromName(tokens[1]);
					if (sg != null)
						stackGrowth = sg;
					else
						stackGrowth = STACKGROWTH_DEFAULT;
					break;
				case "BaseNumerica":
					NumericalRepresentation nr = NumericalRepresentation.getFromName(tokens[1]);
					if (nr != null)
						numRep = nr;
					else
						numRep = NUMREP_DEFAULT;
					break;
				case "InvasionCodigo":
					checkSPInvadingCode = parseTrueFalseOption(tokens[1], CODEINVASION_DEFAULT);
					break;
				case "InvasionPila":
					checkPCInvadingStack = parseTrueFalseOption(tokens[1], STACKINVASION_DEFAULT);
					break;
				case "ReiniciarRegistros":
					resetRegistersWithNewRun = parseTrueFalseOption(tokens[1], RESETREGISTERS_DEFAULT);
					break;
				case "Overflow":
					checkOverflow = parseTrueFalseOption(tokens[1], OVERFLOW_DEFAULT);
					break;
				case "Underflow":
					checkUnderflow = parseTrueFalseOption(tokens[1], UNDERFLOW_DEFAULT);
					break;
					
				default:
					//De nuevo, en caso de identificador erróneo no se hace nada.
					break;
			}
		}
		
	}
	
	private boolean parseTrueFalseOption(String optString, boolean defaultValue) {
		boolean val;
		
		switch(optString) {
			case "SI":
				val = true;
				break;
			case "NO":
				val = false;
				break;
			default:
				val = defaultValue;
				break;
		}
		
		return val;
	}
	
	public void saveSettingsToFile() {
		try {
			FileWriter fw = new FileWriter(DEFAULTFILENAME);
			PrintWriter  pw = new PrintWriter(fw);
			pw.println("CrecimientoPila=" + stackGrowth.getDescription());
			pw.println("BaseNumerica=" + numRep.toString());
			pw.println("InvasionCodigo=" + stringifyTrueFalseOption(checkSPInvadingCode));
			pw.println("InvasionPila=" + stringifyTrueFalseOption(checkPCInvadingStack));
			pw.println("ReiniciarRegistros=" + stringifyTrueFalseOption(resetRegistersWithNewRun));
			pw.println("Overflow=" + stringifyTrueFalseOption(checkOverflow));
			pw.println("Underflow=" + stringifyTrueFalseOption(checkUnderflow));
			pw.close();
		} catch (IOException e) {
			throw new FileManagementException("Error guardando la configuración del programa.");
		}
	}
	
	private String stringifyTrueFalseOption(boolean value) {
		if (value == true)
			return "SI";
		return "NO";
	}
}
