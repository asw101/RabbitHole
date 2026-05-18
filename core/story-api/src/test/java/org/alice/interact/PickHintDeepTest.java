package org.alice.interact;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class PickHintDeepTest {
  @Test
  public void pickTypePickHintCachesSingleInstance() {
    assertSame(PickHint.PickType.MOVEABLE.pickHint(), PickHint.PickType.MOVEABLE.pickHint());
  }

  @Test
  public void differentPickTypesUseDifferentCachedInstances() {
    assertNotSame(PickHint.PickType.MOVEABLE.pickHint(), PickHint.PickType.TURNABLE.pickHint());
  }

  @Test
  public void createEverythingHintReturnsFreshInstance() {
    assertNotSame(PickHint.createEverythingHint(), PickHint.createEverythingHint());
  }

  @Test
  public void getAnythingHintReturnsSingletonWithAllBits() {
    PickHint hint = PickHint.getAnythingHint();
    assertSame(hint, PickHint.getAnythingHint());
    for (PickHint.PickType type : PickHint.PickType.values()) {
      assertTrue(hint.get(type));
    }
  }

  @Test
  public void markersHintIsSingleton() {
    assertSame(PickHint.getMarkersHint(), PickHint.getMarkersHint());
  }

  @Test
  public void addPickTypeIsIdempotent() {
    PickHint hint = new PickHint(PickHint.PickType.MOVEABLE);
    hint.addPickType(PickHint.PickType.MOVEABLE);
    assertTrue(hint.get(PickHint.PickType.MOVEABLE));
    assertFalse(hint.get(PickHint.PickType.TURNABLE));
  }

  @Test
  public void cloneProducesIndependentBitSet() {
    PickHint original = new PickHint(PickHint.PickType.MOVEABLE);
    PickHint copy = (PickHint) original.clone();
    copy.addPickType(PickHint.PickType.TURNABLE);

    assertFalse(original.get(PickHint.PickType.TURNABLE));
    assertTrue(copy.get(PickHint.PickType.TURNABLE));
  }

  @Test
  public void markersHintContainsOnlyMarkerFlagsFromPublicFactories() {
    PickHint markers = PickHint.getMarkersHint();
    assertTrue(markers.get(PickHint.PickType.CAMERA_MARKER));
    assertTrue(markers.get(PickHint.PickType.OBJECT_MARKER));
    assertFalse(markers.get(PickHint.PickType.MOVEABLE));
  }

  @Test
  public void allHandlesHintContainsBothHandleFlags() {
    PickHint handles = PickHint.getAllHandlesHint();
    assertTrue(handles.get(PickHint.PickType.TWO_D_HANDLE));
    assertTrue(handles.get(PickHint.PickType.THREE_D_HANDLE));
    assertFalse(handles.get(PickHint.PickType.CAMERA_MARKER));
  }

  @Test
  public void nonInteractiveHintContainsNothingFlagOnly() {
    PickHint nonInteractive = PickHint.getNonInteractiveHint();
    assertTrue(nonInteractive.get(PickHint.PickType.NOTHING));
    assertFalse(nonInteractive.get(PickHint.PickType.SELECTABLE));
  }

  @Test
  public void toStringListsOnlyEnabledTypes() {
    PickHint hint = new PickHint(PickHint.PickType.MOVEABLE, PickHint.PickType.TURNABLE);
    String text = hint.toString();
    assertTrue(text.contains("MOVEABLE"));
    assertTrue(text.contains("TURNABLE"));
    assertFalse(text.contains("RESIZABLE"));
  }
}
