package com.plantuml.stdlibencoder.js;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import com.plantuml.stdlibencoder.Util;
import com.plantuml.stdlibencoder.spm.SpmBuilder;

import net.sourceforge.plantuml.json.Json;

/**
 * Builds a single JSON data bundle for a stdlib library: the same content as
 * the JS bundle produced by {@link JsBuilder}, but as pure data.
 *
 * <pre>
 * {
 *   "info":  { ... README.md YAML header ... },
 *   "files": { "path/to/file": ["line", "line", ...], ... },
 *   "json":  { "path/to/file": { ... }, ... }
 * }
 * </pre>
 *
 * A host fetches this and populates {@code PLANTUML_STDLIB},
 * {@code PLANTUML_STDLIB_JSON} and {@code PLANTUML_STDLIB_INFO} itself (the
 * TeaVM engine's {@code PLANTUML_STDLIB_LOADER} hook exists for exactly that).
 * The JS bundle cannot serve those hosts: a browser extension may fetch remote
 * data but not execute remote code (Chrome MV3 and Mozilla AMO rules), and a
 * Web Worker has no document to load a script tag into. The content is
 * identical to the JS bundle's because both are written through the same
 * {@link JsBuilder#escapeJsonString(String)} escape set and the same
 * {@link JsBuilder#readInfoJson(Path)} info map.
 *
 * The file goes to {@code output-js/<libname>.json}, next to the JS bundle,
 * so the publishing step that carries {@code <libname>.min.js} to the site
 * carries the JSON along unchanged.
 */
public class JsonBuilder {

	private final Path stdlib;
	private final String libName;
	private final StringBuilder files = new StringBuilder();
	private final StringBuilder json = new StringBuilder();

	public JsonBuilder(String name) throws IOException {
		this(Paths.get("stdlib"), Paths.get("output-js"), name);
	}

	// Roots are parameters so tests can run against a temporary tree.
	public JsonBuilder(Path stdlibRoot, Path outputDir, String name) throws IOException {
		this.libName = name.toLowerCase();
		this.stdlib = stdlibRoot.resolve(name);

		final String infoStringJson = JsBuilder.readInfoJson(stdlib.resolve("README.md"));

		processDir(stdlib);

		final StringBuilder sb = new StringBuilder();
		sb.append("{\"info\":").append(infoStringJson);
		sb.append(",\"files\":{").append(files).append('}');
		sb.append(",\"json\":{").append(json).append('}');
		sb.append('}');

		final String result = sb.toString();
		// A bundle that does not parse must fail the build, not get published.
		// Two things could break it: a control character the escape set does
		// not cover, or a malformed embedded .json data file.
		try {
			Json.parse(result);
		} catch (Exception e) {
			throw new IOException("Generated " + libName + ".json is not valid JSON: " + e.getMessage(), e);
		}

		Files.createDirectories(outputDir);
		final Path outputFile = outputDir.resolve(libName + ".json");
		Files.writeString(outputFile, result, StandardCharsets.UTF_8);
		System.err.println("Written " + outputFile);
	}

	private void processDir(Path dir) throws IOException {
		try (Stream<Path> stream = Files.list(dir).sorted(Comparator.comparing(p -> p.getFileName().toString()))) {
			stream.forEach(p -> {
				try {
					if (Files.isRegularFile(p)) {
						final String fileName = p.getFileName().toString();
						if (fileName.endsWith(".puml"))
							appendPumlFile(p);
						else if (fileName.endsWith(".json"))
							appendJsonFile(p);
					} else if (Files.isDirectory(p)
							&& SpmBuilder.isSkippedFolder(p.getFileName().toString()) == false) {
						processDir(p);
					}
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			});
		}
	}

	private String key(Path file, String extension) {
		final Path relative = stdlib.relativize(file);
		return relative.toString().replace('\\', '/').toLowerCase().replaceAll("\\." + extension + "$", "");
	}

	private void appendPumlFile(Path pumlFile) throws IOException {
		final List<String> lines = Util.readAllLine(pumlFile);

		if (files.length() > 0)
			files.append(',');
		files.append('"').append(JsBuilder.escapeJsonString(key(pumlFile, "puml"))).append("\":[");
		for (int i = 0; i < lines.size(); i++) {
			if (i > 0)
				files.append(',');
			files.append('"').append(JsBuilder.escapeJsonString(lines.get(i))).append('"');
		}
		files.append(']');
	}

	private void appendJsonFile(Path jsonFile) throws IOException {
		// The data file is JSON already; embed it verbatim, the same trust
		// JsBuilder places in it when embedding it as a JS expression.
		final String content = Files.readString(jsonFile, StandardCharsets.UTF_8);

		if (json.length() > 0)
			json.append(',');
		json.append('"').append(JsBuilder.escapeJsonString(key(jsonFile, "json"))).append("\":");
		json.append(content.trim());
	}

}
