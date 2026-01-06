package enums;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum AddressingMode {
	NONE(0, "SIN OPERANDO"), //Sin operando
	LITERAL(1, "INMEDIATO"), //Inmediato
	REGISTER(2, "DIRECTO A REGISTRO"), //Directo a registro
	MEMORY(3, "DIRECTO A MEMORIA"), //Directo a memoria
	INDIRECT(4, "INDIRECTO"), //Indirecto
	RELATIVE_IX(5, "RELATIVO A IX"), //Relativo a IX
	RELATIVE_IY(6, "RELATIVO A IY"), //Relativo a IY
	RELATIVE_PC(7, "RELATIVO A PC"); //Relativo al contador de programa


	private final int id;
	private final String mode;
	
	AddressingMode(int id, String mode) {
		this.id = id;
		this.mode = mode;
	}

	public int getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return mode;
	}
	
	//Mapa y método para obtener el tipo en base al valor numérico asociado.
	private static final Map<Integer, AddressingMode> ID_MAP;
	static {
		Map<Integer, AddressingMode> temp = new HashMap<>();
		for(AddressingMode addr : AddressingMode.values())
			temp.put(addr.id, addr);
		ID_MAP = Collections.unmodifiableMap(temp);
	}
	
	public static final AddressingMode getFromId(int id) {
		return ID_MAP.get(id);
	}

}

