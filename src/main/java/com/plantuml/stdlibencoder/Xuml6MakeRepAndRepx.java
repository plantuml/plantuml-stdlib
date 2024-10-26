package com.plantuml.stdlibencoder;

import java.io.File;
import java.io.IOException;

public class Xuml6MakeRepAndRepx {

	public Xuml6MakeRepAndRepx(String name, int diff) throws IOException {
		final File dir = new File(pathRaw(), name);
		new StdlibBuilder6(dir, diff);
	}

	private static File pathRaw() {
		return new File(".");
	}
}
