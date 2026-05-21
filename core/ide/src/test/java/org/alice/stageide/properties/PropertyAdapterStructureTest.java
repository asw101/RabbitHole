package org.alice.stageide.properties;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Structural tests for stageide property adapter classes.
 * Validates class hierarchy, method signatures, and accessibility.
 */
public class PropertyAdapterStructureTest {

  // ---- ModelSizeAdapter ----

  @Test
  public void modelSizeAdapter_classIsAccessible() {
    assertNotNull(ModelSizeAdapter.class);
  }

  @Test
  public void modelSizeAdapter_isPublic() {
    assertTrue(Modifier.isPublic(ModelSizeAdapter.class.getModifiers()));
  }

  @Test
  public void modelSizeAdapter_isNotAbstract() {
    assertFalse(Modifier.isAbstract(ModelSizeAdapter.class.getModifiers()));
  }

  @Test
  public void modelSizeAdapter_hasGetValueMethod() throws Exception {
    Method m = ModelSizeAdapter.class.getMethod("getValue");
    assertNotNull(m);
  }

  @Test
  public void modelSizeAdapter_hasSetValueMethod() throws Exception {
    Method m = ModelSizeAdapter.class.getMethod("setValue", Object.class);
    assertNotNull(m);
  }

  @Test
  public void modelSizeAdapter_hasConstructor() throws Exception {
    Constructor<?>[] ctors = ModelSizeAdapter.class.getDeclaredConstructors();
    assertTrue(ctors.length > 0);
  }

  // ---- ModelOpacityAdapter ----

  @Test
  public void modelOpacityAdapter_classIsAccessible() {
    assertNotNull(ModelOpacityAdapter.class);
  }

  @Test
  public void modelOpacityAdapter_isPublic() {
    assertTrue(Modifier.isPublic(ModelOpacityAdapter.class.getModifiers()));
  }

  @Test
  public void modelOpacityAdapter_isNotAbstract() {
    assertFalse(Modifier.isAbstract(ModelOpacityAdapter.class.getModifiers()));
  }

  @Test
  public void modelOpacityAdapter_hasGetValueMethod() throws Exception {
    Method m = ModelOpacityAdapter.class.getMethod("getValue");
    assertNotNull(m);
  }

  // ---- GroundOpacityAdapter ----

  @Test
  public void groundOpacityAdapter_classIsAccessible() {
    assertNotNull(GroundOpacityAdapter.class);
  }

  @Test
  public void groundOpacityAdapter_isPublic() {
    assertTrue(Modifier.isPublic(GroundOpacityAdapter.class.getModifiers()));
  }

  @Test
  public void groundOpacityAdapter_isNotAbstract() {
    assertFalse(Modifier.isAbstract(GroundOpacityAdapter.class.getModifiers()));
  }

  // ---- PaintPropertyAdapter ----

  @Test
  public void paintPropertyAdapter_classIsAccessible() {
    assertNotNull(PaintPropertyAdapter.class);
  }

  @Test
  public void paintPropertyAdapter_isPublic() {
    assertTrue(Modifier.isPublic(PaintPropertyAdapter.class.getModifiers()));
  }

  // ---- BillboardBackPaintPropertyAdapter ----

  @Test
  public void billboardBackPaintPropertyAdapter_classIsAccessible() {
    assertNotNull(BillboardBackPaintPropertyAdapter.class);
  }

  @Test
  public void billboardBackPaintPropertyAdapter_isPublic() {
    assertTrue(Modifier.isPublic(BillboardBackPaintPropertyAdapter.class.getModifiers()));
  }

  // ---- BillboardFrontPaintPropertyAdapter ----

  @Test
  public void billboardFrontPaintPropertyAdapter_classIsAccessible() {
    assertNotNull(BillboardFrontPaintPropertyAdapter.class);
  }

  @Test
  public void billboardFrontPaintPropertyAdapter_isPublic() {
    assertTrue(Modifier.isPublic(BillboardFrontPaintPropertyAdapter.class.getModifiers()));
  }

  // ---- MoveableTurnableTranslationAdapter ----

  @Test
  public void moveableTurnableTranslationAdapter_classIsAccessible() {
    assertNotNull(MoveableTurnableTranslationAdapter.class);
  }

  @Test
  public void moveableTurnableTranslationAdapter_isPublic() {
    assertTrue(Modifier.isPublic(MoveableTurnableTranslationAdapter.class.getModifiers()));
  }

  // ---- MutableRiderVehicleAdapter ----

  @Test
  public void mutableRiderVehicleAdapter_classIsAccessible() {
    assertNotNull(MutableRiderVehicleAdapter.class);
  }

  @Test
  public void mutableRiderVehicleAdapter_isPublic() {
    assertTrue(Modifier.isPublic(MutableRiderVehicleAdapter.class.getModifiers()));
  }

  // ---- TextValuePropertyAdapter ----

  @Test
  public void textValuePropertyAdapter_classIsAccessible() {
    assertNotNull(TextValuePropertyAdapter.class);
  }

  @Test
  public void textValuePropertyAdapter_isPublic() {
    assertTrue(Modifier.isPublic(TextValuePropertyAdapter.class.getModifiers()));
  }

  // ---- TextFontPropertyAdapter ----

  @Test
  public void textFontPropertyAdapter_classIsAccessible() {
    assertNotNull(TextFontPropertyAdapter.class);
  }

  @Test
  public void textFontPropertyAdapter_isPublic() {
    assertTrue(Modifier.isPublic(TextFontPropertyAdapter.class.getModifiers()));
  }

  // ---- ResourcePropertyAdapter ----

  @Test
  public void resourcePropertyAdapter_classIsAccessible() {
    assertNotNull(ResourcePropertyAdapter.class);
  }

  @Test
  public void resourcePropertyAdapter_isPublic() {
    assertTrue(Modifier.isPublic(ResourcePropertyAdapter.class.getModifiers()));
  }
}
