package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import edu.cmu.cs.dennisc.scenegraph.Box;
import edu.cmu.cs.dennisc.scenegraph.Scene;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.*;

public class ChangeHandlerSceneEventIntegrationTest {

@Before
  public void setUp() throws Exception {
    AdapterRenderTestSupport.resetFactory();
    resetChangeHandlerState();
  }

  @After
  public void tearDown() throws Exception {
    resetChangeHandlerState();
  }

  @Test
  public void bufferedSceneChangesApplyWhenRenderingModeEnds() {
    Scene scene = new Scene();
    Visual visual = AdapterRenderTestSupport.visualWith(new Box(), AdapterRenderTestSupport.appearance(1.0f));

    GlrScene sceneAdapter = AdapterRenderTestSupport.sceneAdapter(scene);
    GlrVisual<Visual> visualAdapter = AdapterRenderTestSupport.visualAdapter(visual);
    ChangeHandler.resetEventCount();

    ChangeHandler.pushRenderingMode();
    scene.addComponent(visual);

    assertTrue(ChangeHandler.getEventCountSinceLastReset() >= 2);
    assertEquals(0, childCount(sceneAdapter));

    ChangeHandler.popRenderingMode();

    assertEquals(1, childCount(sceneAdapter));
    assertSame(sceneAdapter, visualAdapter.getGlrScene());
  }

  @Test
  public void removingListenersStopsFutureEventCounting() {
    Scene scene = new Scene();
    Visual visual = AdapterRenderTestSupport.visualWith(new Box(), AdapterRenderTestSupport.appearance(1.0f));

    AdapterRenderTestSupport.sceneAdapter(scene);
    AdapterRenderTestSupport.visualAdapter(visual);
    ChangeHandler.removeListeners(scene);
    ChangeHandler.removeListeners(visual);
    ChangeHandler.resetEventCount();

    scene.addComponent(visual);
    visual.isShowing.setValue(true);

    assertEquals(0, ChangeHandler.getEventCountSinceLastReset());
  }

  private static int childCount(GlrScene sceneAdapter) {
    int count = 0;
    for (GlrComponent<?> ignored : sceneAdapter.accessChildren()) {
      count++;
    }
    return count;
  }

  @SuppressWarnings("unchecked")
  private static void resetChangeHandlerState() throws Exception {
    ChangeHandler.resetEventCount();
    Field renderingModeCount = ChangeHandler.class.getDeclaredField("renderingModeCount");
    renderingModeCount.setAccessible(true);
    renderingModeCount.setInt(null, 0);
    Field bufferedEvents = ChangeHandler.class.getDeclaredField("bufferedEvents");
    bufferedEvents.setAccessible(true);
    List<?> events = (List<?>) bufferedEvents.get(null);
    synchronized (events) {
      events.clear();
    }
  }
}
