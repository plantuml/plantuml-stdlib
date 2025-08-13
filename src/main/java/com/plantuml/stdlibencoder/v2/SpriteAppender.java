package com.plantuml.stdlibencoder.v2;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.plantuml.stdlibencoder.brotli.Lazy;

import net.sourceforge.plantuml.klimt.sprite.SpriteMonochrome;

public class SpriteAppender {

	private final Lazy<DataOutputStream> toc;
	private final Lazy<DataOutputStream> dat;
	private int counter;

	public SpriteAppender(Path path) throws IOException {
		Files.createDirectories(path);

		this.toc = new Lazy<>(() -> {
			try {
				return new DataOutputStream(Files.newOutputStream(SpmChannel.SPRITE_TAB.getPath(path)));
				// return new DataOutputStream(Files.newOutputStream(path.resolve("sprite-toc.spm")));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
		this.dat = new Lazy<>(() -> {
			try {
				return new DataOutputStream(Files.newOutputStream(SpmChannel.SPRITE_DAT.getPath(path)));
				// return new DataOutputStream(Files.newOutputStream(path.resolve("sprite-dat.spm")));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	public void close() throws IOException {
		if (counter > 0) {
			this.toc.get().writeInt(0);
			this.toc.get().close();
			this.dat.get().close();
		}
	}

	public int appendSprite(SpriteMonochrome sprite) throws IOException {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		sprite.exportSprite1(baos);
		final byte data[] = baos.toByteArray();
		toc.get().writeInt(data.length);
		dat.get().write(data);

		return counter++;
	}

}
