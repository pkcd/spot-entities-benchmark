package de.mpii.spotter;

import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.map.TIntIntMap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.mpii.ternarytree.TernaryTriePrimitive;

public class TrieSpotter implements Spotter{

    private TernaryTriePrimitive trie;

    public TrieSpotter() {
        trie = new TernaryTriePrimitive();
    }

    /**
     * 
     * @param mentions
     *            An map of mentions along with ids that must be recognized in a
     *            document.
     * @return An object that can be used to spot the these tokens in a
     *         document.
     * @throws IOException 
     */
    public void build(Iterable<String> iterable) throws IOException {
        File entityFile = File.createTempFile("entities_copy", "txt");
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(entityFile)));
    	Iterator<String> iterator = iterable.iterator();
    	int id = 0;
    	while(iterator.hasNext()) {
    		writer.write(iterator.next() + "\t" + id + "\n");
    		id++;
    	}
    	writer.close();

        File entitySorted = File.createTempFile("entities_sorted", "txt");
    	ExternalSort.sort(entityFile.getPath(), entitySorted.getPath(), '\t', 1, false, false, true);
    	
    	Iterable<String> entities = new SpotIterable(entitySorted, "\t");
        id = 0;
        for (String entity : entities) {
            trie.put(entity, id++);
        }
        entitySorted.delete();
        entityFile.delete();
    }

    /**
     * 
     * @param tokens
     *            The document as a string of tokens
     * @return A map describing the spotted entities. The key and value is
     *         offset and count of the match.
     */
    public List<Spot> findLongestMatches(String[] tokens) {
        List<Spot> returnedResults = new ArrayList<Spot>();
        List<de.mpii.ternarytree.Match> matches = trie.getAllMatches(tokens);
        for (de.mpii.ternarytree.Match match : matches) {
            Spot s = new Spot(match.getTokenOffset(), match.getTokenCount(),
                    match.getValue());
            returnedResults.add(s);
        }
        return returnedResults;
    }  

    public void logStats() {
        System.out.println("Total nodes after building: " + trie.getTotalNodes());
        int[] nodesPerLevel = trie.getNodesPerLevel();
        int total = 0;
        for (int i = 0; i < nodesPerLevel.length; i++) {
            if (i == 0) {
                System.out.print("Nodes per level: " + i + "-" + nodesPerLevel[i]);
            } else {
                System.out.print(", " + i + "-" + nodesPerLevel[i]);
            }
            total += nodesPerLevel[i];
        }
        System.out.println("\nTotal of all levels: " + total);
        
        TIntIntMap distribution = trie.getCollapsableLengths();
        boolean first = true;
        long totalCollapsableCharacters = 0;
        long totalCollapsableStrings = 0;
        for (TIntIntIterator it = distribution.iterator(); it.hasNext();) {
            it.advance();
            if (first) {
                System.out.print("Distribution of collapsable strings: " + it.key() + "-" + it.value());
            } else {
                System.out.print(", " + it.key() + "-" + it.value());
            }
            totalCollapsableCharacters += it.key() * it.value();
            totalCollapsableStrings += it.value();
        }
        System.out.println("\nTotal collapsable characters: " + totalCollapsableCharacters);
        System.out.println("Total collapsable strings: " + totalCollapsableStrings);
        System.out.println("Average collapsable length: " + (totalCollapsableCharacters * 1.0) / totalCollapsableStrings);
    }
}
