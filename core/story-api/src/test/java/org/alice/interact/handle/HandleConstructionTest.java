package org.alice.interact.handle;

import edu.cmu.cs.dennisc.color.Color4f;
import edu.cmu.cs.dennisc.scenegraph.scale.Resizer;
import org.alice.interact.MovementDirection;
import org.alice.interact.MovementType;
import org.alice.interact.condition.MovementDescription;
import org.alice.interact.event.ManipulationEvent;
import org.alice.interact.handle.HandleSet.HandleGroup;
import org.alice.interact.handle.RotationRingHandle.HandlePosition;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Construction and method tests for handle classes in the interact.handle package.
 * Uses concrete subclasses (RotationRingHandle, StoodUpRotationRingHandle,
 * JointRotationRingHandle, LinearScaleHandle) to test the full
 * ManipulationHandle3D API surface. All tests are headless-safe.
 */
public class HandleConstructionTest {

  private static final ManipulationEvent DUMMY_EVENT =
      new ManipulationEvent(ManipulationEvent.EventType.Translate, null, null);

  // ═══════════════════════════════════════════════════════════════════════
  //  Helper: exercise ManipulationHandle3D API on any concrete instance
  // ═══════════════════════════════════════════════════════════════════════

  private void exerciseHandle3DApi(ManipulationHandle3D h, String label) {
    assertNotNull(label + " not null", h);
    assertNull(label + " manipulatedObject", h.getManipulatedObject());
    assertFalse(label + " isAlwaysVisible", h.isAlwaysVisible());

    HandleSet hs = h.getHandleSet();
    assertNotNull(label + " handleSet", hs);

    // setManipulation takes AbstractManipulator, getManipulation takes InputState
    h.setManipulation(null);
    assertNull(label + " getManipulation", h.getManipulation(new org.alice.interact.InputState()));

    HandleManager mgr = new HandleManager();
    h.setHandleManager(mgr);
    assertSame(label + " handleManager", mgr, h.getHandleManager());

    h.setHandleVisible(true);
    assertTrue(label + " isHandleVisible", h.isHandleVisible());
    h.setHandleVisible(false);
    assertFalse(label + " not visible", h.isHandleVisible());

    h.setHandleActive(true);
    h.setHandleActive(false);
    h.setHandleRollover(true);
    h.setHandleRollover(false);

    h.setPickable(true);
    h.setHandleVisible(true);
    assertTrue(label + " isPickable", h.isPickable());
    h.setPickable(false);
    assertFalse(label + " not pickable", h.isPickable());

    HandleState state = h.getHandleStateCopy();
    assertNotNull(label + " handleState", state);

    h.setCameraPosition(new Point3(0, 5, 10));

    h.activate(DUMMY_EVENT);
    h.deactivate(DUMMY_EVENT);

    h.clear();
    assertNull(label + " after clear", h.getManipulatedObject());

    h.setDragAdapter(null);

    h.setVisualsShowing(true);
    h.setVisualsShowing(false);

    h.setManipulatedObject(null);

    assertNotNull(label + " pickHint", h.getPickHint());

    h.calculateCameraRelativeOpacity(new Point3(0, 5, 10));
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  RotationRingHandle — all constructors
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void rotationRingHandleDefaultConstruction() {
    RotationRingHandle h = new RotationRingHandle();
    exerciseHandle3DApi(h, "RotationRingHandle()");
  }

  @Test
  public void rotationRingHandleWithDirection() {
    RotationRingHandle h = new RotationRingHandle(MovementDirection.UP);
    exerciseHandle3DApi(h, "RotationRingHandle(UP)");
  }

  @Test
  public void rotationRingHandleWithDirectionAndColor() {
    RotationRingHandle h = new RotationRingHandle(MovementDirection.LEFT, Color4f.RED);
    assertNotNull(h);
  }

  @Test
  public void rotationRingHandleWithDirectionAndPosition() {
    RotationRingHandle h = new RotationRingHandle(MovementDirection.FORWARD, HandlePosition.BOTTOM);
    assertNotNull(h);
  }

  @Test
  public void rotationRingHandleWithDirectionPositionAndColor() {
    RotationRingHandle h = new RotationRingHandle(MovementDirection.RIGHT, HandlePosition.TOP, Color4f.GREEN);
    assertNotNull(h);
  }

  @Test
  public void rotationRingHandleFullColorConstructor() {
    RotationRingHandle h = new RotationRingHandle(
        MovementDirection.UP, HandlePosition.BOTTOM,
        Color4f.RED, Color4f.GREEN, Color4f.BLUE, Color4f.YELLOW);
    assertNotNull(h);
  }

  @Test
  public void rotationRingHandleCopyConstructor() {
    RotationRingHandle original = new RotationRingHandle(MovementDirection.UP);
    RotationRingHandle copy = new RotationRingHandle(original);
    assertNotNull(copy);
  }

  @Test
  public void rotationRingHandleClone() {
    RotationRingHandle h = new RotationRingHandle(MovementDirection.UP);
    RotationRingHandle cloned = h.clone();
    assertNotNull(cloned);
    assertNotSame(h, cloned);
  }

  @Test
  public void rotationRingHandleGetRadius() {
    RotationRingHandle h = new RotationRingHandle();
    double radius = h.getRadius();
    assertTrue(radius >= 0);
  }

  @Test
  public void rotationRingHandleSetSphereVisibility() {
    RotationRingHandle h = new RotationRingHandle();
    h.setSphereVisibility(true);
    h.setSphereVisibility(false);
  }

  @Test
  public void rotationRingHandleIsMemberOf() {
    RotationRingHandle h = new RotationRingHandle(MovementDirection.UP);
    h.addToSet(new HandleSet(HandleGroup.ROTATION));
    HandleSet query = new HandleSet(HandleGroup.ROTATION);
    assertTrue(h.isMemberOf(query));
  }

  @Test
  public void rotationRingHandleMultipleGroups() {
    RotationRingHandle h = new RotationRingHandle(MovementDirection.UP);
    h.addToGroup(HandleGroup.ROTATION);
    h.addToGroup(HandleGroup.INTERACTION);
    h.addToGroup(HandleGroup.SELECTION);
    HandleSet query = new HandleSet(HandleGroup.ROTATION, HandleGroup.INTERACTION);
    assertTrue(h.isMemberOf(query));
  }

  @Test
  public void rotationRingHandleNotMemberOfDisjoint() {
    RotationRingHandle h = new RotationRingHandle(MovementDirection.UP);
    h.addToGroup(HandleGroup.ROTATION);
    HandleSet query = new HandleSet(HandleGroup.TRANSLATION);
    assertFalse(h.isMemberOf(query));
  }

  @Test
  public void rotationRingHandleGetSGVisualAndAppearance() {
    RotationRingHandle h = new RotationRingHandle(MovementDirection.UP);
    h.getSGVisual();
    h.getSGFrontFacingAppearance();
  }

  @Test
  public void rotationRingHandleIsRenderable() {
    RotationRingHandle h = new RotationRingHandle(MovementDirection.UP);
    h.isRenderable();
  }

  @Test
  public void rotationRingHandleGetReferenceFrame() {
    RotationRingHandle h = new RotationRingHandle(MovementDirection.UP);
    assertNull(h.getReferenceFrame());
  }

  @Test
  public void rotationRingHandleMatchesNull() {
    RotationRingHandle h = new RotationRingHandle(MovementDirection.UP);
    assertFalse(h.matches(null));
  }

  @Test
  public void rotationRingHandleAddToGroups() {
    RotationRingHandle h = new RotationRingHandle();
    h.addToGroups(HandleGroup.ROTATION, HandleGroup.SELECTION, HandleGroup.INTERACTION);
    assertTrue(h.isMemberOf(new HandleSet(HandleGroup.ROTATION, HandleGroup.SELECTION)));
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  StoodUpRotationRingHandle
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void stoodUpRotationRingHandleDefaultConstruction() {
    StoodUpRotationRingHandle h = new StoodUpRotationRingHandle();
    exerciseHandle3DApi(h, "StoodUpRotationRingHandle()");
  }

  @Test
  public void stoodUpRotationRingHandleWithDirection() {
    StoodUpRotationRingHandle h = new StoodUpRotationRingHandle(MovementDirection.UP);
    assertNotNull(h);
  }

  @Test
  public void stoodUpRotationRingHandleWithDirectionAndPosition() {
    StoodUpRotationRingHandle h = new StoodUpRotationRingHandle(MovementDirection.LEFT, HandlePosition.TOP);
    assertNotNull(h);
  }

  @Test
  public void stoodUpRotationRingHandleCopyConstructor() {
    StoodUpRotationRingHandle original = new StoodUpRotationRingHandle();
    StoodUpRotationRingHandle copy = new StoodUpRotationRingHandle(original);
    assertNotNull(copy);
  }

  @Test
  public void stoodUpRotationRingHandleClone() {
    StoodUpRotationRingHandle h = new StoodUpRotationRingHandle();
    StoodUpRotationRingHandle cloned = (StoodUpRotationRingHandle) h.clone();
    assertNotNull(cloned);
    assertNotSame(h, cloned);
  }

  @Test
  public void stoodUpRotationRingHandleRadius() {
    StoodUpRotationRingHandle h = new StoodUpRotationRingHandle();
    double radius = h.getRadius();
    assertTrue(radius >= 0);
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  JointRotationRingHandle
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void jointRotationRingHandleDefaultConstruction() {
    JointRotationRingHandle h = new JointRotationRingHandle();
    exerciseHandle3DApi(h, "JointRotationRingHandle()");
  }

  @Test
  public void jointRotationRingHandleWithDirection() {
    JointRotationRingHandle h = new JointRotationRingHandle(MovementDirection.RIGHT);
    assertNotNull(h);
  }

  @Test
  public void jointRotationRingHandleWithDirectionAndColor() {
    JointRotationRingHandle h = new JointRotationRingHandle(MovementDirection.UP, Color4f.BLUE);
    assertNotNull(h);
  }

  @Test
  public void jointRotationRingHandleWithDirectionAndPosition() {
    JointRotationRingHandle h = new JointRotationRingHandle(MovementDirection.FORWARD, HandlePosition.BOTTOM);
    assertNotNull(h);
  }

  @Test
  public void jointRotationRingHandleWithDirectionPositionAndColor() {
    JointRotationRingHandle h = new JointRotationRingHandle(MovementDirection.LEFT, HandlePosition.TOP, Color4f.RED);
    assertNotNull(h);
  }

  @Test
  public void jointRotationRingHandleFullColorConstructor() {
    JointRotationRingHandle h = new JointRotationRingHandle(
        MovementDirection.UP, HandlePosition.BOTTOM,
        Color4f.RED, Color4f.GREEN, Color4f.BLUE, Color4f.YELLOW);
    assertNotNull(h);
  }

  @Test
  public void jointRotationRingHandleCopyConstructor() {
    JointRotationRingHandle original = new JointRotationRingHandle(MovementDirection.UP);
    JointRotationRingHandle copy = new JointRotationRingHandle(original);
    assertNotNull(copy);
  }

  @Test
  public void jointRotationRingHandleClone() {
    JointRotationRingHandle h = new JointRotationRingHandle(MovementDirection.UP);
    JointRotationRingHandle cloned = (JointRotationRingHandle) h.clone();
    assertNotNull(cloned);
    assertNotSame(h, cloned);
  }

  @Test
  public void jointRotationRingHandleRadiusBounds() {
    JointRotationRingHandle h = new JointRotationRingHandle();
    double minRadius = h.getMinTorusRadius();
    double maxRadius = h.getMaxTorusRadius();
    assertTrue(minRadius >= 0);
    assertTrue(maxRadius >= minRadius);
  }

  @Test
  public void jointRotationRingHandleMajorAxisRadius() {
    JointRotationRingHandle h = new JointRotationRingHandle(MovementDirection.UP);
    double r = h.getMajorAxisRadius();
    assertTrue(r >= 0);
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  LinearScaleHandle (concrete subclass of abstract LinearDragHandle)
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void linearScaleHandleConstruction() {
    MovementDescription desc = new MovementDescription(MovementDirection.UP);
    LinearScaleHandle h = new LinearScaleHandle(desc, Color4f.RED, true, Resizer.Y_AXIS);
    assertNotNull(h);
  }

  @Test
  public void linearScaleHandleCreateFromResizer() {
    LinearScaleHandle h = LinearScaleHandle.createFromResizer(Resizer.X_AXIS);
    assertNotNull(h);
    assertSame(Resizer.X_AXIS, h.getResizer());
  }

  @Test
  public void linearScaleHandleCreateFromEachResizer() {
    for (Resizer r : Resizer.values()) {
      LinearScaleHandle h = LinearScaleHandle.createFromResizer(r);
      assertNotNull(r.name(), h);
      assertSame(r.name(), r, h.getResizer());
    }
  }

  @Test
  public void linearScaleHandleCopyConstruction() {
    MovementDescription desc = new MovementDescription(MovementDirection.RIGHT);
    LinearScaleHandle original = new LinearScaleHandle(desc, Color4f.BLUE, false, Resizer.Z_AXIS);
    LinearScaleHandle copy = new LinearScaleHandle(original);
    assertNotNull(copy);
    assertSame(Resizer.Z_AXIS, copy.getResizer());
  }

  @Test
  public void linearScaleHandleClone() {
    LinearScaleHandle h = LinearScaleHandle.createFromResizer(Resizer.UNIFORM);
    LinearScaleHandle cloned = (LinearScaleHandle) h.clone();
    assertNotNull(cloned);
    assertNotSame(h, cloned);
    assertSame(Resizer.UNIFORM, cloned.getResizer());
  }

  @Test
  public void linearScaleHandleApplyAlongAxis() {
    MovementDescription desc = new MovementDescription(MovementDirection.UP);
    LinearScaleHandle hTrue = new LinearScaleHandle(desc, Color4f.RED, true, Resizer.Y_AXIS);
    assertTrue(hTrue.applyAlongAxis());
    LinearScaleHandle hFalse = new LinearScaleHandle(desc, Color4f.RED, false, Resizer.Y_AXIS);
    assertFalse(hFalse.applyAlongAxis());
  }

  @Test
  public void linearScaleHandleExerciseInheritedApi() {
    LinearScaleHandle h = LinearScaleHandle.createFromResizer(Resizer.X_AXIS);
    exerciseHandle3DApi(h, "LinearScaleHandle(X_AXIS)");
  }

  @Test
  public void linearScaleHandleGetMovementDescription() {
    LinearScaleHandle h = LinearScaleHandle.createFromResizer(Resizer.Y_AXIS);
    assertNotNull(h.getMovementDescription());
  }

  @Test
  public void linearScaleHandleGetBaseColor() {
    LinearScaleHandle h = LinearScaleHandle.createFromResizer(Resizer.X_AXIS);
    Color4f color = h.getBaseColor();
    assertNotNull(color);
  }

  @Test
  public void linearScaleHandleGetSize() {
    LinearScaleHandle h = LinearScaleHandle.createFromResizer(Resizer.Z_AXIS);
    double size = h.getSize();
    assertTrue(size >= 0);
  }

  @Test
  public void linearScaleHandleSetSize() {
    LinearScaleHandle h = LinearScaleHandle.createFromResizer(Resizer.X_AXIS);
    h.setSize(2.0);
    assertEquals(2.0, h.getSize(), 1e-9);
  }

  @Test
  public void linearScaleHandleGetHandleLength() {
    LinearScaleHandle h = LinearScaleHandle.createFromResizer(Resizer.Y_AXIS);
    double length = h.getHandleLength();
    assertTrue(length >= 0);
  }

  @Test
  public void linearScaleHandleGetDragAxis() {
    LinearScaleHandle h = LinearScaleHandle.createFromResizer(Resizer.X_AXIS);
    assertNotNull(h.getDragAxis());
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  HandleState tests
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void handleStateDefaultConstruction() {
    HandleState state = new HandleState();
    assertNotNull(state);
    assertFalse(state.isActive());
    assertFalse(state.isRollover());
    assertFalse(state.isVisible());
    assertFalse(state.shouldRender());
  }

  @Test
  public void handleStateCopyConstruction() {
    HandleState original = new HandleState();
    original.setActive(true);
    original.setRollover(true);
    original.setVisible(true);
    HandleState copy = new HandleState(original);
    assertTrue(copy.isActive());
    assertTrue(copy.isRollover());
    assertTrue(copy.isVisible());
    assertTrue(copy.shouldRender());
  }

  @Test
  public void handleStateSetActive() {
    HandleState state = new HandleState();
    state.setActive(true);
    assertTrue(state.isActive());
    assertTrue(state.shouldRender());
    state.setActive(false);
    assertFalse(state.isActive());
  }

  @Test
  public void handleStateSetRollover() {
    HandleState state = new HandleState();
    state.setRollover(true);
    assertTrue(state.isRollover());
    assertTrue(state.shouldRender());
    state.setRollover(false);
    assertFalse(state.isRollover());
  }

  @Test
  public void handleStateSetVisible() {
    HandleState state = new HandleState();
    state.setVisible(true);
    assertTrue(state.isVisible());
    assertTrue(state.shouldRender());
    state.setVisible(false);
    assertFalse(state.isVisible());
  }

  @Test
  public void handleStateShouldRenderCombinations() {
    HandleState state = new HandleState();
    assertFalse(state.shouldRender());
    state.setActive(true);
    assertTrue(state.shouldRender());
    state.setActive(false);
    state.setRollover(true);
    assertTrue(state.shouldRender());
    state.setRollover(false);
    state.setVisible(true);
    assertTrue(state.shouldRender());
  }

  @Test
  public void handleStateFromRotationRingHandle() {
    RotationRingHandle h = new RotationRingHandle();
    HandleState state = h.getHandleStateCopy();
    assertNotNull(state);
    assertFalse(state.isActive());
    assertFalse(state.isRollover());
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  MovementDirection enum coverage
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void movementDirectionGetOpposite() {
    assertEquals(MovementDirection.BACKWARD, MovementDirection.FORWARD.getOpposite());
    assertEquals(MovementDirection.FORWARD, MovementDirection.BACKWARD.getOpposite());
    assertEquals(MovementDirection.DOWN, MovementDirection.UP.getOpposite());
    assertEquals(MovementDirection.UP, MovementDirection.DOWN.getOpposite());
    assertEquals(MovementDirection.RIGHT, MovementDirection.LEFT.getOpposite());
    assertEquals(MovementDirection.LEFT, MovementDirection.RIGHT.getOpposite());
  }

  @Test
  public void movementDirectionGetHandleGroup() {
    assertEquals(HandleGroup.Y_AXIS, MovementDirection.UP.getHandleGroup());
    assertEquals(HandleGroup.Y_AXIS, MovementDirection.DOWN.getHandleGroup());
    assertEquals(HandleGroup.X_AXIS, MovementDirection.LEFT.getHandleGroup());
    assertEquals(HandleGroup.X_AXIS, MovementDirection.RIGHT.getHandleGroup());
    assertEquals(HandleGroup.Z_AXIS, MovementDirection.FORWARD.getHandleGroup());
    assertEquals(HandleGroup.Z_AXIS, MovementDirection.BACKWARD.getHandleGroup());
  }

  @Test
  public void movementDirectionHasDirection() {
    assertTrue(MovementDirection.UP.hasDirection(new org.alice.math.immutable.Vector3(0, 1, 0)));
    assertFalse(MovementDirection.UP.hasDirection(new org.alice.math.immutable.Vector3(0, -1, 0)));
  }

  @Test
  public void movementDirectionAllDiagonals() {
    assertNotNull(MovementDirection.UP_RIGHT.getHandleGroup());
    assertNotNull(MovementDirection.UP_LEFT.getHandleGroup());
    assertNotNull(MovementDirection.DOWN_RIGHT.getHandleGroup());
    assertNotNull(MovementDirection.DOWN_LEFT.getHandleGroup());
    assertNotNull(MovementDirection.UP_BACKWARD.getHandleGroup());
    assertNotNull(MovementDirection.UP_FORWARD.getHandleGroup());
    assertNotNull(MovementDirection.DOWN_BACKWARD.getHandleGroup());
    assertNotNull(MovementDirection.DOWN_FORWARD.getHandleGroup());
    assertNotNull(MovementDirection.RIGHT_BACKWARD.getHandleGroup());
    assertNotNull(MovementDirection.LEFT_BACKWARD.getHandleGroup());
    assertNotNull(MovementDirection.RIGHT_FORWARD.getHandleGroup());
    assertNotNull(MovementDirection.LEFT_FORWARD.getHandleGroup());
  }

  @Test
  public void movementDirectionResizeHandleGroup() {
    assertEquals(HandleGroup.RESIZE_AXIS, MovementDirection.RESIZE.getHandleGroup());
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  MovementDescription coverage
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void movementDescriptionSingleArgConstructor() {
    MovementDescription desc = new MovementDescription(MovementDirection.UP);
    assertEquals(MovementDirection.UP, desc.direction);
    assertEquals(MovementType.STOOD_UP, desc.type);
  }

  @Test
  public void movementDescriptionTwoArgConstructor() {
    MovementDescription desc = new MovementDescription(MovementDirection.LEFT, MovementType.LOCAL);
    assertEquals(MovementDirection.LEFT, desc.direction);
    assertEquals(MovementType.LOCAL, desc.type);
  }

  @Test
  public void movementDescriptionEquals() {
    MovementDescription a = new MovementDescription(MovementDirection.UP);
    MovementDescription b = new MovementDescription(MovementDirection.UP, MovementType.STOOD_UP);
    MovementDescription c = new MovementDescription(MovementDirection.DOWN);
    assertTrue(a.equals(b));
    assertFalse(a.equals(c));
    assertFalse(a.equals("not a description"));
  }

  @Test
  public void movementDescriptionToString() {
    MovementDescription desc = new MovementDescription(MovementDirection.FORWARD);
    String str = desc.toString();
    assertNotNull(str);
    assertTrue(str.contains("FORWARD"));
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  ManipulationEventCriteria basic coverage
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void manipulationEventCriteriaConstruction() {
    org.alice.interact.event.ManipulationEventCriteria criteria =
        new org.alice.interact.event.ManipulationEventCriteria(
            ManipulationEvent.EventType.Rotate,
            new MovementDescription(MovementDirection.UP),
            org.alice.interact.PickHint.PickType.MOVEABLE.pickHint());
    assertNotNull(criteria);
    assertNotNull(criteria.toString());
  }

  @Test
  public void manipulationEventCriteriaMatches() {
    MovementDescription desc = new MovementDescription(MovementDirection.UP);
    org.alice.interact.event.ManipulationEventCriteria criteria =
        new org.alice.interact.event.ManipulationEventCriteria(
            ManipulationEvent.EventType.Translate,
            desc,
            org.alice.interact.PickHint.PickType.MOVEABLE.pickHint());
    ManipulationEvent evt = new ManipulationEvent(ManipulationEvent.EventType.Translate, desc, null);
    criteria.matches(evt);
  }
}
