package org.lgna.project.migration;

import org.lgna.project.Version;
import org.junit.Test;

import static org.lgna.project.migration.TextMigrationRule.replace;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MigrationCoreBehaviorTest {
  private static final class RecordingMigration extends AbstractMigration {
    private RecordingMigration(String resultVersion) {
      super(new Version(resultVersion));
    }
  }

  @Test
  public void abstractMigrationUsesResultVersionForApplicabilityAndToString() {
    RecordingMigration migration = new RecordingMigration("3.2.110.0.0");

    assertTrue(migration.isApplicable(new Version("3.2.109.0.0")));
    assertFalse(migration.isApplicable(new Version("3.2.110.0.0")));
    assertFalse(migration.isApplicable(new Version("3.2.111.0.0")));
    assertEquals("3.2.110.0.0", migration.getResultVersion().toString());
    assertEquals("RecordingMigration[3.2.110.0.0]", migration.toString());
  }

  @Test
  public void textMigrationAppliesPairsSequentiallyAndLeavesUnmatchedTextAlone() {
    TextMigration migration = new TextMigration(
        new Version("3.2.110.0.0"),
        replace("legacy", "modern"),
        replace("modern-cat", "cheshire")
    );

    assertEquals("modern cheshire untouched", migration.migrate("legacy legacy-cat untouched"));
    assertEquals("unchanged text", migration.migrate("unchanged text"));
  }

  @Test
  public void factoryMigrationBuildsExpectedVersionAndReplacementBehavior() {
    TextMigration migration = ProjectMigrationTextMigrationFactory.createVersion3_2_110TextMigration();
    String source = String.join("\n",
        "name=\"OVAL\">",
        "<declaringClass name=\"org.lgna.story.resources.prop.SandDunesResource\"",
        "name=\"DEFAULT\">",
        "<declaringClass name=\"org.lgna.story.resources.prop.AncientTempleArchResource\""
    );

    String migrated = migration.migrate(source);

    assertEquals("3.2.110.0.0", migration.getResultVersion().toString());
    assertTrue(migrated.contains("OVAL_DESERT"));
    assertTrue(migrated.contains("INDIA_BRICK_D"));
    assertFalse(migrated.contains("name=\"OVAL\">"));
  }
}
