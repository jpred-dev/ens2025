package controlunit.disassembler;

import java.util.ArrayList;
import java.util.List;

import controlunit.instruction.Operand;
import data.RData;
import data.RWData;
import enums.InstructionType;
import settings.Settings;

public class DisassembledInstruction {
	private RData address;
	private Settings set;
	private List<Operand> op;
	private int size;
	private InstructionType iType;
	private boolean breakpoint;
	private boolean isFaulty = false;
	private static final String WRONG_INSTRUCTION = "INSTRUCCIÓN NO VÁLIDA";
	
	public DisassembledInstruction(RData address, InstructionType iType, List<Operand> op, int size, Settings set, boolean hasBreakpoint) {
		this.address = new RWData(address);
		this.iType = iType;
		this.op = op;
		this.set = set;
		this.breakpoint = hasBreakpoint;
		this.size = size;
	}
	
	public DisassembledInstruction(RData address, boolean hasBreakpoint) {
		this.isFaulty = true;
		this.address = new RWData(address);
		this.breakpoint = hasBreakpoint;
		this.op = new ArrayList<>();
		this.size = 1;
	}
	
	@Override
	public String toString() {
		return getAddressString() + ": " + getInstructionString();
	}
	
	public String getInstructionString() {
		StringBuilder sb;
		
		if (isFaulty == true)
			return WRONG_INSTRUCTION;
		
		sb = new StringBuilder(iType.toString());
		switch (op.size()) {
			case 1:
				sb.append(" ");
				sb.append(op.get(0).toString());
				break;
			case 2:
				sb.append(" ");
				sb.append(op.get(0).toString());
				sb.append(", ");
				sb.append(op.get(1).toString());
				break;
			default:
				break;
		}
		
		return sb.toString();
	}
	
	public String getAddressString() {
		String str;
		
		switch(set.getNumericalRepresentation()) {
			case DEC:
			case USDEC:
				str = address.getUnsignedDecString();
				break;
			case HEX:
				str = address.getHexString();
				break;
			default:
				str = "";
				break;
		}
		
		return str;
	}
	
	public RData getAddress() {
		return address.getImmutableCopy();
	}
	
	public boolean hasBreakpoint() {
		return breakpoint;
	}

	public int getSize() {
		return size;
	}
}
