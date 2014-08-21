package de.mpii.trie.benchmark;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class BenchmarkTest {

    private Benchmark benchmark;
    private Class<?> spotterClass;
    
    @Before
    public void initBenchmarkTest() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        this.benchmark = new Benchmark(getClass().getResourceAsStream("/entities.txt"), 
                getClass().getResourceAsStream("/document.txt"));
        File testJar = new File(getClass().getResource(
                "/ternarytree-0.0.4-SNAPSHOT-jar-with-dependencies.jar").getPath());
        ClassLoader loader = URLClassLoader.newInstance(new URL[] { testJar
                .toURI().toURL() }, System.class.getClassLoader());
        spotterClass = Class.forName("de.mpii.ternarytree.Spotter", true, loader);
    }
    
    @Test
    public void testBenchmark() throws InstantiationException,
            IllegalAccessException, NoSuchMethodException,
            InvocationTargetException {
        Object spotter = spotterClass.newInstance();
        benchmark.measureBuildTime(spotterClass, spotter);
        Map<Integer, Integer> result = benchmark.measureSpottingTime(spotterClass, spotter);
        assertEquals(3, result.size());
        assertEquals(1, (int)result.get(0));
        assertEquals(1, (int)result.get(8));
        assertEquals(2, (int)result.get(14));
    }
}

