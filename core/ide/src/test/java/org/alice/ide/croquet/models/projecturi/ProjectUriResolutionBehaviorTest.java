package org.alice.ide.croquet.models.projecturi;

import org.alice.ide.projecturi.ProjectSnapshot;
import org.alice.ide.testing.TestIdeBootstrap;
import org.alice.ide.uricontent.BlankSlateProjectLoader;
import org.alice.ide.uricontent.FileProjectLoader;
import org.alice.ide.uricontent.StarterProjectFileLoader;
import org.alice.ide.uricontent.StarterProjectUtilities;
import org.alice.ide.uricontent.UriProjectLoader;
import org.alice.stageide.openprojectpane.models.TemplateUriState;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.lgna.project.io.IoUtilities;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ProjectUriResolutionBehaviorTest {
  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void fileSnapshotResolvesToFileLoaderUsingCanonicalMainProjectFile() throws Exception {
    File projectFile = temporaryFolder.newFile("lesson-world.a3p");
    IoUtilities.writeProject(projectFile, TestIdeBootstrap.createMinimalProject());
    ProjectSnapshot snapshot = new ProjectSnapshot(projectFile.toURI());

    UriProjectLoader loader = UriProjectLoader.createInstance(snapshot, false);

    assertTrue(loader instanceof FileProjectLoader);
    assertEquals(projectFile.getCanonicalFile(), loader.getMainProjectFile().getCanonicalFile());
    assertFalse(loader.isBackup());
    assertFalse(loader.shouldBeSaved());
  }

  @Test
  public void backupLoaderResolvesOriginalProjectBesideBackupDirectory() throws Exception {
    File projectDirectory = temporaryFolder.newFolder("projects");
    File mainProject = new File(projectDirectory, "lesson-world.a3p");
    IoUtilities.writeProject(mainProject, TestIdeBootstrap.createMinimalProject());
    File backupDirectory = new File(projectDirectory, "lesson-world.bak");
    assertTrue(backupDirectory.mkdir());
    File backupProject = new File(backupDirectory, "auto-20250101_120000.a3p");
    IoUtilities.writeProject(backupProject, TestIdeBootstrap.createMinimalProject());

    FileProjectLoader loader = new FileProjectLoader(backupProject);

    assertTrue(loader.isBackup());
    assertEquals(mainProject.getCanonicalFile(), loader.getMainProjectFile().getCanonicalFile());
  }

  @Test
  public void templateSnapshotResolvesToBlankSlateLoaderFromUriFragment() {
    ProjectSnapshot snapshot = TemplateUriState.Template.GRASS.getProjectSnapshot();

    UriProjectLoader loader = UriProjectLoader.createInstance(snapshot, false);

    assertTrue(loader instanceof BlankSlateProjectLoader);
    assertTrue(loader.isNewProject());
    assertNull(loader.getMainProjectFile());
    assertEquals(TemplateUriState.Template.GRASS, snapshot.getUriFragment());
  }

  @Test
  public void starterSnapshotResolvesToStarterProjectLoader() throws Exception {
    File starterFile = temporaryFolder.newFile("starter-world.a3p");
    IoUtilities.writeProject(starterFile, TestIdeBootstrap.createMinimalProject());
    ProjectSnapshot snapshot = new ProjectSnapshot(StarterProjectUtilities.toUri(starterFile));

    UriProjectLoader loader = UriProjectLoader.createInstance(snapshot, false);

    assertTrue(loader instanceof StarterProjectFileLoader);
    assertTrue(loader.isNewProject());
    assertEquals(snapshot.getUri(), loader.getUri());
    assertNull(loader.getMainProjectFile());
  }
}
