package org.lgna.issue.swing;

import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JEnvironmentSubPaneTest {
  @BeforeClass
  public static void setHeadless() {
    System.setProperty("java.awt.headless", "true");
  }

  @Test
  public void constructorCreatesPropertyLabelsAndShowAllLink() {
    String propertyName = "alice.issue.reporting.test.property";
    String previousValue = System.getProperty(propertyName);
    System.setProperty(propertyName, "value");
    try {
      List<String> propertyNames = Arrays.asList("java.version", propertyName);

      JEnvironmentSubPane pane = new JEnvironmentSubPane(propertyNames);

      assertTrue(pane.getLayout() instanceof BoxLayout);
      assertEquals(3, pane.getComponentCount());
      assertEquals("java.version: " + System.getProperty("java.version"), ((JLabel) pane.getComponent(0)).getText());
      assertEquals(propertyName + ": value", ((JLabel) pane.getComponent(1)).getText());
      assertEquals("<html><u>show all system properties...</u></html>", ((JLabel) pane.getComponent(2)).getText());
    } finally {
      if (previousValue != null) {
        System.setProperty(propertyName, previousValue);
      } else {
        System.clearProperty(propertyName);
      }
    }
  }
}
