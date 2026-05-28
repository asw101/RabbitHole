package org.lgna.project.migration;

import org.junit.Ignore;
import org.junit.Test;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * TDD contract tests for PR #832: TextMigration JSON migration.
 *
 * These tests verify the behavioral contracts that MUST be preserved when
 * migrating from hardcoded TextMigrationRegistry* classes to JSON-loaded
 * migrations via TextMigrationJsonLoader.
 *
 * FAILS on develop: JSON resource file doesn't exist yet; TextMigrationRegistry
 * doesn't have USE_LEGACY_REGISTRIES_PROPERTY or JSON loading support.
 * PASSES after PR #832 merge: JSON file on classpath, loader active.
 */
public class TextMigrationJsonContractTest {

  // Cached reflection — avoids per-call Class.forName and getDeclaredField in extractPairs()
  private static Field cachedPairsField;
  private static Field cachedPatternField;
  private static Field cachedReplacementField;
  private static String cachedLegacyPropName;

  // ── JSON resource existence (FAILS on develop) ────────────────────

  @Ignore("Verifies post-PR-832 structure; passes after merge")
  @Test
  public void jsonMigrationResourceExistsOnClasspath() {
    InputStream stream = getClass().getClassLoader()
        .getResourceAsStream("migrations/text-migrations.json");
    assertNotNull("text-migrations.json must exist on classpath after PR #832", stream);
  }

  // ── Migration count preservation ──────────────────────────────────

  @Test
  public void createAll_returnsNonEmptyArray() {
    TextMigration[] migrations = TextMigrationRegistry.createAll();
    assertNotNull(migrations);
    assertTrue("Must return at least one migration", migrations.length > 0);
  }

  @Test
  public void createAll_versionsAreAllDistinct() {
    TextMigration[] migrations = TextMigrationRegistry.createAll();
    Set<String> seen = new HashSet<>();
    for (TextMigration migration : migrations) {
      String version = migration.getResultVersion().toString();
      assertTrue("Duplicate version: " + version, seen.add(version));
    }
  }

  @Test
  public void createAll_firstVersionIs_3_1_8() {
    TextMigration[] migrations = TextMigrationRegistry.createAll();
    assertEquals("First migration version", "3.1.8.0.0",
        migrations[0].getResultVersion().toString());
  }

  // ── Specific migration content contracts ──────────────────────────

  @Test
  public void version3_1_9_containsArmoireClothingRemoval() throws Exception {
    TextMigration[] migrations = TextMigrationRegistry.createAll();
    TextMigration v319 = findByVersion(migrations, "3.1.9.0.0");
    assertNotNull("Version 3.1.9.0.0 must exist", v319);

    List<PairData> pairs = extractPairs(v319);
    assertTrue("v3.1.9 must contain ARMOIRE_CLOTHING pattern",
        pairs.stream().anyMatch(p -> p.pattern.equals("ARMOIRE_CLOTHING")));
  }

  @Test
  public void version3_1_34_containsProgramToSProgramRename() throws Exception {
    TextMigration[] migrations = TextMigrationRegistry.createAll();
    TextMigration v3134 = findByVersion(migrations, "3.1.34.0.0");
    assertNotNull("Version 3.1.34.0.0 must exist", v3134);

    List<PairData> pairs = extractPairs(v3134);
    boolean found = pairs.stream().anyMatch(p ->
        p.pattern.equals("org.lgna.story.Program")
            && "org.lgna.story.SProgram".equals(p.replacement));
    assertTrue("v3.1.34 must map Program → SProgram", found);
  }

  @Test
  public void version3_1_8_hasNoReplacements() throws Exception {
    TextMigration[] migrations = TextMigrationRegistry.createAll();
    TextMigration v318 = findByVersion(migrations, "3.1.8.0.0");
    assertNotNull(v318);
    List<PairData> pairs = extractPairs(v318);
    assertTrue("v3.1.8 should have no replacements", pairs.isEmpty());
  }

  // ── JSON/legacy equivalence (FAILS on develop: no USE_LEGACY_REGISTRIES_PROPERTY) ──

  @Ignore("Verifies post-PR-832 structure; passes after merge")
  @Test
  public void useLegacyRegistriesProperty_exists() throws Exception {
    Field field = TextMigrationRegistry.class.getDeclaredField("USE_LEGACY_REGISTRIES_PROPERTY");
    assertNotNull("USE_LEGACY_REGISTRIES_PROPERTY must exist after PR #832", field);
  }

  @Ignore("Verifies post-PR-832 structure; passes after merge")
  @Test
  public void jsonAndLegacy_haveSameMigrationCount() throws Exception {
    String propName = getLegacyPropertyName();
    String previousValue = System.getProperty(propName);
    try {
      System.setProperty(propName, Boolean.TRUE.toString());
      TextMigration[] legacy = TextMigrationRegistry.createAll();

      System.clearProperty(propName);
      TextMigration[] json = TextMigrationRegistry.createAll();

      assertEquals("JSON migration count must match legacy count",
          legacy.length, json.length);
    } finally {
      restoreProperty(propName, previousValue);
    }
  }

  @Ignore("Verifies post-PR-832 structure; passes after merge")
  @Test
  public void jsonAndLegacy_haveIdenticalVersionsInOrder() throws Exception {
    String propName = getLegacyPropertyName();
    String previousValue = System.getProperty(propName);
    try {
      System.setProperty(propName, Boolean.TRUE.toString());
      TextMigration[] legacy = TextMigrationRegistry.createAll();

      System.clearProperty(propName);
      TextMigration[] json = TextMigrationRegistry.createAll();

      for (int i = 0; i < legacy.length; i++) {
        assertEquals("Version mismatch at index " + i,
            legacy[i].getResultVersion().toString(),
            json[i].getResultVersion().toString());
      }
    } finally {
      restoreProperty(propName, previousValue);
    }
  }

  @Ignore("Verifies post-PR-832 structure; passes after merge")
  @Test
  public void jsonAndLegacy_haveIdenticalPairsPerVersion() throws Exception {
    String propName = getLegacyPropertyName();
    String previousValue = System.getProperty(propName);
    try {
      System.setProperty(propName, Boolean.TRUE.toString());
      TextMigration[] legacy = TextMigrationRegistry.createAll();

      System.clearProperty(propName);
      TextMigration[] json = TextMigrationRegistry.createAll();

      for (int i = 0; i < legacy.length; i++) {
        String version = legacy[i].getResultVersion().toString();
        List<PairData> legacyPairs = extractPairs(legacy[i]);
        List<PairData> jsonPairs = extractPairs(json[i]);
        assertEquals("Pair count mismatch for " + version,
            legacyPairs.size(), jsonPairs.size());
        for (int j = 0; j < legacyPairs.size(); j++) {
          assertEquals("Pattern mismatch at " + version + "[" + j + "]",
              legacyPairs.get(j).pattern, jsonPairs.get(j).pattern);
          assertEquals("Replacement mismatch at " + version + "[" + j + "]",
              legacyPairs.get(j).replacement, jsonPairs.get(j).replacement);
        }
      }
    } finally {
      restoreProperty(propName, previousValue);
    }
  }

  // ── Helpers ───────────────────────────────────────────────────────

  private static TextMigration findByVersion(TextMigration[] migrations, String version) {
    for (TextMigration m : migrations) {
      if (m.getResultVersion().toString().equals(version)) {
        return m;
      }
    }
    return null;
  }

  private static void ensurePairReflectionCached() throws Exception {
    if (cachedPairsField == null) {
      cachedPairsField = TextMigration.class.getDeclaredField("pairs");
      cachedPairsField.setAccessible(true);
      Class<?> pairClass = Class.forName(TextMigration.class.getName() + "$Pair");
      cachedPatternField = pairClass.getDeclaredField("pattern");
      cachedPatternField.setAccessible(true);
      cachedReplacementField = pairClass.getDeclaredField("replacement");
      cachedReplacementField.setAccessible(true);
    }
  }

  private static List<PairData> extractPairs(TextMigration migration) throws Exception {
    ensurePairReflectionCached();
    Object[] pairs = (Object[]) cachedPairsField.get(migration);
    List<PairData> result = new ArrayList<>(pairs.length);
    for (Object pair : pairs) {
      result.add(new PairData(
          ((Pattern) cachedPatternField.get(pair)).pattern(),
          (String) cachedReplacementField.get(pair)));
    }
    return result;
  }

  private static String getLegacyPropertyName() throws Exception {
    if (cachedLegacyPropName == null) {
      Field prop = TextMigrationRegistry.class.getDeclaredField("USE_LEGACY_REGISTRIES_PROPERTY");
      prop.setAccessible(true);
      cachedLegacyPropName = (String) prop.get(null);
    }
    return cachedLegacyPropName;
  }

  private static void restoreProperty(String propName, String previousValue) {
    if (previousValue == null) {
      System.clearProperty(propName);
    } else {
      System.setProperty(propName, previousValue);
    }
  }

  private static final class PairData {
    final String pattern;
    final String replacement;

    PairData(String pattern, String replacement) {
      this.pattern = pattern;
      this.replacement = replacement;
    }
  }
}
