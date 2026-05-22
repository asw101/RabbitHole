package org.alice.ide;

import org.alice.ide.uricontent.UriProjectLoader;
import org.junit.Test;
import org.lgna.croquet.history.UserActivity;
import org.lgna.project.Project;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.NamedUserConstructor;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserPackage;
import org.lgna.project.ast.UserParameter;

import java.io.File;
import java.net.URI;
import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.*;

public class ProjectStateTest {
  @Test
  public void loaderStateDelegatesToUriProjectLoader() throws Exception {
    ProjectState state = new ProjectState();
    File file = new File("build/test-state/world.a3p");
    state.setUriProjectLoader(new StubUriProjectLoader(file, false, true));

    assertEquals(file.toURI(), state.getUri());
    assertFalse(state.isNewProject());
    assertFalse(state.isBackup());
    assertEquals(file.getCanonicalFile(), state.getMainProjectFile().getCanonicalFile());
    assertTrue(state.hasUriProjectLoaderThatShouldBeSaved());
  }

  @Test
  public void beginNewProjectFinishesOpenAndPreviousActivities() {
    ProjectState state = new ProjectState();
    UserActivity overallUserActivity = new UserActivity();
    UserActivity openActivity = overallUserActivity.newChildActivity();

    UserActivity firstProjectActivity = state.beginNewProject(overallUserActivity, openActivity);
    UserActivity secondOpenActivity = overallUserActivity.newChildActivity();
    UserActivity secondProjectActivity = state.beginNewProject(overallUserActivity, secondOpenActivity);

    assertTrue(openActivity.isSuccessfullyCompleted());
    assertTrue(secondOpenActivity.isSuccessfullyCompleted());
    assertTrue(firstProjectActivity.isSuccessfullyCompleted());
    assertSame(secondProjectActivity, state.getProjectActivity());
  }

  @Test
  public void visibleOpenActivityHidesCurrentProjectActivityOnly() {
    ProjectState state = new ProjectState();
    UserActivity overallUserActivity = new UserActivity();
    UserActivity projectActivity = state.beginNewProject(overallUserActivity, null);
    UserActivity foreignActivity = overallUserActivity.newChildActivity();

    assertNull(state.getVisibleOpenActivity(projectActivity));
    assertSame(foreignActivity, state.getVisibleOpenActivity(foreignActivity));
  }

  @Test
  public void sanitizeProjectRemovesNullMembersAndReportsFixes() {
    ProjectState state = new ProjectState();
    UserMethod keptMethod = new UserMethod("doThing", void.class, new UserParameter[0], new BlockStatement());
    UserField keptField = new UserField("actor", String.class);
    NamedUserType type = new NamedUserType("Scene", new UserPackage("test"), Object.class,
        new NamedUserConstructor[0], new UserMethod[]{keptMethod, null}, new UserField[]{keptField, null});
    Project project = new Project(type, Set.of(type), Collections.emptySet(), Project.SceneCameraType.WindowCamera);

    String message = state.sanitizeProject(project);

    assertEquals(1, type.getDeclaredMethods().size());
    assertEquals(1, type.getDeclaredFields().size());
    assertTrue(message.contains("null method was removed from Scene."));
    assertTrue(message.contains("null field was removed from Scene."));
  }

  private static final class StubUriProjectLoader extends UriProjectLoader {
    private final URI uri;
    private final boolean newProject;
    private final boolean shouldBeSaved;

    private StubUriProjectLoader(File file, boolean newProject, boolean shouldBeSaved) {
      super(false);
      this.uri = file.toURI();
      this.newProject = newProject;
      this.shouldBeSaved = shouldBeSaved;
    }

    @Override
    public URI getUri() {
      return this.uri;
    }

    @Override
    public boolean isNewProject() {
      return this.newProject;
    }

    @Override
    public boolean shouldBeSaved() {
      return this.shouldBeSaved;
    }

    @Override
    protected Project load() {
      return null;
    }
  }
}
