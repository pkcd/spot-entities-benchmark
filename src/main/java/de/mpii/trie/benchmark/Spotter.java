package de.mpii.trie.benchmark;

import java.util.Map;

public interface Spotter {

    public void build(Map<String, Integer> tokens);
    
    public Map<Integer, Integer> findAllSpots(String[] tokens);
}
