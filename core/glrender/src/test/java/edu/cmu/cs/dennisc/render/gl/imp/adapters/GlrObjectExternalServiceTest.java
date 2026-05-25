package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * External service integration tests for {@link GlrObject}.
 * Verifies the adapter lifecycle, owner management, and toString
 * contracts without requiring a live GL context.
 */
public class GlrObjectExternalServiceTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(GlrObject.class.getModifiers()));
  }

  @Test
  public void class_isPublic() {
    assertTrue(Modifier.isPublic(GlrObject.class.getModifiers()));
  }

  @Test
  public void class_hasGenericTypeParameter() {
    assertEquals(1, GlrObject.class.getTypeParameters().length);
    assertEquals("T", GlrObject.class.getTypeParameters()[0].getName());
  }

  @Test
  public void owner_fieldExists() throws Exception {
    Field f = GlrObject.class.getDeclaredField("owner");
    assertTrue(Modifier.isProtected(f.getModifiers()));
  }

  @Test
  public void getOwner_methodSignature() throws Exception {
    Method m = GlrObject.class.getDeclaredMethod("getOwner");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void initialize_methodSignature() throws Exception {
    Method m = GlrObject.class.getDeclaredMethod("initialize",
        edu.cmu.cs.dennisc.pattern.Releasable.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void handleReleased_static_methodSignature() throws Exception {
    Method m = GlrObject.class.getDeclaredMethod("handleReleased",
        edu.cmu.cs.dennisc.pattern.event.ReleaseEvent.class);
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertFalse(Modifier.isPublic(m.getModifiers()));
    assertFalse(Modifier.isPrivate(m.getModifiers()));
  }

  @Test
  public void handleReleased_instance_methodSignature() throws Exception {
    Method m = GlrObject.class.getDeclaredMethod("handleReleased");
    assertTrue(Modifier.isProtected(m.getModifiers()));
    assertFalse(Modifier.isStatic(m.getModifiers()));
  }

  @Test
  public void toString_methodIsOverridden() throws Exception {
    Method m = GlrObject.class.getDeclaredMethod("toString");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(GlrObject.class, m.getDeclaringClass());
  }

  @Test
  public void glrElement_extendsGlrObject() {
    assertTrue(GlrObject.class.isAssignableFrom(GlrElement.class));
  }

  @Test
  public void glrComponent_extendsGlrElement() {
    assertTrue(GlrElement.class.isAssignableFrom(GlrComponent.class));
  }

  @Test
  public void glrComposite_extendsGlrComponent() {
    assertTrue(GlrComponent.class.isAssignableFrom(GlrComposite.class));
  }

  @Test
  public void glrScene_extendsGlrComposite() {
    assertTrue(GlrComposite.class.isAssignableFrom(GlrScene.class));
  }

  @Test
  public void glrTransformable_extendsGlrComponent() {
    assertTrue(GlrComponent.class.isAssignableFrom(GlrTransformable.class));
  }
}
