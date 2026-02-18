package com.plantuml.stdlibencoder.uid;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import com.plantuml.stdlibencoder.Util;

/**
 * Computes a stable SHA-1 digest for a single stdlib library directory.
 * <p>
 * The digest covers all {@code .puml} and {@code .json} files (excluding files
 * whose name starts with an underscore). For {@code .puml} files, the content
 * is filtered through {@link Util#readAllLine(Path)} so that comments and
 * markers do not affect the hash. For {@code .json} files, raw bytes are used.
 * <p>
 * Stability is ensured by:
 * <ul>
 * <li>Sorting files in deterministic alphabetical order</li>
 * <li>Including the relative file path in the digest</li>
 * <li>Using UTF-8 encoding throughout</li>
 * </ul>
 */
public class UidBuilder {

	private final Path stdlibDir;
	private final MessageDigest digest;
	private final String sha1;
	private int fileCount;

	public UidBuilder(String name) {
		this.stdlibDir = Paths.get("stdlib", name);
		try {
			this.digest = MessageDigest.getInstance("SHA-1");
			processDir(stdlibDir);
			this.sha1 = fileCount == 0 ? "0" : bytesToHex(digest.digest());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * Returns the computed SHA-1 hex string.
	 */
	public String getSha1() {
		return sha1;
	}

	private void processDir(Path dir) throws IOException {
		try (Stream<Path> stream = Files.list(dir).sorted(Comparator.comparing(p -> p.getFileName().toString()))) {
			stream.forEach(p -> {
				try {
					if (Files.isRegularFile(p)) {
						final String fileName = p.getFileName().toString();
						if (fileName.startsWith("_"))
							return;
						if (fileName.endsWith(".puml"))
							feedPumlFile(p);
						else if (fileName.endsWith(".json"))
							feedJsonFile(p);
					} else if (Files.isDirectory(p) && !isUnderscored(p.getFileName().toString())) {
						processDir(p);
					}
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			});
		}
	}

	private boolean isUnderscored(String s) {
		return s.startsWith("_") && s.endsWith("_");
	}

	private void feedPumlFile(Path pumlFile) throws IOException {
		fileCount++;
		feedRelativePath(pumlFile);
		final List<String> lines = Util.readAllLine(pumlFile);
		for (String line : lines) {
			digest.update(line.getBytes(StandardCharsets.UTF_8));
			digest.update((byte) '\n');
		}
	}

	private void feedJsonFile(Path jsonFile) throws IOException {
		fileCount++;
		feedRelativePath(jsonFile);
		final byte[] content = Files.readAllBytes(jsonFile);
		digest.update(content);
	}

	/**
	 * Feeds the normalized relative path into the digest, so that renaming or
	 * moving a file changes the hash even if its content stays the same.
	 */
	private void feedRelativePath(Path file) {
		final String relativePath = stdlibDir.relativize(file).toString().replace('\\', '/');
		digest.update(relativePath.getBytes(StandardCharsets.UTF_8));
	}

	private static String bytesToHex(byte[] bytes) {
		final StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) {
			sb.append(String.format("%02x", b & 0xff));
		}
		return sb.toString();
	}

}
