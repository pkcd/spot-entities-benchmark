package de.mpii.spotter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

public class SpotIterator implements Iterator<String> {
	BufferedReader br = null;
	String nextLine = null;
	private String delimiter;

	public SpotIterator(InputStream spotStream) {
		this(spotStream, "\"");
	}

	public SpotIterator(InputStream spotStream, String delimiter) {
		br = new BufferedReader(new InputStreamReader(spotStream));
		try {
			nextLine = br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.delimiter = delimiter;
	}
	
	public boolean hasNext() {
		return nextLine != null;
	}

	public String next() {
		String next;
		int indexOf = nextLine.indexOf(delimiter);
		if (indexOf == 0) {
		    next = nextLine.substring(1, nextLine.indexOf(delimiter, 1));
		} else {
		    next = nextLine.substring(0, indexOf);
		}
		try {
			nextLine = br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return next;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

}
