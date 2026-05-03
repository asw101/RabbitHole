package org.alice.netbeans.project;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.lgna.common.Resource;
import org.lgna.project.Project;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;
import org.lgna.project.io.IoUtilities;
import org.lgna.story.SProgram;
import org.openide.filesystems.FileObject;

import java.io.File;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

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

  @Test
  public void generatesResourceFileAndResourcesTypeFromSyntheticAliceProject() throws Exception {
    byte[] data = "hello alice".getBytes(StandardCharsets.UTF_8);
    Project project = new Project(programType("Program"), Project.SceneCameraType.WindowCamera);
    project.addResource(new TestResource("note.txt", "text/plain", data));
    File aliceProject = temporaryFolder.newFile("synthetic-resource.a3p");
    IoUtilities.writeProject(aliceProject, project);
    File sourceDirectory = temporaryFolder.newFolder("generated-resource-src");

    ProjectCodeGenerator.generateCode(aliceProject, sourceDirectory, null, false);

    Path generatedResourcePath = sourceDirectory.toPath().resolve("resources").resolve("note.txt");
    Path resourcesTypePath = sourceDirectory.toPath().resolve("Resources.java");
    assertArrayEquals(data, Files.readAllBytes(generatedResourcePath));
    assertTrue(Files.exists(resourcesTypePath));
    String resourcesSource = Files.readString(resourcesTypePath);
    assertTrue(resourcesSource.contains("class Resources"));
    assertTrue(resourcesSource.contains("note.txt"));
  }

  @Test
  public void generatedSyntheticAliceProjectSourcesCompile() throws Exception {
    File aliceProject = temporaryFolder.newFile("synthetic-compile.a3p");
    IoUtilities.writeProject(
        aliceProject,
        new Project(programType("Program"), Project.SceneCameraType.WindowCamera));
    File sourceDirectory = temporaryFolder.newFolder("compiled-source-src");
    ProjectCodeGenerator.generateCode(aliceProject, sourceDirectory, null, false);

    compileJavaSources(
        temporaryFolder.newFolder("compiled-classes").toPath(),
        sourceDirectory.toPath().resolve("Program.java"),
        sourceDirectory.toPath().resolve("AliceJavaFXLauncher.java"));
  }

  @Test
  public void generatedSyntheticResourceProjectSourcesCompile() throws Exception {
    Project project = new Project(programType("Program"), Project.SceneCameraType.WindowCamera);
    project.addResource(new TestResource("note.txt", "text/plain", "hello alice".getBytes(StandardCharsets.UTF_8)));
    File aliceProject = temporaryFolder.newFile("synthetic-resource-compile.a3p");
    IoUtilities.writeProject(aliceProject, project);
    File sourceDirectory = temporaryFolder.newFolder("compiled-resource-source-src");
    ProjectCodeGenerator.generateCode(aliceProject, sourceDirectory, null, false);

    compileJavaSources(
        temporaryFolder.newFolder("compiled-resource-classes").toPath(),
        sourceDirectory.toPath().resolve("Program.java"),
        sourceDirectory.toPath().resolve("AliceJavaFXLauncher.java"),
        sourceDirectory.toPath().resolve("Resources.java"));
  }

  @Test
  public void generatedSyntheticResourcesLoadCopiedResourceBytes() throws Exception {
    byte[] data = "hello alice".getBytes(StandardCharsets.UTF_8);
    Project project = new Project(programType("Program"), Project.SceneCameraType.WindowCamera);
    project.addResource(new TestResource("note.txt", "text/plain", data));
    File aliceProject = temporaryFolder.newFile("synthetic-resource-runtime.a3p");
    IoUtilities.writeProject(aliceProject, project);
    File sourceDirectory = temporaryFolder.newFolder("runtime-resource-source-src");
    ProjectCodeGenerator.generateCode(aliceProject, sourceDirectory, null, false);

    assertGeneratedResourceLoads(sourceDirectory.toPath(), data);
  }

  @Test
  public void generatedSyntheticResourcesLoadOriginalFileNameWhenDisplayNameDiffers() throws Exception {
    byte[] data = "hello alice".getBytes(StandardCharsets.UTF_8);
    TestResource resource = new TestResource("note.txt", "text/plain", data);
    resource.setName("friendly note");
    Project project = new Project(programType("Program"), Project.SceneCameraType.WindowCamera);
    project.addResource(resource);
    File aliceProject = temporaryFolder.newFile("synthetic-resource-original-name.a3p");
    IoUtilities.writeProject(aliceProject, project);
    File sourceDirectory = temporaryFolder.newFolder("runtime-original-name-src");
    ProjectCodeGenerator.generateCode(aliceProject, sourceDirectory, null, false);

    assertTrue(Files.exists(sourceDirectory.toPath().resolve("resources").resolve("note.txt")));
    assertFalse(Files.exists(sourceDirectory.toPath().resolve("resources").resolve("friendly note")));
    assertGeneratedResourceLoads(sourceDirectory.toPath(), data);
  }

  @Test
  public void generatedSyntheticResourcesLoadDuplicateOriginalFileNames() throws Exception {
    TestResource first = new TestResource("note.txt", "text/plain", "first".getBytes(StandardCharsets.UTF_8));
    first.setName("first note");
    TestResource second = new TestResource("note.txt", "text/plain", "second".getBytes(StandardCharsets.UTF_8));
    second.setName("second note");
    Project project = new Project(programType("Program"), Project.SceneCameraType.WindowCamera);
    project.addResource(first);
    project.addResource(second);
    File aliceProject = temporaryFolder.newFile("synthetic-resource-duplicate-original-name.a3p");
    IoUtilities.writeProject(aliceProject, project);
    File sourceDirectory = temporaryFolder.newFolder("runtime-duplicate-original-name-src");
    ProjectCodeGenerator.generateCode(aliceProject, sourceDirectory, null, false);

    assertTrue(Files.exists(sourceDirectory.toPath().resolve("resources").resolve("note.txt")));
    assertTrue(Files.exists(sourceDirectory.toPath().resolve("resources2").resolve("note.txt")));
    List<Resource> generatedResources = loadGeneratedResources(sourceDirectory.toPath());
    Set<String> generatedResourceText = generatedResources.stream()
        .map(resource -> new String(resource.getData(), StandardCharsets.UTF_8))
        .collect(Collectors.toSet());
    assertEquals(Set.of("first", "second"), generatedResourceText);
  }

  private static NamedUserType programType(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    type.superType.setValue(JavaType.getInstance(SProgram.class));
    type.methods.add(mainMethod());
    return type;
  }

  private static UserMethod mainMethod() {
    UserParameter argsParameter = new UserParameter("args", String[].class);
    UserMethod mainMethod = new UserMethod(
        "main",
        Void.TYPE,
        new UserParameter[] {argsParameter},
        new BlockStatement());
    mainMethod.isStatic.setValue(true);
    mainMethod.isSignatureLocked.setValue(true);
    return mainMethod;
  }

  private static void compileJavaSources(Path outputDirectory, Path... sources) throws Exception {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    assertNotNull("Tests must run on a JDK with the Java compiler available", compiler);
    StringWriter compilerOutput = new StringWriter();
    try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
      List<String> options = Arrays.asList(
          "-classpath",
          System.getProperty("java.class.path"),
          "-proc:none",
          "-d",
          outputDirectory.toString());
      Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(
          Arrays.stream(sources).map(Path::toFile).toList());
      Boolean result = compiler.getTask(
          compilerOutput,
          fileManager,
          null,
          options,
          null,
          compilationUnits).call();
      assertTrue(compilerOutput.toString(), result);
    }
  }

  private void assertGeneratedResourceLoads(Path sourceDirectory, byte[] expectedData) throws Exception {
    List<Resource> generatedResources = loadGeneratedResources(sourceDirectory);
    assertEquals(1, generatedResources.size());
    Resource generatedResource = generatedResources.get(0);
    assertEquals("text/plain", generatedResource.getContentType());
    assertArrayEquals(expectedData, generatedResource.getData());
  }

  private List<Resource> loadGeneratedResources(Path sourceDirectory) throws Exception {
    Path classesDirectory = temporaryFolder.newFolder("generated-resource-classes").toPath();
    compileJavaSources(
        classesDirectory,
        sourceDirectory.resolve("Program.java"),
        sourceDirectory.resolve("AliceJavaFXLauncher.java"),
        sourceDirectory.resolve("Resources.java"));
    copyGeneratedResourceFiles(sourceDirectory, classesDirectory);

    try (URLClassLoader classLoader = new URLClassLoader(
        new URL[] {classesDirectory.toUri().toURL()},
        ProjectCodeGeneratorTest.class.getClassLoader())) {
      Class<?> resourcesClass = Class.forName("Resources", true, classLoader);
      return Arrays.stream(resourcesClass.getFields())
          .filter(field -> TestResource.class.isAssignableFrom(field.getType()))
          .map(field -> getGeneratedResource(field))
          .toList();
    }
  }

  private static Resource getGeneratedResource(Field field) {
    try {
      field.setAccessible(true);
      return (Resource) field.get(null);
    } catch (IllegalAccessException e) {
      throw new AssertionError(e);
    }
  }

  private static void copyGeneratedResourceFiles(Path sourceDirectory, Path classesDirectory) throws Exception {
    try (Stream<Path> paths = Files.walk(sourceDirectory)) {
      for (Path path : paths.filter(Files::isRegularFile).toList()) {
        Path relativePath = sourceDirectory.relativize(path);
        if (relativePath.getNameCount() > 1 && relativePath.getName(0).toString().startsWith("resources")) {
          Path classpathResourcePath = classesDirectory.resolve(relativePath);
          Files.createDirectories(classpathResourcePath.getParent());
          Files.copy(path, classpathResourcePath, StandardCopyOption.REPLACE_EXISTING);
        }
      }
    }
  }

  public static class TestResource extends Resource {
    public TestResource(String fileName, String contentType, byte[] data) {
      super(fileName, contentType, data);
    }

    public TestResource(Class<?> resourceClass, String resourceName, String contentType) {
      super(resourceClass, resourceName, contentType);
    }

    private TestResource(UUID uuid) {
      super(uuid);
    }

    public static TestResource valueOf(String uuidText) {
      return new TestResource(UUID.fromString(uuidText));
    }
  }
}
