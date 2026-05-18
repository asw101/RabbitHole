package org.alice.ide.common;

import org.junit.Assume;
import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;

import java.awt.*;

import static org.junit.Assert.*;

public class TypeBorderTest {

  @Test
  public void getSingletonForNull_returnsNonNull() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertNotNull(TypeBorder.getSingletonFor(null));
  }

  @Test
  public void getSingletonForJavaType_returnsNonNull() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertNotNull(TypeBorder.getSingletonFor(JavaType.getInstance(String.class)));
  }

  @Test
  public void getSingletonForUserType_returnsNonNull() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertNotNull(TypeBorder.getSingletonForUserType());
  }

  @Test
  public void getSingletonForNull_returnsSameInstance() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertSame(TypeBorder.getSingletonFor(null), TypeBorder.getSingletonFor(null));
  }

  @Test
  public void getSingletonForJavaType_returnsSameInstance() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertSame(
        TypeBorder.getSingletonFor(JavaType.getInstance(String.class)),
        TypeBorder.getSingletonFor(JavaType.getInstance(Integer.class)));
  }

  @Test
  public void differentTypeCategories_returnDifferentSingletons() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    TypeBorder javaBorder = TypeBorder.getSingletonFor(JavaType.getInstance(String.class));
    TypeBorder nullBorder = TypeBorder.getSingletonFor(null);
    assertNotSame(javaBorder, nullBorder);
  }

  @Test
  public void isBorderOpaque_returnsFalse() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    TypeBorder border = TypeBorder.getSingletonFor(JavaType.getInstance(String.class));
    assertFalse(border.isBorderOpaque());
  }

  @Test
  public void getBorderInsets_returnsNonNull() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    TypeBorder border = TypeBorder.getSingletonFor(JavaType.getInstance(String.class));
    Insets insets = border.getBorderInsets(null);
    assertNotNull(insets);
    assertTrue(insets.left > 0);
    assertTrue(insets.right > 0);
  }
}
