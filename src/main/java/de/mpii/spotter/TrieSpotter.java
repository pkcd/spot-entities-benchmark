package de.mpii.spotter;

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
     */
    public void build(ArrayList<String> mentions) {
        int id = 0;
        for (String mention : mentions) {
            trie.put(mention, ++id);
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
