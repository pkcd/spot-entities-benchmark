package de.mpii.spotter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class Benchmark {
	private static final String DOCSTART = "--DOCSTART--";
    private File entityFile; //from a source like aida_means.tsv
    private File documentFile; //from a source like CoNLL.tsv
    
    public Benchmark(File eFile, File dFile)
            throws IOException {
        this.entityFile = eFile;
        this.documentFile = dFile;
    }

    /**
     * @param spotter to be benchmarked
     * @throws IOException 
     */
    public Result measureBuildTime(Spotter spotter) throws IOException {
        long startTime = System.nanoTime();
        spotter.build(new SpotIterable(entityFile));
        long endTime = System.nanoTime();
        return new Result((endTime - startTime) / (1.0 * 1e9), null);
    }
    
    /**
     * @param spotter to be benchmarked
     */
    public List<Result> measureSpottingTime(Spotter spotter) throws IOException {
    	List<Result> results = new ArrayList<Result>();
        ArrayList<String> document = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(documentFile)));
    	while (true) {
            String line = null;
            document.clear();
            while ((line = reader.readLine()) != null && !line.equals(DOCSTART)) {
                String tok = line.split("\t")[0];
                if (tok.length() > 0)
                    document.add(tok);
            }
            if (document.size() <= 0) {
            	break;
            }

            long startTime = System.nanoTime();
            List<Spot> result = spotter.findLongestMatches(document.toArray(new String[]{}));
            long endTime = System.nanoTime();
            double spottingTime = (endTime - startTime)/(1.0*1e9);
            //System.out.println("Spotting Time " + spottingTime + " s");
            results.add(new Result(spottingTime, result));
    	}
    	reader.close();
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
        	File eFile = new File(cmd.getOptionValue("e"));
            File dFile = new File(cmd.getOptionValue("d"));
            
            Benchmark benchmark = new Benchmark(eFile, dFile);
        	File dir = createTempDirectory("mphDir");
        	dir.deleteOnExit();
            Spotter[] subjectSpotters = new Spotter[]{new TrieSpotter(), new MPHSpotter(dir)};
            for (Spotter spotter : subjectSpotters) {
                System.out.println("Benchmarking " + spotter.getClass());

                System.out.println("Building...");
                long startMem = Runtime.getRuntime().totalMemory();
                System.out.println("Total Memory before " + startMem + " bytes");  
                    Result r = benchmark.measureBuildTime(spotter);
                long endMem = Runtime.getRuntime().totalMemory();
                System.out.println("Total Memory after " + endMem + " bytes. " + "Approx build usage " + (endMem - startMem)/(1024.0 * 1024.0) + " MB.");  
                System.out.println("Build Time " + r.getTime() + " s");

                
                System.out.println("Spotting...");
                startMem = Runtime.getRuntime().totalMemory();
                System.out.println("Total Memory before " + startMem + " bytes");  
                    List<Result> results = benchmark.measureSpottingTime(spotter);
                double averageTime = 0;
                for (Result singleResult : results) {
                	averageTime += singleResult.getTime();
                }
                averageTime /= results.size();
                System.out.println("Total Memory after " + endMem + " bytes. " + "Approx spot usage " + (endMem - startMem)/(1024.0 * 1024.0) + " MB.");  
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
