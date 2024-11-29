package com.plantuml.stdlibencoder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class Main {

	public static void main(String[] args) throws IOException, InterruptedException {
		// Path to the "stdlib" directory
		final Path stdlibPath = Paths.get("stdlib");

		// Create a fixed thread pool to manage parallel tasks
		final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		final Collection<String> names = new TreeSet<String>();

		try (Stream<Path> paths = Files.list(stdlibPath)) { //
			paths.filter(Files::isDirectory) //
					.forEach(dir -> executor.submit(() -> {
						try {
							final String name = dir.getFileName().toString();
							new StdlibFolderBuilder(name);
							synchronized (names) {
								names.add(name.toLowerCase());
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}));
		} finally {
			executor.shutdown(); // Initiates an orderly shutdown of the thread pool
			executor.awaitTermination(1, TimeUnit.HOURS);
		}

		System.err.println("names=" + names);
		final Path filePath = Paths.get("output", "home.repx");
		Files.writeString(filePath, String.join("\n", names));
	}

}
