package org.alice.interact.handle;

import org.alice.interact.handle.HandleSet.HandleGroup;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import static org.junit.Assert.*;

public class HandleSetAndManagerTest {

  // ── HandleSet construction ──

  @Test
  public void handleSetEmptyConstruction() {
    HandleSet hs = new HandleSet();
    assertNotNull(hs);
    assertTrue(hs.isEmpty());
  }

  @Test
  public void handleSetSingleGroupConstruction() {
    HandleSet hs = new HandleSet(HandleGroup.ROTATION);
    assertFalse(hs.isEmpty());
  }

  @Test
  public void handleSetMultipleGroupsConstruction() {
    HandleSet hs = new HandleSet(HandleGroup.ROTATION, HandleGroup.TRANSLATION, HandleGroup.RESIZE);
    assertFalse(hs.isEmpty());
  }

  // ── HandleSet add ──

  @Test
  public void handleSetAddGroup() {
    HandleSet hs = new HandleSet();
    hs.addGroup(HandleGroup.SELECTION);
    assertFalse(hs.isEmpty());
  }

  @Test
  public void handleSetAddGroups() {
    HandleSet hs = new HandleSet();
    hs.addGroups(HandleGroup.X_AXIS, HandleGroup.Y_AXIS, HandleGroup.Z_AXIS);
    assertFalse(hs.isEmpty());
  }

  @Test
  public void handleSetAddSet() {
    HandleSet a = new HandleSet(HandleGroup.ROTATION);
    HandleSet b = new HandleSet(HandleGroup.TRANSLATION);
    a.addSet(b);
    // a now contains both ROTATION and TRANSLATION, so it contains all bits of b
    assertTrue(a.intersects(b));
  }

  // ── HandleSet intersects (containsAll semantics) ──

  @Test
  public void handleSetSupersetIntersectsSubset() {
    HandleSet superset = new HandleSet(HandleGroup.ROTATION, HandleGroup.TRANSLATION, HandleGroup.RESIZE);
    HandleSet subset = new HandleSet(HandleGroup.ROTATION, HandleGroup.TRANSLATION);
    assertTrue(superset.intersects(subset));
  }

  @Test
  public void handleSetSubsetDoesNotIntersectSuperset() {
    HandleSet superset = new HandleSet(HandleGroup.ROTATION, HandleGroup.TRANSLATION, HandleGroup.RESIZE);
    HandleSet subset = new HandleSet(HandleGroup.ROTATION, HandleGroup.TRANSLATION);
    // subset does NOT contain RESIZE, so intersects returns false
    assertFalse(subset.intersects(superset));
  }

  @Test
  public void handleSetDisjointDoesNotIntersect() {
    HandleSet a = new HandleSet(HandleGroup.ROTATION);
    HandleSet b = new HandleSet(HandleGroup.RESIZE);
    assertFalse(a.intersects(b));
  }

  @Test
  public void handleSetSelfIntersects() {
    HandleSet hs = new HandleSet(HandleGroup.ROTATION);
    assertTrue(hs.intersects(hs));
  }

  @Test
  public void handleSetEmptyDoesNotIntersect() {
    HandleSet a = new HandleSet();
    HandleSet b = new HandleSet();
    assertFalse(a.intersects(b));
  }

  @Test
  public void handleSetIntersectsNullReturnsFalse() {
    HandleSet hs = new HandleSet(HandleGroup.ROTATION);
    assertFalse(hs.intersects(null));
  }

  // ── HandleSet toString / getStringForSet ──

  @Test
  public void handleSetToStringNotNull() {
    HandleSet hs = new HandleSet(HandleGroup.ROTATION, HandleGroup.SELECTION);
    String str = hs.toString();
    assertNotNull(str);
    assertTrue(str.contains("ROTATION"));
    assertTrue(str.contains("SELECTION"));
  }

  @Test
  public void handleSetGetStringForSetContainsGroupNames() {
    HandleSet hs = new HandleSet(HandleGroup.DEFAULT, HandleGroup.INTERACTION);
    String str = HandleSet.getStringForSet(hs);
    assertNotNull(str);
    assertTrue(str.contains("DEFAULT"));
    assertTrue(str.contains("INTERACTION"));
  }

  @Test
  public void handleSetEmptyToStringIsEmpty() {
    HandleSet hs = new HandleSet();
    assertEquals("", hs.toString());
  }

  // ── HandleSet static constants ──

  @Test
  public void staticInteractionSetsAreNotNull() {
    assertNotNull(HandleSet.DEFAULT_INTERACTION);
    assertNotNull(HandleSet.SELECTION_ONLY);
    assertNotNull(HandleSet.RESIZE_INTERACTION);
    assertNotNull(HandleSet.ROTATION_INTERACTION);
    assertNotNull(HandleSet.TRANSLATION_INTERACTION);
    assertNotNull(HandleSet.JOINT_ROTATION_INTERACTION);
    assertNotNull(HandleSet.JOINT_TRANSLATION_INTERACTION);
    assertNotNull(HandleSet.STOOD_UP_TRANSLATION_INTERACTION);
    assertNotNull(HandleSet.ABSOLUTE_TRANSLATION_INTERACTION);
  }

  @Test
  public void staticVisualizationSetsAreNotNull() {
    assertNotNull(HandleSet.STOOD_UP_GROUND_TRANSLATION_VISUALIZATION);
    assertNotNull(HandleSet.STOOD_UP_UP_DOWN_TRANSLATION_VISUALIZATION);
    assertNotNull(HandleSet.ABSOLUTE_GROUND_TRANSLATION_VISUALIZATION);
    assertNotNull(HandleSet.ABSOLUTE_UP_DOWN_TRANSLATION_VISUALIZATION);
  }

  @Test
  public void staticCameraSetsAreNotNull() {
    assertNotNull(HandleSet.MAIN_ORTHOGRAPHIC_CAMERA_CONTROLS);
    assertNotNull(HandleSet.MAIN_PERSPECTIVE_CAMERA_CONTROLS);
  }

  @Test
  public void defaultInteractionContainsDefaultAndSelection() {
    assertTrue(HandleSet.DEFAULT_INTERACTION.intersects(HandleSet.SELECTION_ONLY));
  }

  // ── HandleGroup enum ──

  @Test
  public void handleGroupEnumHasExpectedCount() {
    HandleGroup[] groups = HandleGroup.values();
    assertTrue(groups.length >= 20);
  }

  @Test
  public void handleGroupValueOfKnownNames() {
    assertNotNull(HandleGroup.valueOf("ROTATION"));
    assertNotNull(HandleGroup.valueOf("TRANSLATION"));
    assertNotNull(HandleGroup.valueOf("RESIZE"));
    assertNotNull(HandleGroup.valueOf("DEFAULT"));
    assertNotNull(HandleGroup.valueOf("SELECTION"));
    assertNotNull(HandleGroup.valueOf("CAMERA"));
    assertNotNull(HandleGroup.valueOf("JOINT"));
    assertNotNull(HandleGroup.valueOf("X_AXIS"));
    assertNotNull(HandleGroup.valueOf("Y_AXIS"));
    assertNotNull(HandleGroup.valueOf("Z_AXIS"));
    assertNotNull(HandleGroup.valueOf("STOOD_UP_TRANSLATION"));
    assertNotNull(HandleGroup.valueOf("ABSOLUTE_TRANSLATION"));
  }

  // ── HandleManager ──

  @Test
  public void handleManagerDefaultConstruction() {
    HandleManager mgr = new HandleManager();
    assertNotNull(mgr);
  }

  @Test
  public void handleManagerSelectedObjectIsNullInitially() {
    HandleManager mgr = new HandleManager();
    assertNull(mgr.getSelectedObject());
  }

  @Test
  public void handleManagerSetAndGetHandleSet() {
    HandleManager mgr = new HandleManager();
    HandleSet hs = new HandleSet(HandleGroup.ROTATION);
    mgr.setHandleSet(hs);
    assertNotNull(mgr.getCurrentHandleSet());
  }

  @Test
  public void handleManagerPushAndPopHandleSet() {
    HandleManager mgr = new HandleManager();
    HandleSet first = new HandleSet(HandleGroup.ROTATION);
    mgr.setHandleSet(first);
    HandleSet second = new HandleSet(HandleGroup.TRANSLATION);
    mgr.pushNewHandleSet(second);
    assertNotNull(mgr.getCurrentHandleSet());
    HandleSet popped = mgr.popHandleSet();
    assertNotNull(popped);
    // After pop, should be back to first set
    assertNotNull(mgr.getCurrentHandleSet());
  }

  @Test
  public void handleManagerClear() {
    HandleManager mgr = new HandleManager();
    mgr.setHandleSet(new HandleSet(HandleGroup.ROTATION));
    mgr.clear();
    assertNull(mgr.getSelectedObject());
  }

  @Test
  public void handleManagerUpdateCameraPosition() {
    HandleManager mgr = new HandleManager();
    mgr.updateCameraPosition(new Point3(0, 5, 10));
    // verify no exception thrown
  }

  @Test
  public void handleManagerCanHaveHandlesNullReturnsFalse() {
    assertFalse(HandleManager.canHaveHandles(null));
  }

  @Test
  public void handleManagerIsSelectableNullReturnsFalse() {
    assertFalse(HandleManager.isSelectable(null));
  }

  @Test
  public void handleManagerSetHandlesShowingNoHandles() {
    HandleManager mgr = new HandleManager();
    mgr.setHandlesShowing(true);
    mgr.setHandlesShowing(false);
  }

  @Test
  public void handleManagerSetHandlesVisibleNoHandles() {
    HandleManager mgr = new HandleManager();
    mgr.setHandlesVisible(true);
    mgr.setHandlesVisible(false);
  }
}
