package org.alice.interact.condition;

import org.alice.interact.InputState;
import org.alice.interact.handle.HandleSet;
import org.alice.interact.manipulator.AbstractManipulator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ManipulatorConditionSetDeepTest {
  private static final class StubCondition extends InputCondition {
    boolean running;
    boolean started;
    boolean ended;
    boolean changed;
    boolean clicked;
    int updateCount;

    @Override public boolean isRunning(InputState currentState, InputState previousState) { return running; }
    @Override public boolean justEnded(InputState currentState, InputState previousState) { return ended; }
    @Override public boolean justStarted(InputState currentState, InputState previousState) { return started; }
    @Override public boolean stateChanged(InputState currentState, InputState previousState) { return changed; }
    @Override public boolean clicked(InputState currentState, InputState previousState) { return clicked; }
    @Override protected void update(InputState currentState, InputState previousState) { updateCount++; }
    @Override protected boolean testState(InputState state) { return running; }
  }

  private static final class TestManipulator extends AbstractManipulator {
    @Override protected HandleSet getHandleSetToEnable() { return null; }
    @Override public String getUndoRedoDescription() { return "test"; }
    @Override public boolean doStartManipulator(InputState startInput) { return false; }
    @Override public void doDataUpdateManipulator(InputState currentInput, InputState previousInput) {}
    @Override public void doTimeUpdateManipulator(double dTime, InputState currentInput) {}
    @Override public void doEndManipulator(InputState endInput, InputState previousInput) {}
    @Override public void doClickManipulator(InputState endInput, InputState previousInput) {}
  }

  private final InputState current = new InputState();
  private final InputState previous = new InputState();

  @Test
  public void updateInvokesAllChildConditions() {
    ManipulatorConditionSet set = new ManipulatorConditionSet(new TestManipulator(), "deep");
    StubCondition first = new StubCondition();
    StubCondition second = new StubCondition();
    set.addCondition(first);
    set.addCondition(second);

    set.update(current, previous);

    assertEquals(1, first.updateCount);
    assertEquals(1, second.updateCount);
  }

  @Test
  public void stateChangedReturnsTrueWhenAnyConditionChanges() {
    ManipulatorConditionSet set = new ManipulatorConditionSet(new TestManipulator(), "deep");
    StubCondition condition = new StubCondition();
    condition.changed = true;
    set.addCondition(condition);

    assertTrue(set.stateChanged(current, previous));
  }

  @Test
  public void shouldContinueReturnsTrueWhenAnyConditionIsRunning() {
    ManipulatorConditionSet set = new ManipulatorConditionSet(new TestManipulator(), "deep");
    StubCondition condition = new StubCondition();
    condition.running = true;
    set.addCondition(condition);

    assertTrue(set.shouldContinue(current, previous));
  }

  @Test
  public void justStartedRequiresStartWithoutExistingRunningCondition() {
    ManipulatorConditionSet set = new ManipulatorConditionSet(new TestManipulator(), "deep");
    StubCondition condition = new StubCondition();
    condition.started = true;
    set.addCondition(condition);

    assertTrue(set.justStarted(current, previous));
  }

  @Test
  public void justStartedIsFalseWhenAConditionIsAlreadyRunning() {
    ManipulatorConditionSet set = new ManipulatorConditionSet(new TestManipulator(), "deep");
    StubCondition condition = new StubCondition();
    condition.started = true;
    condition.running = true;
    set.addCondition(condition);

    assertFalse(set.justStarted(current, previous));
  }

  @Test
  public void justEndedRequiresEndedWithoutRunningOrStartingConditions() {
    ManipulatorConditionSet set = new ManipulatorConditionSet(new TestManipulator(), "deep");
    StubCondition condition = new StubCondition();
    condition.ended = true;
    set.addCondition(condition);

    assertTrue(set.justEnded(current, previous));
  }

  @Test
  public void justEndedIsFalseWhenAnotherConditionJustStarted() {
    ManipulatorConditionSet set = new ManipulatorConditionSet(new TestManipulator(), "deep");
    StubCondition ending = new StubCondition();
    ending.ended = true;
    StubCondition starting = new StubCondition();
    starting.started = true;
    set.addCondition(ending);
    set.addCondition(starting);

    assertFalse(set.justEnded(current, previous));
  }

  @Test
  public void clickedReturnsTrueWhenAnyConditionReportsClick() {
    ManipulatorConditionSet set = new ManipulatorConditionSet(new TestManipulator(), "deep");
    StubCondition condition = new StubCondition();
    condition.clicked = true;
    set.addCondition(condition);

    assertTrue(set.clicked(current, previous));
  }

  @Test
  public void enabledNameAndManipulatorRoundTrip() {
    TestManipulator manipulator = new TestManipulator();
    ManipulatorConditionSet set = new ManipulatorConditionSet(manipulator, "deep");
    set.setEnabled(false);

    assertFalse(set.isEnabled());
    assertEquals("deep", set.getName());
    assertSame(manipulator, set.getManipulator());
  }
}
