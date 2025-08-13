package com.plantuml.stdlibencoder.v2;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class SolidFolderWriter {

	private final OutputStream dat;
	private final DataOutputStream toc;

	public SolidFolderWriter(Path root, SpmChannel dat, SpmChannel toc) throws IOException {
		Files.createDirectories(root);

//		this.toc = new DataOutputStream(Files.newOutputStream(root.resolve(name + "-toc.spm")));
//		this.dat = new DataOutputStream(Files.newOutputStream(root.resolve(name + "-dat.spm")));
		this.dat = Files.newOutputStream(dat.getPath(root));
		this.toc = new DataOutputStream(Files.newOutputStream(toc.getPath(root)));
	}

	public void close() throws IOException {
		this.toc.writeUTF("");
		this.toc.close();
		this.dat.close();
	}

	public void putContent(String title, byte[] content) throws IOException {
		this.dat.write(content, 0, content.length);
		this.toc.writeUTF(title.toLowerCase());
		this.toc.writeInt(content.length);
	}

	public void putContent(String title, String content) throws IOException {
		putContent(title, content.getBytes(StandardCharsets.UTF_8));
	}

}
