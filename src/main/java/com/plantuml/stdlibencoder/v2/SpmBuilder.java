package com.plantuml.stdlibencoder.v2;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.plantuml.stdlibencoder.brotli.Lazy;

import net.sourceforge.plantuml.klimt.creole.atom.AtomImg;
import net.sourceforge.plantuml.klimt.sprite.SpriteGrayLevel;
import net.sourceforge.plantuml.klimt.sprite.SpriteMonochrome;

public class SpmBuilder {

	public static final String DATA_IMAGE_PNG_SPM = "data:image/png;spm ";

	private final Path stdlib;
	private final String libName;

	private SolidFolderWriter pumlWriter;
	private Lazy<SolidFolderWriter> jsonWriter;
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

		pumlWriter = new SolidFolderWriter(raw, SpmChannel.TEXT_DAT, SpmChannel.TEXT_TOC);
		jsonWriter = new Lazy<>(() -> {
			try {
				return new SolidFolderWriter(raw, SpmChannel.JSON_DAT, SpmChannel.JSON_TOC);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
		spriteAppender = new SpriteAppender(raw);
		imageAppender = new ImageAppender(raw);
		svgAppender = new SvgAppender(raw);

		processDir(stdlib);

		if (jsonWriter.isInitialized())
			jsonWriter.get().close();
		pumlWriter.close();
		spriteAppender.close();
		imageAppender.close();
		svgAppender.close();
		System.err.println("  Ending " + name);

	}

	private void processDir(Path dir) throws IOException {
		for (Path f : Files.list(dir).toList()) {
			if (Files.isRegularFile(f) && f.getFileName().toString().endsWith(".puml"))
				processSinglePumlFile(f);
			else if (Files.isRegularFile(f) && f.getFileName().toString().endsWith(".json"))
				processSingleJsonFile(f);
		}

		for (Path f : Files.list(dir).toList())
			if (Files.isDirectory(f))
				processDir(f);

	}

	private void processSingleJsonFile(Path f) throws IOException {
		String name = f.toString().replace('\\', '/');
		int x = name.indexOf('/');
		x = name.indexOf('/', x + 1);
		name = name.substring(x + 1);
		System.err.println("processSingleJsonFile " + name);

		final byte[] json = Files.readAllBytes(f);

		jsonWriter.get().putContent(name, json);

	}

	private void processSinglePumlFile(Path f) throws IOException {

		String name = f.toString().replace('\\', '/').replaceFirst("\\.puml$", "");
		int x = name.indexOf('/');
		x = name.indexOf('/', x + 1);
		name = name.substring(x + 1);
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
				if (!s.endsWith("</svg>")) {
					s = readMultilineSvg(it, s);
				}
				final int idx = s.indexOf("<svg");
				final String svg = s.substring(idx);

				final int num = svgAppender.appendSvg(svg);

				s = s.substring(0, idx).trim();
				s = s + " :" + libName + "/" + num;
				
				System.err.println("SVG=" + s);
				System.err.println("svg=" + num + " " + svg);

			} else if (isSpriteLine(s)) {
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

					final int id = manageSpriteCompressed(width, height, it);
					s = s + "#" + libName + "/" + id;
					// System.err.println("s2=" + s);
				} else if (s.contains("/16]")) {
					// texts.writeUTF(s);
					final int id = manageSpriteNormalUncompressed(it);
					if (s.endsWith("{") == false)
						throw new IllegalStateException(s);
					s = s.substring(0, s.length() - 1);
					s = s + "#" + libName + "/" + id;
					// System.err.println("s3=" + s);

				} else {
					throw new UnsupportedOperationException("Those sprites are not supported (yet) " + s);
				}

			}

			sb.append(s);
			sb.append("\n");

		}

		pumlWriter.putContent(name, sb.toString());
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

	private int manageSpriteNormalUncompressed(Iterator<String> it) throws IOException {
		final List<String> strings = new ArrayList<String>();
		while (true) {
			String s = it.next();
			if (s.trim().equals("}")) {
				final SpriteMonochrome sprite = (SpriteMonochrome) SpriteGrayLevel.GRAY_16.buildSprite(-1, -1, strings);
				return spriteAppender.appendSprite(sprite);
			}
			strings.add(s.trim());
		}
	}

	private int manageSpriteCompressed(int width, int height, Iterator<String> it) throws IOException {
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
				return spriteAppender.appendSprite(sprite);
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

	private String readInfo(Path file) throws IOException {
		final StringBuilder result = new StringBuilder();

		final List<String> lines = Files.readAllLines(file);
		if (lines.get(0).equals("---") == false)
			throw new IOException("README.md must have a YAML header");
		for (int i = 1; i < lines.size(); i++) {
			if (lines.get(i).equals("---"))
				return result.toString();
			final String[] parts = lines.get(i).split(":", 2);
			if (parts.length == 2) {
				final String key = parts[0].trim();
				final String value = parts[1].trim();
				result.append(key).append("=").append(value).append("\n");
			} else {
				throw new IOException("Invalid YAML line: " + lines.get(i));
			}
		}
		throw new IOException("Bad YAML header in README.md");
	}

}
