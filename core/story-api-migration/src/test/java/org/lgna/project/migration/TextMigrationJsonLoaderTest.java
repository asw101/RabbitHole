package org.lgna.project.migration;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TextMigrationJsonLoaderTest {
  @Test
  public void jsonLoaderMatchesLegacyRegistryDefinitionsExactly() throws Exception {
    TextMigration[] expected = TextMigrationTestHelper.createLegacyMigrations();
    TextMigration[] actual = TextMigrationJsonLoader.load();

    TextMigrationTestHelper.assertDefinitionsEqual(expected, actual);
  }

  @Test
  public void jsonLoaderContainsKnownResolvedPairs() throws Exception {
    TextMigration[] migrations = TextMigrationJsonLoader.load();

    TextMigrationTestHelper.assertContainsPair(TextMigrationTestHelper.migrationForVersion(migrations, "3.1.9.0.0"),
        "ARMOIRE_CLOTHING",
        null);
    TextMigrationTestHelper.assertContainsPair(TextMigrationTestHelper.migrationForVersion(migrations, "3.1.34.0.0"),
        "org.lgna.story.Program",
        "org.lgna.story.SProgram");
    TextMigrationTestHelper.assertContainsPair(TextMigrationTestHelper.migrationForVersion(migrations, "3.2.110.0.0"),
        "name=\"OVAL\">\\s*<declaringClass name=\"org.lgna.story.resources.prop.SandDunesResource\"",
        "name=\"OVAL_DESERT\"> <declaringClass name=\"org.lgna.story.resources.prop.SandDunesResource\"");
  }

  @Test
  public void jsonLoaderPreservesNullNoReplacementSemantics() throws Exception {
    TextMigration migration = TextMigrationTestHelper.migrationForVersion(TextMigrationJsonLoader.load(), "3.1.9.0.0");
    TextMigrationTestHelper.PairData firstPair = TextMigrationTestHelper.pairAt(migration, 0);

    assertEquals("ARMOIRE_CLOTHING", firstPair.pattern);
    assertNull("JSON null replacement must reload as Java null", firstPair.replacement);
    assertEquals("JSON null replacement must match MigrationManager.NO_REPLACEMENT",
        MigrationManager.NO_REPLACEMENT, firstPair.replacement);
  }
}
