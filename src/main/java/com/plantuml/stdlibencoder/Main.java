package com.plantuml.stdlibencoder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class Main {

	public static void main(String[] args) throws IOException {
		StdlibFolderBuilder.deleteHomeRepx();

		// Path to the "stdlib" directory
		final Path stdlibPath = Paths.get("stdlib");

		// Create a fixed thread pool to manage parallel tasks
		final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

		try (Stream<Path> paths = Files.list(stdlibPath)) { //
			paths.filter(Files::isDirectory) //
					.forEach(dir -> executor.submit(() -> {
						try {
							new StdlibFolderBuilder(dir.getFileName().toString());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}));
		} finally {
			executor.shutdown(); // Initiates an orderly shutdown of the thread pool
		}
	}

}
