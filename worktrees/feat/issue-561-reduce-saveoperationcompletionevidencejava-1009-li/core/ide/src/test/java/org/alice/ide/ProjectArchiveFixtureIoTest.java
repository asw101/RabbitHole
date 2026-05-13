package org.alice.ide;

import org.junit.Test;
import org.lgna.project.Project;
import org.lgna.project.io.IoUtilities;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

public class ProjectArchiveFixtureIoTest {
  @Test
  public void readsBundledStarterProjectFixtureWhenAvailable() throws Exception {
    File fixture = new File("../resources/src/application/resources/starter-projects/lagoonMinimum.a3p");
    assumeTrue("starter project fixture is not available in this checkout", fixture.isFile());

    Project project = IoUtilities.readProject(fixture);

    assertNotNull(project);
    assertNotNull(project.getProgramType());
    assertFalse(project.getProgramType().getName().isBlank());
  }
}
