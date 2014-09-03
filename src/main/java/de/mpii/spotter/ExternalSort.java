package de.mpii.spotter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ExternalSort {

	public static int sort(String inputPath, String outputPath, char delimiter,
			int keyField, boolean isNumeric, boolean doShuffle) throws IOException {
		ArrayList<String> command = new ArrayList<String>();
		command.add("sort");
		command.add("-t");
		command.add(String.valueOf(delimiter));
		command.add("-k" + String.valueOf(keyField));
		if (isNumeric) {
			command.add("-n");
		}
		if (doShuffle) {
			command.add("-R");
		}
		command.add(inputPath);
		command.add("-o");
		command.add(outputPath);
		
		//System.out.println("Executing command: " + command);
		
		Process pb = Runtime.getRuntime().exec(command.toArray(new String[]{}));
		try {
			pb.waitFor();
		} catch (InterruptedException e) {
			return -1;
		}
//		StringBuffer output = new StringBuffer();
//		BufferedReader reader = 
//                new BufferedReader(new InputStreamReader(pb.getInputStream()));
//
//            String line = "";			
//		while ((line = reader.readLine())!= null) {
//			output.append(line + "\n");
//		}
//		System.out.println("output: " + output.toString());

		return pb.exitValue();
	}
	
	public static void main(String args[]) throws IOException {
		int returnValue = ExternalSort.sort("/tmp/test.txt", "/tmp/sort_test.txt", '\t', 2, true, false);
		System.out.println("Return Value = " + returnValue);

		returnValue = ExternalSort.sort("/tmp/test.txt", "/tmp/random_test.txt", '\t', 1, false, false);
		System.out.println("Return Value = " + returnValue);
	}
}
