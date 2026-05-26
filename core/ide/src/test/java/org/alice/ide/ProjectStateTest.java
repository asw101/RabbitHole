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
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class ProjectStateTest {
  @Test
  public void defaultLoaderStateUsesSafeNullAndFalseValues() {
    ProjectState state = new ProjectState();

    assertNull(state.getUri());
    assertFalse(state.isNewProject());
    assertFalse(state.isBackup());
    assertNull(state.getMainProjectFile());
    assertFalse(state.hasUriProjectLoaderThatShouldBeSaved());
    assertNull(state.getUriProjectLoader());
  }

  @Test
  public void loaderStateDelegatesToUriProjectLoader() throws Exception {
    ProjectState state = new ProjectState();
    File file = new File("build/test-state/world.a3p");
    StubUriProjectLoader loader = new StubUriProjectLoader(file, false, false, true);
    state.setUriProjectLoader(loader);

    assertSame(loader, state.getUriProjectLoader());
    assertEquals(file.toURI(), state.getUri());
    assertFalse(state.isNewProject());
    assertFalse(state.isBackup());
    assertEquals(file.getCanonicalFile(), state.getMainProjectFile().getCanonicalFile());
    assertTrue(state.hasUriProjectLoaderThatShouldBeSaved());
  }

  @Test
  public void loaderStateSupportsNewProjectAndBackupFlags() {
    ProjectState state = new ProjectState();
    StubUriProjectLoader loader = new StubUriProjectLoader(new File("build/test-state/backup.a3p"), true, true, false);
    state.setUriProjectLoader(loader);

    assertTrue(state.isNewProject());
    assertTrue(state.isBackup());
    assertNull(state.getMainProjectFile());
    assertFalse(state.hasUriProjectLoaderThatShouldBeSaved());
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

  @Test
  public void sanitizeProjectReturnsEmptyMessageWhenNoNullMembersExist() {
    ProjectState state = new ProjectState();
    UserMethod method = new UserMethod("keep", void.class, new UserParameter[0], new BlockStatement());
    UserField field = new UserField("actor", String.class);
    NamedUserType type = new NamedUserType("CleanScene", new UserPackage("test"), Object.class,
        new NamedUserConstructor[0], new UserMethod[]{method}, new UserField[]{field});
    Project project = new Project(type, Set.of(type), Collections.emptySet(), Project.SceneCameraType.WindowCamera);

    String message = state.sanitizeProject(project);

    assertEquals("", message);
    assertEquals(1, type.getDeclaredMethods().size());
    assertEquals(1, type.getDeclaredFields().size());
  }

  @Test
  public void sanitizeProjectAddsNewlineWhenMultipleTypesNeedFixes() {
    ProjectState state = new ProjectState();
    NamedUserType first = new NamedUserType("FirstScene", new UserPackage("test"), Object.class,
        new NamedUserConstructor[0], new UserMethod[]{new UserMethod("keep", void.class, new UserParameter[0], new BlockStatement()), null}, new UserField[0]);
    NamedUserType second = new NamedUserType("SecondScene", new UserPackage("test"), Object.class,
        new NamedUserConstructor[0], new UserMethod[0], new UserField[]{new UserField("actor", String.class), null});
    LinkedHashSet<NamedUserType> types = new LinkedHashSet<>();
    types.add(first);
    types.add(second);
    Project project = new Project(first, types, Collections.emptySet(), Project.SceneCameraType.WindowCamera);

    String message = state.sanitizeProject(project);

    assertTrue(message.contains("null method was removed from FirstScene."));
    assertTrue(message.contains("null field was removed from SecondScene."));
    assertTrue(message.contains("\n"));
  }

  private static final class StubUriProjectLoader extends UriProjectLoader {
    private final URI uri;
    private final boolean newProject;
    private final boolean backup;
    private final boolean shouldBeSaved;

    private StubUriProjectLoader(File file, boolean newProject, boolean backup, boolean shouldBeSaved) {
      super(false);
      this.uri = file.toURI();
      this.newProject = newProject;
      this.backup = backup;
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
    public boolean isBackup() {
      return this.backup;
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
