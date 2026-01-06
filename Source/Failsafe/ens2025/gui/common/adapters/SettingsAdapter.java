package ens2025.gui.common.adapters;

import customexception.CustomException;
import data.RData;
import ens2025.gui.common.properties.DataProperty;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import settings.NumericalRepresentation;
import settings.Settings;
import settings.StackGrowth;

public class SettingsAdapter {
	private Settings set;
	private ObjectProperty<StackGrowth> stackGrowthProperty;
	private ObjectProperty<NumericalRepresentation> numRepProperty;
	private BooleanProperty checkPCInvadingStackProperty;
	private BooleanProperty checkSPInvadingCodeProperty;
	private BooleanProperty checkOverflowProperty;
	private BooleanProperty checkUnderflowProperty;
	private BooleanProperty resetRegistersWithNewRunProperty;
	private DataProperty codeStartProperty;
	private DataProperty codeEndProperty;
	private DataProperty stackStartProperty;
	
	public SettingsAdapter(Settings set) {
		this.set = set;

		stackGrowthProperty = new SimpleObjectProperty<>(set.getStackGrowth());
		numRepProperty = new SimpleObjectProperty<>(set.getNumericalRepresentation());
		checkPCInvadingStackProperty = new SimpleBooleanProperty(set.getCheckPCInvadingStack());
		checkSPInvadingCodeProperty = new SimpleBooleanProperty(set.getCheckSPInvadingCode());
		checkOverflowProperty = new SimpleBooleanProperty(set.getCheckOverflow());
		checkUnderflowProperty = new SimpleBooleanProperty(set.getCheckUnderflow());
		resetRegistersWithNewRunProperty = new SimpleBooleanProperty(set.getResetRegistersWithNewRun());
		codeStartProperty = new DataProperty(set.getCodeStart(), this, true);
		codeEndProperty = new DataProperty(set.getCodeEnd(), this, true);
		stackStartProperty = new DataProperty(set.getStackStart(), this, true);
		
		setBehavior();
	}
	
	private void setBehavior() {
		numRepProperty.addListener((observable, oldVal, newVal) -> {
			Platform.runLater(() -> {
				//memoryUpdated.set(true);
				codeStartProperty.set(set.getCodeStart());
				codeEndProperty.set(set.getCodeEnd());
				stackStartProperty.set(set.getStackStart());
			});
		});
	}
	
	public void setToDefault() {
		set.setToDefault();
		
		reloadAll();
	}
	
	public void recalcStackStart() {
		set.recalcStackStart();

		codeStartProperty.set(set.getCodeStart());
		codeEndProperty.set(set.getCodeEnd());
		stackStartProperty.set(set.getStackStart());	
	}
	
	public void setStackGrowth(StackGrowth sg) {
		set.setStackGrowth(sg);
		stackGrowthProperty.set(sg);
	}
	
	public void setCheckPCInvadingStack(boolean val) {
		set.setCheckPCInvadingStack(val);
		checkPCInvadingStackProperty.set(val);
	}
	
	public void setCheckSPInvadingCode(boolean val) {
		set.setCheckSPInvadingCode(val);
		checkSPInvadingCodeProperty.set(val);
	}
	
	public void setCheckOverflow(boolean val) {
		set.setCheckOverflow(val);
		checkOverflowProperty.set(val);
	}
	
	public void setCheckUnderflow(boolean val) {
		set.setCheckUnderflow(val);
		checkUnderflowProperty.set(val);
	}
	
	public void setResetRegistersWithNewRun(boolean val) {
		set.setResetRegistersWithNewRun(val);
		resetRegistersWithNewRunProperty.set(val);
	}
	
	public void setNumericalRepresentation(NumericalRepresentation nr) {
		set.setNumericalRepresentation(nr);
		numRepProperty.set(nr);
	}
	
	public void setCodeStart(String str) {
		codeStartProperty.set(str);
		set.setCodeStart(codeStartProperty.getData());
		/*try {
			codeStartProperty.set(str);
			set.setCodeStart(codeStartProperty.getData());
		} catch (CustomException ex) { }*/
	}
	
	public void setCodeEnd(String str) {
		try {
			codeEndProperty.set(str);
			set.setCodeEnd(codeEndProperty.getData());
		} catch (CustomException ex) { }
	}
	
	public void setStackStart(String str) {
		try {
			stackStartProperty.set(str);
			set.setStackStart(stackStartProperty.getData());
		} catch (CustomException ex) { }
	}
	
	public ObjectProperty<StackGrowth> getStackGrowthProperty() {
		return stackGrowthProperty;
	}
	
	public BooleanProperty getCheckPCInvadingStackProperty() {
		return checkPCInvadingStackProperty;
	}
	
	public BooleanProperty getCheckSPInvadingCodeProperty() {
		return checkSPInvadingCodeProperty;
	}
	
	public BooleanProperty getCheckOverflowProperty() {
		return checkOverflowProperty;
	}
	
	public BooleanProperty getCheckUnderflowProperty() {
		return checkUnderflowProperty;
	}
	
	public BooleanProperty getResetRegistersWithNewRunProperty() {
		return resetRegistersWithNewRunProperty;
	}
	
	public ObjectProperty<NumericalRepresentation> getNumericalRepresentationProperty() {
		return numRepProperty;
	}
	
	public DataProperty getCodeStartProperty() {
		return codeStartProperty;
	}
	
	public DataProperty getCodeEndProperty() {
		return codeEndProperty;
	}
	
	public DataProperty getStackStartProperty() {
		return stackStartProperty;
	}
	
	public StackGrowth getStackGrowth() {
		return set.getStackGrowth();
	}
	
	public boolean getCheckPCInvadingStack() {
		return set.getCheckPCInvadingStack();
	}
	
	public boolean getCheckSPInvadingCode() {
		return set.getCheckSPInvadingCode();
	}
	
	public boolean getResetRegistersWithNewRun() {
		return set.getResetRegistersWithNewRun();
	}
	
	public boolean getCheckOverflow() {
		return set.getCheckOverflow();
	}
	
	public boolean getCheckUnderflow() {
		return set.getCheckUnderflow();
	}
	
	public NumericalRepresentation getNumericalRepresentation() {
		return set.getNumericalRepresentation();
	}
	
	public RData getCodeStart() {
		return set.getCodeStart();
	}
	
	public RData getCodeEnd() {
		return set.getCodeEnd();
	}
	
	public RData getStackStart() {
		return set.getStackStart();
	}

	public void reloadZones() {
		codeStartProperty.set(set.getCodeStart());
		codeEndProperty.set(set.getCodeEnd());
		stackStartProperty.set(set.getStackStart());
	}

	public void loadSettingsSequence() {
		set.loadSettingsSequence();
		
		reloadAll();
	}
	
	public void reloadAll() {
		stackGrowthProperty.set(set.getStackGrowth());
		numRepProperty.set(set.getNumericalRepresentation());
		checkPCInvadingStackProperty.set(set.getCheckPCInvadingStack());
		checkSPInvadingCodeProperty.set(set.getCheckSPInvadingCode());
		checkOverflowProperty = new SimpleBooleanProperty(set.getCheckOverflow());
		checkUnderflowProperty = new SimpleBooleanProperty(set.getCheckUnderflow());
		resetRegistersWithNewRunProperty.set(set.getResetRegistersWithNewRun());
		codeStartProperty.set(set.getCodeStart());
		codeEndProperty.set(set.getCodeEnd());
		stackStartProperty.set(set.getStackStart());
	}
}
