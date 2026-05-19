package org.lgna.croquet.icon;

import org.junit.Before;
import org.junit.Test;

import javax.swing.Icon;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Tests for {@link ImageIconFactory} — construction from Image,
 * icon creation at various sizes, scaling behavior, and hierarchy.
 */
public class ImageIconFactoryTest {

  private ImageIconFactory factory;
  private BufferedImage testImage;

  @Before
  public void setUp() {
    testImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
    factory = new ImageIconFactory(testImage);
  }

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void constructor_image_nonNull() {
    assertNotNull(factory);
  }

  @Test
  public void constructor_icon_nonNull() {
    Icon icon = new javax.swing.ImageIcon(testImage);
    ImageIconFactory f = new ImageIconFactory(icon);
    assertNotNull(f);
  }

  // ── getIcon ───────────────────────────────────────────────────────

  @Test
  public void getIcon_sameSize_returnsNonNull() {
    Icon icon = factory.getIconExactSize(new Dimension(32, 32));
    assertNotNull(icon);
  }

  @Test
  public void getIcon_sameSize_matchesDimensions() {
    Icon icon = factory.getIconExactSize(new Dimension(32, 32));
    assertEquals(32, icon.getIconWidth());
    assertEquals(32, icon.getIconHeight());
  }

  @Test
  public void getIcon_smallerSize_returnsScaled() {
    Icon icon = factory.getIconExactSize(new Dimension(16, 16));
    assertNotNull(icon);
    assertEquals(16, icon.getIconWidth());
    assertEquals(16, icon.getIconHeight());
  }

  @Test
  public void getIcon_largerSize_returnsScaled() {
    Icon icon = factory.getIconExactSize(new Dimension(64, 64));
    assertNotNull(icon);
    assertEquals(64, icon.getIconWidth());
    assertEquals(64, icon.getIconHeight());
  }

  @Test
  public void getIcon_rectangularSize_returnsScaled() {
    Icon icon = factory.getIconExactSize(new Dimension(48, 24));
    assertNotNull(icon);
    assertEquals(48, icon.getIconWidth());
    assertEquals(24, icon.getIconHeight());
  }

  // ── getDefaultSize ────────────────────────────────────────────────

  @Test
  public void getDefaultSize_matchesSourceImage() {
    Dimension d = factory.getDefaultSize(new Dimension(99, 99));
    assertEquals(32, d.width);
    assertEquals(32, d.height);
  }

  @Test
  public void getDefaultSize_ignoresFallback_whenSourceExists() {
    Dimension d = factory.getDefaultSize(new Dimension(100, 100));
    assertNotEquals(100, d.width);
  }

  // ── paintIcon ─────────────────────────────────────────────────────

  @Test
  public void paintIcon_sameSize_noException() {
    Icon icon = factory.getIconExactSize(new Dimension(32, 32));
    BufferedImage canvas = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
    icon.paintIcon(null, canvas.getGraphics(), 0, 0);
  }

  @Test
  public void paintIcon_scaledDown_noException() {
    Icon icon = factory.getIconExactSize(new Dimension(16, 16));
    BufferedImage canvas = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
    icon.paintIcon(null, canvas.getGraphics(), 0, 0);
  }

  // ── Hierarchy ─────────────────────────────────────────────────────

  @Test
  public void class_extendsAbstractSingleSourceImageIconFactory() {
    assertTrue(AbstractSingleSourceImageIconFactory.class.isAssignableFrom(
        ImageIconFactory.class));
  }

  @Test
  public void class_isNotAbstract() {
    assertFalse(Modifier.isAbstract(ImageIconFactory.class.getModifiers()));
  }

  @Test
  public void class_isNotFinal() {
    assertFalse(Modifier.isFinal(ImageIconFactory.class.getModifiers()));
  }

  // ── Method existence ──────────────────────────────────────────────

  @Test
  public void method_createIcon_exists() throws Exception {
    Method m = ImageIconFactory.class.getDeclaredMethod("createIcon", Dimension.class);
    assertNotNull(m);
  }

  @Test
  public void method_getDefaultSize_exists() throws Exception {
    Method m = ImageIconFactory.class.getMethod("getDefaultSize", Dimension.class);
    assertNotNull(m);
  }

  // ── Constructor count ─────────────────────────────────────────────

  @Test
  public void hasThreeConstructors() {
    assertEquals(3, ImageIconFactory.class.getDeclaredConstructors().length);
  }

  // ── Different image sizes ─────────────────────────────────────────

  @Test
  public void smallImage_getDefaultSize() {
    BufferedImage small = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
    ImageIconFactory f = new ImageIconFactory(small);
    Dimension d = f.getDefaultSize(new Dimension(99, 99));
    assertEquals(8, d.width);
    assertEquals(8, d.height);
  }

  @Test
  public void largeImage_getDefaultSize() {
    BufferedImage large = new BufferedImage(128, 64, BufferedImage.TYPE_INT_ARGB);
    ImageIconFactory f = new ImageIconFactory(large);
    Dimension d = f.getDefaultSize(new Dimension(99, 99));
    assertEquals(128, d.width);
    assertEquals(64, d.height);
  }

  @Test
  public void toString_nonNull() {
    assertNotNull(factory.toString());
  }
}
