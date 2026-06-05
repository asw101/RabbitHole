package org.lgna.project.io.compat;

import org.alice.tweedle.file.Manifest;
import org.alice.tweedle.file.ManifestEncoderDecoder;
import org.alice.tweedle.file.ProjectManifest;
import org.lgna.common.Resource;
import org.lgna.project.Project;
import org.lgna.project.VersionNotSupportedException;
import org.lgna.project.io.IoUtilities;
import org.lgna.project.io.ProjectIo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

final class RabbitHoleReplayRunner implements ReplaySummaryProvider {
  private final Path tempRoot;
  private final ReplaySummaryWriter writer = new ReplaySummaryWriter();

  RabbitHoleReplayRunner(Path tempRoot) throws IOException {
    this.tempRoot = Objects.requireNonNull(tempRoot, "tempRoot").toAbsolutePath().normalize();
    Files.createDirectories(this.tempRoot);
  }

  @Override
  public ReplaySummary summarize(ReplayCase replayCase) throws IOException {
    Objects.requireNonNull(replayCase, "replayCase");
    Path caseDirectory = tempRoot.resolve(replayCase.id());
    Files.createDirectories(caseDirectory);
    Path projectArchive = caseDirectory.resolve(replayCase.id() + ".a3p");
    Path playerArchive = caseDirectory.resolve(replayCase.id() + ".a3w");

    IoUtilities.writeProject(projectArchive.toFile(), replayCase.project());
    Project reopenedProject = readProject(projectArchive);
    IoUtilities.exportProject(playerArchive.toFile(), reopenedProject);

    ReplaySummaryWriter.Input.Builder input = ReplaySummaryWriter.Input.builder("RabbitHole")
        .addArchiveEntries("a3p", archiveEntries(projectArchive))
        .addArchiveEntries("a3w", archiveEntries(playerArchive))
        .addManifestFields("a3p", manifestFields(projectArchive, "a3p"))
        .addManifestFields("a3w", manifestFields(playerArchive, "a3w"));
    addResourceIdentities(input, reopenedProject, projectArchive);
    return writer.write(replayCase, input.build());
  }

  private static Project readProject(Path projectArchive) throws IOException {
    try {
      return IoUtilities.readProject(projectArchive.toFile());
    } catch (VersionNotSupportedException e) {
      throw new IOException("Unable to reopen generated RabbitHole project archive " + projectArchive.getFileName(), e);
    }
  }

  private static List<String> archiveEntries(Path archive) throws IOException {
    try (ZipFile zipFile = new ZipFile(archive.toFile())) {
      List<String> entries = new ArrayList<>();
      zipFile.stream()
          .map(ZipEntry::getName)
          .sorted()
          .forEach(entries::add);
      return List.copyOf(entries);
    }
  }

  private static Map<String, String> manifestFields(Path archive, String archiveType) throws IOException {
    try (ZipFile zipFile = new ZipFile(archive.toFile())) {
      ZipEntry manifestEntry = zipFile.getEntry(ProjectIo.MANIFEST_ENTRY_NAME);
      if (manifestEntry == null) {
        throw new IOException("Missing " + ProjectIo.MANIFEST_ENTRY_NAME + " in " + archive.getFileName());
      }
      String manifestText;
      try (InputStream inputStream = zipFile.getInputStream(manifestEntry)) {
        manifestText = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
      }
      ProjectManifest manifest = ManifestEncoderDecoder.fromJson(manifestText, ProjectManifest.class);
      Map<String, String> fields = new LinkedHashMap<>();
      fields.put("archiveType", archiveType);
      if ((manifest.description != null) && (manifest.description.name != null)) {
        fields.put("projectName", manifest.description.name);
      }
      if ((manifest.metadata != null) && (manifest.metadata.fileType != null)) {
        fields.put("manifestFileType", manifest.metadata.fileType);
      }
      if ((manifest.metadata != null) && (manifest.metadata.identifier != null) && (manifest.metadata.identifier.type != null)) {
        fields.put("projectType", manifest.metadata.identifier.type.name());
      }
      if ((manifest.projectStructure != null) && (manifest.projectStructure.sceneCameraType != null)) {
        fields.put("sceneCameraType", manifest.projectStructure.sceneCameraType.name());
      }
      fields.put("resourceReferenceCount", Integer.toString(manifest.resources == null ? 0 : manifest.resources.size()));
      fields.put("prerequisiteCount", Integer.toString(manifest.prerequisites == null ? 0 : manifest.prerequisites.size()));
      return fields;
    }
  }

  private static void addResourceIdentities(
      ReplaySummaryWriter.Input.Builder input,
      Project project,
      Path projectArchive) throws IOException {
    List<Resource> resources = new ArrayList<>(project.getResources());
    resources.sort(Comparator
        .comparing((Resource resource) -> safe(resource.getOriginalFileName()))
        .thenComparing(resource -> safe(resource.getName()))
        .thenComparing(resource -> safe(resource.getContentType()))
        .thenComparing(resource -> ReplaySummaryWriter.sha256(resource.getData())));
    List<String> resourceEntries = resourceEntries(projectArchive);
    if (resources.size() != resourceEntries.size()) {
      throw new IOException(
          "Resource count mismatch for "
              + projectArchive.getFileName()
              + ": project has "
              + resources.size()
              + " resources but archive has "
              + resourceEntries.size()
              + " resource entries");
    }
    for (int i = 0; i < resources.size(); i++) {
      Resource resource = resources.get(i);
      input.addResource(new ReplaySummaryWriter.ResourceIdentity(
          safe(resource.getName()),
          safe(resource.getContentType()),
          resourceEntries.get(i),
          resource.getData()));
    }
  }

  private static List<String> resourceEntries(Path archive) throws IOException {
    List<String> entries = archiveEntries(archive);
    TreeMap<String, String> resourceEntries = new TreeMap<>();
    for (String entry : entries) {
      if (isResourceEntry(entry)) {
        resourceEntries.put(entry, entry);
      }
    }
    return List.copyOf(resourceEntries.values());
  }

  private static boolean isResourceEntry(String entry) {
    int slash = entry.indexOf('/');
    if (slash <= 0) {
      return false;
    }
    String directory = entry.substring(0, slash);
    if ("resources".equals(directory)) {
      return true;
    }
    if (!directory.startsWith("resources")) {
      return false;
    }
    for (int i = "resources".length(); i < directory.length(); i++) {
      if (!Character.isDigit(directory.charAt(i))) {
        return false;
      }
    }
    return directory.length() > "resources".length();
  }

  private static String safe(String value) {
    return value == null ? "" : value;
  }
}
