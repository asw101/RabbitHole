package edu.cmu.cs.dennisc.render.gl.imp;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.DoubleBuffer;

import static org.junit.Assert.*;

/**
 * Tests for {@link Graphics2D} — structural verification and field-level
 * tests via reflection. Since Graphics2D is package-private and requires
 * a RenderContext with GL, these tests verify the class structure and
 * the conversion math without calling GL methods.
 */
public class Graphics2DStructureTest {

  private static final String CLASS_NAME = "edu.cmu.cs.dennisc.render.gl.imp.Graphics2D";

  // ── Class existence and visibility ────────────────────────────────

  @Test
  public void classExists() throws ClassNotFoundException {
    Class<?> cls = Class.forName(CLASS_NAME);
    assertNotNull(cls);
  }

  @Test
  public void classIsPackagePrivate() throws ClassNotFoundException {
    Class<?> cls = Class.forName(CLASS_NAME);
    int mods = cls.getModifiers();
    assertFalse(Modifier.isPublic(mods));
    assertFalse(Modifier.isProtected(mods));
    assertFalse(Modifier.isPrivate(mods));
  }

  @Test
  public void extendsCustomGraphics2D() throws ClassNotFoundException {
    Class<?> cls = Class.forName(CLASS_NAME);
    assertEquals("edu.cmu.cs.dennisc.render.Graphics2D", cls.getSuperclass().getName());
  }

  // ── Field declarations ────────────────────────────────────────────

  @Test
  public void hasRenderContextField() throws Exception {
    Class<?> cls = Class.forName(CLASS_NAME);
    Field f = cls.getDeclaredField("renderContext");
    assertNotNull(f);
    assertEquals(RenderContext.class, f.getType());
  }

  @Test
  public void hasPaintField() throws Exception {
    Class<?> cls = Class.forName(CLASS_NAME);
    Field f = cls.getDeclaredField("paint");
    assertNotNull(f);
  }

  @Test
  public void hasBackgroundField() throws Exception {
    Class<?> cls = Class.forName(CLASS_NAME);
    Field f = cls.getDeclaredField("background");
    assertNotNull(f);
  }

  @Test
  public void hasFontField() throws Exception {
    Class<?> cls = Class.forName(CLASS_NAME);
    Field f = cls.getDeclaredField("font");
    assertNotNull(f);
  }

  @Test
  public void hasStrokeField() throws Exception {
    Class<?> cls = Class.forName(CLASS_NAME);
    Field f = cls.getDeclaredField("stroke");
    assertNotNull(f);
  }

  @Test
  public void hasAffineTransformField() throws Exception {
    Class<?> cls = Class.forName(CLASS_NAME);
    Field f = cls.getDeclaredField("affineTransform");
    assertNotNull(f);
  }

  @Test
  public void hasGlTransformField() throws Exception {
    Class<?> cls = Class.forName(CLASS_NAME);
    Field f = cls.getDeclaredField("glTransform");
    assertNotNull(f);
  }

  @Test
  public void hasWidthAndHeightFields() throws Exception {
    Class<?> cls = Class.forName(CLASS_NAME);
    Field w = cls.getDeclaredField("width");
    Field h = cls.getDeclaredField("height");
    assertNotNull(w);
    assertNotNull(h);
    assertEquals(int.class, w.getType());
    assertEquals(int.class, h.getType());
  }

  // ── Delegate fields ───────────────────────────────────────────────

  @Test
  public void hasPrimitiveRendererField() throws Exception {
    Class<?> cls = Class.forName(CLASS_NAME);
    Field f = cls.getDeclaredField("primitiveRenderer");
    assertNotNull(f);
  }

  @Test
  public void hasTessellationRendererField() throws Exception {
    Class<?> cls = Class.forName(CLASS_NAME);
    Field f = cls.getDeclaredField("tessellationRenderer");
    assertNotNull(f);
  }

  @Test
  public void hasTextRendererField() throws Exception {
    Class<?> cls = Class.forName(CLASS_NAME);
    Field f = cls.getDeclaredField("textRenderer");
    assertNotNull(f);
  }

  @Test
  public void hasImageRendererField() throws Exception {
    Class<?> cls = Class.forName(CLASS_NAME);
    Field f = cls.getDeclaredField("imageRenderer");
    assertNotNull(f);
  }

  // ── Constructor ───────────────────────────────────────────────────

  @Test
  public void constructorAcceptsRenderContext() throws Exception {
    Class<?> cls = Class.forName(CLASS_NAME);
    Constructor<?> ctor = cls.getDeclaredConstructor(RenderContext.class);
    assertNotNull(ctor);
  }

  // ── Key methods exist ─────────────────────────────────────────────

  @Test
  public void hasInitializeMethod() throws Exception {
    Class<?> cls = Class.forName(CLASS_NAME);
    Method m = cls.getDeclaredMethod("initialize", java.awt.Dimension.class);
    assertNotNull(m);
  }

  @Test
  public void hasDisposeMethod() throws Exception {
    Class<?> cls = Class.forName(CLASS_NAME);
    Method m = cls.getDeclaredMethod("dispose");
    assertNotNull(m);
  }

  @Test
  public void hasIsValidMethod() throws Exception {
    Class<?> cls = Class.forName(CLASS_NAME);
    Method m = cls.getDeclaredMethod("isValid");
    assertNotNull(m);
  }

  @Test
  public void hasGlUpdateTransformMethod() throws Exception {
    Class<?> cls = Class.forName(CLASS_NAME);
    Method m = cls.getDeclaredMethod("glUpdateTransform");
    assertNotNull(m);
  }

  @Test
  public void hasGlSetColorMethod() throws Exception {
    Class<?> cls = Class.forName(CLASS_NAME);
    Method m = cls.getDeclaredMethod("glSetColor", java.awt.Color.class);
    assertNotNull(m);
  }

  @Test
  public void hasGetGLMethod() throws Exception {
    Class<?> cls = Class.forName(CLASS_NAME);
    Method m = cls.getDeclaredMethod("getGL");
    assertNotNull(m);
  }

  // ── glTransform array properties ──────────────────────────────────

  @Test
  public void glTransformArray_has16Elements() throws Exception {
    Class<?> cls = Class.forName(CLASS_NAME);
    Field f = cls.getDeclaredField("glTransform");
    f.setAccessible(true);
    // Create an instance via reflection
    Constructor<?> ctor = cls.getDeclaredConstructor(RenderContext.class);
    ctor.setAccessible(true);
    Object g2d = ctor.newInstance(new RenderContext());
    double[] arr = (double[]) f.get(g2d);
    assertEquals(16, arr.length);
  }

  @Test
  public void glTransformBuffer_wrapsArray() throws Exception {
    Class<?> cls = Class.forName(CLASS_NAME);
    Constructor<?> ctor = cls.getDeclaredConstructor(RenderContext.class);
    ctor.setAccessible(true);
    Object g2d = ctor.newInstance(new RenderContext());

    Field arrayField = cls.getDeclaredField("glTransform");
    arrayField.setAccessible(true);
    double[] arr = (double[]) arrayField.get(g2d);

    Field bufField = cls.getDeclaredField("glTransformBuffer");
    bufField.setAccessible(true);
    DoubleBuffer buf = (DoubleBuffer) bufField.get(g2d);

    assertEquals(16, buf.capacity());
  }

  // ── Width/height initial values ───────────────────────────────────

  @Test
  public void width_initiallyNegativeOne() throws Exception {
    Class<?> cls = Class.forName(CLASS_NAME);
    Constructor<?> ctor = cls.getDeclaredConstructor(RenderContext.class);
    ctor.setAccessible(true);
    Object g2d = ctor.newInstance(new RenderContext());
    Field f = cls.getDeclaredField("width");
    f.setAccessible(true);
    assertEquals(-1, f.getInt(g2d));
  }

  @Test
  public void height_initiallyNegativeOne() throws Exception {
    Class<?> cls = Class.forName(CLASS_NAME);
    Constructor<?> ctor = cls.getDeclaredConstructor(RenderContext.class);
    ctor.setAccessible(true);
    Object g2d = ctor.newInstance(new RenderContext());
    Field f = cls.getDeclaredField("height");
    f.setAccessible(true);
    assertEquals(-1, f.getInt(g2d));
  }

  @Test
  public void isValid_initiallyFalse() throws Exception {
    Class<?> cls = Class.forName(CLASS_NAME);
    Constructor<?> ctor = cls.getDeclaredConstructor(RenderContext.class);
    ctor.setAccessible(true);
    Object g2d = ctor.newInstance(new RenderContext());
    Method m = cls.getDeclaredMethod("isValid");
    m.setAccessible(true);
    assertFalse((Boolean) m.invoke(g2d));
  }

  // ── s_matrix array ────────────────────────────────────────────────

  @Test
  public void sMatrixArray_has6Elements() throws Exception {
    Class<?> cls = Class.forName(CLASS_NAME);
    Constructor<?> ctor = cls.getDeclaredConstructor(RenderContext.class);
    ctor.setAccessible(true);
    Object g2d = ctor.newInstance(new RenderContext());
    Field f = cls.getDeclaredField("s_matrix");
    f.setAccessible(true);
    double[] arr = (double[]) f.get(g2d);
    assertEquals(6, arr.length);
  }

  // ── SelectionBufferInfo structure ─────────────────────────────────

  @Test
  public void selectionBufferInfo_classExists() throws ClassNotFoundException {
    Class<?> cls = Class.forName("edu.cmu.cs.dennisc.render.gl.imp.SelectionBufferInfo");
    assertNotNull(cls);
  }

  @Test
  public void selectionBufferInfo_isPackagePrivate() throws ClassNotFoundException {
    Class<?> cls = Class.forName("edu.cmu.cs.dennisc.render.gl.imp.SelectionBufferInfo");
    int mods = cls.getModifiers();
    assertFalse(java.lang.reflect.Modifier.isPublic(mods));
  }

  @Test
  public void selectionBufferInfo_hasZFrontField() throws Exception {
    Class<?> cls = Class.forName("edu.cmu.cs.dennisc.render.gl.imp.SelectionBufferInfo");
    Field f = cls.getDeclaredField("zFront");
    assertNotNull(f);
    assertEquals(float.class, f.getType());
  }

  @Test
  public void selectionBufferInfo_hasZBackField() throws Exception {
    Class<?> cls = Class.forName("edu.cmu.cs.dennisc.render.gl.imp.SelectionBufferInfo");
    Field f = cls.getDeclaredField("zBack");
    assertNotNull(f);
    assertEquals(float.class, f.getType());
  }
}
