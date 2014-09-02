package de.mpii.spotter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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
    public void build(String entityFilePath) throws IOException {
    	InputStream entityStream = Files.newInputStream(Paths.get(entityFilePath));
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                entityStream));
        String line = null;
        int id = 0;
        while ((line = reader.readLine()) != null) {
            int startPos = line.indexOf('"');
            int endPos = line.indexOf('"', startPos + 1);
            String key = line.substring(startPos + 1, endPos);
            trie.put(key, ++id);
        }
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
