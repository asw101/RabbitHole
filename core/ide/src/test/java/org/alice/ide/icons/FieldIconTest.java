package org.alice.ide.icons;

import org.junit.Assume;
import org.junit.Test;
import org.lgna.project.ast.UserField;

import javax.swing.Icon;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class FieldIconTest {
  @Test
  public void constructor_setsFieldAndFallback() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    UserField field = new UserField();
    field.name.setValue("testField");
    Icon fallback = CheckIconFactory.getInstance().getIconExactSize(new Dimension(24, 24));
    FieldIcon icon = new FieldIcon(field, fallback);
    assertNotNull(icon);
  }

  @Test
  public void getIconWidth_returnsFallbackWidth() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    UserField field = new UserField();
    field.name.setValue("widthField");
    Dimension size = new Dimension(32, 32);
    Icon fallback = CheckIconFactory.getInstance().getIconExactSize(size);
    FieldIcon icon = new FieldIcon(field, fallback);
    assertEquals(fallback.getIconWidth(), icon.getIconWidth());
  }

  @Test
  public void getIconHeight_returnsFallbackHeight() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    UserField field = new UserField();
    field.name.setValue("heightField");
    Dimension size = new Dimension(48, 36);
    Icon fallback = CheckIconFactory.getInstance().getIconExactSize(size);
    FieldIcon icon = new FieldIcon(field, fallback);
    assertEquals(fallback.getIconHeight(), icon.getIconHeight());
  }

  @Test
  public void markDirty_doesNotThrow() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    UserField field = new UserField();
    field.name.setValue("dirtyField");
    Icon fallback = CheckIconFactory.getInstance().getIconExactSize(new Dimension(24, 24));
    FieldIcon icon = new FieldIcon(field, fallback);
    icon.markDirty();
  }

  @Test
  public void markDirty_canBeCalledMultipleTimes() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    UserField field = new UserField();
    field.name.setValue("multiDirty");
    Icon fallback = CheckIconFactory.getInstance().getIconExactSize(new Dimension(24, 24));
    FieldIcon icon = new FieldIcon(field, fallback);
    icon.markDirty();
    icon.markDirty();
  }
}
