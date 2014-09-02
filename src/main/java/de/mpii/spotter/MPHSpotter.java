package de.mpii.spotter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.mpii.mph.RamSpotFile;
import de.mpii.mph.RamSpotRepository;
import de.mpii.mph.SpotEliasFanoOffsets;
import de.mpii.mph.SpotMinimalPerfectHash;

public class MPHSpotter implements Spotter {
	RamSpotRepository repo;
	private final int maxShingleSize;
	private File mphDir;

	public MPHSpotter(File mphDir, int maxShingleSize) {
		this.mphDir = mphDir;
		this.maxShingleSize = maxShingleSize;
	}

	public MPHSpotter(File mphDir) {
		this(mphDir, 5);
	}

	public void build(String entityFilePath) throws IOException {
		//generate mph values
		File inputPath = new File(entityFilePath);
		File mphPath = new File(mphDir, SpotMinimalPerfectHash.STDNAME);
		File spotMphFile = new File(mphDir,
				SpotMinimalPerfectHash.STDSPOTNAME);

		System.out.println("mphPath = " + mphPath);
		System.out.println("outputPath = " + spotMphFile);

		SpotMinimalPerfectHash mph = new SpotMinimalPerfectHash()
				.generateHash(inputPath);
		mph.dumpSpotsAndHash(inputPath, spotMphFile);
		mph.dump(mphPath);

		//sort by mph values
		File sortedSpotMphFile = new File(mphDir, "sorted_spot_mph.tsv");
		ExternalSort.sort(spotMphFile.getPath(), sortedSpotMphFile.getPath(), '\t', 2, true, false);
		sortedSpotMphFile.renameTo(spotMphFile);
		
		//generate mph index
		File outputPath = new File(mphDir, RamSpotFile.STDNAME);
		File efPath = new File(mphDir, SpotEliasFanoOffsets.STDNAME);
		File tmp = null;
		try {
			tmp = File.createTempFile("eliasfano-offset", ".txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tmp.deleteOnExit();
		// inputPath.deleteOnExit();
		RamSpotFile spotFile = new RamSpotFile();
		spotFile.dumpSpotFile(spotMphFile, outputPath, tmp);
		SpotEliasFanoOffsets offsets = new SpotEliasFanoOffsets()
				.generateEliasFanoFile(tmp.getAbsolutePath());
		offsets.dump(efPath);
	}

	public List<Spot> findLongestMatches(String[] document) {
		List<Spot> matchedSpots = new ArrayList<Spot>();
		Shingler shingler = new Shingler(document);
		while (shingler.hasNext()) {
			String spot = shingler.text();
			int id = (int) repo.getId(spot);
			if (id >= 0) {
				System.out.println("match:" + spot + "(" + shingler.getStart()
						+ "," + shingler.getEnd() + ")");
				matchedSpots.add(new Spot(shingler.getStart(), shingler
						.getCurrentTokenLength(), id));

				shingler.shiftWindow();
			} else {
				shingler.next();
			}

		}
		return matchedSpots;
	}

	private class Shingler {

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

		public int getCurrentTokenLength() {
			return end - start;
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
		List<Spot> map = spotter.findLongestMatches(new String[] { "in",
				"1984", "Diego", "Armando", "Maradona", "infamous", "Hand",
				"of", "God", "goal", "against", "England", "in", "the",
				"quarter-final", "of", "the", "1986" });
		System.out.println(map);
	}

}
