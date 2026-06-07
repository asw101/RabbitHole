package org.lgna.project.migration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.lgna.project.Version;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

final class TextMigrationTestHelper {
  static final String WRITE_JSON_PROPERTY = "org.lgna.project.migration.TextMigrationJsonGenerator.write";

  private static final String RESOURCE_PATH = "migrations/text-migrations.json";
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  static TextMigration[] createLegacyMigrations() {
    TextMigration[] early = TextMigrationRegistrySmallVersions.createEarly();
    TextMigration[] v3134 = TextMigrationRegistryV3134.create();
    TextMigration[] mid = TextMigrationRegistrySmallVersions.createMid();
    TextMigration[] v3159 = TextMigrationRegistryV3159.create();
    TextMigration[] late = TextMigrationRegistryLateVersions.create();

    TextMigration[] all = new TextMigration[early.length + v3134.length + mid.length + v3159.length + late.length];
    int offset = 0;
    System.arraycopy(early, 0, all, offset, early.length);
    offset += early.length;
    System.arraycopy(v3134, 0, all, offset, v3134.length);
    offset += v3134.length;
    System.arraycopy(mid, 0, all, offset, mid.length);
    offset += mid.length;
    System.arraycopy(v3159, 0, all, offset, v3159.length);
    offset += v3159.length;
    System.arraycopy(late, 0, all, offset, late.length);
    return all;
  }

  static TextMigration[] createRegistryMigrationsWithProperty(boolean useLegacyRegistries) {
    String previousValue = System.getProperty(TextMigrationRegistry.USE_LEGACY_REGISTRIES_PROPERTY);
    try {
      System.setProperty(TextMigrationRegistry.USE_LEGACY_REGISTRIES_PROPERTY, Boolean.toString(useLegacyRegistries));
      return TextMigrationRegistry.createAll();
    } finally {
      restoreProperty(TextMigrationRegistry.USE_LEGACY_REGISTRIES_PROPERTY, previousValue);
    }
  }

  static String serializeLegacyMigrationsToJson() throws IOException, ReflectiveOperationException {
    return serializeToJson(createLegacyMigrations());
  }

  static String serializeToJson(TextMigration[] textMigrations) throws IOException, ReflectiveOperationException {
    return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(toMigrationJson(textMigrations));
  }

  static String readCommittedMigrationJson() throws IOException {
    return Files.readString(resolveCommittedMigrationJsonPath(), StandardCharsets.UTF_8);
  }

  static Path resolveCommittedMigrationJsonPath() {
    Path moduleRelative = Paths.get("src/main/resources", RESOURCE_PATH).toAbsolutePath().normalize();
    if (Files.isRegularFile(moduleRelative)) {
      return moduleRelative;
    }

    Path repoRelative = Paths.get("core/story-api-migration/src/main/resources", RESOURCE_PATH).toAbsolutePath().normalize();
    if (Files.isRegularFile(repoRelative)) {
      return repoRelative;
    }

    throw new IllegalStateException("Unable to resolve committed text migration JSON resource");
  }

  static TextMigration migrationForVersion(TextMigration[] migrations, String version) {
    for (TextMigration migration : migrations) {
      if (migration.getResultVersion().toString().equals(version)) {
        return migration;
      }
    }
    throw new AssertionError("No migration found for version " + version);
  }

  static PairData pairAt(TextMigration migration, int pairIndex) throws ReflectiveOperationException {
    List<PairData> pairs = pairsOf(migration);
    if (pairIndex >= pairs.size()) {
      throw new AssertionError("No pair " + pairIndex + " in migration " + migration.getResultVersion());
    }
    return pairs.get(pairIndex);
  }

  static List<PairData> pairsOf(TextMigration textMigration) throws ReflectiveOperationException {
    Field pairsField = TextMigration.class.getDeclaredField("pairs");
    pairsField.setAccessible(true);

    Class<?> pairClass = Class.forName(TextMigration.class.getName() + "$Pair");
    Field patternField = pairClass.getDeclaredField("pattern");
    patternField.setAccessible(true);
    Field replacementField = pairClass.getDeclaredField("replacement");
    replacementField.setAccessible(true);

    Object[] pairs = (Object[]) pairsField.get(textMigration);
    List<PairData> data = new ArrayList<>(pairs.length);
    for (Object pair : pairs) {
      data.add(new PairData(((Pattern) patternField.get(pair)).pattern(), (String) replacementField.get(pair)));
    }
    return data;
  }

  static void assertDefinitionEquals(int migrationIndex, TextMigration expected, TextMigration actual) throws ReflectiveOperationException {
    String context = "migration index " + migrationIndex;
    assertEquals(context + " version", expected.getResultVersion().toString(), actual.getResultVersion().toString());

    List<PairData> expectedPairs = pairsOf(expected);
    List<PairData> actualPairs = pairsOf(actual);
    assertEquals(context + " pair count for version " + expected.getResultVersion(), expectedPairs.size(), actualPairs.size());
    for (int pairIndex = 0; pairIndex < expectedPairs.size(); pairIndex++) {
      PairData expectedPair = expectedPairs.get(pairIndex);
      PairData actualPair = actualPairs.get(pairIndex);
      String pairContext = context + " version " + expected.getResultVersion() + " pair index " + pairIndex;
      assertEquals(pairContext + " regex pattern", expectedPair.pattern, actualPair.pattern);
      assertEquals(pairContext + " replacement string", expectedPair.replacement, actualPair.replacement);
      assertEquals(pairContext + " null/no-replacement semantics", expectedPair.replacement == null, actualPair.replacement == null);
    }
  }

  static void assertDefinitionsEqual(TextMigration[] expected, TextMigration[] actual) throws ReflectiveOperationException {
    assertEquals("Migration count must match legacy registry oracle", expected.length, actual.length);
    for (int migrationIndex = 0; migrationIndex < expected.length; migrationIndex++) {
      assertDefinitionEquals(migrationIndex, expected[migrationIndex], actual[migrationIndex]);
    }
  }

  static void assertContainsPair(TextMigration migration, String expectedPattern, String expectedReplacement) throws ReflectiveOperationException {
    List<PairData> pairs = pairsOf(migration);
    for (int pairIndex = 0; pairIndex < pairs.size(); pairIndex++) {
      PairData pair = pairs.get(pairIndex);
      if (pair.pattern.equals(expectedPattern) && equalsNullable(pair.replacement, expectedReplacement)) {
        return;
      }
    }
    throw new AssertionError("Missing pair in version " + migration.getResultVersion()
        + ": pattern=" + expectedPattern + ", replacement=" + expectedReplacement);
  }

  static String migrateThrough(TextMigration[] migrations, String source, String versionText) {
    String result = source;
    Version version = new Version(versionText);
    for (TextMigration migration : migrations) {
      if (migration.isApplicable(version)) {
        result = migration.migrate(result);
        version = migration.getResultVersion();
      }
    }
    return result;
  }

  static void restoreProperty(String propertyName, String previousValue) {
    if (previousValue == null) {
      System.clearProperty(propertyName);
    } else {
      System.setProperty(propertyName, previousValue);
    }
  }

  private static List<MigrationJson> toMigrationJson(TextMigration[] textMigrations) throws ReflectiveOperationException {
    List<MigrationJson> migrations = new ArrayList<>(textMigrations.length);
    for (TextMigration textMigration : textMigrations) {
      MigrationJson migration = new MigrationJson();
      migration.version = textMigration.getResultVersion().toString();

      List<PairData> pairs = pairsOf(textMigration);
      migration.replacements = new ArrayList<>(pairs.size());
      for (PairData pair : pairs) {
        ReplacementJson replacement = new ReplacementJson();
        replacement.pattern = pair.pattern;
        replacement.replacement = pair.replacement;
        migration.replacements.add(replacement);
      }
      migrations.add(migration);
    }
    return migrations;
  }

  private static boolean equalsNullable(String left, String right) {
    return left == null ? right == null : left.equals(right);
  }

  static final class PairData {
    final String pattern;
    final String replacement;

    private PairData(String pattern, String replacement) {
      this.pattern = pattern;
      this.replacement = replacement;
    }
  }

  static final class MigrationJson {
    public String version;
    public List<ReplacementJson> replacements = new ArrayList<>();
  }

  static final class ReplacementJson {
    public String pattern;
    public String replacement;
  }

  private TextMigrationTestHelper() {
  }
}
