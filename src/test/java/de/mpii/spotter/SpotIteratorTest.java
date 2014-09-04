package de.mpii.spotter;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Test;

public class SpotIteratorTest {

	@Test
	public void testBasic() {
		SpotIterable iterable = new SpotIterable(getClass().getResourceAsStream("/entities.txt"));
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
