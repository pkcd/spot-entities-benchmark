package de.mpii.spotter;

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
    	ExternalSort.sort(entityFile.getPath(), entitySorted.getPath(), '\t', 1, false, true);
    	
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
    	List<de.mpii.ternarytree.Spot> matches = trie.getAllMatches(tokens);
    	for (de.mpii.ternarytree.Spot match : matches) {
    		Spot s = new Spot(match.getTokenOffset(), match.getTokenCount(), match.getValue());
    		returnedResults.add(s);
    	}
    	return returnedResults;
   }

}
