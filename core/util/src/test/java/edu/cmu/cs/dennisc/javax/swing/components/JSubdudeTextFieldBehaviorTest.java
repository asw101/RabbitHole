package edu.cmu.cs.dennisc.javax.swing.components;

import org.junit.Test;

import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.Color;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.awt.event.KeyEvent;

import static org.junit.Assert.*;

public class JSubdudeTextFieldBehaviorTest {

  @Test
  public void constructorBindsEscapeAndEnter() {
    JSubdudeTextField field = new JSubdudeTextField();

    assertNotNull(field.getInputMap().get(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0)));
    assertNotNull(field.getInputMap().get(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0)));
  }

  @Test
  public void preferredWidthHasMinimumAndMaximumMatchesPreferred() {
    JSubdudeTextField field = new JSubdudeTextField();
    Insets insets = field.getInsets();

    assertTrue(field.getPreferredSize().width >= 24 + insets.left + insets.right);
    assertEquals(field.getPreferredSize(), field.getMaximumSize());
  }

  @Test
  public void addNotifyAndRemoveNotifyDoNotThrow() {
    JSubdudeTextField field = new JSubdudeTextField();
    JPanel parent = new JPanel();
    parent.add(field);

    parent.addNotify();
    field.addNotify();
    field.removeNotify();
  }

  @Test
  public void paintWithoutFocusUsesParentBackground() {
    JSubdudeTextField field = new JSubdudeTextField();
    field.setText("hello");
    field.setForeground(Color.BLACK);
    field.setSize(120, 24);

    JPanel parent = new JPanel();
    parent.setBackground(Color.YELLOW);
    parent.add(field);

    BufferedImage image = new BufferedImage(120, 24, BufferedImage.TYPE_INT_ARGB);
    field.paint(image.getGraphics());

    assertEquals(Color.YELLOW.getRGB(), image.getRGB(1, 1));
  }
}
