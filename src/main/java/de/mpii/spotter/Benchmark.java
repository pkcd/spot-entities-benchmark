package de.mpii.spotter;

import java.io.BufferedReader;
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
    private String[] mentions; //from a source like aida_means.tsv
    private ArrayList<String[]> documents; //from a source like CoNLL.tsv
    
    private void initMentions(InputStream entityStream) throws IOException {
        ArrayList<String> mentions = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                entityStream));
        String line = null;
        while ((line = reader.readLine()) != null) {
            int startPos = line.indexOf('"');
            int endPos = line.indexOf('"', startPos + 1);
            String key = line.substring(startPos + 1, endPos);
            mentions.add(key);
        }
        this.mentions = mentions.toArray(new String[]{});
    }

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
    
    public Benchmark(InputStream entityStream, InputStream documentStream)
            throws IOException {
        initMentions(entityStream);
        initDocument(documentStream);
    }

    /**
     * @param spotter to be benchmarked
     */
    public Result measureBuildTime(Spotter spotter) {
        long startTime = System.nanoTime();
        spotter.build(mentions);
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
            
            InputStream entityStream = Files.newInputStream(Paths
                            .get(entityFilePath));
            InputStream documentStream = Files.newInputStream(Paths
                    .get(documentFilePath));
            Benchmark benchmark = new Benchmark(entityStream, documentStream);
            Spotter[] subjectSpotters = new Spotter[]{new TrieSpotter(), /*new MPHSpotter()*/};
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
