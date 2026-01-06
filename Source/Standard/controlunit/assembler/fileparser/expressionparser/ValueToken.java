package controlunit.assembler.fileparser.expressionparser;



public class ValueToken extends Token {
	private int value;
	
	public ValueToken(TokenType type, int value) {
		super(type);
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
}
