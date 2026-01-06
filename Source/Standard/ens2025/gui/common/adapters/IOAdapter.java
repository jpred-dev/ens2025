package ens2025.gui.common.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import data.RData;
import data.RWData;
import io.IOInterface;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class IOAdapter implements IOInterface {
	private StringProperty logProperty;
	private StringProperty consoleOutputProperty;
	private StringProperty consoleInputProperty;
	private BooleanProperty promptProperty;
	private SettingsAdapter sa;
	private CountDownLatch inputOpeningLatch = null;
	private CountDownLatch inputClosingLatch = null;
	
	private StringProperty textDump;
	
	private static final int MAX_STRING_CHUNK_SIZE = 10_000;
	private static final int THROTTLING_DELAY = 1;
	
	
	public IOAdapter(SettingsAdapter sa) {
		this.sa = sa;
		logProperty = new SimpleStringProperty();
		consoleOutputProperty = new SimpleStringProperty();
		consoleInputProperty = new SimpleStringProperty();
		promptProperty = new SimpleBooleanProperty(false);
		textDump = new SimpleStringProperty("");
		
		setBehavior();
	}
	
	private void setBehavior() {
		consoleInputProperty.addListener((observable, oldVal, newVal) -> {
			if (inputOpeningLatch != null) {
				inputOpeningLatch.countDown();
			}
		});
	}

	@Override
	public void printChar(char c) {
		try {
			Thread.sleep(THROTTLING_DELAY);
		} catch (InterruptedException e) { }
		
		Platform.runLater(() -> {
			consoleOutputProperty.set(String.valueOf(c));
		});
	}

	@Override
	public void printStr(String str) {
		List<String> chunkList = new ArrayList<>();
		
		while (str.length() > MAX_STRING_CHUNK_SIZE) {
			chunkList.add(new String(str.substring(0, MAX_STRING_CHUNK_SIZE)));
			str = str.substring(MAX_STRING_CHUNK_SIZE);
		}

		chunkList.add(str);
		
		for (int i=0; i<chunkList.size(); i++) {
			String chunk = chunkList.get(i);
			try {
				Thread.sleep(THROTTLING_DELAY);
			} catch (InterruptedException e) { }
			
			Platform.runLater(() -> {
				consoleOutputProperty.set(chunk);
			});
		}
	}

	@Override
	public void printData(RData d) {
		try {
			Thread.sleep(THROTTLING_DELAY);
		} catch (InterruptedException e) { }
		
		Platform.runLater(() -> {
			switch(sa.getNumericalRepresentation()) {
				case HEX:
					consoleOutputProperty.set(d.getHexString());
					break;
				case DEC:
					consoleOutputProperty.set(d.getDecString());
					break;
				case USDEC:
					consoleOutputProperty.set(d.getUnsignedDecString());
					break;
				default:
					break;
			}
		});
	}

	@Override
	public void printLog(String str) {
		Platform.runLater(() -> {
			logProperty.set(str);
			logProperty.set("");
		});
	}

	@Override
	public void printlnLog(String str) {
		Platform.runLater(() -> {
			logProperty.set(str + "\n");
			logProperty.set("");
		});
	}

	@Override
	public RData readData() {
		return new RWData(readStr().trim().split("\\s")[0]);
	}

	@Override
	public char readChar() {
		String str = readStr();
		
		if (str.isEmpty())
			return '\0';
		
		return str.charAt(0);
	}

	@Override
	public String readStr() {
		String result = "";
		inputOpeningLatch = new CountDownLatch(1);
		inputClosingLatch = new CountDownLatch(1);
		
		Platform.runLater(() -> {
			promptProperty.set(true);
		});
		
		try {
			inputOpeningLatch.await();
			result = consoleInputProperty.get();
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			result = "";
		}
		
		inputOpeningLatch = null;
		
		Platform.runLater(() -> {
			consoleInputProperty.set("");
			promptProperty.set(false);
			inputClosingLatch.countDown();
		});
		
		try {
			inputClosingLatch.await();
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			result = "";
		}
		
		inputClosingLatch = null;
		
		return result;
	}
	
	public StringProperty getLogProperty() {
		return logProperty;
	}
	
	public StringProperty getConsoleOutputProperty() {
		return consoleOutputProperty;
	}
	
	public StringProperty getConsoleInputProperty() {
		return consoleInputProperty;
	}
	
	public BooleanProperty getPromptProperty() {
		return promptProperty;
	}
	
	public StringProperty getTextDumpProperty() {
		return textDump;
	}
}
