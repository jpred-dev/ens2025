package controlunit.assembler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum DirectiveType {
	END("END"),
	DATA("DATA"),
	ORG("ORG"),
	EQU("EQU"),
	RES("RES");
	
	private final String name;
	
	DirectiveType(String name) {
		this.name = name;
	}
	
	private static final Map<String, DirectiveType> NAME_MAP;
	static {
		Map<String, DirectiveType> temp = new HashMap<>();
		for (DirectiveType dir : DirectiveType.values())
			temp.put(dir.name, dir);
		NAME_MAP = Collections.unmodifiableMap(temp);
	}
	
	public static DirectiveType getFromName(String name) {
		return NAME_MAP.get(name);
	}
}
