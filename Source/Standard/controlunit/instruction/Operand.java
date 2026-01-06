package controlunit.instruction;

import data.RData;
import enums.AddressingMode;
import enums.RegisterID;
import settings.Settings;

public final class Operand {
	private AddressingMode addr;
	private RegisterID reg;
	private RData data;
	private Settings set;
	
	public Operand(AddressingMode addr, RData data, Settings set) {
		this.addr = addr;
		this.data = data.getImmutableCopy();
		reg = null;
		this.set = set;
	}
	
	public Operand(AddressingMode addr, RegisterID reg, Settings set) {
		this.addr = addr;
		this.reg = reg;
		data = null;
		this.set = set;
	}
	
	public Operand(AddressingMode addr, RegisterID reg, RData data, Settings set) {
		this.addr = addr;
		this.reg = reg;
		this.data = data.getImmutableCopy();
		this.set = set;
	}

	public AddressingMode getAddressingMode() {
		return addr;
	}

	public RegisterID getRegister() {
		return reg;
	}
	
	public RData getData() {
		return data.getImmutableCopy();
	}
	
	@Override
	public String toString() {
		String str;
		
		switch(addr) {
			case LITERAL:
				str = "#" + dataString(false);
				break;
			case MEMORY:
				str = "/" + dataString(true);
				break;
			case REGISTER:
				str = "." + reg.toString();
				break;
			case INDIRECT:
				str = "[." + reg.toString() + "]";
				break;
			case RELATIVE_IX:
			case RELATIVE_IY:
				str = "#" + dataOffsetString() + "[." + reg.toString() + "]";
				break;
			case RELATIVE_PC:
				str = "$" + dataOffsetString();
				break;
			default:
				str = "";
				break;
		}
		
		return str;
	}
	
	private String dataString(boolean isAddress) {
		String str;
		
		if (isAddress == false)
			switch(set.getNumericalRepresentation()) {
				case DEC:
					str = data.getDecString();
					break;
				case USDEC:
					str = data.getUnsignedDecString();
					break;
				case HEX:
					str = data.getHexString();
					break;
				default:
					str = "";
					break;
			}
		else
			switch(set.getNumericalRepresentation()) {
				case DEC:
				case USDEC:
					str = data.getUnsignedDecString();
					break;
				case HEX:
					str = data.getHexString();
					break;
				default:
					str = "";
					break;
		}
		
		return str;
	}
	
	private String dataOffsetString() {
		String str;
		

		switch(set.getNumericalRepresentation()) {
			case DEC:
			case USDEC:
				str = data.getHalfDecString();
				break;
			case HEX:
				str = data.getHalfHexString();
				break;
			default:
				str = "";
				break;
		}
		
		return str;
	}
}
