package com.plantuml.stdlibencoder.uid;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Models a stdlib {@code README.md} file with a YAML front matter header.
 * <p>
 * The file format is:
 * 
 * <pre>
 * ---
 * key1: value1
 * key2: value2
 * ---
 * Body text...
 * </pre>
 * <p>
 * The YAML header fields are preserved in insertion order so that
 * round-tripping (read then write) keeps a stable layout.
 */
public class ReadmeMd {

	private final Map<String, String> headers = new LinkedHashMap<>();
	private final List<String> body = new ArrayList<>();
	private final Path path;

	/**
	 * Reads and parses a {@code README.md} file.
	 *
	 * @param path path to the README.md file
	 */
	public ReadmeMd(Path path) throws IOException {
		this.path = path;
		try {
			parse(Files.readAllLines(path, StandardCharsets.UTF_8));
		} catch (IOException e) {
			throw new IOException("Error reading " + path, e);
		}
	}

	private void parse(List<String> lines) throws IOException {
		if (lines.isEmpty() || !lines.get(0).trim().equals("---"))
			throw new IOException("README.md must start with a YAML header: " + path);

		int i = 1;
		while (i < lines.size()) {
			final String line = lines.get(i);
			i++;
			if (line.trim().equals("---"))
				break;

			final int colon = line.indexOf(':');
			if (colon == -1)
				throw new IOException("Invalid YAML line in " + path + ": " + line);

			final String key = line.substring(0, colon).trim();
			final String value = line.substring(colon + 1).trim();
			headers.put(key, value);
		}

		// Everything after the closing "---" is the body
		while (i < lines.size()) {
			body.add(lines.get(i));
			i++;
		}
	}

	/**
	 * Sets a YAML header field. If the key already exists, its value is updated
	 * in place. Otherwise the field is appended at the end of the header.
	 */
	public void set(String key, String value) {
		headers.put(key, value);
	}

	/**
	 * Writes the file back to its original path, preserving the YAML header field
	 * order and the body content.
	 */
	public void save() throws IOException {
		save(this.path);
	}

	/**
	 * Writes the file to the specified path.
	 */
	private void save(Path target) throws IOException {
		final List<String> lines = new ArrayList<>();
		lines.add("---");
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			if (entry.getValue().isEmpty())
				lines.add(entry.getKey() + ":");
			else
				lines.add(entry.getKey() + ": " + entry.getValue());
		}
		lines.add("---");
		lines.addAll(body);
		Files.write(target, lines, StandardCharsets.UTF_8);
	}

}
