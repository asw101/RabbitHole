package org.alice.ide.declarationseditor;

import org.junit.Assume;
import org.junit.Test;

import java.awt.GraphicsEnvironment;
import javax.swing.Icon;

import static org.junit.Assert.*;

public class DeclarationTabStateIconTest {

  @Test
  public void getProcedureIcon_notNull() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    Icon icon = DeclarationTabState.getProcedureIcon();
    assertNotNull(icon);
  }

  @Test
  public void getFunctionIcon_notNull() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    Icon icon = DeclarationTabState.getFunctionIcon();
    assertNotNull(icon);
  }

  @Test
  public void getFieldIcon_notNull() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    Icon icon = DeclarationTabState.getFieldIcon();
    assertNotNull(icon);
  }

  @Test
  public void getConstructorIcon_notNull() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    Icon icon = DeclarationTabState.getConstructorIcon();
    assertNotNull(icon);
  }

  @Test
  public void procedureIcon_hasPositiveDimensions() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    Icon icon = DeclarationTabState.getProcedureIcon();
    assertTrue(icon.getIconWidth() > 0);
    assertTrue(icon.getIconHeight() > 0);
  }

  @Test
  public void allIcons_areSameSize() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    Icon proc = DeclarationTabState.getProcedureIcon();
    Icon func = DeclarationTabState.getFunctionIcon();
    Icon field = DeclarationTabState.getFieldIcon();
    Icon ctor = DeclarationTabState.getConstructorIcon();
    assertEquals(proc.getIconWidth(), func.getIconWidth());
    assertEquals(proc.getIconWidth(), field.getIconWidth());
    assertEquals(proc.getIconWidth(), ctor.getIconWidth());
  }

  @Test
  public void icons_areSingleton() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertSame(DeclarationTabState.getProcedureIcon(), DeclarationTabState.getProcedureIcon());
    assertSame(DeclarationTabState.getFunctionIcon(), DeclarationTabState.getFunctionIcon());
  }
}
