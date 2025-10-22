package com.plantuml.stdlibencoder.v2;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import net.sourceforge.plantuml.klimt.sprite.SpriteMonochrome;

public class SpriteAppender {

	private final Path path;
	private final Map<String, SpriteMonochrome> sprites = new TreeMap<>();

	public SpriteAppender(Path path) throws IOException {
		Files.createDirectories(path);

		this.path = path;
	}

	public void close() throws IOException {
		if (sprites.size() == 0)
			return;

		try (OutputStream os = Files.newOutputStream(SpmChannel.SPRITE.getPath(path));
				DataOutputStream dos = new DataOutputStream(os)) {
			dos.writeInt(sprites.size());
			for (Entry<String, SpriteMonochrome> ent : sprites.entrySet()) {
				final SpriteMonochrome sprite = ent.getValue();
				dos.writeUTF(ent.getKey());
				dos.writeInt(sprite.getWidth());
				dos.writeInt(sprite.getHeight());
				sprite.exportSprite1(dos);

			}
		}

	}

	public void appendSprite(String spriteName, SpriteMonochrome sprite) throws IOException {
		sprites.put(spriteName, sprite);
	}

}
