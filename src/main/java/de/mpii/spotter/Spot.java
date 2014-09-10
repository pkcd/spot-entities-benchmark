package de.mpii.spotter;

class Spot {

	private int tokenOffset;
	private int tokenCount;
	private int value;
	
	public Spot(int tokenOffset, int tokenCount, int value) {
		this.tokenOffset = tokenOffset;
		this.tokenCount = tokenCount;
		this.value = value;
	}
	
	public int getTokenOffset() {
		return tokenOffset;
	}

	public int getTokenCount() {
		return tokenCount;
	}

	public int getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return "offset:" + tokenOffset + ", count:" + tokenCount + ", value:" + value;
	}
}
