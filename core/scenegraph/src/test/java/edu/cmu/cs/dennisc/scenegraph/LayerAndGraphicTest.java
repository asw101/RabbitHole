package edu.cmu.cs.dennisc.scenegraph;

import edu.cmu.cs.dennisc.scenegraph.event.GraphicAddedEvent;
import edu.cmu.cs.dennisc.scenegraph.event.GraphicRemovedEvent;
import edu.cmu.cs.dennisc.scenegraph.event.GraphicsListener;
import org.junit.Test;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class LayerAndGraphicTest {

  // Concrete Graphic subclass for testing
  private static class TestGraphic extends Graphic {
  }

  @Test
  public void layerStartsEmpty() {
    Layer layer = new Layer();
    assertTrue(layer.getGraphics().isEmpty());
  }

  @Test
  public void addGraphicAddsToLayer() {
    Layer layer = new Layer();
    TestGraphic graphic = new TestGraphic();
    layer.addGraphic(graphic);

    Collection<Graphic> graphics = layer.getGraphics();
    assertEquals(1, graphics.size());
    assertTrue(graphics.contains(graphic));
    assertSame(layer, graphic.getParent());
  }

  @Test
  public void removeGraphicRemovesFromLayer() {
    Layer layer = new Layer();
    TestGraphic graphic = new TestGraphic();
    layer.addGraphic(graphic);
    layer.removeGraphic(graphic);

    assertTrue(layer.getGraphics().isEmpty());
    assertNull(graphic.getParent());
  }

  @Test(expected = RuntimeException.class)
  public void removeGraphicFromWrongLayerThrows() {
    Layer layer1 = new Layer();
    Layer layer2 = new Layer();
    TestGraphic graphic = new TestGraphic();
    layer1.addGraphic(graphic);

    layer2.removeGraphic(graphic);
  }

  @Test
  public void graphicsListenerReceivesAddEvent() {
    Layer layer = new Layer();
    AtomicInteger addCount = new AtomicInteger();
    layer.addGraphicsListener(new GraphicsListener() {
      @Override
      public void graphicAdded(GraphicAddedEvent e) {
        addCount.incrementAndGet();
      }
      @Override
      public void graphicRemoved(GraphicRemovedEvent e) {}
    });

    layer.addGraphic(new TestGraphic());
    assertEquals(1, addCount.get());
  }

  @Test
  public void graphicsListenerReceivesRemoveEvent() {
    Layer layer = new Layer();
    TestGraphic graphic = new TestGraphic();
    layer.addGraphic(graphic);

    AtomicInteger removeCount = new AtomicInteger();
    layer.addGraphicsListener(new GraphicsListener() {
      @Override
      public void graphicAdded(GraphicAddedEvent e) {}
      @Override
      public void graphicRemoved(GraphicRemovedEvent e) {
        removeCount.incrementAndGet();
      }
    });

    layer.removeGraphic(graphic);
    assertEquals(1, removeCount.get());
  }

  @Test
  public void removeGraphicsListenerStopsEvents() {
    Layer layer = new Layer();
    AtomicInteger addCount = new AtomicInteger();
    GraphicsListener listener = new GraphicsListener() {
      @Override
      public void graphicAdded(GraphicAddedEvent e) {
        addCount.incrementAndGet();
      }
      @Override
      public void graphicRemoved(GraphicRemovedEvent e) {}
    };

    layer.addGraphicsListener(listener);
    layer.addGraphic(new TestGraphic());
    assertEquals(1, addCount.get());

    layer.removeGraphicsListener(listener);
    layer.addGraphic(new TestGraphic());
    assertEquals(1, addCount.get());
  }

  @Test
  public void getGraphicsListenersReturnsRegistered() {
    Layer layer = new Layer();
    GraphicsListener listener = new GraphicsListener() {
      @Override
      public void graphicAdded(GraphicAddedEvent e) {}
      @Override
      public void graphicRemoved(GraphicRemovedEvent e) {}
    };

    layer.addGraphicsListener(listener);
    Collection<GraphicsListener> listeners = layer.getGraphicsListeners();
    assertEquals(1, listeners.size());
    assertTrue(listeners.contains(listener));
  }

  @Test
  public void multipleGraphicsCanBeAdded() {
    Layer layer = new Layer();
    TestGraphic g1 = new TestGraphic();
    TestGraphic g2 = new TestGraphic();
    TestGraphic g3 = new TestGraphic();

    layer.addGraphic(g1);
    layer.addGraphic(g2);
    layer.addGraphic(g3);

    assertEquals(3, layer.getGraphics().size());
  }

  @Test
  public void graphicParentChangesWhenReparented() {
    Layer layer1 = new Layer();
    Layer layer2 = new Layer();
    TestGraphic graphic = new TestGraphic();

    layer1.addGraphic(graphic);
    assertSame(layer1, graphic.getParent());

    graphic.setParent(layer2);
    assertSame(layer2, graphic.getParent());
    assertTrue(layer1.getGraphics().isEmpty());
    assertEquals(1, layer2.getGraphics().size());
  }

  @Test
  public void setParentToSameLayerIsNoOp() {
    Layer layer = new Layer();
    TestGraphic graphic = new TestGraphic();
    layer.addGraphic(graphic);

    AtomicInteger events = new AtomicInteger();
    layer.addGraphicsListener(new GraphicsListener() {
      @Override
      public void graphicAdded(GraphicAddedEvent e) { events.incrementAndGet(); }
      @Override
      public void graphicRemoved(GraphicRemovedEvent e) { events.incrementAndGet(); }
    });

    // Setting same parent should be no-op
    graphic.setParent(layer);
    assertEquals(0, events.get());
  }

  @Test
  public void graphicStartsWithNullParent() {
    TestGraphic graphic = new TestGraphic();
    assertNull(graphic.getParent());
  }
}
