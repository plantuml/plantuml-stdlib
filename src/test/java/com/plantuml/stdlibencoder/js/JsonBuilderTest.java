package com.plantuml.stdlibencoder.js;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.sourceforge.plantuml.json.Json;
import net.sourceforge.plantuml.json.JsonArray;
import net.sourceforge.plantuml.json.JsonObject;
import net.sourceforge.plantuml.json.JsonValue;

/**
 * The JSON data bundle must carry exactly the content of the JS bundle: the
 * same info map, the same lowercased extension-stripped keys, the same
 * comment-stripped lines. Hosts that populate the engine's globals from it
 * (the PLANTUML_STDLIB_LOADER path) depend on that equivalence.
 */
class JsonBuilderTest {

	@TempDir
	Path tmp;

	private Path stdlibRoot;
	private Path outputDir;

	@BeforeEach
	void setUp() throws IOException {
		stdlibRoot = tmp.resolve("stdlib");
		outputDir = tmp.resolve("output-js");
		Files.createDirectories(stdlibRoot);
	}

	private void writeLib(String name, String readmeHeader) throws IOException {
		final Path lib = stdlibRoot.resolve(name);
		Files.createDirectories(lib);
		Files.writeString(lib.resolve("README.md"), readmeHeader, StandardCharsets.UTF_8);
	}

	private JsonObject buildAndParse(String name) throws IOException {
		new JsonBuilder(stdlibRoot, outputDir, name);
		final String text = Files.readString(outputDir.resolve(name.toLowerCase() + ".json"),
				StandardCharsets.UTF_8);
		return Json.parse(text).asObject();
	}

	@Test
	void info_map_comes_from_the_readme_yaml_header() throws IOException {
		writeLib("MyLib", "---\nname: MyLib\nversion: 1.2.3\nlink: otherlib\n---\nBody text.\n");

		final JsonObject root = buildAndParse("MyLib");

		final JsonObject info = root.get("info").asObject();
		assertEquals("MyLib", info.get("name").asString());
		assertEquals("1.2.3", info.get("version").asString());
		assertEquals("otherlib", info.get("link").asString());
	}

	@Test
	void keys_are_lowercased_and_extension_stripped_like_the_js_bundle() throws IOException {
		writeLib("MyLib", "---\nname: MyLib\n---\n");
		final Path lib = stdlibRoot.resolve("MyLib");
		Files.writeString(lib.resolve("Greeting.puml"), "participant Alice\n");
		Files.createDirectories(lib.resolve("Sub"));
		Files.writeString(lib.resolve("Sub").resolve("Nested.puml"), "participant Bob\n");

		final JsonObject files = buildAndParse("MyLib").get("files").asObject();

		assertEquals("participant Alice", files.get("greeting").asArray().get(0).asString());
		assertEquals("participant Bob", files.get("sub/nested").asArray().get(0).asString());
		assertNull(files.get("Greeting"));
	}

	@Test
	void string_escaping_round_trips_the_json_escape_set() throws IOException {
		writeLib("esc", "---\nname: esc\n---\n");
		final String nasty = "say \"hi\" to C:\\temp\tand beyond";
		Files.writeString(stdlibRoot.resolve("esc").resolve("f.puml"), nasty + "\n");

		final JsonArray lines = buildAndParse("esc").get("files").asObject().get("f").asArray();

		assertEquals(nasty, lines.get(0).asString());
	}

	@Test
	void json_data_files_are_embedded_as_objects_not_lines() throws IOException {
		writeLib("data", "---\nname: data\n---\n");
		final Path lib = stdlibRoot.resolve("data");
		Files.createDirectories(lib.resolve("cfg"));
		Files.writeString(lib.resolve("cfg").resolve("Colors.json"), "{\"main\": \"#0000FF\"}\n");

		final JsonObject root = buildAndParse("data");

		final JsonValue colors = root.get("json").asObject().get("cfg/colors");
		assertEquals("#0000FF", colors.asObject().get("main").asString());
		assertNull(root.get("files").asObject().get("cfg/colors"));
	}

	@Test
	void meta_folders_are_skipped_like_everywhere_else() throws IOException {
		writeLib("meta", "---\nname: meta\n---\n");
		final Path lib = stdlibRoot.resolve("meta");
		Files.createDirectories(lib.resolve("_private_"));
		Files.writeString(lib.resolve("_private_").resolve("hidden.puml"), "participant Hidden\n");
		Files.writeString(lib.resolve("visible.puml"), "participant Visible\n");

		final JsonObject files = buildAndParse("meta").get("files").asObject();

		assertNull(files.get("_private_/hidden"));
		assertEquals("participant Visible", files.get("visible").asArray().get(0).asString());
	}

	@Test
	void comment_stripping_matches_the_js_bundle_reader() throws IOException {
		writeLib("cmt", "---\nname: cmt\n---\n");
		Files.writeString(stdlibRoot.resolve("cmt").resolve("f.puml"),
				"participant Alice\n' a line comment\nparticipant Bob\n");

		final JsonArray lines = buildAndParse("cmt").get("files").asObject().get("f").asArray();

		for (int i = 0; i < lines.size(); i++)
			assertFalse(lines.get(i).asString().startsWith("'"), "comment line survived: " + lines.get(i));
		assertEquals(2, lines.size());
	}

	@Test
	void output_shape_is_the_three_documented_sections() throws IOException {
		writeLib("shape", "---\nname: shape\n---\n");

		final JsonObject root = buildAndParse("shape");

		assertEquals(3, root.names().size());
		assertTrue(root.names().contains("info"));
		assertTrue(root.names().contains("files"));
		assertTrue(root.names().contains("json"));
	}

	@Test
	void readme_without_yaml_header_is_refused() throws IOException {
		writeLib("bad", "No header here.\n");

		assertThrows(IOException.class, () -> new JsonBuilder(stdlibRoot, outputDir, "bad"));
	}

	@Test
	void a_malformed_embedded_json_data_file_fails_the_build_instead_of_publishing_garbage() throws IOException {
		writeLib("broken", "---\nname: broken\n---\n");
		Files.writeString(stdlibRoot.resolve("broken").resolve("oops.json"), "{\"unclosed\": \n");

		assertThrows(IOException.class, () -> new JsonBuilder(stdlibRoot, outputDir, "broken"));
		assertFalse(Files.exists(outputDir.resolve("broken.json")), "invalid bundle must not be written");
	}

}
