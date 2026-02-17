package com.plantuml.stdlibencoder.js;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

	public static void main(String[] args) throws IOException, InterruptedException {
		final Path stdlibPath = Paths.get("stdlib");

		final Set<String> names;
		try (Stream<Path> paths = Files.list(stdlibPath)) {
			names = paths.filter(Files::isDirectory).parallel() //
					.map(path -> path.getFileName().toString()) //
					.map(name -> {
							new JsBuilder(name);
							return name.toLowerCase();
					}) //
					.collect(Collectors.toCollection(TreeSet::new));
		}

		System.err.println("names=" + names);
	}

}
