package controlunit.assembler.util;

import data.RData;
import enums.AddressingMode;
import enums.RegisterID;

public class OperandBlueprint {
	private AddressingMode addr;
	private RegisterID reg;
	private RData data;
	private String reference;
	
	public OperandBlueprint(AddressingMode addr, RData data) {
		this.addr = addr;
		this.reg = null;
		this.data = data.getImmutableCopy();
		this.reference = null;
	}
	
	public OperandBlueprint(AddressingMode addr, RegisterID reg) {
		this.addr = addr;
		this.reg = reg;
		data = null;
		this.reference = null;
	}
	
	public OperandBlueprint(AddressingMode addr, RegisterID reg, RData data) {
		this.addr = addr;
		this.reg = reg;
		this.data = data.getImmutableCopy();
		this.reference = null;
	}
	
	public void markAsReference(String rName) {
		reference = rName;
	}
	
	public AddressingMode getAddressingMode() {
		return addr;
	}
	
	public RegisterID getRegister() {
		return reg;
	}
	
	public RData getData() {
		return data;
	}
	
	public String getReference() {
		return reference;
	}
}
