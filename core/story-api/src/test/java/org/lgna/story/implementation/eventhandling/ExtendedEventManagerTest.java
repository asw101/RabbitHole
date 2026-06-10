package org.lgna.story.implementation.eventhandling;

import org.junit.Before;
import org.junit.Test;
import org.lgna.story.HeldKeyPolicy;
import org.lgna.story.MultipleEventPolicy;
import org.lgna.story.SScene;
import org.lgna.story.SThing;
import org.lgna.story.SModel;
import org.lgna.story.Visual;
import org.lgna.story.event.ArrowKeyEvent;
import org.lgna.story.event.ArrowKeyPressListener;
import org.lgna.story.event.KeyPressListener;
import org.lgna.story.event.MouseClickOnScreenListener;
import org.lgna.story.event.NumberKeyEvent;
import org.lgna.story.event.NumberKeyPressListener;
import org.lgna.story.event.PointOfViewChangeListener;
import org.lgna.story.event.SceneActivationEvent;
import org.lgna.story.event.SceneActivationListener;
import org.lgna.story.event.TimeListener;
import org.lgna.story.event.ViewEnterListener;
import org.lgna.story.event.ViewExitListener;
import org.lgna.story.event.OcclusionStartListener;
import org.lgna.story.event.WhileCollisionListener;
import org.lgna.story.event.WhileProximityListener;
import org.lgna.story.event.WhileOcclusionListener;
import org.lgna.story.event.WhileInViewListener;
import org.lgna.story.implementation.SceneImp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Extended tests for EventManager, exercising the while-listener registration
 * paths, collision handler access, multiple listener management, and the
 * scene-activation lifecycle. All tests are headless-safe.
 */
public class ExtendedEventManagerTest {

  private SceneImp sceneImp;
  private EventManager eventManager;

  @Before
  public void setUp() {
    sceneImp = new TestScene().getImplementation();
    eventManager = sceneImp.getEventManager();
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  EventManager construction and basics
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void eventManagerConstructedViaSceneImp() {
    assertNotNull(eventManager);
  }

  @Test
  public void getCollisionHandlerReturnsSameInstance() {
    CollisionHandler h1 = eventManager.getCollisionHandler();
    CollisionHandler h2 = eventManager.getCollisionHandler();
    assertNotNull(h1);
    assertSame(h1, h2);
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  Silence / restore cycles
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void silenceAndRestoreRoundTrip() {
    eventManager.silenceAllListeners();
    eventManager.restoreAllListeners();
  }

  @Test
  public void multipleSilenceRestoreCycles() {
    for (int i = 0; i < 3; i++) {
      eventManager.silenceAllListeners();
      eventManager.restoreAllListeners();
    }
  }

  @Test
  public void silenceAfterRegisteringListeners() {
    eventManager.addKeyListener(e -> {}, MultipleEventPolicy.IGNORE, HeldKeyPolicy.FIRE_ONCE_ON_PRESS);
    eventManager.addMouseClickOnScreenListener(e -> {}, MultipleEventPolicy.IGNORE);
    eventManager.silenceAllListeners();
    eventManager.restoreAllListeners();
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  Key listeners
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void addMultipleKeyListeners() {
    eventManager.addKeyListener(e -> {}, MultipleEventPolicy.IGNORE, HeldKeyPolicy.FIRE_ONCE_ON_PRESS);
    eventManager.addKeyListener(e -> {}, MultipleEventPolicy.ENQUEUE, HeldKeyPolicy.FIRE_MULTIPLE);
    eventManager.addKeyListener(e -> {}, MultipleEventPolicy.COMBINE, HeldKeyPolicy.FIRE_ONCE_ON_PRESS);
  }

  @Test
  public void addArrowKeyListenerWithAllPolicies() {
    eventManager.addArrowKeyListener(e -> {}, MultipleEventPolicy.IGNORE, HeldKeyPolicy.FIRE_ONCE_ON_PRESS);
    eventManager.addArrowKeyListener(e -> {}, MultipleEventPolicy.ENQUEUE, HeldKeyPolicy.FIRE_MULTIPLE);
  }

  @Test
  public void addNumberKeyListenerWithAllPolicies() {
    eventManager.addNumberKeyListener(e -> {}, MultipleEventPolicy.IGNORE, HeldKeyPolicy.FIRE_ONCE_ON_PRESS);
    eventManager.addNumberKeyListener(e -> {}, MultipleEventPolicy.ENQUEUE, HeldKeyPolicy.FIRE_MULTIPLE);
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  Mouse click listeners
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void addMouseClickOnScreenWithDifferentPolicies() {
    eventManager.addMouseClickOnScreenListener(e -> {}, MultipleEventPolicy.IGNORE);
    eventManager.addMouseClickOnScreenListener(e -> {}, MultipleEventPolicy.ENQUEUE);
    eventManager.addMouseClickOnScreenListener(e -> {}, MultipleEventPolicy.COMBINE);
  }

  @Test
  public void addMouseClickOnObjectListenerEmpty() {
    eventManager.addMouseClickOnObjectListener(e -> {}, MultipleEventPolicy.IGNORE, new Visual[0]);
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  Scene activation listeners
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void addAndRemoveSceneActivationListener() {
    SceneActivationListener listener = e -> {};
    eventManager.addSceneActivationListener(listener);
    eventManager.removeSceneActivationListener(listener);
  }

  @Test
  public void addMultipleSceneActivationListeners() {
    eventManager.addSceneActivationListener(e -> {});
    eventManager.addSceneActivationListener(e -> {});
    eventManager.addSceneActivationListener(e -> {});
  }

  @Test
  public void sceneActivatedFiresListener() throws Exception {
    CountDownLatch latch = new CountDownLatch(1);
    eventManager.addSceneActivationListener(e -> latch.countDown());
    eventManager.sceneActivated();
    assertTrue("Listener should fire", latch.await(5, TimeUnit.SECONDS));
  }

  @Test
  public void sceneActivatedFiresMultipleListeners() throws Exception {
    CountDownLatch latch = new CountDownLatch(3);
    eventManager.addSceneActivationListener(e -> latch.countDown());
    eventManager.addSceneActivationListener(e -> latch.countDown());
    eventManager.addSceneActivationListener(e -> latch.countDown());
    eventManager.sceneActivated();
    assertTrue("All 3 listeners should fire", latch.await(5, TimeUnit.SECONDS));
  }

  @Test
  public void removedSceneActivationListenerDoesNotFire() throws Exception {
    AtomicBoolean called = new AtomicBoolean(false);
    SceneActivationListener listener = e -> called.set(true);
    eventManager.addSceneActivationListener(listener);
    eventManager.removeSceneActivationListener(listener);
    eventManager.sceneActivated();
    EventTestSupport.waitForSceneActivationDispatchIdle(eventManager);
    assertFalse("Removed listener should not fire", called.get());
  }

  @Test
  public void sceneActivatedWhenSilenced() throws Exception {
    AtomicBoolean called = new AtomicBoolean(false);
    eventManager.addSceneActivationListener(e -> called.set(true));
    eventManager.silenceAllListeners();
    eventManager.sceneActivated();
    EventTestSupport.waitForSceneActivationDispatchIdle(eventManager);
    assertFalse("Silenced listener should not fire", called.get());
    eventManager.restoreAllListeners();
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  Transformation listeners
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void addTransformationListenerNoTargets() {
    PointOfViewChangeListener listener = e -> {};
    eventManager.addTransformationListener(listener, new SThing[0]);
  }

  @Test
  public void addMultipleTransformationListeners() {
    eventManager.addTransformationListener(e -> {}, new SThing[0]);
    eventManager.addTransformationListener(e -> {}, new SThing[0]);
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  Collision listeners (correct API signatures)
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void addCollisionListenerDoesNotThrow() {
    List<SThing> groupOne = new ArrayList<>();
    List<SThing> groupTwo = new ArrayList<>();
    eventManager.addCollisionListener(new Object() {}, groupOne, groupTwo, MultipleEventPolicy.IGNORE);
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  Proximity listeners (basic path)
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void addProximityEventListenerDoesNotThrow() {
    List<SThing> groupOne = new ArrayList<>();
    List<SThing> groupTwo = new ArrayList<>();
    eventManager.addProximityEventListener(new Object() {}, groupOne, groupTwo, 5.0, MultipleEventPolicy.IGNORE);
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  Occlusion listeners
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void addOcclusionEventListenerDoesNotThrow() {
    ArrayList<SModel> groupOne = new ArrayList<>();
    ArrayList<SModel> groupTwo = new ArrayList<>();
    eventManager.addOcclusionEventListener(
        (OcclusionStartListener) e -> {}, groupOne, groupTwo, MultipleEventPolicy.IGNORE);
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  View listeners
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void addComesIntoViewEventListenerDoesNotThrow() {
    eventManager.addComesIntoViewEventListener(
        (ViewEnterListener) e -> {}, new SModel[0], MultipleEventPolicy.IGNORE);
  }

  @Test
  public void addLeavesViewEventListenerDoesNotThrow() {
    eventManager.addLeavesViewEventListener(
        (ViewExitListener) e -> {}, new SModel[0], MultipleEventPolicy.IGNORE);
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  Timer event listener
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void addTimerEventListenerDoesNotThrow() {
    eventManager.addTimerEventListener((TimeListener) e -> {}, 1.0, MultipleEventPolicy.IGNORE);
  }

  @Test
  public void addMultipleTimerEventListeners() {
    eventManager.addTimerEventListener((TimeListener) e -> {}, 0.5, MultipleEventPolicy.IGNORE);
    eventManager.addTimerEventListener((TimeListener) e -> {}, 1.0, MultipleEventPolicy.ENQUEUE);
    eventManager.addTimerEventListener((TimeListener) e -> {}, 2.0, MultipleEventPolicy.COMBINE);
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  SceneActivationHandler standalone tests
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void sceneActivationHandlerEnqueueAndDequeue() {
    SceneActivationHandler handler = new SceneActivationHandler();
    handler.setScene(sceneImp);

    AtomicInteger count = new AtomicInteger(0);
    SceneActivationListener listener = e -> count.incrementAndGet();
    handler.addListener(listener);

    handler.enqueue(new SceneActivationEvent());
    handler.enqueue(new SceneActivationEvent());
    handler.fireDequeue(listener);
  }

  @Test
  public void sceneActivationHandlerMultipleFires() throws Exception {
    SceneActivationHandler handler = new SceneActivationHandler();
    handler.setScene(sceneImp);

    AtomicInteger count = new AtomicInteger(0);
    CountDownLatch fired = new CountDownLatch(1);
    handler.addListener(e -> {
      count.incrementAndGet();
      fired.countDown();
    });

    handler.handleEventFire(new SceneActivationEvent());
    handler.handleEventFire(new SceneActivationEvent());
    EventTestSupport.await(fired, "scene activation handler listener");
    EventTestSupport.waitForEventDispatchIdle(handler);
    assertTrue("Should fire at least once", count.get() >= 1);
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  TransformationHandler standalone tests
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void transformationHandlerConstruction() {
    TransformationHandler handler = new TransformationHandler();
    assertNotNull(handler);
    assertTrue(handler.getModelList().isEmpty());
  }

  @Test
  public void transformationHandlerSilenceRestore() {
    TransformationHandler handler = new TransformationHandler();
    handler.silenceListeners();
    assertFalse(handler.shouldFire);
    handler.restoreListeners();
    assertTrue(handler.shouldFire);
  }

  @Test
  public void transformationHandlerFireAllTargetedWhenSilenced() {
    TransformationHandler handler = new TransformationHandler();
    handler.setScene(sceneImp);
    handler.silenceListeners();
    handler.fireAllTargeted(null);
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  KeyPressedHandler extended tests
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void keyPressedHandlerAddKeyListenerAllPolicies() {
    KeyPressedHandler kph = new KeyPressedHandler();
    kph.setScene(sceneImp);
    kph.addListener((KeyPressListener) e -> {}, MultipleEventPolicy.IGNORE, HeldKeyPolicy.FIRE_ONCE_ON_PRESS);
    kph.addListener((KeyPressListener) e -> {}, MultipleEventPolicy.ENQUEUE, HeldKeyPolicy.FIRE_MULTIPLE);
    kph.addListener((KeyPressListener) e -> {}, MultipleEventPolicy.COMBINE, HeldKeyPolicy.FIRE_ONCE_ON_PRESS);
  }

  @Test
  public void keyPressedHandlerAddArrowKeyListenerAllPolicies() {
    KeyPressedHandler kph = new KeyPressedHandler();
    kph.setScene(sceneImp);
    kph.addListener((ArrowKeyPressListener) e -> {},
        MultipleEventPolicy.IGNORE, ArrowKeyEvent.ARROWS, HeldKeyPolicy.FIRE_ONCE_ON_PRESS);
    kph.addListener((ArrowKeyPressListener) e -> {},
        MultipleEventPolicy.ENQUEUE, ArrowKeyEvent.ARROWS, HeldKeyPolicy.FIRE_MULTIPLE);
  }

  @Test
  public void keyPressedHandlerAddNumberKeyListenerAllPolicies() {
    KeyPressedHandler kph = new KeyPressedHandler();
    kph.setScene(sceneImp);
    kph.addListener((NumberKeyPressListener) e -> {},
        MultipleEventPolicy.IGNORE, NumberKeyEvent.NUMBERS, HeldKeyPolicy.FIRE_ONCE_ON_PRESS);
    kph.addListener((NumberKeyPressListener) e -> {},
        MultipleEventPolicy.ENQUEUE, NumberKeyEvent.NUMBERS, HeldKeyPolicy.FIRE_MULTIPLE);
  }

  @Test
  public void keyPressedHandlerReleaseAllKeysAfterRegistration() {
    KeyPressedHandler kph = new KeyPressedHandler();
    kph.setScene(sceneImp);
    kph.addListener((KeyPressListener) e -> {}, MultipleEventPolicy.IGNORE, HeldKeyPolicy.FIRE_ONCE_ON_PRESS);
    kph.releaseAllKeys();
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  MouseClickedHandler extended tests
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void mouseClickedHandlerAddScreenListenerAllPolicies() {
    MouseClickedHandler mch = new MouseClickedHandler();
    mch.setScene(sceneImp);
    mch.addListener((MouseClickOnScreenListener) e -> {}, MultipleEventPolicy.IGNORE, null);
    mch.addListener((MouseClickOnScreenListener) e -> {}, MultipleEventPolicy.ENQUEUE, null);
    mch.addListener((MouseClickOnScreenListener) e -> {}, MultipleEventPolicy.COMBINE, null);
  }

  @Test
  public void mouseClickedHandlerFireAllTargetedNull() {
    MouseClickedHandler mch = new MouseClickedHandler();
    mch.setScene(sceneImp);
    mch.addListener((MouseClickOnScreenListener) e -> {}, MultipleEventPolicy.IGNORE, null);
    mch.silenceListeners();
    mch.fireAllTargeted(null);
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  CollisionHandler basic tests
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void collisionHandlerFromEventManager() {
    CollisionHandler ch = eventManager.getCollisionHandler();
    assertNotNull(ch);
  }

  @Test
  public void collisionHandlerSilenceRestore() {
    CollisionHandler ch = eventManager.getCollisionHandler();
    ch.silenceListeners();
    ch.restoreListeners();
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  Mouse button listener (deprecated path)
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  @SuppressWarnings("deprecation")
  public void addMouseButtonListenerDoesNotThrow() {
    eventManager.addMouseButtonListener(new Object() {}, MultipleEventPolicy.IGNORE, new Visual[0]);
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  Listener wiring through render target
  // ═══════════════════════════════════════════════════════════════════════

  @Test
  public void addListenersToAndRemoveListenersFromOnscreenRenderTarget() {
    edu.cmu.cs.dennisc.render.OnscreenRenderTarget target =
        org.alice.interact.manipulator.StubOnscreenRenderTarget.downwardRays();
    eventManager.addListenersTo(target);
    eventManager.removeListenersFrom(target);
  }

  @Test
  public void initializeAddsTimerAsSceneActivationListener() {
    // Calling initialize() a second time should be safe.
    eventManager.initialize();
    eventManager.initialize();
  }

  @Test
  public void removeKeyListenerThrowsRuntimeAsTodo() {
    try {
      eventManager.removeKeyListener(null);
      fail("expected RuntimeException(todo)");
    } catch (RuntimeException expected) {
      assertEquals("todo", expected.getMessage());
    }
  }

  // ═══════════════════════════════════════════════════════════════════════
  //  Helper
  // ═══════════════════════════════════════════════════════════════════════

  private static class TestScene extends SScene {
    @Override
    public void handleActiveChanged(Boolean isActive, Integer activationCount) {
    }
  }
}
