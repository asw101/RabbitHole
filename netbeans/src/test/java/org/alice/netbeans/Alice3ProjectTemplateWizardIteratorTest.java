package org.alice.netbeans;

import edu.cmu.cs.dennisc.java.io.FileUtilities;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.openide.WizardDescriptor;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Set;
import java.util.zip.ZipFile;

import static org.junit.Assert.*;

public class Alice3ProjectTemplateWizardIteratorTest {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void projectTemplateArchiveContainsExpectedNetBeansFiles() throws Exception {
    Path archive = Path.of("target/classes/org/alice/netbeans/ProjectTemplate.zip");

    try (ZipFile zipFile = new ZipFile(archive.toFile())) {
      Set<String> expectedEntries = Set.of(
          "build.xml",
          "manifest.mf",
          "nbproject/build-impl.xml",
          "nbproject/genfiles.properties",
          "nbproject/project.properties",
          "nbproject/project.xml");

      for (String entry : expectedEntries) {
        assertNotNull(entry, zipFile.getEntry(entry));
      }
      assertNull(zipFile.getEntry("ProjectTemplate/nbproject/project.properties"));
    }
  }

  @Test
  public void projectTemplateBuildPropertiesDeclareAliceLibraryContract() throws Exception {
    Path archive = Path.of("target/classes/org/alice/netbeans/ProjectTemplate.zip");

    try (ZipFile zipFile = new ZipFile(archive.toFile())) {
      String properties = new String(
          zipFile.getInputStream(zipFile.getEntry("nbproject/project.properties")).readAllBytes(),
          StandardCharsets.UTF_8);

      assertTrue(properties.contains("javac.release = 21"));
      assertTrue(properties.contains("javac.source = 21"));
      assertTrue(properties.contains("javac.target = 21"));
      assertTrue(properties.contains("javac.classpath = \\\n    ${libs.Alice3Library.classpath}"));
      assertTrue(properties.contains("main.class = AliceJavaFXLauncher"));
      assertTrue(properties.contains("-Dorg.alice.ide.rootDirectory=\"${libs.Alice3Library.src}_root\""));
      assertTrue(properties.contains("--add-opens=javafx.graphics/com.sun.javafx.application=ALL-UNNAMED"));
    }
  }

  @Test
  public void projectPropertiesAreRenamedForGeneratedProject() {
    String properties = """
        application.title = Alice3JavaApplication
        dist.jar = ${dist.dir}/Alice3JavaApplication.jar
        main.class = AliceJavaFXLauncher
        """;

    String renamed = Alice3ProjectTemplateWizardIterator.renameProjectProperties(properties, "MyImportedWorld");

    assertTrue(renamed.contains("application.title = MyImportedWorld"));
    assertTrue(renamed.contains("dist.jar = ${dist.dir}/MyImportedWorld.jar"));
    assertTrue(renamed.contains("main.class = AliceJavaFXLauncher"));
    assertFalse(renamed.contains("Alice3JavaApplication"));
  }

  @Test
  public void availableProjectNameSearchesPastFormerDoubleDigitLimit() throws Exception {
    File projectLocation = temporaryFolder.newFolder("projects");
    for (int i = 1; i <= 100; i++) {
      File projectDirectory = new File(projectLocation, i == 1 ? "World" : "World" + i);
      assertTrue(projectDirectory.mkdir());
    }
    Alice3ProjectTemplatePanelVisual visual = new Alice3ProjectTemplatePanelVisual(new Alice3ProjectTemplateWizardPanel());
    readProjectLocation(visual, projectLocation);

    assertEquals("World101", visual.getAvailableProjectName("World"));
  }

  @Test
  public void addNotifyDoesNotPopulateFromDeveloperHomeProject() throws Exception {
    File defaultDirectory = temporaryFolder.newFolder("default-directory");
    File aliceProject = new File(defaultDirectory, "Alice3/MyProjects/a.a3p");
    assertTrue(aliceProject.getParentFile().mkdirs());
    assertTrue(aliceProject.createNewFile());

    String previousUserHome = System.getProperty("user.home");
    File previousDefaultDirectory = setDefaultDirectory(defaultDirectory);
    try {
      System.setProperty("user.home", "C:\\Users\\dennisc");
      Alice3ProjectTemplatePanelVisual visual = new Alice3ProjectTemplatePanelVisual(new Alice3ProjectTemplateWizardPanel());

      visual.addNotify();
      SwingUtilities.invokeAndWait(() -> {
      });

      assertEquals("", textField(visual, "aliceWorldLocationTextField").getText());
      assertEquals("", textField(visual, "projectNameTextField").getText());
    } finally {
      if (previousUserHome == null) {
        System.clearProperty("user.home");
      } else {
        System.setProperty("user.home", previousUserHome);
      }
      setDefaultDirectory(previousDefaultDirectory);
    }
  }

  @Test
  public void ensureDirectoryExistsRejectsExistingFile() throws Exception {
    File file = temporaryFolder.newFile("not-a-directory");

    try {
      Alice3ProjectTemplateWizardIterator.ensureDirectoryExists(file, "project directory");
      fail("Expected IOException");
    } catch (IOException expected) {
      assertTrue(expected.getMessage().contains("not a directory"));
    }
  }

  private static File setDefaultDirectory(File directory) throws Exception {
    Field field = FileUtilities.class.getDeclaredField("s_defaultDirectory");
    field.setAccessible(true);
    File previous = (File) field.get(null);
    field.set(null, directory);
    return previous;
  }

  private static JTextField textField(Alice3ProjectTemplatePanelVisual visual, String name) throws Exception {
    Field field = Alice3ProjectTemplatePanelVisual.class.getDeclaredField(name);
    field.setAccessible(true);
    return (JTextField) field.get(visual);
  }

  private static void readProjectLocation(Alice3ProjectTemplatePanelVisual visual, File projectLocation) {
    WizardDescriptor settings = new WizardDescriptor(new WizardDescriptor.Panel[0]);
    settings.putProperty("projdir", new File(projectLocation, "World"));
    settings.putProperty("name", "World");
    visual.read(settings);
  }
}
