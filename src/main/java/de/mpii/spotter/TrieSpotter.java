package de.mpii.spotter;

import java.util.Map;

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
    public void build(String[] mentions) {
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
    public Map<Integer, Integer> findAllSpots(String[] tokens) {
        return trie.getAllMatches(tokens);
    }

}
