package com.plantuml.stdlibencoder.spm;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.aayushatharva.brotli4j.encoder.Encoder;
import com.plantuml.stdlibencoder.brotli.BrotliUtils;

public class MainSpm {

	final static Path raw = Paths.get("raw");
	final static Path output = Paths.get("output");

	public static void main(String[] args) throws IOException, InterruptedException {
		
	}

	public static void deltree(final Path dir) throws IOException {
		try (Stream<Path> walk = Files.walk(dir)) {
			walk.filter(p -> !p.equals(dir)) //
					.sorted(Comparator.reverseOrder()) //
					.forEach(p -> {
						try {
							Files.delete(p);
						} catch (IOException e) {
							throw new UncheckedIOException(e);
						}
					});
		}
	}

	public static void brotli(Path p) throws IOException {
		final byte data[] = Files.readAllBytes(p);
		final Path outPath = Paths.get("output").resolve(p.subpath(1, p.getNameCount()));
		Files.createDirectories(outPath.getParent());
		System.err.println("Writing " + outPath);

		final Encoder.Parameters params = new Encoder.Parameters().setQuality(11).setWindow(24);
		final byte data2[] = BrotliUtils.compress(data, params);
		Files.write(outPath, data2);
	}

	public static void computeSha1(Path p) throws IOException {
		final MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("SHA-1 not available", e);
		}

		try (InputStream in = Files.newInputStream(p); DigestInputStream din = new DigestInputStream(in, md)) {
			byte[] buffer = new byte[8192];
			while (din.read(buffer) != -1) {
				// DigestInputStream is automatically updated
			}
		}

		final byte[] digest = md.digest();
		final String hex = toHexLower(digest);

		final Path outPath = Paths.get("output").resolve(p.subpath(1, p.getNameCount()));
		Files.createDirectories(outPath.getParent());

		final Path shaPath = outPath
				.resolveSibling(outPath.getFileName().toString().replaceFirst("\\.spm$", "") + ".sha1");

		Files.writeString(shaPath, hex, StandardCharsets.US_ASCII, StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING);
	}

	final private static char[] HEX = "0123456789abcdef".toCharArray();

	private static String toHexLower(byte[] bytes) {
		final char[] out = new char[bytes.length * 2];
		for (int i = 0, j = 0; i < bytes.length; i++) {
			int v = bytes[i] & 0xFF;
			out[j++] = HEX[v >>> 4];
			out[j++] = HEX[v & 0x0F];
		}
		return new String(out);
	}


}
