package org.lgna.project.migration;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

import static org.junit.Assert.assertEquals;

public class TextMigrationJsonGeneratorTest {
  @Test
  public void generatedTextMigrationJsonMatchesCommittedResourceExactly() throws Exception {
    String generatedJson = TextMigrationTestHelper.serializeLegacyMigrationsToJson();
    Path committedJsonPath = TextMigrationTestHelper.resolveCommittedMigrationJsonPath();

    if (Boolean.getBoolean(TextMigrationTestHelper.WRITE_JSON_PROPERTY)) {
      Files.writeString(committedJsonPath, generatedJson, StandardCharsets.UTF_8);
    }

    assertEquals("Generated text migrations JSON must match the committed resource exactly",
        TextMigrationTestHelper.readCommittedMigrationJson(), generatedJson);
  }

  @Test
  public void generatorIsReadOnlyUnlessExplicitWritePropertyIsSet() throws Exception {
    String previousValue = System.getProperty(TextMigrationTestHelper.WRITE_JSON_PROPERTY);
    Path committedJsonPath = TextMigrationTestHelper.resolveCommittedMigrationJsonPath();
    FileTime modifiedBefore = Files.getLastModifiedTime(committedJsonPath);
    try {
      System.clearProperty(TextMigrationTestHelper.WRITE_JSON_PROPERTY);
      TextMigrationTestHelper.serializeLegacyMigrationsToJson();

      assertEquals("Normal generator validation must not modify text-migrations.json",
          modifiedBefore, Files.getLastModifiedTime(committedJsonPath));
    } finally {
      TextMigrationTestHelper.restoreProperty(TextMigrationTestHelper.WRITE_JSON_PROPERTY, previousValue);
    }
  }
}
