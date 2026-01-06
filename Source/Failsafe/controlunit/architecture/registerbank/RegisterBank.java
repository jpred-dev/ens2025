package controlunit.architecture.registerbank;

import java.util.HashMap;
import java.util.Map;

import data.RData;
import data.RWData;
import enums.Flag;
import enums.RegisterID;

public class RegisterBank {
	private Map<RegisterID, RWData> registers;
	private Map<Flag, Boolean> flags;
	
	public RegisterBank() {
		registers = new HashMap<RegisterID, RWData>();
		flags = new HashMap<Flag, Boolean>();
		
		for (RegisterID reg : RegisterID.values())
			registers.put(reg, new RWData());
		
		for (Flag f : Flag.values())
			flags.put(f, false);
	}
	
	public RData readRegister(RegisterID reg) {		
		return registers.get(reg).getImmutableCopy();
	}
	
	public void writeRegister(RegisterID reg, RData value) {
		registers.get(reg).assign(value);
		if (reg == RegisterID.SR)
			updateFlags();
	}
	
	private void updateFlags() {
		//Se obtiene el registro SR y se usa para reasignar el valor al temporal, pero no se opera con él directamente.
		RData sr = readRegister(RegisterID.SR);
		RWData temp = new RWData();
		
		for (Flag f : Flag.values()) {
			temp.assign(sr);
			temp.bitAnd(f.getMask());
			if (temp.isEqual(0))
				flags.put(f, false);
			else
				flags.put(f, true);
		}
	}

	public void setFlag(Flag f, boolean status) {
		flags.put(f, status);
		RWData mask = f.getMask().getMutableCopy();
		RWData temp = readRegister(RegisterID.SR).getMutableCopy();
		
		if (status == true) {
			//Si se busca activar el flag, se aplica su máscara correspondiente al registro SR mediante un OR.
			temp.bitOr(mask);
		}
		else {
			//Si se busca desactivar el flag, primero debe invertirse la máscara y después aplicarla al registro mediante un AND.
			mask.bitInvert();
			temp.bitAnd(mask);
		}
		
		registers.get(RegisterID.SR).assign(temp);
	}

	public boolean getFlag(Flag f) {
		return flags.get(f);
	}
}
