package org.alice.ide.sceneeditor;

import org.alice.ide.ReasonToDisableSomeAmountOfRendering;
import org.junit.Test;
import org.lgna.project.Project;
import org.lgna.project.ast.*;
import org.lgna.project.virtualmachine.UserInstance;

import javax.swing.SwingUtilities;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class AbstractSceneEditorTemplateMethodTest {
  @Test
  public void initializeIfNecessaryInvokesTemplateHooksOnlyOnce() throws Exception {
    RecordingSceneEditor editor = new RecordingSceneEditor();

    invokeInitializeIfNecessary(editor);
    invokeInitializeIfNecessary(editor);

    assertEquals(1, editor.initializeComponentsCount);
    assertEquals(1, editor.initializeObserversCount);
  }

  @Test
  public void setSelectedFieldStoresSelection() {
    RecordingSceneEditor editor = new RecordingSceneEditor();
    UserField field = new UserField("camera", String.class);
    NamedUserType declaringType = new NamedUserType("Scene", null, Object.class, new NamedUserConstructor[0], new UserMethod[0], new UserField[] {field});

    editor.setSelectedField(declaringType, field);

    assertSame(field, editor.getSelectedField());
  }

  @Test
  public void handleProjectOpenedAcceptsNullAndClearsProgramType() throws Exception {
    RecordingSceneEditor editor = new RecordingSceneEditor();
    NamedUserType programType = new NamedUserType("Program", null, Object.class, new NamedUserConstructor[0], new UserMethod[0], new UserField[0]);
    Project project = new Project(programType, Project.SceneCameraType.WindowCamera);

    runOnEdt(() -> {
      editor.openProject(project);
      return null;
    });
    assertSame(programType, getPrivateField(editor, "programType"));

    runOnEdt(() -> {
      editor.openProject(null);
      return null;
    });
    assertNull(getPrivateField(editor, "programType"));
    assertNull(editor.getUserProgramInstance());
  }

  private static void invokeInitializeIfNecessary(AbstractSceneEditor editor) throws Exception {
    java.lang.reflect.Method initializeIfNecessary = AbstractSceneEditor.class.getDeclaredMethod("initializeIfNecessary");
    initializeIfNecessary.setAccessible(true);
    initializeIfNecessary.invoke(editor);
  }

  private static <T> T runOnEdt(Callable<T> callable) throws Exception {
    AtomicReference<T> result = new AtomicReference<>();
    AtomicReference<Throwable> failure = new AtomicReference<>();
    SwingUtilities.invokeAndWait(() -> {
      try {
        result.set(callable.call());
      } catch (Throwable throwable) {
        failure.set(throwable);
      }
    });
    if (failure.get() != null) {
      if (failure.get() instanceof Exception exception) {
        throw exception;
      }
      throw new RuntimeException(failure.get());
    }
    return result.get();
  }

  private static Object getPrivateField(Object instance, String name) throws Exception {
    Field field = AbstractSceneEditor.class.getDeclaredField(name);
    field.setAccessible(true);
    return field.get(instance);
  }

  private static final class RecordingSceneEditor extends AbstractSceneEditor {
    private int initializeComponentsCount;
    private int initializeObserversCount;

    private void openProject(Project project) {
      this.handleProjectOpened(project);
    }

    @Override
    public void disableRendering(ReasonToDisableSomeAmountOfRendering reasonToDisableSomeAmountOfRendering) {
    }

    @Override
    public void enableRendering(ReasonToDisableSomeAmountOfRendering reasonToDisableSomeAmountOfRendering) {
    }

    @Override
    public void generateCodeForSetUp(StatementListProperty bodyStatementsProperty) {
    }

    @Override
    public Statement[] getDoStatementsForAddField(UserField field, org.alice.math.immutable.AffineMatrix4x4 initialTransform) {
      return new Statement[0];
    }

    @Override
    public Statement[] getDoStatementsForCopyField(UserField fieldToCopy, UserField newField, org.alice.math.immutable.AffineMatrix4x4 initialTransform) {
      return new Statement[0];
    }

    @Override
    public Statement[] getUndoStatementsForAddField(UserField field) {
      return new Statement[0];
    }

    @Override
    public Statement[] getDoStatementsForRemoveField(UserField field, Map<AbstractField, Statement> riders) {
      return new Statement[0];
    }

    @Override
    public Statement[] getUndoStatementsForRemoveField(UserField field, Map<AbstractField, Statement> riders) {
      return new Statement[0];
    }

    @Override
    public void preScreenCapture() {
    }

    @Override
    public void postScreenCapture() {
    }

    @Override
    protected void handleExpandContractChange(boolean isExpanded) {
    }

    @Override
    protected void initializeComponents() {
      this.initializeComponentsCount++;
    }

    @Override
    protected void initializeObservers() {
      this.initializeObserversCount++;
    }

    @Override
    protected UserInstance createProgramInstance() {
      return null;
    }

    @Override
    public Statement getCurrentStateCodeForField(UserField field) {
      return null;
    }
  }
}
