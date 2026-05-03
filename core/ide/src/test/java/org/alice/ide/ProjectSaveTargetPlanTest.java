package org.alice.ide;

import org.alice.ide.uricontent.FileProjectLoader;
import org.alice.ide.uricontent.UriProjectLoader;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.lgna.project.Project;

import java.io.File;
import java.net.URI;

import static org.junit.Assert.*;

public class ProjectSaveTargetPlanTest {
  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void newProjectSaveCopiesDefaultBackupsAndTargetsNormalProjectFile() throws Exception {
    File target = new File(temporaryFolder.getRoot(), "world.a3p");

    ProjectSaveTargetPlan plan = ProjectSaveTargetPlan.choose(new NewProjectLoader(), target);

    assertTrue(plan.shouldCopyDefaultBackupDirectory());
    assertFalse(plan.isBackupSave());
    assertEquals(target.toURI(), plan.getNextLoader().getUri());
  }

  @Test
  public void defaultBackupSaveToNormalProjectCopiesDefaultBackups() throws Exception {
    File defaultBackup = new File(temporaryFolder.newFolder(".defaultbak"), "auto20240102_120000.a3p");
    File target = new File(temporaryFolder.getRoot(), "world.a3p");

    ProjectSaveTargetPlan plan = ProjectSaveTargetPlan.choose(new FileProjectLoader(defaultBackup), target);

    assertTrue(plan.shouldCopyDefaultBackupDirectory());
    assertFalse(plan.isBackupSave());
  }

  @Test
  public void defaultBackupSaveToDefaultBackupDoesNotCopyDefaultBackups() throws Exception {
    File defaultBackupDirectory = temporaryFolder.newFolder(".defaultbak");
    File current = new File(defaultBackupDirectory, "auto20240102_120000.a3p");
    File target = new File(defaultBackupDirectory, "auto20240102_130000.a3p");

    ProjectSaveTargetPlan plan = ProjectSaveTargetPlan.choose(new FileProjectLoader(current), target);

    assertFalse(plan.shouldCopyDefaultBackupDirectory());
    assertTrue(plan.isBackupSave());
  }

  @Test
  public void savedProjectSaveToBackupTargetsBackupSave() throws Exception {
    File current = new File(temporaryFolder.getRoot(), "world.a3p");
    File backup = new File(temporaryFolder.newFolder("world.bak"), "auto20240102_120000.a3p");

    ProjectSaveTargetPlan plan = ProjectSaveTargetPlan.choose(new FileProjectLoader(current), backup);

    assertFalse(plan.shouldCopyDefaultBackupDirectory());
    assertTrue(plan.isBackupSave());
    assertEquals(backup.toURI(), plan.getNextLoader().getUri());
  }

  private static final class NewProjectLoader extends UriProjectLoader {
    private NewProjectLoader() {
      super(false);
    }

    @Override
    public URI getUri() {
      return URI.create("blank://project");
    }

    @Override
    public boolean isNewProject() {
      return true;
    }

    @Override
    protected Project load() {
      return null;
    }
  }
}
