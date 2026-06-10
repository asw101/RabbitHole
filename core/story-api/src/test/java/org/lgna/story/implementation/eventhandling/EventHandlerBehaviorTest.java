package org.lgna.story.implementation.eventhandling;

import org.junit.Before;
import org.junit.Test;
import org.lgna.story.HeldKeyPolicy;
import org.lgna.story.MultipleEventPolicy;
import org.lgna.story.SScene;
import org.lgna.story.Visual;
import org.lgna.story.event.ArrowKeyEvent;
import org.lgna.story.event.ArrowKeyPressListener;
import org.lgna.story.event.KeyEvent;
import org.lgna.story.event.KeyPressListener;
import org.lgna.story.event.MouseClickOnScreenEvent;
import org.lgna.story.event.MouseClickOnScreenListener;
import org.lgna.story.event.PointOfViewChangeListener;
import org.lgna.story.event.PointOfViewEvent;
import org.lgna.story.event.SceneActivationEvent;
import org.lgna.story.event.SceneActivationListener;
import org.lgna.story.implementation.SceneImp;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Headless-safe tests for the event-handler classes:
 * AbstractEventHandler (via SceneActivationHandler),
 * SceneActivationHandler, TransformationHandler,
 * KeyPressedHandler, MouseClickedHandler, and EventManager.
 */
public class EventHandlerBehaviorTest {

  private SceneActivationHandler activationHandler;
  private TransformationHandler transformationHandler;
  private SceneImp sceneImp;

  @Before
  public void setUp() {
    activationHandler = new SceneActivationHandler();
    transformationHandler = new TransformationHandler();
    sceneImp = new TestScene().getImplementation();
    activationHandler.setScene(sceneImp);
    transformationHandler.setScene(sceneImp);
  }

  // ── SceneActivationHandler construction ──

  @Test
  public void sceneActivationHandlerConstructsSuccessfully() {
    assertNotNull(activationHandler);
  }

  // ── SceneActivationHandler add/remove listeners ──

  @Test
  public void addListenerDoesNotThrow() {
    activationHandler.addListener(e -> {});
  }

  @Test
  public void removeListenerDoesNotThrow() {
    SceneActivationListener listener = e -> {};
    activationHandler.addListener(listener);
    activationHandler.removeListener(listener);
  }

  @Test
  public void addMultipleListenersDoesNotThrow() {
    activationHandler.addListener(e -> {});
    activationHandler.addListener(e -> {});
    activationHandler.addListener(e -> {});
  }

  // ── AbstractEventHandler silence/restore ──

  @Test
  public void silenceListenersPreventsFieldFromBeingTrue() {
    activationHandler.silenceListeners();
    assertFalse(activationHandler.shouldFire);
  }

  @Test
  public void restoreListenersResetsFieldToTrue() {
    activationHandler.silenceListeners();
    activationHandler.restoreListeners();
    assertTrue(activationHandler.shouldFire);
  }

  @Test
  public void initialShouldFireIsTrue() {
    assertTrue(activationHandler.shouldFire);
  }

  // ── AbstractEventHandler setScene ──

  @Test
  public void setSceneSetsSceneField() {
    assertSame(sceneImp, activationHandler.scene);
  }

  // ── AbstractEventHandler registerPolicyMap ──

  @Test
  public void registerPolicyMapStoresPolicy() {
    SceneActivationListener listener = e -> {};
    activationHandler.addListener(listener);
    assertEquals(MultipleEventPolicy.IGNORE, activationHandler.policyMap.get(listener));
  }

  // ── AbstractEventHandler registerIsFiringMap ──

  @Test
  public void registerIsFiringMapCreatesEntry() {
    SceneActivationListener listener = e -> {};
    activationHandler.addListener(listener);
    assertTrue(activationHandler.isFiringMap.containsKey(listener));
    assertFalse(activationHandler.isFiringMap.get(listener).get(listener));
  }

  // ── SceneActivationHandler handleEventFire with silenced listeners ──

  @Test
  public void handleEventFireWhenSilencedDoesNotCallListener() {
    AtomicBoolean called = new AtomicBoolean(false);
    activationHandler.addListener(e -> called.set(true));
    activationHandler.silenceListeners();
    activationHandler.handleEventFire(new SceneActivationEvent());
    assertFalse("Listener should not fire when silenced", called.get());
  }

  // ── TransformationHandler construction ──

  @Test
  public void transformationHandlerConstructsSuccessfully() {
    assertNotNull(transformationHandler);
  }

  @Test
  public void transformationHandlerModelListEmptyInitially() {
    assertTrue(transformationHandler.getModelList().isEmpty());
  }

  // ── TransformationChangedHandler.fireAllTargeted when silenced ──

  @Test
  public void fireAllTargetedWhenSilencedDoesNothing() {
    transformationHandler.silenceListeners();
    // No registered models, no listener, should not throw
    transformationHandler.fireAllTargeted(null);
  }

  // ── Multiple silence/restore cycles ──

  @Test
  public void multipleSilenceRestoreCycles() {
    for (int i = 0; i < 5; i++) {
      activationHandler.silenceListeners();
      assertFalse(activationHandler.shouldFire);
      activationHandler.restoreListeners();
      assertTrue(activationHandler.shouldFire);
    }
  }

  // ── Count field on AbstractEventHandler ──

  @Test
  public void countFieldInitiallyZero() {
    assertEquals(Integer.valueOf(0), activationHandler.count);
  }

  // ── NULL_OBJECT sentinel ──

  @Test
  public void nullObjectSentinelIsNotNull() {
    assertNotNull(activationHandler.NULL_OBJECT);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  KeyPressedHandler — construction and listener registration
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void keyPressedHandlerConstructsSuccessfully() {
    KeyPressedHandler kph = new KeyPressedHandler();
    assertNotNull(kph);
  }

  @Test
  public void keyPressedHandlerAddKeyPressListenerDoesNotThrow() {
    KeyPressedHandler kph = new KeyPressedHandler();
    kph.setScene(sceneImp);
    kph.addListener((KeyPressListener) e -> {}, MultipleEventPolicy.IGNORE, HeldKeyPolicy.FIRE_ONCE_ON_PRESS);
  }

  @Test
  public void keyPressedHandlerAddArrowKeyListenerDoesNotThrow() {
    KeyPressedHandler kph = new KeyPressedHandler();
    kph.setScene(sceneImp);
    kph.addListener((ArrowKeyPressListener) e -> {},
        MultipleEventPolicy.IGNORE, ArrowKeyEvent.ARROWS, HeldKeyPolicy.FIRE_ONCE_ON_PRESS);
  }

  @Test
  public void keyPressedHandlerRegistersPolicyMapEntry() {
    KeyPressedHandler kph = new KeyPressedHandler();
    kph.setScene(sceneImp);
    KeyPressListener listener = e -> {};
    kph.addListener(listener, MultipleEventPolicy.ENQUEUE, HeldKeyPolicy.FIRE_ONCE_ON_PRESS);
    assertEquals(MultipleEventPolicy.ENQUEUE, kph.policyMap.get(listener));
  }

  @Test
  public void keyPressedHandlerRegistersIsFiringMapEntry() {
    KeyPressedHandler kph = new KeyPressedHandler();
    kph.setScene(sceneImp);
    KeyPressListener listener = e -> {};
    kph.addListener(listener, MultipleEventPolicy.IGNORE, HeldKeyPolicy.FIRE_ONCE_ON_PRESS);
    assertTrue(kph.isFiringMap.containsKey(listener));
  }

  @Test
  public void keyPressedHandlerSilenceAndRestore() {
    KeyPressedHandler kph = new KeyPressedHandler();
    assertTrue(kph.shouldFire);
    kph.silenceListeners();
    assertFalse(kph.shouldFire);
    kph.restoreListeners();
    assertTrue(kph.shouldFire);
  }

  @Test
  public void keyPressedHandlerReleaseAllKeysDoesNotThrow() {
    KeyPressedHandler kph = new KeyPressedHandler();
    kph.releaseAllKeys();
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  MouseClickedHandler — construction and listener registration
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void mouseClickedHandlerConstructsSuccessfully() {
    MouseClickedHandler mch = new MouseClickedHandler();
    assertNotNull(mch);
  }

  @Test
  public void mouseClickedHandlerAddScreenListenerDoesNotThrow() {
    MouseClickedHandler mch = new MouseClickedHandler();
    mch.setScene(sceneImp);
    mch.addListener((MouseClickOnScreenListener) e -> {},
        MultipleEventPolicy.IGNORE, null);
  }

  @Test
  public void mouseClickedHandlerAddListenerWithAllVisualsDoesNotThrow() {
    MouseClickedHandler mch = new MouseClickedHandler();
    mch.setScene(sceneImp);
    mch.addListener((MouseClickOnScreenListener) e -> {},
        MultipleEventPolicy.IGNORE, MouseClickedHandler.ALL_VISUALS);
  }

  @Test
  public void mouseClickedHandlerRegistersPolicyMap() {
    MouseClickedHandler mch = new MouseClickedHandler();
    mch.setScene(sceneImp);
    MouseClickOnScreenListener listener = e -> {};
    mch.addListener(listener, MultipleEventPolicy.ENQUEUE, null);
    assertEquals(MultipleEventPolicy.ENQUEUE, mch.policyMap.get(listener));
  }

  @Test
  public void mouseClickedHandlerSilenceAndRestore() {
    MouseClickedHandler mch = new MouseClickedHandler();
    assertTrue(mch.shouldFire);
    mch.silenceListeners();
    assertFalse(mch.shouldFire);
    mch.restoreListeners();
    assertTrue(mch.shouldFire);
  }

  @Test
  public void mouseClickedHandlerFireAllTargetedWhenSilencedDoesNothing() {
    MouseClickedHandler mch = new MouseClickedHandler();
    mch.silenceListeners();
    mch.fireAllTargeted(null);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  EventManager — via SceneImp
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void eventManagerIsNotNull() {
    EventManager em = sceneImp.getEventManager();
    assertNotNull(em);
  }

  @Test
  public void eventManagerSilenceAndRestoreAllListeners() {
    EventManager em = sceneImp.getEventManager();
    em.silenceAllListeners();
    em.restoreAllListeners();
  }

  @Test
  public void eventManagerAddKeyListenerDoesNotThrow() {
    EventManager em = sceneImp.getEventManager();
    em.addKeyListener(e -> {}, MultipleEventPolicy.IGNORE, HeldKeyPolicy.FIRE_ONCE_ON_PRESS);
  }

  @Test
  public void eventManagerAddArrowKeyListenerDoesNotThrow() {
    EventManager em = sceneImp.getEventManager();
    em.addArrowKeyListener(e -> {}, MultipleEventPolicy.IGNORE, HeldKeyPolicy.FIRE_ONCE_ON_PRESS);
  }

  @Test
  public void eventManagerAddNumberKeyListenerDoesNotThrow() {
    EventManager em = sceneImp.getEventManager();
    em.addNumberKeyListener(e -> {}, MultipleEventPolicy.IGNORE, HeldKeyPolicy.FIRE_ONCE_ON_PRESS);
  }

  @Test
  public void eventManagerAddMouseClickOnScreenListenerDoesNotThrow() {
    EventManager em = sceneImp.getEventManager();
    em.addMouseClickOnScreenListener(e -> {}, MultipleEventPolicy.IGNORE);
  }

  @Test
  public void eventManagerAddMouseClickOnObjectListenerDoesNotThrow() {
    EventManager em = sceneImp.getEventManager();
    em.addMouseClickOnObjectListener(e -> {}, MultipleEventPolicy.IGNORE, new Visual[0]);
  }

  @Test
  public void eventManagerAddSceneActivationListenerDoesNotThrow() {
    EventManager em = sceneImp.getEventManager();
    SceneActivationListener listener = e -> {};
    em.addSceneActivationListener(listener);
  }

  @Test
  public void eventManagerRemoveSceneActivationListenerDoesNotThrow() {
    EventManager em = sceneImp.getEventManager();
    SceneActivationListener listener = e -> {};
    em.addSceneActivationListener(listener);
    em.removeSceneActivationListener(listener);
  }

  @Test
  public void eventManagerSceneActivatedDoesNotThrow() {
    EventManager em = sceneImp.getEventManager();
    em.sceneActivated();
  }

  @Test
  public void eventManagerSceneActivatedFiresRegisteredListener() throws Exception {
    EventManager em = sceneImp.getEventManager();
    java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
    em.addSceneActivationListener(e -> latch.countDown());
    em.sceneActivated();
    assertTrue("Scene activation listener should fire on sceneActivated()",
        latch.await(5, java.util.concurrent.TimeUnit.SECONDS));
  }

  @Test
  public void eventManagerGetCollisionHandlerIsNotNull() {
    EventManager em = sceneImp.getEventManager();
    assertNotNull(em.getCollisionHandler());
  }

  @Test
  public void eventManagerAddTransformationListenerDoesNotThrow() {
    EventManager em = sceneImp.getEventManager();
    PointOfViewChangeListener listener = e -> {};
    em.addTransformationListener(listener, new org.lgna.story.SThing[0]);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  AbstractEventHandler fireEvent path via SceneActivationHandler
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void sceneActivationHandlerFiresRegisteredListener() throws Exception {
    java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
    activationHandler.addListener(e -> latch.countDown());
    activationHandler.handleEventFire(new SceneActivationEvent());
    assertTrue("Listener should be called when handler fires",
        latch.await(5, java.util.concurrent.TimeUnit.SECONDS));
  }

  @Test
  public void sceneActivationHandlerFiresMultipleListeners() throws Exception {
    java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(3);
    activationHandler.addListener(e -> latch.countDown());
    activationHandler.addListener(e -> latch.countDown());
    activationHandler.addListener(e -> latch.countDown());
    activationHandler.handleEventFire(new SceneActivationEvent());
    assertTrue("All 3 listeners should fire",
        latch.await(5, java.util.concurrent.TimeUnit.SECONDS));
  }

  @Test
  public void removedListenerDoesNotFire() throws Exception {
    AtomicBoolean called = new AtomicBoolean(false);
    SceneActivationListener listener = e -> called.set(true);
    activationHandler.addListener(listener);
    activationHandler.removeListener(listener);
    activationHandler.handleEventFire(new SceneActivationEvent());
    EventTestSupport.waitForEventDispatchIdle(activationHandler);
    assertFalse("Removed listener should not fire", called.get());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  AbstractEventHandler enqueue / fireDequeue
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void enqueueDoesNotThrow() {
    activationHandler.enqueue(new SceneActivationEvent());
  }

  @Test
  public void fireDequeueWhenSilencedDoesNotThrow() {
    SceneActivationListener listener = e -> {};
    activationHandler.addListener(listener);
    activationHandler.enqueue(new SceneActivationEvent());
    activationHandler.silenceListeners();
    activationHandler.fireDequeue(listener);
  }

  private static class TestScene extends SScene {
    @Override
    public void handleActiveChanged(Boolean isActive, Integer activationCount) {
    }
  }
}
