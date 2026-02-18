package com.plantuml.stdlibencoder.uid;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Main entry point to compute a stable SHA-1 for each stdlib library.
 * <p>
 * For each directory under {@code stdlib/}, a SHA-1 digest is computed covering
 * all {@code .puml} and {@code .json} files. The resulting UID is written into
 * the YAML header of each library's {@code README.md}.
 * <p>
 * Usage: run from the plantuml-stdlib root directory.
 */
public class MainUid {

	public static void main(String[] args) throws IOException {
		final Path stdlibPath = Paths.get("stdlib");

		final Map<String, String> uidByLibrary = computeUids(stdlibPath);

		updateReadMeFiles(stdlibPath, uidByLibrary);
	}

	public static void updateReadMeFiles(final Path stdlibPath, final Map<String, String> uidByLibrary)
			throws IOException {
		for (Map.Entry<String, String> entry : uidByLibrary.entrySet()) {
			final String name = entry.getKey();
			final String uid = entry.getValue();

			if ("0".equals(uid))
				continue;

			final Path readmePath = stdlibPath.resolve(name).resolve("README.md");
			if (Files.exists(readmePath)) {
				final ReadmeMd readme = new ReadmeMd(readmePath);
				readme.set("uid", uid);
				readme.save();
				System.out.println(name + " " + uid);
			}
		}
	}

	public static Map<String, String> computeUids(final Path stdlibPath) throws IOException {
		final Map<String, String> uidByLibrary;

		try (Stream<Path> paths = Files.list(stdlibPath)) {
			uidByLibrary = paths.filter(Files::isDirectory).parallel() //
					.collect(Collectors.toMap( //
							path -> path.getFileName().toString().toLowerCase(), //
							path -> new UidBuilder(path.getFileName().toString()).getSha1(), //
							(a, b) -> a, //
							TreeMap::new));
		}
		return uidByLibrary;
	}

}
