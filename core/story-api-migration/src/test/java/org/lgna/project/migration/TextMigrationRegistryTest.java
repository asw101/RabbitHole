package org.lgna.project.migration;

import org.junit.Test;
import org.lgna.project.Version;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class TextMigrationRegistryTest {
  private static final List<String> EXPECTED_VERSIONS = Arrays.asList(
      "3.1.8.0.0", "3.1.9.0.0", "3.1.11.0.0", "3.1.14.0.0",
      "3.1.15.1.0", "3.1.20.0.0", "3.1.33.0.0", "3.1.34.0.0",
      "3.1.35.0.0", "3.1.38.0.0", "3.1.39.0.0", "3.1.48.0.0",
      "3.1.58.0.0", "3.1.59.0.0", "3.1.68.0.0", "3.1.69.0.0",
      "3.1.70.0.0", "3.1.85.0.0", "3.1.92.0.0", "3.1.93.0.0",
      "3.2.108.0.0", "3.2.110.0.0", "3.2.111.0.0", "3.2.112.0.0",
      "3.2.113.0.0", "3.3.0.0.0", "3.4.0.0", "3.9.0.0");

  @Test
  public void createAllReturnsRuntimeJsonMigrationsInLegacyRegistryOrder() throws Exception {
    List<TextMigrationParityTestSupport.MigrationData> expected = TextMigrationParityTestSupport.legacyRegistryData();
    List<TextMigrationParityTestSupport.MigrationData> actual = TextMigrationParityTestSupport.runtimeJsonRegistryData();

    assertEquals("Runtime JSON registry must match the authoritative legacy registry sequence", expected, actual);
  }

  @Test
  public void legacyPropertyReturnsTheSameOrderedMigrationSequence() throws Exception {
    List<TextMigrationParityTestSupport.MigrationData> expected = TextMigrationParityTestSupport.legacyRegistryData();
    List<TextMigrationParityTestSupport.MigrationData> actual = TextMigrationParityTestSupport.legacyPropertyRegistryData();

    assertEquals("Legacy registry system property must preserve the authoritative sequence", expected, actual);
  }

  @Test
  public void createAllContainsExpectedVersionsInOrder() throws Exception {
    List<TextMigrationParityTestSupport.MigrationData> all = TextMigrationParityTestSupport.runtimeJsonRegistryData();

    assertEquals("Text migration versions must stay order-sensitive", EXPECTED_VERSIONS,
        TextMigrationParityTestSupport.versionsOf(all));
  }

  @Test
  public void createAllContainsNoNulls() throws Exception {
    TextMigration[] all = TextMigrationParityTestSupport.runtimeJsonRegistrySequence();

    for (int i = 0; i < all.length; i++) {
      assertNotNull("Migration at index " + i + " is null", all[i]);
    }
  }

  @Test
  public void createAllVersionsAreStrictlyIncreasing() throws Exception {
    TextMigration[] all = TextMigrationParityTestSupport.runtimeJsonRegistrySequence();
    Version previous = null;

    for (TextMigration migration : all) {
      Version version = migration.getResultVersion();
      if (previous != null) {
        assertTrue(previous + " should be before " + version, previous.compareTo(version) < 0);
      }
      previous = version;
    }
  }

  @Test
  public void createAllVersionsHaveNoDuplicates() throws Exception {
    TextMigration[] all = TextMigrationParityTestSupport.runtimeJsonRegistrySequence();
    Set<String> seen = new HashSet<>();

    for (TextMigration migration : all) {
      String version = migration.getResultVersion().toString();
      assertTrue("Duplicate version: " + version, seen.add(version));
    }
  }

  @Test
  public void createAllPreservesFirstAndLastMigrationBoundaries() throws Exception {
    TextMigration[] all = TextMigrationParityTestSupport.runtimeJsonRegistrySequence();

    assertEquals("3.1.8.0.0", all[0].getResultVersion().toString());
    assertEquals("3.9.0.0", all[all.length - 1].getResultVersion().toString());
  }

  @Test
  public void createAllPreservesRepresentativeSegmentPairs() throws Exception {
    TextMigration[] all = TextMigrationParityTestSupport.runtimeJsonRegistrySequence();

    assertTrue(TextMigrationParityTestSupport.containsPair(
        TextMigrationParityTestSupport.migrationForVersion(all, "3.1.9.0.0"),
        "ARMOIRE_CLOTHING",
        null));
    assertTrue(TextMigrationParityTestSupport.containsPair(
        TextMigrationParityTestSupport.migrationForVersion(all, "3.1.34.0.0"),
        "org.lgna.story.Program",
        "org.lgna.story.SProgram"));
    assertTrue(TextMigrationParityTestSupport.containsPair(
        TextMigrationParityTestSupport.migrationForVersion(all, "3.1.59.0.0"),
        "name=\"org.lgna.story.resources.prop.JungleShrubResource",
        "name=\"org.lgna.story.resources.prop.JunglePlantResource"));
    assertTrue(TextMigrationParityTestSupport.containsPair(
        TextMigrationParityTestSupport.migrationForVersion(all, "3.2.110.0.0"),
        "name=\"OVAL\">\\s*<declaringClass name=\"org.lgna.story.resources.prop.SandDunesResource\"",
        "name=\"OVAL_DESERT\"> <declaringClass name=\"org.lgna.story.resources.prop.SandDunesResource\""));
    assertTrue(TextMigrationParityTestSupport.containsPair(
        TextMigrationParityTestSupport.migrationForVersion(all, "3.2.111.0.0"),
        "name=\"BONE_PILE\">\\s*<declaringClass name=\"org.lgna.story.resources.prop.BonesResource\"",
        "name=\"DEFAULT\"> <declaringClass name=\"org.lgna.story.resources.prop.BonePileResource\""));
    TextMigration jointIdMigration = TextMigrationParityTestSupport.migrationForVersion(all, "3.1.93.0.0");
    assertTrue(TextMigrationParityTestSupport.containsPair(
        jointIdMigration,
        "name=\"TAIL\">\\s*<declaringClass name=\"org.lgna.story.resources.biped.BigBadWolfResource\"",
        "name=\"TAIL_0\"> <declaringClass name=\"org.lgna.story.resources.biped.BigBadWolfResource\""));
    assertTrue(TextMigrationParityTestSupport.containsPair(
        TextMigrationParityTestSupport.migrationForVersion(all, "3.1.93.0.0"),
        "name=\"LEFT_PLUMAGE_1\">\\s*<declaringClass name=\"org.lgna.story.resources.flyer.PeacockResource\"",
        "name=\"PLUMAGE_LEFT_TIP\"> <declaringClass name=\"org.lgna.story.resources.flyer.PeacockResource\""));
  }

  @Test
  public void jsonRegistryMatchesLegacyRegistryForRepresentativeOldProjectFragments() throws Exception {
    TextMigration[] fromJson = TextMigrationParityTestSupport.runtimeJsonRegistrySequence();
    TextMigration[] fromLegacy = TextMigrationParityTestSupport.legacyRegistrySequence();

    assertMigrationBehaviorParity(fromLegacy, fromJson, "3.1.34.0.0",
        "org.lgna.story.Program",
        "org.lgna.story.SProgram");
    assertMigrationBehaviorParity(fromLegacy, fromJson, "3.2.110.0.0",
        "name=\"OVAL\">\n<declaringClass name=\"org.lgna.story.resources.prop.SandDunesResource\"",
        "name=\"OVAL_DESERT\"> <declaringClass name=\"org.lgna.story.resources.prop.SandDunesResource\"");
    assertMigrationBehaviorParity(fromLegacy, fromJson, "3.2.111.0.0",
        "name=\"BONE_PILE\">\n<declaringClass name=\"org.lgna.story.resources.prop.BonesResource\"",
        "name=\"DEFAULT\"> <declaringClass name=\"org.lgna.story.resources.prop.BonePileResource\"");
    assertMigrationBehaviorParity(fromLegacy, fromJson, "3.1.59.0.0",
        "name=\"PIRATE_SHIP\">\n<declaringClass name=\"org.lgna.story.resources.prop.PirateShipResource\"",
        "name=\"DEFAULT\"> <declaringClass name=\"org.lgna.story.resources.prop.PirateShipPropResource\"");
    assertMigrationBehaviorParity(fromLegacy, fromJson, "3.1.93.0.0",
        "name=\"TAIL\">\n<declaringClass name=\"org.lgna.story.resources.biped.BigBadWolfResource\"",
        "name=\"TAIL_0\"> <declaringClass name=\"org.lgna.story.resources.biped.BigBadWolfResource\"");
    assertMigrationBehaviorParity(fromLegacy, fromJson, "3.1.93.0.0",
        "name=\"LEFT_PLUMAGE_1\">\n<declaringClass name=\"org.lgna.story.resources.flyer.PeacockResource\"",
        "name=\"PLUMAGE_LEFT_TIP\"> <declaringClass name=\"org.lgna.story.resources.flyer.PeacockResource\"");
  }

  @Test
  public void jsonRegistryMatchesLegacyRegistryForSequentialAndVersionGatedMigrationFlow() throws Exception {
    TextMigration[] fromJson = TextMigrationParityTestSupport.runtimeJsonRegistrySequence();
    TextMigration[] fromLegacy = TextMigrationParityTestSupport.legacyRegistrySequence();
    String oldDresser = String.join("\n",
        "<type name=\"org.lgna.story.resources.dresser.DresserCentralAsian\"/>",
        "<declaringClass name=\"org.lgna.story.resources.dresser.DresserCentralAsian\"/>"
    );
    String expectedMigratedDresser = String.join("\n",
        "<type name=\"org.lgna.story.resources.prop.DresserResource\"/>",
        "<declaringClass name=\"org.lgna.story.resources.prop.DresserResource\"/>"
    );

    assertEquals(expectedMigratedDresser, migrateThrough(fromLegacy, oldDresser, "3.1.19.0.0"));
    assertEquals(expectedMigratedDresser, migrateThrough(fromJson, oldDresser, "3.1.19.0.0"));
    assertEquals(oldDresser, migrateThrough(fromLegacy, oldDresser, "3.1.20.0.0"));
    assertEquals(oldDresser, migrateThrough(fromJson, oldDresser, "3.1.20.0.0"));
  }

  @Test
  public void legacyRegistryComponentsAssembleIntoTheAuthoritativeSequence() throws Exception {
    TextMigration[] early = TextMigrationRegistrySmallVersions.createEarly();
    TextMigration[] v3134 = TextMigrationRegistryV3134.create();
    TextMigration[] mid = TextMigrationRegistrySmallVersions.createMid();
    TextMigration[] v3159 = TextMigrationRegistryV3159.create();
    TextMigration[] late = TextMigrationRegistryLateVersions.create();
    TextMigration[] all = TextMigrationParityTestSupport.legacyRegistrySequence();

    int expectedTotal = early.length + v3134.length + mid.length + v3159.length + late.length;
    assertEquals("Sum of parts must equal whole", expectedTotal, all.length);

    int offset = 0;
    offset = assertSegmentMatches("early", early, all, offset);
    offset = assertSegmentMatches("v3134", v3134, all, offset);
    offset = assertSegmentMatches("mid", mid, all, offset);
    offset = assertSegmentMatches("v3159", v3159, all, offset);
    offset = assertSegmentMatches("late", late, all, offset);
    assertEquals("Every migration must be covered by a segment", all.length, offset);
  }

  @Test
  public void createAllReturnsFreshArrayEachCall() throws Exception {
    TextMigration[] first = TextMigrationParityTestSupport.runtimeJsonRegistrySequence();
    TextMigration[] second = TextMigrationParityTestSupport.runtimeJsonRegistrySequence();

    assertNotSame("createAll must return a new array each time", first, second);
    assertEquals(first.length, second.length);
  }

  private static void assertMigrationBehaviorParity(TextMigration[] expectedMigrations, TextMigration[] actualMigrations,
      String version, String source, String expectedOutput) {
    TextMigration expected = TextMigrationParityTestSupport.migrationForVersion(expectedMigrations, version);
    TextMigration actual = TextMigrationParityTestSupport.migrationForVersion(actualMigrations, version);

    assertEquals("Legacy oracle output for " + version, expectedOutput, expected.migrate(source));
    assertEquals("JSON-loaded output for " + version, expectedOutput, actual.migrate(source));
    assertEquals("JSON-loaded migration must match legacy output for " + version,
        expected.migrate(source), actual.migrate(source));
  }

  private static String migrateThrough(TextMigration[] migrations, String source, String versionText) {
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

  private static int assertSegmentMatches(String segmentName, TextMigration[] segment, TextMigration[] all, int offset)
      throws Exception {
    for (TextMigration migration : segment) {
      assertEquals(segmentName + " mismatch at offset " + offset,
          TextMigrationParityTestSupport.dataOf(new TextMigration[] {migration}).get(0),
          TextMigrationParityTestSupport.dataOf(new TextMigration[] {all[offset]}).get(0));
      offset++;
    }
    return offset;
  }
}
