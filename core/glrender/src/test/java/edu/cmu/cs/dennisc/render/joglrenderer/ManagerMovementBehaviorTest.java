package edu.cmu.cs.dennisc.render.joglrenderer;



import com.jogamp.opengl.util.packrect.Rect;
import org.junit.Test;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import static org.junit.Assert.*;

public class ManagerMovementBehaviorTest {



private static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 16);

  @Test
  public void moveCopiesWithinSameBackingStore() {
    NonCachingTextRenderer renderer = HeadlessTextRendererFactory.createRenderer(FONT);
    Manager manager = new Manager(renderer);
    HeadlessTextRendererFactory.HeadlessTextureRenderer store = HeadlessTextRendererFactory.HeadlessTextureRenderer.create(8, 8);
    paint(store, 0, 0, 2, 2, Color.RED);

    manager.beginMovement(store, store);
    manager.move(store, new Rect(0, 0, 2, 2, null), store, new Rect(3, 1, 2, 2, null));
    manager.endMovement(store, store);

    assertEquals(Color.RED.getRGB(), ((java.awt.image.BufferedImage) store.getImage()).getRGB(3, 1));
    assertEquals(Color.RED.getRGB(), ((java.awt.image.BufferedImage) store.getImage()).getRGB(4, 2));
    assertTrue(renderer.properties.needToResetColor);
    assertEquals(1, store.getMarkDirtyCount());
  }

  @Test
  public void moveCopiesBetweenDifferentBackingStores() {
    NonCachingTextRenderer renderer = HeadlessTextRendererFactory.createRenderer(FONT);
    Manager manager = new Manager(renderer);
    HeadlessTextRendererFactory.HeadlessTextureRenderer oldStore = HeadlessTextRendererFactory.HeadlessTextureRenderer.create(8, 8);
    HeadlessTextRendererFactory.HeadlessTextureRenderer newStore = HeadlessTextRendererFactory.HeadlessTextureRenderer.create(8, 8);
    paint(oldStore, 1, 2, 2, 2, Color.BLUE);

    manager.beginMovement(oldStore, newStore);
    manager.move(oldStore, new Rect(1, 2, 2, 2, null), newStore, new Rect(4, 3, 2, 2, null));
    manager.endMovement(oldStore, newStore);

    assertEquals(Color.BLUE.getRGB(), ((java.awt.image.BufferedImage) newStore.getImage()).getRGB(4, 3));
    assertTrue(renderer.properties.needToResetColor);
    assertEquals(1, newStore.getMarkDirtyCount());
  }

  private static void paint(HeadlessTextRendererFactory.HeadlessTextureRenderer renderer, int x, int y, int width, int height, Color color) {
    Graphics2D graphics = renderer.createGraphics();
    try {
      graphics.setColor(color);
      graphics.fillRect(x, y, width, height);
    } finally {
      graphics.dispose();
    }
  }
}
