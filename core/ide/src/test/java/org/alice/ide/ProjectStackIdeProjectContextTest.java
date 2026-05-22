package org.alice.ide;

import org.alice.ide.testing.ProjectContextTestCase;
import org.junit.Test;
import org.lgna.project.Project;

import static org.junit.Assert.assertSame;

public class ProjectStackIdeProjectContextTest extends ProjectContextTestCase {
  @Test
  public void peekUpToDateProjectFallsBackToActiveIdeProject() {
    Project project = ProjectStack.peekUpToDateProject();
    assertSame(fixture.project, project);
  }
}
