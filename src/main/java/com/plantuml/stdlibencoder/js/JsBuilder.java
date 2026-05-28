package com.plantuml.stdlibencoder.js;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import com.plantuml.stdlibencoder.Util;
import com.plantuml.stdlibencoder.spm.SpmBuilder;

/**
 * Builds a single JS file for a stdlib library.
 * <p>
 * The generated file registers all .puml files into
 * {@code window.PLANTUML_STDLIB["libname"]["stdlib/libname/path.puml"]} as
 * arrays of lines, ready for use by the TeaVM-based browser version.
 */
public class JsBuilder {

	private final Path stdlib;
	private final String libName;
	private final StringBuilder sb = new StringBuilder();

	public JsBuilder(String name) throws IOException {
		this.libName = name.toLowerCase();
		this.stdlib = Paths.get("stdlib", name);

		final String infoStringJson = readInfoJson(stdlib.resolve("README.md"));

		try {
			build(infoStringJson);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private String readInfoJson(Path file) throws IOException {

		final StringBuilder result = new StringBuilder();
		result.append('{');
		boolean first = true;

		try (BufferedReader br = new BufferedReader(new FileReader(file.toFile()))) {
			String line = br.readLine().trim();
			if (line.equals("---") == false)
				throw new IOException("README.md must have a YAML header");

			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.equals("---")) {
					result.append('}');
					return result.toString();
				}

				final int colon = line.indexOf(':');
				if (colon == -1)
					throw new IOException("Invalid YAML line: " + line);

				final String key = line.substring(0, colon).trim();
				final String value = line.substring(colon + 1).trim();

				if (first == false)
					result.append(',');
				first = false;

				result.append('"').append(escapeJsonString(key)).append('"');
				result.append(':');
				result.append('"').append(escapeJsonString(value)).append('"');
			}
		}
		throw new IOException("Bad YAML header in README.md");
	}

	private static String escapeJsonString(String s) {
		final StringBuilder out = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			final char c = s.charAt(i);
			switch (c) {
			case '\\':
				out.append("\\\\");
				break;
			case '"':
				out.append("\\\"");
				break;
			case '\n':
				out.append("\\n");
				break;
			case '\r':
				out.append("\\r");
				break;
			case '\t':
				out.append("\\t");
				break;
			default:
				out.append(c);
			}
		}
		return out.toString();
	}

	private void build(String infoStringJson) throws IOException {
		sb.append("// stdlib/").append(libName).append(".js\n");
		sb.append("(function () {\n");

		sb.append("  window.PLANTUML_STDLIB_JSON = window.PLANTUML_STDLIB_JSON || {};\n");
		sb.append("  window.PLANTUML_STDLIB_JSON[\"").append(libName).append("\"] = window.PLANTUML_STDLIB_JSON[\"")
				.append(libName).append("\"] || {};\n");

		sb.append("  window.PLANTUML_STDLIB_INFO = window.PLANTUML_STDLIB_INFO || {};\n");
		sb.append("  window.PLANTUML_STDLIB_INFO[\"").append(libName).append("\"] = ").append(infoStringJson)
				.append(";\n");

		sb.append("  window.PLANTUML_STDLIB = window.PLANTUML_STDLIB || {};\n");
		sb.append("  window.PLANTUML_STDLIB[\"").append(libName).append("\"] = window.PLANTUML_STDLIB[\"")
				.append(libName).append("\"] || {};\n");

		processDir(stdlib);

		sb.append("})();\n");

		final Path outputDir = Paths.get("output-js");
		Files.createDirectories(outputDir);
		final Path outputFile = outputDir.resolve(libName + ".js");
		Files.writeString(outputFile, sb.toString(), StandardCharsets.UTF_8);
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
					} else if (Files.isDirectory(p) && !isUnderscored(p.getFileName().toString())) {
						processDir(p);
					}
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			});
		}
	}

	private boolean isUnderscored(String s) {
		return s.startsWith("_") && s.endsWith("_");
	}

	private void appendJsonFile(Path jsonFile) throws IOException {
		final Path relative = stdlib.relativize(jsonFile);
		final String key = relative.toString().replace('\\', '/').toLowerCase().replaceAll("\\.json$", "");
		final List<String> lines = Util.readAllLine(jsonFile);

		sb.append("\n  window.PLANTUML_STDLIB_JSON[\"").append(libName).append("\"][\"");
		sb.append(key).append("\"] = \n");
		for (String s : lines)
			sb.append(s + "\n");
		sb.append(";\n");

	}

	private void appendPumlFile(Path pumlFile) throws IOException {
		// Build the key: "stdlib/libname/relative/path.puml"
		final Path relative = stdlib.relativize(pumlFile);
		final String key = relative.toString().replace('\\', '/').toLowerCase().replaceAll("\\.puml$", "");

		final List<String> lines = Util.readAllLine(pumlFile);

		sb.append("\n  window.PLANTUML_STDLIB[\"").append(libName).append("\"][\"");
		sb.append(key).append("\"] = [\n");

		for (int i = 0; i < lines.size(); i++) {
			sb.append("    ");
			sb.append(escapeJsString(lines.get(i)));
			if (i < lines.size() - 1)
				sb.append(",");
			sb.append("\n");
		}

		sb.append("  ];\n");
	}

	/**
	 * Escapes a string for use as a JavaScript string literal (double-quoted).
	 */
	private static String escapeJsString(String s) {
		final StringBuilder out = new StringBuilder();
		out.append('"');
		for (int i = 0; i < s.length(); i++) {
			final char c = s.charAt(i);
			switch (c) {
			case '\\':
				out.append("\\\\");
				break;
			case '"':
				out.append("\\\"");
				break;
			case '\n':
				out.append("\\n");
				break;
			case '\r':
				out.append("\\r");
				break;
			case '\t':
				out.append("\\t");
				break;
			default:
				out.append(c);
			}
		}
		out.append('"');
		return out.toString();
	}

}
