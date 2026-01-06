package ens2025.console;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

import controlunit.ControlUnit;
import controlunit.disassembler.DisassembledInstruction;
import customexception.CustomException;
import customexception.FileManagementException;
import data.RData;
import data.RWData;
import ens2025.console.consoleexception.InvalidArgumentException;
import ens2025.console.consoleexception.InvalidParameterFormatException;
import ens2025.console.consoleexception.InvalidParameterNumberException;
import ens2025.console.consoleexception.InvalidParameterValueException;
import enums.RegisterID;
import io.IOInterface;
import settings.NumericalRepresentation;
import settings.Settings;
import settings.StackGrowth;

public class Menu {
	private ControlUnit cu;
	private IOInterface io;
	private Settings set;
	private boolean tempSettings;
	private Map<Command, Consumer<List<String>>> commandMap;
	private static final String SEPARATOR = "-----------------------------------------------------------------------------";
	private static final String PROMPT_STRING = "ENS2025> ";
	private static final String RECOVERY_NAME = "recovery.dump";
	
	private Thread shutdownHookThread;
	
	
	public Menu(ControlUnit cu, IOInterface io, Settings set) {
		this.cu = cu;
		this.io = io;
		this.set = set;
		
		tempSettings = false;
		
		shutdownHookThread = createHook();
		
		commandMap = new HashMap<>();
		commandMap.put(Command.ASSEMBLE_FILE, this::assembleFile);
		commandMap.put(Command.DISASSEMBLE_INST, this::disassembleInstructions);
		commandMap.put(Command.RUN, this::run);
		commandMap.put(Command.RUN_STEP, this::runStep);
		commandMap.put(Command.RUN_DEBUG, this::runDebug);
		commandMap.put(Command.SET_BREAKPOINT, this::setBreakpoint);
		commandMap.put(Command.PRINT_MEMORY, this::printMemory);
		commandMap.put(Command.WRITE_MEMORY, this::writeToMemory);
		commandMap.put(Command.RESET_MEMORY, this::wipeMemory);
		commandMap.put(Command.PRINT_STACK, this::printStack);
		commandMap.put(Command.LOAD_MEMFILE, this::loadMemoryFromFile);
		commandMap.put(Command.SAVE_MEMFILE, this::saveMemoryToFile);
		commandMap.put(Command.PRINT_REGBANK, this::printRegisterBank);
		commandMap.put(Command.WRITE_REGISTER, this::writeToRegister);
		commandMap.put(Command.HELP, this::printHelp);
		commandMap.put(Command.PRINT_SETTINGS, this::printSettings);
		commandMap.put(Command.SET_STACKINVASION, this::setStackInvasion);
		commandMap.put(Command.SET_CODEINVASION, this::setCodeInvasion);
		commandMap.put(Command.SET_OVERFLOW, this::setOverflow);
		commandMap.put(Command.SET_UNDERFLOW, this::setUnderflow);
		commandMap.put(Command.SET_STACKGROWTH, this::setStackGrowth);
		commandMap.put(Command.SET_CODESTART, this::setCodeStart);
		commandMap.put(Command.SET_CODEEND, this::setCodeEnd);
		commandMap.put(Command.SET_STACKSTART, this::setStackStart);
		commandMap.put(Command.RESET_REGBANK, this::resetRegisterBank);
		commandMap.put(Command.SET_BASE, this::setNumericalBase);
		commandMap.put(Command.QUIT, this::quit);
		
	}
	
	private Thread createHook() {
		return new Thread(() -> {
			io.printlnLog("Ejecución detenida por el usuario.");
			io.printlnLog("Iniciando volcado de memoria y terminando el programa.");
			io.printlnLog("Escribiendo el fichero 'recovery.mem'...");
			try {
				cu.writeMemoryToFile(RECOVERY_NAME);
				io.printlnLog("Fichero '" + RECOVERY_NAME + "' guardado correctamente.");
				io.printlnLog("Puede cargarlo manualmente o invocando el programa con el argumento '-rec'.");
			} catch (CustomException ex) {
				io.printlnLog("Se ha producido un error en el volcado de memoria.");
			};
		});
	}
	
	public void preselection(String[] args) {
		boolean isRecovery = false;
		boolean hasLoad = false;
		boolean hasSettings = false;
		boolean hasRun = false;
		String dumpFileName = null;
		
		//Si no se suministran argumentos, se inicia ejecución normal.
		if (args.length == 0) {
			mainMenu();
		}
		
		//Para 1+ argumentos se interpretan los comandos:
		else {
			for (int i=0; i<args.length; i++) {
				switch(args[i]) {
					case "-a":
						if (isRecovery)
							throw new InvalidArgumentException("El argumento '-a' es incompatible con el argumento '-rec'.");
						if (hasLoad)
							throw new InvalidArgumentException("No se puede asignar más de un modo de carga de ficheros.");

						i++;
						
						if (i == args.length)
							throw new InvalidArgumentException("Se esperaba un argumento con un nombre de fichero válido tras '" + args[i] + "'.");
						
						this.assembleFile(Arrays.asList(args[i]));
						hasLoad = true;
						break;
						
					case "-m":
						if (isRecovery)
							throw new InvalidArgumentException("El argumento '-m' es incompatible con el argumento '-rec'.");
						if (hasLoad)
							throw new InvalidArgumentException("No se puede asignar más de un modo de carga de ficheros.");
						
						i++;
						
						if (i == args.length)
							throw new InvalidArgumentException("Se esperaba un argumento con un nombre de fichero válido tras '" + args[i] + "'.");
						
						this.loadMemoryFromFile(Arrays.asList(args[i]));
						hasLoad = true;
						break;
						
					case "-r":
						if (isRecovery)
							throw new InvalidArgumentException("El argumento '-r' es incompatible con el argumento '-rec'.");
						if (hasLoad)
							throw new InvalidArgumentException("No se puede asignar más de un modo de carga de ficheros.");
						if (hasRun)
							throw new InvalidArgumentException("No se puede asignar más de un modo de ejecución.");
						
						i++;
						
						if (i == args.length)
							throw new InvalidArgumentException("Se esperaba un argumento con un nombre de fichero válido tras '" + args[i] + "'.");
						
						this.assembleFile(Arrays.asList(args[i]));
						hasLoad = true;
						hasRun = true;
						break;
						
					case "-rm":
						if (isRecovery)
							throw new InvalidArgumentException("El argumento '-rm' es incompatible con el argumento '-rec'.");
						if (hasLoad)
							throw new InvalidArgumentException("No se puede asignar más de un modo de carga de ficheros.");
						if (hasRun)
							throw new InvalidArgumentException("No se puede asignar más de un modo de ejecución.");
						
						i++;
						
						if (i == args.length)
							throw new InvalidArgumentException("Se esperaba un argumento con un nombre de fichero válido tras '" + args[i] + "'.");
						
						this.loadMemoryFromFile(Arrays.asList(args[i]));
						hasLoad = true;
						hasRun = true;
						break;
						
					case "-s":
						if (hasSettings)
							throw new InvalidArgumentException("No se puede cargar más de un fichero de configuración.");
						
						i++;
						
						if (i == args.length)
							throw new InvalidArgumentException("Se esperaba un argumento con un nombre de fichero válido tras '" + args[i] + "'.");
						
						try {
							set.loadSettingsFromFile(args[i]);
						} catch (IOException ex) {
							throw new FileManagementException("Error de lectura del fichero de configuración.");
						}
						
						hasSettings = true;
						tempSettings = true;
						break;
						
					case "-d":
						if (isRecovery)
							throw new InvalidArgumentException("El argumento '-d' es incompatible con el argumento '-rec'.");
						if (dumpFileName != null)
							throw new InvalidArgumentException("No se puede cargar más de un fichero de volcado.");
						
						i++;
						
						if (i == args.length)
							throw new InvalidArgumentException("Se esperaba un argumento con un nombre de fichero válido tras '" + args[i] + "'.");
						
						dumpFileName = args[i];
						break;
						
					case "-rec":
						if (dumpFileName != null)
							throw new InvalidArgumentException("El argumento '-rec' es incompatible con el argumento '-d'.");
						
						if (hasRun)
							throw new InvalidArgumentException("El argumento '-rec' es incompatible con los argumentos '-r' y '-rm'.");
						
						if (hasLoad)
							throw new InvalidArgumentException("El argumento '-rec' es incompatible con los argumentos '-a' y '-m'.");
						
						if (isRecovery)
							throw new InvalidArgumentException("El argumento '-rec' está repetido.");
						
						this.loadMemoryFromFile(Arrays.asList(RECOVERY_NAME));
						
						isRecovery = true;
						break;
						
					default:
						throw new InvalidArgumentException("El argumento '" + args[i] + "' no pertenece a ningún comando válido.");
				}
			}
			
			if (hasRun == false && dumpFileName != null)
				throw new InvalidArgumentException("Se ha definido un fichero de salida pero no uno de entrada.");
			
			else if (hasRun && dumpFileName != null) {
				try {
					this.run(Collections.emptyList());
				} catch (NoSuchElementException ex) {
					//No se hace nada, se captura para que no se propague la excepción al hacer Ctrl+C.
				}
				this.saveMemoryToFile(Arrays.asList(dumpFileName));
			}
			
			else if (hasRun)
				try {
					this.run(Collections.emptyList());
				} catch (NoSuchElementException ex) {
					//No se hace nada, se captura para que no se propague la excepción al hacer Ctrl+C.
				}
			
			else
				mainMenu();
		}
	}
	
	
	private void mainMenu() {
		Command com = null;
		String line;
		String[] sub;
		List<String> param = new ArrayList<>();
		
		printSettings(Collections.emptyList());
		printHelp(Collections.emptyList());
		
		
		while (com != Command.QUIT) {
			try {
				io.printLog(PROMPT_STRING);
				line = io.readStr();
				sub = line.trim().split("\\s+");
				//En caso de array vacío (todo espacios en blanco), simplemente se reitera el prompt.
				if (sub.length == 0)
					continue;	
				for (int i=1; i<sub.length; i++)
					param.add(sub[i]);
				com = Command.getFromName(sub[0]);
				if (com == null)
					io.printlnLog("Por favor, introduzca un comando válido. Introduzca \"H\" para ver una lista de comandos.");
				else
					commandMap.get(com).accept(param);
				param.clear();
			} catch (CustomException ex) {
				param.clear();
				io.printlnLog(ex.getMessage());
			} catch (NoSuchElementException ex) {
				//No se hace nada, se captura para que no se propague la excepción al hacer Ctrl+C.
				break;
			}
		}
	}
	
	private String convertDataToRepresentation(RData d) {
		String num;
		
		switch (set.getNumericalRepresentation()) {
			case DEC:
				num = d.getDecString();
				break;
			case USDEC:
				num = d.getUnsignedDecString();
				break;
			case HEX:
				num = d.getHexString();
				break;
			default:
				num = null;
				break;
		}
		
		return num;
	}
	
	private String convertIndexToRepresentation(RData d) {
		String num;
		
		switch (set.getNumericalRepresentation()) {
			case DEC:
			case USDEC:
				num = d.getUnsignedDecString();
				break;
			case HEX:
				num = d.getHexString();
				break;
			default:
				num = null;
				break;
		}
		
		return num;
	}
	
	private int parseInt(String str) {
		if (str.charAt(0) == '-')
			return Integer.parseInt(str, 10);
		
		else if (str.length() > 2 && str.startsWith("0x"))
			return Integer.parseInt(str.substring(2), 16);
		
		else
			return Integer.parseInt(str, 10);
	}
	
	private void assembleFile(List<String> param) {
		if (param.size() != 1)
			throw new InvalidParameterNumberException(1);
		
		cu.assembleFile(param.get(0));
	}
	
	private void disassembleInstructions(List<String> param) {
		if (param.size() != 2)
			throw new InvalidParameterNumberException(2);

		int index;
		int num;
		List<DisassembledInstruction> instList = new ArrayList<>();
		
		//comprobación de validez del tipo de los argumentos.
		try {
			index = parseInt(param.get(0));
			num = parseInt(param.get(1));
		} catch (NumberFormatException ex) {
			throw new InvalidParameterFormatException("ERROR: Los argumentos de este comando deben ser dos números enteros (decimales con/sin signo y hexadecimales sin signo).");
		}
		
		//comprobación de validez del contenido de los argumentos.
		if (index < 0 || index > RWData.MAX_UNSIGNED_VALUE)
			throw new InvalidParameterValueException("ERROR: La dirección de memoria debe tener un valor entre 0 y " + RWData.MAX_UNSIGNED_VALUE + ".");
		
		else if (num <= 0)
			throw new InvalidParameterValueException("ERROR: El número de instrucciones a desensamblar debe ser mayor que 0.");

		else {
			instList = cu.disassembleInstructions(new RWData(index), num);
			instList.forEach((inst) -> {
				if (inst.hasBreakpoint())
					io.printLog("[*] ");
				else
					io.printLog("[ ] ");
				io.printlnLog(inst.toString());
			});
		}
	}
	
	private void run(List<String> param) {
		if (param.size() != 0)
			throw new InvalidParameterNumberException(0);

		Runtime.getRuntime().addShutdownHook(shutdownHookThread);
		io.printlnLog("Iniciando simulación.");
		try {
			cu.runSimulation();
		} catch (CustomException ex) {
			io.printlnLog(ex.getMessage());
		}
		Runtime.getRuntime().removeShutdownHook(shutdownHookThread);
	}
	
	private void runStep(List<String> param) {
		if (param.size() != 0) {
			throw new InvalidParameterNumberException(0);
		}
		
		RData pc = cu.getProgramCounter();
		DisassembledInstruction inst = cu.disassembleInstructions(pc, 1).get(0);
		
		io.printlnLog(inst.toString());
		
		cu.runSimulationOnce();
	}
	
	private void runDebug(List<String> param) {
		if (param.size() != 0)
			throw new InvalidParameterNumberException(0);

		Runtime.getRuntime().addShutdownHook(shutdownHookThread);
		io.printlnLog("Iniciando simulación en modo depuración.");
		try {
			cu.debugSimulation();
		} catch (CustomException ex) {
			io.printlnLog(ex.getMessage());
		}
		Runtime.getRuntime().removeShutdownHook(shutdownHookThread);
	}
	
	private void setBreakpoint(List<String> param) {
		if (param.size() != 1 && param.size() != 2)
			throw new InvalidParameterNumberException(1, 2);

		int index;
		
		//comprobación de que el primer parámetro es un entero:
		try {
			index = parseInt(param.get(0));
		} catch (NumberFormatException ex) {
			throw new InvalidParameterFormatException("ERROR: El primer argumento de este comando debe ser un número entero (decimal con/sin signo y hexadecimal sin signo).");
		}
		
		//comprobación de que el primer parámetro no excede los valores sin signo.
		if (index < 0 || index > RWData.MAX_UNSIGNED_VALUE)
			throw new InvalidParameterValueException("ERROR: La dirección de memoria debe tener un valor entre 0 y " + RWData.MAX_UNSIGNED_VALUE + ".");

		else {
			//Zona de código común: creación del objeto Data con el índice y comprobación del tipo de representación.
			RData ind = new RWData(index);
			String num = convertIndexToRepresentation(ind);
			
			//Bloques de código específicos según número de parámetros.
			//Un parámetro: asignar un valor de breakpoint opuesto al existente.
			if (param.size() == 1) {
				boolean val = cu.hasBreakpoint(ind);
				val = !val;
				cu.setBreakpoint(ind, val);
				
				if (val == true)
					io.printlnLog("El breakpoint en la dirección " + num + " ha sido ACTIVADO.");
				else
					io.printlnLog("El breakpoint en la dirección " + num + " ha sido DESACTIVADO.");
			}
			
			//Dos parámetros: comprobar que el segundo parámetro es un valor verdadero/falso y asignarlo.
			else if (param.size() == 2) {
				switch (param.get(1).toUpperCase()) {
					case "SI":
						cu.setBreakpoint(ind, true);
						io.printlnLog("El breakpoint en la dirección " + num + " ha sido ACTIVADO.");
						break;
					case "NO":
						cu.setBreakpoint(ind, false);
						io.printlnLog("El breakpoint en la dirección " + num + " ha sido DESACTIVADO.");
						break;
					default:
						io.printlnLog("El argumento '" + param.get(1) + "' no es válido.");
				}
			}
		}
	}
	
	private void printMemory(List<String> param) {
		if (param.size() != 2)
			throw new InvalidParameterNumberException(2);

		int index;
		int num;
		
		//comprobación de validez de tipo de los argumentos.
		try {
			index = parseInt(param.get(0));
			num = parseInt(param.get(1));
		} catch (NumberFormatException ex) {
			throw new InvalidParameterFormatException("ERROR: Los argumentos de este comando deben ser dos números enteros (decimales con/sin signo y hexadecimales sin signo).");
		}
		//comprobación de validez de contenido de los argumentos.
		if (index < 0 || index > RWData.MAX_UNSIGNED_VALUE)
			throw new InvalidParameterValueException("ERROR: La dirección de memoria debe tener un valor entre 0 y " + RWData.MAX_UNSIGNED_VALUE + ".");

		else if (num <= 0)
			throw new InvalidParameterValueException("ERROR: El número de posiciones de memoria a mostrar debe ser mayor que 0.");

		else {
			int i = index;
			RWData pos = new RWData(index);
			RData value;
			String str;

			//Si el índice actual supera el límite máximo de memoria, simplemente no se muestran más en vez de dar un error.
			while (i < (index+num) && i < RWData.MAX_UNSIGNED_VALUE) {
				value = cu.readMemory(pos).getImmutableCopy();
				if(cu.hasBreakpoint(pos)) 
					io.printLog("[*]");
				else
					io.printLog("[ ]");
				if(set.getCodeStart().isLessEqual(pos) && set.getCodeEnd().isGreaterEqual(pos)) 
					io.printLog("[C]");
				else
					io.printLog("[ ]");
				if(set.getStackStart().isLessEqual(pos) && cu.getStackPointer().isGreater(pos) ||
					set.getStackStart().isGreaterEqual(pos) && cu.getStackPointer().isLess(pos)) 
					io.printLog("[P] ");
				else if (cu.getStackPointer().isEqual(pos))
					io.printLog("[>] ");
				else
					io.printLog("[ ] ");
				str = convertIndexToRepresentation(pos) + ": " + convertDataToRepresentation(value);
				io.printlnLog(str);
				i++;
				pos.add(1);
			}
		}
	}
	
	private void writeToMemory(List<String> param) {
		if (param.size() != 2)
			throw new InvalidParameterNumberException(2);
		
		int index;
		int value;
		
		//comprobación de validez de tipo de los argumentos.
		try {
			index = parseInt(param.get(0));
			value = parseInt(param.get(1));
		} catch (NumberFormatException ex) {
			throw new InvalidParameterFormatException("ERROR: Los argumentos de este comando deben ser dos números enteros (decimales con/sin signo y hexadecimales sin signo).");
		}
		
		//comprobación de validez de contenido de los argumentos.
		if (index < 0 || index > RWData.MAX_UNSIGNED_VALUE)
			throw new InvalidParameterValueException("ERROR: La dirección de memoria debe tener un valor entre 0 y " + RWData.MAX_UNSIGNED_VALUE + ".");

		//La clase Data convierte internamente un valor con signo en su representación sin signo, por lo que usar enteros con signo negativos no requiere tratamiento adicional.
		else if (value < RWData.MIN_VALUE || value > RWData.MAX_UNSIGNED_VALUE)
			throw new InvalidParameterValueException("ERROR: El valor a introducir en memoria debe estar acotado entre " + RWData.MIN_VALUE + " y " + RWData.MAX_UNSIGNED_VALUE + ".");

		else {
			RData ind = new RWData(index);
			RData val = new RWData(value);
			cu.writeMemory(ind, val);
		}
	}
	
	private void wipeMemory(List<String> param) {
		if (param.size() != 0)
			throw new InvalidParameterNumberException(0);
		
		cu.wipeMemory();
	}
	
	private void printStack(List<String> param) {
		if (param.size() != 1)
			throw new InvalidParameterNumberException(1);

		int num;
		
		//comprobación de validez de tipo de los argumentos.
		try {
			num = parseInt(param.get(0));
		} catch (NumberFormatException ex) {
			throw new InvalidParameterFormatException("ERROR: El argumento de este comando debe ser un número entero (decimal con/sin signo y hexadecimal sin signo).");
		}
		
		RWData pos = cu.getStackPointer().getMutableCopy();
		RData value;
		String str;
		int i = 0;
		
		if (num <= 0)
			throw new InvalidParameterValueException("ERROR: El número de posiciones de pila a mostrar debe ser mayor que 0.");
		
		//Si el crecimiento es negativo, la copia del SP irá aumentando, y terminará cuando:
		// - Se imprima el número deseado de posiciones.
		// - La copia del SP valga más que la posición de inicio de la pila (subdesbordamiento).
		// - La copia del SP sea igual a 0 (desbordamiento).
		if (set.getStackGrowth() == StackGrowth.NEGATIVE) {
			while (i < num && pos.isGreater(set.getStackStart()) == false && pos.isEqual(0) == false) {
				value = cu.readMemory(pos);
				if(cu.hasBreakpoint(pos)) 
					io.printLog("[*]");
				else
					io.printLog("[ ]");
				if(set.getCodeStart().isLessEqual(pos) && set.getCodeEnd().isGreaterEqual(pos)) 
					io.printLog("[C]");
				else
					io.printLog("[ ]");
				if(set.getStackStart().isLessEqual(pos) && cu.getStackPointer().isGreater(pos) ||
					set.getStackStart().isGreaterEqual(pos) && cu.getStackPointer().isLess(pos)) 
					io.printLog("[P] ");
				else if (cu.getStackPointer().isEqual(pos))
					io.printLog("[>] ");
				else
					io.printLog("[ ] ");
				str = convertIndexToRepresentation(pos) + ": " + convertDataToRepresentation(value);
				io.printlnLog(str);
				i++;
				pos.add(1);
			}
		}
		
		//Si el crecimiento es positivo, la copia del SP irá descendiendo, y terminará cuando:
		// - Se imprima el número deseado de posiciones.
		// - La copia del SP valga menos que la posición de inicio de la pila (subdesbordamiento).
		// - La copia del SP sea igual a la última dirección de memoria (desbordamiento).
		else {
			while (i < num && pos.isLess(set.getStackStart()) == false && pos.isEqual(RWData.MAX_UNSIGNED_VALUE) == false) {
				value = cu.readMemory(pos);
				if(cu.hasBreakpoint(pos)) 
					io.printLog("[*]");
				else
					io.printLog("[ ]");
				if(set.getCodeStart().isLessEqual(pos) && set.getCodeEnd().isGreaterEqual(pos)) 
					io.printLog("[C]");
				else
					io.printLog("[ ]");
				if(set.getStackStart().isLessEqual(pos) && cu.getStackPointer().isGreater(pos) ||
					set.getStackStart().isGreaterEqual(pos) && cu.getStackPointer().isLess(pos)) 
					io.printLog("[P] ");
				else if (cu.getStackPointer().isEqual(pos))
					io.printLog("[>] ");
				else
					io.printLog("[ ] ");
				str = convertIndexToRepresentation(pos) + ": " + convertDataToRepresentation(value);
				io.printlnLog(str);
				i++;
				pos.subtract(1);
			}
		}
	}
	
	private void loadMemoryFromFile(List<String> param) {
		if (param.size() != 1)
			throw new InvalidParameterNumberException(1);

		cu.loadMemoryFromFile(param.get(0));
	}
	
	private void saveMemoryToFile(List<String> param) {
		if (param.size() != 1)
			throw new InvalidParameterNumberException(1);

		cu.writeMemoryToFile(param.get(0));
	}
	
	private void printRegisterBank(List<String> param) {
		if (param.size() != 0)
			throw new InvalidParameterNumberException(0);
		
		RWData val = new RWData();
		String str;
		
		for (RegisterID reg : RegisterID.values()) {
			val.assign(cu.readRegister(reg));
			str = reg.toString() + ": " + convertDataToRepresentation(val);
			io.printlnLog(str);
		}
	}
	
	private void writeToRegister(List<String> param) {
		if (param.size() != 2)
			throw new InvalidParameterNumberException(2);
		
		RegisterID reg = RegisterID.getFromName(param.get(0));
		if (reg == null)
			throw new InvalidParameterValueException("ERROR: Identificador de registro no válido.");

		else {
			int num;
			try {
				num = parseInt(param.get(1));
			} catch (NumberFormatException ex) {
				throw new InvalidParameterFormatException("ERROR: El segundo argumento de este comando debe ser un número entero.");
			}
			
			RData data = new RWData(num);
			cu.writeRegister(reg, data);
		}
	}
	
	private void printHelp(List<String> param) {
		if (param.size() != 0 && param.size() != 1)
			throw new InvalidParameterNumberException(0, 1);

		StringBuilder sb;
		//Si no se reciben argumentos, muestra la ayuda simple para todos los comandos.
		if (param.size() == 0) {
			io.printlnLog(SEPARATOR);
			for (Command com : Command.values()) {
				sb = new StringBuilder(com.getName());
				if (com.getParams().isEmpty() == false)
					sb.append(" " + com.getParams());
				sb.append(": " + com.getTooltip());
				io.printlnLog(sb.toString());
			}
			io.printlnLog(SEPARATOR);
		}
		
		//Si se recibe un argumento, se comprueba que se trata de un nombre de comando existente y se muestra la ayuda detallada del mismo.
		else if (param.size() == 1) {
			Command com = Command.getFromName(param.get(0));
			if (com != null) {
				io.printlnLog(SEPARATOR);
				sb = new StringBuilder(com.getName());
				if (com.getParams().isEmpty() == false)
					sb.append(" " + com.getParams() + ":\n");
				io.printlnLog(sb.toString());
				io.printlnLog(com.getAdvancedTooltip());
				io.printlnLog(SEPARATOR);
			}
			else
				io.printlnLog("ERROR: No existe el comando '" + param.get(0) + "'.");
		}
	}
	
	private void printSettings(List<String> param) {
		StringBuilder sb;

		if (param.size() != 0)
			throw new InvalidParameterNumberException(0);
		
		sb = new StringBuilder("Modo de representación numérica: ");
		if (set.getNumericalRepresentation() == NumericalRepresentation.DEC)
			sb.append("DECIMAL SIN SIGNO.");
		else if (set.getNumericalRepresentation() == NumericalRepresentation.USDEC)
			sb.append("DECIMAL CON SIGNO.");
		else if (set.getNumericalRepresentation() == NumericalRepresentation.HEX)
			sb.append("HEXADECIMAL.");
		
		io.printlnLog(sb.toString());
		
		
		sb = new StringBuilder("Modo de crecimiento de la pila: ");
		if (set.getStackGrowth() == StackGrowth.NEGATIVE)
			sb.append("NEGATIVO.\n");
		else if (set.getStackGrowth() == StackGrowth.POSITIVE)
			sb.append("POSITIVO.\n");
		
		io.printlnLog(sb.toString());
		
		
		sb = new StringBuilder("comprobación de invasión del espacio de pila por el PC: ");
		if (set.getCheckPCInvadingStack() == true)
			sb.append("ACTIVADA.\n");
		else
			sb.append("DESACTIVADA.\n");
		
		io.printlnLog(sb.toString());
		
		
		sb = new StringBuilder("comprobación de invasión del espacio de código por el SP: ");
		if (set.getCheckSPInvadingCode() == true)
			sb.append("ACTIVADA.\n");
		else
			sb.append("DESACTIVADA.\n");
		
		io.printlnLog(sb.toString());
		
		
		sb = new StringBuilder("comprobación de overflow de la pila: ");
		if (set.getCheckOverflow() == true)
			sb.append("ACTIVADA.\n");
		else
			sb.append("DESACTIVADA.\n");
		
		io.printlnLog(sb.toString());
		
		
		sb = new StringBuilder("comprobación de underflow de la pila: ");
		if (set.getCheckUnderflow() == true)
			sb.append("ACTIVADA.\n");
		else
			sb.append("DESACTIVADA.\n");
		
		io.printlnLog(sb.toString());
		
		
		sb = new StringBuilder("Reinicio del banco de registros al ejecutar con el biestable H en alto: ");
		if (set.getResetRegistersWithNewRun() == true)
			sb.append("ACTIVADO.\n");
		else
			sb.append("DESACTIVADO.\n");
		
		io.printlnLog(sb.toString());
		
		
		sb = new StringBuilder("");
		switch(set.getNumericalRepresentation()) {
			case DEC:
			case USDEC:
				sb.append("Zona de inicio de código: " + set.getCodeStart().getUnsignedDecString() + "\n");
				sb.append("Zona de fin de código: " + set.getCodeEnd().getUnsignedDecString() + "\n");
				sb.append("Zona de inicio de pila: " + set.getStackStart().getUnsignedDecString() + "\n");
				break;
			case HEX:
				sb.append("Zona de inicio de código: " + set.getCodeStart().getHexString() + "\n");
				sb.append("Zona de fin de código: " + set.getCodeEnd().getHexString() + "\n");
				sb.append("Zona de inicio de pila: " + set.getStackStart().getHexString() + "\n");
				break;
			default:
				break;
		}
		
		io.printlnLog(sb.toString());
	}
	
	private void setStackInvasion(List<String> param) {
		if (param.size() != 0 && param.size() != 1)
			throw new InvalidParameterNumberException(0, 1);
		
		if (param.size() == 0) {
			boolean val = set.getCheckPCInvadingStack();
			val = !val;
			set.setCheckPCInvadingStack(val);
			if (val)
				io.printlnLog("La comprobación de invasión del espacio de pila por el PC está ACTIVADA.");
			else
				io.printlnLog("La comprobación de invasión del espacio de pila por el PC está DESACTIVADA.");
		}
		
		else if (param.size() == 1) {
			switch (param.get(0).toUpperCase()) {
				case "SI":
					set.setCheckPCInvadingStack(true);
					io.printlnLog("La comprobación de invasión del espacio de pila por el PC está ACTIVADA.");
					break;
				case "NO":
					set.setCheckPCInvadingStack(false);
					io.printlnLog("La comprobación de invasión del espacio de pila por el PC está DESACTIVADA.");
					break;
				default:
					io.printlnLog("El argumento '" + param.get(0) + "' no es válido.");
			}
		}
	}
	
	private void setCodeInvasion(List<String> param) {
		if (param.size() != 0 && param.size() != 1)
			throw new InvalidParameterNumberException(0, 1);
		
		if (param.size() == 0) {
			boolean val = set.getCheckSPInvadingCode();
			val = !val;
			set.setCheckSPInvadingCode(val);
			if (val)
				io.printlnLog("La comprobación de invasión del espacio de código por el SP está ACTIVADA.");
			else
				io.printlnLog("La comprobación de invasión del espacio de código por el SP está DESACTIVADA.");
		}
		
		else if (param.size() == 1) {
			switch (param.get(0).toUpperCase()) {
				case "SI":
					set.setCheckSPInvadingCode(true);
					io.printlnLog("La comprobación de invasión del espacio de código por el SP está ACTIVADA.");
					break;
				case "NO":
					set.setCheckSPInvadingCode(false);
					io.printlnLog("La comprobación de invasión del espacio de código por el SP está DESACTIVADA.");
					break;
				default:
					io.printlnLog("El argumento '" + param.get(0) + "' no es válido.");
			}
		}
	}
	
	private void setOverflow(List<String> param) {
		if (param.size() != 0 && param.size() != 1)
			throw new InvalidParameterNumberException(0, 1);
		
		if (param.size() == 0) {
			boolean val = set.getCheckOverflow();
			val = !val;
			set.setCheckOverflow(val);
			if (val)
				io.printlnLog("La comprobación de overflow de la pila está ACTIVADA.");
			else
				io.printlnLog("La comprobación de overflow de la pila está DESACTIVADA.");
		}
		
		else if (param.size() == 1) {
			switch (param.get(0).toUpperCase()) {
				case "SI":
					set.setCheckOverflow(true);
					io.printlnLog("La comprobación de overflow de la pila está ACTIVADA.");
					break;
				case "NO":
					set.setCheckOverflow(false);
					io.printlnLog("La comprobación de overflow de la pila está DESACTIVADA.");
					break;
				default:
					io.printlnLog("El argumento '" + param.get(0) + "' no es válido.");
			}
		}
	}
	
	private void setUnderflow(List<String> param) {
		if (param.size() != 0 && param.size() != 1)
			throw new InvalidParameterNumberException(0, 1);
		
		if (param.size() == 0) {
			boolean val = set.getCheckUnderflow();
			val = !val;
			set.setCheckUnderflow(val);
			if (val)
				io.printlnLog("La comprobación de underflow de la pila está ACTIVADA.");
			else
				io.printlnLog("La comprobación de underflow de la pila está DESACTIVADA.");
		}
		
		else if (param.size() == 1) {
			switch (param.get(0).toUpperCase()) {
				case "SI":
					set.setCheckUnderflow(true);
					io.printlnLog("La comprobación de underflow de la pila está ACTIVADA.");
					break;
				case "NO":
					set.setCheckUnderflow(false);
					io.printlnLog("La comprobación de underflow de la pila está DESACTIVADA.");
					break;
				default:
					io.printlnLog("El argumento '" + param.get(0) + "' no es válido.");
			}
		}
	}
	
	private void setStackGrowth(List<String> param) {
		if (param.size() != 0 && param.size() != 1)
			throw new InvalidParameterNumberException(0, 1);
		
		if (param.size() == 0) {
			StackGrowth val = set.getStackGrowth();
			
			if (val == StackGrowth.NEGATIVE) {
				set.setStackGrowth(StackGrowth.POSITIVE);
				io.printlnLog("El crecimiento de la pila es POSITIVO.");
			}
			else {
				set.setStackGrowth(StackGrowth.NEGATIVE);
				io.printlnLog("El crecimiento de la pila es NEGATIVO.");
			}
		}
		else if (param.size() == 1) {
			switch (param.get(0).toUpperCase()) {
				case "POS":
					set.setStackGrowth(StackGrowth.POSITIVE);
					io.printlnLog("El crecimiento de la pila es POSITIVO.");
					break;
				case "NEG":
					set.setStackGrowth(StackGrowth.NEGATIVE);
					io.printlnLog("El crecimiento de la pila es NEGATIVO.");
					break;
				default:
					io.printlnLog("El argumento '" + param.get(0) + "' no es válido.");
			}
		}
	}
	
	private void setCodeStart(List<String> param) {
		if (param.size() != 1)
			throw new InvalidParameterNumberException(1);
		
		int num;
		
		//comprobación de validez de tipo de los argumentos.
		try {
			num = parseInt(param.get(0));
		} catch (NumberFormatException ex) {
			throw new InvalidParameterFormatException("ERROR: El argumento de este comando debe ser un número entero (decimal con/sin signo y hexadecimal sin signo).");
		}
		
		set.setCodeStart(new RWData(num));
	}
	
	private void setCodeEnd(List<String> param) {
		if (param.size() != 1)
			throw new InvalidParameterNumberException(1);
		
		int num;
		
		//comprobación de validez de tipo de los argumentos.
		try {
			num = parseInt(param.get(0));
		} catch (NumberFormatException ex) {
			throw new InvalidParameterFormatException("ERROR: El argumento de este comando debe ser un número entero (decimal con/sin signo y hexadecimal sin signo).");
		}
		
		set.setCodeEnd(new RWData(num));
	}
	
	private void setStackStart(List<String> param) {
		if (param.size() != 1)
			throw new InvalidParameterNumberException(1);
		
		int num;
		
		//comprobación de validez de tipo de los argumentos.
		try {
			num = parseInt(param.get(0));
		} catch (NumberFormatException ex) {
			throw new InvalidParameterFormatException("ERROR: El argumento de este comando debe ser un número entero (decimal con/sin signo y hexadecimal sin signo).");
		}
		
		set.setStackStart(new RWData(num));
	}
	
	private void resetRegisterBank(List<String> param) {
		if (param.size() != 0 && param.size() != 1)
			throw new InvalidParameterNumberException(0, 1);
		
		if (param.size() == 0) {
			boolean val = set.getResetRegistersWithNewRun();
			val = !val;
			set.setCheckSPInvadingCode(val);
			if (val)
				io.printlnLog("El reinicio del banco de registros al iniciar una ejecución con el biestable H en alto está ACTIVADO.");
			else
				io.printlnLog("El reinicio del banco de registros al iniciar una ejecución con el biestable H en alto está DESACTIVADO.");
		}
		
		else if (param.size() == 1) {
			switch (param.get(0).toUpperCase()) {
				case "SI":
					set.setResetRegistersWithNewRun(true);
					io.printlnLog("El reinicio del banco de registros al iniciar una ejecución con el biestable H en alto está ACTIVADO.");
					break;
				case "NO":
					set.setResetRegistersWithNewRun(false);
					io.printlnLog("El reinicio del banco de registros al iniciar una ejecución con el biestable H en alto está DESACTIVADO.");
					break;
				default:
					io.printlnLog("El argumento '" + param.get(0) + "' no es válido.");
			}
		}
	}
	
	private void setNumericalBase(List<String> param) {
		if (param.size() != 1)
			throw new InvalidParameterNumberException(1);
		
		switch (param.get(0).toUpperCase()) {
			case "HEX":
				set.setNumericalRepresentation(NumericalRepresentation.HEX);
				io.printlnLog("La base numérica de representación es ahora HEXADECIMAL.");
				break;
			case "DEC":
				set.setNumericalRepresentation(NumericalRepresentation.DEC);
				io.printlnLog("La base numérica de representación es ahora DECIMAL CON SIGNO (los índices se siguen representando sin signo).");
				break;
			case "USDEC":
				set.setNumericalRepresentation(NumericalRepresentation.USDEC);
				io.printlnLog("La base numérica de representación es ahora DECIMAL SIN SIGNO.");
				break;
			default:
				io.printlnLog("El argumento '" + param.get(0) + "' no es válido.");
		}
	}
	
	private void quit(List<String> param) {
		if (param.size() != 0)
			throw new InvalidParameterNumberException(0);
		
		if (tempSettings == false)
			try {
				set.saveSettingsToFile();
			} catch (CustomException ex) {
				io.printlnLog(ex.getMessage());
			}
		
		io.printlnLog("Programa terminado.");
	}
}
