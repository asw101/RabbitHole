package org.lgna.croquet.icon;

import org.junit.Test;

import javax.swing.Icon;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static org.junit.Assert.*;

/**
 * Tests for {@link AbstractIconFactory} — icon caching and sizing logic,
 * and {@link AbstractIcon} — icon dimension accessors.
 */
public class AbstractIconFactoryTest {

  // ── AbstractIconFactory with caching ──────────────────────────────

  @Test
  public void getIconExactSize_withCaching_returnsIcon() {
    TestIconFactory factory = new TestIconFactory(true);
    Icon icon = factory.getIconExactSize(new Dimension(32, 32));
    assertNotNull(icon);
    assertEquals(32, icon.getIconWidth());
    assertEquals(32, icon.getIconHeight());
  }

  @Test
  public void getIconExactSize_withCaching_returnsCachedInstance() {
    TestIconFactory factory = new TestIconFactory(true);
    Dimension size = new Dimension(16, 16);
    Icon first = factory.getIconExactSize(size);
    Icon second = factory.getIconExactSize(size);
    assertSame(first, second);
    assertEquals(1, factory.createCount);
  }

  @Test
  public void getIconExactSize_withCaching_differentSizes_differentIcons() {
    TestIconFactory factory = new TestIconFactory(true);
    Icon icon16 = factory.getIconExactSize(new Dimension(16, 16));
    Icon icon32 = factory.getIconExactSize(new Dimension(32, 32));
    assertNotSame(icon16, icon32);
    assertEquals(2, factory.createCount);
  }

  // ── AbstractIconFactory without caching ───────────────────────────

  @Test
  public void getIconExactSize_noCaching_createsNewEachTime() {
    TestIconFactory factory = new TestIconFactory(false);
    Dimension size = new Dimension(24, 24);
    Icon first = factory.getIconExactSize(size);
    Icon second = factory.getIconExactSize(size);
    assertNotSame(first, second);
    assertEquals(2, factory.createCount);
  }

  // ── getIconToFit ──────────────────────────────────────────────────

  @Test
  public void getIconToFit_noDefaultSize_returnsMaxSize() {
    TestIconFactory factory = new TestIconFactory(false);
    Dimension maxSize = new Dimension(48, 48);
    Icon icon = factory.getIconToFit(maxSize);
    assertNotNull(icon);
  }

  @Test
  public void getIconToFit_withDefaultSize_sameRatio_returnsSameSize() {
    TestIconFactoryWithDefault factory = new TestIconFactoryWithDefault(100, 75);
    Dimension maxSize = new Dimension(200, 150); // Same 4:3 ratio
    Icon icon = factory.getIconToFit(maxSize);
    assertEquals(200, icon.getIconWidth());
    assertEquals(150, icon.getIconHeight());
  }

  @Test
  public void getIconToFit_withDefaultSize_widerRequest_fitsHeight() {
    TestIconFactoryWithDefault factory = new TestIconFactoryWithDefault(100, 100);
    Dimension maxSize = new Dimension(200, 100); // Wider than 1:1 default
    Icon icon = factory.getIconToFit(maxSize);
    assertEquals(100, icon.getIconHeight());
  }

  @Test
  public void getIconToFit_withDefaultSize_tallerRequest_fitsWidth() {
    TestIconFactoryWithDefault factory = new TestIconFactoryWithDefault(100, 100);
    Dimension maxSize = new Dimension(100, 200); // Taller than 1:1 default
    Icon icon = factory.getIconToFit(maxSize);
    assertEquals(100, icon.getIconWidth());
  }

  // ── getDefaultSize ────────────────────────────────────────────────

  @Test
  public void getDefaultSize_noOverride_returnsFallback() {
    TestIconFactory factory = new TestIconFactory(false);
    Dimension fallback = new Dimension(50, 50);
    assertEquals(fallback, factory.getDefaultSize(fallback));
  }

  @Test
  public void getDefaultSize_noOverride_nullFallback_returnsNull() {
    TestIconFactory factory = new TestIconFactory(false);
    assertNull(factory.getDefaultSize(null));
  }

  // ── getDefaultSizeForWidth / getDefaultSizeForHeight ──────────────

  @Test
  public void getDefaultSizeForWidth_returnsProportionalSize() {
    TestIconFactoryWithDefault factory = new TestIconFactoryWithDefault(100, 50);
    Dimension size = factory.getDefaultSizeForWidth(200);
    assertEquals(200, size.width);
    assertEquals(100, size.height); // 2:1 ratio → height = width/2
  }

  @Test
  public void getDefaultSizeForHeight_returnsProportionalSize() {
    TestIconFactoryWithDefault factory = new TestIconFactoryWithDefault(100, 50);
    Dimension size = factory.getDefaultSizeForHeight(100);
    assertEquals(200, size.width); // 2:1 ratio → width = height*2
    assertEquals(100, size.height);
  }

  // ── AbstractIcon ──────────────────────────────────────────────────

  @Test
  public void abstractIcon_getIconWidth() {
    TestIcon icon = new TestIcon(new Dimension(64, 48));
    assertEquals(64, icon.getIconWidth());
  }

  @Test
  public void abstractIcon_getIconHeight() {
    TestIcon icon = new TestIcon(new Dimension(64, 48));
    assertEquals(48, icon.getIconHeight());
  }

  @Test
  public void abstractIcon_paintIcon_doesNotThrow() {
    TestIcon icon = new TestIcon(new Dimension(32, 32));
    BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = img.createGraphics();
    icon.paintIcon(null, g2, 0, 0);
    g2.dispose();
  }

  // ── getMapValues ──────────────────────────────────────────────────

  @Test
  public void getMapValues_afterCreation_containsIcons() {
    TestIconFactory factory = new TestIconFactory(true);
    factory.getIconExactSize(new Dimension(16, 16));
    factory.getIconExactSize(new Dimension(32, 32));
    assertEquals(2, factory.getMapValues().size());
  }

  // ── Test infrastructure ───────────────────────────────────────────

  private static class TestIconFactory extends AbstractIconFactory {
    int createCount = 0;

    TestIconFactory(boolean caching) {
      super(caching ? IsCachingDesired.TRUE : IsCachingDesired.FALSE);
    }

    @Override
    protected Icon createIcon(Dimension size) {
      createCount++;
      return new TestIcon(size);
    }
  }

  private static class TestIconFactoryWithDefault extends AbstractIconFactory {
    private final Dimension defaultSize;

    TestIconFactoryWithDefault(int w, int h) {
      super(IsCachingDesired.FALSE);
      this.defaultSize = new Dimension(w, h);
    }

    @Override
    public Dimension getDefaultSize(Dimension fallbackSize) {
      return defaultSize;
    }

    @Override
    protected Icon createIcon(Dimension size) {
      return new TestIcon(size);
    }
  }

  private static class TestIcon extends AbstractIcon {
    TestIcon(Dimension size) {
      super(size);
    }

    @Override
    protected void paintIcon(Component c, Graphics2D g2) {
      // No-op for testing
    }
  }
}
