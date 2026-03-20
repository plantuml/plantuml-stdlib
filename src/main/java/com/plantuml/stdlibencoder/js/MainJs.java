package com.plantuml.stdlibencoder.js;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Main entry point to generate JS files for all stdlib libraries.
 * <p>
 * For each directory under {@code stdlib/}, a corresponding {@code .js} file is
 * generated in the {@code output-js/} directory.
 * <p>
 * Usage: run from the plantuml-stdlib root directory.
 */
public class MainJs {

	public static void main(String[] args) throws IOException {
		final Path stdlibPath = Paths.get("stdlib");
		final Path outputDir = Paths.get("output-js");

		Files.createDirectories(outputDir);

		final Set<String> names;
		try (Stream<Path> paths = Files.list(stdlibPath)) {
			names = paths.filter(Files::isDirectory).parallel() //
					.map(path -> path.getFileName().toString()) //
					.map(name -> {
						try {
							new JsBuilder(name);
						} catch (IOException e) {
							e.printStackTrace();
						}
						return name.toLowerCase();
					}) //
					.collect(Collectors.toCollection(TreeSet::new));
		}

		System.err.println("Generated JS for: " + names);
	}

}
