package org.lgna.project.migration;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TextMigrationJsonGeneratorTest {
  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void generatedTextMigrationJsonMatchesCommittedResourceExactly() throws Exception {
    String generatedJson = TextMigrationParityTestSupport.legacyRegistryJson();

    if (Boolean.getBoolean(TextMigrationParityTestSupport.WRITE_JSON_PROPERTY)) {
      TextMigrationParityTestSupport.writeLegacyRegistryJson(TextMigrationParityTestSupport.committedJsonPath());
    }

    assertEquals("Generated text migrations JSON must match the committed resource exactly",
        TextMigrationParityTestSupport.committedJson(), generatedJson);
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
}
