package edu.cmu.cs.dennisc.ui;

import edu.cmu.cs.dennisc.pattern.Action;
import org.junit.Test;

import static org.junit.Assert.*;

public class UndoRedoManagerDeepTest {

  private static class TestManager extends UndoRedoManager {
    int changeCount = 0;

    @Override
    protected void handleChange() {
      changeCount++;
    }
  }

  private static class TestAction implements Action {
    int runCount = 0;
    int undoCount = 0;
    int redoCount = 0;

    @Override
    public void run() {
      runCount++;
    }

    @Override
    public void undo() {
      undoCount++;
    }

    @Override
    public void redo() {
      redoCount++;
    }
  }

  @Test
  public void newManager_undoStackEmpty() {
    TestManager manager = new TestManager();
    assertTrue(manager.isUndoStackEmpty());
  }

  @Test
  public void newManager_redoStackEmpty() {
    TestManager manager = new TestManager();
    assertTrue(manager.isRedoStackEmpty());
  }

  @Test
  public void runAndPush_runsAction() {
    TestManager manager = new TestManager();
    TestAction action = new TestAction();
    manager.runAndPush(action);
    assertEquals(1, action.runCount);
    assertFalse(manager.isUndoStackEmpty());
  }

  @Test
  public void pushAlreadyRun_doesNotRunAction() {
    TestManager manager = new TestManager();
    TestAction action = new TestAction();
    manager.pushAlreadyRunActionOntoUndoStack(action);
    assertEquals(0, action.runCount);
    assertFalse(manager.isUndoStackEmpty());
  }

  @Test
  public void undo_movesToRedoStack() {
    TestManager manager = new TestManager();
    TestAction action = new TestAction();
    manager.pushAlreadyRunActionOntoUndoStack(action);
    manager.undo();
    assertTrue(manager.isUndoStackEmpty());
    assertFalse(manager.isRedoStackEmpty());
    assertEquals(1, action.undoCount);
  }

  @Test
  public void redo_movesToUndoStack() {
    TestManager manager = new TestManager();
    TestAction action = new TestAction();
    manager.pushAlreadyRunActionOntoUndoStack(action);
    manager.undo();
    manager.redo();
    assertFalse(manager.isUndoStackEmpty());
    assertTrue(manager.isRedoStackEmpty());
    assertEquals(1, action.redoCount);
  }

  @Test
  public void pushClearsRedoStack() {
    TestManager manager = new TestManager();
    TestAction a1 = new TestAction();
    TestAction a2 = new TestAction();
    manager.pushAlreadyRunActionOntoUndoStack(a1);
    manager.undo();
    assertFalse(manager.isRedoStackEmpty());
    manager.pushAlreadyRunActionOntoUndoStack(a2);
    assertTrue(manager.isRedoStackEmpty());
  }

  @Test
  public void handleChange_calledOnEachOperation() {
    TestManager manager = new TestManager();
    TestAction action = new TestAction();
    manager.pushAlreadyRunActionOntoUndoStack(action);
    assertEquals(1, manager.changeCount);
    manager.undo();
    assertEquals(2, manager.changeCount);
    manager.redo();
    assertEquals(3, manager.changeCount);
  }

  @Test
  public void multipleUndos() {
    TestManager manager = new TestManager();
    manager.pushAlreadyRunActionOntoUndoStack(new TestAction());
    manager.pushAlreadyRunActionOntoUndoStack(new TestAction());
    manager.pushAlreadyRunActionOntoUndoStack(new TestAction());
    manager.undo();
    manager.undo();
    manager.undo();
    assertTrue(manager.isUndoStackEmpty());
  }
}
