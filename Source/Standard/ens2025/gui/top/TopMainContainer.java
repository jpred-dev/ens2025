package ens2025.gui.top;

import ens2025.gui.common.adapters.ControlUnitAdapter;
import ens2025.gui.common.adapters.IOAdapter;
import ens2025.gui.common.adapters.SettingsAdapter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

public class TopMainContainer extends HBox {
	private FileToolbar ftb;
	private RunToolbar rtb;
	private SettingsToolbar stb;
	
	public static final double SPACING = 10;
	public static final double TOTAL_WIDTH = FileToolbar.TOTAL_WIDTH + RunToolbar.TOTAL_WIDTH + SettingsToolbar.TOTAL_WIDTH + SPACING * 2;

	public TopMainContainer(ControlUnitAdapter cua, SettingsAdapter sa, IOAdapter io) {
		ftb = new FileToolbar(cua, io);
		rtb = new RunToolbar(cua, io);
		stb = new SettingsToolbar(cua, sa);
		
		stb.setAlignment(Pos.CENTER);
		
		this.getChildren().addAll(ftb, rtb, stb);
		this.setSpacing(SPACING);
		this.setPadding(new Insets(0, 0, 10, 0));
	}
}
