package org.alice.ide.operations;

import org.alice.ide.croquet.models.history.RedoOperation;
import org.alice.ide.croquet.models.history.UndoOperation;
import org.alice.ide.croquet.models.projecturi.OpenProjectOperation;
import org.alice.ide.croquet.models.projecturi.PotentialClearanceUriCreatorIteratingOperation;
import org.alice.ide.croquet.models.projecturi.SaveAsProjectOperation;
import org.alice.ide.croquet.models.projecturi.SaveProjectOperation;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.lgna.croquet.Application;
import org.lgna.croquet.Group;
import org.lgna.croquet.edits.Edit;
import org.lgna.croquet.undo.UndoHistory;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class UserFacingOperationsBehaviorTest {
  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void saveOperationsRespectPromptValidationRules() throws Exception {
    File writableProject = temporaryFolder.newFile("world.a3p");
    File missingProject = new File(temporaryFolder.getRoot(), "missing.a3p");

    Method savePromptRule = SaveProjectOperation.class.getDeclaredMethod("isPromptNecessary", File.class);
    savePromptRule.setAccessible(true);
    Method saveAsPromptRule = SaveAsProjectOperation.class.getDeclaredMethod("isPromptNecessary", File.class);
    saveAsPromptRule.setAccessible(true);

    assertTrue((Boolean) savePromptRule.invoke(SaveProjectOperation.getInstance(), new Object[]{null}));
    assertFalse((Boolean) savePromptRule.invoke(SaveProjectOperation.getInstance(), writableProject));
    assertTrue((Boolean) savePromptRule.invoke(SaveProjectOperation.getInstance(), missingProject));

    assertTrue((Boolean) saveAsPromptRule.invoke(SaveAsProjectOperation.getInstance(), new Object[]{null}));
    assertTrue((Boolean) saveAsPromptRule.invoke(SaveAsProjectOperation.getInstance(), writableProject));
    assertTrue((Boolean) saveAsPromptRule.invoke(SaveAsProjectOperation.getInstance(), missingProject));
  }

  @Test
  public void openOperationExposesExistingProjectWorkflowEntryPoint() throws Exception {
    Constructor<OpenProjectOperation> constructor = OpenProjectOperation.class.getDeclaredConstructor();

    assertTrue(Modifier.isPublic(constructor.getModifiers()));
    assertSame(PotentialClearanceUriCreatorIteratingOperation.class, OpenProjectOperation.class.getSuperclass());
  }

  @Test
  public void undoAndRedoIgnoreEmptyHistoryAndMoveWhenEditsExist() throws Exception {
    UndoOperation undoOperation = new UndoOperation(null);
    RedoOperation redoOperation = new RedoOperation(null);
    Method performUndo = UndoOperation.class.getDeclaredMethod("performInternal", UndoHistory.class);
    Method performRedo = RedoOperation.class.getDeclaredMethod("performInternal", UndoHistory.class);
    performUndo.setAccessible(true);
    performRedo.setAccessible(true);

    UndoHistory history = new UndoHistory(Application.PROJECT_GROUP);
    performUndo.invoke(undoOperation, history);
    performRedo.invoke(redoOperation, history);
    assertEquals(0, history.getInsertionIndex());

    CountingEdit edit = new CountingEdit(Application.PROJECT_GROUP, "rename field");
    history.push(edit);
    assertEquals(1, history.getInsertionIndex());

    performUndo.invoke(undoOperation, history);
    assertEquals(0, history.getInsertionIndex());
    assertEquals(1, edit.undoCount);

    performRedo.invoke(redoOperation, history);
    assertEquals(1, history.getInsertionIndex());
    assertEquals(1, edit.redoCount);

    performRedo.invoke(redoOperation, history);
    assertEquals(1, history.getInsertionIndex());
    assertEquals(1, edit.redoCount);
    assertTrue(redoOperation.isToolBarTextClobbered());
  }

  private static final class CountingEdit implements Edit {
    private final Group group;
    private final String description;
    private int undoCount;
    private int redoCount;

    private CountingEdit(Group group, String description) {
      this.group = group;
      this.description = description;
    }

    @Override
    public Group getGroup() {
      return this.group;
    }

    @Override
    public boolean canUndo() {
      return true;
    }

    @Override
    public boolean canRedo() {
      return true;
    }

    @Override
    public void doOrRedo(boolean isDo) {
      if (!isDo) {
        this.redoCount++;
      }
    }

    @Override
    public void undo() {
      this.undoCount++;
    }

    @Override
    public String getRedoPresentation() {
      return this.description;
    }

    @Override
    public String getUndoPresentation() {
      return this.description;
    }

    @Override
    public String getTerseDescription() {
      return this.description;
    }

    @Override
    public String getDetailedDescription() {
      return this.description;
    }

    @Override
    public String getLogDescription() {
      return this.description;
    }
  }
}
