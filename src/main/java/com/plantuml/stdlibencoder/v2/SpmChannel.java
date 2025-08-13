package com.plantuml.stdlibencoder.v2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public enum SpmChannel {
	INFO, //
	TEXT_DAT, //
	TEXT_TOC, //
	JSON_DAT, //
	JSON_TOC, //
	SPRITE_DAT, //
	SPRITE_TAB, //
	SVG_DAT, //
	SVG_TAB, //
	IMAGE_DAT, //
	IMAGE_TAB, //
	IMAGE_COL;

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
