package org.lgna.project.migration;

import org.junit.Test;
import org.lgna.project.Version;

import java.io.IOException;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import static org.lgna.project.migration.TextMigrationRule.replace;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class TextMigrationJsonLoaderTest {
  @Test
  public void jsonLoaderMatchesLegacyRegistryDefinitionsExactly() throws Exception {
    assertEquals(TextMigrationParityTestSupport.legacyRegistryData(), TextMigrationParityTestSupport.loadedJsonData());
  }

  @Test
  public void jsonLoaderContainsKnownResolvedPairs() throws Exception {
    TextMigration[] migrations = TextMigrationJsonLoader.load();

    assertTrue(TextMigrationParityTestSupport.containsPair(
        TextMigrationParityTestSupport.migrationForVersion(migrations, "3.1.9.0.0"),
        "ARMOIRE_CLOTHING",
        null));
    assertTrue(TextMigrationParityTestSupport.containsPair(
        TextMigrationParityTestSupport.migrationForVersion(migrations, "3.1.34.0.0"),
        "org.lgna.story.Program",
        "org.lgna.story.SProgram"));
    assertTrue(TextMigrationParityTestSupport.containsPair(
        TextMigrationParityTestSupport.migrationForVersion(migrations, "3.2.110.0.0"),
        "name=\"OVAL\">\\s*<declaringClass name=\"org.lgna.story.resources.prop.SandDunesResource\"",
        "name=\"OVAL_DESERT\"> <declaringClass name=\"org.lgna.story.resources.prop.SandDunesResource\""));
  }

  @Test
  public void jsonLoaderPreservesNullNoReplacementSemantics() throws Exception {
    TextMigration migration = TextMigrationParityTestSupport.migrationForVersion(TextMigrationJsonLoader.load(), "3.1.9.0.0");
    TextMigrationParityTestSupport.PairData firstPair = TextMigrationParityTestSupport.pairsOf(migration).get(0);

    assertEquals("ARMOIRE_CLOTHING", firstPair.pattern);
    assertNull("JSON null replacement must reload as Java null", firstPair.replacement);
    assertEquals("JSON null replacement must match MigrationManager.NO_REPLACEMENT",
        MigrationManager.NO_REPLACEMENT, firstPair.replacement);
  }

  @Test
  public void nullReplacementEntriesAreNoOpTextMigrations() {
    TextMigration migration = new TextMigration(
        new Version("3.1.9.0.0"),
        replace("legacyName", MigrationManager.NO_REPLACEMENT),
        replace("oldName", "newName"));

    assertEquals("legacyName and newName", migration.migrate("legacyName and oldName"));
  }

  @Test
  public void jsonLoaderPreservesMigrationAndReplacementOrder() throws Exception {
    TextMigration[] migrations = TextMigrationParityTestSupport.parseJson("["
        + "{\"version\":\"3.1.1.0.0\",\"replacements\":["
        + "{\"pattern\":\"first\",\"replacement\":\"one\"},"
        + "{\"pattern\":\"second\",\"replacement\":\"two\"}]},"
        + "{\"version\":\"3.1.2.0.0\",\"replacements\":["
        + "{\"pattern\":\"third\",\"replacement\":\"three\"}]}]");

    List<TextMigrationParityTestSupport.MigrationData> data = TextMigrationParityTestSupport.dataOf(migrations);
    assertEquals("3.1.1.0.0", data.get(0).version);
    assertEquals("first", data.get(0).pairs.get(0).pattern);
    assertEquals("second", data.get(0).pairs.get(1).pattern);
    assertEquals("3.1.2.0.0", data.get(1).version);
    assertEquals("third", data.get(1).pairs.get(0).pattern);
  }

  @Test
  public void jsonLoaderAcceptsEmptyMigrationList() throws Exception {
    TextMigration[] migrations = TextMigrationParityTestSupport.parseJson("[]");

    assertEquals(0, migrations.length);
  }

  @Test
  public void jsonLoaderAcceptsEmptyReplacementList() throws Exception {
    TextMigration[] migrations = TextMigrationParityTestSupport.parseJson("["
        + "{\"version\":\"3.1.1.0.0\",\"replacements\":[]}]");

    assertEquals(0, TextMigrationParityTestSupport.pairsOf(migrations[0]).size());
  }

  @Test
  public void jsonLoaderAcceptsDuplicateReplacementEntriesInOrder() throws Exception {
    TextMigration[] migrations = TextMigrationParityTestSupport.parseJson("["
        + "{\"version\":\"3.1.1.0.0\",\"replacements\":["
        + "{\"pattern\":\"same\",\"replacement\":\"one\"},"
        + "{\"pattern\":\"same\",\"replacement\":\"two\"}]}]");

    List<TextMigrationParityTestSupport.PairData> pairs = TextMigrationParityTestSupport.pairsOf(migrations[0]);
    assertEquals(2, pairs.size());
    assertEquals("one", pairs.get(0).replacement);
    assertEquals("two", pairs.get(1).replacement);
  }

  @Test
  public void jsonLoaderRejectsMalformedJson() {
    assertThrows(IOException.class, () -> TextMigrationParityTestSupport.parseJson("["));
  }

  @Test
  public void jsonLoaderSurfacesMissingVersionField() {
    assertThrows(NullPointerException.class, () -> TextMigrationParityTestSupport.parseJson("["
        + "{\"replacements\":[{\"pattern\":\"legacy\",\"replacement\":\"modern\"}]}]"));
  }

  @Test
  public void jsonLoaderSurfacesMissingPatternField() {
    assertThrows(NullPointerException.class, () -> TextMigrationParityTestSupport.parseJson("["
        + "{\"version\":\"3.1.1.0.0\",\"replacements\":[{\"replacement\":\"modern\"}]}]"));
  }

  @Test
  public void jsonLoaderRejectsEmptyPatternField() {
    assertThrows(IllegalArgumentException.class, () -> TextMigrationParityTestSupport.parseJson("["
        + "{\"version\":\"3.1.1.0.0\",\"replacements\":[{\"pattern\":\"\",\"replacement\":\"modern\"}]}]"));
  }

  @Test
  public void jsonLoaderSurfacesInvalidPatternField() {
    assertThrows(PatternSyntaxException.class, () -> TextMigrationParityTestSupport.parseJson("["
        + "{\"version\":\"3.1.1.0.0\",\"replacements\":[{\"pattern\":\"[\",\"replacement\":\"modern\"}]}]"));
  }
}
