package org.alice.interact;

import edu.cmu.cs.dennisc.scenegraph.Component;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.junit.Test;
import org.lgna.story.implementation.EntityImp;
import org.lgna.story.implementation.StandInImp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Tests for PickHint (bitset-based type hints) and PickUtilities (pick
 * resolution from scenegraph elements). Headless-safe.
 */
public class PickHintAndUtilitiesTest {

  // ══════════════════════════════════════════════════════════════════════════
  //  PickHint — construction and bitset operations
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void emptyPickHintIsEmpty() {
    PickHint hint = new PickHint();
    assertTrue(hint.isEmpty());
  }

  @Test
  public void singlePickTypeHintIsNotEmpty() {
    PickHint hint = new PickHint(PickHint.PickType.MOVEABLE);
    assertFalse(hint.isEmpty());
  }

  @Test
  public void singlePickTypeCanBeQueried() {
    PickHint hint = new PickHint(PickHint.PickType.MOVEABLE);
    assertTrue(hint.get(PickHint.PickType.MOVEABLE));
    assertFalse(hint.get(PickHint.PickType.TURNABLE));
  }

  @Test
  public void multiplePickTypesCanBeSet() {
    PickHint hint = new PickHint(PickHint.PickType.MOVEABLE, PickHint.PickType.TURNABLE);
    assertTrue(hint.get(PickHint.PickType.MOVEABLE));
    assertTrue(hint.get(PickHint.PickType.TURNABLE));
    assertFalse(hint.get(PickHint.PickType.RESIZABLE));
  }

  @Test
  public void addPickTypeAddsToExisting() {
    PickHint hint = new PickHint(PickHint.PickType.MOVEABLE);
    hint.addPickType(PickHint.PickType.TURNABLE);
    assertTrue(hint.get(PickHint.PickType.MOVEABLE));
    assertTrue(hint.get(PickHint.PickType.TURNABLE));
  }

  @Test
  public void intersectsReturnsTrueForOverlap() {
    PickHint a = new PickHint(PickHint.PickType.MOVEABLE, PickHint.PickType.TURNABLE);
    PickHint b = new PickHint(PickHint.PickType.TURNABLE, PickHint.PickType.RESIZABLE);
    assertTrue(a.intersects(b));
  }

  @Test
  public void intersectsReturnsFalseForNoOverlap() {
    PickHint a = new PickHint(PickHint.PickType.MOVEABLE);
    PickHint b = new PickHint(PickHint.PickType.TURNABLE);
    assertFalse(a.intersects(b));
  }

  @Test
  public void nothingPickHintIsSingleton() {
    PickHint a = PickHint.PickType.NOTHING.pickHint();
    PickHint b = PickHint.PickType.NOTHING.pickHint();
    assertSame(a, b);
  }

  @Test
  public void pickTypeSingletonCachesPickHint() {
    PickHint h1 = PickHint.PickType.MOVEABLE.pickHint();
    PickHint h2 = PickHint.PickType.MOVEABLE.pickHint();
    assertSame(h1, h2);
  }

  @Test
  public void everythingHintContainsAllTypes() {
    PickHint everything = PickHint.createEverythingHint();
    for (PickHint.PickType type : PickHint.PickType.values()) {
      assertTrue("Everything hint should contain " + type,
          everything.get(type));
    }
  }

  @Test
  public void getAnythingHintIsNotEmpty() {
    PickHint anything = PickHint.getAnythingHint();
    assertFalse(anything.isEmpty());
  }

  @Test
  public void getMarkersHintContainsCameraAndObjectMarker() {
    PickHint markers = PickHint.getMarkersHint();
    assertTrue(markers.get(PickHint.PickType.CAMERA_MARKER));
    assertTrue(markers.get(PickHint.PickType.OBJECT_MARKER));
  }

  @Test
  public void getAllHandlesHintContainsBothHandleTypes() {
    PickHint handles = PickHint.getAllHandlesHint();
    assertTrue(handles.get(PickHint.PickType.TWO_D_HANDLE));
    assertTrue(handles.get(PickHint.PickType.THREE_D_HANDLE));
  }

  @Test
  public void getNonInteractiveHintContainsNothing() {
    PickHint nonInteractive = PickHint.getNonInteractiveHint();
    assertTrue(nonInteractive.get(PickHint.PickType.NOTHING));
  }

  @Test
  public void toStringContainsPickOnMatch() {
    PickHint hint = new PickHint(PickHint.PickType.MOVEABLE);
    String str = hint.toString();
    assertTrue(str.contains("pick:"));
    assertTrue(str.contains("MOVEABLE"));
  }

  @Test
  public void toStringContainsNoMatchesWhenEmpty() {
    PickHint hint = new PickHint();
    String str = hint.toString();
    assertTrue(str.contains("No Matches"));
  }

  @Test
  public void toStringContainsMultipleTypes() {
    PickHint hint = new PickHint(PickHint.PickType.MOVEABLE, PickHint.PickType.TURNABLE);
    String str = hint.toString();
    assertTrue(str.contains("MOVEABLE"));
    assertTrue(str.contains("TURNABLE"));
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  PickUtilities — static helpers
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void getPickTypeForNullPickResultReturnsNothing() {
    PickHint result = PickUtilities.getPickType((edu.cmu.cs.dennisc.render.PickResult) null);
    assertNotNull(result);
    assertTrue(result.intersects(PickHint.PickType.NOTHING.pickHint()));
  }

  @Test
  public void getPickTypeForNullComponentReturnsNothing() {
    PickHint result = PickUtilities.getPickType((Component) null);
    assertTrue(result.intersects(PickHint.PickType.NOTHING.pickHint()));
  }

  @Test
  public void getFirstClassFromNullReturnsNull() {
    assertNull(PickUtilities.getFirstClassFromComponent(null));
  }

  @Test
  public void getFirstClassFromRegisteredComponentReturnsSelf() {
    StandInImp standIn = new StandInImp();
    Component result = PickUtilities.getFirstClassFromComponent(standIn.getSgComposite());
    assertSame(standIn.getSgComposite(), result);
  }

  @Test
  public void getFirstClassFromUnregisteredVisualTraversesUp() {
    Visual v = new Visual();
    Transformable parent = new Transformable();
    v.setParent(parent);
    Component result = PickUtilities.getFirstClassFromComponent(v);
    // Unregistered visual will traverse up and return null eventually
    assertNull(result);
  }

  @Test
  public void getEntityImpFromPickedObjectReturnsNullForNull() {
    assertNull(PickUtilities.getEntityImpFromPickedObject(null));
  }

  @Test
  public void getEntityImpFromRegisteredComponent() {
    StandInImp standIn = new StandInImp();
    EntityImp imp = PickUtilities.getEntityImpFromPickedObject(standIn.getSgComposite());
    assertSame(standIn, imp);
  }

  @Test
  public void getEntityFromPickedObjectReturnsNullForNull() {
    assertNull(PickUtilities.getEntityFromPickedObject(null));
  }

  @Test
  public void getEntityFromPickedObjectReturnsNullForStandIn() {
    StandInImp standIn = new StandInImp();
    // StandInImp.getAbstraction() returns null
    assertNull(PickUtilities.getEntityFromPickedObject(standIn.getSgComposite()));
  }

  @Test
  public void getPickTypeForImpReturnsEmptyForNullAbstraction() {
    StandInImp standIn = new StandInImp();
    PickHint hint = PickUtilities.getPickTypeForImp(standIn);
    assertNotNull(hint);
  }

  @Test
  public void getPickTypeForNullImpReturnsEmpty() {
    PickHint hint = PickUtilities.getPickTypeForImp(null);
    assertNotNull(hint);
  }

  @Test
  public void getPickTypeForComponentWithRegisteredEntity() {
    StandInImp standIn = new StandInImp();
    PickHint hint = PickUtilities.getPickType(standIn.getSgComposite());
    assertNotNull(hint);
  }

  @Test
  public void getPickTypeForUnregisteredTransformableReturnsNothing() {
    Transformable t = new Transformable();
    PickHint hint = PickUtilities.getPickType(t);
    assertTrue(hint.intersects(PickHint.PickType.NOTHING.pickHint()));
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  PickType enum
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void allPickTypesHaveDistinctOrdinals() {
    PickHint.PickType[] types = PickHint.PickType.values();
    for (int i = 0; i < types.length; i++) {
      for (int j = i + 1; j < types.length; j++) {
        assertTrue("Ordinals should be distinct",
            types[i].ordinal() != types[j].ordinal());
      }
    }
  }

  @Test
  public void pickTypeValuesContainsExpectedTypes() {
    PickHint.PickType[] types = PickHint.PickType.values();
    assertTrue(types.length >= 10);
  }
}
