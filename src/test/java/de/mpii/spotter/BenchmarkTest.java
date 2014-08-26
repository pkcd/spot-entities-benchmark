package de.mpii.spotter;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.mpii.spotter.Benchmark.Result;

public class BenchmarkTest {

    private Benchmark benchmark;
    
    @Before
    public void initBenchmarkTest() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        this.benchmark = new Benchmark(getClass().getResourceAsStream("/entities.txt"), 
                getClass().getResourceAsStream("/document.txt"));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testBenchmark() {
        Spotter[] subjectSpotters = new Spotter[]{new TrieSpotter(), /*new MPHSpotter()*/};
        for (Spotter spotter : subjectSpotters) {
            Result r = benchmark.measureBuildTime(spotter);
            assertEquals(true, r.getTime() > 0);
            r = benchmark.measureSpottingTime(spotter);
            assertEquals(true, r.getTime() > 0);
            List<Spot> result = (List<Spot>)r.getResult();
            assertEquals(3, result.size());
            
            assertEquals(0, result.get(0).getTokenOffset());
            assertEquals(8, result.get(1).getTokenOffset());
            assertEquals(14, result.get(2).getTokenOffset());
            
            assertEquals(1, result.get(0).getTokenCount());
            assertEquals(1, result.get(1).getTokenCount());
            assertEquals(2, result.get(2).getTokenCount());
        }
    }
}

