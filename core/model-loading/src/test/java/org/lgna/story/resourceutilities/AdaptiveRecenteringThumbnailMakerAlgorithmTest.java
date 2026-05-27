package org.lgna.story.resourceutilities;

import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Point3;
import org.junit.Test;
import sun.misc.Unsafe;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

public class AdaptiveRecenteringThumbnailMakerAlgorithmTest {
  private static final double EPSILON = 0.000001;

  @Test
  public void recenterPositionStaysStableForBalancedImage() throws Exception {
    BufferedImage image = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
    paintRect(image, 2, 2, 5, 5);
    Point3 current = new Point3(1, 2, 3);
    AxisAlignedBox box = new AxisAlignedBox(new Point3(0, 0, 0), new Point3(4, 8, 2));

    Point3 recentered = invokeRecenterPosition(image, current, box);

    assertEquals(current, recentered);
  }

  @Test
  public void recenterPositionMovesTowardUnbalancedOpaqueRegion() throws Exception {
    BufferedImage image = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
    paintRect(image, 1, 0, 3, 3);
    Point3 current = new Point3(10, 20, 30);
    AxisAlignedBox box = new AxisAlignedBox(new Point3(0, 0, 0), new Point3(4, 8, 2));

    Point3 recentered = invokeRecenterPosition(image, current, box);

    assertEquals(8.5, recentered.x(), EPSILON);
    assertEquals(24.5, recentered.y(), EPSILON);
    assertEquals(30.0, recentered.z(), EPSILON);
  }

  private static Point3 invokeRecenterPosition(BufferedImage image, Point3 current, AxisAlignedBox box) throws Exception {
    AdaptiveRecenteringThumbnailMaker maker =
        (AdaptiveRecenteringThumbnailMaker) unsafe().allocateInstance(AdaptiveRecenteringThumbnailMaker.class);
    Method method = AdaptiveRecenteringThumbnailMaker.class.getDeclaredMethod(
        "getRecenterPositionBasedOnImage",
        BufferedImage.class,
        Point3.class,
        AxisAlignedBox.class
    );
    method.setAccessible(true);
    return (Point3) method.invoke(maker, image, current, box);
  }

  private static Unsafe unsafe() throws Exception {
    Field field = Unsafe.class.getDeclaredField("theUnsafe");
    field.setAccessible(true);
    return (Unsafe) field.get(null);
  }

  private static void paintRect(BufferedImage image, int startX, int startY, int width, int height) {
    for (int x = startX; x < startX + width; x++) {
      for (int y = startY; y < startY + height; y++) {
        image.setRGB(x, y, 0xff00ff00);
      }
    }
  }
}
