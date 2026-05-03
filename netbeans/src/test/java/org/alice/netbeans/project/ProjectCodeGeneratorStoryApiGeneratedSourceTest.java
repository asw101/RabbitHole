package org.alice.netbeans.project;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.lgna.project.Project;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.DoubleLiteral;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;
import org.lgna.project.io.IoUtilities;
import org.lgna.story.SScene;
import org.lgna.story.SProgram;

import java.io.File;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import static org.junit.Assert.*;

public class ProjectCodeGeneratorStoryApiGeneratedSourceTest {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void generatedSyntheticStoryApiCallSourceCompiles() throws Exception {
    Path sourceDirectory = generateProgramSource(
        "synthetic-story-api-call.a3p",
        programTypeWithStoryApiCall(),
        "generated-story-api-call-src");

    Path programPath = sourceDirectory.resolve("Program.java");
    String programSource = Files.readString(programPath);
    assertTrue(programSource.contains("void configureStory()"));
    assertTrue(programSource, programSource.contains("this.setSimulationSpeedFactor(1.5);"));
    compileProgramAndLauncher("generated-story-api-call-classes", programPath, sourceDirectory);
  }

  @Test
  public void generatedSyntheticSceneActivationCallSourceCompiles() throws Exception {
    Path sourceDirectory = generateProgramSource(
        "synthetic-scene-activation-call.a3p",
        programTypeWithSceneActivationCall(),
        "generated-scene-activation-call-src");

    Path programPath = sourceDirectory.resolve("Program.java");
    String programSource = Files.readString(programPath);
    assertTrue(programSource.contains("void clearScene()"));
    assertTrue(programSource, programSource.contains("this.setActiveScene(null);"));
    compileProgramAndLauncher("generated-scene-activation-call-classes", programPath, sourceDirectory);
  }

  private Path generateProgramSource(String projectFileName, NamedUserType programType, String sourceDirectoryName)
      throws Exception {
    File aliceProject = temporaryFolder.newFile(projectFileName);
    IoUtilities.writeProject(aliceProject, new Project(programType, Project.SceneCameraType.WindowCamera));
    File sourceDirectory = temporaryFolder.newFolder(sourceDirectoryName);
    ProjectCodeGenerator.generateCode(aliceProject, sourceDirectory, null, false);
    return sourceDirectory.toPath();
  }

  private void compileProgramAndLauncher(String classesDirectoryName, Path programPath, Path sourceDirectory)
      throws Exception {
    compileJavaSources(
        temporaryFolder.newFolder(classesDirectoryName).toPath(),
        programPath,
        sourceDirectory.resolve("AliceJavaFXLauncher.java"));
  }

  private static NamedUserType programType(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    type.superType.setValue(JavaType.getInstance(SProgram.class));
    type.methods.add(mainMethod());
    return type;
  }

  private static NamedUserType programTypeWithStoryApiCall() {
    NamedUserType type = programType("Program");
    JavaMethod setSimulationSpeedFactor =
        AstUtilities.lookupMethod(SProgram.class, "setSimulationSpeedFactor", Number.class);
    UserMethod configureStory = new UserMethod(
        "configureStory",
        Void.TYPE,
        new UserParameter[0],
        new BlockStatement(AstUtilities.createMethodInvocationStatement(
            new ThisExpression(),
            setSimulationSpeedFactor,
            new DoubleLiteral(1.5))));
    type.methods.add(configureStory);
    return type;
  }

  private static NamedUserType programTypeWithSceneActivationCall() {
    NamedUserType type = programType("Program");
    JavaMethod setActiveScene = AstUtilities.lookupMethod(SProgram.class, "setActiveScene", SScene.class);
    UserMethod clearScene = new UserMethod(
        "clearScene",
        Void.TYPE,
        new UserParameter[0],
        new BlockStatement(AstUtilities.createMethodInvocationStatement(
            new ThisExpression(),
            setActiveScene,
            new NullLiteral())));
    type.methods.add(clearScene);
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
}
