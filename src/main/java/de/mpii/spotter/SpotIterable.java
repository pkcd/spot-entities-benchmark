package de.mpii.spotter;

import java.io.InputStream;
import java.util.Iterator;

public class SpotIterable implements Iterable<String>{
	private InputStream spotStream;
	private String delimiter;

	 public SpotIterable(InputStream spotStream) {
		this.spotStream = spotStream;
		this.delimiter = "\"";
	}
	 
	 public SpotIterable(InputStream spotStream, String delimiter) {
		this.spotStream = spotStream;
		this.delimiter = delimiter;
	}

	public Iterator<String> iterator() {
		return new SpotIterator(spotStream, delimiter);
	}

}
