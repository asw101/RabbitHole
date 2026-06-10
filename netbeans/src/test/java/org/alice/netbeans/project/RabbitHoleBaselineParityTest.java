package org.alice.netbeans.project;

import org.alice.tweedle.file.ManifestEncoderDecoder;
import org.alice.tweedle.file.ProjectManifest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.lgna.common.resources.ImageResource;
import org.lgna.project.Project;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.LocalDeclarationStatement;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.ResourceExpression;
import org.lgna.project.ast.UserLocal;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;
import org.lgna.project.io.IoUtilities;
import org.lgna.project.io.ProjectIo;
import org.lgna.story.SProgram;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class RabbitHoleBaselineParityTest {
  private static final String UPDATE_PROPERTY = "rabbithole.baseline.updateSnapshots";
  private static final String SNAPSHOT_DIR_PROPERTY = "rabbithole.baseline.snapshotDir";
  private static final String SNAPSHOT_RESOURCE_ROOT = "/org/alice/netbeans/project/parity/";
  private static final Path SNAPSHOT_PACKAGE_PATH = Path.of("org", "alice", "netbeans", "project", "parity");
  private static final UUID PROGRAM_TYPE_ID = stableUuid("Program");
  private static final UUID MAIN_METHOD_ID = stableUuid("main");
  private static final UUID MAIN_ARGS_ID = stableUuid("main-args");
  private static final UUID REMEMBER_METHOD_ID = stableUuid("remember-image");
  private static final UUID IMAGE_LOCAL_ID = stableUuid("image-local");
  private static final UUID IMAGE_RESOURCE_ID = stableUuid("image-resource");
  private static final byte[] IMAGE_BYTES = "rabbithole baseline image fixture\n".getBytes(StandardCharsets.UTF_8);

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void generatedProjectFilesMatchBaselineSnapshot() throws Exception {
    File aliceProject = temporaryFolder.newFile("simple-project.a3p");
    IoUtilities.writeProject(aliceProject, simpleProject());
    File sourceDirectory = temporaryFolder.newFolder("generated-src");

    ProjectCodeGenerator.generateCode(aliceProject, sourceDirectory, null, false);

    assertMatchesSnapshot(
        "simple-project-java.snapshot",
        summarizeGeneratedSource(sourceDirectory.toPath()));
  }

  @Test
  public void editableProjectArchiveMatchesBaselineSnapshot() throws Exception {
    File aliceProject = temporaryFolder.newFile("simple-project.a3p");
    IoUtilities.writeProject(aliceProject, simpleProject());

    assertMatchesSnapshot(
        "simple-project-a3p-archive.snapshot",
        summarizeArchive(aliceProject, IoUtilities.PROJECT_EXTENSION));
  }

  @Test
  public void playerExportArchiveMatchesBaselineSnapshot() throws Exception {
    File exportFile = temporaryFolder.newFile("simple-project.a3w");
    IoUtilities.exportProject(exportFile, simpleProject());

    assertMatchesSnapshot(
        "simple-project-a3w-export.snapshot",
        summarizeArchive(exportFile, IoUtilities.EXPORT_EXTENSION));
  }

  private static Project simpleProject() {
    ImageResource image = new ImageResource(IMAGE_RESOURCE_ID);
    image.setOriginalFileName("baseline-texture.png");
    image.setName("baseline-texture.png");
    image.setContent("image.png", IMAGE_BYTES);
    image.setWidth(1);
    image.setHeight(1);
    NamedUserType programType = programType("Program", image);
    Project project = new Project(programType, Project.SceneCameraType.WindowCamera);
    project.addResource(image);
    return project;
  }

  private static NamedUserType programType(String name, ImageResource image) {
    NamedUserType type = new NamedUserType();
    type.setId(PROGRAM_TYPE_ID);
    type.name.setValue(name);
    type.superType.setValue(JavaType.getInstance(SProgram.class));
    type.methods.add(mainMethod());
    type.methods.add(rememberImageMethod(image));
    return type;
  }

  private static UserMethod mainMethod() {
    UserParameter argsParameter = new UserParameter("args", String[].class);
    argsParameter.setId(MAIN_ARGS_ID);
    UserMethod mainMethod = new UserMethod(
        "main",
        Void.TYPE,
        new UserParameter[] {argsParameter},
        new BlockStatement());
    mainMethod.setId(MAIN_METHOD_ID);
    mainMethod.isStatic.setValue(true);
    mainMethod.isSignatureLocked.setValue(true);
    return mainMethod;
  }

  private static UserMethod rememberImageMethod(ImageResource image) {
    UserLocal noteLocal = new UserLocal("baselineImage", ImageResource.class, true);
    noteLocal.setId(IMAGE_LOCAL_ID);
    UserMethod method = new UserMethod(
        "rememberBaselineImage",
        Void.TYPE,
        new UserParameter[0],
        new BlockStatement(new LocalDeclarationStatement(
            noteLocal,
            new ResourceExpression(ImageResource.class, image))));
    method.setId(REMEMBER_METHOD_ID);
    return method;
  }

  private static String summarizeGeneratedSource(Path sourceDirectory) throws Exception {
    StringBuilder summary = new StringBuilder();
    summary.append("schema: rabbithole.generated-project-files/v1\n");
    summary.append("fixture: simple-project\n\n");
    List<Path> files;
    try (Stream<Path> stream = Files.walk(sourceDirectory)) {
      files = stream
          .filter(Files::isRegularFile)
          .sorted(Comparator.comparing(path -> relativePath(sourceDirectory, path)))
          .toList();
    }
    for (int i = 0; i < files.size(); i++) {
      Path file = files.get(i);
      String relativePath = relativePath(sourceDirectory, file);
      byte[] bytes = Files.readAllBytes(file);
      summary.append("file: ").append(relativePath).append('\n');
      summary.append("bytes: ").append(bytes.length).append('\n');
      summary.append("sha256: ").append(sha256(bytesForHash(relativePath, bytes))).append('\n');
      if (i < (files.size() - 1)) {
        summary.append('\n');
      }
    }
    return summary.toString();
  }

  private static byte[] bytesForHash(String relativePath, byte[] bytes) {
    if (relativePath.endsWith(".java")) {
      return normalizeLineEndings(new String(bytes, StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8);
    }
    return bytes;
  }

  private static String summarizeArchive(File archive, String expectedFileType) throws Exception {
    StringBuilder summary = new StringBuilder();
    summary.append("schema: rabbithole.archive/v1\n");
    summary.append("fixture: simple-project\n");
    summary.append("archiveType: ").append(expectedFileType).append("\n\n");
    try (ZipFile zipFile = new ZipFile(archive)) {
      summary.append("entries:\n");
      for (String name : sortedEntryNames(zipFile)) {
        summary.append("  ").append(name).append('\n');
      }
      summary.append('\n');
      appendManifestSummary(summary, readProjectManifest(zipFile), expectedFileType);
    }
    return summary.toString();
  }

  private static List<String> sortedEntryNames(ZipFile zipFile) {
    List<String> names = new ArrayList<>();
    Enumeration<? extends ZipEntry> entries = zipFile.entries();
    while (entries.hasMoreElements()) {
      ZipEntry entry = entries.nextElement();
      String name = entry.getName().replace('\\', '/');
      if (isUnsafeEntryName(name)) {
        throw new AssertionError("Unsafe archive entry: " + entry.getName());
      }
      names.add(name);
    }
    names.sort(String::compareTo);
    return names;
  }

  private static boolean isUnsafeEntryName(String name) {
    return name.isEmpty()
        || name.startsWith("/")
        || name.equals("..")
        || name.startsWith("../")
        || name.endsWith("/..")
        || name.contains("/../")
        || name.matches("^[A-Za-z]:/.*");
  }

  private static ProjectManifest readProjectManifest(ZipFile zipFile) throws Exception {
    ZipEntry manifestEntry = zipFile.getEntry(ProjectIo.MANIFEST_ENTRY_NAME);
    assertNotNull("Missing archive entry " + ProjectIo.MANIFEST_ENTRY_NAME, manifestEntry);
    try (InputStream inputStream = zipFile.getInputStream(manifestEntry)) {
      return ManifestEncoderDecoder.fromJsonOrThrow(
          normalizeLineEndings(new String(inputStream.readAllBytes(), StandardCharsets.UTF_8)),
          ProjectManifest.class);
    }
  }

  private static void appendManifestSummary(
      StringBuilder summary,
      ProjectManifest manifest,
      String expectedFileType) {
    assertEquals(expectedFileType, manifest.metadata.fileType);
    summary.append("manifest:\n");
    summary.append("  description.name: ").append(manifest.description.name).append('\n');
    summary.append("  metadata.fileType: ").append(manifest.metadata.fileType).append('\n');
    summary.append("  metadata.formatVersion: ").append(manifest.metadata.formatVersion).append('\n');
    summary.append("  metadata.identifier.type: ").append(manifest.metadata.identifier.type).append('\n');
    summary.append("  projectStructure.sceneCameraType: ")
        .append(manifest.projectStructure.sceneCameraType)
        .append('\n');
    summary.append("  prerequisites:\n");
    if (manifest.prerequisites.isEmpty()) {
      summary.append("    <none>\n");
    } else {
      manifest.prerequisites.stream()
          .sorted(Comparator.comparing(identifier -> identifier.name))
          .forEach(identifier -> summary
              .append("    ")
              .append(identifier.name)
              .append(":")
              .append(identifier.type)
              .append(":")
              .append(identifier.version)
              .append('\n'));
    }
    summary.append("  resources:\n");
    if (manifest.resources.isEmpty()) {
      summary.append("    <none>\n");
    } else {
      manifest.resources.stream()
          .sorted(Comparator.comparing(resource -> resource.name))
          .forEach(resource -> summary
              .append("    ")
              .append(resource.name)
              .append(":")
              .append(resource.getClass().getSimpleName())
              .append('\n'));
    }
  }

  private static void assertMatchesSnapshot(String snapshotName, String actual) throws Exception {
    String normalizedActual = normalizeLineEndings(actual);
    if (Boolean.getBoolean(UPDATE_PROPERTY)) {
      Path snapshotPath = snapshotSourcePath(snapshotName);
      Files.createDirectories(snapshotPath.getParent());
      Files.writeString(snapshotPath, normalizedActual, StandardCharsets.UTF_8);
      return;
    }

    try (InputStream inputStream = RabbitHoleBaselineParityTest.class.getResourceAsStream(
        SNAPSHOT_RESOURCE_ROOT + snapshotName)) {
      if (inputStream == null) {
        fail("Missing baseline snapshot " + snapshotName
            + ". Regenerate with -D" + UPDATE_PROPERTY + "=true.");
      }
      String expected = normalizeLineEndings(new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
      assertEquals(
          "Baseline snapshot mismatch for " + snapshotName
              + ". Review behavior changes before regenerating with -D" + UPDATE_PROPERTY + "=true.",
          expected,
          normalizedActual);
    }
  }

  private static Path snapshotSourcePath(String snapshotName) throws Exception {
    Path testClassesDirectory = Path.of(
        RabbitHoleBaselineParityTest.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    Path moduleDirectory = testClassesDirectory.getParent().getParent();
    String configuredDirectory = System.getProperty(SNAPSHOT_DIR_PROPERTY);
    if ((configuredDirectory != null) && !configuredDirectory.isBlank()) {
      return configuredSnapshotDirectory(configuredDirectory, moduleDirectory).resolve(snapshotName);
    }
    Path resourcesDirectory = moduleDirectory.resolve("src").resolve("test").resolve("resources");
    if (Files.isDirectory(resourcesDirectory) || Files.isDirectory(resourcesDirectory.getParent())) {
      return resourcesDirectory.resolve(SNAPSHOT_PACKAGE_PATH).resolve(snapshotName);
    }
    for (Path resourcesRoot : List.of(
        Path.of("src", "test", "resources"),
        Path.of("netbeans", "src", "test", "resources"))) {
      if (Files.isDirectory(resourcesRoot) || Files.isDirectory(resourcesRoot.getParent())) {
        return resourcesRoot.resolve(SNAPSHOT_PACKAGE_PATH).resolve(snapshotName);
      }
    }
    throw new IllegalStateException(
        "Unable to locate snapshot source directory. Set -D"
            + SNAPSHOT_DIR_PROPERTY
            + "=netbeans/src/test/resources/org/alice/netbeans/project/parity");
  }

  private static Path configuredSnapshotDirectory(String configuredDirectory, Path moduleDirectory) {
    Path configuredPath = Path.of(configuredDirectory);
    if (configuredPath.isAbsolute()) {
      return configuredPath;
    }
    if ((configuredPath.getNameCount() > 0)
        && configuredPath.getName(0).equals(moduleDirectory.getFileName())
        && (moduleDirectory.getParent() != null)) {
      return moduleDirectory.getParent().resolve(configuredPath);
    }
    return moduleDirectory.resolve(configuredPath);
  }

  private static String relativePath(Path root, Path file) {
    return root.relativize(file).toString().replace(File.separatorChar, '/');
  }

  private static String normalizeLineEndings(String text) {
    return text.replace("\r\n", "\n").replace('\r', '\n');
  }

  private static String sha256(byte[] bytes) throws Exception {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] hash = digest.digest(bytes);
    try (Formatter formatter = new Formatter(Locale.ROOT)) {
      for (byte b : hash) {
        formatter.format("%02x", b);
      }
      return formatter.toString();
    }
  }

  private static UUID stableUuid(String key) {
    return UUID.nameUUIDFromBytes(("rabbithole-baseline:" + key).getBytes(StandardCharsets.UTF_8));
  }
}
