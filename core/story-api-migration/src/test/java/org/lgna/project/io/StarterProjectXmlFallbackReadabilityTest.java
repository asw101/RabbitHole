package org.lgna.project.io;

import org.junit.Test;
import org.lgna.common.Resource;
import org.lgna.project.Project;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class StarterProjectXmlFallbackReadabilityTest {
  private static final List<String> REPRESENTATIVE_LEGACY_PROJECT_ARCHIVES = Arrays.asList(
      "magic2.a3p",
      "lagoonMinimum.a3p",
      "wonderland.a3p");

  @Test
  public void representativeStarterProjectFixturesUseXmlFallbackArchives() throws Exception {
    for (String archiveName : REPRESENTATIVE_LEGACY_PROJECT_ARCHIVES) {
      Path archive = starterProjectsDirectory().resolve(archiveName);

      try (ZipFile zipFile = new ZipFile(archive.toFile())) {
        ZipEntry versionEntry = zipFile.getEntry(ProjectIo.VERSION_ENTRY_NAME);
        assertNotNull(archiveName + " should declare a project version for XML fallback selection", versionEntry);
        assertFalse(archiveName + " should declare a non-empty project version",
            readTrimmedEntry(zipFile, versionEntry).isEmpty());
        assertNull(archiveName + " should exercise XML fallback rather than manifest loading",
            zipFile.getEntry(ProjectIo.MANIFEST_ENTRY_NAME));
      }
    }
  }

  @Test
  public void representativeStarterProjectFixturesReadThroughXmlFallback() throws Exception {
    for (String archiveName : REPRESENTATIVE_LEGACY_PROJECT_ARCHIVES) {
      Path archive = starterProjectsDirectory().resolve(archiveName);

      assertNull(archiveName + " historical version should not be treated as future",
          IoUtilities.projectReader(archive.toFile()).checkForFutureVersion());

      Project project = IoUtilities.readProject(archive.toFile());

      assertNotNull(archiveName + " should remain readable through project I/O XML fallback", project);
      assertNotNull(archiveName + " should decode a program type", project.getProgramType());
      assertFalse(archiveName + " should decode a named program type", project.getProgramType().getName().isEmpty());
    }
  }

  @Test
  public void representativeStarterProjectFixtureRestoresCommittedResources() throws Exception {
    boolean foundResourceBearingFixture = false;

    for (String archiveName : REPRESENTATIVE_LEGACY_PROJECT_ARCHIVES) {
      Project project = IoUtilities.readProject(starterProjectsDirectory().resolve(archiveName).toFile());
      Collection<Resource> resources = project.getResources();

      if (!resources.isEmpty()) {
        foundResourceBearingFixture = true;
      }
      for (Resource resource : resources) {
        assertNotNull(archiveName + " should restore resource names", resource.getName());
        assertFalse(archiveName + " should restore non-empty resource names", resource.getName().isEmpty());
        assertNotNull(archiveName + " should restore resource content types", resource.getContentType());
        assertFalse(archiveName + " should restore non-empty content types", resource.getContentType().isEmpty());
        assertNotNull(archiveName + " should restore resource data", resource.getData());
        assertTrue(archiveName + " should restore non-empty resource data", resource.getData().length > 0);
      }
    }

    assertTrue("Representative fixtures should include at least one resource-bearing XML fallback archive",
        foundResourceBearingFixture);
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
