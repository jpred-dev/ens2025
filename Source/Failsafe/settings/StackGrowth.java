package settings;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum StackGrowth {
	POSITIVE("POSITIVO", "Positivo"),
	NEGATIVE("NEGATIVO", "Negativo");
	
	private final String name;
	private final String description;
	
	StackGrowth(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	private static final Map<String, StackGrowth> NAME_MAP;
	static {
		Map<String, StackGrowth> temp = new HashMap<>();
		for (StackGrowth sg : StackGrowth.values())
			temp.put(sg.name, sg);
		NAME_MAP = Collections.unmodifiableMap(temp);
	}
	public static StackGrowth getFromName(String name) {
		return NAME_MAP.get(name.toUpperCase());
	}
	
	
	private static final Map<String, StackGrowth> DESCRIPTION_MAP;
	static {
		Map<String, StackGrowth> temp = new HashMap<>();
		for (StackGrowth sg : StackGrowth.values())
			temp.put(sg.description, sg);
		DESCRIPTION_MAP = Collections.unmodifiableMap(temp);
	}
	public static StackGrowth getFromDescription(String description) {
		return DESCRIPTION_MAP.get(description);
	}
}
