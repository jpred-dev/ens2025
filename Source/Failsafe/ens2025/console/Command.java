package ens2025.console;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum Command {
	ASSEMBLE_FILE("ENS", "<f>", "Ensamblar fichero", "Ensambla el fichero de nombre 'f' y carga en la memoria el contenido de las instrucciones ensambladas."),
	DISASSEMBLE_INST("DES", "<dir> <num>", "Desensamblar instrucciones", "Desensambla 'num' instrucciones a partir de la dirección de memoria 'dir'."),
	RUN("RUN", "", "Simular", "Ejecuta el código con normalidad."),
	RUN_STEP("PASO", "", "Simular paso a paso", "Muestra la instrucción apuntada por el PC y la ejecuta."),
	RUN_DEBUG("DEBUG", "", "Depurar", "Ejecuta el código hasta encontrar un punto de ruptura."),
	SET_BREAKPOINT("BP", "<dir> [val]", "Establecer punto de ruptura", "Si se invoca sin segundo parámetro, añade un punto de ruptura en la dirección 'dir' si no había uno,"
			+ " y lo elimina si ya existía. Si se invoca con segundo argumento, fuerza ese estado. Los valores pueden ser:"
			+ "\n- SI: Introduce el punto de ruptura."
			+ "\n- NO: Elimina el punto de ruptura."),
	PRINT_MEMORY("LEERMEM", "<dir> <num>", "Leer posiciones de memoria", "Muestra 'num' posiciones de memoria a partir de la dirección de memoria 'dir'."
			+ "\nSi el número de posiciones a mostrar es mayor que el número de posiciones restantes hasta el final de la memoria, se mostrarán solamente las posiciones disponibles."),
	WRITE_MEMORY("ESCMEM", "<dir> <val>", "Escribir en memoria", "Escribe el valor 'val' en la dirección de memoria 'dir'."),
	RESET_MEMORY("RESMEM", "", "Reiniciar memoria", "Restablece a 0 el valor almacenado en todas las posiciones de memoria."),
	PRINT_STACK("LEERPILA", "<num>", "Leer posiciones de memoria", "Muestra 'num' posiciones de memoria a partir de la dirección de memoria del puntero de pila, empezando por la cima."
			+ "\nSi el número de posiciones a mostrar es mayor que el número de elemento restantes hasta el final de la pila, o si se produce desbordamiento, se mostrarán solamente las posiciones disponibles."),
	LOAD_MEMFILE("CARGAR", "<f>", "Cargar imagen de memoria desde fichero", "Carga el estado del procesador (memoria, registros, zonas de código y pila) contenida en el fichero de nombre 'f'."
			+ "\nCompatible con ficheros de memoria generados por ENS2001, pero solamente contienen la memoria."),
	SAVE_MEMFILE("GUARDAR", "<f>", "Guardar imagen de memoria en fichero", "Guarda el estado actual del procesador (memoria, registros, zonas de código y pila) en el fichero de nombre 'f'."
			+ "\nEstos ficheros pueden ser cargados por ENS2001, pero solamente cargará el estado de la memoria."),
	PRINT_REGBANK("BANCO", "", "Mostrar banco de registros", "Imprime por pantalla los valores de todos los registros."),
	WRITE_REGISTER("REGESC", "<reg> <val>", "Escribir en registro", "Inserta el valor 'val' en el registro identificado por 'reg'."),
	HELP("H", "[com]", "Mostrar ayuda general/específica", "Si se invoca sin parámetros, muestra la ayuda simplificada de todos los comandos. Si se invoca usando el comando 'com' como parámetro, muestra"
			+ "la ayuda avanzada para ese comando específico."),
	PRINT_SETTINGS("CONF", "", "Mostrar configuración", "Imprime por pantalla los valores actuales de configuración del sistema."),
	SET_STACKINVASION("INVPILA", "[val]", "Cambiar la comprobación de invasión de pila", "Modifica la comprobación de invasión de la zona de pila por parte del PC."
			+ "\nSi se invoca sin parámetros, establece como nuevo valor el opuesto al actual."
			+ "\nSi se invoca con un parámetro, fuerza ese estado. Los valores pueden ser:"
			+ "\n- SI: Activa la comprobación."
			+ "\n- NO: Desactiva la comprobación."),
	SET_CODEINVASION("INVCODE", "[val]", "Cambiar la comprobación de invasión de código", "Modifica la comprobación de invasión de la zona de código por parte del SP."
			+ "\nSi se invoca sin parámetros, establece como nuevo valor el opuesto al actual."
			+ "\nSi se invoca con un parámetro, fuerza ese estado. Los valores pueden ser:"
			+ "\n- SI: Activa la comprobación."
			+ "\n- NO: Desactiva la comprobación."),
	SET_STACKGROWTH("DIRPILA", "[val]", "Cambiar la dirección de crecimiento de pila", "Modifica la dirección hacia la que se mueve el SP cuando se introduce un elemento en la pila."
			+ "\nSi se invoca sin parámetros, establece como nuevo valor el opuesto al actual."
			+ "\nSi se invoca con un parámetro, fuerza ese estado. Los valores pueden ser:"
			+ "\n- POS: Crecimiento positivo (\"hacia abajo\")."
			+ "\n- NEG: Crecimiento negativo (\"hacia arriba\")."),
	SET_OVERFLOW("OVERFLOW", "[val]", "Cambiar la comprobación de overflow", "Modifica la comprobación de desbordamiento de la pila."
			+ "\nSi se invoca sin parámetros, establece como nuevo valor el opuesto al actual."
			+ "\nSi se invoca con un parámetro, fuerza ese estado. Los valores pueden ser:"
			+ "\n- SI: Activa la comprobación."
			+ "\n- NO: Desactiva la comprobación."),
	SET_UNDERFLOW("UNDERFLOW", "[val]", "Cambiar la comprobación de underflow", "Modifica la comprobación de subdesbordamiento de la pila."
			+ "\nSi se invoca sin parámetros, establece como nuevo valor el opuesto al actual."
			+ "\nSi se invoca con un parámetro, fuerza ese estado. Los valores pueden ser:"
			+ "\n- SI: Activa la comprobación."
			+ "\n- NO: Desactiva la comprobación."),
	SET_CODESTART("SETINIC", "<dir>", "Modificar el inicio del código", "Cambia el valor de la dirección inicial de la zona de código por la suministrada en 'dir'."),
	SET_CODEEND("SETFINC", "<dir>", "Modificar el final del código", "Cambia el valor de la dirección final de la zona de código por la suministrada en 'dir'."),
	SET_STACKSTART("SETINIP", "<dir>", "Modificar el inicio de la pila", "Cambia el valor de la dirección inicial de la zona de pila por la suministrada en 'dir'."),
	RESET_REGBANK("CONFREG", "[val]", "Cambiar la comprobación de reinicio de banco de registros", "Modifica la comprobación de reinicio del banco de registros al recibir una orden de simulación "
			+ "mientras el biestable H está en alto."
			+ "\nSi se invoca sin parámetros, establece como nuevo valor el opuesto al actual."
			+ "\nSi se invoca con un parámetro, fuerza ese estado. Los valores pueden ser:"
			+ "\n- SI: Activa la comprobación."
			+ "\n- NO: Desactiva la comprobación."),
	SET_BASE("BASE", "<val>", "Cambiar la base de representación numérica", "Modifica la base numérica en la que se representan los valores."
			+ "\nLos posibles valores son:"
			+ "\n- HEX: Hexadecimal."
			+ "\n- DEC: Decimal con signo."
			+ "\n- USDEC: Decimal sin signo."
			+ "\nAlgunos elementos (direcciones de memoria, índices...) mostrarán su valor decimal sin signo incluso si la representación es decimal con signo."),
	QUIT("X", "", "Salir del programa", "Guarda el estado actual de la configuración del sistema en el fichero pertinente y termina la ejecución.");
	
	Command(String name, String params, String tooltip, String advancedTooltip) {
		this.name = name;
		this.tooltip = tooltip;
		this.params = params;
		this.advancedTooltip = advancedTooltip;
	}
	
	private final String name;
	private final String params;
	private final String tooltip;
	private final String advancedTooltip;
	
	public String getName() {
		return name;
	}	
	
	public String getParams() {
		return params;
	}
	
	public String getTooltip() {
		return tooltip;
	}
	
	public String getAdvancedTooltip() {
		return advancedTooltip;
	}

	//Mapa y método para obtener el tipo de comando en base a su nombre.
	private static final Map<String, Command> NAME_MAP;
	static {
		Map<String, Command> temp = new HashMap<>();
		for (Command c : Command.values())
			temp.put(c.name, c);
		NAME_MAP = Collections.unmodifiableMap(temp);
	}
	
	public static Command getFromName(String name) {
		return NAME_MAP.get(name.toUpperCase());
	}
	
}
