package com.plantuml.stdlibencoder.brotli;

import java.io.IOException;

import com.aayushatharva.brotli4j.Brotli4jLoader;
import com.aayushatharva.brotli4j.encoder.Encoder;

public final class BrotliUtils {

	static {
		Brotli4jLoader.ensureAvailability();
	}

	private BrotliUtils() {
	}

	public static byte[] compress(byte[] input) throws IOException {
		final Encoder.Parameters params = new Encoder.Parameters();
		return compress(input, params);
	}

	public static byte[] compress(byte[] input, Encoder.Parameters params) throws IOException {
		final byte[] result = Encoder.compress(input, params);
		if (result == null)
			throw new RuntimeException("Brotli compression failure.");

		return result;
	}
}
