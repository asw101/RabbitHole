package org.alice.netbeans.palette;

import org.junit.Test;
import org.netbeans.spi.palette.PaletteController;

import java.lang.reflect.Field;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public class Alice3PaletteFactoryTest {

  @Test
  public void createPaletteReturnsCachedControllerWhenAlreadyInitialized() throws Exception {
    resetPalette();
    PaletteController cached = emptyPaletteController();
    try {
      cachePalette(cached);

      PaletteController result = Alice3PaletteFactory.createPalette();

      assertNotNull(result);
      assertSame(cached, result);
    } finally {
      resetPalette();
    }
  }

  private static void resetPalette() throws Exception {
    Field field = Alice3PaletteFactory.class.getDeclaredField("palette");
    field.setAccessible(true);
    field.set(null, null);
  }

  private static void cachePalette(PaletteController paletteController) throws Exception {
    Field field = Alice3PaletteFactory.class.getDeclaredField("palette");
    field.setAccessible(true);
    field.set(null, paletteController);
  }

  private static PaletteController emptyPaletteController() throws Exception {
    var constructor = PaletteController.class.getDeclaredConstructor();
    constructor.setAccessible(true);
    return constructor.newInstance();
  }
}
