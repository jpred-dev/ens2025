package settings;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum NumericalRepresentation {
	DEC("DEC", "Decimal con signo"),
	USDEC("USDEC", "Decimal sin signo"),
	HEX("HEX", "Hexadecimal");
	
	private final String name;
	private final String description;
	
	NumericalRepresentation(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	private static final Map<String, NumericalRepresentation> NAME_MAP;
	static {
		Map<String, NumericalRepresentation> temp = new HashMap<>();
		for (NumericalRepresentation nr : NumericalRepresentation.values())
			temp.put(nr.name, nr);
		NAME_MAP = Collections.unmodifiableMap(temp);
	}
	public static final NumericalRepresentation getFromName(String name) {
		return NAME_MAP.get(name.toUpperCase());
	}
	
	private static final Map<String, NumericalRepresentation> DESCRIPTION_MAP;
	static {
		Map<String, NumericalRepresentation> temp = new HashMap<>();
		for (NumericalRepresentation nr : NumericalRepresentation.values())
			temp.put(nr.description, nr);
		DESCRIPTION_MAP = Collections.unmodifiableMap(temp);
	}
	public static final NumericalRepresentation getFromDescription(String description) {
		return DESCRIPTION_MAP.get(description);
	}
}
