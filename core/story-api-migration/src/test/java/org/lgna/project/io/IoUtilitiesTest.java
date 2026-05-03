package org.lgna.project.io;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.lgna.project.Project;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.story.SProgram;

import java.io.File;
import java.util.zip.ZipFile;

import static org.junit.Assert.*;

public class IoUtilitiesTest {
  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void writesReadableSyntheticProjectWithoutBinaryFixture() throws Exception {
    Project project = new Project(programType("Program"), Project.SceneCameraType.WindowCamera);
    File projectFile = temporaryFolder.newFile("synthetic.a3p");

    IoUtilities.writeProject(projectFile, project);

    Project readProject = IoUtilities.readProject(projectFile);
    assertEquals("Program", readProject.getProgramType().getName());
    assertEquals(Project.SceneCameraType.WindowCamera, readProject.createSaveManifest().projectStructure.sceneCameraType);
    assertTrue(readProject.getResources().isEmpty());
  }

  @Test
  public void writtenProjectContainsVersionAndProgramTypeEntries() throws Exception {
    Project project = new Project(programType("Program"), Project.SceneCameraType.WindowCamera);
    File projectFile = temporaryFolder.newFile("synthetic.a3p");

    IoUtilities.writeProject(projectFile, project);

    try (ZipFile zipFile = new ZipFile(projectFile)) {
      assertNotNull(zipFile.getEntry(ProjectIo.VERSION_ENTRY_NAME));
      assertNotNull(zipFile.getEntry("programType.xml"));
      assertNull(zipFile.getEntry("resources.xml"));
    }
  }

  private static NamedUserType programType(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    type.superType.setValue(JavaType.getInstance(SProgram.class));
    return type;
  }
}
