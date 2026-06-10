package org.lgna.project.migration;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class TextMigrationJsonGeneratorTest {
  private static final String COMMITTED_JSON_RESOURCE =
      "core/story-api-migration/src/main/resources/migrations/text-migrations.json";
  private static final String STRICT_DRIFT_CHECK_COMMAND =
      "mvn -pl core/story-api-migration -am -DincludeSims=false -Dinstall4j.skip "
          + "-Dcheckstyle.skip -Djava.awt.headless=true -Dtest=TextMigrationJsonGeneratorTest "
          + "-Dsurefire.failIfNoSpecifiedTests=false test";
  private static final String REGENERATION_COMMAND =
      "mvn -pl core/story-api-migration -am -DincludeSims=false -Dinstall4j.skip "
          + "-Dcheckstyle.skip -Djava.awt.headless=true "
          + "-Dorg.lgna.project.migration.TextMigrationJsonGenerator.write=true "
          + "-Dtest=TextMigrationJsonGeneratorTest -Dsurefire.failIfNoSpecifiedTests=false test";
  private static final String GENERATED_JSON_DRIFT_MESSAGE =
      "Generated text migrations JSON must match the committed resource by exact UTF-8 string comparison: "
          + COMMITTED_JSON_RESOURCE
          + ". Run the strict drift check without the write property: "
          + STRICT_DRIFT_CHECK_COMMAND
          + ". If an intentional legacy registry change requires regeneration, run: "
          + REGENERATION_COMMAND
          + " and inspect: git diff -- "
          + COMMITTED_JSON_RESOURCE;

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void generatedTextMigrationJsonMatchesCommittedResourceExactly() throws Exception {
    String generatedJson = TextMigrationParityTestSupport.legacyRegistryJson();

    if (Boolean.getBoolean(TextMigrationParityTestSupport.WRITE_JSON_PROPERTY)) {
      TextMigrationParityTestSupport.writeLegacyRegistryJson(TextMigrationParityTestSupport.committedJsonPath());
    }

    assertGeneratedJsonMatchesCommittedText(generatedJson, TextMigrationParityTestSupport.committedJsonPath());
  }

  @Test
  public void generatorIsReadOnlyUnlessExplicitWritePropertyIsSet() throws Exception {
    String previousValue = System.getProperty(TextMigrationParityTestSupport.WRITE_JSON_PROPERTY);
    Path committedJsonPath = TextMigrationParityTestSupport.committedJsonPath();
    FileTime modifiedBefore = Files.getLastModifiedTime(committedJsonPath);
    try {
      System.clearProperty(TextMigrationParityTestSupport.WRITE_JSON_PROPERTY);
      TextMigrationParityTestSupport.legacyRegistryJson();

      assertEquals("Normal generator validation must not modify text-migrations.json",
          modifiedBefore, Files.getLastModifiedTime(committedJsonPath));
    } finally {
      TextMigrationParityTestSupport.restoreProperty(TextMigrationParityTestSupport.WRITE_JSON_PROPERTY, previousValue);
    }
  }

  @Test
  public void strictComparisonFailsOnFormattingOnlyJsonDrift() throws Exception {
    String generatedJson = TextMigrationParityTestSupport.legacyRegistryJson();
    Path generatedPath = temporaryFolder.newFile("generated-text-migrations.json").toPath();
    Path formattedPath = temporaryFolder.newFile("formatted-text-migrations.json").toPath();
    Files.writeString(generatedPath, generatedJson, StandardCharsets.UTF_8);
    Files.writeString(formattedPath, generatedJson + "\n", StandardCharsets.UTF_8);

    assertEquals("Formatting-only drift must remain semantically equivalent JSON for this characterization",
        TextMigrationParityTestSupport.jsonTree(generatedPath),
        TextMigrationParityTestSupport.jsonTree(formattedPath));
    AssertionError error = assertThrows(AssertionError.class,
        () -> assertGeneratedJsonMatchesCommittedText(generatedJson, formattedPath));
    assertTrue(error.getMessage().contains("exact UTF-8 string comparison"));
  }

  @Test
  public void strictDriftCheckDoesNotModifyStaleJson() throws Exception {
    String generatedJson = TextMigrationParityTestSupport.legacyRegistryJson();
    Path stalePath = temporaryFolder.newFile("stale-text-migrations.json").toPath();
    String staleJson = generatedJson + "\n";
    Files.writeString(stalePath, staleJson, StandardCharsets.UTF_8);
    FileTime modifiedBefore = Files.getLastModifiedTime(stalePath);

    assertThrows(AssertionError.class, () -> assertGeneratedJsonMatchesCommittedText(generatedJson, stalePath));

    assertEquals("Strict drift check must not rewrite stale JSON",
        staleJson, Files.readString(stalePath, StandardCharsets.UTF_8));
    assertEquals("Strict drift check must leave stale JSON metadata untouched",
        modifiedBefore, Files.getLastModifiedTime(stalePath));
  }

  @Test
  public void driftFailureMessageSeparatesStrictCheckFromRegeneration() {
    assertTrue(GENERATED_JSON_DRIFT_MESSAGE.contains(COMMITTED_JSON_RESOURCE));
    assertTrue(GENERATED_JSON_DRIFT_MESSAGE.contains("Run the strict drift check without the write property"));
    assertTrue(GENERATED_JSON_DRIFT_MESSAGE.contains(STRICT_DRIFT_CHECK_COMMAND));
    assertTrue(GENERATED_JSON_DRIFT_MESSAGE.contains("If an intentional legacy registry change requires regeneration"));
    assertTrue(GENERATED_JSON_DRIFT_MESSAGE.contains(REGENERATION_COMMAND));
    assertFalse(STRICT_DRIFT_CHECK_COMMAND.contains(TextMigrationParityTestSupport.WRITE_JSON_PROPERTY));
    assertTrue(REGENERATION_COMMAND.contains(TextMigrationParityTestSupport.WRITE_JSON_PROPERTY + "=true"));
  }

  @Test
  public void generatedTextMigrationJsonMatchesCommittedResourceCanonically() throws Exception {
    Path generatedPath = temporaryFolder.newFile("text-migrations.json").toPath();

    TextMigrationParityTestSupport.writeLegacyRegistryJson(generatedPath);

    assertEquals(TextMigrationParityTestSupport.jsonTree(TextMigrationParityTestSupport.committedJsonPath()),
        TextMigrationParityTestSupport.jsonTree(generatedPath));
  }

  @Test
  public void generatedTextMigrationJsonLoadsToLegacyRegistryDefinitions() throws Exception {
    Path generatedPath = temporaryFolder.newFile("text-migrations.json").toPath();

    TextMigrationParityTestSupport.writeLegacyRegistryJson(generatedPath);

    assertEquals(TextMigrationParityTestSupport.legacyRegistryData(),
        TextMigrationParityTestSupport.dataFromJson(generatedPath));
  }

  @Test
  public void committedTextMigrationJsonRemainsLoadableByDefaultRegistryPath() throws Exception {
    assertTrue(TextMigrationParityTestSupport.committedJsonPath().toFile().isFile());
    assertEquals(TextMigrationParityTestSupport.dataFromJson(TextMigrationParityTestSupport.committedJsonPath()),
        TextMigrationParityTestSupport.runtimeJsonRegistryData());
  }

  private static void assertGeneratedJsonMatchesCommittedText(String generatedJson, Path committedJsonPath) throws Exception {
    assertEquals(GENERATED_JSON_DRIFT_MESSAGE, Files.readString(committedJsonPath, StandardCharsets.UTF_8), generatedJson);
  }
}
