package de.mpii.spotter;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import de.mpii.mph.RamSpotRepository;

public class MPHSpotter implements Spotter {
	RamSpotRepository repo;

	public MPHSpotter(File mphDir) {
		repo = new RamSpotRepository(mphDir);
	}

	public void build(String[] mentions) {
		// TODO Auto-generated method stub

	}

	public Map<Integer, Integer> findAllSpots(String[] document) {
		Map<Integer, Integer> map = new LinkedHashMap<Integer, Integer>();
		Shingler shingler = new Shingler(document);
		while (shingler.hasNext()) {
			String spot = shingler.text();
			int id = (int) repo.getId(spot);
			if (id >= 0) {
				System.out.println("match:" + spot + "(" + shingler.getStart()
						+ "," + shingler.getEnd() + ")");
				map.put(shingler.getStart(),
						shingler.getEnd() - shingler.getStart());
				shingler.shiftWindow();
			} else {
				shingler.next();
			}

		}
		return map;
	}

	private class Shingler {

		private int maxShingleSize = 5;
		int start = 0;
		int end = maxShingleSize;
		String[] tokens = null;
		StringBuffer sb = new StringBuffer();

		public Shingler(String[] tokens) {
			this.tokens = tokens;
			end = Math.min(maxShingleSize, tokens.length);
		}

		public void shiftRight() {
			start += 1;
			end = Math.min(start + maxShingleSize, tokens.length);
		}

		public void shiftWindow() {
			start = end;
			end = Math.min(start + maxShingleSize, tokens.length);
		}

		public void next() {
			end--;
			if (start == end) {
				shiftRight();
			}
		}

		public boolean hasNext() {
			return (start < tokens.length) && (end > start);
		}

		public String text() {
			sb.setLength(0);
			for (int i = start; i < end; i++) {
				sb.append(tokens[i]);
				if (i < end - 1)
					sb.append(' ');
			}
			return sb.toString();
		}

		public int getMaxShingleSize() {
			return maxShingleSize;
		}

		public void setMaxShingleSize(int maxShingleSize) {
			this.maxShingleSize = maxShingleSize;
		}

		public int getStart() {
			return start;
		}

		public void setStart(int start) {
			this.start = start;
		}

		public int getEnd() {
			return end;
		}

		public void setEnd(int end) {
			this.end = end;
		}

	}

	public static void main(String[] args) {
		MPHSpotter spotter = new MPHSpotter(new File("/tmp/aida"));
		Map<Integer, Integer> map = spotter.findAllSpots(new String[] { "in",
				"1984", "Diego", "Armando", "Maradona", "infamous", "Hand",
				"of", "God", "goal", "against", "England", "in", "the",
				"quarter-final", "of", "the", "1986" });
		System.out.println(map);
	}

}
