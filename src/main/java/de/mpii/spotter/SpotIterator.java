package de.mpii.spotter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Scanner;

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
		Scanner scanner = new Scanner(nextLine).useDelimiter(delimiter);
		String next = scanner.next();
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
