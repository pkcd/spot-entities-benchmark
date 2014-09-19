package de.mpii.spotter;

import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SimpleSpotter implements Spotter{

    private TObjectIntHashMap<String> mentions = new TObjectIntHashMap<String>();
    private int max = -1;
    private String delim = " ";
    
    @Override
    public void build(Iterable<String> entities) throws IOException {
        Iterator<String> iter = entities.iterator();
        int id = 0;
        while (iter.hasNext()) {
            String s = iter.next();
            int l = s.split(delim).length;
            if (l > max) max = l;
            mentions.put(s, ++id);
        }
    }

    @Override
    public List<Spot> findLongestMatches(String[] document) {
        List<Spot> spots = new ArrayList<Spot>();
        int i = 0;
        while (i < document.length) {
            int len;
            for (len = max; len > 0; len--) {
                String key = getSubString(document, i, len);
                int val = mentions.get(key);
                if (mentions.get(key) > 0) {
                    spots.add(new Spot(i, len, val));
                    i = i + len;
                    break;
                }
            }
            if (len == 0) {
                i++;
            }
        }
        return spots;
    }
    
    private String getSubString(String[] tokens, int offset, int len) {
        StringBuilder s = new StringBuilder();
        if (offset + len > tokens.length) {
            len = tokens.length - offset;
        }
        for (int i = 0; i < len; i++) {
            s.append(tokens[offset + i]);
            if (i < len - 1) {
                s.append(delim);
            }
        }
        return s.toString();
    }

}
