package edu.cmu.cs.dennisc.render.joglrenderer;

import com.jogamp.opengl.util.awt.TextureRenderer;
import com.jogamp.opengl.util.packrect.Rect;
import org.junit.Test;

import java.awt.Font;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

import static org.junit.Assert.*;

public class ManagerBehaviorTest {
private static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 16);

  @Test
  public void deleteBackingStoreAcceptsHeadlessTextureRenderer() {
    SpyRenderer renderer = HeadlessTextRendererFactory.createRenderer(SpyRenderer.class, FONT);
    Manager manager = new Manager(renderer);
    TextureRenderer backingStore = HeadlessTextRendererFactory.HeadlessTextureRenderer.create(32, 16);

    assertEquals(32, backingStore.getWidth());
    assertEquals(16, backingStore.getHeight());
    manager.deleteBackingStore(backingStore);
  }

  @Test
  public void preExpandAttemptZeroFlushesAndClearsUnusedEntries() {
    SpyRenderer renderer = HeadlessTextRendererFactory.createRenderer(SpyRenderer.class, FONT);
    renderer.inBeginEndPair = true;
    Manager manager = new Manager(renderer);

    assertTrue(manager.preExpand(new Rect(0, 0, 4, 4, null), 0));
    assertEquals(1, renderer.flushCount);
    assertEquals(1, renderer.clearUnusedCount);
    assertFalse(manager.preExpand(new Rect(0, 0, 4, 4, null), 1));
  }

  @Test
  public void additionFailedClearsStateAndOnlyRetriesOnce() {
    SpyRenderer renderer = HeadlessTextRendererFactory.createRenderer(SpyRenderer.class, FONT);
    Manager manager = new Manager(renderer);
    Rect rect = new Rect(0, 0, 4, 4, new TextData("dead", new Point(), new Rectangle2D.Double(0, 0, 1, 1), 65));
    renderer.packer.add(rect);
    renderer.stringLocations.put("dead", rect);
    renderer.mGlyphProducer.register(new TextRendererGlyph(65, 2, 2.0f, null, renderer.mGlyphProducer, renderer));

    assertTrue(manager.additionFailed(rect, 0));
    assertTrue(renderer.stringLocations.isEmpty());
    assertEquals(TextRendererGlyphProducer.undefined, renderer.mGlyphProducer.unicodes2Glyphs['A']);
    assertFalse(manager.additionFailed(rect, 1));
  }

  @Test
  public void canCompactIsAlwaysTrue() {
    assertTrue(new Manager(HeadlessTextRendererFactory.createRenderer(SpyRenderer.class, FONT)).canCompact());
  }

  private static class SpyRenderer extends NonCachingTextRenderer {
    private int flushCount;
    private int clearUnusedCount;

    private SpyRenderer() {
      super(FONT);
    }

    @Override
    void flushGlyphPipeline() {
      this.flushCount++;
    }

    @Override
    void clearUnusedEntries() {
      this.clearUnusedCount++;
    }
  }
}
