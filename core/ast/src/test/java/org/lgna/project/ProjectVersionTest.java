package org.lgna.project;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ProjectVersionTest {
  @Test
  public void currentVersionTextMatchesCurrentVersionObject() {
    String currentText = ProjectVersion.getCurrentVersionText();
    Version currentVersion = ProjectVersion.getCurrentVersion();

    assertFalse(currentText.isBlank());
    assertTrue(currentVersion.isValid());
    assertEquals(currentText, currentVersion.toString());
  }

  @Test
  public void currentVersionIsCachedSingleton() {
    assertSame(ProjectVersion.getCurrentVersion(), ProjectVersion.getCurrentVersion());
    assertEquals(ProjectVersion.getCurrentVersionText(), ProjectVersion.getCurrentVersionText());
  }
}
