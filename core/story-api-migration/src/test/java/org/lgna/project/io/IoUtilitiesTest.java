package org.lgna.project.io;

import org.alice.tweedle.file.ManifestEncoderDecoder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.lgna.common.Resource;
import org.lgna.common.resources.AudioResource;
import org.lgna.common.resources.ImageResource;
import org.lgna.project.Project;
import org.lgna.project.Version;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.LocalDeclarationStatement;
import org.lgna.project.ast.ResourceExpression;
import org.lgna.project.ast.UserLocal;
import org.lgna.project.ast.UserMethod;
import org.lgna.story.SProgram;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

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

  @Test
  public void writesAndReadsSyntheticProjectResource() throws Exception {
    Project project = new Project(programType("Program"), Project.SceneCameraType.WindowCamera);
    TestResource resource = new TestResource("note.txt", "text/plain", "hello alice".getBytes(StandardCharsets.UTF_8));
    project.addResource(resource);
    File projectFile = temporaryFolder.newFile("synthetic-resource.a3p");

    IoUtilities.writeProject(projectFile, project);

    Project readProject = IoUtilities.readProject(projectFile);
    assertEquals(1, readProject.getResources().size());
    Resource readResource = readProject.getResources().iterator().next();
    assertEquals(TestResource.class, readResource.getClass());
    assertEquals(resource.getId(), readResource.getId());
    assertEquals("note.txt", readResource.getOriginalFileName());
    assertEquals("note.txt", readResource.getName());
    assertEquals("text/plain", readResource.getContentType());
    assertArrayEquals("hello alice".getBytes(StandardCharsets.UTF_8), readResource.getData());

    try (ZipFile zipFile = new ZipFile(projectFile)) {
      assertNotNull(zipFile.getEntry("resources.xml"));
      assertNotNull(zipFile.getEntry("resources/note.txt"));
    }
  }

  @Test
  public void readsExportedPlayerArchiveImageResource() throws Exception {
    ImageResource imageResource = new ImageResource(
        new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
        "picture.png",
        "png");
    Project project = new Project(programTypeReferencingImageResource("Program", imageResource), Project.SceneCameraType.WindowCamera);
    project.addResource(imageResource);
    File exportFile = temporaryFolder.newFile("exported-image.a3w");

    IoUtilities.exportProject(exportFile, project);

    Project readProject = IoUtilities.readProject(exportFile);
    assertNull("Tweedle decoding is still not implemented for player archives", readProject.getProgramType());
    assertEquals(1, readProject.getResources().size());
    Resource readResource = readProject.getResources().iterator().next();
    assertEquals(ImageResource.class, readResource.getClass());
    assertEquals(imageResource.getId(), readResource.getId());
    assertEquals("picture.png", readResource.getOriginalFileName());
    assertEquals("picture.png", readResource.getName());
    assertEquals("png", readResource.getContentType());
    assertArrayEquals(imageResource.getData(), readResource.getData());
  }

  @Test
  public void readsExportedPlayerArchiveAudioResource() throws Exception {
    byte[] audioBytes = new byte[] {0, 1, 2, 3};
    File audioFile = temporaryFolder.newFile("sound.wav");
    Files.write(audioFile.toPath(), audioBytes);
    AudioResource audioResource = new AudioResource(audioFile, "audio.x_wav");
    Project project = new Project(programTypeReferencingResource("Program", AudioResource.class, audioResource), Project.SceneCameraType.WindowCamera);
    project.addResource(audioResource);
    File exportFile = temporaryFolder.newFile("exported-audio.a3w");

    IoUtilities.exportProject(exportFile, project);

    Project readProject = IoUtilities.readProject(exportFile);
    assertNull("Tweedle decoding is still not implemented for player archives", readProject.getProgramType());
    assertEquals(1, readProject.getResources().size());
    Resource readResource = readProject.getResources().iterator().next();
    assertEquals(AudioResource.class, readResource.getClass());
    assertEquals(audioResource.getId(), readResource.getId());
    assertEquals("sound.wav", readResource.getOriginalFileName());
    assertEquals("sound.wav", readResource.getName());
    assertEquals("audio.x_wav", readResource.getContentType());
    assertArrayEquals(audioBytes, readResource.getData());
    assertEquals(0.0, ((AudioResource) readResource).getDuration(), 0.0);
  }

  @Test
  public void jsonPlayerReaderReportsFutureVersion() throws Exception {
    String futureVersion = "999.0.0.0";
    File exportFile = temporaryFolder.newFile("future-export.a3w");
    writePlayerArchive(exportFile, futureVersion);

    Version version = IoUtilities.projectReader(exportFile).checkForFutureVersion();

    assertEquals(futureVersion, version.toString());
  }

  @Test
  public void jsonPlayerExportUsesSafeDistinctResourceEntries() throws Exception {
    ImageResource first = imageResource("image.png", 0xFFFF0000);
    ImageResource duplicate = imageResource("image.png", 0xFF00FF00);
    ImageResource pathLike = imageResource("../folder/picture.png", 0xFF0000FF);
    Project project = new Project(
        programTypeReferencingImageResources("Program", first, duplicate, pathLike),
        Project.SceneCameraType.WindowCamera);
    File exportFile = temporaryFolder.newFile("safe-resource-entries.a3w");

    IoUtilities.exportProject(exportFile, project);

    try (ZipFile zipFile = new ZipFile(exportFile)) {
      assertNotNull(zipFile.getEntry("resources/image.png"));
      assertNotNull(zipFile.getEntry("resources2/image.png"));
      assertNotNull(zipFile.getEntry("resources/.._folder_picture.png"));
      assertNull(zipFile.getEntry("resources/../folder/picture.png"));
    }
    Project readProject = IoUtilities.readProject(exportFile);
    Map<UUID, Resource> resourcesById = resourcesById(readProject);
    assertArrayEquals(first.getData(), resourcesById.get(first.getId()).getData());
    assertArrayEquals(duplicate.getData(), resourcesById.get(duplicate.getId()).getData());
    assertArrayEquals(pathLike.getData(), resourcesById.get(pathLike.getId()).getData());
  }

  private static NamedUserType programType(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    type.superType.setValue(JavaType.getInstance(SProgram.class));
    return type;
  }

  private static NamedUserType programTypeReferencingImageResource(String name, ImageResource imageResource) {
    return programTypeReferencingImageResources(name, imageResource);
  }

  private static NamedUserType programTypeReferencingImageResources(String name, ImageResource... imageResources) {
    return programTypeReferencingResource(name, ImageResource.class, imageResources);
  }

  private static <T extends Resource> NamedUserType programTypeReferencingResource(String name, Class<T> resourceClass, T... resources) {
    NamedUserType type = programType(name);
    BlockStatement body = new BlockStatement();
    for (int i = 0; i < resources.length; i++) {
      UserLocal resource = new UserLocal("resource" + i, resourceClass, true);
      body.statements.add(new LocalDeclarationStatement(resource, new ResourceExpression(resourceClass, resources[i])));
    }
    UserMethod userMethod = new UserMethod("rememberResources", Void.TYPE, new org.lgna.project.ast.UserParameter[0], body);
    type.methods.add(userMethod);
    return type;
  }

  private static ImageResource imageResource(String fileName, int rgb) throws Exception {
    BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    image.setRGB(0, 0, rgb);
    return new ImageResource(image, fileName, "png");
  }

  private static Map<UUID, Resource> resourcesById(Project project) {
    Map<UUID, Resource> resources = new HashMap<>();
    for (Resource resource : project.getResources()) {
      resources.put(resource.getId(), resource);
    }
    return resources;
  }

  private static void writePlayerArchive(File file, String version) throws Exception {
    Project project = new Project(programType("Program"), Project.SceneCameraType.WindowCamera);
    try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(file))) {
      writeZipEntry(zipOutputStream, ProjectIo.VERSION_ENTRY_NAME, version);
      writeZipEntry(zipOutputStream, ProjectIo.MANIFEST_ENTRY_NAME, ManifestEncoderDecoder.toJson(project.createExportManifest()));
    }
  }

  private static void writeZipEntry(ZipOutputStream zipOutputStream, String name, String content) throws Exception {
    zipOutputStream.putNextEntry(new ZipEntry(name));
    zipOutputStream.write(content.getBytes(StandardCharsets.UTF_8));
    zipOutputStream.closeEntry();
  }

  public static class TestResource extends Resource {
    public TestResource(String fileName, String contentType, byte[] data) {
      super(fileName, contentType, data);
    }

    private TestResource(UUID uuid) {
      super(uuid);
    }

    public static TestResource valueOf(String uuidText) {
      return new TestResource(UUID.fromString(uuidText));
    }
  }
}
