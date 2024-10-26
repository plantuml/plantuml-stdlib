package com.plantuml.stdlibencoder;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import net.sourceforge.plantuml.klimt.creole.atom.AtomImg;
import net.sourceforge.plantuml.security.SImageIO;
import net.sourceforge.plantuml.utils.Base64Coder;

public class Out6 {

	final private Pattern p = Pattern.compile(AtomImg.DATA_IMAGE_PNG_BASE64 + "([0-9a-zA-Z+/]+[=]*)");

	private final DataOutputStream sprs;

	private final Map<Integer, Integer> colorAndIndex = new HashMap<>();
	private final List<Integer> colors = new ArrayList<>();
	private final ByteArrayOutputStream pngData = new ByteArrayOutputStream();

	private final File pathRaw;
	private final String name;

	public Out6(String name, File pathRaw) throws FileNotFoundException {
		this.pathRaw = pathRaw;
		this.sprs = new DataOutputStream(new FileOutputStream(new File(pathRaw, name + "-def.repx")));
		this.name = name;
	}

	public final DataOutputStream sprs() {
		return sprs;
	}

	public void close() throws IOException {
		sprs.close();
		if (colors.size() > 0) {
			if (colors.size() != colorAndIndex.size())
				throw new IllegalStateException();
			final OutputStream fos = new BufferedOutputStream(
					new FileOutputStream(new File(pathRaw, name + "-ghi.repx")));
			exportColorTable(fos);
			pngData.close();
			fos.write(pngData.toByteArray());
			fos.close();
		}
	}

	int cpt = 0;

	public void dataImagePngBase64(String s) throws IOException {
		final Matcher m = p.matcher(s);
		if (m.find() == false)
			throw new IllegalArgumentException();
		final String data = m.group(1);

		final byte bytes[] = Base64Coder.decode(data);
		BufferedImage img = SImageIO.read(bytes);
		final int width = img.getWidth();
		final int height = img.getHeight();
		pngData.write(width);
		pngData.write(height);
		// System.err.println("img=" + (cpt++));

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
						throw new IllegalStateException();
				}
				if (colors.get(idx) != rgb)
					throw new IllegalStateException();

				pngData.write((idx.intValue() >> 8) & 0xFF);
				pngData.write(idx.intValue() & 0xFF);
			}
		}

	}

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

	private int smartCompress(int red) {
		if ((red & 0x80) != 0)
			return (red | 0x01) & 0xFF;
		return red & 0xFE;
		// return red & (0b1111_1111);
	}

}
