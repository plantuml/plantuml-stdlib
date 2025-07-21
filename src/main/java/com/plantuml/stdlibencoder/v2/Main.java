package com.plantuml.stdlibencoder.v2;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Comparator;
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

		Files.createDirectories(raw);
		Files.createDirectories(output);
		deltree(raw);
		deltree(output);

		// Create a fixed thread pool to manage parallel tasks
		final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		final Collection<String> names = new TreeSet<String>();

		try (Stream<Path> paths = Files.list(stdlibPath)) { //
			paths.filter(Files::isDirectory) //
					.forEach(dir -> executor.submit(() -> {
						try {
							final String name = dir.getFileName().toString();
							new SpmBuilder(name);
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
		final Path filePath = Paths.get("output", "home.spm");
		Files.writeString(filePath, String.join("\n", names));

		Files.walk(raw).filter(f -> Files.isRegularFile(f)) //
				.filter(f -> f.getFileName().toString().endsWith(".spm")) //
				.parallel() //
				.forEach(p -> {
					try {
						brotli(p);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});

	}

	public static void deltree(final Path dir) throws IOException {
		try (Stream<Path> walk = Files.walk(dir)) {
			walk.filter(p -> !p.equals(dir)) //
					.sorted(Comparator.reverseOrder()) //
					.forEach(p -> {
						try {
							Files.delete(p);
						} catch (IOException e) {
							throw new UncheckedIOException(e);
						}
					});
		}
	}

	public static void brotli(Path p) throws IOException {
		final byte data[] = Files.readAllBytes(p);
		final Path outPath = Paths.get("output").resolve(p.subpath(1, p.getNameCount()));
		Files.createDirectories(outPath.getParent());
		System.err.println(outPath);

		final Encoder.Parameters params = new Encoder.Parameters().setQuality(11);
		final byte data2[] = BrotliUtils.compress(data, params);
		System.err.println(data2.length);
		Files.write(outPath, data2);
	}

}
