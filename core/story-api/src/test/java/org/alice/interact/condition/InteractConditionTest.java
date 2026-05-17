package org.alice.interact.condition;

import org.alice.interact.InputState;
import org.alice.interact.PickHint;
import org.junit.Test;

import static org.junit.Assert.*;

public class InteractConditionTest {

  // ══════════════════════════════════════════════════════════════════════════
  //  PickCondition
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void pickConditionConstructionWithEmptyHint() {
    PickCondition pc = new PickCondition(new PickHint());
    assertNotNull(pc);
  }

  @Test
  public void pickConditionConstructionWithSpecificHint() {
    PickCondition pc = new PickCondition(new PickHint(PickHint.PickType.VIEWABLE));
    assertNotNull(pc);
  }

  @Test
  public void pickConditionSetNextCondition() {
    PickCondition first = new PickCondition(new PickHint(PickHint.PickType.VIEWABLE));
    PickCondition second = new PickCondition(new PickHint(PickHint.PickType.SELECTABLE));
    first.setNextCondition(second);
    assertSame(second, first.nextCondition);
  }

  @Test
  public void pickConditionChainedConstructor() {
    PickCondition first = new PickCondition(new PickHint(PickHint.PickType.VIEWABLE));
    PickCondition second = new PickCondition(new PickHint(PickHint.PickType.SELECTABLE), first);
    // second should be wired as first's nextCondition
    assertSame(second, first.nextCondition);
  }

  @Test
  public void pickConditionEvaluateObjectWithEmptyState() {
    PickCondition pc = new PickCondition(new PickHint(PickHint.PickType.NOTHING));
    InputState state = new InputState();
    // Fresh state has NOTHING pick hint, so NOTHING.intersects(NOTHING) → true
    assertTrue(pc.evaluateObject(state));
  }

  @Test
  public void pickConditionEvaluateObjectMismatch() {
    PickCondition pc = new PickCondition(new PickHint(PickHint.PickType.VIEWABLE));
    InputState state = new InputState();
    // Fresh state has NOTHING pick hint, VIEWABLE doesn't intersect NOTHING
    assertFalse(pc.evaluateObject(state));
  }

  @Test
  public void pickConditionEvaluateChainSingleNode() {
    PickCondition pc = new PickCondition(new PickHint(PickHint.PickType.NOTHING));
    InputState state = new InputState();
    assertTrue(pc.evalutateChain(state));
  }

  @Test
  public void pickConditionEvaluateChainSecondNodeFails() {
    PickCondition first = new PickCondition(new PickHint(PickHint.PickType.NOTHING));
    PickCondition second = new PickCondition(new PickHint(PickHint.PickType.VIEWABLE));
    first.setNextCondition(second);
    InputState state = new InputState();
    // first passes (NOTHING matches NOTHING), second fails (VIEWABLE vs NOTHING)
    assertFalse(first.evalutateChain(state));
  }

  @Test
  public void pickConditionEvaluateDebugWithEmptyState() {
    PickCondition pc = new PickCondition(new PickHint(PickHint.PickType.NOTHING));
    InputState state = new InputState();
    assertTrue(pc.evaluateObject_debug(state));
  }

  @Test
  public void pickConditionEvaluateChainDebugSingleNode() {
    PickCondition pc = new PickCondition(new PickHint(PickHint.PickType.NOTHING));
    InputState state = new InputState();
    assertTrue(pc.evalutateChain_debug(state));
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  SelectedObjectCondition
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void selectedObjectConditionConstruction() {
    SelectedObjectCondition soc = new SelectedObjectCondition(new PickHint());
    assertNotNull(soc);
  }

  @Test
  public void selectedObjectConditionWithSwitchBehavior() {
    SelectedObjectCondition soc = new SelectedObjectCondition(
        new PickHint(), SelectedObjectCondition.ObjectSwitchBehavior.IGNORE_SWITCH);
    assertNotNull(soc);
  }

  @Test
  public void selectedObjectConditionSwitchBehaviorEnumValues() {
    SelectedObjectCondition.ObjectSwitchBehavior[] values =
        SelectedObjectCondition.ObjectSwitchBehavior.values();
    assertEquals(2, values.length);
    assertNotNull(SelectedObjectCondition.ObjectSwitchBehavior.END_ON_SWITCH);
    assertNotNull(SelectedObjectCondition.ObjectSwitchBehavior.IGNORE_SWITCH);
  }

  @Test
  public void selectedObjectConditionIsRunningNoSelection() {
    SelectedObjectCondition soc = new SelectedObjectCondition(new PickHint());
    InputState current = new InputState();
    InputState previous = new InputState();
    // No selected object → testState returns false → isRunning returns false
    assertFalse(soc.isRunning(current, previous));
  }

  @Test
  public void selectedObjectConditionJustStartedNoSelection() {
    SelectedObjectCondition soc = new SelectedObjectCondition(new PickHint());
    InputState current = new InputState();
    InputState previous = new InputState();
    assertFalse(soc.justStarted(current, previous));
  }

  @Test
  public void selectedObjectConditionJustEndedNoSelection() {
    SelectedObjectCondition soc = new SelectedObjectCondition(new PickHint());
    InputState current = new InputState();
    InputState previous = new InputState();
    assertFalse(soc.justEnded(current, previous));
  }

  @Test
  public void selectedObjectConditionStateChangedIdenticalStates() {
    SelectedObjectCondition soc = new SelectedObjectCondition(
        new PickHint(), SelectedObjectCondition.ObjectSwitchBehavior.IGNORE_SWITCH);
    InputState current = new InputState();
    InputState previous = new InputState();
    // Same null selected object and same testState result → no change
    assertFalse(soc.stateChanged(current, previous));
  }

  @Test
  public void selectedObjectConditionClickedReturnsFalse() {
    SelectedObjectCondition soc = new SelectedObjectCondition(new PickHint());
    InputState current = new InputState();
    InputState previous = new InputState();
    // InputCondition.clicked default returns false
    assertFalse(soc.clicked(current, previous));
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  AndInputCondition
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void andInputConditionEmptyIsRunning() {
    AndInputCondition aic = new AndInputCondition();
    InputState current = new InputState();
    InputState previous = new InputState();
    // No conditions → vacuously true
    assertTrue(aic.isRunning(current, previous));
  }

  @Test
  public void andInputConditionEmptyJustStarted() {
    AndInputCondition aic = new AndInputCondition();
    InputState current = new InputState();
    InputState previous = new InputState();
    // No conditions → anyStart stays false
    assertFalse(aic.justStarted(current, previous));
  }

  @Test
  public void andInputConditionEmptyJustEnded() {
    AndInputCondition aic = new AndInputCondition();
    InputState current = new InputState();
    InputState previous = new InputState();
    assertFalse(aic.justEnded(current, previous));
  }

  @Test
  public void andInputConditionEmptyStateChanged() {
    AndInputCondition aic = new AndInputCondition();
    InputState current = new InputState();
    InputState previous = new InputState();
    assertFalse(aic.stateChanged(current, previous));
  }

  @Test
  public void andInputConditionEmptyTestState() {
    AndInputCondition aic = new AndInputCondition();
    InputState state = new InputState();
    // No conditions → vacuously true
    assertTrue(aic.testState(state));
  }

  @Test
  public void andInputConditionEmptyToString() {
    AndInputCondition aic = new AndInputCondition();
    assertEquals("", aic.toString());
  }

  @Test
  public void andInputConditionWithSingleCondition() {
    SelectedObjectCondition soc = new SelectedObjectCondition(new PickHint());
    AndInputCondition aic = new AndInputCondition(soc);
    InputState current = new InputState();
    InputState previous = new InputState();
    // soc returns false with no selection → AND returns false
    assertFalse(aic.isRunning(current, previous));
  }

  @Test
  public void andInputConditionWithMultipleConditions() {
    SelectedObjectCondition soc1 = new SelectedObjectCondition(new PickHint());
    SelectedObjectCondition soc2 = new SelectedObjectCondition(
        new PickHint(), SelectedObjectCondition.ObjectSwitchBehavior.IGNORE_SWITCH);
    AndInputCondition aic = new AndInputCondition(soc1, soc2);
    InputState current = new InputState();
    InputState previous = new InputState();
    assertFalse(aic.isRunning(current, previous));
  }

  @Test
  public void andInputConditionToStringWithConditions() {
    SelectedObjectCondition soc1 = new SelectedObjectCondition(new PickHint());
    SelectedObjectCondition soc2 = new SelectedObjectCondition(new PickHint());
    AndInputCondition aic = new AndInputCondition(soc1, soc2);
    String str = aic.toString();
    assertNotNull(str);
    assertTrue(str.contains(" AND "));
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  ManipulatorConditionSet
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void manipulatorConditionSetConstruction() {
    ManipulatorConditionSet mcs = new ManipulatorConditionSet(null, "test");
    assertNotNull(mcs);
  }

  @Test
  public void manipulatorConditionSetName() {
    ManipulatorConditionSet mcs = new ManipulatorConditionSet(null, "myCondition");
    assertEquals("myCondition", mcs.getName());
  }

  @Test
  public void manipulatorConditionSetEnabled() {
    ManipulatorConditionSet mcs = new ManipulatorConditionSet(null, "test");
    assertTrue(mcs.isEnabled());
    mcs.setEnabled(false);
    assertFalse(mcs.isEnabled());
    mcs.setEnabled(true);
    assertTrue(mcs.isEnabled());
  }

  @Test
  public void manipulatorConditionSetGetManipulator() {
    ManipulatorConditionSet mcs = new ManipulatorConditionSet(null, "test");
    assertNull(mcs.getManipulator());
  }

  @Test
  public void manipulatorConditionSetToString() {
    ManipulatorConditionSet mcs = new ManipulatorConditionSet(null, "myName");
    String str = mcs.toString();
    assertNotNull(str);
    assertTrue(str.contains("myName"));
    assertEquals("ManipulatorConditionSet:myName", str);
  }

  @Test
  public void manipulatorConditionSetAddCondition() {
    ManipulatorConditionSet mcs = new ManipulatorConditionSet(null, "test");
    SelectedObjectCondition soc = new SelectedObjectCondition(new PickHint());
    mcs.addCondition(soc);
    // Verify no exception thrown
  }

  @Test
  public void manipulatorConditionSetStateMethodsWithEmptyConditions() {
    ManipulatorConditionSet mcs = new ManipulatorConditionSet(null, "test");
    InputState current = new InputState();
    InputState previous = new InputState();
    // All return false with empty conditions
    assertFalse(mcs.stateChanged(current, previous));
    assertFalse(mcs.shouldContinue(current, previous));
    assertFalse(mcs.justStarted(current, previous));
    assertFalse(mcs.justEnded(current, previous));
    assertFalse(mcs.clicked(current, previous));
  }

  @Test
  public void manipulatorConditionSetUpdateWithEmptyConditions() {
    ManipulatorConditionSet mcs = new ManipulatorConditionSet(null, "test");
    InputState current = new InputState();
    InputState previous = new InputState();
    mcs.update(current, previous);
    // No exception with empty conditions
  }

  @Test
  public void manipulatorConditionSetWithConditionStateChanged() {
    ManipulatorConditionSet mcs = new ManipulatorConditionSet(null, "test");
    SelectedObjectCondition soc = new SelectedObjectCondition(new PickHint());
    mcs.addCondition(soc);
    InputState current = new InputState();
    InputState previous = new InputState();
    // Both states have no selection → stateChanged = false
    assertFalse(mcs.stateChanged(current, previous));
  }
}
