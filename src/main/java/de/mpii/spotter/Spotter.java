package de.mpii.spotter;

import java.util.Map;

/**
 * An interface describing methods that must be implemented by some
 * benchmarked spotter.
 */
public interface Spotter {

    /**
     * 
     * @param tokens
     *            An array mentions that must be recognized in a document.
     */
    public void build(String[] mentions);
    
    /**
     * 
     * @param document
     *            The document as a string of tokens
     * @return A map describing the spotted entities. The key and value is
     *         offset and count of the match.
     */
    public Map<Integer, Integer> findAllSpots(String[] document);
}
