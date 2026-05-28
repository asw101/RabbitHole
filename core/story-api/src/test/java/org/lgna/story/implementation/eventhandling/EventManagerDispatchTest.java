package org.lgna.story.implementation.eventhandling;

import edu.cmu.cs.dennisc.java.awt.event.LenientMouseClickAdapter;
import edu.cmu.cs.dennisc.render.OnscreenRenderTarget;
import org.alice.interact.manipulator.StubOnscreenRenderTarget;
import org.junit.Before;
import org.junit.Test;
import org.lgna.story.MultipleEventPolicy;
import org.lgna.story.SScene;
import org.lgna.story.event.MouseClickOnScreenListener;
import org.lgna.story.event.SceneActivationListener;

import javax.swing.JPanel;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EventManagerDispatchTest {
  private EventManager eventManager;

  @Before
  public void setUp() {
    eventManager = new TestScene().getImplementation().getEventManager();
  }

  @Test
  public void sceneActivationListenersFireThroughEventManager() throws Exception {
    CountDownLatch latch = new CountDownLatch(3);
    AtomicInteger fired = new AtomicInteger();

    eventManager.addSceneActivationListener(event -> {
      fired.incrementAndGet();
      latch.countDown();
    });
    eventManager.addSceneActivationListener(event -> {
      fired.incrementAndGet();
      latch.countDown();
    });
    eventManager.addSceneActivationListener(event -> {
      fired.incrementAndGet();
      latch.countDown();
    });

    eventManager.sceneActivated();

    assertTrue("all registered listeners should fire", latch.await(2, TimeUnit.SECONDS));
    assertEquals(3, fired.get());
  }

  @Test
  public void duplicateSceneActivationRegistrationsFireForEachRegistration() throws Exception {
    AtomicInteger fired = new AtomicInteger();
    AtomicReference<CountDownLatch> latch = new AtomicReference<>(new CountDownLatch(2));
    SceneActivationListener listener = event -> {
      fired.incrementAndGet();
      latch.get().countDown();
    };

    eventManager.addSceneActivationListener(listener);
    eventManager.addSceneActivationListener(listener);
    eventManager.sceneActivated();

    assertTrue("duplicate registrations each fire independently", latch.get().await(2, TimeUnit.SECONDS));
    assertEquals(2, fired.get());

    eventManager.removeSceneActivationListener(listener);
    latch.set(new CountDownLatch(1));
    eventManager.sceneActivated();

    assertTrue("removing one registration still leaves one callback", latch.get().await(2, TimeUnit.SECONDS));
    assertEquals(3, fired.get());
  }

  @Test
  public void addAndRemoveListenersAdjustsAwtListenerCounts() {
    OnscreenRenderTarget target = StubOnscreenRenderTarget.downwardRays();
    JPanel component = target.getAwtComponent();

    assertEquals(0, component.getMouseListeners().length);
    assertEquals(0, component.getMouseMotionListeners().length);
    assertEquals(0, component.getKeyListeners().length);
    assertEquals(0, component.getFocusListeners().length);

    eventManager.addListenersTo(target);

    assertEquals(1, component.getMouseListeners().length);
    assertEquals(1, component.getMouseMotionListeners().length);
    assertEquals(1, component.getKeyListeners().length);
    assertEquals(1, component.getFocusListeners().length);

    eventManager.removeListenersFrom(target);

    assertEquals(0, component.getMouseListeners().length);
    assertEquals(0, component.getMouseMotionListeners().length);
    assertEquals(0, component.getKeyListeners().length);
    assertEquals(0, component.getFocusListeners().length);
  }

  @Test
  public void mouseAdapterTracksDoubleClickCountWithinThresholdAndResetsOutsideThreshold() throws Exception {
    OnscreenRenderTarget target = StubOnscreenRenderTarget.downwardRays();
    JPanel component = target.getAwtComponent();
    CountDownLatch latch = new CountDownLatch(3);
    AtomicInteger fired = new AtomicInteger();

    eventManager.addMouseClickOnScreenListener((MouseClickOnScreenListener) event -> {
      fired.incrementAndGet();
      latch.countDown();
    }, MultipleEventPolicy.COMBINE);
    eventManager.addListenersTo(target);

    dispatchClick(component, 100, 150, MouseEvent.BUTTON1, 10, 10);
    dispatchClick(component, 200, 240, MouseEvent.BUTTON1, 12, 12);
    assertEquals("nearby clicks should accumulate as a double-click sequence", 2, reflectedAdapterCount());

    dispatchClick(component, 900, 940, MouseEvent.BUTTON1, 10, 10);
    assertTrue(latch.await(2, TimeUnit.SECONDS));
    assertEquals(3, fired.get());
    assertEquals("late click should begin a new sequence", 1, reflectedAdapterCount());

    eventManager.removeListenersFrom(target);
  }

  @Test
  public void mouseAdapterDoesNotFilterByButtonWhenDispatchingScreenClicks() throws Exception {
    OnscreenRenderTarget target = StubOnscreenRenderTarget.downwardRays();
    JPanel component = target.getAwtComponent();
    CountDownLatch latch = new CountDownLatch(2);
    AtomicInteger fired = new AtomicInteger();

    eventManager.addMouseClickOnScreenListener((MouseClickOnScreenListener) event -> {
      fired.incrementAndGet();
      latch.countDown();
    }, MultipleEventPolicy.COMBINE);
    eventManager.addListenersTo(target);

    dispatchClick(component, 100, 150, MouseEvent.BUTTON1, 10, 10);
    dispatchClick(component, 200, 240, MouseEvent.BUTTON3, 12, 12);

    assertTrue(latch.await(2, TimeUnit.SECONDS));
    assertEquals(2, fired.get());
    assertEquals("mixed buttons still advance the adapter click sequence", 2, reflectedAdapterCount());

    eventManager.removeListenersFrom(target);
  }

  private void dispatchClick(JPanel component, long pressWhen, long releaseWhen, int button, int x, int y) {
    component.getMouseListeners()[0].mousePressed(mouseEvent(component, MouseEvent.MOUSE_PRESSED, pressWhen, button, x, y));
    component.getMouseListeners()[0].mouseReleased(mouseEvent(component, MouseEvent.MOUSE_RELEASED, releaseWhen, button, x, y));
  }

  private MouseEvent mouseEvent(JPanel component, int id, long when, int button, int x, int y) {
    return new MouseEvent(component, id, when, 0, x, y, 1, false, button);
  }

  private int reflectedAdapterCount() throws Exception {
    Field adapterField = EventManager.class.getDeclaredField("mouseAdapter");
    adapterField.setAccessible(true);
    Object adapter = adapterField.get(eventManager);
    Field countField = LenientMouseClickAdapter.class.getDeclaredField("count");
    countField.setAccessible(true);
    return countField.getInt(adapter);
  }

  private static final class TestScene extends SScene {
    @Override
    public void handleActiveChanged(Boolean isActive, Integer activationCount) {
    }
  }
}
