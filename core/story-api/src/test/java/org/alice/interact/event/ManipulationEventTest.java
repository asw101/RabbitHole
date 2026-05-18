package org.alice.interact.event;

import edu.cmu.cs.dennisc.scenegraph.Transformable;
import org.alice.interact.InputState;
import org.alice.interact.MovementDirection;
import org.alice.interact.MovementType;
import org.alice.interact.PickHint;
import org.alice.interact.condition.MovementDescription;
import org.junit.Test;

import static org.junit.Assert.*;

/** Headless-safe tests for ManipulationEvent value behavior. */
public class ManipulationEventTest {

  @Test
  public void constructorStoresTypeMovementAndTarget() {
    Transformable target = new Transformable();
    MovementDescription movement = new MovementDescription(MovementDirection.LEFT, MovementType.LOCAL);
    ManipulationEvent event = new ManipulationEvent(ManipulationEvent.EventType.Rotate, movement, target);

    assertEquals(ManipulationEvent.EventType.Rotate, event.getType());
    assertSame(movement, event.getMovementDescription());
    assertSame(target, event.getTarget());
  }

  @Test
  public void inputStateStartsNull() {
    assertNull(new ManipulationEvent(ManipulationEvent.EventType.Translate, null, null).getInputState());
  }

  @Test
  public void setInputStateStoresReference() {
    ManipulationEvent event = new ManipulationEvent(ManipulationEvent.EventType.Translate, null, null);
    InputState state = new InputState();
    event.setInputState(state);
    assertSame(state, event.getInputState());
  }

  @Test
  public void nullTargetProducesNothingPickHint() {
    ManipulationEvent event = new ManipulationEvent(ManipulationEvent.EventType.Translate, null, null);
    assertTrue(event.getTargetPickHint().intersects(PickHint.PickType.NOTHING.pickHint()));
  }

  @Test
  public void defaultTransformableTargetAlsoHasPickHint() {
    ManipulationEvent event = new ManipulationEvent(ManipulationEvent.EventType.Translate, null, new Transformable());
    assertNotNull(event.getTargetPickHint());
  }

  @Test
  public void toStringIncludesTypeName() {
    ManipulationEvent event = new ManipulationEvent(ManipulationEvent.EventType.Zoom, null, null);
    assertTrue(event.toString().contains("Zoom"));
  }

  @Test
  public void toStringUsesPlaceholderForNullMovementDescription() {
    ManipulationEvent event = new ManipulationEvent(ManipulationEvent.EventType.Scale, null, null);
    assertTrue(event.toString().contains("---"));
  }

  @Test
  public void toStringIncludesMovementDescriptionText() {
    MovementDescription movement = new MovementDescription(MovementDirection.UP, MovementType.STOOD_UP);
    ManipulationEvent event = new ManipulationEvent(ManipulationEvent.EventType.Translate, movement, null);
    assertTrue(event.toString().contains(movement.toString()));
  }

  @Test
  public void eventTypeEnumContainsExpectedValues() {
    assertNotNull(ManipulationEvent.EventType.Translate);
    assertNotNull(ManipulationEvent.EventType.Scale);
    assertNotNull(ManipulationEvent.EventType.Rotate);
    assertNotNull(ManipulationEvent.EventType.Zoom);
  }
}
