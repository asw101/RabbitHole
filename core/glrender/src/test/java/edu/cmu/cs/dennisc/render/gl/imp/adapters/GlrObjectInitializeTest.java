package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * Tests for {@link GlrObject} — getOwner, toString, initialize,
 * and the abstract adapter hierarchy field defaults.
 */
public class GlrObjectInitializeTest {

  // ── getOwner ──────────────────────────────────────────────────────

  @Test
  public void getOwner_initiallyNull() {
    TestableObject obj = new TestableObject();
    assertNull(obj.getOwner());
  }

  // ── toString ──────────────────────────────────────────────────────

  @Test
  public void toString_nullOwner_usesDefault() {
    TestableObject obj = new TestableObject();
    String s = obj.toString();
    assertNotNull(s);
    // Default Object.toString() format
    assertTrue(s.contains("@") || s.contains("GlrObject"));
  }

  // ── owner field ───────────────────────────────────────────────────

  @Test
  public void owner_field_accessible() throws Exception {
    Field f = GlrObject.class.getDeclaredField("owner");
    f.setAccessible(true);
    TestableObject obj = new TestableObject();
    assertNull(f.get(obj));
  }

  // ── GlrElement propertyChanged fallback ───────────────────────────

  @Test
  public void glrElement_propertyChanged_noException() throws Exception {
    // The base propertyChanged in GlrElement just logs, should not throw
    TestableElement elem = new TestableElement();
    java.lang.reflect.Method m = GlrElement.class.getDeclaredMethod(
        "propertyChanged", edu.cmu.cs.dennisc.property.InstanceProperty.class);
    m.setAccessible(true);
    m.invoke(elem, (Object) null);
  }

  // ── GlrScalable default state ─────────────────────────────────────

  @Test
  public void glrScalable_x_defaultNaN() throws Exception {
    GlrScalable scalable = new GlrScalable();
    Field f = GlrScalable.class.getDeclaredField("x");
    f.setAccessible(true);
    assertTrue(Double.isNaN(f.getDouble(scalable)));
  }

  @Test
  public void glrScalable_y_defaultNaN() throws Exception {
    GlrScalable scalable = new GlrScalable();
    Field f = GlrScalable.class.getDeclaredField("y");
    f.setAccessible(true);
    assertTrue(Double.isNaN(f.getDouble(scalable)));
  }

  @Test
  public void glrScalable_z_defaultNaN() throws Exception {
    GlrScalable scalable = new GlrScalable();
    Field f = GlrScalable.class.getDeclaredField("z");
    f.setAccessible(true);
    assertTrue(Double.isNaN(f.getDouble(scalable)));
  }

  @Test
  public void glrScalable_isIdentity_defaultFalse() throws Exception {
    GlrScalable scalable = new GlrScalable();
    Field f = GlrScalable.class.getDeclaredField("isIdentity");
    f.setAccessible(true);
    assertFalse(f.getBoolean(scalable));
  }

  // ── GlrScene default state ────────────────────────────────────────

  @Test
  public void glrScene_backgroundAdapter_initiallyNull() throws Exception {
    GlrScene scene = new GlrScene();
    Field f = GlrScene.class.getDeclaredField("backgroundAdapter");
    f.setAccessible(true);
    assertNull(f.get(scene));
  }

  @Test
  public void glrScene_getBackgroundAdapter_initiallyNull() {
    GlrScene scene = new GlrScene();
    assertNull(scene.getBackgroundAdapter());
  }

  @Test
  public void glrScene_globalBrightness_defaultZero() throws Exception {
    GlrScene scene = new GlrScene();
    Field f = GlrScene.class.getDeclaredField("globalBrightness");
    f.setAccessible(true);
    assertEquals(0.0f, f.getFloat(scene), 0.0001f);
  }

  @Test
  public void glrScene_ghostDescendants_initiallyEmpty() throws Exception {
    GlrScene scene = new GlrScene();
    Field f = GlrScene.class.getDeclaredField("glrGhostDescendants");
    f.setAccessible(true);
    java.util.List<?> list = (java.util.List<?>) f.get(scene);
    assertTrue(list.isEmpty());
  }

  @Test
  public void glrScene_visualDescendants_initiallyEmpty() throws Exception {
    GlrScene scene = new GlrScene();
    Field f = GlrScene.class.getDeclaredField("glrVisualDescendants");
    f.setAccessible(true);
    java.util.List<?> list = (java.util.List<?>) f.get(scene);
    assertTrue(list.isEmpty());
  }

  @Test
  public void glrScene_planarReflectorDescendants_initiallyEmpty() throws Exception {
    GlrScene scene = new GlrScene();
    Field f = GlrScene.class.getDeclaredField("glrPlanarReflectorDescendants");
    f.setAccessible(true);
    java.util.List<?> list = (java.util.List<?>) f.get(scene);
    assertTrue(list.isEmpty());
  }

  // ── GlrComponent absolute/inverseAbsolute buffers ─────────────────

  @Test
  public void glrComponent_absoluteBuffer_hasLength16() throws Exception {
    TestableComponent comp = new TestableComponent();
    Field f = GlrComponent.class.getDeclaredField("absolute");
    f.setAccessible(true);
    double[] arr = (double[]) f.get(comp);
    assertEquals(16, arr.length);
  }

  @Test
  public void glrComponent_inverseAbsoluteBuffer_hasLength16() throws Exception {
    TestableComponent comp = new TestableComponent();
    Field f = GlrComponent.class.getDeclaredField("inverseAbsolute");
    f.setAccessible(true);
    double[] arr = (double[]) f.get(comp);
    assertEquals(16, arr.length);
  }

  @Test
  public void glrComponent_absolute_initiallyNaN() throws Exception {
    TestableComponent comp = new TestableComponent();
    Field f = GlrComponent.class.getDeclaredField("absolute");
    f.setAccessible(true);
    double[] arr = (double[]) f.get(comp);
    assertTrue("First element should be NaN (marks as dirty)", Double.isNaN(arr[0]));
  }

  @Test
  public void glrComponent_inverseAbsolute_initiallyNaN() throws Exception {
    TestableComponent comp = new TestableComponent();
    Field f = GlrComponent.class.getDeclaredField("inverseAbsolute");
    f.setAccessible(true);
    double[] arr = (double[]) f.get(comp);
    assertTrue("First element should be NaN (marks as dirty)", Double.isNaN(arr[0]));
  }

  @Test
  public void glrComponent_absoluteBuffer_notNull() throws Exception {
    TestableComponent comp = new TestableComponent();
    Field f = GlrComponent.class.getDeclaredField("absoluteBuffer");
    f.setAccessible(true);
    assertNotNull(f.get(comp));
  }

  @Test
  public void glrComponent_inverseAbsoluteBuffer_notNull() throws Exception {
    TestableComponent comp = new TestableComponent();
    Field f = GlrComponent.class.getDeclaredField("inverseAbsoluteBuffer");
    f.setAccessible(true);
    assertNotNull(f.get(comp));
  }

  // ── GlrComposite children list ────────────────────────────────────

  @Test
  public void glrComposite_children_initiallyEmpty() throws Exception {
    GlrScene scene = new GlrScene();
    java.util.List<?> children = (java.util.List<?>) getPrivateField(GlrComposite.class, "glrChildren", scene);
    assertTrue(children.isEmpty());
  }

  @Test
  public void glrComposite_accessChildren_initiallyEmpty() {
    GlrScene scene = new GlrScene();
    Iterable<?> children = scene.accessChildren();
    assertNotNull(children);
    assertFalse(children.iterator().hasNext());
  }

  // ── Helpers ───────────────────────────────────────────────────────

  @SuppressWarnings("rawtypes")
  private static class TestableObject extends GlrObject {
  }

  @SuppressWarnings("rawtypes")
  private static class TestableElement extends GlrElement {
  }

  @SuppressWarnings("rawtypes")
  private static class TestableComponent extends GlrComponent {
    @Override
    public void accept(edu.cmu.cs.dennisc.pattern.Visitor visitor) {
      // no-op
    }
  }

  private static Object getPrivateField(Class<?> clazz, String name, Object obj) throws Exception {
    Field f = clazz.getDeclaredField(name);
    f.setAccessible(true);
    return f.get(obj);
  }
}
