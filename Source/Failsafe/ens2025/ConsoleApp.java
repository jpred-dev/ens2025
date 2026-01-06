package ens2025;

import controlunit.ControlUnit;
import customexception.CustomException;
import ens2025.console.CLInterface;
import ens2025.console.Menu;
import ens2025.console.consoleexception.ConsoleException;
import io.IOInterface;
import settings.Settings;

public class ConsoleApp {

	public static void main(String[] args) {
		Settings set = new Settings();
		IOInterface io = new CLInterface(set);
		ControlUnit cu;
		Menu menu;

		try {
			set.loadSettingsSequence();
		} catch (CustomException ex) {
			io.printlnLog(ex.getMessage());
		}
		
		cu = new ControlUnit(set, io);
		menu = new Menu(cu, io, set);
		try {
			menu.preselection(args);
		} catch (ConsoleException ex) {
			io.printlnLog(ex.getMessage());
		} catch (CustomException ex) {
			io.printlnLog(ex.getMessage());
		}
	}
}
