package org.alice.ide.projecturi;

import org.alice.ide.uricontent.FileProjectLoader;
import org.alice.ide.uricontent.UriProjectLoader;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.lgna.project.Project;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.io.IoUtilities;
import org.lgna.story.SProgram;

import java.io.File;

import static org.junit.Assert.*;

public class ProjectUriBehaviorTest {
  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void directoryUriListDataSurfacesSavedProjectEvidenceAsSnapshots() throws Exception {
    File projectDirectory = temporaryFolder.newFolder("projects");
    File savedProject = new File(projectDirectory, "lesson-world.a3p");
    File ignoredTextFile = new File(projectDirectory, "notes.txt");
    IoUtilities.writeProject(savedProject, projectNamed("LessonWorld"));
    assertTrue(ignoredTextFile.createNewFile());

    DirectoryUriListData data = new DirectoryUriListData(projectDirectory);

    assertEquals(1, data.getItemCount());
    ProjectSnapshot snapshot = data.getItemAt(0);
    assertEquals(savedProject.toURI(), snapshot.getUri());
    assertEquals("lesson-world.a3p", snapshot.getText());
    assertTrue(snapshot.hasUri());
    assertNotNull(snapshot.getIcon());
  }

  @Test
  public void fileSystemTabBuildsFileProjectLoaderFromSelectedPath() throws Exception {
    File savedProject = temporaryFolder.newFile("selected-world.a3p");
    IoUtilities.writeProject(savedProject, projectNamed("SelectedWorld"));
    FileSystemTab tab = new FileSystemTab();
    tab.getPathState().setValueTransactionlessly(savedProject.getAbsolutePath());

    UriProjectLoader loader = tab.getSelectedUri();

    assertTrue(loader instanceof FileProjectLoader);
    assertEquals(savedProject.toURI(), loader.getUri());
    assertEquals(savedProject.getCanonicalFile(), loader.getMainProjectFile().getCanonicalFile());
    assertNotNull(tab.getSnapshot());
    assertEquals(savedProject.toURI(), tab.getSnapshot().getUri());
    assertEquals("selected-world.a3p", tab.getSnapshot().getText());
  }

  @Test
  public void fileSystemTabClearsSnapshotWhenPathDoesNotExist() {
    FileSystemTab tab = new FileSystemTab();
    File missingProject = new File(temporaryFolder.getRoot(), "missing-world.a3p");
    tab.getPathState().setValueTransactionlessly(missingProject.getAbsolutePath());

    UriProjectLoader loader = tab.getSelectedUri();

    assertNull(loader);
    assertNull(tab.getSnapshot());
  }

  private static Project projectNamed(String name) {
    return new Project(programType(name), Project.SceneCameraType.WindowCamera);
  }

  private static NamedUserType programType(String name) {
    return AstUtilities.createType(name, JavaType.getInstance(SProgram.class));
  }
}
