package org.alice.ide;

import org.junit.Test;
import org.lgna.project.Project;
import org.lgna.project.ast.NamedUserType;

import static org.junit.Assert.*;

public class ProjectStackTest {
  private static Project createTestProject() {
    NamedUserType sceneType = new NamedUserType();
    sceneType.name.setValue("TestScene");
    return new Project(sceneType, Project.SceneCameraType.WindowCamera);
  }

  @Test
  public void pushAndPopReturnsSameProject() {
    Project project = createTestProject();

    ProjectStack.pushProject(project);

    assertSame(project, ProjectStack.popProject());
  }

  @Test
  public void pushTwoProjectsPopsInLastInFirstOutOrder() {
    Project first = createTestProject();
    Project second = createTestProject();
    ProjectStack.pushProject(first);
    ProjectStack.pushProject(second);

    assertSame(second, ProjectStack.popProject());
    assertSame(first, ProjectStack.popProject());
  }

  @Test
  public void popAndCheckProjectReturnsExpectedProjectWhenItMatches() {
    Project project = createTestProject();
    ProjectStack.pushProject(project);

    assertSame(project, ProjectStack.popAndCheckProject(project));
  }

  @Test
  public void peekProjectReturnsTopOfStack() {
    Project project = createTestProject();
    ProjectStack.pushProject(project);
    try {
      assertSame(project, ProjectStack.peekProject());
    } finally {
      ProjectStack.popProject();
    }
  }

  @Test
  public void peekUpToDateProjectReturnsTopOfStack() {
    Project project = createTestProject();
    ProjectStack.pushProject(project);
    try {
      assertSame(project, ProjectStack.peekUpToDateProject());
    } finally {
      ProjectStack.popProject();
    }
  }
}
