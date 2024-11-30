package com.plantuml.stdlibencoder;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.klimt.creole.atom.AtomImg;
import net.sourceforge.plantuml.klimt.sprite.SpriteGrayLevel;
import net.sourceforge.plantuml.klimt.sprite.SpriteMonochrome;

/**
 * This class is responsible for building and processing standard libraries
 * files for use in PlantUML. It handles directory traversal, sprite extraction,
 * and file generation for the specified folder.
 * 
 * Sprite information is stored in SpritesStreams, and non-sprite information
 * (macros and others) is stored in a DataOutputStream. Separating data into
 * different files allows for better compression with Brotli later.
 */
public class StdlibFolderBuilder {

	/**
	 * Separator used to delineate sections within the sprite data files.
	 */
	private static final String SEPARATOR = "\uF8FF";

	/**
	 * Directory containing the files to be processed.
	 */
	private final File dir;

	private final DataOutputStream texts;

	/**
	 * Output stream used to write monochrome sprite data.
	 */
	private DataOutputStream spritesOutputStream;

	private ColorImagesStream colorImagesOutputStream;

	/**
	 * Constructor that initializes the builder with the specified directory.
	 *
	 * @param name The name of the directory containing the included files to be
	 *             processed.
	 */
	public StdlibFolderBuilder(String name) throws IOException {
		this.dir = new File("stdlib", name);
		final File rawFolder = rawFolder();

		final File spritesFile = new File(rawFolder, name.toLowerCase() + "-def.repx");
		final File textFile = new File(rawFolder, name.toLowerCase() + "-abc.repx");
		final File colorImagesFile = new File(rawFolder, name.toLowerCase() + "-ghi.repx");

		final String infoString = readInfo(new File(dir, "README.md"));
		this.texts = new DataOutputStream(new FileOutputStream(textFile));
		texts.writeUTF(infoString);

		if (infoString.contains("link=")) {
			texts.writeUTF(SEPARATOR);
			texts.close();
		} else {
			this.colorImagesOutputStream = new ColorImagesStream(colorImagesFile);
			this.spritesOutputStream = new DataOutputStream(new FileOutputStream(spritesFile));
			processDir(dir);
			texts.writeUTF(SEPARATOR);
			texts.close();
			colorImagesOutputStream.close();
			spritesOutputStream.close();
		}
	}

	/**
	 * Creates and returns the raw folder where the output files will be stored.
	 *
	 * @return The raw folder as a File object.
	 */
	private static File rawFolder() {
		final File result = new File("raw");
		result.mkdirs();
		return result;
	}

	/**
	 * Reads the content of the INFO file into a string.
	 *
	 * @param file The input stream from which the INFO file is read.
	 * @return The content of the INFO file as a String.
	 * @throws IOException If an error occurs while reading the file.
	 */
	private String readInfo(File file) throws IOException {
		final StringBuilder result = new StringBuilder();

		final List<String> lines = Files.readAllLines(file.toPath());
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

	private void processDir(File dir) throws IOException {
		for (File f : dir.listFiles())
			if (f.isFile() && f.getName().endsWith(".puml"))
				processSingleFile(f);

		for (File f : dir.listFiles())
			if (f.isDirectory())
				processDir(f);

	}

	/**
	 * Appends a sprite file to the output streams.
	 *
	 * @param inputFile The file to be appended.
	 * @throws IOException If an error occurs while reading or writing files.
	 */
	private void processSingleFile(File inputFile) throws IOException {

		// First entry in the stream is the name of the "file"
		texts.writeUTF(getShortName(inputFile));

		// We process the file line by line
		final BufferedReader br = new BufferedReader(new FileReader(inputFile));
		String s;
		while ((s = br.readLine()) != null) {
			if (s.contains("https://raw.githubusercontent"))
				throw new IOException(inputFile + " " + s);

			if (s.contains(AtomImg.DATA_IMAGE_PNG_BASE64)) {
				final int x1 = s.indexOf(AtomImg.DATA_IMAGE_PNG_BASE64);
				final int x2 = s.lastIndexOf(AtomImg.DATA_IMAGE_PNG_BASE64);
				if (x1 != x2)
					throw new IllegalStateException(s);
				// BASE64 PNG images are written in the sprite streams
				colorImagesOutputStream.exportDataImagePngBase64(s);

				// So we remove the image encoding for the string
				s = s.replaceAll(AtomImg.DATA_IMAGE_PNG_BASE64 + "([0-9a-zA-Z+/]+[=]*)", AtomImg.DATA_IMAGE_PNG_BASE64);
			}

			if (isSpriteLine(s))
				// Sprite are not stored in the text output stream
				if (s.contains("/16z]")) {
					// We cheat here because sprites are never stored compressed
					texts.writeUTF(s.replace("/16z]", "/16]"));
					final Pattern p = Pattern.compile("\\[(\\d+)x(\\d+)");
					final Matcher m = p.matcher(s);
					if (m.find() == false)
						throw new IllegalStateException(s);

					final int width = Integer.parseInt(m.group(1));
					final int height = Integer.parseInt(m.group(2));
					manageSpriteCompressed(width, height, br);
				} else if (s.contains("/16]")) {
					texts.writeUTF(s);
					manageSpriteNormalUncompressed(br);
				} else {
					throw new UnsupportedOperationException("Those sprites are not supported (yet) " + s);
				}
			else
				texts.writeUTF(s);

		}
		br.close();

		// Denotate the end of the file
		texts.writeUTF(SEPARATOR);
		// Next file will follow later
	}

	/**
	 * Manages the extraction and compression of a sprite with given dimensions.
	 *
	 * @param width        The width of the sprite.
	 * @param height       The height of the sprite.
	 * @param spriteStream The output stream to which the sprite is written.
	 * @param br           The BufferedReader used to read the sprite definition.
	 * @throws IOException If an error occurs while reading or writing the sprite.
	 */
	private void manageSpriteCompressed(int width, int height, final BufferedReader br) throws IOException {
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
				sprite.exportSprite1(spritesOutputStream);
				break;
			}
			definition.append(s.trim());
		}
	}

	/**
	 * Manages the extraction of a normal uncompressed sprite.
	 *
	 * @param spriteStream The output stream to which the sprite is written.
	 * @param br           The BufferedReader used to read the sprite definition.
	 * @throws IOException If an error occurs while reading or writing the sprite.
	 */
	private void manageSpriteNormalUncompressed(final BufferedReader br) throws IOException {
		String s;
		final List<String> strings = new ArrayList<String>();
		while ((s = br.readLine()) != null) {
			if (s.trim().equals("}")) {
				final SpriteMonochrome sprite = (SpriteMonochrome) SpriteGrayLevel.GRAY_16.buildSprite(-1, -1, strings);
				sprite.exportSprite1(spritesOutputStream);
				break;
			}
			strings.add(s.trim());
		}
	}

	/**
	 * Checks if a line represents the start of a sprite definition.
	 *
	 * @param s The line to be checked.
	 * @return True if the line starts a sprite definition, false otherwise.
	 */
	private boolean isSpriteLine(String s) {
		return s.trim().startsWith("sprite") && s.trim().endsWith("{");
	}

	/**
	 * Returns a shortened version of the file path relative to the directory.
	 *
	 * @param f The file for which the short name is to be generated.
	 * @return The shortened file path as a String.
	 */
	private String getShortName(File f) {
		final String name = f.getAbsolutePath();
		final String dirName = dir.getAbsolutePath();
		if (name.startsWith(dirName) == false)
			throw new IllegalStateException();

		return name.substring(dirName.length() + 1).replace('\\', '/').replace(".puml", "");
	}

}
