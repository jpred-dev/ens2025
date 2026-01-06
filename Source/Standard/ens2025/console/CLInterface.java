package ens2025.console;

import java.util.Scanner;

import data.RData;
import data.RWData;
import io.IOInterface;
import settings.Settings;

public class CLInterface implements IOInterface {
	private Scanner input;
	private Settings set;
	
	public CLInterface(Settings set) {
		this.set = set;
		input = new Scanner(System.in);
	}
	
	@Override
	public void printChar(char c) {
		System.out.print(c);
	}

	@Override
	public void printStr(String str) {
		System.out.print(str);
	}

	@Override
	public void printData(RData d) {
		switch(set.getNumericalRepresentation()) {
			case HEX:
				System.out.print(d.getHexString());
				break;
			case DEC:
				System.out.print(d.getDecString());
				break;
			case USDEC:
				System.out.print(d.getUnsignedDecString());
				break;
			default:
				break;
		}
	}

	@Override
	public void printLog(String str) {
		System.err.print(str);
		System.err.flush();
	}

	@Override
	public void printlnLog(String str) {
		System.err.println(str);
		System.err.flush();
	}

	@Override
	public RData readData() {
		return new RWData(readStr().trim().split("\\s")[0]);
	}

	@Override
	public char readChar() {
		String line = readStr();
		
		if (line.isEmpty())
			return '\0';
		
		return line.charAt(0);
	}

	@Override
	public String readStr() {
		return input.nextLine();
	}

}
