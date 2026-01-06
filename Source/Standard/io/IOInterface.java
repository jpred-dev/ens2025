package io;

import data.RData;

public interface IOInterface {
	public void printChar(char c);
	public void printStr(String str);
	public void printData(RData d);
	public void printLog(String str);
	public void printlnLog(String str);
	
	public RData readData();
	public char readChar();
	public String readStr();
}
