package de.mpii.trie.benchmark;

import java.util.Map;

/**
 * A placeholder interface describing methods that must be implemented by some
 * class in the benchmarked jar.
 */
public interface Spotter {

    /**
     * 
     * @param tokens
     *            An map of mentions along with ids that must be recognized in a
     *            document.
     */
    public void build(Map<String, Integer> tokens);
    
    /**
     * 
     * @param tokens
     *            The document as a string of tokens
     * @return A map describing the spotted entities. The key and value is
     *         offset and count of the match.
     */
    public Map<Integer, Integer> findAllSpots(String[] tokens);
}
