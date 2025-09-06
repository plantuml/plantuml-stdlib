package com.plantuml.stdlibencoder.v2;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class SolidFolderWriter {

	private final Path root;
	private final SpmChannel channel;
	private final Map<String, byte[]> data = new TreeMap<>();

	public SolidFolderWriter(Path root, SpmChannel channel) throws IOException {
		Files.createDirectories(root);
		this.channel = channel;
		this.root = root;

	}

	public void close() throws IOException {
		if (data.size() == 0)
			return;
		try (final DataOutputStream out = new DataOutputStream(Files.newOutputStream(channel.getPath(root)))) {
			out.writeInt(data.size());
			for (Entry<String, byte[]> ent : data.entrySet()) {
				out.writeUTF(ent.getKey());
				final byte[] bytes = ent.getValue();
				out.writeInt(bytes.length);
				out.write(bytes, 0, bytes.length);
			}

		}
	}

	public void putContent(String title, byte[] content) throws IOException {
		this.data.put(title, content);
		// this.dat.write(content, 0, content.length);
	}

	public void putContent(String title, String content) throws IOException {
		putContent(title, content.getBytes(StandardCharsets.UTF_8));
	}

}
