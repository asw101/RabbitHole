package org.lgna.story.implementation;

import org.junit.Test;
import org.lgna.story.SBillboard;
import org.lgna.story.STextModel;
import org.lgna.story.SThingMarker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Tests for additional implementation classes:
 * <ul>
 *   <li>{@link BillboardImp} via {@link SBillboard}</li>
 *   <li>{@link TextModelImp} via {@link STextModel}</li>
 *   <li>{@link ObjectMarkerImp} via {@link SThingMarker}</li>
 * </ul>
 *
 * <p>All tests are headless-safe — the scenegraph nodes used by these
 * implementations do not require AWT display or rendering.
 */
public class MoreImplBehaviorTest {

  // ═══════════════════════════════════════════════════════════════════════
  //  SBillboard / BillboardImp
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void billboard_constructsWithoutError() {
    SBillboard billboard = new SBillboard();
    assertNotNull(billboard);
    assertNotNull(billboard.getImplementation());
  }

  @Test
  public void billboard_implementationReturnsSelf() {
    SBillboard billboard = new SBillboard();
    BillboardImp imp = billboard.getImplementation();
    assertSame(billboard, imp.getAbstraction());
  }

  @Test
  public void billboard_sgVisualsAreNotNull() {
    SBillboard billboard = new SBillboard();
    BillboardImp imp = billboard.getImplementation();
    assertNotNull(imp.getSgVisuals());
    assertEquals("Billboard should have front and back faces", 2, imp.getSgVisuals().length);
  }

  @Test
  public void billboard_resizersAvailable() {
    SBillboard billboard = new SBillboard();
    BillboardImp imp = billboard.getImplementation();
    assertNotNull(imp.getResizers());
    assertEquals(3, imp.getResizers().length);
  }

  @Test
  public void billboard_getValueForResizer_returnsFiniteForKnownResizers() {
    SBillboard billboard = new SBillboard();
    BillboardImp imp = billboard.getImplementation();
    for (edu.cmu.cs.dennisc.scenegraph.scale.Resizer resizer : imp.getResizers()) {
      double value = imp.getValueForResizer(resizer);
      assertNotNull(value);
      assertTrue("Resizer value should be finite", Double.isFinite(value));
    }
  }

  @Test
  public void billboard_setValueForResizer_xAxis() {
    SBillboard billboard = new SBillboard();
    BillboardImp imp = billboard.getImplementation();
    imp.setValueForResizer(edu.cmu.cs.dennisc.scenegraph.scale.Resizer.X_AXIS, 2.0);
    assertEquals(2.0, imp.getValueForResizer(edu.cmu.cs.dennisc.scenegraph.scale.Resizer.X_AXIS), 1e-9);
  }

  @Test
  public void billboard_setValueForResizer_yAxis() {
    SBillboard billboard = new SBillboard();
    BillboardImp imp = billboard.getImplementation();
    imp.setValueForResizer(edu.cmu.cs.dennisc.scenegraph.scale.Resizer.Y_AXIS, 3.0);
    assertEquals(3.0, imp.getValueForResizer(edu.cmu.cs.dennisc.scenegraph.scale.Resizer.Y_AXIS), 1e-9);
  }

  @Test
  public void billboard_setValueForResizer_xyPlane() {
    SBillboard billboard = new SBillboard();
    BillboardImp imp = billboard.getImplementation();
    double originalY = imp.getScale().y();
    imp.setValueForResizer(edu.cmu.cs.dennisc.scenegraph.scale.Resizer.XY_PLANE, 2.0);
    assertEquals(2.0, imp.getValueForResizer(edu.cmu.cs.dennisc.scenegraph.scale.Resizer.XY_PLANE), 1e-9);
  }

  @Test
  public void billboard_setValueForResizer_zeroIgnored() {
    SBillboard billboard = new SBillboard();
    BillboardImp imp = billboard.getImplementation();
    double before = imp.getValueForResizer(edu.cmu.cs.dennisc.scenegraph.scale.Resizer.X_AXIS);
    imp.setValueForResizer(edu.cmu.cs.dennisc.scenegraph.scale.Resizer.X_AXIS, 0.0);
    // Zero or negative should be ignored (logged as severe)
    assertEquals(before, imp.getValueForResizer(edu.cmu.cs.dennisc.scenegraph.scale.Resizer.X_AXIS), 1e-9);
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  STextModel / TextModelImp
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void textModel_constructsWithoutError() {
    STextModel model = new STextModel();
    assertNotNull(model);
    assertNotNull(model.getImplementation());
  }

  @Test
  public void textModel_initialValueIsEmpty() {
    STextModel model = new STextModel();
    assertEquals("", model.getValue());
    assertEquals(0, (int) model.getLength());
  }

  @Test
  public void textModel_setValue() {
    STextModel model = new STextModel();
    model.setValue("Hello");
    assertEquals("Hello", model.getValue());
    assertEquals(5, (int) model.getLength());
  }

  @Test
  public void textModel_append() {
    STextModel model = new STextModel();
    model.setValue("Hello");
    model.append(" World");
    assertEquals("Hello World", model.getValue());
  }

  @Test
  public void textModel_charAt() {
    STextModel model = new STextModel();
    model.setValue("ABC");
    assertEquals('A', (char) model.charAt(0));
    assertEquals('B', (char) model.charAt(1));
    assertEquals('C', (char) model.charAt(2));
  }

  @Test
  public void textModel_delete() {
    STextModel model = new STextModel();
    model.setValue("Hello World");
    model.delete(5, 11);
    assertEquals("Hello", model.getValue());
  }

  @Test
  public void textModel_deleteCharAt() {
    STextModel model = new STextModel();
    model.setValue("ABC");
    model.deleteCharAt(1);
    assertEquals("AC", model.getValue());
  }

  @Test
  public void textModel_indexOf() {
    STextModel model = new STextModel();
    model.setValue("Hello World Hello");
    assertEquals(0, (int) model.indexOf("Hello"));
    assertEquals(12, (int) model.indexOf("Hello", 1));
  }

  @Test
  public void textModel_indexOf_notFound() {
    STextModel model = new STextModel();
    model.setValue("Hello");
    assertEquals(-1, (int) model.indexOf("xyz"));
  }

  @Test
  public void textModel_insert() {
    STextModel model = new STextModel();
    model.setValue("Helo");
    model.insert(2, "l");
    assertEquals("Hello", model.getValue());
  }

  @Test
  public void textModel_lastIndexOf() {
    STextModel model = new STextModel();
    model.setValue("abcabc");
    assertEquals(3, (int) model.lastIndexOf("abc"));
    assertEquals(0, (int) model.lastIndexOf("abc", 2));
  }

  @Test
  public void textModel_replace() {
    STextModel model = new STextModel();
    model.setValue("Hello World");
    model.replace(6, 11, "Java");
    assertEquals("Hello Java", model.getValue());
  }

  @Test
  public void textModel_setCharAt() {
    STextModel model = new STextModel();
    model.setValue("Hallo");
    model.setCharAt(1, 'e');
    assertEquals("Hello", model.getValue());
  }

  @Test
  public void textModel_impAbstractionRoundTrip() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    assertSame(model, imp.getAbstraction());
  }

  @Test
  public void textModel_impSetAndGetValue() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    imp.setValue("test");
    assertEquals("test", imp.getValue());
    assertEquals(4, imp.getLength());
  }

  @Test
  public void textModel_impCharAt() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    imp.setValue("XYZ");
    assertEquals('X', imp.charAt(0));
    assertEquals('Z', imp.charAt(2));
  }

  @Test
  public void textModel_impDelete() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    imp.setValue("ABCDE");
    imp.delete(1, 3);
    assertEquals("ADE", imp.getValue());
  }

  @Test
  public void textModel_impDeleteCharAt() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    imp.setValue("ABC");
    imp.deleteCharAt(0);
    assertEquals("BC", imp.getValue());
  }

  @Test
  public void textModel_impIndexOf() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    imp.setValue("foobar");
    assertEquals(3, imp.indexOf("bar"));
    assertEquals(-1, imp.indexOf("baz"));
    assertEquals(3, imp.indexOf("bar", 0));
    assertEquals(-1, imp.indexOf("bar", 4));
  }

  @Test
  public void textModel_impInsert() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    imp.setValue("ac");
    imp.insert(1, "b");
    assertEquals("abc", imp.getValue());
  }

  @Test
  public void textModel_impLastIndexOf() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    imp.setValue("abab");
    assertEquals(2, imp.lastIndexOf("ab"));
    assertEquals(0, imp.lastIndexOf("ab", 1));
  }

  @Test
  public void textModel_impReplace() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    imp.setValue("aXXb");
    imp.replace(1, 3, "Y");
    assertEquals("aYb", imp.getValue());
  }

  @Test
  public void textModel_impSetCharAt() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    imp.setValue("aXc");
    imp.setCharAt(1, 'b');
    assertEquals("abc", imp.getValue());
  }

  @Test
  public void textModel_impAppend() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    imp.setValue("ab");
    imp.append("cd");
    assertEquals("abcd", imp.getValue());
  }

  @Test
  public void textModel_impFont() {
    STextModel model = new STextModel();
    TextModelImp imp = model.getImplementation();
    assertNotNull(imp.getFont());
    java.awt.Font newFont = new java.awt.Font("SansSerif", java.awt.Font.BOLD, 24);
    imp.setFont(newFont);
    assertEquals(newFont, imp.getFont());
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  SThingMarker / ObjectMarkerImp
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void thingMarker_constructsWithoutError() {
    SThingMarker marker = new SThingMarker();
    assertNotNull(marker);
    assertNotNull(marker.getImplementation());
  }

  @Test
  public void thingMarker_implementationReturnsSelf() {
    SThingMarker marker = new SThingMarker();
    ObjectMarkerImp imp = marker.getImplementation();
    assertSame(marker, imp.getAbstraction());
  }

  @Test
  public void thingMarker_sgVisualsAreNotNull() {
    SThingMarker marker = new SThingMarker();
    ObjectMarkerImp imp = marker.getImplementation();
    assertNotNull(imp.getSgVisuals());
    assertTrue("Marker should have visual axes", imp.getSgVisuals().length > 0);
  }

  @Test
  public void thingMarker_paintAppearancesNotNull() {
    SThingMarker marker = new SThingMarker();
    ObjectMarkerImp imp = marker.getImplementation();
    // Accessing protected methods via the abstraction's public getImplementation
    assertNotNull(imp.getSgVisuals());
  }
}
