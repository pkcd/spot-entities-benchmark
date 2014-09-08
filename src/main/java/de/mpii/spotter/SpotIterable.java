package de.mpii.spotter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;

public class SpotIterable implements Iterable<String>{
	private File spotFile;
	private String delimiter;

	 public SpotIterable(File spotFile) {
		this.spotFile = spotFile;
		this.delimiter = "\"";
	}
	 
	 public SpotIterable(File spotFile, String delimiter) {
		this.spotFile = spotFile;
		this.delimiter = delimiter;
	}

	public Iterator<String> iterator() {
		try {
            return new SpotIterator(new FileInputStream(spotFile), delimiter);
        } catch (FileNotFoundException e) {
            return null;
        }
	}

}
