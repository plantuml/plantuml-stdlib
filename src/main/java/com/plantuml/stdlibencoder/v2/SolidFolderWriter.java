package com.plantuml.stdlibencoder.v2;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class SolidFolderWriter {

	private final DataOutputStream toc;
	private final DataOutputStream dat;

	public SolidFolderWriter(Path path, String name) throws IOException {
		Files.createDirectories(path);

		this.toc = new DataOutputStream(Files.newOutputStream(path.resolve(name + "-toc.spm")));
		this.dat = new DataOutputStream(Files.newOutputStream(path.resolve(name + "-dat.spm")));
	}

	public void close() throws IOException {
		this.toc.close();
		this.dat.close();
	}

	public void putContent(String title, byte[] content) throws IOException {
		this.toc.writeUTF(title);
		this.toc.writeInt(content.length);
		this.dat.write(content, 0, content.length);
	}

	public void putContent(String title, String content) throws IOException {
		putContent(title, content.getBytes(StandardCharsets.UTF_8));
	}

}
