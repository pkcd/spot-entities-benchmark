package de.mpii.spotter;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;

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
            Map<Integer, Integer> result = (Map<Integer, Integer>)r.getResult();
            assertEquals(3, result.size());
            assertEquals(1, (int)result.get(0));
            assertEquals(1, (int)result.get(8));
            assertEquals(2, (int)result.get(14));
        }
    }
}

