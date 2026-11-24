package com.plantuml.stdlibencoder.spm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * What SpmBuilder exports to svg.spm and sprite.spm, and what it leaves in the
 * text of puml.spm. A sprite written with variables, such as the icons that
 * edgy2 declares in procedures (plantuml/plantuml#3039), only exists once the
 * preprocessor has run: it must stay in the text. Two different sprites under
 * one exported name must stop the build, since only one could be stored.
 */
class SpmBuilderTest {

	@TempDir
	Path root;

	private void library(String puml) throws IOException {
		final Path lib = Files.createDirectories(root.resolve("stdlib").resolve("foo"));
		Files.write(lib.resolve("README.md"), "---\nname: foo\n---\n".getBytes(StandardCharsets.UTF_8));
		Files.write(lib.resolve("foo.puml"), puml.getBytes(StandardCharsets.UTF_8));
	}

	private Path raw(SpmChannel channel) throws IOException {
		return channel.getPath(root.resolve("raw").resolve("foo"));
	}

	private String text() throws IOException {
		try (DataInputStream dis = new DataInputStream(Files.newInputStream(raw(SpmChannel.PUML)))) {
			assertEquals(1, dis.readInt());
			assertEquals("foo", dis.readUTF());
			final byte[] data = new byte[dis.readInt()];
			dis.readFully(data);
			return new String(data, StandardCharsets.UTF_8);
		}
	}

	private List<String> svgNames() throws IOException {
		final List<String> result = new ArrayList<>();
		try (DataInputStream dis = new DataInputStream(Files.newInputStream(raw(SpmChannel.SVG)))) {
			final int nb = dis.readInt();
			for (int i = 0; i < nb; i++) {
				result.add(dis.readUTF());
				dis.readUTF();
			}
		}
		return result;
	}

	private List<String> spriteNames() throws IOException {
		final List<String> result = new ArrayList<>();
		try (InputStream is = Files.newInputStream(raw(SpmChannel.SPRITE));
				DataInputStream dis = new DataInputStream(is)) {
			final int nb = dis.readInt();
			for (int i = 0; i < nb; i++) {
				result.add(dis.readUTF());
				final int width = dis.readInt();
				final int height = dis.readInt();
				// one byte per column for each pair of rows, see SpriteMonochrome.exportSprite1
				dis.readFully(new byte[width * ((height + 1) / 2)]);
			}
		}
		return result;
	}

	@Test
	void anSvgSpriteWithVariablesStaysInTheText() throws IOException {
		library("@startuml\n" //
				+ "!procedure $icon($name, $color)\n" //
				+ "sprite $i##$name <svg viewBox=\"0 0 10 10\">\n" //
				+ "\t<rect width=\"10\" height=\"10\" fill=\"$color\"/>\n" //
				+ "</svg>\n" //
				+ "!endprocedure\n" //
				+ "$icon(\"A\", \"#f00\")\n" //
				+ "sprite $plain <svg viewBox=\"0 0 10 10\"><rect width=\"10\" height=\"10\" fill=\"#00f\"/></svg>\n" //
				+ "@enduml\n");
		new SpmBuilder(root, "foo");

		assertEquals("[foo:plain]", svgNames().toString());
		final String text = text();
		assertTrue(text.contains("\nsprite $i##$name <svg viewBox=\"0 0 10 10\">"
				+ "<rect width=\"10\" height=\"10\" fill=\"$color\"/></svg>\n!endprocedure\n"), text);
		assertTrue(text.contains("\nsprite $plain :foo:foo:plain\n"), text);
	}

	@Test
	void aBitmapSpriteWithVariablesStaysInTheText() throws IOException {
		library("@startuml\n" //
				+ "!procedure $dot($name)\n" //
				+ "sprite $d##$name [4x4/16] {\n0F0F\nF0F0\n0F0F\nF0F0\n}\n" //
				+ "!endprocedure\n" //
				+ "$dot(\"A\")\n" //
				+ "sprite $plain [4x4/16] {\nFFFF\n0000\nFFFF\n0000\n}\n" //
				+ "@enduml\n");
		new SpmBuilder(root, "foo");

		assertEquals("[foo:plain]", spriteNames().toString());
		final String text = text();
		assertTrue(text.contains("\nsprite $d##$name [4x4/16] {\n0F0F\nF0F0\n0F0F\nF0F0\n}\n!endprocedure\n"), text);
		assertTrue(text.contains("\nsprite $plain #foo#foo:plain\n"), text);
	}

	@Test
	void twoDifferentSvgSpritesUnderOneNameStopTheBuild() throws IOException {
		library("@startuml\n" //
				+ "sprite $same <svg viewBox=\"0 0 10 10\"><rect width=\"10\" height=\"10\" fill=\"#f00\"/></svg>\n" //
				+ "sprite $same <svg viewBox=\"0 0 10 10\"><rect width=\"10\" height=\"10\" fill=\"#00f\"/></svg>\n" //
				+ "@enduml\n");
		final UncheckedIOException e = assertThrows(UncheckedIOException.class, () -> new SpmBuilder(root, "foo"));
		assertTrue(e.getCause().getMessage().contains("foo:same"), e.getCause().getMessage());
	}

	@Test
	void twoDifferentBitmapSpritesUnderOneNameStopTheBuild() throws IOException {
		library("@startuml\n" //
				+ "sprite $same [4x4/16] {\nFFFF\n0000\nFFFF\n0000\n}\n" //
				+ "sprite $same [4x4/16] {\n0000\nFFFF\n0000\nFFFF\n}\n" //
				+ "@enduml\n");
		final UncheckedIOException e = assertThrows(UncheckedIOException.class, () -> new SpmBuilder(root, "foo"));
		assertTrue(e.getCause().getMessage().contains("foo:same"), e.getCause().getMessage());
	}

	@Test
	void theSameSpriteTwiceIsExportedOnce() throws IOException {
		library("@startuml\n" //
				+ "sprite $svg <svg viewBox=\"0 0 10 10\"><rect width=\"10\" height=\"10\" fill=\"#f00\"/></svg>\n" //
				+ "sprite $svg <svg viewBox=\"0 0 10 10\"><rect width=\"10\" height=\"10\" fill=\"#f00\"/></svg>\n" //
				+ "sprite $bitmap [4x4/16] {\nFFFF\n0000\nFFFF\n0000\n}\n" //
				+ "sprite $bitmap [4x4/16] {\nFFFF\n0000\nFFFF\n0000\n}\n" //
				+ "@enduml\n");
		new SpmBuilder(root, "foo");

		assertEquals("[foo:svg]", svgNames().toString());
		assertEquals("[foo:bitmap]", spriteNames().toString());
	}

}
