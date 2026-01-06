package controlunit.sim;

import java.util.concurrent.atomic.AtomicBoolean;

import controlunit.architecture.Architecture;
import controlunit.instruction.Instruction;
import controlunit.sim.decoder.Decoder;
import customexception.CustomException;
import data.RWData;
import enums.Flag;
import io.IOInterface;
import settings.Settings;

public class Simulator {

	private Settings set;
	private Architecture arc;
	private Decoder decoder;
	private IOInterface io;
	private AtomicBoolean stopSimulation;

	public Simulator(Settings set, Architecture arc, IOInterface io) {
		this.set = set;
		this.io = io;
		this.arc = arc;
		this.decoder = new Decoder (arc, io, set);
		stopSimulation = new AtomicBoolean(false);
	}
	
	public void runSimulation() {
		Instruction inst;
		RWData PCclone = new RWData(0);
		stopSimulation.set(false);
		
		//Se comprueba si se deben reiniciar a 0 los registros al activar una ejecución mientras el biestable H está activo.
		checkResetRegisters();
		
		while (arc.getFlag(Flag.HALT) == false && stopSimulation.get() == false) {
			PCclone.assign(arc.getProgramCounter());
			try {
				inst = decoder.getInstruction(true);
				inst.run(arc);
			} catch (CustomException ex) {
				ex.setLine(PCclone);
				throw ex;
			}
		}
		
		checkHalt();		
	}
	
	public void debugSimulation() {
		Instruction inst;
		RWData PCclone = new RWData(0);
		stopSimulation.set(false);
		
		//Se comprueba si se deben reiniciar a 0 los registros al activar una ejecución mientras el biestable H está activo.
		checkResetRegisters();
		
		try {
			//Se realiza una primera iteración fuera del bucle con el flag de ignorar breakpoints activo para que el sistema no se quede permanentemente atascado
			//intentando ejecutar la primera instrucción si tiene breakpoint.
			if (arc.getFlag(Flag.HALT) == false) {
				PCclone.assign(arc.getProgramCounter());
				inst = decoder.getInstruction(true);
				inst.run(arc);
			}
			
			while (arc.getFlag(Flag.HALT) == false && stopSimulation.get() == false) {
				PCclone.assign(arc.getProgramCounter());
				inst = decoder.getInstruction(false);
				inst.run(arc);
			}
		} catch (CustomException ex) {
			ex.setLine(PCclone);
			throw ex;
		}
		
		checkHalt();
	}
	
	public void runSimulationOnce() {
		Instruction inst;
		RWData PCclone = new RWData(arc.getProgramCounter());
		
		//Se comprueba si se deben reiniciar a 0 los registros al activar una ejecución mientras el biestable H está activo.
		checkResetRegisters();
		
		try {
			inst = decoder.getInstruction(true);
			inst.run(arc);
			io.printlnLog("Ejecución paso a paso. Se ha detenido la ejecución.");
		} catch (CustomException ex) {
			ex.setLine(PCclone);
			throw ex;
		}
		
		checkHalt();
	}
	
	private void checkHalt() {
		if (arc.getFlag(Flag.HALT) == true)
			io.printlnLog("Fin del programa: Biestable H (HALT) activo.");
	}
	
	private void checkResetRegisters() {
		if (set.getResetRegistersWithNewRun() && arc.getFlag(Flag.HALT) == true)
			arc.resetRegisterBank();
		else
			arc.setFlag(Flag.HALT, false); //Se reinicia el flag de HALT manualmente para que no fuerce detención en cada nueva ejecución si no se reinician registros.
	}
	
	public AtomicBoolean getStopFlag() {
		return stopSimulation;
	}
	
	public void setStopFlag(Boolean value) {
		stopSimulation.set(value);
	}
	
}
