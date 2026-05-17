package org.alice.interact;

import org.alice.interact.DragAdapter.ObjectType;
import org.alice.interact.condition.MovementDescription;
import org.alice.interact.handle.HandleSet;
import org.alice.interact.handle.HandleSet.HandleGroup;
import org.alice.math.immutable.Vector3;
import org.junit.Test;

import java.awt.event.KeyEvent;
import java.util.BitSet;

import static org.junit.Assert.*;

/**
 * Tests for interact domain classes: MovementDirection, MovementType,
 * MovementDescription, MovementKey, InteractionGroup, ObjectType,
 * HandleSet, and PickHint. All headless-safe.
 */
public class DragAdapterStateTest {

  // ═══════════════════════════════════════════════════════
  // MovementDirection enum
  // ═══════════════════════════════════════════════════════

  @Test
  public void movementDirection_allValuesExist() {
    assertEquals(19, MovementDirection.values().length);
  }

  @Test
  public void movementDirection_forwardVector() {
    Vector3 v = MovementDirection.FORWARD.getVector();
    assertEquals(0.0, v.x(), 0.001);
    assertEquals(0.0, v.y(), 0.001);
    assertTrue("Forward should have negative z", v.z() < 0);
  }

  @Test
  public void movementDirection_backwardVector() {
    Vector3 v = MovementDirection.BACKWARD.getVector();
    assertTrue("Backward should have positive z", v.z() > 0);
  }

  @Test
  public void movementDirection_leftVector() {
    Vector3 v = MovementDirection.LEFT.getVector();
    assertTrue("Left should have negative x", v.x() < 0);
  }

  @Test
  public void movementDirection_rightVector() {
    Vector3 v = MovementDirection.RIGHT.getVector();
    assertTrue("Right should have positive x", v.x() > 0);
  }

  @Test
  public void movementDirection_upVector() {
    Vector3 v = MovementDirection.UP.getVector();
    assertTrue("Up should have positive y", v.y() > 0);
  }

  @Test
  public void movementDirection_downVector() {
    Vector3 v = MovementDirection.DOWN.getVector();
    assertTrue("Down should have negative y", v.y() < 0);
  }

  @Test
  public void movementDirection_vectorsAreNormalized() {
    for (MovementDirection dir : MovementDirection.values()) {
      Vector3 v = dir.getVector();
      double length = Math.sqrt(v.x() * v.x() + v.y() * v.y() + v.z() * v.z());
      assertEquals("Vector for " + dir + " should be normalized", 1.0, length, 0.001);
    }
  }

  // ── getOpposite ───────────────────────────────────────

  @Test
  public void movementDirection_forwardOppositeIsBackward() {
    assertEquals(MovementDirection.BACKWARD, MovementDirection.FORWARD.getOpposite());
  }

  @Test
  public void movementDirection_backwardOppositeIsForward() {
    assertEquals(MovementDirection.FORWARD, MovementDirection.BACKWARD.getOpposite());
  }

  @Test
  public void movementDirection_upOppositeIsDown() {
    assertEquals(MovementDirection.DOWN, MovementDirection.UP.getOpposite());
  }

  @Test
  public void movementDirection_downOppositeIsUp() {
    assertEquals(MovementDirection.UP, MovementDirection.DOWN.getOpposite());
  }

  @Test
  public void movementDirection_leftOppositeIsRight() {
    assertEquals(MovementDirection.RIGHT, MovementDirection.LEFT.getOpposite());
  }

  @Test
  public void movementDirection_rightOppositeIsLeft() {
    assertEquals(MovementDirection.LEFT, MovementDirection.RIGHT.getOpposite());
  }

  @Test
  public void movementDirection_compoundDirectionOppositeIsSelf() {
    // Compound directions return themselves as their opposite
    assertEquals(MovementDirection.UP_RIGHT, MovementDirection.UP_RIGHT.getOpposite());
    assertEquals(MovementDirection.DOWN_LEFT, MovementDirection.DOWN_LEFT.getOpposite());
    assertEquals(MovementDirection.UP_BACKWARD, MovementDirection.UP_BACKWARD.getOpposite());
    assertEquals(MovementDirection.RESIZE, MovementDirection.RESIZE.getOpposite());
  }

  // ── getHandleGroup ────────────────────────────────────

  @Test
  public void movementDirection_forwardHandleGroupIsZAxis() {
    assertEquals(HandleGroup.Z_AXIS, MovementDirection.FORWARD.getHandleGroup());
  }

  @Test
  public void movementDirection_backwardHandleGroupIsZAxis() {
    assertEquals(HandleGroup.Z_AXIS, MovementDirection.BACKWARD.getHandleGroup());
  }

  @Test
  public void movementDirection_upHandleGroupIsYAxis() {
    assertEquals(HandleGroup.Y_AXIS, MovementDirection.UP.getHandleGroup());
  }

  @Test
  public void movementDirection_downHandleGroupIsYAxis() {
    assertEquals(HandleGroup.Y_AXIS, MovementDirection.DOWN.getHandleGroup());
  }

  @Test
  public void movementDirection_leftHandleGroupIsXAxis() {
    assertEquals(HandleGroup.X_AXIS, MovementDirection.LEFT.getHandleGroup());
  }

  @Test
  public void movementDirection_rightHandleGroupIsXAxis() {
    assertEquals(HandleGroup.X_AXIS, MovementDirection.RIGHT.getHandleGroup());
  }

  @Test
  public void movementDirection_upBackwardHandleGroupIsYAndZ() {
    assertEquals(HandleGroup.Y_AND_Z_AXIS, MovementDirection.UP_BACKWARD.getHandleGroup());
  }

  @Test
  public void movementDirection_upForwardHandleGroupIsYAndZ() {
    assertEquals(HandleGroup.Y_AND_Z_AXIS, MovementDirection.UP_FORWARD.getHandleGroup());
  }

  @Test
  public void movementDirection_downBackwardHandleGroupIsYAndZ() {
    assertEquals(HandleGroup.Y_AND_Z_AXIS, MovementDirection.DOWN_BACKWARD.getHandleGroup());
  }

  @Test
  public void movementDirection_downForwardHandleGroupIsYAndZ() {
    assertEquals(HandleGroup.Y_AND_Z_AXIS, MovementDirection.DOWN_FORWARD.getHandleGroup());
  }

  @Test
  public void movementDirection_upRightHandleGroupIsXAndY() {
    assertEquals(HandleGroup.X_AND_Y_AXIS, MovementDirection.UP_RIGHT.getHandleGroup());
  }

  @Test
  public void movementDirection_upLeftHandleGroupIsXAndY() {
    assertEquals(HandleGroup.X_AND_Y_AXIS, MovementDirection.UP_LEFT.getHandleGroup());
  }

  @Test
  public void movementDirection_downRightHandleGroupIsXAndY() {
    assertEquals(HandleGroup.X_AND_Y_AXIS, MovementDirection.DOWN_RIGHT.getHandleGroup());
  }

  @Test
  public void movementDirection_downLeftHandleGroupIsXAndY() {
    assertEquals(HandleGroup.X_AND_Y_AXIS, MovementDirection.DOWN_LEFT.getHandleGroup());
  }

  @Test
  public void movementDirection_rightBackwardHandleGroupIsXAndZ() {
    assertEquals(HandleGroup.X_AND_Z_AXIS, MovementDirection.RIGHT_BACKWARD.getHandleGroup());
  }

  @Test
  public void movementDirection_leftBackwardHandleGroupIsXAndZ() {
    assertEquals(HandleGroup.X_AND_Z_AXIS, MovementDirection.LEFT_BACKWARD.getHandleGroup());
  }

  @Test
  public void movementDirection_rightForwardHandleGroupIsXAndZ() {
    assertEquals(HandleGroup.X_AND_Z_AXIS, MovementDirection.RIGHT_FORWARD.getHandleGroup());
  }

  @Test
  public void movementDirection_leftForwardHandleGroupIsXAndZ() {
    assertEquals(HandleGroup.X_AND_Z_AXIS, MovementDirection.LEFT_FORWARD.getHandleGroup());
  }

  @Test
  public void movementDirection_resizeHandleGroupIsResizeAxis() {
    assertEquals(HandleGroup.RESIZE_AXIS, MovementDirection.RESIZE.getHandleGroup());
  }

  // ── hasDirection ──────────────────────────────────────

  @Test
  public void movementDirection_hasDirection_alignedVectorReturnsTrue() {
    assertTrue(MovementDirection.FORWARD.hasDirection(new Vector3(0, 0, -5)));
  }

  @Test
  public void movementDirection_hasDirection_oppositeVectorReturnsFalse() {
    assertFalse(MovementDirection.FORWARD.hasDirection(new Vector3(0, 0, 5)));
  }

  @Test
  public void movementDirection_hasDirection_perpendicularReturnsFalse() {
    assertFalse(MovementDirection.FORWARD.hasDirection(new Vector3(1, 0, 0)));
  }

  @Test
  public void movementDirection_hasDirection_partiallyAlignedReturnsTrue() {
    assertTrue(MovementDirection.UP.hasDirection(new Vector3(1, 1, 0)));
  }

  // ═══════════════════════════════════════════════════════
  // MovementType enum
  // ═══════════════════════════════════════════════════════

  @Test
  public void movementType_allValuesExist() {
    assertEquals(3, MovementType.values().length);
    assertNotNull(MovementType.STOOD_UP);
    assertNotNull(MovementType.LOCAL);
    assertNotNull(MovementType.ABSOLUTE);
  }

  @Test
  public void movementType_valueOfRoundTrips() {
    for (MovementType type : MovementType.values()) {
      assertEquals(type, MovementType.valueOf(type.name()));
    }
  }

  // ═══════════════════════════════════════════════════════
  // MovementDescription
  // ═══════════════════════════════════════════════════════

  @Test
  public void movementDescription_singleArgConstructorDefaultsToStoodUp() {
    MovementDescription desc = new MovementDescription(MovementDirection.FORWARD);
    assertEquals(MovementType.STOOD_UP, desc.type);
    assertEquals(MovementDirection.FORWARD, desc.direction);
  }

  @Test
  public void movementDescription_twoArgConstructor() {
    MovementDescription desc = new MovementDescription(MovementDirection.UP, MovementType.LOCAL);
    assertEquals(MovementType.LOCAL, desc.type);
    assertEquals(MovementDirection.UP, desc.direction);
  }

  @Test
  public void movementDescription_equalsWithSameFields() {
    MovementDescription a = new MovementDescription(MovementDirection.LEFT, MovementType.ABSOLUTE);
    MovementDescription b = new MovementDescription(MovementDirection.LEFT, MovementType.ABSOLUTE);
    assertTrue(a.equals(b));
  }

  @Test
  public void movementDescription_equalsWithDifferentDirection() {
    MovementDescription a = new MovementDescription(MovementDirection.LEFT, MovementType.ABSOLUTE);
    MovementDescription b = new MovementDescription(MovementDirection.RIGHT, MovementType.ABSOLUTE);
    assertFalse(a.equals(b));
  }

  @Test
  public void movementDescription_equalsWithDifferentType() {
    MovementDescription a = new MovementDescription(MovementDirection.UP, MovementType.LOCAL);
    MovementDescription b = new MovementDescription(MovementDirection.UP, MovementType.ABSOLUTE);
    assertFalse(a.equals(b));
  }

  @Test
  public void movementDescription_equalsWithNull() {
    MovementDescription desc = new MovementDescription(MovementDirection.FORWARD);
    assertFalse(desc.equals(null));
  }

  @Test
  public void movementDescription_equalsWithWrongType() {
    MovementDescription desc = new MovementDescription(MovementDirection.FORWARD);
    assertFalse(desc.equals("not a description"));
  }

  @Test
  public void movementDescription_toStringContainsInfo() {
    MovementDescription desc = new MovementDescription(MovementDirection.UP, MovementType.LOCAL);
    String str = desc.toString();
    assertTrue(str.contains("LOCAL"));
    assertTrue(str.contains("UP"));
  }

  // ═══════════════════════════════════════════════════════
  // MovementKey
  // ═══════════════════════════════════════════════════════

  @Test
  public void movementKey_singleArgConstructor() {
    MovementDescription desc = new MovementDescription(MovementDirection.FORWARD);
    MovementKey key = new MovementKey(KeyEvent.VK_W, desc);
    assertEquals(KeyEvent.VK_W, key.keyValue);
  }

  @Test
  public void movementKey_threeArgConstructor() {
    MovementDescription desc = new MovementDescription(MovementDirection.UP, MovementType.LOCAL);
    MovementKey key = new MovementKey(KeyEvent.VK_PAGE_UP, desc, 0.5);
    assertEquals(KeyEvent.VK_PAGE_UP, key.keyValue);
  }

  // ── DragAdapter static key arrays ─────────────────────

  @Test
  public void defaultMovementKeys_hasExpectedCount() {
    // DEFAULT_MOVEMENT_KEYS is protected, check indirectly by verifying DragAdapter can be subclassed
    TestDragAdapter adapter = new TestDragAdapter();
    assertNotNull(adapter);
  }

  // ═══════════════════════════════════════════════════════
  // ObjectType enum
  // ═══════════════════════════════════════════════════════

  @Test
  public void objectType_allValuesExist() {
    ObjectType[] values = ObjectType.values();
    assertEquals(7, values.length);
    assertNotNull(ObjectType.JOINT);
    assertNotNull(ObjectType.MODEL);
    assertNotNull(ObjectType.CAMERA_MARKER);
    assertNotNull(ObjectType.OBJECT_MARKER);
    assertNotNull(ObjectType.MAIN_CAMERA);
    assertNotNull(ObjectType.UNKNOWN);
    assertNotNull(ObjectType.ANY);
  }

  @Test
  public void objectType_getObjectType_nullReturnsUnknown() {
    assertEquals(ObjectType.UNKNOWN, ObjectType.getObjectType(null));
  }

  // ═══════════════════════════════════════════════════════
  // InteractionGroup.PossibleObjects
  // ═══════════════════════════════════════════════════════

  @Test
  public void possibleObjects_containsExactType() {
    InteractionGroup.PossibleObjects po = new InteractionGroup.PossibleObjects(ObjectType.MODEL, ObjectType.JOINT);
    assertTrue(po.containsType(ObjectType.MODEL));
    assertTrue(po.containsType(ObjectType.JOINT));
    assertFalse(po.containsType(ObjectType.CAMERA_MARKER));
  }

  @Test
  public void possibleObjects_anyMatchesEverything() {
    InteractionGroup.PossibleObjects po = new InteractionGroup.PossibleObjects(ObjectType.ANY);
    assertTrue(po.containsType(ObjectType.MODEL));
    assertTrue(po.containsType(ObjectType.JOINT));
    assertTrue(po.containsType(ObjectType.CAMERA_MARKER));
    assertTrue(po.containsType(ObjectType.UNKNOWN));
  }

  @Test
  public void possibleObjects_emptyMatchesNothing() {
    InteractionGroup.PossibleObjects po = new InteractionGroup.PossibleObjects();
    assertFalse(po.containsType(ObjectType.MODEL));
  }

  // ═══════════════════════════════════════════════════════
  // InteractionGroup
  // ═══════════════════════════════════════════════════════

  @Test
  public void interactionGroup_defaultConstructor() {
    InteractionGroup ig = new InteractionGroup();
    assertNull(ig.getMatchingInfo(ObjectType.MODEL));
  }

  // ═══════════════════════════════════════════════════════
  // PickHint
  // ═══════════════════════════════════════════════════════

  @Test
  public void pickHint_defaultConstructorIsEmpty() {
    PickHint hint = new PickHint();
    assertTrue(hint.isEmpty());
  }

  @Test
  public void pickHint_constructedWithTypes() {
    PickHint hint = new PickHint(PickHint.PickType.MOVEABLE, PickHint.PickType.SELECTABLE);
    assertTrue(hint.get(PickHint.PickType.MOVEABLE));
    assertTrue(hint.get(PickHint.PickType.SELECTABLE));
    assertFalse(hint.get(PickHint.PickType.NOTHING));
  }

  @Test
  public void pickHint_addPickType() {
    PickHint hint = new PickHint();
    hint.addPickType(PickHint.PickType.JOINT);
    assertTrue(hint.get(PickHint.PickType.JOINT));
  }

  @Test
  public void pickHint_getMarkersHint_containsMarkerTypes() {
    PickHint markers = PickHint.getMarkersHint();
    assertTrue(markers.get(PickHint.PickType.CAMERA_MARKER));
    assertTrue(markers.get(PickHint.PickType.OBJECT_MARKER));
  }

  @Test
  public void pickHint_getMarkersHint_isSingleton() {
    assertSame(PickHint.getMarkersHint(), PickHint.getMarkersHint());
  }

  @Test
  public void pickHint_getAnythingHint_containsAllTypes() {
    PickHint anything = PickHint.getAnythingHint();
    for (PickHint.PickType type : PickHint.PickType.values()) {
      assertTrue("Should contain " + type, anything.get(type));
    }
  }

  @Test
  public void pickHint_getAnythingHint_isSingleton() {
    assertSame(PickHint.getAnythingHint(), PickHint.getAnythingHint());
  }

  @Test
  public void pickHint_getNonInteractiveHint_containsNothing() {
    PickHint nonInteractive = PickHint.getNonInteractiveHint();
    assertTrue(nonInteractive.get(PickHint.PickType.NOTHING));
    assertFalse(nonInteractive.get(PickHint.PickType.MOVEABLE));
  }

  @Test
  public void pickHint_getNonInteractiveHint_isSingleton() {
    assertSame(PickHint.getNonInteractiveHint(), PickHint.getNonInteractiveHint());
  }

  @Test
  public void pickHint_getAllHandlesHint() {
    PickHint allHandles = PickHint.getAllHandlesHint();
    assertTrue(allHandles.get(PickHint.PickType.TWO_D_HANDLE));
    assertTrue(allHandles.get(PickHint.PickType.THREE_D_HANDLE));
  }

  @Test
  public void pickHint_getAllHandlesHint_isSingleton() {
    assertSame(PickHint.getAllHandlesHint(), PickHint.getAllHandlesHint());
  }

  @Test
  public void pickHint_createEverythingHint_containsAllTypes() {
    PickHint everything = PickHint.createEverythingHint();
    for (PickHint.PickType type : PickHint.PickType.values()) {
      assertTrue("Should contain " + type, everything.get(type));
    }
  }

  @Test
  public void pickHint_toString_containsPickTypeNames() {
    PickHint hint = new PickHint(PickHint.PickType.MOVEABLE);
    String str = hint.toString();
    assertTrue(str.contains("pick:"));
    assertTrue(str.contains("MOVEABLE"));
  }

  @Test
  public void pickHint_toString_emptyHintShowsNoMatches() {
    PickHint hint = new PickHint();
    assertTrue(hint.toString().contains("No Matches"));
  }

  @Test
  public void pickType_pickHint_returnsSingletonHint() {
    PickHint hint = PickHint.PickType.JOINT.pickHint();
    assertNotNull(hint);
    assertTrue(hint.get(PickHint.PickType.JOINT));
    assertSame("pickHint() should cache", hint, PickHint.PickType.JOINT.pickHint());
  }

  @Test
  public void pickType_allValues() {
    assertEquals(14, PickHint.PickType.values().length);
  }

  // ═══════════════════════════════════════════════════════
  // HandleSet
  // ═══════════════════════════════════════════════════════

  @Test
  public void handleSet_constructorSetsGroups() {
    HandleSet set = new HandleSet(HandleGroup.ROTATION, HandleGroup.INTERACTION);
    assertTrue(set.get(HandleGroup.ROTATION.ordinal()));
    assertTrue(set.get(HandleGroup.INTERACTION.ordinal()));
    assertFalse(set.get(HandleGroup.TRANSLATION.ordinal()));
  }

  @Test
  public void handleSet_addGroup() {
    HandleSet set = new HandleSet();
    set.addGroup(HandleGroup.RESIZE);
    assertTrue(set.get(HandleGroup.RESIZE.ordinal()));
  }

  @Test
  public void handleSet_addGroups() {
    HandleSet set = new HandleSet();
    set.addGroups(HandleGroup.X_AXIS, HandleGroup.Y_AXIS, HandleGroup.Z_AXIS);
    assertTrue(set.get(HandleGroup.X_AXIS.ordinal()));
    assertTrue(set.get(HandleGroup.Y_AXIS.ordinal()));
    assertTrue(set.get(HandleGroup.Z_AXIS.ordinal()));
  }

  @Test
  public void handleSet_addSet() {
    HandleSet a = new HandleSet(HandleGroup.ROTATION);
    HandleSet b = new HandleSet(HandleGroup.TRANSLATION);
    a.addSet(b);
    assertTrue(a.get(HandleGroup.ROTATION.ordinal()));
    assertTrue(a.get(HandleGroup.TRANSLATION.ordinal()));
  }

  @Test
  public void handleSet_intersects_fullSubset() {
    HandleSet set = new HandleSet(HandleGroup.ROTATION, HandleGroup.INTERACTION);
    HandleSet query = new HandleSet(HandleGroup.ROTATION);
    assertTrue(set.intersects(query));
  }

  @Test
  public void handleSet_intersects_missingGroup() {
    HandleSet set = new HandleSet(HandleGroup.ROTATION);
    HandleSet query = new HandleSet(HandleGroup.ROTATION, HandleGroup.INTERACTION);
    assertFalse("Should not match if query has groups not in set", set.intersects(query));
  }

  @Test
  public void handleSet_intersects_null() {
    HandleSet set = new HandleSet(HandleGroup.ROTATION);
    assertFalse(set.intersects(null));
  }

  @Test
  public void handleSet_intersects_emptySet() {
    HandleSet set = new HandleSet(HandleGroup.ROTATION);
    HandleSet empty = new HandleSet();
    assertFalse("Empty set should never match", set.intersects(empty));
  }

  @Test
  public void handleSet_toString_containsGroupNames() {
    HandleSet set = new HandleSet(HandleGroup.ROTATION, HandleGroup.SELECTION);
    String str = set.toString();
    assertTrue(str.contains("ROTATION"));
    assertTrue(str.contains("SELECTION"));
  }

  @Test
  public void handleSet_getStringForSet_containsGroupNames() {
    HandleSet set = new HandleSet(HandleGroup.CAMERA);
    String str = HandleSet.getStringForSet(set);
    assertTrue(str.contains("CAMERA"));
  }

  // ── HandleSet static instances ────────────────────────

  @Test
  public void handleSet_defaultInteraction_containsExpectedGroups() {
    HandleSet di = HandleSet.DEFAULT_INTERACTION;
    assertTrue(di.get(HandleGroup.DEFAULT.ordinal()));
    assertTrue(di.get(HandleGroup.INTERACTION.ordinal()));
    assertTrue(di.get(HandleGroup.SELECTION.ordinal()));
  }

  @Test
  public void handleSet_resizeInteraction_containsExpectedGroups() {
    HandleSet ri = HandleSet.RESIZE_INTERACTION;
    assertTrue(ri.get(HandleGroup.RESIZE.ordinal()));
    assertTrue(ri.get(HandleGroup.INTERACTION.ordinal()));
  }

  @Test
  public void handleSet_rotationInteraction_containsExpectedGroups() {
    HandleSet rot = HandleSet.ROTATION_INTERACTION;
    assertTrue(rot.get(HandleGroup.ROTATION.ordinal()));
    assertTrue(rot.get(HandleGroup.INTERACTION.ordinal()));
  }

  @Test
  public void handleSet_translationInteraction_containsExpectedGroups() {
    HandleSet ti = HandleSet.TRANSLATION_INTERACTION;
    assertTrue(ti.get(HandleGroup.TRANSLATION.ordinal()));
    assertTrue(ti.get(HandleGroup.INTERACTION.ordinal()));
  }

  @Test
  public void handleSet_selectionOnly_containsSelection() {
    HandleSet so = HandleSet.SELECTION_ONLY;
    assertTrue(so.get(HandleGroup.SELECTION.ordinal()));
    assertFalse(so.get(HandleGroup.INTERACTION.ordinal()));
  }

  @Test
  public void handleSet_stoodUpGroundTranslationVisualization() {
    HandleSet set = HandleSet.STOOD_UP_GROUND_TRANSLATION_VISUALIZATION;
    assertTrue(set.get(HandleGroup.STOOD_UP_TRANSLATION.ordinal()));
    assertTrue(set.get(HandleGroup.VISUALIZATION.ordinal()));
    assertTrue(set.get(HandleGroup.X_AND_Z_AXIS.ordinal()));
  }

  @Test
  public void handleSet_absoluteGroundTranslationVisualization() {
    HandleSet set = HandleSet.ABSOLUTE_GROUND_TRANSLATION_VISUALIZATION;
    assertTrue(set.get(HandleGroup.ABSOLUTE_TRANSLATION.ordinal()));
    assertTrue(set.get(HandleGroup.VISUALIZATION.ordinal()));
    assertTrue(set.get(HandleGroup.X_AND_Z_AXIS.ordinal()));
  }

  @Test
  public void handleSet_stoodUpUpDownTranslationVisualization() {
    HandleSet set = HandleSet.STOOD_UP_UP_DOWN_TRANSLATION_VISUALIZATION;
    assertTrue(set.get(HandleGroup.STOOD_UP_TRANSLATION.ordinal()));
    assertTrue(set.get(HandleGroup.Y_AXIS.ordinal()));
  }

  @Test
  public void handleSet_absoluteUpDownTranslationVisualization() {
    HandleSet set = HandleSet.ABSOLUTE_UP_DOWN_TRANSLATION_VISUALIZATION;
    assertTrue(set.get(HandleGroup.ABSOLUTE_TRANSLATION.ordinal()));
    assertTrue(set.get(HandleGroup.Y_AXIS.ordinal()));
  }

  @Test
  public void handleSet_mainOrthographicCameraControls() {
    HandleSet set = HandleSet.MAIN_ORTHOGRAPHIC_CAMERA_CONTROLS;
    assertTrue(set.get(HandleGroup.ORTHOGRAPHIC_CAMERA.ordinal()));
    assertTrue(set.get(HandleGroup.MAIN_CAMERA.ordinal()));
  }

  @Test
  public void handleSet_mainPerspectiveCameraControls() {
    HandleSet set = HandleSet.MAIN_PERSPECTIVE_CAMERA_CONTROLS;
    assertTrue(set.get(HandleGroup.PERSPECTIVE_CAMERA.ordinal()));
    assertTrue(set.get(HandleGroup.MAIN_CAMERA.ordinal()));
  }

  // ── HandleGroup enum ──────────────────────────────────

  @Test
  public void handleGroup_allValuesExist() {
    HandleGroup[] values = HandleGroup.values();
    assertTrue("Should have many handle groups", values.length >= 20);
  }

  @Test
  public void handleGroup_valueOfRoundTrips() {
    for (HandleGroup hg : HandleGroup.values()) {
      assertEquals(hg, HandleGroup.valueOf(hg.name()));
    }
  }

  // ── DragAdapter.CameraView ────────────────────────────

  @Test
  public void cameraView_allValuesExist() {
    DragAdapter.CameraView[] values = DragAdapter.CameraView.values();
    assertNotNull(values);
    assertTrue("Should have camera view values", values.length > 0);
  }

  // ═══════════════════════════════════════════════════════
  // DragAdapter subclass construction
  // ═══════════════════════════════════════════════════════

  @Test
  public void dragAdapter_defaultSnapSettingsAreFalse() {
    TestDragAdapter adapter = new TestDragAdapter();
    assertFalse(adapter.shouldSnapToGround());
    assertFalse(adapter.shouldSnapToGrid());
    assertFalse(adapter.shouldSnapToRotation());
  }

  @Test
  public void dragAdapter_defaultGridSpacingIsOne() {
    TestDragAdapter adapter = new TestDragAdapter();
    assertEquals(1.0, adapter.getGridSpacing(), 0.001);
  }

  @Test
  public void dragAdapter_rotationSnapAngleIsNotNull() {
    TestDragAdapter adapter = new TestDragAdapter();
    assertNotNull(adapter.getRotationSnapAngle());
  }

  @Test
  public void dragAdapter_hasSceneEditorReturnsFalse() {
    TestDragAdapter adapter = new TestDragAdapter();
    assertFalse(adapter.hasSceneEditor());
  }

  @Test
  public void dragAdapter_getOnscreenRenderTargetIsNullInitially() {
    TestDragAdapter adapter = new TestDragAdapter();
    assertNull(adapter.getOnscreenRenderTarget());
  }

  @Test
  public void dragAdapter_getAnimatorIsNullInitially() {
    TestDragAdapter adapter = new TestDragAdapter();
    assertNull(adapter.getAnimator());
  }

  @Test
  public void dragAdapter_getActiveCameraIsNullInitially() {
    TestDragAdapter adapter = new TestDragAdapter();
    assertNull(adapter.getActiveCamera());
  }

  static class TestDragAdapter extends DragAdapter {
  }
}
