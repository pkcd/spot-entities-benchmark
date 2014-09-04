package de.mpii.spotter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

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
    public void build(Iterable<String> iterable) {
    	ArrayList<String> entities = new ArrayList<String>();
    	Iterator<String> entitiesIter = iterable.iterator();
    	while(entitiesIter.hasNext()) {
    		entities.add(entitiesIter.next());
    	}
        Random r = new Random();
        for(int i = entities.size() - 1; i > 0; i--) {
            int randomIndex = r.nextInt(i);
            String temp = entities.get(i);
            entities.set(i, entities.get(randomIndex));
            entities.set(randomIndex, temp);
        }
        int id = 0;
        for (String entity : entities) {
            trie.put(entity, id++);
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
