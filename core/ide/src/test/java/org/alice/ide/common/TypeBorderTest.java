package org.alice.ide.common;

import org.junit.Assume;
import org.junit.Test;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;

import javax.swing.JPanel;

import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;

import static org.junit.Assert.*;

public class TypeBorderTest {

  @Test
  public void getSingletonFor_nullType_returnsNullSingleton() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    TypeBorder border = TypeBorder.getSingletonFor(null);
    assertNotNull(border);
  }

  @Test
  public void getSingletonFor_javaType_returnsSingleton() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    JavaType doubleType = JavaType.getInstance(Double.class);
    TypeBorder border = TypeBorder.getSingletonFor(doubleType);
    assertNotNull(border);
  }

  @Test
  public void getSingletonFor_sameJavaType_returnsSameInstance() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    JavaType intType = JavaType.getInstance(Integer.class);
    TypeBorder a = TypeBorder.getSingletonFor(intType);
    TypeBorder b = TypeBorder.getSingletonFor(intType);
    assertSame(a, b);
  }

  @Test
  public void getSingletonFor_differentJavaTypes_returnsSameJavaInstance() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    JavaType intType = JavaType.getInstance(Integer.class);
    JavaType strType = JavaType.getInstance(String.class);
    TypeBorder a = TypeBorder.getSingletonFor(intType);
    TypeBorder b = TypeBorder.getSingletonFor(strType);
    assertSame(a, b);
  }

  @Test
  public void getSingletonForUserType_notNull() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    TypeBorder border = TypeBorder.getSingletonForUserType();
    assertNotNull(border);
  }

  @Test
  public void getBorderInsets_returnsNonNull() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    TypeBorder border = TypeBorder.getSingletonFor(null);
    Insets insets = border.getBorderInsets(new JPanel());
    assertNotNull(insets);
    assertTrue(insets.left > 0);
    assertTrue(insets.top >= 0);
  }

  @Test
  public void isBorderOpaque_returnsFalse() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    TypeBorder border = TypeBorder.getSingletonFor(null);
    assertFalse(border.isBorderOpaque());
  }

  @Test
  public void implementsBorder() {
    assertTrue(javax.swing.border.Border.class.isAssignableFrom(TypeBorder.class));
  }
}
