package org.alice.netbeans;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertTrue;

public class Alice3LibraryRegistrationTest {

  @Test
  public void layerRegistersAlice3LibraryDefinition() throws Exception {
    String layer = Files.readString(
        Path.of("target/classes/org/alice/netbeans/layer.xml"),
        StandardCharsets.UTF_8);

    assertTrue(layer.contains("<folder name=\"org-netbeans-api-project-libraries\">"));
    assertTrue(layer.contains("<folder name=\"Libraries\">"));
    assertTrue(layer.contains("<file name=\"Alice3Library.xml\" url=\"Alice3Library.xml\"/>"));
  }

  @Test
  public void alice3LibraryDeclaresExportedProjectVolumes() throws Exception {
    String library = Files.readString(
        Path.of("target/classes/org/alice/netbeans/Alice3Library.xml"),
        StandardCharsets.UTF_8);

    assertTrue(library.contains("<name>Alice3Library</name>"));
    assertTrue(library.contains("<type>j2se</type>"));
    assertTrue(library.contains("<type>classpath</type>"));
    assertTrue(library.contains("nbinst:/modules/ext/org.alice.netbeans/org-alice/util.jar"));
    assertTrue(library.contains("nbinst:/modules/ext/org.alice.netbeans/org-alice/story-api.jar/"));
    assertTrue(library.contains("nbinst:/modules/ext/org.alice.netbeans/org-openjfx/javafx-graphics.jar"));
    assertTrue(library.contains("<type>src</type>"));
    assertTrue(library.contains("nbinst:/src/aliceSource.jar"));
    assertTrue(library.contains("<type>javadoc</type>"));
    assertTrue(library.contains("nbinst:/doc/aliceDocs.zip"));
  }

  @Test
  public void pomPackagesAliceLibrarySourceAndJavadocVolumes() throws Exception {
    String pom = Files.readString(Path.of("pom.xml"), StandardCharsets.UTF_8);

    assertTrue(pom.contains("<id>javadoc</id>"));
    assertTrue(pom.contains("<descriptor>src/main/resources/assemblies/rename-javadoc.xml</descriptor>"));
    assertTrue(pom.contains("<id>story-src</id>"));
    assertTrue(pom.contains("<descriptor>src/main/resources/assemblies/story-src.xml</descriptor>"));
    assertTrue(pom.contains("<finalName>nbm/clusters/extra/src/aliceSource</finalName>"));
    assertTrue(pom.contains("<id>final-name</id>"));
    assertTrue(pom.contains("<descriptor>src/main/resources/assemblies/rename-nbm.xml</descriptor>"));
  }
}
