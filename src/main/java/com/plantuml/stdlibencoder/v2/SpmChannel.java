package com.plantuml.stdlibencoder.v2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public enum SpmChannel {
	INFO, //
	PUML, //
	JSON, //
	SPRITE, //
	SVG, //
	IMAGE;

	private String getFileName() {
		return name().toLowerCase().replace('_', '-') + ".spm";
	}

	public Path getPath(Path rootFolder, String name) throws IOException {
		final Path dir = rootFolder.resolve(name);
		return getPath(dir);
	}

	public Path getPath(Path dir) throws IOException {
		Files.createDirectories(dir);
		return dir.resolve(getFileName());
	}

}
