package de.mpii.trie.benchmark;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class Benchmark {
    private Map<String, Integer> mentions; //from a source like aida_means.tsv
    private String[] document; //from a source like CoNLL.tsv
    
    private void initMentions(InputStream entityStream) throws IOException {
        mentions = new HashMap<String, Integer>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                entityStream));
        String line = null;
        int lineNumber = 0;
        while ((line = reader.readLine()) != null) {
            int startPos = line.indexOf('"');
            int endPos = line.indexOf('"', startPos + 1);
            String key = line.substring(startPos + 1, endPos);
            mentions.put(key, lineNumber++);
        }
    }

    private void initDocument(InputStream documentStream) throws IOException {
        ArrayList<String> doc = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                documentStream));
        String line = null;
        while ((line = reader.readLine()) != null) {
            String[] tokens = line.split("\t");
            doc.add(tokens[0]);
        }
        document = doc.toArray(new String[]{});
    }
    
    public Benchmark(InputStream entityStream, InputStream documentStream)
            throws IOException {
        initMentions(entityStream);
        initDocument(documentStream);
    }

    /**
     * @param spotter to be benchmarked
     */
    public void measureBuildTime(Spotter spotter) {
        long startTime = System.nanoTime();
        spotter.build(mentions);
        long endTime = System.nanoTime();
        double buildTime = (endTime - startTime)/(1.0*1e9);
        System.out.println("Build Time " + buildTime + " s");
    }
    
    /**
     * @param spotter to be benchmarked
     */
    public void measureSpottingTime(Spotter spotter) {
        long startTime = System.nanoTime();
        spotter.findAllSpots(document);
        long endTime = System.nanoTime();
        double spottingTime = (endTime - startTime)/(1.0*1e9);
        System.out.println("Spotting Time " + spottingTime + " s");
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
            String spotterClass = cmd.getOptionValue("c");
            String entityFilePath = cmd.getOptionValue("e");
            String documentFilePath = cmd.getOptionValue("d");
            
            InputStream entityStream = Files.newInputStream(Paths
                            .get(entityFilePath));
            InputStream documentStream = Files.newInputStream(Paths
                    .get(documentFilePath));
            Benchmark benchmark = new Benchmark(entityStream, documentStream);
            File testJar = new File(inputJarPath);
            ClassLoader loader = URLClassLoader.newInstance(new URL[] { testJar
                    .toURI().toURL() }, benchmark.getClass().getClassLoader());
            Class<?> clazz = Class.forName(spotterClass, true, loader);
            Spotter spotter = (Spotter)clazz.newInstance();
            benchmark.measureBuildTime(spotter);
            benchmark.measureSpottingTime(spotter);
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
