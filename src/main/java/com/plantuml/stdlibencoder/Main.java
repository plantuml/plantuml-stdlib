package com.plantuml.stdlibencoder;

import java.io.File;
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

import com.aayushatharva.brotli4j.encoder.Encoder;
import com.plantuml.stdlibencoder.brotli.BrotliUtils;

public class Main {

	final static Path raw = Paths.get("raw");
	final static Path output = Paths.get("output");

	public static void main(String[] args) throws IOException, InterruptedException {
		// Path to the "stdlib" directory
		final Path stdlibPath = Paths.get("stdlib");

		raw.toFile().mkdirs();
		output.toFile().mkdirs();
		deltree(raw.toFile());
		deltree(output.toFile());

		// Create a fixed thread pool to manage parallel tasks
		final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		final Collection<String> names = new TreeSet<String>();

		try (Stream<Path> paths = Files.list(stdlibPath)) { //
			paths.filter(Files::isDirectory) //
					.forEach(dir -> executor.submit(() -> {
						try {
							final String name = dir.getFileName().toString();
							new StdlibFolderBuilder(name, raw.toFile());
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

		Files.list(raw) //
				.filter(f -> f.getFileName().toString().endsWith(".repx")) //
				.parallel().forEach(p -> {
					try {
						brotli(p);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});

	}

	public static void deltree(final File dir) throws IOException {
		Files.list(dir.toPath()) //
				.filter(f -> f.getFileName().toString().endsWith(".repx")) //
				.forEach(p -> {
					try {
						Files.delete(p);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
	}

	public static void brotli(Path p) throws IOException {
		System.err.println(p);
		final byte data[] = Files.readAllBytes(p);
		System.err.println(data.length);

		final String name = p.getFileName().toString().replaceFirst("[a-z].repx", "x.repx");
		final Path outPath = output.resolve(name);
		System.err.println(outPath);

		final Encoder.Parameters params = new Encoder.Parameters().setQuality(11);
		final byte data2[] = BrotliUtils.compress(data, params);
		System.err.println(data2.length);
		Files.write(outPath, data2);
	}

}
