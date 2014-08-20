package de.mpii.trie.benchmark;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import org.junit.Before;
import org.junit.Test;

public class BenchmarkTest {

    private Benchmark benchmark;
    private Spotter spotter;
    
    @Before
    public void initBenchmarkTest() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        this.benchmark = new Benchmark(getClass().getResourceAsStream("/entities.txt"), 
                getClass().getResourceAsStream("/document.txt"));
        File testJar = new File(getClass().getResource(
                "/ternarytree-0.0.4-SNAPSHOT.jar").getPath());
        ClassLoader loader = URLClassLoader.newInstance(new URL[] { testJar
                .toURI().toURL() }, System.class.getClassLoader());
        Class<?> clazz = Class.forName("de.mpii.ternarytree.Spotter", true, loader);
        Object spot = clazz.newInstance();
        this.spotter = (Spotter) spot;
    }
    
    @Test
    public void testBenchmark() {
        benchmark.measureBuildTime(spotter);
        benchmark.measureSpottingTime(spotter);
    }
}

