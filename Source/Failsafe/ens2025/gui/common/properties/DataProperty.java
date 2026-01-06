package ens2025.gui.common.properties;

import data.RData;
import data.RWData;
import ens2025.gui.common.adapters.SettingsAdapter;
import javafx.beans.property.SimpleStringProperty;
import settings.NumericalRepresentation;

public class DataProperty extends SimpleStringProperty {
	private RWData data;
	private final boolean isAddress;
	private SettingsAdapter sa;
	
	public DataProperty(RData data, SettingsAdapter sa, boolean isAddress) {
		this.sa = sa;
		this.data = new RWData(data);
		this.isAddress = isAddress;
		
		//asignación de valor inicial a la propiedad.
		set(data);
	}
	
	@Override
	public void set(String str) {
		NumericalRepresentation nr = sa.getNumericalRepresentation();
		
		//Se intenta asignar y, si se genera excepción, se captura en el método llamante y se decide el comportamiento.
		data.assign(str);
		

		//Si no salta excepción, se asigna el string a la propiedad inherente según el modo de representación vigente.
		switch(nr) {
			case HEX:
				super.set(data.getHexString());
				break;
			case DEC:
				if (isAddress)
					super.set(data.getUnsignedDecString());
				else
					super.set(data.getDecString());
				break;
			case USDEC:
				super.set(data.getUnsignedDecString());
				break;
			default:
				break;
		}
	}
	
	public void set(RData data) {
		NumericalRepresentation nr = sa.getNumericalRepresentation();
		
		this.data.assign(data);
		
		switch(nr) {
			case HEX:
				this.set(data.getHexString());
				break;
			case DEC:
				if (isAddress)
					this.set(data.getUnsignedDecString());
				else
					this.set(data.getDecString());
				break;
			case USDEC:
				this.set(data.getUnsignedDecString());
				break;
			default:
				break;
		}
	}
	
	public void set(DataProperty data) {
		set(data.getData());
	}
	
	public RData getData() {
		return data.getImmutableCopy();
	}
	
}
