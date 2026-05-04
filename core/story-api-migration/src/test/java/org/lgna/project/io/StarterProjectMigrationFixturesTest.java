package org.lgna.project.io;

import org.junit.Test;
import org.lgna.common.Resource;
import org.lgna.common.resources.ImageResource;
import org.lgna.project.Project;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class StarterProjectMigrationFixturesTest {
  private static final String PROGRAM_TYPE_ENTRY_NAME = "programType.xml";
  private static final String RESOURCES_ENTRY_NAME = "resources.xml";
  private static final String HISTORICAL_FIXTURE_VERSION = "3.3.0.0.0";

  private static final List<String> REPRESENTATIVE_LEGACY_PROJECT_ARCHIVES = Arrays.asList(
      "magic2.a3p",
      "lagoonMinimum.a3p",
      "wonderland.a3p");

  @Test
  public void representativeStarterProjectFixturesRemainLegacyXmlArchives() throws Exception {
    for (String archiveName : REPRESENTATIVE_LEGACY_PROJECT_ARCHIVES) {
      Path archive = starterProjectsDirectory().resolve(archiveName);

      try (ZipFile zipFile = new ZipFile(archive.toFile())) {
        ZipEntry versionEntry = zipFile.getEntry(ProjectIo.VERSION_ENTRY_NAME);
        assertNotNull(archiveName + " should keep version.txt for migration compatibility", versionEntry);
        assertEquals(HISTORICAL_FIXTURE_VERSION, readTrimmedEntry(zipFile, versionEntry));
        assertNotNull(archiveName + " should keep legacy programType.xml", zipFile.getEntry(PROGRAM_TYPE_ENTRY_NAME));
        assertNull(archiveName + " should remain a legacy XML fixture without manifest.json", zipFile.getEntry(ProjectIo.MANIFEST_ENTRY_NAME));
        assertNotNull(archiveName + " should keep a thumbnail entry", zipFile.getEntry("thumbnail.png"));

        boolean hasResourcesXml = zipFile.getEntry(RESOURCES_ENTRY_NAME) != null;
        assertEquals(
            archiveName + " resources.xml presence is part of the historical fixture shape",
            "lagoonMinimum.a3p".equals(archiveName),
            hasResourcesXml);
      }
    }
  }

  @Test
  public void representativeStarterProjectFixturesReadThroughNoManifestXmlFallback() throws Exception {
    for (String archiveName : REPRESENTATIVE_LEGACY_PROJECT_ARCHIVES) {
      Path archive = starterProjectsDirectory().resolve(archiveName);

      assertNull(archiveName + " historical version should not be treated as future",
          IoUtilities.projectReader(archive.toFile()).checkForFutureVersion());

      Project project = IoUtilities.readProject(archive.toFile());

      assertNotNull(archiveName + " should remain readable through migration project I/O", project);
      assertNotNull(archiveName + " should decode a program type", project.getProgramType());
      assertFalse(archiveName + " should decode a named program type", project.getProgramType().getName().isEmpty());
    }
  }

  @Test
  public void resourceBearingStarterProjectFixtureRestoresCommittedResource() throws Exception {
    Project project = IoUtilities.readProject(starterProjectsDirectory().resolve("lagoonMinimum.a3p").toFile());

    assertEquals(1, project.getResources().size());
    Resource resource = project.getResources().iterator().next();
    assertTrue(resource instanceof ImageResource);
    assertEquals("sandDunesLight_diffuse.png", resource.getName());
    assertEquals("sandDunesLight_diffuse.png", resource.getOriginalFileName());
    assertEquals("image/png", resource.getContentType());
    assertNotEquals(0, resource.getData().length);
  }

  private static Path starterProjectsDirectory() {
    Path current = Paths.get("").toAbsolutePath();
    while (current != null) {
      Path candidate = current.resolve("core/resources/src/application/resources/starter-projects");
      if (Files.isDirectory(candidate)) {
        return candidate;
      }
      current = current.getParent();
    }
    fail("Unable to find core/resources/src/application/resources/starter-projects from " + Paths.get("").toAbsolutePath());
    return null;
  }

  private static String readTrimmedEntry(ZipFile zipFile, ZipEntry zipEntry) throws IOException {
    return new String(zipFile.getInputStream(zipEntry).readAllBytes(), StandardCharsets.UTF_8).trim();
  }
}
