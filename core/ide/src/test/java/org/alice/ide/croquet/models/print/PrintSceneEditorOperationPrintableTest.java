package org.alice.ide.croquet.models.print;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

public class PrintSceneEditorOperationPrintableTest {
  @Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }

  @Test
  public void printable_ignoresPagesAfterTheFirst() throws Exception {
    PrintSceneEditorOperation operation = new PrintSceneEditorOperation();
    Method method = PrintSceneEditorOperation.class.getDeclaredMethod("getPrintable");
    method.setAccessible(true);
    Printable printable = (Printable) method.invoke(operation);

    BufferedImage image = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = image.createGraphics();
    try {
      assertEquals(Printable.NO_SUCH_PAGE, printable.print(graphics, new PageFormat(), 1));
    } finally {
      graphics.dispose();
    }
  }
}
