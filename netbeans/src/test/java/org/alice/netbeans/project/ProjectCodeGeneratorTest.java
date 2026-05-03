package org.alice.netbeans.project;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.lgna.project.Project;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.io.IoUtilities;
import org.lgna.story.SProgram;
import org.openide.filesystems.FileObject;

import java.io.File;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Properties;

import static org.junit.Assert.*;

public class ProjectCodeGeneratorTest {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void generatedLauncherMatchesNetBeansMainClassTemplate() throws Exception {
    Properties projectProperties = new Properties();
    Path projectPropertiesPath = Path.of("src/main/resources/ProjectTemplate/nbproject/project.properties");
    assertTrue(Files.exists(projectPropertiesPath));
    try (Reader reader = Files.newBufferedReader(projectPropertiesPath)) {
      projectProperties.load(reader);
    }

    File sourceDirectory = temporaryFolder.newFolder("src");
    FileObject launcherFileObject = ProjectCodeGenerator.generateLauncher(sourceDirectory);
    assertNotNull(launcherFileObject);

    Path launcherPath = sourceDirectory.toPath().resolve(launcherFileObject.getNameExt());
    String launcherSource = Files.readString(launcherPath);
    String launcherClassName = launcherFileObject.getName();

    assertEquals("AliceJavaFXLauncher.java", launcherFileObject.getNameExt());
    assertEquals("AliceJavaFXLauncher", launcherClassName);
    assertEquals(launcherClassName, projectProperties.getProperty("main.class"));
    assertTrue(launcherSource.contains("public class " + launcherClassName + " extends Application"));
    assertTrue(launcherSource.contains("Program.main(startingArgs)"));
    assertTrue(launcherSource.contains("launch(args)"));
  }

  @Test
  public void generatesProgramAndLauncherFromSyntheticAliceProject() throws Exception {
    File aliceProject = temporaryFolder.newFile("synthetic.a3p");
    IoUtilities.writeProject(
        aliceProject,
        new Project(programType("Program"), Project.SceneCameraType.WindowCamera));
    File sourceDirectory = temporaryFolder.newFolder("generated-src");

    Collection<FileObject> filesToOpen = ProjectCodeGenerator.generateCode(
        aliceProject,
        sourceDirectory,
        null,
        false);

    Path programPath = sourceDirectory.toPath().resolve("Program.java");
    Path launcherPath = sourceDirectory.toPath().resolve("AliceJavaFXLauncher.java");
    assertTrue(Files.exists(programPath));
    assertTrue(Files.exists(launcherPath));
    assertTrue(filesToOpen.stream()
        .anyMatch(fileObject -> "AliceJavaFXLauncher.java".equals(fileObject.getNameExt())));
    String programSource = Files.readString(programPath);
    assertTrue(programSource.contains("class Program extends SProgram"));
  }

  private static NamedUserType programType(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    type.superType.setValue(JavaType.getInstance(SProgram.class));
    return type;
  }
}
