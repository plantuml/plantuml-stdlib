package com.plantuml.stdlibencoder;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

import javax.imageio.ImageIO;

import net.sourceforge.plantuml.code.CompressionHuffman;
import net.sourceforge.plantuml.klimt.color.ColorMapper;
import net.sourceforge.plantuml.klimt.color.HColors;
import net.sourceforge.plantuml.klimt.creole.atom.AtomImg;
import net.sourceforge.plantuml.klimt.shape.UImage;
import net.sourceforge.plantuml.klimt.sprite.SpriteGrayLevel;
import net.sourceforge.plantuml.klimt.sprite.SpriteMonochrome;

public class StdlibBuilder6 {

	public static final File HOME = new File(pathRaw(), "home.repx");
	private static final String SEPARATOR = "\uF8FF";
	private final File dir;
	private final int diff;

	public StdlibBuilder6(File dir, int diff) throws IOException {
		this.dir = dir;
		this.diff = diff;
		System.err.println("in " + dir);

		File info = new File(dir, "INFO");
		String infoString = readInfo(new FileInputStream(info));
		System.err.println("infoString=" + infoString);
		if (infoString.contains("LINK=")) {
			System.err.println("Link!");
			final DataOutputStream texts = new DataOutputStream(
					new FileOutputStream(new File(pathRaw(), dir.getName() + "-abc.repx")));
			texts.writeUTF(infoString);
			texts.writeUTF(SEPARATOR);
			texts.close();
		} else {
			final DataOutputStream texts = new DataOutputStream(
					new FileOutputStream(new File(pathRaw(), dir.getName() + "-abc.repx")));
			texts.writeUTF(infoString);
			final Out6 out = new Out6(dir.getName(), pathRaw());
			processDir(dir, texts, out);
			texts.writeUTF(SEPARATOR);
			texts.close();
			out.close();
		}

		final PrintWriter pw = new PrintWriter(new FileOutputStream(HOME, true));
		pw.println(dir.getName());
		pw.close();
	}

	public static File pathRaw() {
		final File result = new File("raw");
		result.mkdirs();
		return result;
	}

	private String readInfo(FileInputStream fileInputStream) throws IOException {
		final BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));
		StringBuilder sb = new StringBuilder();
		String s;
		while ((s = br.readLine()) != null) {
			sb.append(s);
			sb.append("\n");
		}
		br.close();
		return sb.toString();
	}

	private void processDir(File dir, DataOutputStream texts, Out6 path) throws IOException {
		// System.err.println("dir=" + dir.getAbsolutePath());
		for (File f : dir.listFiles())
			if (f.isFile() && f.getName().endsWith(".puml"))
				append(f, texts, path);

		for (File f : dir.listFiles())
			if (f.isDirectory())
				processDir(f, texts, path);

	}

	private void append(File f, DataOutputStream texts, Out6 out) throws IOException {
		// String name = f.getAbsolutePath();
		final String name = getShortName(f);
		texts.writeUTF(name);
		copyToStream(f, texts, out);
	}

	// static int saved = 0;
	// static int CPT = 0;

	final private Pattern p = Pattern.compile(AtomImg.DATA_IMAGE_PNG_BASE64 + "([0-9a-zA-Z+/]+[=]*)");

	private void copyToStream(File src, DataOutputStream dos, Out6 out) throws IOException {
		final BufferedReader br = new BufferedReader(new FileReader(src));
		String s;
		// final File dir = new File("spp");
		// dir.mkdirs();
		try {
			while ((s = br.readLine()) != null) {
				if (s.contains("https://raw.githubusercontent"))
					throw new IOException(src + " " + s);

				if (s.contains(AtomImg.DATA_IMAGE_PNG_BASE64)) {
					final int x1 = s.indexOf(AtomImg.DATA_IMAGE_PNG_BASE64);
					final int x2 = s.lastIndexOf(AtomImg.DATA_IMAGE_PNG_BASE64);
					if (x1 != x2)
						throw new IllegalStateException(s);
					out.dataImagePngBase64(s);
					s = s.replaceAll(AtomImg.DATA_IMAGE_PNG_BASE64 + "([0-9a-zA-Z+/]+[=]*)",
							AtomImg.DATA_IMAGE_PNG_BASE64);
				}

				if (isSpriteLine(s))
					if (s.contains("/16z]")) {
						dos.writeUTF(s.replace("/16z]", "/16]"));
						final Pattern p = Pattern.compile("\\[(\\d+)x(\\d+)");
						final Matcher m = p.matcher(s);
						if (m.find() == false)
							throw new IllegalArgumentException(s);

						final int width = Integer.parseInt(m.group(1));
						final int height = Integer.parseInt(m.group(2));
						manageSpriteCompressed(width, height, out.sprs(), br);
					} else if (s.contains("/16]")) {
						dos.writeUTF(s);
						manageSpriteNormal(out.sprs(), br);
					} else {
						throw new IllegalArgumentException(s);
					}
				else
					dos.writeUTF(s);

			}
			dos.writeUTF(SEPARATOR);
			br.close();
		} catch (Throwable t) {
			System.err.println("Error in " + src);
			throw t;
		}
	}

	private void manageSpriteCompressed(int width, int height, DataOutputStream spriteStream, final BufferedReader br)
			throws IOException {
		String s = null;
		final StringBuilder definition = new StringBuilder();
		while ((s = br.readLine()) != null) {
			if (s.trim().equals("}")) {
				final SpriteMonochrome sprite = (SpriteMonochrome) SpriteGrayLevel.GRAY_16.buildSpriteZ(width, height,
						definition.toString());
				if (sprite == null) {
					System.err.println("ERROR in " + definition);
					System.err.println("ERROR in " + s);
					SpriteGrayLevel.GRAY_16.buildSpriteZ(width, height, definition.toString());
					throw new IOException("Error in sprite");
				}
				sprite.exportSprite1(spriteStream);
				break;
			}
			definition.append(s.trim());
		}
	}

	private void manageSpriteNormal(DataOutputStream spriteStream, final BufferedReader br) throws IOException {
		String s;
		final List<String> strings = new ArrayList<String>();
		while ((s = br.readLine()) != null) {
			if (s.trim().equals("}")) {
				final SpriteMonochrome sprite = (SpriteMonochrome) SpriteGrayLevel.GRAY_16.buildSprite(-1, -1, strings);
				sprite.exportSprite1(spriteStream);
				// CPT++;
				break;
			}
			strings.add(s.trim());
		}
	}



	private int priceHuffman(SpriteMonochrome sprite) throws IOException {
		final CompressionHuffman comp = new CompressionHuffman();
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		sprite.exportSprite1(baos);
		baos.close();
		return comp.compress(baos.toByteArray()).length;
	}


	private boolean isSpriteLine(String s) {
		return s.trim().startsWith("sprite") && s.trim().endsWith("{");
	}

	private String getShortName(File f) {
		final String name = f.getAbsolutePath();
		final String dirName = dir.getAbsolutePath();
		if (name.startsWith(dirName) == false)
			throw new IllegalStateException();

		return name.substring(dirName.length() + 1).replace('\\', '/').replace(".puml", "");
	}

	public final File getDir() {
		return dir;
	}

}
