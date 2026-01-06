package enums;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum RegisterID {
	R0("R0", 0), R1("R1", 1), R2("R2", 2), R3("R3", 3), R4("R4", 4),
	R5("R5", 5), R6("R6", 6), R7("R7", 7), R8("R8", 8), R9("R9", 9),
	A("A", 10),
	SR("SR", 11),
	IX("IX", 12), IY("IY", 13),
	SP("SP", 14),
	PC("PC", 15);
	
	private final String name;
	private final int id;
	
	RegisterID(String name, int id){
		this.name = name;
		this.id = id;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public int getId() {
		return id;
	}
	
	//Mapa y método para obtener el tipo en base al valor numérico asociado.
	private static final Map<Integer, RegisterID> ID_MAP;
	static {
		Map<Integer, RegisterID> temp = new HashMap<>();
		for(RegisterID reg : RegisterID.values()) {
			temp.put(reg.id, reg);
		}
		ID_MAP = Collections.unmodifiableMap(temp);
	}
	
	public static final RegisterID getFromId(int id) {
		return ID_MAP.get(id);
	}
	
	//Mapa y método para obtener el tipo en base al nombre asociado.
	private static final Map<String, RegisterID> NAME_MAP;
	static {
		Map<String, RegisterID> temp2 = new HashMap<>();
		for(RegisterID reg : RegisterID.values())
			temp2.put(reg.name, reg);
		NAME_MAP = Collections.unmodifiableMap(temp2);
	}
	
	public static final RegisterID getFromName(String name) {
		return NAME_MAP.get(name.toUpperCase());
	}
}
