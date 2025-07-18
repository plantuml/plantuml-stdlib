package com.plantuml.stdlibencoder;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

import com.plantuml.stdlibencoder.brotli.Lazy;

public class JsonStream {

	private final Lazy<DataOutputStream> out;

	public JsonStream(Lazy<DataOutputStream> out) throws FileNotFoundException {
		this.out = out;
	}

	public void close() throws IOException {
		if (out.isInitialized())
			out.get().close();
	}

	public void append(File jsonFile) throws IOException {
		String path = jsonFile.getPath().replace('\\', '/');
		int x = path.indexOf('/');
		x = path.indexOf('/', x + 1);
		path = path.substring(x + 1);

		out.get().writeUTF(path);

		final byte data[] = Files.readAllBytes(jsonFile.toPath());
		out.get().writeShort(data.length);
		out.get().write(data, 0, data.length);

	}

}
