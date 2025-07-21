package com.plantuml.stdlibencoder.v2;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.plantuml.stdlibencoder.brotli.Lazy;

import net.sourceforge.plantuml.klimt.creole.atom.AtomImg;
import net.sourceforge.plantuml.security.SImageIO;
import net.sourceforge.plantuml.utils.Base64Coder;

public class ImageAppender {

	/**
	 * Pattern to match Base64 encoded PNG images in a data URI format.
	 */
	final private Pattern PATTERN_BASE64 = Pattern.compile(AtomImg.DATA_IMAGE_PNG_BASE64 + "([0-9a-zA-Z+/]+[=]*)");

	private final Lazy<OutputStream> toc;
	private final Lazy<OutputStream> dat;
	private final Lazy<OutputStream> colorOs;
	private int counter;

	private final Map<Integer, Integer> colorAndIndex = new HashMap<>();

	private final List<Integer> colors = new ArrayList<>();

	public ImageAppender(Path path) throws IOException {
		Files.createDirectories(path);

		this.toc = new Lazy<>(() -> {
			try {
				return Files.newOutputStream(path.resolve("image-toc.spm"));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
		this.dat = new Lazy<>(() -> {
			try {
				return Files.newOutputStream(path.resolve("image-dat.spm"));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
		this.colorOs = new Lazy<>(() -> {
			try {
				return Files.newOutputStream(path.resolve("image-col.spm"));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	public void close() throws IOException {
		if (colors.size() > 0) {
			if (colors.size() != colorAndIndex.size())
				throw new IllegalStateException("Color count mismatch");
			exportColorTable(colorOs.get());
			this.colorOs.get().close();
			this.toc.get().close();
			this.dat.get().close();
		}
	}

	/**
	 * Compresses the color component value to optimize storage by reducing
	 * redundancy in the data.
	 * 
	 * This form of compression helps to save space while maintaining a balance
	 * between precision and data integrity. Changing the LSB has minimal impact on
	 * the color, which means that colors are almost visually identical to their
	 * original values. Additionally, this method ensures that extreme colors like
	 * pure white (RGB 255, 255, 255) and pure black (RGB 0, 0, 0) are preserved
	 * accurately.
	 *
	 * @param value The original color component value.
	 * @return The compressed value.
	 */
	private int smartCompress(int red) {
		if ((red & 0x80) != 0)
			return (red | 0x01) & 0xFF;
		return red & 0xFE;
	}

	public int exportDataImagePngBase64(String s) throws IOException {

		final Matcher m = PATTERN_BASE64.matcher(s);
		if (m.find() == false)
			throw new IllegalArgumentException("Invalid Base64 PNG data");

		final String data = m.group(1);

		final byte bytes[] = Base64Coder.decode(data);
		final BufferedImage img = SImageIO.read(bytes);
		final int width = img.getWidth();
		final int height = img.getHeight();
		toc.get().write(width);
		toc.get().write(height);

		// Ok, here we store the image information without any compression
		// (Except that we use a 16 bit colors index)

		for (int y = 0; y < height; y += 1) {
			for (int x = 0; x < width; x += 1) {
				int rgb = img.getRGB(x, y);
				final int alpha = smartCompress((rgb & 0xFF000000) >> 24);
				final int red = smartCompress((rgb & 0xFF0000) >> 16);
				final int green = smartCompress((rgb & 0x00FF00) >> 8);
				final int blue = smartCompress(rgb & 0x0000FF);
				rgb = (alpha << 24) + (red << 16) + (green << 8) + blue;
				img.setRGB(x, y, rgb);
				Integer idx = colorAndIndex.get(rgb);
				if (idx == null) {
					colorAndIndex.put(rgb, colorAndIndex.size());
					idx = colorAndIndex.get(rgb);
					colors.add(rgb);
					if (idx > 0xFFFF)
						throw new IllegalStateException("Too many colors!");
				}
				if (colors.get(idx) != rgb)
					throw new AssertionError();

				dat.get().write((idx.intValue() >> 8) & 0xFF);
				dat.get().write(idx.intValue() & 0xFF);
			}
		}
		
		
		return counter++;

	}

	/**
	 * Exports the color table to the provided output stream.
	 *
	 * @param os The output stream to which the color table will be written.
	 * @throws IOException If an error occurs during writing.
	 */
	private void exportColorTable(OutputStream os) throws IOException, FileNotFoundException {
		os.write((colors.size() >> 8) & 0xFF);
		os.write(colors.size() & 0xFF);

		for (Integer rgb : colors) {
			final int alpha = smartCompress((rgb & 0xFF000000) >> 24);
			final int red = smartCompress((rgb & 0xFF0000) >> 16);
			final int green = smartCompress((rgb & 0x00FF00) >> 8);
			final int blue = smartCompress(rgb & 0x0000FF);
			os.write(alpha);
			os.write(red);
			os.write(green);
			os.write(blue);
		}
	}

}
