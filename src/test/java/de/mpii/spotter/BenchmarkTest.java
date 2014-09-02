package de.mpii.spotter;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.mpii.spotter.Benchmark.Result;

public class BenchmarkTest {

    private Benchmark benchmark;
    
    @Before
    public void initBenchmarkTest() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        this.benchmark = new Benchmark(getClass().getResource("/entities.txt").getPath(), 
                getClass().getResourceAsStream("/document.txt"));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testBenchmark() throws IOException {
    	File mphDir = createTempDirectory("mphDir");
    	mphDir.deleteOnExit();
        Spotter[] subjectSpotters = new Spotter[]{new TrieSpotter(), new MPHSpotter(mphDir)};
        for (Spotter spotter : subjectSpotters) {
            Result r = benchmark.measureBuildTime(spotter);
            assertEquals(true, r.getTime() > 0);
            
            List<Result> outputs = benchmark.measureSpottingTime(spotter);
            assertEquals(2, outputs.size());
            
            r = outputs.get(0);
            assertEquals(true, r.getTime() > 0);
            List<Spot> result = (List<Spot>)r.getResult();
            assertEquals(3, result.size());
            
            assertEquals(0, result.get(0).getTokenOffset());
            assertEquals(8, result.get(1).getTokenOffset());
            assertEquals(14, result.get(2).getTokenOffset());
            
            assertEquals(1, result.get(0).getTokenCount());
            assertEquals(1, result.get(1).getTokenCount());
            assertEquals(2, result.get(2).getTokenCount());

            r = outputs.get(1);
            assertEquals(true, r.getTime() > 0);
            result = (List<Spot>)r.getResult();
            assertEquals(3, result.size());
            
            assertEquals(0, result.get(0).getTokenOffset());
            assertEquals(3, result.get(1).getTokenOffset());
            assertEquals(9, result.get(2).getTokenOffset());
            
            assertEquals(2, result.get(0).getTokenCount());
            assertEquals(1, result.get(1).getTokenCount());
            assertEquals(1, result.get(2).getTokenCount());
        }
    }
    
	private File createTempDirectory(String prefix) throws IOException {
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
}

