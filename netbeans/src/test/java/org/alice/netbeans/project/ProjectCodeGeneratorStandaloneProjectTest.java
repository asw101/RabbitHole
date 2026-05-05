package org.alice.netbeans.project;

import org.alice.netbeans.Alice3LibraryClasspathTestSupport;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.lgna.project.Project;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.ParameterAccess;
import org.lgna.project.ast.TypeExpression;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;
import org.lgna.project.io.IoUtilities;
import org.lgna.story.SProgram;

import java.io.File;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import static org.junit.Assert.*;

public class ProjectCodeGeneratorStandaloneProjectTest {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void generatedStandaloneProjectCompilesAndLaunchesWithJavaFxStubs() throws Exception {
    File aliceProject = temporaryFolder.newFile("standalone-smoke.a3p");
    IoUtilities.writeProject(
        aliceProject,
        new Project(programType("Program"), Project.SceneCameraType.WindowCamera));
    Path projectDirectory = temporaryFolder.newFolder("standalone-project").toPath();
    Path sourceDirectory = projectDirectory.resolve("src");
    Files.createDirectories(sourceDirectory);

    ProjectCodeGenerator.generateCode(aliceProject, sourceDirectory.toFile(), null, false);
    writeJavaFxStubs(sourceDirectory);

    Path classesDirectory = projectDirectory.resolve("build").resolve("classes");
    compileJavaSources(classesDirectory, javaSourcesUnder(sourceDirectory));

    try (GeneratedProjectClassLoader classLoader = new GeneratedProjectClassLoader(
        new URL[] {classesDirectory.toUri().toURL()})) {
      Class<?> launcherClass = Class.forName("AliceJavaFXLauncher", true, classLoader);
      Class<?> applicationClass = Class.forName("javafx.application.Application", true, classLoader);
      String[] args = {"--project", "standalone-smoke.a3p"};

      launcherClass.getMethod("main", String[].class).invoke(null, (Object) args);

      assertArrayEquals(args, (String[]) applicationClass.getField("launchedArgs").get(null));
      assertTrue((Boolean) applicationClass.getField("startInvoked").get(null));
    }
  }

  @Test
  public void generatedLauncherInvokesProgramMainThroughStubbedJavaFxLaunchPath() throws Exception {
    File aliceProject = temporaryFolder.newFile("launcher-runtime.a3p");
    IoUtilities.writeProject(
        aliceProject,
        new Project(programTypeWithMainProbe("Program"), Project.SceneCameraType.WindowCamera));
    Path projectDirectory = temporaryFolder.newFolder("launcher-runtime-project").toPath();
    Path sourceDirectory = projectDirectory.resolve("src");
    Files.createDirectories(sourceDirectory);

    ProjectCodeGenerator.generateCode(aliceProject, sourceDirectory.toFile(), null, false);
    writeJavaFxStubs(sourceDirectory);

    String programSource = Files.readString(sourceDirectory.resolve("Program.java"));
    assertTrue(programSource, programSource.contains("recordGeneratedProgramMainArgs(args);"));

    Path classesDirectory = projectDirectory.resolve("build").resolve("classes");
    compileJavaSources(classesDirectory, javaSourcesUnder(sourceDirectory));

    CountDownLatch latch = new CountDownLatch(1);
    synchronized (GENERATED_PROGRAM_PROBE_LOCK) {
      generatedProgramMainArgs = null;
      generatedProgramMainLatch = latch;
    }
    try (GeneratedProjectClassLoader classLoader = new GeneratedProjectClassLoader(
        new URL[] {classesDirectory.toUri().toURL()})) {
      Class<?> launcherClass = Class.forName("AliceJavaFXLauncher", true, classLoader);
      String[] args = {"--project", "launcher-runtime.a3p"};

      launcherClass.getMethod("main", String[].class).invoke(null, (Object) args);

      assertTrue(
          "Stubbed JavaFX launch path should reach the generated Program.main probe",
          latch.await(5, TimeUnit.SECONDS));
      assertArrayEquals(args, generatedProgramMainArgs);
    } finally {
      synchronized (GENERATED_PROGRAM_PROBE_LOCK) {
        generatedProgramMainArgs = null;
        generatedProgramMainLatch = null;
      }
    }
  }

  @Test
  public void generatedTemplateProjectSourcesCompileWithAliceLibraryClasspath() throws Exception {
    File aliceProject = temporaryFolder.newFile("template-smoke.a3p");
    IoUtilities.writeProject(
        aliceProject,
        new Project(programType("Program"), Project.SceneCameraType.WindowCamera));
    Path projectDirectory = temporaryFolder.newFolder("template-project").toPath();
    extractProjectTemplate(projectDirectory);
    Path sourceDirectory = projectDirectory.resolve("src");
    Files.createDirectories(sourceDirectory);

    ProjectCodeGenerator.generateCode(aliceProject, sourceDirectory.toFile(), null, false);

    Properties properties = loadProperties(projectDirectory.resolve("nbproject").resolve("project.properties"));
    assertEquals("src", properties.getProperty("src.dir"));
    assertEquals("AliceJavaFXLauncher", properties.getProperty("main.class"));
    assertEquals("${libs.Alice3Library.classpath}", properties.getProperty("javac.classpath").trim());
    assertTemplateCompilerStructure(properties);
    assertTrue(Files.exists(projectDirectory.resolve("build.xml")));
    assertTrue(Files.exists(projectDirectory.resolve("nbproject").resolve("build-impl.xml")));

    Path classesDirectory = resolveBuildClassesDirectory(projectDirectory, properties);
    compileJavaSources(classesDirectory, Alice3LibraryClasspathTestSupport.aliceLibraryClasspath(), javaSourcesUnder(sourceDirectory));

    assertTrue(Files.exists(classesDirectory.resolve("Program.class")));
    assertTrue(Files.exists(classesDirectory.resolve("AliceJavaFXLauncher.class")));
  }

  private static NamedUserType programType(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    type.superType.setValue(JavaType.getInstance(SProgram.class));
    type.methods.add(mainMethod());
    return type;
  }

  private static NamedUserType programTypeWithMainProbe(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    type.superType.setValue(JavaType.getInstance(SProgram.class));
    type.methods.add(mainMethodWithProbe());
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

  private static UserMethod mainMethodWithProbe() {
    UserParameter argsParameter = new UserParameter("args", String[].class);
    JavaMethod recorder = AstUtilities.lookupMethod(
        ProjectCodeGeneratorStandaloneProjectTest.class,
        "recordGeneratedProgramMainArgs",
        String[].class);
    UserMethod mainMethod = new UserMethod(
        "main",
        Void.TYPE,
        new UserParameter[] {argsParameter},
        new BlockStatement(AstUtilities.createMethodInvocationStatement(
            new TypeExpression(recorder.getDeclaringType()),
            recorder,
            new ParameterAccess(argsParameter))));
    mainMethod.isStatic.setValue(true);
    mainMethod.isSignatureLocked.setValue(true);
    return mainMethod;
  }

  public static void recordGeneratedProgramMainArgs(String[] args) {
    synchronized (GENERATED_PROGRAM_PROBE_LOCK) {
      generatedProgramMainArgs = args;
      if (generatedProgramMainLatch != null) {
        generatedProgramMainLatch.countDown();
      }
    }
  }

  private static final Object GENERATED_PROGRAM_PROBE_LOCK = new Object();
  private static volatile String[] generatedProgramMainArgs;
  private static CountDownLatch generatedProgramMainLatch;

  private static void writeJavaFxStubs(Path sourceDirectory) throws Exception {
    writeJavaSource(
        sourceDirectory.resolve("javafx/application/Application.java"),
        """
        package javafx.application;

        public abstract class Application {
          public static volatile String[] launchedArgs;
          public static volatile boolean startInvoked;

          public abstract void start(javafx.stage.Stage stage) throws Exception;

          public static void launch(String[] args) {
            try {
              launchedArgs = args;
              String callerClassName = StackWalker
                  .getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                  .walk(frames -> frames.skip(1).findFirst().orElseThrow().getDeclaringClass().getName());
              Application application = (Application) Class
                  .forName(callerClassName)
                  .getDeclaredConstructor()
                  .newInstance();
              application.start(new javafx.stage.Stage());
              startInvoked = true;
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
  }

  private static void compileJavaSources(Path outputDirectory, Path... sources) throws Exception {
    compileJavaSources(outputDirectory, System.getProperty("java.class.path"), sources);
  }

  private static void compileJavaSources(Path outputDirectory, String classpath, Path... sources) throws Exception {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    assertNotNull("Tests must run on a JDK with the Java compiler available", compiler);
    Files.createDirectories(outputDirectory);
    StringWriter compilerOutput = new StringWriter();
    try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
      List<String> options = Arrays.asList(
          "-classpath",
          classpath,
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

  private static Path[] javaSourcesUnder(Path sourceDirectory) throws Exception {
    try (Stream<Path> paths = Files.walk(sourceDirectory)) {
      return paths
          .filter(path -> path.getFileName().toString().endsWith(".java"))
          .toArray(Path[]::new);
    }
  }

  private static void extractProjectTemplate(Path projectDirectory) throws Exception {
    Path archive = Path.of("target/classes/org/alice/netbeans/ProjectTemplate.zip");
    assertTrue("ProjectTemplate.zip must be built as a test resource", Files.exists(archive));
    try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(archive))) {
      ZipEntry entry;
      while ((entry = zipInputStream.getNextEntry()) != null) {
        Path entryPath = projectDirectory.resolve(entry.getName()).normalize();
        assertTrue(entry.getName(), entryPath.startsWith(projectDirectory));
        if (entry.isDirectory()) {
          Files.createDirectories(entryPath);
        } else {
          Files.createDirectories(entryPath.getParent());
          Files.copy(zipInputStream, entryPath);
        }
      }
    }
  }

  private static Properties loadProperties(Path propertiesPath) throws Exception {
    Properties properties = new Properties();
    try (java.io.Reader reader = Files.newBufferedReader(propertiesPath)) {
      properties.load(reader);
    }
    return properties;
  }

  private static void assertTemplateCompilerStructure(Properties properties) {
    assertEquals("build", properties.getProperty("build.dir"));
    assertEquals("${build.dir}/classes", properties.getProperty("build.classes.dir"));
    String runClasspath = properties.getProperty("run.classpath");
    assertTrue(runClasspath, runClasspath.contains("${build.classes.dir}"));
  }

  private static Path resolveBuildClassesDirectory(Path projectDirectory, Properties properties) {
    String buildClassesDirectory = properties.getProperty("build.classes.dir")
        .replace("${build.dir}", properties.getProperty("build.dir"));
    return projectDirectory.resolve(buildClassesDirectory);
  }

  private static void writeJavaSource(Path sourcePath, String source) throws Exception {
    Files.createDirectories(sourcePath.getParent());
    Files.writeString(sourcePath, source);
  }

  private static class GeneratedProjectClassLoader extends URLClassLoader {
    GeneratedProjectClassLoader(URL[] urls) {
      super(urls, ProjectCodeGeneratorStandaloneProjectTest.class.getClassLoader());
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
      if (name.startsWith("javafx.") || "Program".equals(name) || "AliceJavaFXLauncher".equals(name)) {
        synchronized (getClassLoadingLock(name)) {
          Class<?> loadedClass = findLoadedClass(name);
          if (loadedClass == null) {
            try {
              loadedClass = findClass(name);
            } catch (ClassNotFoundException e) {
              loadedClass = super.loadClass(name, false);
            }
          }
          if (resolve) {
            resolveClass(loadedClass);
          }
          return loadedClass;
        }
      }
      return super.loadClass(name, resolve);
    }
  }
}
