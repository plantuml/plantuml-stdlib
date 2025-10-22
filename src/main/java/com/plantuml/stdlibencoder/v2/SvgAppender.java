package com.plantuml.stdlibencoder.v2;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class SvgAppender {

	private final Map<String, String> sprites = new TreeMap<>();
	private final Path path;

	public SvgAppender(Path path) throws IOException {
		Files.createDirectories(path);
		this.path = path;
	}

	public void close() throws IOException {
		if (sprites.size() == 0)
			return;

		try (OutputStream os = Files.newOutputStream(SpmChannel.SVG.getPath(path));
				DataOutputStream dos = new DataOutputStream(os)) {
			dos.writeInt(sprites.size());
			for (Entry<String, String> ent : sprites.entrySet()) {
				dos.writeUTF(ent.getKey());
				dos.writeUTF(ent.getValue());
			}
		}
	}

	public void appendSvg(String spriteName, String svg) throws IOException {
		sprites.put(spriteName, svg);
	}

}
