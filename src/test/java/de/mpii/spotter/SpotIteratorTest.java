package de.mpii.spotter;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Test;

public class SpotIteratorTest {

	@Test
	public void testBasic() throws URISyntaxException {
        SpotIterable iterable = new SpotIterable(new File(getClass()
                .getResource("/entities.txt").toURI()));
		Iterator<String> iterator = iterable.iterator();
		ArrayList<String> readValues = new ArrayList<String>();
		while(iterator.hasNext()) {
			readValues.add(iterator.next());
		}
		assertEquals("Saarbruken", readValues.get(0));
		assertEquals("German Union", readValues.get(1));
		assertEquals("Berlin", readValues.get(2));
		assertEquals("Saarland University", readValues.get(3));
		assertEquals("CHE", readValues.get(4));
	}
}
