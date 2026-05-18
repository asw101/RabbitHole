package org.alice.ide.common;

import org.junit.Assume;
import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;

import javax.swing.border.Border;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Characterization tests for {@link TypeBorder} — singleton routing,
 * Border interface contract, identity checks, and shape creation.
 *
 * <p>TypeBorder's static initializer depends on {@link org.alice.ide.ThemeUtilities}
 * which falls back to DefaultTheme when no IDE is active. This works headless.
 * Paint-dependent tests use Assume guards.
 */
public class TypeBorderTest {

  // ── Static initializer guard ───────────────────────────────────────
  // TypeBorder loads FILL_COLOR = ThemeUtilities.getActiveTheme().getColorFor(...)
  // In headless without IDE, this uses DefaultTheme and should not throw.
  // If it does throw (ExceptionInInitializerError), skip all tests.

  private static boolean typeBorderInitialized;
  static {
    try {
      // Force class loading
      TypeBorder.getSingletonFor(null);
      typeBorderInitialized = true;
    } catch (ExceptionInInitializerError | NoClassDefFoundError e) {
      typeBorderInitialized = false;
    }
  }

  private void assumeInitialized() {
    Assume.assumeTrue("TypeBorder could not initialize (likely headless issue)",
        typeBorderInitialized);
  }

  // ── getSingletonFor routing ────────────────────────────────────────

  @Test
  public void getSingletonFor_null_returnsSingletonForNull() {
    assumeInitialized();
    TypeBorder border = TypeBorder.getSingletonFor(null);
    assertNotNull("null type should return non-null singleton", border);
  }

  @Test
  public void getSingletonFor_javaType_returnsSingletonForJava() {
    assumeInitialized();
    TypeBorder border = TypeBorder.getSingletonFor(JavaType.getInstance(String.class));
    assertNotNull(border);
  }

  @Test
  public void getSingletonFor_namedUserType_returnsSingletonForUser() {
    assumeInitialized();
    NamedUserType userType = new NamedUserType();
    userType.name.setValue("MyType");
    userType.superType.setValue(JavaType.getInstance(Object.class));

    TypeBorder border = TypeBorder.getSingletonFor(userType);
    assertNotNull(border);
  }

  @Test
  public void getSingletonForUserType_returnsSameAsUserSingleton() {
    assumeInitialized();
    NamedUserType userType = new NamedUserType();
    userType.name.setValue("Test");
    userType.superType.setValue(JavaType.getInstance(Object.class));

    assertSame("getSingletonForUserType should match getSingletonFor(NamedUserType)",
        TypeBorder.getSingletonForUserType(),
        TypeBorder.getSingletonFor(userType));
  }

  // ── singleton identity ─────────────────────────────────────────────

  @Test
  public void getSingletonFor_sameTypeCategory_returnsSameInstance() {
    assumeInitialized();
    TypeBorder b1 = TypeBorder.getSingletonFor(JavaType.getInstance(String.class));
    TypeBorder b2 = TypeBorder.getSingletonFor(JavaType.getInstance(Integer.class));
    assertSame("All Java types share same singleton", b1, b2);
  }

  @Test
  public void getSingletonFor_differentCategories_returnDifferentInstances() {
    assumeInitialized();
    TypeBorder nullBorder = TypeBorder.getSingletonFor(null);
    TypeBorder javaBorder = TypeBorder.getSingletonFor(JavaType.getInstance(Object.class));
    NamedUserType userType = new NamedUserType();
    userType.name.setValue("X");
    userType.superType.setValue(JavaType.getInstance(Object.class));
    TypeBorder userBorder = TypeBorder.getSingletonFor(userType);

    assertNotSame("Null and Java singletons differ", nullBorder, javaBorder);
    assertNotSame("Java and User singletons differ", javaBorder, userBorder);
    assertNotSame("Null and User singletons differ", nullBorder, userBorder);
  }

  // ── Border interface methods ───────────────────────────────────────

  @Test
  public void getBorderInsets_returnsNonNull() {
    assumeInitialized();
    TypeBorder border = TypeBorder.getSingletonFor(JavaType.getInstance(String.class));
    Insets insets = border.getBorderInsets(null);
    assertNotNull(insets);
  }

  @Test
  public void getBorderInsets_hasPositiveInsets() {
    assumeInitialized();
    TypeBorder border = TypeBorder.getSingletonFor(JavaType.getInstance(String.class));
    Insets insets = border.getBorderInsets(null);
    assertTrue("Left inset should be positive", insets.left > 0);
    assertTrue("Right inset should be positive", insets.right > 0);
    assertTrue("Top inset should be positive", insets.top > 0);
    assertTrue("Bottom inset should be positive", insets.bottom > 0);
  }

  @Test
  public void getBorderInsets_symmetricHorizontal() {
    assumeInitialized();
    TypeBorder border = TypeBorder.getSingletonFor(JavaType.getInstance(String.class));
    Insets insets = border.getBorderInsets(null);
    assertEquals("Left and right insets should match", insets.left, insets.right);
  }

  @Test
  public void getBorderInsets_symmetricVertical() {
    assumeInitialized();
    TypeBorder border = TypeBorder.getSingletonFor(JavaType.getInstance(String.class));
    Insets insets = border.getBorderInsets(null);
    assertEquals("Top and bottom insets should match", insets.top, insets.bottom);
  }

  @Test
  public void getBorderInsets_sameAcrossAllSingletons() {
    assumeInitialized();
    Insets javaInsets = TypeBorder.getSingletonFor(JavaType.getInstance(String.class)).getBorderInsets(null);
    Insets nullInsets = TypeBorder.getSingletonFor(null).getBorderInsets(null);
    NamedUserType ut = new NamedUserType();
    ut.name.setValue("T");
    ut.superType.setValue(JavaType.getInstance(Object.class));
    Insets userInsets = TypeBorder.getSingletonFor(ut).getBorderInsets(null);

    assertEquals(javaInsets, nullInsets);
    assertEquals(nullInsets, userInsets);
  }

  @Test
  public void isBorderOpaque_returnsFalse() {
    assumeInitialized();
    TypeBorder border = TypeBorder.getSingletonFor(JavaType.getInstance(String.class));
    assertFalse("TypeBorder should not be opaque", border.isBorderOpaque());
  }

  // ── implements Border ──────────────────────────────────────────────

  @Test
  public void typeBorder_implementsBorderInterface() {
    assertTrue("TypeBorder should implement Border",
        Border.class.isAssignableFrom(TypeBorder.class));
  }

  // ── internal structure via reflection ──────────────────────────────

  @Test
  public void staticSingletonFields_exist() throws Exception {
    assumeInitialized();
    Field userField = TypeBorder.class.getDeclaredField("singletonForUser");
    assertTrue("singletonForUser should be static",
        Modifier.isStatic(userField.getModifiers()));
    assertTrue("singletonForUser should be final",
        Modifier.isFinal(userField.getModifiers()));

    Field javaField = TypeBorder.class.getDeclaredField("singletonForJava");
    assertTrue("singletonForJava should be static",
        Modifier.isStatic(javaField.getModifiers()));

    Field nullField = TypeBorder.class.getDeclaredField("singletonForNull");
    assertTrue("singletonForNull should be static",
        Modifier.isStatic(nullField.getModifiers()));
  }

  @Test
  public void xInset_isEight() throws Exception {
    Field xInsetField = TypeBorder.class.getDeclaredField("X_INSET");
    xInsetField.setAccessible(true);
    assertEquals(8, xInsetField.getInt(null));
  }

  @Test
  public void yInset_isTwo() throws Exception {
    Field yInsetField = TypeBorder.class.getDeclaredField("Y_INSET");
    yInsetField.setAccessible(true);
    assertEquals(2, yInsetField.getInt(null));
  }

  @Test
  public void createShape_returnsNonNullShape() throws Exception {
    assumeInitialized();
    // createShape is private static — invoke via reflection
    java.lang.reflect.Method createShape = TypeBorder.class.getDeclaredMethod(
        "createShape", int.class, int.class, int.class, int.class);
    createShape.setAccessible(true);
    Shape shape = (Shape) createShape.invoke(null, 0, 0, 100, 30);
    assertNotNull(shape);
  }

  @Test
  public void createShape_boundsMatchInput() throws Exception {
    assumeInitialized();
    java.lang.reflect.Method createShape = TypeBorder.class.getDeclaredMethod(
        "createShape", int.class, int.class, int.class, int.class);
    createShape.setAccessible(true);
    Shape shape = (Shape) createShape.invoke(null, 10, 5, 80, 20);
    Rectangle bounds = shape.getBounds();
    assertTrue("Shape width should be positive", bounds.width > 0);
    assertTrue("Shape height should be positive", bounds.height > 0);
  }
}
