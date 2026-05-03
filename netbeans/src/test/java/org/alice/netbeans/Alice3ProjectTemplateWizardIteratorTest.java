package org.alice.netbeans;

import org.junit.Test;

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
