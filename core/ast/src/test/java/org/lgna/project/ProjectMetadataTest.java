package org.lgna.project;

import edu.cmu.cs.dennisc.java.io.TextFileUtilities;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ProjectMetadataTest {
  @Test
  public void projectVersionReadsTheBundledVersionText() {
    String versionText = TextFileUtilities.read(Version.class.getResourceAsStream("Version.txt")).trim();

    assertEquals(versionText, ProjectVersion.getCurrentVersionText());
    assertEquals(versionText, ProjectVersion.getCurrentVersion().toString());
    assertSame(ProjectVersion.getCurrentVersion(), ProjectVersion.getCurrentVersion());
  }

  @Test
  public void licenseLoadsBundledText() {
    assertNotNull(License.TEXT);
    assertFalse(License.TEXT.trim().isEmpty());
    assertTrue(License.TEXT.contains("Carnegie Mellon University"));
  }

  @Test
  public void versionNotSupportedExceptionRetainsProvidedVersions() {
    VersionNotSupportedException exception = new VersionNotSupportedException(3.0, 2.5);

    assertEquals(3.0, exception.getMinimumSupportedVersion(), 0.0);
    assertEquals(2.5, exception.getVersion(), 0.0);
  }
}
