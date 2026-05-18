package org.alice.ide.common;

import org.junit.Assume;
import org.junit.Test;
import org.lgna.project.ast.JavaType;

import java.awt.GraphicsEnvironment;

import javax.swing.JLabel;

import static org.junit.Assert.*;

public class TypeComponentTest {

  @Test
  public void createInstance_returnsNonNullComponent() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertNotNull(TypeComponent.createInstance(JavaType.getInstance(String.class)));
  }

  @Test
  public void awtComponent_isJLabel() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    TypeComponent component = TypeComponent.createInstance(JavaType.getInstance(String.class));
    assertTrue(component.getAwtComponent() instanceof JLabel);
  }

  @Test
  public void createInstance_installsTypeIcon() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    TypeComponent component = TypeComponent.createInstance(JavaType.getInstance(String.class));
    assertTrue(component.getAwtComponent().getIcon() instanceof TypeIcon);
  }

  @Test
  public void createInstance_handlesNullType() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    TypeComponent component = TypeComponent.createInstance(null);
    assertNotNull(component.getAwtComponent().getIcon());
  }
}
