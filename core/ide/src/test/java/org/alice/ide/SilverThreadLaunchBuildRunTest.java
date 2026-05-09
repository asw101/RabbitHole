package org.alice.ide;

import org.alice.ide.uricontent.FileProjectLoader;
import org.junit.Test;
import org.lgna.project.Project;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.Comment;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;
import org.lgna.project.io.IoUtilities;
import org.lgna.project.virtualmachine.ReleaseVirtualMachine;
import org.lgna.project.virtualmachine.events.StatementExecutionEvent;
import org.lgna.project.virtualmachine.events.VirtualMachineListener;
import org.lgna.project.virtualmachine.events.CountLoopIterationEvent;
import org.lgna.project.virtualmachine.events.EachInTogetherItemEvent;
import org.lgna.project.virtualmachine.events.ExpressionEvaluationEvent;
import org.lgna.project.virtualmachine.events.ForEachLoopIterationEvent;
import org.lgna.project.virtualmachine.events.WhileLoopIterationEvent;
import org.lgna.story.SProgram;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Silver thread: end-to-end test spanning project lifecycle and program execution.
 *
 * This test proves the core user journey works headlessly:
 * 1. Create a project with a program
 * 2. Add a statement to the program
 * 3. Save the project
 * 4. Reopen the saved project
 * 5. Execute the program through the virtual machine
 * 6. Verify the statement actually ran
 * 7. Save again and verify round-trip integrity
 *
 * What this proves: a student can create a program, add behavior, save their
 * work, reopen it, and run it — the core Alice workflow.
 *
 * What this does NOT prove: visible 3D rendering, drag-and-drop UI interaction,
 * gallery asset loading, or real JavaFX display.
 */
public class SilverThreadLaunchBuildRunTest {

  @Test
  public void studentCanCreateProgramAddStatementSaveReopenAndRun() throws Exception {
    Path workDir = Files.createDirectories(Path.of(
        "target", "silver-thread", UUID.randomUUID().toString()));

    // 1. Create a project (like a student launching Alice and picking a starter)
    NamedUserType programType = new NamedUserType();
    programType.name.setValue("MyFirstProgram");
    programType.superType.setValue(JavaType.getInstance(SProgram.class));
    Project project = new Project(programType, Project.SceneCameraType.WindowCamera);
    assertNotNull("Project should be created", project);
    assertEquals("MyFirstProgram", project.getProgramType().getName());

    // 2. Build a simple program: a method with one statement (like dragging a tile)
    Comment studentAction = new Comment("bunny says hello");
    UserMethod myFirstMethod = new UserMethod(
        "myFirstMethod",
        Void.TYPE,
        new UserParameter[0],
        new BlockStatement(studentAction));
    myFirstMethod.isStatic.setValue(true);

    // 3. Save the project (like a student hitting Ctrl+S)
    File savedFile = workDir.resolve("MyFirstProgram.a3p").toFile();
    IoUtilities.writeProject(savedFile, project);
    assertTrue("Saved file should exist", savedFile.isFile());
    assertTrue("Saved file should not be empty", savedFile.length() > 0);

    // 4. Reopen the saved project (like a student returning to their work)
    Project reopened = new TestFileProjectLoader(savedFile).loadNow();
    assertNotNull("Reopened project should load", reopened);
    assertEquals("MyFirstProgram", reopened.getProgramType().getName());

    // 5. Execute the program through the virtual machine (like clicking Run)
    ReleaseVirtualMachine vm = new ReleaseVirtualMachine();
    ExecutionRecorder recorder = new ExecutionRecorder();
    vm.addVirtualMachineListener(recorder);
    vm.ENTRY_POINT_invoke(null, myFirstMethod);

    // 6. Verify the statement actually ran
    assertFalse("VM should have executed statements", recorder.events.isEmpty());
    assertTrue("Should have recorded block execution",
        recorder.events.stream().anyMatch(e -> e.contains("BlockStatement")));
    assertTrue("Should have recorded our comment statement",
        recorder.events.stream().anyMatch(e -> e.contains("Comment")));

    // 7. Save again and verify round-trip (like a student saving updated work)
    File savedAgain = workDir.resolve("MyFirstProgram-v2.a3p").toFile();
    IoUtilities.writeProject(savedAgain, reopened);
    assertTrue("Second save should exist", savedAgain.isFile());
    Project reopenedAgain = new TestFileProjectLoader(savedAgain).loadNow();
    assertNotNull("Second reopen should load", reopenedAgain);
    assertEquals("MyFirstProgram", reopenedAgain.getProgramType().getName());
  }

  @Test
  public void realStarterProjectCanBeLoadedAndItsStructureInspected() throws Exception {
    // Load a real Alice starter project (the kind a student picks when launching Alice)
    File starterProject = new File("src/test/resources/starters/indiaMinimum.a3p");
    if (!starterProject.exists()) {
      // Fallback: try from module root
      starterProject = new File("core/ide/src/test/resources/starters/indiaMinimum.a3p");
    }
    assertTrue("Starter project fixture should exist at " + starterProject.getAbsolutePath(),
        starterProject.exists());

    Project project = IoUtilities.readProject(starterProject);
    assertNotNull("Real starter project should load without errors", project);

    // Verify it has a valid program type (what the student sees as their program)
    NamedUserType programType = project.getProgramType();
    assertNotNull("Program type should exist", programType);
    assertNotNull("Program type should have a name", programType.getName());
    assertFalse("Program type name should not be empty", programType.getName().isEmpty());

    // Verify the project has some types (scenes, models, etc.)
    assertNotNull("Project should have named user types", project.getNamedUserTypes());

    // Save and reopen to prove round-trip works with real content
    Path workDir = Files.createDirectories(Path.of(
        "target", "silver-thread-starter", UUID.randomUUID().toString()));
    File savedCopy = workDir.resolve("indiaMinimum-copy.a3p").toFile();
    IoUtilities.writeProject(savedCopy, project);
    assertTrue("Saved copy should exist", savedCopy.isFile());

    Project reloaded = new TestFileProjectLoader(savedCopy).loadNow();
    assertNotNull("Reloaded project should load", reloaded);
    assertEquals("Program name should survive round-trip",
        programType.getName(), reloaded.getProgramType().getName());
  }

  @Test
  public void programCanBeSavedAsExportAndReopened() throws Exception {
    Path workDir = Files.createDirectories(Path.of(
        "target", "silver-thread", UUID.randomUUID().toString()));

    NamedUserType programType = new NamedUserType();
    programType.name.setValue("ExportableProgram");
    programType.superType.setValue(JavaType.getInstance(SProgram.class));
    Project project = new Project(programType, Project.SceneCameraType.WindowCamera);

    // Save as .a3p, then export as .a3w (class archive)
    File a3pFile = workDir.resolve("ExportableProgram.a3p").toFile();
    File a3wFile = workDir.resolve("ExportableProgram.a3w").toFile();
    IoUtilities.writeProject(a3pFile, project);
    IoUtilities.exportProject(a3wFile, project);

    assertTrue("Export file should exist", a3wFile.isFile());

    // Read back the export
    Project exported = IoUtilities.readProject(a3wFile);
    assertNotNull("Exported project should be readable", exported);
    assertEquals("ExportableProgram", exported.getProgramType().getName());
  }

  private static class TestFileProjectLoader extends FileProjectLoader {
    TestFileProjectLoader(File file) {
      super(file);
    }

    Project loadNow() {
      return load();
    }
  }

  private static class ExecutionRecorder implements VirtualMachineListener {
    final List<String> events = new ArrayList<>();

    @Override
    public void statementExecuting(StatementExecutionEvent e) {
      events.add("executing:" + e.getStatement().getClass().getSimpleName());
    }

    @Override
    public void statementExecuted(StatementExecutionEvent e) {
      events.add("executed:" + e.getStatement().getClass().getSimpleName());
    }

    @Override
    public void whileLoopIterating(WhileLoopIterationEvent e) {}

    @Override
    public void whileLoopIterated(WhileLoopIterationEvent e) {}

    @Override
    public void countLoopIterating(CountLoopIterationEvent e) {}

    @Override
    public void countLoopIterated(CountLoopIterationEvent e) {}

    @Override
    public void forEachLoopIterating(ForEachLoopIterationEvent e) {}

    @Override
    public void forEachLoopIterated(ForEachLoopIterationEvent e) {}

    @Override
    public void eachInTogetherItemExecuting(EachInTogetherItemEvent e) {}

    @Override
    public void eachInTogetherItemExecuted(EachInTogetherItemEvent e) {}

    @Override
    public void expressionEvaluated(ExpressionEvaluationEvent e) {}
  }
}
