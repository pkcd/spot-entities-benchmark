package de.mpii.spotter;

import java.io.IOException;
import java.util.List;

/**
 * An interface describing methods that must be implemented by some
 * benchmarked spotter.
 */
public interface Spotter {

    /**
     * 
     * @param tokens
     *            An list of mentions that must be recognized in a document.
     */
    public void build(Iterable<String> entities) throws IOException;
    
    /**
     * 
     * @param document
     *            The document as a string of tokens
     * @return A map describing the spotted entities. The key and value is
     *         offset and count of the match.
     */
    public List<Spot> findLongestMatches(String[] document);
    
}
