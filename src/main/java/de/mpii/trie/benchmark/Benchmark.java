package de.mpii.trie.benchmark;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class Benchmark {
    private InputStream entityStream;
    
    public Benchmark(InputStream entityStream) {
        this.entityStream = entityStream;
    }

    /**
     * @param trie to be benchmarked
     * @return build time in seconds.
     */
    public Trie build(Spotter helper) {
        return helper.build(entityStream);
    }
    
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("i", "input-jar", true,
                "Jar file containing the trie implementation");
        options.addOption("c", "class-name", true,
                "Qualified class name of the trie to be benchmarked");
        options.addOption("e", "entity-file", true,
                "File containing entities for building the trie");
        options.addOption("d", "document-file", true,
                "Document in which the entities should be spotted");
        CommandLineParser parser = new PosixParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            String inputJarPath = cmd.getOptionValue("i");
            String trieClass = cmd.getOptionValue("c");
            String entityFilePath = cmd.getOptionValue("e");
//            String documentFilePath = cmd.getOptionValue("d");
            
            InputStream entityStream = Files.newInputStream(Paths
                            .get(entityFilePath));
            Benchmark benchmark = new Benchmark(entityStream);
            File testJar = new File(inputJarPath);
            ClassLoader loader = URLClassLoader.newInstance(new URL[] { testJar
                    .toURI().toURL() }, benchmark.getClass().getClassLoader());
            Class<?> clazz = Class.forName(trieClass, true, loader);
            Spotter helper = (Spotter)clazz.newInstance();
            
            long startTime = System.nanoTime();
            benchmark.build(helper);
            long endTime = System.nanoTime();
            double buildTime = (endTime - startTime)/(1.0*1e9);
            System.out.println("Build Time " + buildTime + " s");
            
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
