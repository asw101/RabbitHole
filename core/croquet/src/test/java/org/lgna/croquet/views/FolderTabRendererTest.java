package org.lgna.croquet.views;

import org.junit.Test;

import javax.swing.*;
import javax.swing.plaf.basic.BasicToggleButtonUI;
import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * TDD contract tests for FolderTabRenderer extraction from FolderTabbedPane.
 *
 * <p>FolderTabRenderer.java must contain three extracted inner classes:
 * <ul>
 *   <li>{@code FolderTabTitleUI} — custom BasicToggleButtonUI for tab sizing/painting</li>
 *   <li>{@code JFolderTabTitle} — JToggleButton subclass with non-opaque, SpringLayout, custom border</li>
 *   <li>{@code FolderTabTitle} — BooleanStateButton with close-button support and model injection</li>
 * </ul>
 *
 * <p>These tests will FAIL until the extraction is implemented. They define the
 * behavioral contract that the extracted classes must satisfy.</p>
 */
public class FolderTabRendererTest {

  // ── FolderTabTitleUI structural contract ──────────────────────────────

  @Test
  public void folderTabTitleUI_existsAndExtendsBasicToggleButtonUI() throws Exception {
    Class<?> clazz = Class.forName("org.lgna.croquet.views.FolderTabTitleUI");
    assertTrue("FolderTabTitleUI must extend BasicToggleButtonUI",
        BasicToggleButtonUI.class.isAssignableFrom(clazz));
  }

  @Test
  public void folderTabTitleUI_hasNoArgConstructor() throws Exception {
    Class<?> clazz = Class.forName("org.lgna.croquet.views.FolderTabTitleUI");
    Constructor<?> ctor = clazz.getDeclaredConstructor();
    assertNotNull("FolderTabTitleUI must have a no-arg constructor", ctor);
  }

  // ── FolderTabTitleUI preferred size behavior ─────────────────────────

  @Test
  public void folderTabTitleUI_preferredSizeIncludesInsets() throws Exception {
    BasicToggleButtonUI ui = createFolderTabTitleUI();

    JToggleButton button = new JToggleButton("Test");
    button.setUI(ui);
    button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    button.setFont(new Font("Dialog", Font.PLAIN, 12));

    Dimension size = ui.getPreferredSize(button);
    assertNotNull("Preferred size must not be null", size);
    assertTrue("Width must include left+right insets (≥20px)", size.width >= 20);
    assertTrue("Height must include top+bottom insets (≥20px)", size.height >= 20);
  }

  @Test
  public void folderTabTitleUI_preferredSizeGrowsWithChildComponents() throws Exception {
    BasicToggleButtonUI ui = createFolderTabTitleUI();

    JToggleButton button = new JToggleButton("Tab");
    button.setUI(ui);
    button.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 8));
    button.setFont(new Font("Dialog", Font.PLAIN, 12));
    button.setLayout(new SpringLayout());

    Dimension sizeWithout = ui.getPreferredSize(button);

    JButton child = new JButton();
    child.setPreferredSize(new Dimension(16, 16));
    button.add(child);

    Dimension sizeWith = ui.getPreferredSize(button);
    // Each child adds 4px gap + child width
    assertTrue("Adding child component must increase preferred width by ≥20px (4 gap + 16 child)",
        sizeWith.width >= sizeWithout.width + 20);
  }

  @Test
  public void folderTabTitleUI_preferredSizeHandlesIconButton() throws Exception {
    BasicToggleButtonUI ui = createFolderTabTitleUI();

    Icon icon = new Icon() {
      @Override public void paintIcon(Component c, Graphics g, int x, int y) {}
      @Override public int getIconWidth() { return 24; }
      @Override public int getIconHeight() { return 24; }
    };

    JToggleButton button = new JToggleButton("Tab", icon);
    button.setUI(ui);
    button.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 8));
    button.setFont(new Font("Dialog", Font.PLAIN, 12));

    Dimension size = ui.getPreferredSize(button);
    assertNotNull("Preferred size for icon button must not be null", size);
    // Size must account for icon at minimum
    assertTrue("Width must accommodate the 24px icon", size.width >= 24);
    assertTrue("Height must accommodate the 24px icon", size.height >= 24);
  }

  @Test
  public void folderTabTitleUI_preferredSizeHandlesTextOnlyButton() throws Exception {
    BasicToggleButtonUI ui = createFolderTabTitleUI();

    JToggleButton button = new JToggleButton("Hello");
    button.setUI(ui);
    button.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 8));
    button.setFont(new Font("Dialog", Font.PLAIN, 12));

    Dimension size = ui.getPreferredSize(button);
    assertNotNull(size);
    // "Hello" in a 12pt font is at least a few pixels wide
    assertTrue("Width must be positive for text-only button", size.width > 0);
    assertTrue("Height must be positive for text-only button", size.height > 0);
  }

  // ── JFolderTabTitle structural contract ──────────────────────────────

  @Test
  public void jFolderTabTitle_existsAndExtendsJToggleButton() throws Exception {
    Class<?> clazz = Class.forName("org.lgna.croquet.views.JFolderTabTitle");
    assertTrue("JFolderTabTitle must extend JToggleButton",
        JToggleButton.class.isAssignableFrom(clazz));
  }

  // ── JFolderTabTitle behavioral contract ──────────────────────────────

  @Test
  public void jFolderTabTitle_isNotOpaque() throws Exception {
    JToggleButton button = createJFolderTabTitle();
    assertFalse("JFolderTabTitle must not be opaque", button.isOpaque());
  }

  @Test
  public void jFolderTabTitle_borderInsetsAre_4_4_4_8() throws Exception {
    JToggleButton button = createJFolderTabTitle();
    Insets insets = button.getInsets();
    assertEquals("Top inset must be 4", 4, insets.top);
    assertEquals("Left inset must be 4", 4, insets.left);
    assertEquals("Bottom inset must be 4", 4, insets.bottom);
    assertEquals("Right inset must be 8", 8, insets.right);
  }

  @Test
  public void jFolderTabTitle_usesSpringLayout() throws Exception {
    JToggleButton button = createJFolderTabTitle();
    assertTrue("JFolderTabTitle must use SpringLayout",
        button.getLayout() instanceof SpringLayout);
  }

  @Test
  public void jFolderTabTitle_usesFolderTabTitleUI() throws Exception {
    Class<?> uiClass = Class.forName("org.lgna.croquet.views.FolderTabTitleUI");
    JToggleButton button = createJFolderTabTitle();
    assertTrue("JFolderTabTitle must use FolderTabTitleUI as its UI delegate",
        uiClass.isInstance(button.getUI()));
  }

  @Test
  public void jFolderTabTitle_alignmentIsLeftBottom() throws Exception {
    JToggleButton button = createJFolderTabTitle();
    assertEquals("AlignmentX must be LEFT_ALIGNMENT",
        Component.LEFT_ALIGNMENT, button.getAlignmentX(), 0.001f);
    assertEquals("AlignmentY must be BOTTOM_ALIGNMENT",
        Component.BOTTOM_ALIGNMENT, button.getAlignmentY(), 0.001f);
  }

  @Test
  public void jFolderTabTitle_horizontalPositionIsLeading() throws Exception {
    JToggleButton button = createJFolderTabTitle();
    assertEquals("Horizontal text position must be LEADING",
        SwingConstants.LEADING, button.getHorizontalTextPosition());
    assertEquals("Horizontal alignment must be LEADING",
        SwingConstants.LEADING, button.getHorizontalAlignment());
  }

  // ── FolderTabTitle structural contract ───────────────────────────────

  @Test
  public void folderTabTitle_existsAndExtendsBooleanStateButton() throws Exception {
    Class<?> clazz = Class.forName("org.lgna.croquet.views.FolderTabTitle");
    assertTrue("FolderTabTitle must extend BooleanStateButton",
        BooleanStateButton.class.isAssignableFrom(clazz));
  }

  @Test
  public void folderTabTitle_constructorAcceptsSingleSelectListState() throws Exception {
    // After extraction, FolderTabTitle must accept a SingleSelectListState
    // to replace the implicit FolderTabbedPane.this.getModel() outer reference
    Class<?> clazz = Class.forName("org.lgna.croquet.views.FolderTabTitle");
    Constructor<?>[] constructors = clazz.getDeclaredConstructors();

    boolean foundModelParam = false;
    for (Constructor<?> ctor : constructors) {
      for (Class<?> paramType : ctor.getParameterTypes()) {
        if (org.lgna.croquet.SingleSelectListState.class.isAssignableFrom(paramType)) {
          foundModelParam = true;
          break;
        }
      }
      if (foundModelParam) break;
    }
    assertTrue("FolderTabTitle must have a constructor accepting SingleSelectListState " +
        "(replaces implicit FolderTabbedPane.this outer reference)", foundModelParam);
  }

  @Test
  public void folderTabTitle_hasUpdateForMethod() throws Exception {
    Class<?> clazz = Class.forName("org.lgna.croquet.views.FolderTabTitle");
    // After type erasure, <F extends TabComposite<?>> → TabComposite
    Method updateFor = clazz.getMethod("updateFor", org.lgna.croquet.TabComposite.class);
    assertNotNull("FolderTabTitle must have updateFor(TabComposite) method", updateFor);
  }

  @Test
  public void folderTabTitle_hasSetCloseableMethod() throws Exception {
    Class<?> clazz = Class.forName("org.lgna.croquet.views.FolderTabTitle");
    Method setCloseable = clazz.getMethod("setCloseable", boolean.class);
    assertNotNull("FolderTabTitle must have setCloseable(boolean) method", setCloseable);
  }

  // ── Helper methods ──────────────────────────────────────────────────

  private static BasicToggleButtonUI createFolderTabTitleUI() throws Exception {
    Class<?> clazz = Class.forName("org.lgna.croquet.views.FolderTabTitleUI");
    Constructor<?> ctor = clazz.getDeclaredConstructor();
    ctor.setAccessible(true);
    return (BasicToggleButtonUI) ctor.newInstance();
  }

  private static JToggleButton createJFolderTabTitle() throws Exception {
    Class<?> clazz = Class.forName("org.lgna.croquet.views.JFolderTabTitle");
    Constructor<?> ctor = clazz.getDeclaredConstructor();
    ctor.setAccessible(true);
    return (JToggleButton) ctor.newInstance();
  }
}
