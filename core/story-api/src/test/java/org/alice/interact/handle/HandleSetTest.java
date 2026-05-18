package org.alice.interact.handle;

import org.alice.interact.handle.HandleSet.HandleGroup;
import org.junit.Test;

import java.util.BitSet;

import static org.junit.Assert.*;

/** Headless-safe characterization tests for HandleSet. */
public class HandleSetTest {

  @Test
  public void constructorWithNoGroupsStartsEmpty() {
    HandleSet set = new HandleSet();
    assertTrue(set.isEmpty());
  }

  @Test
  public void constructorWithGroupsSetsCorrespondingBits() {
    HandleSet set = new HandleSet(HandleGroup.ROTATION, HandleGroup.TRANSLATION);
    assertTrue(set.get(HandleGroup.ROTATION.ordinal()));
    assertTrue(set.get(HandleGroup.TRANSLATION.ordinal()));
    assertFalse(set.get(HandleGroup.RESIZE.ordinal()));
  }

  @Test
  public void constructorWithDuplicateGroupsOnlySetsSingleBit() {
    HandleSet set = new HandleSet(HandleGroup.ROTATION, HandleGroup.ROTATION);
    assertEquals(1, set.cardinality());
  }

  @Test
  public void addGroupSetsBitForGroup() {
    HandleSet set = new HandleSet();
    set.addGroup(HandleGroup.SELECTION);
    assertTrue(set.get(HandleGroup.SELECTION.ordinal()));
  }

  @Test
  public void addGroupsAddsAllRequestedGroups() {
    HandleSet set = new HandleSet();
    set.addGroups(HandleGroup.X_AXIS, HandleGroup.Y_AXIS, HandleGroup.Z_AXIS);
    assertTrue(set.get(HandleGroup.X_AXIS.ordinal()));
    assertTrue(set.get(HandleGroup.Y_AXIS.ordinal()));
    assertTrue(set.get(HandleGroup.Z_AXIS.ordinal()));
  }

  @Test
  public void intersectsRequiresEveryBitInOtherSetToBePresent() {
    HandleSet superset = new HandleSet(HandleGroup.ROTATION, HandleGroup.TRANSLATION, HandleGroup.INTERACTION);
    HandleSet subset = new HandleSet(HandleGroup.ROTATION, HandleGroup.INTERACTION);
    assertTrue(superset.intersects(subset));
    assertFalse(subset.intersects(superset));
  }

  @Test
  public void intersectsReturnsFalseForDisjointSet() {
    HandleSet left = new HandleSet(HandleGroup.ROTATION);
    HandleSet right = new HandleSet(HandleGroup.RESIZE);
    assertFalse(left.intersects(right));
  }

  @Test
  public void intersectsReturnsFalseForEmptyBitSet() {
    HandleSet set = new HandleSet(HandleGroup.ROTATION);
    assertFalse(set.intersects(new BitSet()));
  }

  @Test
  public void addSetPerformsUnionOperation() {
    HandleSet left = new HandleSet(HandleGroup.ROTATION);
    HandleSet right = new HandleSet(HandleGroup.TRANSLATION, HandleGroup.INTERACTION);
    left.addSet(right);
    assertTrue(left.intersects(right));
    assertEquals(3, left.cardinality());
  }

  @Test
  public void toStringUsesEnumDeclarationOrder() {
    HandleSet set = new HandleSet(HandleGroup.TRANSLATION, HandleGroup.ROTATION);
    assertEquals("ROTATION TRANSLATION ", set.toString());
  }

  @Test
  public void getStringForStaticConstantContainsExpectedGroups() {
    String text = HandleSet.getStringForSet(HandleSet.DEFAULT_INTERACTION);
    assertTrue(text.contains("DEFAULT"));
    assertTrue(text.contains("INTERACTION"));
    assertTrue(text.contains("SELECTION"));
  }

  @Test
  public void staticConstantsContainTheirExpectedGroups() {
    assertTrue(HandleSet.RESIZE_INTERACTION.get(HandleGroup.RESIZE.ordinal()));
    assertTrue(HandleSet.ROTATION_INTERACTION.get(HandleGroup.ROTATION.ordinal()));
    assertTrue(HandleSet.TRANSLATION_INTERACTION.get(HandleGroup.TRANSLATION.ordinal()));
    assertTrue(HandleSet.MAIN_PERSPECTIVE_CAMERA_CONTROLS.get(HandleGroup.PERSPECTIVE_CAMERA.ordinal()));
    assertTrue(HandleSet.MAIN_PERSPECTIVE_CAMERA_CONTROLS.get(HandleGroup.MAIN_CAMERA.ordinal()));
  }
}
