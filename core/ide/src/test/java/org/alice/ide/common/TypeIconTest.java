package org.alice.ide.common;

import org.junit.Assume;
import org.junit.Test;
import org.lgna.project.ast.JavaType;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

import javax.swing.JLabel;

import static org.junit.Assert.*;

public class TypeIconTest {

  private static final class ExposedTypeIcon extends TypeIcon {
    private ExposedTypeIcon() {
      super(JavaType.getInstance(String.class), false, new Font("Dialog", Font.PLAIN, 12), new Font("Dialog", Font.BOLD, 10));
    }

    private Color exposeTextColor(Component component) {
      return this.getTextColor(component);
    }
  }

  @Test
  public void getInstance_returnsDistinctObjects() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    assertNotSame(TypeIcon.getInstance(JavaType.getInstance(String.class)), TypeIcon.getInstance(JavaType.getInstance(String.class)));
  }

  @Test
  public void customConstructor_preservesBonusFont() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    Font bonusFont = new Font("Dialog", Font.BOLD, 10);
    TypeIcon icon = new TypeIcon(JavaType.getInstance(String.class), false, new Font("Dialog", Font.PLAIN, 12), bonusFont);
    assertEquals(bonusFont, icon.getBonusFont());
  }

  @Test
  public void textColor_tracksEnabledState() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    ExposedTypeIcon icon = new ExposedTypeIcon();
    JLabel label = new JLabel();
    label.setEnabled(true);
    assertEquals(Color.BLACK, icon.exposeTextColor(label));
    label.setEnabled(false);
    assertEquals(Color.GRAY, icon.exposeTextColor(label));
  }

  @Test
  public void iconDimensions_arePositive() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    TypeIcon icon = TypeIcon.getInstance(JavaType.getInstance(String.class));
    assertTrue(icon.getIconWidth() > 0);
    assertTrue(icon.getIconHeight() > 0);
  }
}
