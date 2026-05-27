package org.alice.interact;

import edu.cmu.cs.dennisc.scenegraph.Transformable;
import org.alice.interact.DragAdapter.ObjectType;
import org.alice.interact.condition.ManipulatorConditionSet;
import org.alice.interact.condition.MovementDescription;
import org.alice.interact.handle.HandleSet;
import org.alice.interact.manipulator.AbstractManipulator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class InteractionMetadataCoverageTest {
  @Test
  public void movementDescriptionDefaultsToStoodUpAndSupportsEqualityAndFormatting() {
    MovementDescription description = new MovementDescription(MovementDirection.FORWARD);
    MovementDescription same = new MovementDescription(MovementDirection.FORWARD, MovementType.STOOD_UP);
    MovementDescription different = new MovementDescription(MovementDirection.LEFT, MovementType.LOCAL);

    assertEquals(MovementType.STOOD_UP, description.type);
    assertEquals(same, description);
    assertNotEquals(different, description);
    assertTrue(description.toString().contains("FORWARD"));
    assertTrue(description.toString().contains("STOOD_UP"));
  }

  @Test
  public void interactionGroupMatchesObjectTypesAndTogglesManipulatorState() {
    DummyManipulator manipulator = new DummyManipulator();
    ManipulatorConditionSet conditionSet = new ManipulatorConditionSet(manipulator, "coverage");
    InteractionGroup.InteractionInfo info = new InteractionGroup.InteractionInfo(
        new InteractionGroup.PossibleObjects(ObjectType.MODEL),
        HandleSet.DEFAULT_INTERACTION,
        conditionSet,
        PickHint.PickType.SELECTABLE,
        PickHint.PickType.MOVEABLE);
    InteractionGroup group = new InteractionGroup();

    group.addInteractionInfo(info);

    assertSame(info, group.getMatchingInfo(ObjectType.MODEL));
    assertNull(group.getMatchingInfo(ObjectType.JOINT));
    assertTrue(info.canUseIteractionGroup(PickHint.PickType.SELECTABLE.pickHint()));
    assertFalse(info.canUseIteractionGroup(PickHint.PickType.JOINT.pickHint()));
    assertTrue(info.toString().contains("INTERACTION"));

    group.enabledManipulators(false);
    assertFalse(conditionSet.isEnabled());

    group.enabledManipulators(true);
    assertTrue(conditionSet.isEnabled());
  }

  @Test
  public void interactionInfoPossibleObjectsAcceptAnySentinel() {
    InteractionGroup.PossibleObjects possibleObjects = new InteractionGroup.PossibleObjects(ObjectType.ANY);

    assertTrue(possibleObjects.containsType(ObjectType.MODEL));
    assertTrue(possibleObjects.containsType(ObjectType.JOINT));
  }

  @Test
  public void movementKeyUsesDescriptionAndDirectionMultiplierForTranslation() {
    Transformable transformable = new Transformable();
    MovementKey key = new MovementKey(87, new MovementDescription(MovementDirection.FORWARD, MovementType.LOCAL), 2.0d);

    key.applyTranslation(transformable, 1.5d);

    assertEquals(-3.0d, transformable.getLocalTransformation().translation().z(), 1.0e-9);
  }

  private static final class DummyManipulator extends AbstractManipulator {
    @Override
    protected HandleSet getHandleSetToEnable() {
      return HandleSet.DEFAULT_INTERACTION;
    }

    @Override
    public String getUndoRedoDescription() {
      return "coverage";
    }

    @Override
    public boolean doStartManipulator(InputState startInput) {
      return true;
    }

    @Override
    public void doDataUpdateManipulator(InputState currentInput, InputState previousInput) {
    }

    @Override
    public void doTimeUpdateManipulator(double dTime, InputState currentInput) {
    }

    @Override
    public void doEndManipulator(InputState endInput, InputState previousInput) {
    }

    @Override
    public void doClickManipulator(InputState endInput, InputState previousInput) {
    }
  }
}
