package com.plantuml.stdlibencoder.v2;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.plantuml.stdlibencoder.brotli.Lazy;

public class SvgAppender {

	private final Lazy<DataOutputStream> toc;
	private final Lazy<DataOutputStream> dat;
	private int counter;

	public SvgAppender(Path path) throws IOException {
		Files.createDirectories(path);

		this.toc = new Lazy<>(() -> {
			try {
				return new DataOutputStream(Files.newOutputStream(SpmChannel.SVG_TAB.getPath(path)));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
		this.dat = new Lazy<>(() -> {
			try {
				return new DataOutputStream(Files.newOutputStream(SpmChannel.SVG_DAT.getPath(path)));
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

	public int appendSvg(String svg) throws IOException {
		final byte data[] = svg.getBytes(StandardCharsets.UTF_8);
		toc.get().writeInt(data.length);
		dat.get().write(data);

		return counter++;
	}

}
