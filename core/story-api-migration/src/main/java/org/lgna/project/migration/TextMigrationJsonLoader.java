package org.lgna.project.migration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.lgna.project.Version;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
final class TextMigrationJsonLoader {
  private static final String RESOURCE_PATH = "migrations/text-migrations.json";
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  static TextMigration[] load() {
    try (InputStream inputStream = TextMigrationJsonLoader.class.getClassLoader().getResourceAsStream(RESOURCE_PATH)) {
      if (inputStream == null) {
        throw new IllegalStateException("Missing text migration resource: " + RESOURCE_PATH);
      }

      MigrationJson[] migrations = OBJECT_MAPPER.readValue(inputStream, MigrationJson[].class);
      TextMigration[] textMigrations = new TextMigration[migrations.length];
      for (int i = 0; i < migrations.length; i++) {
        textMigrations[i] = migrations[i].toTextMigration();
      }
      return textMigrations;
    } catch (IOException exception) {
      throw new UncheckedIOException("Unable to load text migrations from " + RESOURCE_PATH, exception);
    }
  }

  static final class MigrationJson {
    public String version;
    public ReplacementJson[] replacements = new ReplacementJson[0];

    TextMigration toTextMigration() {
      ReplacementJson[] entries = this.replacements != null ? this.replacements : new ReplacementJson[0];
      TextMigrationRule[] rules = new TextMigrationRule[entries.length];
      for (int i = 0; i < entries.length; i++) {
        ReplacementJson replacement = entries[i];
        rules[i] = TextMigrationRule.replace(
            replacement.pattern,
            replacement.replacement == null ? MigrationManager.NO_REPLACEMENT : replacement.replacement);
      }
      return new TextMigration(new Version(this.version), rules);
    }
  }

  static final class ReplacementJson {
    public String pattern;
    public String replacement;
  }

  private TextMigrationJsonLoader() {
  }
}
