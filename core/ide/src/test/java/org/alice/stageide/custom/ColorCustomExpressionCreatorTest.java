package org.alice.stageide.custom;

import org.alice.ide.custom.CustomExpressionCreatorComposite;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Tests for {@link ColorCustomExpressionCreatorComposite} —
 * singleton, hierarchy, and API surface.
 */
public class ColorCustomExpressionCreatorTest {

  // ---- class structure --------------------------------------------------

  @Test
  public void classIsLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.custom.ColorCustomExpressionCreatorComposite");
  }

  @Test
  public void extendsCustomExpressionCreatorComposite() {
    assertTrue(CustomExpressionCreatorComposite.class
        .isAssignableFrom(ColorCustomExpressionCreatorComposite.class));
  }

  @Test
  public void classIsNotAbstract() {
    assertFalse(Modifier.isAbstract(
        ColorCustomExpressionCreatorComposite.class.getModifiers()));
  }

  // ---- singleton accessor -----------------------------------------------

  @Test
  public void getInstanceMethod_exists() throws NoSuchMethodException {
    Method m = ColorCustomExpressionCreatorComposite.class.getMethod("getInstance");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertEquals(ColorCustomExpressionCreatorComposite.class, m.getReturnType());
  }

  // ---- constructor is private -------------------------------------------

  @Test
  public void constructor_isPrivate() throws Exception {
    for (var ctor : ColorCustomExpressionCreatorComposite.class.getDeclaredConstructors()) {
      if (ctor.getParameterCount() == 0) {
        assertTrue("no-arg ctor must be private", Modifier.isPrivate(ctor.getModifiers()));
      }
    }
  }

  // ---- overridden methods -----------------------------------------------

  @Test
  public void createViewMethod_isDeclared() throws NoSuchMethodException {
    Method m = ColorCustomExpressionCreatorComposite.class.getDeclaredMethod("createView");
    assertNotNull(m);
  }

  @Test
  public void createValueMethod_isDeclared() throws NoSuchMethodException {
    Method m = ColorCustomExpressionCreatorComposite.class.getDeclaredMethod("createValue");
    assertNotNull(m);
  }

  @Test
  public void getStatusPreRejectorCheckMethod_isDeclared() throws NoSuchMethodException {
    Method m = ColorCustomExpressionCreatorComposite.class.getDeclaredMethod("getStatusPreRejectorCheck");
    assertNotNull(m);
  }

  @Test
  public void initializeToPreviousExpressionMethod_isDeclared() throws NoSuchMethodException {
    Method m = ColorCustomExpressionCreatorComposite.class.getDeclaredMethod(
        "initializeToPreviousExpression", org.lgna.project.ast.Expression.class);
    assertNotNull(m);
  }

  @Test
  public void handlePreShowDialogMethod_isDeclared() throws NoSuchMethodException {
    Method m = ColorCustomExpressionCreatorComposite.class.getDeclaredMethod(
        "handlePreShowDialog", org.lgna.croquet.views.Dialog.class);
    assertNotNull(m);
  }

  @Test
  public void handlePostHideDialogMethod_isDeclared() throws NoSuchMethodException {
    Method m = ColorCustomExpressionCreatorComposite.class.getDeclaredMethod("handlePostHideDialog");
    assertNotNull(m);
  }
}
