package org.alice.netbeans;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Set;
import java.util.zip.ZipFile;

import static org.junit.Assert.*;

public class Alice3ProjectTemplateWizardIteratorTest {

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
}
