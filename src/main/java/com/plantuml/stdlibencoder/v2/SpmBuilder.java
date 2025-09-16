package com.plantuml.stdlibencoder.v2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import net.sourceforge.plantuml.klimt.creole.atom.AtomImg;
import net.sourceforge.plantuml.klimt.sprite.SpriteGrayLevel;
import net.sourceforge.plantuml.klimt.sprite.SpriteMonochrome;

public class SpmBuilder {

	public static final String DATA_IMAGE_PNG_SPM = "data:image/png;spm ";

	private final Path stdlib;
	private final String libName;

	private SolidFolderWriter pumlWriter;
	private SolidFolderWriter jsonWriter;
	private SpriteAppender spriteAppender;
	private ImageAppender imageAppender;
	private SvgAppender svgAppender;

	public SpmBuilder(String name) throws IOException {
		System.err.println("Starting " + name);
		this.stdlib = Paths.get("stdlib", name);
		this.libName = name.toLowerCase();
		final Path raw = Paths.get("raw", libName);
		// System.err.println("stdlib=" + stdlib + " raw=" + raw);

		final String infoString = readInfo(stdlib.resolve("README.md"));

		final Path pathInfo = SpmChannel.INFO.getPath(raw);
		Files.write(pathInfo, infoString.getBytes(StandardCharsets.UTF_8));

		if (infoString.contains("link=")) {
			return;
		}

		pumlWriter = new SolidFolderWriter(raw, SpmChannel.PUML);
		jsonWriter = new SolidFolderWriter(raw, SpmChannel.JSON);

		spriteAppender = new SpriteAppender(raw);
		imageAppender = new ImageAppender(raw);
		svgAppender = new SvgAppender(raw);

		processDir(stdlib);

		jsonWriter.close();
		pumlWriter.close();
		spriteAppender.close();
		imageAppender.close();
		svgAppender.close();
		System.err.println("  Ending " + name);

	}

	private void processDir(Path dir) throws IOException {
		try (Stream<Path> stream = Files.list(dir).sorted(Comparator.comparing(p -> p.getFileName().toString()))) {
			stream.forEach(p -> {
				try {
					if (Files.isRegularFile(p)) {
						final String name = p.getFileName().toString();
						if (name.endsWith(".puml"))
							processSinglePumlFile(p);
						else if (name.endsWith(".json"))
							processSingleJsonFile(p);

					} else if (Files.isDirectory(p) && isUnderscored(p.getFileName().toString()) == false)
						processDir(p);

				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			});
		}
	}

	private boolean isUnderscored(String s) {
		return s.startsWith("_") && s.endsWith("_");
	}

	private void processSingleJsonFile(Path f) throws IOException {
		String name = f.toString().replace('\\', '/');
		int x = name.indexOf('/');
		x = name.indexOf('/', x + 1);
		name = name.substring(x + 1);

		final byte[] json = Files.readAllBytes(f);

		jsonWriter.putContent(name, json);

	}

	private void processSinglePumlFile(Path f) throws IOException {

		String fullpath = f.toString().replace('\\', '/').replaceFirst("\\.puml$", "");
		int x = fullpath.indexOf('/');
		x = fullpath.indexOf('/', x + 1);
		fullpath = fullpath.substring(x + 1);
		// System.err.println("processSinglePumlFile " + name);

		final StringBuilder sb = new StringBuilder();
		final List<String> puml = Files.readAllLines(f);
		for (Iterator<String> it = puml.iterator(); it.hasNext();) {
			String s = it.next();

			if (s.contains("https://raw.githubusercontent"))
				throw new IOException(f + " " + s);

			if (s.contains(AtomImg.DATA_IMAGE_PNG_BASE64)) {
				final int x1 = s.indexOf(AtomImg.DATA_IMAGE_PNG_BASE64);
				final int x2 = s.lastIndexOf(AtomImg.DATA_IMAGE_PNG_BASE64);
				if (x1 != x2)
					throw new IllegalStateException(s);

				final int id = imageAppender.exportDataImagePngBase64(s);

				s = s.replaceAll(AtomImg.DATA_IMAGE_PNG_BASE64 + "([0-9a-zA-Z+/]+[=]*)",
						DATA_IMAGE_PNG_SPM + libName + "/" + id);
			}

			if (isSpriteSvgLine(s)) {
				final String exportedName = fullpath.toLowerCase() + ":" + getSpriteName(s);

				if (!s.endsWith("</svg>")) {
					s = readMultilineSvg(it, s);
				}
				final int idx = s.indexOf("<svg");
				final String svg = s.substring(idx);

				svgAppender.appendSvg(exportedName, svg);

				s = s.substring(0, idx).trim();
				s = s + " :" + libName + ":" + exportedName;
				s = s.replace("  ", " ");

				// System.err.println("svg=" + num + " " + svg);

			} else if (isSpriteLine(s)) {
				final String exportedName = fullpath.toLowerCase() + ":" + getSpriteName(s);
				// Sprite are not stored in the text output stream
				if (s.contains("/16z]")) {
					if (s.endsWith("{") == false)
						throw new IllegalStateException(s);
					s = s.substring(0, s.length() - 1);
					// System.err.println("s1=" + s);
					// We cheat here because sprites are never stored compressed
					s = s.replace("/16z]", "/16]");
					final Pattern p = Pattern.compile("\\[(\\d+)x(\\d+)");
					final Matcher m = p.matcher(s);
					if (m.find() == false)
						throw new IllegalStateException(s);

					final int width = Integer.parseInt(m.group(1));
					final int height = Integer.parseInt(m.group(2));

					manageSpriteCompressed(exportedName, width, height, it);
					s = s.replaceFirst("\\[(.*)\\]", "");
					s = s + "#" + libName + "#" + exportedName;
					s = s.replace("  ", " ");
				} else if (s.contains("/16]")) {
					manageSpriteNormalUncompressed(exportedName, it);
					if (s.endsWith("{") == false)
						throw new IllegalStateException(s);
					s = s.substring(0, s.length() - 1);
					s = s.replaceFirst("\\[(.*)\\]", "");
					s = s + "#" + libName + "#" + exportedName;
					s = s.replace("  ", " ");
				} else {
					throw new UnsupportedOperationException("Those sprites are not supported (yet) " + s);
				}

			}

			sb.append(s);
			sb.append("\n");

		}

		pumlWriter.putContent(fullpath, sb.toString());
	}

	private String readMultilineSvg(Iterator<String> it, String def) {
		final StringBuilder sb = new StringBuilder(def.trim());
		while (true) {
			final String s = it.next().trim();
			sb.append(s);
			if (s.endsWith("</svg>"))
				return sb.toString();

		}

	}

	private void manageSpriteNormalUncompressed(String spriteName, Iterator<String> it) throws IOException {
		final List<String> strings = new ArrayList<String>();
		while (true) {
			String s = it.next();
			if (s.trim().equals("}")) {
				final SpriteMonochrome sprite = (SpriteMonochrome) SpriteGrayLevel.GRAY_16.buildSprite(-1, -1, strings);
				spriteAppender.appendSprite(spriteName, sprite);
				return;
			}
			strings.add(s.trim());
		}
	}

	private void manageSpriteCompressed(String spriteName, int width, int height, Iterator<String> it)
			throws IOException {
		final StringBuilder definition = new StringBuilder();
		while (true) {
			String s = it.next();
			if (s.trim().equals("}")) {
				final SpriteMonochrome sprite = (SpriteMonochrome) SpriteGrayLevel.GRAY_16.buildSpriteZ(width, height,
						definition.toString());
				if (sprite == null) {
					System.err.println("ERROR in " + definition);
					System.err.println("ERROR in " + s);
					SpriteGrayLevel.GRAY_16.buildSpriteZ(width, height, definition.toString());
					throw new IOException("Error in sprite");
				}
				spriteAppender.appendSprite(spriteName, sprite);
				return;
			}
			definition.append(s.trim());
		}
	}

	private boolean isSpriteLine(String s) {
		s = s.trim();
		return s.startsWith("sprite") && s.endsWith("{");
	}

	private boolean isSpriteSvgLine(String s) {
		s = s.trim();
		return s.startsWith("sprite") && s.contains("<svg");
	}

	private String getSpriteName(String s) {
		String name = s.split("\\s+")[1];
		if (name.startsWith("$"))
			name = name.substring(1);
		return name;
	}

	private String readInfo(Path file) throws IOException {

		final StringBuilder result = new StringBuilder();

		try (BufferedReader br = new BufferedReader(new FileReader(file.toFile()))) {
			String line = br.readLine().trim();
			if (line.equals("---") == false)
				throw new IOException("README.md must have a YAML header");

			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.equals("---"))
					return result.toString();

				final int colon = line.indexOf(':');
				if (colon == -1)
					throw new IOException("Invalid YAML line: " + line);

				final String key = line.substring(0, colon).trim();
				final String value = line.substring(colon + 1).trim();
				result.append(key).append('=').append(value).append('\n');
			}
		}
		throw new IOException("Bad YAML header in README.md");
	}

}
