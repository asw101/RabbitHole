package org.alice.ide.icons;

import org.junit.Assume;
import org.junit.Test;
import org.lgna.croquet.icon.IconFactory;
import org.lgna.project.ast.UserField;

import javax.swing.Icon;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class FieldIconFactoryTest {
  @Test
  public void constructor_doesNotThrow() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    UserField field = new UserField();
    field.name.setValue("testField");
    IconFactory fallback = CheckIconFactory.getInstance();
    new FieldIconFactory(field, fallback);
  }

  @Test
  public void getIconExactSize_returnsFieldIcon() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    UserField field = new UserField();
    field.name.setValue("myField");
    IconFactory fallback = CheckIconFactory.getInstance();
    FieldIconFactory factory = new FieldIconFactory(field, fallback);
    Icon icon = factory.getIconExactSize(new Dimension(24, 24));
    assertTrue(icon instanceof FieldIcon);
  }

  @Test
  public void markAllIconsDirty_doesNotThrowWhenEmpty() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    UserField field = new UserField();
    field.name.setValue("emptyField");
    IconFactory fallback = CheckIconFactory.getInstance();
    FieldIconFactory factory = new FieldIconFactory(field, fallback);
    factory.markAllIconsDirty();
  }

  @Test
  public void markAllIconsDirty_afterCreation_doesNotThrow() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    UserField field = new UserField();
    field.name.setValue("dirtyField");
    IconFactory fallback = CheckIconFactory.getInstance();
    FieldIconFactory factory = new FieldIconFactory(field, fallback);
    factory.getIconExactSize(new Dimension(32, 32));
    factory.markAllIconsDirty();
  }
}
