package org.alice.ide.javacode.croquet;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Tests for Java code generation composites and views.
 * Covers JavaCodeFrameComposite structure and JavaCodeView via reflection.
 */
public class JavaCodeUtilitiesTest {

  // -- JavaCodeFrameComposite structural tests ----------------------------

  @Test
  public void javaCodeFrameComposite_classLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.ide.javacode.croquet.JavaCodeFrameComposite");
  }

  @Test
  public void javaCodeFrameComposite_hasGetInstance() throws Exception {
    Class<?> cls = Class.forName("org.alice.ide.javacode.croquet.JavaCodeFrameComposite");
    Method m = cls.getMethod("getInstance");
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void javaCodeFrameComposite_extendsFrameComposite() throws Exception {
    Class<?> cls = Class.forName("org.alice.ide.javacode.croquet.JavaCodeFrameComposite");
    Class<?> superCls = cls.getSuperclass();
    assertNotNull(superCls);
    assertEquals("FrameCompositeWithInternalIsShowingState", superCls.getSimpleName());
  }

  // -- JavaCodeView structural tests --------------------------------------

  @Test
  public void javaCodeView_classLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.ide.javacode.croquet.views.JavaCodeView");
  }

  @Test
  public void javaCodeView_hasSetDeclarationMethod() throws Exception {
    Class<?> cls = Class.forName("org.alice.ide.javacode.croquet.views.JavaCodeView");
    Method m = cls.getMethod("setDeclaration", org.lgna.project.ast.AbstractDeclaration.class);
    assertNotNull(m);
  }
}
