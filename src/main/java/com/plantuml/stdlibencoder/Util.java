package com.plantuml.stdlibencoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Shared utility methods for stdlib encoding tools.
 */
public class Util {

	/**
	 * Reads all lines from a .puml file, filtering out comments and
	 * {@code @startuml}/{@code @enduml} markers.
	 * <p>
	 * Filtered lines include:
	 * <ul>
	 * <li>Block comments ({@code /'} ... {@code '/})</li>
	 * <li>Single-line comments (lines starting with {@code '})</li>
	 * <li>{@code @startuml} and {@code @enduml} directives</li>
	 * </ul>
	 *
	 * @param pumlFile path to the .puml file
	 * @return filtered list of lines
	 */
	public static List<String> readAllLine(Path pumlFile) throws IOException {
		final List<String> raw = Files.readAllLines(pumlFile, StandardCharsets.UTF_8);
		final List<String> result = new ArrayList<>();
		boolean inBlockComment = false;
		for (String line : raw) {
			final String trimmed = line.trim();
			if (inBlockComment) {
				if (trimmed.contains("'/"))
					inBlockComment = false;
				continue;
			}
			if (trimmed.startsWith("/'")) {
				inBlockComment = !trimmed.endsWith("'/");
				continue;
			}
			if (isFilteredLine(trimmed))
				continue;
			result.add(line);
		}
		return result;
	}

	/**
	 * Returns true if the line should be filtered out:
	 * <ul>
	 * <li>{@code @startuml} or {@code @enduml}</li>
	 * <li>PlantUML comments (lines starting with a single quote)</li>
	 * </ul>
	 */
	private static boolean isFilteredLine(String trimmed) {
		if (trimmed.equals("@startuml") || trimmed.equals("@enduml"))
			return true;
		if (trimmed.startsWith("'"))
			return true;
		return false;
	}

}
