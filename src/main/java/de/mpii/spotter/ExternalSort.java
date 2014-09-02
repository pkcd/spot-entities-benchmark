package de.mpii.spotter;

import java.io.IOException;
import java.util.ArrayList;

public class ExternalSort {

	public static int sort(String inputPath, String outputPath, char separator,
			int keyField, boolean isNumeric, boolean doShuffle) throws IOException {
		ArrayList<String> command = new ArrayList<String>();
		command.add("/bin/bash");
		command.add("-c");
		command.add("-t");
		command.add("'" + String.valueOf(separator) + "'");
		command.add("-k");
		command.add(String.valueOf(keyField));
		if (isNumeric) {
			command.add("-n");
		}
		if (doShuffle) {
			command.add("-R");
		}
		command.add(inputPath);
		command.add(">");
		command.add(outputPath);
		
		System.out.println(String.join(" ", command));
		
		Process pb = Runtime.getRuntime().exec(command.toArray(new String[]{}));
		
		return pb.exitValue();
	}
}
