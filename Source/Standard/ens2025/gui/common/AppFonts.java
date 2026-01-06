package ens2025.gui.common;

import java.io.InputStream;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AppFonts {
	private static final int ICON_FONT_SIZE = 18;
	private static final int VALUE_FONT_SIZE = 12;
	private static final int STANDARD_FONT_SIZE = 12;
	private static final int STANDARD_BOLD_FONT_SIZE = 13;

	public static final Font valueFont;
	public static final Font stdFont;
	public static final Font stdFontBold;
	
	static {
		Font loadedValueFont = null;
		try (InputStream fontStream = AppFonts.class.getResourceAsStream("/ens2025/gui/resources/fonts/courier-prime.regular.ttf")) {
			if (fontStream != null) {
				loadedValueFont = Font.loadFont(fontStream, VALUE_FONT_SIZE);
			} else {
				loadedValueFont = Font.loadFont(Font.getDefault().getFamily(), Font.getDefault().getSize());
			}
		} catch (Exception e) {
			loadedValueFont = Font.loadFont(Font.getDefault().getFamily(), Font.getDefault().getSize());
		}
		
		valueFont = loadedValueFont;
	}
	
	static {
		Font loadedStdFont = null;
		try (InputStream fontStream = AppFonts.class.getResourceAsStream("/ens2025/gui/resources/fonts/OpenSans-Regular.ttf")) {
			if (fontStream != null) {
				loadedStdFont = Font.loadFont(fontStream, STANDARD_FONT_SIZE);
			} else {
				loadedStdFont = Font.loadFont(Font.getDefault().getFamily(), Font.getDefault().getSize());
			}
		} catch (Exception e) {
			loadedStdFont = Font.loadFont(Font.getDefault().getFamily(), Font.getDefault().getSize());
		}
		
		stdFont = loadedStdFont;
	}
	
	static {
		Font loadedStdFontBold = null;
		try (InputStream fontStream = AppFonts.class.getResourceAsStream("/ens2025/gui/resources/fonts/OpenSans-Bold.ttf")) {
			if (fontStream != null) {
				loadedStdFontBold = Font.loadFont(fontStream, STANDARD_BOLD_FONT_SIZE);
			} else {
				loadedStdFontBold = Font.loadFont(Font.getDefault().getFamily(), Font.getDefault().getSize());
			}
		} catch (Exception e) {
			loadedStdFontBold = Font.loadFont(Font.getDefault().getFamily(), Font.getDefault().getSize());
		}
		
		stdFontBold = loadedStdFontBold;
	}
	
	public static Font getValueFont() {
		return Font.font(valueFont.getFamily(), FontWeight.NORMAL, valueFont.getSize());
	}
	
	public static Font getStandardBoldFont() {
		return Font.font(stdFontBold.getFamily(), FontWeight.BOLD, stdFontBold.getSize());
	}
	
	public static Font getStandardFont() {
		return Font.font(stdFont.getFamily(), FontWeight.NORMAL, stdFont.getSize());
	}
	
	public static Font getIconFont() {
		return Font.font(stdFont.getFamily(), FontWeight.NORMAL, ICON_FONT_SIZE);
	}
}
