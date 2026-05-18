package org.lgna.croquet.icon;

import org.junit.Test;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import static org.junit.Assert.*;

public class IconFactoryTest {

  @Test
  public void emptyIconFactory_isSingleton() {
    assertSame(EmptyIconFactory.getInstance(), EmptyIconFactory.getInstance());
  }

  @Test
  public void emptyIconFactory_exactSize_matchesRequestedDimensions() {
    Icon icon = EmptyIconFactory.getInstance().getIconExactSize(new Dimension(19, 11));

    assertEquals(19, icon.getIconWidth());
    assertEquals(11, icon.getIconHeight());
  }

  @Test
  public void emptyIconFactory_fitSize_matchesRequestedDimensions() {
    Icon icon = EmptyIconFactory.getInstance().getIconToFit(new Dimension(23, 17));

    assertEquals(23, icon.getIconWidth());
    assertEquals(17, icon.getIconHeight());
  }

  @Test
  public void imageIconFactory_defaultSize_comesFromSourceIcon() {
    Icon source = stubIcon(12, 10);
    ImageIconFactory factory = new ImageIconFactory(source);

    assertEquals(new Dimension(12, 10), factory.getDefaultSize(new Dimension(1, 1)));
  }

  @Test
  public void imageIconFactory_exactSourceSize_returnsWrappedSourceIcon() {
    Icon source = stubIcon(14, 9);
    ImageIconFactory factory = new ImageIconFactory(source);

    assertSame(source, factory.getIconExactSize(new Dimension(14, 9)));
  }

  @Test
  public void imageIconFactory_scaledSize_returnsIconWithRequestedDimensions() {
    Icon source = stubIcon(14, 9);
    ImageIconFactory factory = new ImageIconFactory(source);

    Icon scaled = factory.getIconExactSize(new Dimension(28, 18));

    assertNotSame(source, scaled);
    assertEquals(28, scaled.getIconWidth());
    assertEquals(18, scaled.getIconHeight());
  }

  @Test
  public void imageIconFactory_nullSource_usesFallbackSizingAndIconCreation() {
    ImageIconFactory factory = new ImageIconFactory((Icon) null);
    Dimension fallback = new Dimension(7, 5);

    Icon icon = factory.getIconExactSize(new Dimension(9, 6));

    assertEquals(fallback, factory.getDefaultSize(fallback));
    assertNotNull(icon);
    assertEquals(9, icon.getIconWidth());
    assertEquals(6, icon.getIconHeight());
  }

  @Test
  public void trimmedImageIconFactory_defaultSize_usesConfiguredSize() {
    TrimmedImageIconFactory factory = new TrimmedImageIconFactory(imageIcon(40, 20), 18, 12);

    assertEquals(new Dimension(18, 12), factory.getDefaultSize(new Dimension(1, 1)));
  }

  @Test
  public void trimmedImageIconFactory_exactSize_wrapsSourceIconInTrimmedIcon() {
    ImageIcon source = imageIcon(30, 10);
    TrimmedImageIconFactory factory = new TrimmedImageIconFactory(source, 18, 12);

    Icon icon = factory.getIconExactSize(new Dimension(24, 8));

    assertTrue(icon instanceof TrimmedIcon);
    assertSame(source, ((TrimmedIcon) icon).getImageIcon());
    assertEquals(24, icon.getIconWidth());
    assertEquals(8, icon.getIconHeight());
  }

  @Test
  public void trimmedImageIconFactory_fitSize_preservesConfiguredAspectRatio() {
    TrimmedImageIconFactory factory = new TrimmedImageIconFactory(imageIcon(30, 10), 18, 12);

    Icon icon = factory.getIconToFit(new Dimension(30, 30));

    assertEquals(30, icon.getIconWidth());
    assertEquals(20, icon.getIconHeight());
  }

  private static ImageIcon imageIcon(int width, int height) {
    return new ImageIcon(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB));
  }

  private static Icon stubIcon(int width, int height) {
    return new Icon() {
      @Override
      public void paintIcon(Component c, Graphics g, int x, int y) {
        g.fillRect(x, y, width, height);
      }

      @Override
      public int getIconWidth() {
        return width;
      }

      @Override
      public int getIconHeight() {
        return height;
      }
    };
  }
}
