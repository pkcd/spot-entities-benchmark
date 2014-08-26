package de.mpii.spotter;

public class Spot {

	private int tokenOffset;
	private int tokenCount;
	
	public Spot(int tokenOffset, int tokenCount) {
		this.tokenOffset = tokenOffset;
		this.tokenCount = tokenCount;
	}
	
	public int getTokenOffset() {
		return tokenOffset;
	}

	public int getTokenCount() {
		return tokenCount;
	}

	@Override
	public String toString() {
		return "offset:" + tokenOffset + ", count:" + tokenCount;
	}
}
