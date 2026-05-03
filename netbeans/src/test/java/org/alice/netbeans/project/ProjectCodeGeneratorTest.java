package org.alice.netbeans.project;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.lgna.common.Resource;
import org.lgna.project.Project;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.BooleanExpressionBodyPair;
import org.lgna.project.ast.BooleanLiteral;
import org.lgna.project.ast.Comment;
import org.lgna.project.ast.ConditionalStatement;
import org.lgna.project.ast.CountLoop;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.LocalDeclarationStatement;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.ParameterAccess;
import org.lgna.project.ast.StringLiteral;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.UserLocal;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;
import org.lgna.project.ast.WhileLoop;
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
  public void generatedLauncherPassesStartingArgsToProgramMain() throws Exception {
    Path sourceDirectory = temporaryFolder.newFolder("launcher-runtime-src").toPath();
    ProjectCodeGenerator.generateLauncher(sourceDirectory.toFile());
    writeJavaSource(
        sourceDirectory.resolve("Program.java"),
        """
        public class Program {
          public static volatile String[] receivedArgs;

          public static void main(String[] args) {
            receivedArgs = args;
          }
        }
        """);
    writeJavaSource(
        sourceDirectory.resolve("javafx/application/Application.java"),
        """
        package javafx.application;

        public abstract class Application {
          public abstract void start(javafx.stage.Stage stage) throws Exception;

          public static void launch(String[] args) {
            try {
              String callerClassName = StackWalker
                  .getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                  .walk(frames -> frames.skip(1).findFirst().orElseThrow().getDeclaringClass().getName());
              Application application = (Application) Class
                  .forName(callerClassName)
                  .getDeclaredConstructor()
                  .newInstance();
              application.start(new javafx.stage.Stage());
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          }
        }
        """);
    writeJavaSource(
        sourceDirectory.resolve("javafx/stage/Stage.java"),
        """
        package javafx.stage;

        public class Stage {
        }
        """);
    Path classesDirectory = temporaryFolder.newFolder("launcher-runtime-classes").toPath();
    compileJavaSources(
        classesDirectory,
        sourceDirectory.resolve("AliceJavaFXLauncher.java"),
        sourceDirectory.resolve("Program.java"),
        sourceDirectory.resolve("javafx/application/Application.java"),
        sourceDirectory.resolve("javafx/stage/Stage.java"));

    try (URLClassLoader classLoader = new URLClassLoader(
        new URL[] {classesDirectory.toUri().toURL()},
        ClassLoader.getPlatformClassLoader())) {
      Class<?> launcherClass = Class.forName("AliceJavaFXLauncher", true, classLoader);
      Class<?> programClass = Class.forName("Program", true, classLoader);
      String[] args = {"alpha", "beta"};

      launcherClass.getMethod("main", String[].class).invoke(null, (Object) args);

      assertArrayEquals(args, waitForStringArray(programClass.getField("receivedArgs")));
    }
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
  public void generatedSyntheticUserMethodSourceCompiles() throws Exception {
    File aliceProject = temporaryFolder.newFile("synthetic-method.a3p");
    IoUtilities.writeProject(
        aliceProject,
        new Project(programTypeWithUserMethod(), Project.SceneCameraType.WindowCamera));
    File sourceDirectory = temporaryFolder.newFolder("generated-method-src");
    ProjectCodeGenerator.generateCode(aliceProject, sourceDirectory, null, false);

    Path programPath = sourceDirectory.toPath().resolve("Program.java");
    String programSource = Files.readString(programPath);
    assertTrue(programSource.contains("void sayHello()"));
    assertTrue(programSource.contains("hello alice"));
    compileJavaSources(
        temporaryFolder.newFolder("generated-method-classes").toPath(),
        programPath,
        sourceDirectory.toPath().resolve("AliceJavaFXLauncher.java"));
  }

  @Test
  public void generatedSyntheticUserMethodLocalDeclarationSourceCompiles() throws Exception {
    File aliceProject = temporaryFolder.newFile("synthetic-local-declaration.a3p");
    IoUtilities.writeProject(
        aliceProject,
        new Project(programTypeWithLocalDeclarationMethod(), Project.SceneCameraType.WindowCamera));
    File sourceDirectory = temporaryFolder.newFolder("generated-local-declaration-src");
    ProjectCodeGenerator.generateCode(aliceProject, sourceDirectory, null, false);

    Path programPath = sourceDirectory.toPath().resolve("Program.java");
    String programSource = Files.readString(programPath);
    assertTrue(programSource.contains("void sayHello()"));
    assertTrue(programSource, programSource.contains("final String greeting=\"hello alice\";"));
    compileJavaSources(
        temporaryFolder.newFolder("generated-local-declaration-classes").toPath(),
        programPath,
        sourceDirectory.toPath().resolve("AliceJavaFXLauncher.java"));
  }

  @Test
  public void generatedSyntheticUserMethodParameterSourceCompiles() throws Exception {
    File aliceProject = temporaryFolder.newFile("synthetic-parameter.a3p");
    IoUtilities.writeProject(
        aliceProject,
        new Project(programTypeWithParameterMethod(), Project.SceneCameraType.WindowCamera));
    File sourceDirectory = temporaryFolder.newFolder("generated-parameter-src");
    ProjectCodeGenerator.generateCode(aliceProject, sourceDirectory, null, false);

    Path programPath = sourceDirectory.toPath().resolve("Program.java");
    String programSource = Files.readString(programPath);
    assertTrue(programSource.contains("void remember(String message)"));
    assertTrue(programSource, programSource.contains("final String copy=message;"));
    compileJavaSources(
        temporaryFolder.newFolder("generated-parameter-classes").toPath(),
        programPath,
        sourceDirectory.toPath().resolve("AliceJavaFXLauncher.java"));
  }

  @Test
  public void generatedSyntheticUserMethodInvocationSourceCompiles() throws Exception {
    File aliceProject = temporaryFolder.newFile("synthetic-method-invocation.a3p");
    IoUtilities.writeProject(
        aliceProject,
        new Project(programTypeWithMethodInvocation(), Project.SceneCameraType.WindowCamera));
    File sourceDirectory = temporaryFolder.newFolder("generated-method-invocation-src");
    ProjectCodeGenerator.generateCode(aliceProject, sourceDirectory, null, false);

    Path programPath = sourceDirectory.toPath().resolve("Program.java");
    String programSource = Files.readString(programPath);
    assertTrue(programSource.contains("void sayHello()"));
    assertTrue(programSource.contains("void callSayHello()"));
    assertTrue(programSource, programSource.contains("this.sayHello();"));
    compileJavaSources(
        temporaryFolder.newFolder("generated-method-invocation-classes").toPath(),
        programPath,
        sourceDirectory.toPath().resolve("AliceJavaFXLauncher.java"));
  }

  @Test
  public void generatedSyntheticUserMethodInvocationWithArgumentSourceCompiles() throws Exception {
    File aliceProject = temporaryFolder.newFile("synthetic-method-invocation-argument.a3p");
    IoUtilities.writeProject(
        aliceProject,
        new Project(programTypeWithMethodInvocationArgument(), Project.SceneCameraType.WindowCamera));
    File sourceDirectory = temporaryFolder.newFolder("generated-method-invocation-argument-src");
    ProjectCodeGenerator.generateCode(aliceProject, sourceDirectory, null, false);

    Path programPath = sourceDirectory.toPath().resolve("Program.java");
    String programSource = Files.readString(programPath);
    assertTrue(programSource.contains("void remember(String message)"));
    assertTrue(programSource.contains("void callRemember()"));
    assertTrue(programSource, programSource.contains("this.remember(\"hello alice\");"));
    compileJavaSources(
        temporaryFolder.newFolder("generated-method-invocation-argument-classes").toPath(),
        programPath,
        sourceDirectory.toPath().resolve("AliceJavaFXLauncher.java"));
  }

  @Test
  public void generatedSyntheticUserMethodConditionalSourceCompiles() throws Exception {
    File aliceProject = temporaryFolder.newFile("synthetic-conditional.a3p");
    IoUtilities.writeProject(
        aliceProject,
        new Project(programTypeWithConditionalMethod(), Project.SceneCameraType.WindowCamera));
    File sourceDirectory = temporaryFolder.newFolder("generated-conditional-src");
    ProjectCodeGenerator.generateCode(aliceProject, sourceDirectory, null, false);

    Path programPath = sourceDirectory.toPath().resolve("Program.java");
    String programSource = Files.readString(programPath);
    assertTrue(programSource.contains("void choose()"));
    assertTrue(programSource, programSource.contains("if(true)"));
    assertTrue(programSource, programSource.contains(" else"));
    compileJavaSources(
        temporaryFolder.newFolder("generated-conditional-classes").toPath(),
        programPath,
        sourceDirectory.toPath().resolve("AliceJavaFXLauncher.java"));
  }

  @Test
  public void generatedSyntheticUserMethodCountLoopSourceCompiles() throws Exception {
    File aliceProject = temporaryFolder.newFile("synthetic-count-loop.a3p");
    IoUtilities.writeProject(
        aliceProject,
        new Project(programTypeWithCountLoopMethod(), Project.SceneCameraType.WindowCamera));
    File sourceDirectory = temporaryFolder.newFolder("generated-count-loop-src");
    ProjectCodeGenerator.generateCode(aliceProject, sourceDirectory, null, false);

    Path programPath = sourceDirectory.toPath().resolve("Program.java");
    String programSource = Files.readString(programPath);
    assertTrue(programSource.contains("void repeat()"));
    assertTrue(programSource, programSource.contains("for(Integer indexA=0;indexA<3;indexA++)"));
    compileJavaSources(
        temporaryFolder.newFolder("generated-count-loop-classes").toPath(),
        programPath,
        sourceDirectory.toPath().resolve("AliceJavaFXLauncher.java"));
  }

  @Test
  public void generatedSyntheticUserMethodWhileLoopSourceCompiles() throws Exception {
    File aliceProject = temporaryFolder.newFile("synthetic-while-loop.a3p");
    IoUtilities.writeProject(
        aliceProject,
        new Project(programTypeWithWhileLoopMethod(), Project.SceneCameraType.WindowCamera));
    File sourceDirectory = temporaryFolder.newFolder("generated-while-loop-src");
    ProjectCodeGenerator.generateCode(aliceProject, sourceDirectory, null, false);

    Path programPath = sourceDirectory.toPath().resolve("Program.java");
    String programSource = Files.readString(programPath);
    assertTrue(programSource.contains("void spin()"));
    assertTrue(programSource, programSource.contains("while (true)"));
    compileJavaSources(
        temporaryFolder.newFolder("generated-while-loop-classes").toPath(),
        programPath,
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

  @Test
  public void generatedSyntheticResourcesLoadBlankOriginalFileName() throws Exception {
    byte[] data = "hello alice".getBytes(StandardCharsets.UTF_8);
    TestResource resource = new TestResource("note.txt", "text/plain", data);
    resource.setOriginalFileName("");
    resource.setName("friendly note");
    Project project = new Project(programType("Program"), Project.SceneCameraType.WindowCamera);
    project.addResource(resource);
    File aliceProject = temporaryFolder.newFile("synthetic-resource-blank-original-name.a3p");
    IoUtilities.writeProject(aliceProject, project);
    File sourceDirectory = temporaryFolder.newFolder("runtime-blank-original-name-src");
    ProjectCodeGenerator.generateCode(aliceProject, sourceDirectory, null, false);

    assertTrue(Files.exists(sourceDirectory.toPath().resolve("resources").resolve("friendly_note")));
    assertGeneratedResourceLoads(sourceDirectory.toPath(), data);
  }

  @Test
  public void generatedSyntheticResourcesLoadUnsafeOriginalFileName() throws Exception {
    byte[] data = "hello alice".getBytes(StandardCharsets.UTF_8);
    TestResource resource = new TestResource("note.txt", "text/plain", data);
    resource.setOriginalFileName("../folder\\note.txt");
    Project project = new Project(programType("Program"), Project.SceneCameraType.WindowCamera);
    project.addResource(resource);
    File aliceProject = temporaryFolder.newFile("synthetic-resource-unsafe-original-name.a3p");
    IoUtilities.writeProject(aliceProject, project);
    File sourceDirectory = temporaryFolder.newFolder("runtime-unsafe-original-name-src");
    ProjectCodeGenerator.generateCode(aliceProject, sourceDirectory, null, false);

    assertTrue(Files.exists(sourceDirectory.toPath().resolve("resources").resolve(".._folder_note.txt")));
    assertFalse(Files.exists(sourceDirectory.toPath().resolve("folder").resolve("note.txt")));
    assertGeneratedResourceLoads(sourceDirectory.toPath(), data);
  }

  private static NamedUserType programType(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    type.superType.setValue(JavaType.getInstance(SProgram.class));
    type.methods.add(mainMethod());
    return type;
  }

  private static NamedUserType programTypeWithUserMethod() {
    NamedUserType type = programType("Program");
    UserMethod userMethod = new UserMethod(
        "sayHello",
        Void.TYPE,
        new UserParameter[0],
        new BlockStatement(new Comment("hello alice")));
    type.methods.add(userMethod);
    return type;
  }

  private static NamedUserType programTypeWithLocalDeclarationMethod() {
    NamedUserType type = programType("Program");
    UserLocal greeting = new UserLocal("greeting", String.class, true);
    UserMethod userMethod = new UserMethod(
        "sayHello",
        Void.TYPE,
        new UserParameter[0],
        new BlockStatement(new LocalDeclarationStatement(greeting, new StringLiteral("hello alice"))));
    type.methods.add(userMethod);
    return type;
  }

  private static NamedUserType programTypeWithParameterMethod() {
    NamedUserType type = programType("Program");
    UserParameter message = new UserParameter("message", String.class);
    UserLocal copy = new UserLocal("copy", String.class, true);
    UserMethod userMethod = new UserMethod(
        "remember",
        Void.TYPE,
        new UserParameter[] {message},
        new BlockStatement(new LocalDeclarationStatement(copy, new ParameterAccess(message))));
    type.methods.add(userMethod);
    return type;
  }

  private static NamedUserType programTypeWithMethodInvocation() {
    NamedUserType type = programType("Program");
    UserMethod sayHello = new UserMethod(
        "sayHello",
        Void.TYPE,
        new UserParameter[0],
        new BlockStatement(new Comment("hello alice")));
    UserMethod callSayHello = new UserMethod(
        "callSayHello",
        Void.TYPE,
        new UserParameter[0],
        new BlockStatement(AstUtilities.createMethodInvocationStatement(new ThisExpression(), sayHello)));
    type.methods.add(sayHello);
    type.methods.add(callSayHello);
    return type;
  }

  private static NamedUserType programTypeWithMethodInvocationArgument() {
    NamedUserType type = programType("Program");
    UserParameter message = new UserParameter("message", String.class);
    UserLocal copy = new UserLocal("copy", String.class, true);
    UserMethod remember = new UserMethod(
        "remember",
        Void.TYPE,
        new UserParameter[] {message},
        new BlockStatement(new LocalDeclarationStatement(copy, new ParameterAccess(message))));
    UserMethod callRemember = new UserMethod(
        "callRemember",
        Void.TYPE,
        new UserParameter[0],
        new BlockStatement(AstUtilities.createMethodInvocationStatement(
            new ThisExpression(),
            remember,
            new StringLiteral("hello alice"))));
    type.methods.add(remember);
    type.methods.add(callRemember);
    return type;
  }

  private static NamedUserType programTypeWithConditionalMethod() {
    NamedUserType type = programType("Program");
    UserMethod choose = new UserMethod(
        "choose",
        Void.TYPE,
        new UserParameter[0],
        new BlockStatement(new ConditionalStatement(
            new BooleanExpressionBodyPair[] {
                new BooleanExpressionBodyPair(new BooleanLiteral(true), new BlockStatement(new Comment("then branch")))
            },
            new BlockStatement(new Comment("else branch")))));
    type.methods.add(choose);
    return type;
  }

  private static NamedUserType programTypeWithCountLoopMethod() {
    NamedUserType type = programType("Program");
    CountLoop loop = AstUtilities.createCountLoop(new IntegerLiteral(3));
    loop.body.getValue().statements.add(new Comment("loop body"));
    UserMethod repeat = new UserMethod(
        "repeat",
        Void.TYPE,
        new UserParameter[0],
        new BlockStatement(loop));
    type.methods.add(repeat);
    return type;
  }

  private static NamedUserType programTypeWithWhileLoopMethod() {
    NamedUserType type = programType("Program");
    WhileLoop loop = AstUtilities.createWhileLoop(new BooleanLiteral(true));
    loop.body.getValue().statements.add(new Comment("loop body"));
    UserMethod spin = new UserMethod(
        "spin",
        Void.TYPE,
        new UserParameter[0],
        new BlockStatement(loop));
    type.methods.add(spin);
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

  private static void writeJavaSource(Path sourcePath, String source) throws Exception {
    Files.createDirectories(sourcePath.getParent());
    Files.writeString(sourcePath, source);
  }

  private static String[] waitForStringArray(Field field) throws Exception {
    for (int attempt = 0; attempt < 100; attempt++) {
      String[] value = (String[]) field.get(null);
      if (value != null) {
        return value;
      }
      Thread.sleep(10L);
    }
    return (String[]) field.get(null);
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
