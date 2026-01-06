package controlunit.architecture.breakpointmanager;

import data.RData;
import data.RWData;

public class BreakpointManager {
	private boolean[] points;
	
	public BreakpointManager() {
		points = new boolean[RWData.MAX_UNSIGNED_VALUE+1];
	}
	
	public boolean hasBreakpoint(RData index) {
		return points[index.getValue()];
	}
	
	public void setBreakpoint(RData index, boolean bp) {
		points[index.getValue()] = bp;
	}
}
