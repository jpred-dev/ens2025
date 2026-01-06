package data;

import java.io.IOException;
import java.io.OutputStream;

public interface RData {
	public int getValue();
	public void writeToStream(OutputStream stream) throws IOException;
	public boolean isPositive();
	public boolean isPositiveOrZero();
	public boolean isNegative();
	public boolean isNegativeOrZero();
	public boolean isGreater(Number n);
	public boolean isGreaterEqual(Number n);
	public boolean isLess(Number n);
	public boolean isLessEqual(Number n);
	public boolean isEqual(Number n);
	public boolean isGreater(RData d);
	public boolean isGreaterEqual(RData d);
	public boolean isLess(RData d);
	public boolean isLessEqual(RData d);
	public boolean isEqual(RData d);
	public int bitCount();
	public char getChar();
	public String getHexString();
	public String getHalfHexString();
	public String getDecString();
	public String getHalfDecString();
	public String getUnsignedDecString();
	public String getBinString();
	public RWData getMutableCopy();
	public RData getImmutableCopy();
}
