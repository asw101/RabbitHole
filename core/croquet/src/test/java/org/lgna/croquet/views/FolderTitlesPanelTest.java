package org.lgna.croquet.views;

import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * TDD contract tests for FolderTitlesPanel extraction from FolderTabbedPane.
 *
 * <p>FolderTitlesPanel is the promoted top-level version of
 * {@code FolderTabbedPane.TitlesPanel}. It extends {@link LineAxisPanel}
 * and contains the nested {@code JTitlesPanel} which handles tab geometry
 * and painting.</p>
 *
 * <p>Key behavioral contracts:
 * <ul>
 *   <li>{@code JTitlesPanel.getPreferredSize()} adds TRAILING_TAB_PAD (32px) to width</li>
 *   <li>{@code JTitlesPanel.paintChildren()} paints selected tab last (on top)</li>
 *   <li>{@code FolderTitlesPanel.createJPanel()} returns a JTitlesPanel instance</li>
 *   <li>{@code FolderTitlesPanel} extends {@link LineAxisPanel}</li>
 * </ul>
 *
 * <p>These tests will FAIL until the extraction is implemented.</p>
 */
public class FolderTitlesPanelTest {

  private static final int TRAILING_TAB_PAD = 32;

  // ── FolderTitlesPanel structural contract ────────────────────────────

  @Test
  public void folderTitlesPanel_existsAndExtendsLineAxisPanel() throws Exception {
    Class<?> clazz = Class.forName("org.lgna.croquet.views.FolderTitlesPanel");
    assertTrue("FolderTitlesPanel must extend LineAxisPanel",
        LineAxisPanel.class.isAssignableFrom(clazz));
  }

  @Test
  public void folderTitlesPanel_hasNoArgConstructor() throws Exception {
    Class<?> clazz = Class.forName("org.lgna.croquet.views.FolderTitlesPanel");
    Constructor<?> ctor = clazz.getDeclaredConstructor();
    assertNotNull("FolderTitlesPanel must have a no-arg constructor", ctor);
  }

  // ── JTitlesPanel structural contract ─────────────────────────────────

  @Test
  public void jTitlesPanel_existsAndExtendsJPanel() throws Exception {
    Class<?> clazz = Class.forName("org.lgna.croquet.views.FolderTitlesPanel$JTitlesPanel");
    assertTrue("JTitlesPanel must extend JPanel",
        JPanel.class.isAssignableFrom(clazz));
  }

  // ── JTitlesPanel preferred size includes trailing tab pad ────────────

  @Test
  public void jTitlesPanel_preferredSizeAddsTrailingTabPad() throws Exception {
    JPanel panel = createJTitlesPanel();
    // Add a child with a known preferred size
    JLabel label = new JLabel("Tab1");
    label.setPreferredSize(new Dimension(60, 20));
    panel.add(label);

    Dimension prefSize = panel.getPreferredSize();
    // The JTitlesPanel overrides getPreferredSize() to add TRAILING_TAB_PAD to width
    assertTrue("Preferred width must include TRAILING_TAB_PAD (32px) beyond child content. " +
            "Got width=" + prefSize.width + " but expected ≥" + (60 + TRAILING_TAB_PAD),
        prefSize.width >= 60 + TRAILING_TAB_PAD);
  }

  @Test
  public void jTitlesPanel_emptyPanelStillAddsTrailingPad() throws Exception {
    JPanel panel = createJTitlesPanel();
    Dimension prefSize = panel.getPreferredSize();
    // Even empty, the panel should add TRAILING_TAB_PAD
    assertTrue("Even empty panel must add TRAILING_TAB_PAD to width. Got: " + prefSize.width,
        prefSize.width >= TRAILING_TAB_PAD);
  }

  @Test
  public void jTitlesPanel_preferredSizeHeightUnchanged() throws Exception {
    JPanel panel = createJTitlesPanel();
    JLabel label = new JLabel("Tab");
    label.setPreferredSize(new Dimension(60, 25));
    panel.add(label);

    JPanel reference = new JPanel();
    reference.add(new JLabel("Tab") {{
      setPreferredSize(new Dimension(60, 25));
    }});

    Dimension titlesPrefSize = panel.getPreferredSize();
    Dimension referencePrefSize = reference.getPreferredSize();
    // Height should be the same — only width gets the trailing pad
    assertEquals("Height must not be affected by TRAILING_TAB_PAD",
        referencePrefSize.height, titlesPrefSize.height);
  }

  @Test
  public void jTitlesPanel_multipleChildrenWidthAccumulates() throws Exception {
    JPanel panel = createJTitlesPanel();
    // Use BoxLayout-style horizontal layout to simulate tab strip
    panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

    JLabel tab1 = new JLabel("Tab1");
    tab1.setPreferredSize(new Dimension(60, 20));
    JLabel tab2 = new JLabel("Tab2");
    tab2.setPreferredSize(new Dimension(80, 20));

    panel.add(tab1);
    panel.add(tab2);

    Dimension prefSize = panel.getPreferredSize();
    // Combined width (60+80) plus TRAILING_TAB_PAD
    int expectedMinWidth = 60 + 80 + TRAILING_TAB_PAD;
    assertTrue("Multiple children width + TRAILING_TAB_PAD must be reflected. " +
            "Got width=" + prefSize.width + " expected ≥" + expectedMinWidth,
        prefSize.width >= expectedMinWidth);
  }

  // ── JTitlesPanel painting order ──────────────────────────────────────

  @Test
  public void jTitlesPanel_paintChildrenDoesNotThrowWithNoChildren() throws Exception {
    JPanel panel = createJTitlesPanel();
    panel.setSize(200, 30);

    // Create an off-screen graphics context for painting
    java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
        200, 30, java.awt.image.BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = image.createGraphics();

    try {
      // Must not throw with zero children
      panel.paint(g2);
    } finally {
      g2.dispose();
    }
  }

  @Test
  public void jTitlesPanel_paintChildrenHandlesSelectedButton() throws Exception {
    JPanel panel = createJTitlesPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

    JToggleButton unselected = new JToggleButton("Tab1");
    unselected.setSelected(false);
    unselected.setBackground(Color.LIGHT_GRAY);
    unselected.setSize(80, 25);
    unselected.setBounds(0, 0, 80, 25);

    JToggleButton selected = new JToggleButton("Tab2");
    selected.setSelected(true);
    selected.setBackground(Color.WHITE);
    selected.setSize(80, 25);
    selected.setBounds(80, 0, 80, 25);

    panel.add(unselected);
    panel.add(selected);
    panel.setSize(200, 30);

    java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
        200, 30, java.awt.image.BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = image.createGraphics();

    try {
      // Must paint without throwing; selected tab painted last (on top)
      panel.paint(g2);
    } finally {
      g2.dispose();
    }
  }

  // ── FolderTitlesPanel creates JTitlesPanel ───────────────────────────

  @Test
  public void folderTitlesPanel_createJPanelReturnsJTitlesPanel() throws Exception {
    Class<?> outerClass = Class.forName("org.lgna.croquet.views.FolderTitlesPanel");
    Class<?> innerClass = Class.forName("org.lgna.croquet.views.FolderTitlesPanel$JTitlesPanel");

    // Use reflection to call createJPanel (it's protected in LineAxisPanel hierarchy)
    Object instance = outerClass.getDeclaredConstructor().newInstance();
    Method createJPanel = findMethod(outerClass, "createJPanel");
    createJPanel.setAccessible(true);
    Object panel = createJPanel.invoke(instance);

    assertTrue("createJPanel() must return a JTitlesPanel instance",
        innerClass.isInstance(panel));
  }

  // ── Helper methods ──────────────────────────────────────────────────

  private static JPanel createJTitlesPanel() throws Exception {
    Class<?> clazz = Class.forName("org.lgna.croquet.views.FolderTitlesPanel$JTitlesPanel");
    Constructor<?> ctor = clazz.getDeclaredConstructor();
    ctor.setAccessible(true);
    return (JPanel) ctor.newInstance();
  }

  private static Method findMethod(Class<?> clazz, String name) {
    while (clazz != null) {
      for (Method m : clazz.getDeclaredMethods()) {
        if (m.getName().equals(name)) {
          return m;
        }
      }
      clazz = clazz.getSuperclass();
    }
    throw new RuntimeException("Method " + name + " not found");
  }
}
