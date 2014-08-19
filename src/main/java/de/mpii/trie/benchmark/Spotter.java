package de.mpii.trie.benchmark;

import java.io.InputStream;

public interface Spotter {

    Trie build(InputStream entityStream);
}
