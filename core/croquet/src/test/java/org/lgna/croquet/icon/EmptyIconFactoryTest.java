package org.lgna.croquet.icon;

import org.junit.Test;

import javax.swing.Icon;
import java.awt.Dimension;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Tests for {@link EmptyIconFactory} — singleton access, icon creation,
 * zero-painting behavior, and hierarchy verification.
 */
public class EmptyIconFactoryTest {

  // ── Singleton ─────────────────────────────────────────────────────

  @Test
  public void getInstance_returnsNonNull() {
    assertNotNull(EmptyIconFactory.getInstance());
  }

  @Test
  public void getInstance_returnsSameInstance() {
    assertSame(EmptyIconFactory.getInstance(), EmptyIconFactory.getInstance());
  }

  // ── createIcon via getIcon ────────────────────────────────────────

  @Test
  public void getIcon_defaultSize_returnsNonNull() {
    Icon icon = EmptyIconFactory.getInstance().getIconExactSize(new Dimension(16, 16));
    assertNotNull(icon);
  }

  @Test
  public void getIcon_width_matchesRequested() {
    Icon icon = EmptyIconFactory.getInstance().getIconExactSize(new Dimension(24, 24));
    assertEquals(24, icon.getIconWidth());
  }

  @Test
  public void getIcon_height_matchesRequested() {
    Icon icon = EmptyIconFactory.getInstance().getIconExactSize(new Dimension(32, 32));
    assertEquals(32, icon.getIconHeight());
  }

  @Test
  public void getIcon_smallSize() {
    Icon icon = EmptyIconFactory.getInstance().getIconExactSize(new Dimension(1, 1));
    assertEquals(1, icon.getIconWidth());
    assertEquals(1, icon.getIconHeight());
  }

  @Test
  public void getIcon_largeSize() {
    Icon icon = EmptyIconFactory.getInstance().getIconExactSize(new Dimension(256, 256));
    assertEquals(256, icon.getIconWidth());
    assertEquals(256, icon.getIconHeight());
  }

  @Test
  public void getIcon_rectangularSize() {
    Icon icon = EmptyIconFactory.getInstance().getIconExactSize(new Dimension(48, 32));
    assertEquals(48, icon.getIconWidth());
    assertEquals(32, icon.getIconHeight());
  }

  @Test
  public void getIcon_differentSizes_returnDifferentIcons() {
    Icon icon16 = EmptyIconFactory.getInstance().getIconExactSize(new Dimension(16, 16));
    Icon icon32 = EmptyIconFactory.getInstance().getIconExactSize(new Dimension(32, 32));
    assertNotEquals(icon16.getIconWidth(), icon32.getIconWidth());
  }

  // ── paintIcon (empty, should not throw) ───────────────────────────

  @Test
  public void paintIcon_noException() {
    Icon icon = EmptyIconFactory.getInstance().getIconExactSize(new Dimension(16, 16));
    icon.paintIcon(null, new java.awt.image.BufferedImage(
        16, 16, java.awt.image.BufferedImage.TYPE_INT_ARGB).getGraphics(), 0, 0);
  }

  // ── Hierarchy ─────────────────────────────────────────────────────

  @Test
  public void class_extendsResolutionIndependentIconFactory() {
    assertTrue(ResolutionIndependentIconFactory.class.isAssignableFrom(
        EmptyIconFactory.class));
  }

  @Test
  public void class_extendsAbstractIconFactory() {
    assertTrue(AbstractIconFactory.class.isAssignableFrom(
        EmptyIconFactory.class));
  }

  @Test
  public void class_isNotAbstract() {
    assertFalse(Modifier.isAbstract(EmptyIconFactory.class.getModifiers()));
  }

  // ── Constructor is private ────────────────────────────────────────

  @Test
  public void constructor_isPrivate() throws Exception {
    var ctors = EmptyIconFactory.class.getDeclaredConstructors();
    for (var ctor : ctors) {
      assertTrue("Constructor should be private",
          Modifier.isPrivate(ctor.getModifiers()));
    }
  }

  // ── getDefaultSize ────────────────────────────────────────────────

  @Test
  public void getDefaultSize_returnsNonNull() {
    Dimension d = EmptyIconFactory.getInstance().getDefaultSize(new Dimension(16, 16));
    assertNotNull(d);
  }

  // ── toString ──────────────────────────────────────────────────────

  @Test
  public void toString_nonNull() {
    assertNotNull(EmptyIconFactory.getInstance().toString());
  }
}
