package org.alice.netbeans.project;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.lgna.project.Project;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.BooleanExpressionBodyPair;
import org.lgna.project.ast.BooleanLiteral;
import org.lgna.project.ast.Comment;
import org.lgna.project.ast.ConditionalStatement;
import org.lgna.project.ast.CountLoop;
import org.lgna.project.ast.ForEachInArrayLoop;
import org.lgna.project.ast.ForEachInIterableLoop;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.LocalAccess;
import org.lgna.project.ast.LocalDeclarationStatement;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.ParameterAccess;
import org.lgna.project.ast.SimpleArgument;
import org.lgna.project.ast.StringLiteral;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.TypeExpression;
import org.lgna.project.ast.UserLocal;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;
import org.lgna.project.ast.WhileLoop;
import org.lgna.project.io.IoUtilities;
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

public class ProjectCodeGeneratorGeneratedSourceTest {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void generatedSyntheticUserMethodSourceCompiles() throws Exception {
    Path sourceDirectory = generateProgramSource("synthetic-method.a3p", programTypeWithUserMethod(), "generated-method-src");

    Path programPath = sourceDirectory.resolve("Program.java");
    String programSource = Files.readString(programPath);
    assertTrue(programSource.contains("void sayHello()"));
    assertTrue(programSource.contains("hello alice"));
    compileProgramAndLauncher("generated-method-classes", programPath, sourceDirectory);
  }

  @Test
  public void generatedSyntheticUserMethodLocalDeclarationSourceCompiles() throws Exception {
    Path sourceDirectory = generateProgramSource(
        "synthetic-local-declaration.a3p",
        programTypeWithLocalDeclarationMethod(),
        "generated-local-declaration-src");

    Path programPath = sourceDirectory.resolve("Program.java");
    String programSource = Files.readString(programPath);
    assertTrue(programSource.contains("void sayHello()"));
    assertTrue(programSource, programSource.contains("final String greeting=\"hello alice\";"));
    compileProgramAndLauncher("generated-local-declaration-classes", programPath, sourceDirectory);
  }

  @Test
  public void generatedSyntheticUserMethodParameterSourceCompiles() throws Exception {
    Path sourceDirectory = generateProgramSource(
        "synthetic-parameter.a3p",
        programTypeWithParameterMethod(),
        "generated-parameter-src");

    Path programPath = sourceDirectory.resolve("Program.java");
    String programSource = Files.readString(programPath);
    assertTrue(programSource.contains("void remember(String message)"));
    assertTrue(programSource, programSource.contains("final String copy=message;"));
    compileProgramAndLauncher("generated-parameter-classes", programPath, sourceDirectory);
  }

  @Test
  public void generatedSyntheticUserMethodInvocationSourceCompiles() throws Exception {
    Path sourceDirectory = generateProgramSource(
        "synthetic-method-invocation.a3p",
        programTypeWithMethodInvocation(),
        "generated-method-invocation-src");

    Path programPath = sourceDirectory.resolve("Program.java");
    String programSource = Files.readString(programPath);
    assertTrue(programSource.contains("void sayHello()"));
    assertTrue(programSource.contains("void callSayHello()"));
    assertTrue(programSource, programSource.contains("this.sayHello();"));
    compileProgramAndLauncher("generated-method-invocation-classes", programPath, sourceDirectory);
  }

  @Test
  public void generatedSyntheticUserMethodInvocationWithArgumentSourceCompiles() throws Exception {
    Path sourceDirectory = generateProgramSource(
        "synthetic-method-invocation-argument.a3p",
        programTypeWithMethodInvocationArgument(),
        "generated-method-invocation-argument-src");

    Path programPath = sourceDirectory.resolve("Program.java");
    String programSource = Files.readString(programPath);
    assertTrue(programSource.contains("void remember(String message)"));
    assertTrue(programSource.contains("void callRemember()"));
    assertTrue(programSource, programSource.contains("this.remember(\"hello alice\");"));
    compileProgramAndLauncher("generated-method-invocation-argument-classes", programPath, sourceDirectory);
  }

  @Test
  public void generatedSyntheticUserMethodConditionalSourceCompiles() throws Exception {
    Path sourceDirectory = generateProgramSource(
        "synthetic-conditional.a3p",
        programTypeWithConditionalMethod(),
        "generated-conditional-src");

    Path programPath = sourceDirectory.resolve("Program.java");
    String programSource = Files.readString(programPath);
    assertTrue(programSource.contains("void choose()"));
    assertTrue(programSource, programSource.contains("if(true)"));
    assertTrue(programSource, programSource.contains(" else"));
    compileProgramAndLauncher("generated-conditional-classes", programPath, sourceDirectory);
  }

  @Test
  public void generatedSyntheticUserMethodCountLoopSourceCompiles() throws Exception {
    Path sourceDirectory = generateProgramSource(
        "synthetic-count-loop.a3p",
        programTypeWithCountLoopMethod(),
        "generated-count-loop-src");

    Path programPath = sourceDirectory.resolve("Program.java");
    String programSource = Files.readString(programPath);
    assertTrue(programSource.contains("void repeat()"));
    assertTrue(programSource, programSource.contains("for(Integer indexA=0;indexA<3;indexA++)"));
    compileProgramAndLauncher("generated-count-loop-classes", programPath, sourceDirectory);
  }

  @Test
  public void generatedSyntheticUserMethodWhileLoopSourceCompiles() throws Exception {
    Path sourceDirectory = generateProgramSource(
        "synthetic-while-loop.a3p",
        programTypeWithWhileLoopMethod(),
        "generated-while-loop-src");

    Path programPath = sourceDirectory.resolve("Program.java");
    String programSource = Files.readString(programPath);
    assertTrue(programSource.contains("void spin()"));
    assertTrue(programSource, programSource.contains("while (true)"));
    compileProgramAndLauncher("generated-while-loop-classes", programPath, sourceDirectory);
  }

  @Test
  public void generatedSyntheticUserMethodForEachLoopSourceCompiles() throws Exception {
    Path sourceDirectory = generateProgramSource(
        "synthetic-for-each-loop.a3p",
        programTypeWithForEachLoopMethod(),
        "generated-for-each-loop-src");

    Path programPath = sourceDirectory.resolve("Program.java");
    String programSource = Files.readString(programPath);
    assertTrue(programSource.contains("void visitAll()"));
    assertTrue(programSource, programSource.contains("for(String COUNT__ : new String[]{\"red\", \"blue\"})"));
    compileProgramAndLauncher("generated-for-each-loop-classes", programPath, sourceDirectory);
  }

  @Test
  public void generatedSyntheticForEachLoopItemAccessSourceCompiles() throws Exception {
    Path sourceDirectory = generateProgramSource(
        "synthetic-for-each-loop-item-access.a3p",
        programTypeWithForEachLoopItemAccessMethod(),
        "generated-for-each-loop-item-access-src");

    Path programPath = sourceDirectory.resolve("Program.java");
    String programSource = Files.readString(programPath);
    assertTrue(programSource.contains("void copyEach()"));
    assertTrue(programSource, programSource.contains("for(String COUNT__ : new String[]{\"red\", \"blue\"})"));
    assertTrue(programSource, programSource.contains("final String copy=COUNT__;"));
    compileProgramAndLauncher("generated-for-each-loop-item-access-classes", programPath, sourceDirectory);
  }

  @Test
  public void generatedSyntheticNamedForEachLoopItemAccessSourceCompiles() throws Exception {
    Path sourceDirectory = generateProgramSource(
        "synthetic-named-for-each-loop-item-access.a3p",
        programTypeWithNamedForEachLoopItemAccessMethod(),
        "generated-named-for-each-loop-item-access-src");

    Path programPath = sourceDirectory.resolve("Program.java");
    String programSource = Files.readString(programPath);
    assertTrue(programSource.contains("void copyNamedItem()"));
    assertTrue(programSource, programSource.contains("for(String item : new String[]{\"red\", \"blue\"})"));
    assertTrue(programSource, programSource.contains("final String copy=item;"));
    compileProgramAndLauncher("generated-named-for-each-loop-item-access-classes", programPath, sourceDirectory);
  }

  @Test
  public void generatedSyntheticForEachIterableSourceCompiles() throws Exception {
    Path sourceDirectory = generateProgramSource(
        "synthetic-for-each-iterable.a3p",
        programTypeWithForEachIterableMethod(),
        "generated-for-each-iterable-src");

    Path programPath = sourceDirectory.resolve("Program.java");
    String programSource = Files.readString(programPath);
    assertTrue(programSource.contains("void visitIterable()"));
    assertTrue(programSource, programSource.contains("for(String item : Arrays.asList(\"red\",\"blue\"))"));
    assertTrue(programSource, programSource.contains("final String copy=item;"));
    compileProgramAndLauncher("generated-for-each-iterable-classes", programPath, sourceDirectory);
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

  private static NamedUserType programTypeWithForEachLoopMethod() {
    NamedUserType type = programType("Program");
    ForEachInArrayLoop loop = AstUtilities.createForEachInArrayLoop(AstUtilities.createArrayInstanceCreation(
        String[].class,
        new StringLiteral("red"),
        new StringLiteral("blue")));
    loop.body.getValue().statements.add(new Comment("loop body"));
    UserMethod visitAll = new UserMethod(
        "visitAll",
        Void.TYPE,
        new UserParameter[0],
        new BlockStatement(loop));
    type.methods.add(visitAll);
    return type;
  }

  private static NamedUserType programTypeWithForEachLoopItemAccessMethod() {
    NamedUserType type = programType("Program");
    ForEachInArrayLoop loop = AstUtilities.createForEachInArrayLoop(AstUtilities.createArrayInstanceCreation(
        String[].class,
        new StringLiteral("red"),
        new StringLiteral("blue")));
    UserLocal copy = new UserLocal("copy", String.class, true);
    loop.body.getValue().statements.add(new LocalDeclarationStatement(copy, new LocalAccess(loop.item.getValue())));
    UserMethod copyEach = new UserMethod(
        "copyEach",
        Void.TYPE,
        new UserParameter[0],
        new BlockStatement(loop));
    type.methods.add(copyEach);
    return type;
  }

  private static NamedUserType programTypeWithNamedForEachLoopItemAccessMethod() {
    NamedUserType type = programType("Program");
    ForEachInArrayLoop loop = new ForEachInArrayLoop(
        new UserLocal("item", String.class, true),
        AstUtilities.createArrayInstanceCreation(
            String[].class,
            new StringLiteral("red"),
            new StringLiteral("blue")),
        new BlockStatement());
    UserLocal copy = new UserLocal("copy", String.class, true);
    loop.body.getValue().statements.add(new LocalDeclarationStatement(copy, new LocalAccess(loop.item.getValue())));
    UserMethod copyNamedItem = new UserMethod(
        "copyNamedItem",
        Void.TYPE,
        new UserParameter[0],
        new BlockStatement(loop));
    type.methods.add(copyNamedItem);
    return type;
  }

  private static NamedUserType programTypeWithForEachIterableMethod() {
    NamedUserType type = programType("Program");
    JavaMethod asList = AstUtilities.lookupMethod(Arrays.class, "asList", Object[].class);
    MethodInvocation iterable = new MethodInvocation(
        new TypeExpression(asList.getDeclaringType()),
        asList,
        new SimpleArgument[0],
        new SimpleArgument[] {
            new SimpleArgument(asList.getVariableLengthParameter(), new StringLiteral("red")),
            new SimpleArgument(asList.getVariableLengthParameter(), new StringLiteral("blue"))
        },
        null);
    ForEachInIterableLoop loop = new ForEachInIterableLoop(
        new UserLocal("item", String.class, true),
        iterable,
        new BlockStatement());
    UserLocal copy = new UserLocal("copy", String.class, true);
    loop.body.getValue().statements.add(new LocalDeclarationStatement(copy, new LocalAccess(loop.item.getValue())));
    UserMethod visitIterable = new UserMethod(
        "visitIterable",
        Void.TYPE,
        new UserParameter[0],
        new BlockStatement(loop));
    type.methods.add(visitIterable);
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
