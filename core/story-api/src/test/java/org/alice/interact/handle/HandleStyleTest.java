package org.alice.interact.handle;

import org.alice.interact.handle.HandleSet.HandleGroup;
import org.junit.Test;

import java.util.BitSet;

import static org.junit.Assert.*;

/**
 * Tests for HandleStyle enum and additional HandleSet behavior.
 */
public class HandleStyleTest {

  // ═══════════════════════════════════════════════════════
  // HandleStyle enum
  // ═══════════════════════════════════════════════════════

  @Test
  public void handleStyle_allValuesExist() {
    HandleStyle[] values = HandleStyle.values();
    assertEquals(4, values.length);
  }

  @Test
  public void handleStyle_defaultExists() {
    assertNotNull(HandleStyle.DEFAULT);
  }

  @Test
  public void handleStyle_rotationExists() {
    assertNotNull(HandleStyle.ROTATION);
  }

  @Test
  public void handleStyle_translationExists() {
    assertNotNull(HandleStyle.TRANSLATION);
  }

  @Test
  public void handleStyle_resizeExists() {
    assertNotNull(HandleStyle.RESIZE);
  }

  @Test
  public void handleStyle_valueOfRoundTrips() {
    for (HandleStyle hs : HandleStyle.values()) {
      assertEquals(hs, HandleStyle.valueOf(hs.name()));
    }
  }

  @Test
  public void handleStyle_ordinalOrder() {
    assertEquals(0, HandleStyle.DEFAULT.ordinal());
    assertEquals(1, HandleStyle.ROTATION.ordinal());
    assertEquals(2, HandleStyle.TRANSLATION.ordinal());
    assertEquals(3, HandleStyle.RESIZE.ordinal());
  }

  // ═══════════════════════════════════════════════════════
  // HandleSet — extended tests
  // ═══════════════════════════════════════════════════════

  @Test
  public void handleSet_emptyConstructor() {
    HandleSet set = new HandleSet();
    assertTrue("Empty HandleSet should have no groups set", set.isEmpty());
  }

  @Test
  public void handleSet_intersectsItselfWhenNonEmpty() {
    HandleSet set = new HandleSet(HandleGroup.ROTATION, HandleGroup.INTERACTION);
    assertTrue(set.intersects(set));
  }

  @Test
  public void handleSet_intersects_exactMatch() {
    HandleSet a = new HandleSet(HandleGroup.ROTATION, HandleGroup.INTERACTION);
    HandleSet b = new HandleSet(HandleGroup.ROTATION, HandleGroup.INTERACTION);
    assertTrue(a.intersects(b));
  }

  @Test
  public void handleSet_intersects_supersetContainsSubset() {
    HandleSet superset = new HandleSet(HandleGroup.ROTATION, HandleGroup.INTERACTION, HandleGroup.SELECTION);
    HandleSet subset = new HandleSet(HandleGroup.ROTATION, HandleGroup.INTERACTION);
    assertTrue("Superset should match all bits in subset", superset.intersects(subset));
  }

  @Test
  public void handleSet_intersects_subsetDoesNotContainSuperset() {
    HandleSet superset = new HandleSet(HandleGroup.ROTATION, HandleGroup.INTERACTION, HandleGroup.SELECTION);
    HandleSet subset = new HandleSet(HandleGroup.ROTATION, HandleGroup.INTERACTION);
    assertFalse("Subset should not match superset", subset.intersects(superset));
  }

  @Test
  public void handleSet_intersects_disjointSets() {
    HandleSet a = new HandleSet(HandleGroup.ROTATION);
    HandleSet b = new HandleSet(HandleGroup.TRANSLATION);
    assertFalse(a.intersects(b));
  }

  @Test
  public void handleSet_intersects_partialOverlapReturnsFalse() {
    // a has ROTATION + INTERACTION, b has ROTATION + TRANSLATION
    // b has TRANSLATION which a does not, so intersects should be false
    HandleSet a = new HandleSet(HandleGroup.ROTATION, HandleGroup.INTERACTION);
    HandleSet b = new HandleSet(HandleGroup.ROTATION, HandleGroup.TRANSLATION);
    assertFalse(a.intersects(b));
  }

  @Test
  public void handleSet_addSet_mergesTwoSets() {
    HandleSet a = new HandleSet(HandleGroup.X_AXIS);
    HandleSet b = new HandleSet(HandleGroup.Y_AXIS, HandleGroup.Z_AXIS);
    a.addSet(b);
    assertTrue(a.get(HandleGroup.X_AXIS.ordinal()));
    assertTrue(a.get(HandleGroup.Y_AXIS.ordinal()));
    assertTrue(a.get(HandleGroup.Z_AXIS.ordinal()));
  }

  @Test
  public void handleSet_getStringForSet_emptyReturnsEmpty() {
    HandleSet empty = new HandleSet();
    String str = HandleSet.getStringForSet(empty);
    assertEquals("", str.trim());
  }

  @Test
  public void handleSet_getStringForSet_multipleGroupsAllPresent() {
    HandleSet set = new HandleSet(HandleGroup.JOINT, HandleGroup.LOCAL);
    String str = HandleSet.getStringForSet(set);
    assertTrue(str.contains("JOINT"));
    assertTrue(str.contains("LOCAL"));
  }

  @Test
  public void handleSet_stoodUpTranslationInteraction() {
    HandleSet set = HandleSet.STOOD_UP_TRANSLATION_INTERACTION;
    assertTrue(set.get(HandleGroup.STOOD_UP_TRANSLATION.ordinal()));
    assertTrue(set.get(HandleGroup.INTERACTION.ordinal()));
  }

  @Test
  public void handleSet_absoluteTranslationInteraction() {
    HandleSet set = HandleSet.ABSOLUTE_TRANSLATION_INTERACTION;
    assertTrue(set.get(HandleGroup.ABSOLUTE_TRANSLATION.ordinal()));
    assertTrue(set.get(HandleGroup.INTERACTION.ordinal()));
  }

  @Test
  public void handleSet_jointRotationInteraction() {
    HandleSet set = HandleSet.JOINT_ROTATION_INTERACTION;
    assertTrue(set.get(HandleGroup.ROTATION.ordinal()));
    assertTrue(set.get(HandleGroup.INTERACTION.ordinal()));
  }

  @Test
  public void handleSet_jointTranslationInteraction() {
    HandleSet set = HandleSet.JOINT_TRANSLATION_INTERACTION;
    assertTrue(set.get(HandleGroup.TRANSLATION.ordinal()));
    assertTrue(set.get(HandleGroup.INTERACTION.ordinal()));
  }

  // ── HandleGroup enum ──────────────────────────────────

  @Test
  public void handleGroup_ordinalIsStable() {
    // Spot-check known ordinals to detect enum reordering
    assertEquals(0, HandleGroup.ROTATION.ordinal());
    assertEquals(1, HandleGroup.TRANSLATION.ordinal());
    assertEquals(2, HandleGroup.RESIZE.ordinal());
    assertEquals(3, HandleGroup.DEFAULT.ordinal());
  }

  @Test
  public void handleGroup_valueOf_knownNames() {
    assertNotNull(HandleGroup.valueOf("ROTATION"));
    assertNotNull(HandleGroup.valueOf("VISUALIZATION"));
    assertNotNull(HandleGroup.valueOf("CAMERA"));
    assertNotNull(HandleGroup.valueOf("JOINT"));
    assertNotNull(HandleGroup.valueOf("STOOD_UP_ROTATION"));
    assertNotNull(HandleGroup.valueOf("ABSOLUTE_TRANSLATION"));
  }
}
