package de.mpii.spotter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class Benchmark {
	private static final String DOCSTART = "--DOCSTART--";
    private String entityFilePath; //from a source like aida_means.tsv
    private ArrayList<String[]> documents; //from a source like CoNLL.tsv
    
    private void initDocument(InputStream documentStream) throws IOException {
    	documents = new ArrayList<String[]>();
        ArrayList<String> currentDocument = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                documentStream));
        String line = null;
        while ((line = reader.readLine()) != null) {
            if(line.equals(DOCSTART)) {
            	documents.add(currentDocument.toArray(new String[]{}));
            	currentDocument.clear();
            } else {
                String[] tokens = line.split("\t");
            	currentDocument.add(tokens[0]);
            }
        }
        if (currentDocument.size() > 0) {
        	documents.add(currentDocument.toArray(new String[]{}));
        }
    }
    
    public Benchmark(String entityFilePath, InputStream documentStream)
            throws IOException {
        this.entityFilePath = entityFilePath;
        initDocument(documentStream);
    }

    /**
     * @param spotter to be benchmarked
     * @throws IOException 
     */
    public Result measureBuildTime(Spotter spotter) throws IOException {
        long startTime = System.nanoTime();
        spotter.build(entityFilePath);
        long endTime = System.nanoTime();
        return new Result((endTime - startTime) / (1.0 * 1e9), null);
    }
    
    /**
     * @param spotter to be benchmarked
     */
    public List<Result> measureSpottingTime(Spotter spotter) {
    	List<Result> results = new ArrayList<Result>();
    	for (String[] document : documents) {
            long startTime = System.nanoTime();
            List<Spot> result = spotter.findLongestMatches(document);
            long endTime = System.nanoTime();
            double spottingTime = (endTime - startTime)/(1.0*1e9);
            //System.out.println("Spotting Time " + spottingTime + " s");
            results.add(new Result(spottingTime, result));
    	}
    	return results;
    }
    
    public class Result {
        private Object result;
        private double time;
        
        public Result(double time, Object result) {
            this.result = result;
            this.time = time;
        }
        
        public Object getResult() {
            return result;
        }
        
        public double getTime() {
            return time;
        }
    };
    
    
	private static File createTempDirectory(String prefix) throws IOException {
		final File temp;

		temp = File.createTempFile(prefix, Long.toString(System.nanoTime()));

		if (!(temp.delete())) {
			throw new IOException("Could not delete temp file: "
					+ temp.getAbsolutePath());
		}

		if (!(temp.mkdir())) {
			throw new IOException("Could not create temp directory: "
					+ temp.getAbsolutePath());
		}

		return (temp);
	}

	public static void main(String[] args) {
    	//java -jar Benchmark.jar -e <entity_file> -d <document_file>
        Options options = new Options();
        options.addOption("e", "entity-file", true,
                "File containing entities for building the trie");
        options.addOption("d", "document-file", true,
                "Document in which the entities should be spotted");
        CommandLineParser parser = new PosixParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            String entityFilePath = cmd.getOptionValue("e");
            String documentFilePath = cmd.getOptionValue("d");
            
            InputStream documentStream = Files.newInputStream(Paths
                    .get(documentFilePath));
            Benchmark benchmark = new Benchmark(entityFilePath, documentStream);
        	File dir = createTempDirectory("mphDir");
        	dir.deleteOnExit();
            Spotter[] subjectSpotters = new Spotter[]{new TrieSpotter(), new MPHSpotter(dir)};
            for (Spotter spotter : subjectSpotters) {
                System.out.println("Benchmarking " + spotter.getClass());
                Result r = benchmark.measureBuildTime(spotter);
                System.out.println("Build Time " + r.getTime() + " s");
                List<Result> results = benchmark.measureSpottingTime(spotter);
                double averageTime = 0;
                for (Result singleResult : results) {
                	averageTime += singleResult.getTime();
                }
                averageTime /= results.size();
				System.out.println("Average Spotting Time for "
						+ results.size() + " documents: " + averageTime + " s");
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
