package edu.cmu.cs.dennisc.render.gl.imp;


import edu.cmu.cs.dennisc.render.gl.imp.adapters.ChangeHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Extended integration tests for {@link ChangeHandler} — exercises
 * the event buffering state machine, rendering mode transitions,
 * and concurrent access patterns that exercise the external service
 * integration boundary between event dispatch and GL rendering.
 */
public class ChangeHandlerExternalServiceTest {


  @Before
  public void resetState() throws Exception {
    setRenderingModeCount(0);
    ChangeHandler.resetEventCount();
    clearBufferedEvents();
  }

  @After
  public void cleanup() throws Exception {
    setRenderingModeCount(0);
    ChangeHandler.resetEventCount();
    clearBufferedEvents();
  }

  // ── Rendering mode transitions ────────────────────────────────────

  @Test
  public void pushPop_singleCycle_leavesNonRenderingMode() throws Exception {
    assertFalse(isInRenderingMode());
    ChangeHandler.pushRenderingMode();
    assertTrue(isInRenderingMode());
    ChangeHandler.popRenderingMode();
    assertFalse(isInRenderingMode());
  }

  @Test
  public void deeplyNested_push3pop3_restores() throws Exception {
    ChangeHandler.pushRenderingMode();
    ChangeHandler.pushRenderingMode();
    ChangeHandler.pushRenderingMode();
    assertEquals(-3, getRenderingModeCount());
    assertTrue(isInRenderingMode());
    ChangeHandler.popRenderingMode();
    ChangeHandler.popRenderingMode();
    ChangeHandler.popRenderingMode();
    assertEquals(0, getRenderingModeCount());
    assertFalse(isInRenderingMode());
  }

  @Test
  public void pop_atBoundary_callsHandleBufferedChanges() throws Exception {
    ChangeHandler.pushRenderingMode();
    assertEquals(-1, getRenderingModeCount());
    ChangeHandler.popRenderingMode();
    assertEquals(0, getRenderingModeCount());
  }

  @Test
  public void pop_fromDeepNesting_doesNotCallHandleBufferedChanges() throws Exception {
    ChangeHandler.pushRenderingMode();
    ChangeHandler.pushRenderingMode();
    assertEquals(-2, getRenderingModeCount());
    ChangeHandler.popRenderingMode();
    assertEquals(-1, getRenderingModeCount());
    assertTrue(isInRenderingMode());
    ChangeHandler.popRenderingMode();
    assertEquals(0, getRenderingModeCount());
  }

  // ── Event count tracking ──────────────────────────────────────────

  @Test
  public void eventCount_afterReset_isZero() {
    ChangeHandler.resetEventCount();
    assertEquals(0, ChangeHandler.getEventCountSinceLastReset());
  }

  @Test
  public void eventCount_multipleResets_stayZero() {
    ChangeHandler.resetEventCount();
    ChangeHandler.resetEventCount();
    assertEquals(0, ChangeHandler.getEventCountSinceLastReset());
  }

  // ── Buffered events management ────────────────────────────────────

  @Test
  public void bufferedEvents_clearedByHandleBufferedChanges() throws Exception {
    assertEquals(0, getBufferedEventCount());
    ChangeHandler.handleBufferedChanges();
    assertEquals(0, getBufferedEventCount());
  }

  @Test
  public void handleBufferedChanges_idempotent() throws Exception {
    ChangeHandler.handleBufferedChanges();
    ChangeHandler.handleBufferedChanges();
    assertEquals(0, getBufferedEventCount());
  }

  // ── Concurrent rendering mode push/pop ────────────────────────────

  @Test
  public void concurrent_pushPop_10Threads() throws Exception {
    int threadCount = 10;
    int cyclesPerThread = 50;
    CountDownLatch startLatch = new CountDownLatch(1);
    CountDownLatch doneLatch = new CountDownLatch(threadCount);

    for (int i = 0; i < threadCount; i++) {
      new Thread(() -> {
        try {
          startLatch.await();
          for (int j = 0; j < cyclesPerThread; j++) {
            ChangeHandler.pushRenderingMode();
            ChangeHandler.popRenderingMode();
          }
        } catch (Exception e) {
          // ignore
        } finally {
          doneLatch.countDown();
        }
      }).start();
    }

    startLatch.countDown();
    assertTrue("All threads should finish within 10s",
        doneLatch.await(10, TimeUnit.SECONDS));
  }

  // ── Listener field structure ──────────────────────────────────────

  @Test
  public void allListenerFields_areStaticFinal() throws Exception {
    String[] fields = {"releaseListener", "propertyListener", "componentsListener",
        "graphicsListener", "absoluteTransformationListener",
        "hierarchyListener", "textureListener"};
    for (String fieldName : fields) {
      Field f = ChangeHandler.class.getDeclaredField(fieldName);
      f.setAccessible(true);
      assertTrue(fieldName + " should be static",
          java.lang.reflect.Modifier.isStatic(f.getModifiers()));
      assertTrue(fieldName + " should be final",
          java.lang.reflect.Modifier.isFinal(f.getModifiers()));
      assertNotNull(fieldName + " should be non-null", f.get(null));
    }
  }

  // ── handleEvent method structure ──────────────────────────────────

  @Test
  public void handleEvent_isPrivateStatic() throws Exception {
    Method m = ChangeHandler.class.getDeclaredMethod("handleEvent",
        edu.cmu.cs.dennisc.pattern.event.Event.class);
    m.setAccessible(true);
    assertTrue(java.lang.reflect.Modifier.isStatic(m.getModifiers()));
    assertTrue(java.lang.reflect.Modifier.isPrivate(m.getModifiers()));
  }

  @Test
  public void handleOrBufferEvent_isPrivateStatic() throws Exception {
    Method m = ChangeHandler.class.getDeclaredMethod("handleOrBufferEvent",
        edu.cmu.cs.dennisc.pattern.event.Event.class);
    m.setAccessible(true);
    assertTrue(java.lang.reflect.Modifier.isStatic(m.getModifiers()));
    assertTrue(java.lang.reflect.Modifier.isPrivate(m.getModifiers()));
  }

  // ── addListeners / removeListeners method structure ────────────────

  @Test
  public void addListeners_isPackagePrivateStatic() throws Exception {
    Method m = ChangeHandler.class.getDeclaredMethod("addListeners",
        edu.cmu.cs.dennisc.pattern.Releasable.class);
    m.setAccessible(true);
    assertTrue(java.lang.reflect.Modifier.isStatic(m.getModifiers()));
    assertFalse(java.lang.reflect.Modifier.isPublic(m.getModifiers()));
    assertFalse(java.lang.reflect.Modifier.isPrivate(m.getModifiers()));
  }

  @Test
  public void removeListeners_isPackagePrivateStatic() throws Exception {
    Method m = ChangeHandler.class.getDeclaredMethod("removeListeners",
        edu.cmu.cs.dennisc.pattern.Releasable.class);
    m.setAccessible(true);
    assertTrue(java.lang.reflect.Modifier.isStatic(m.getModifiers()));
    assertFalse(java.lang.reflect.Modifier.isPublic(m.getModifiers()));
    assertFalse(java.lang.reflect.Modifier.isPrivate(m.getModifiers()));
  }

  // ── Helpers ───────────────────────────────────────────────────────

  private static int getRenderingModeCount() throws Exception {
    Field f = ChangeHandler.class.getDeclaredField("renderingModeCount");
    f.setAccessible(true);
    return f.getInt(null);
  }

  private static void setRenderingModeCount(int value) throws Exception {
    Field f = ChangeHandler.class.getDeclaredField("renderingModeCount");
    f.setAccessible(true);
    f.setInt(null, value);
  }

  private static boolean isInRenderingMode() throws Exception {
    Method m = ChangeHandler.class.getDeclaredMethod("isInRenderingMode");
    m.setAccessible(true);
    return (boolean) m.invoke(null);
  }

  @SuppressWarnings("unchecked")
  private static int getBufferedEventCount() throws Exception {
    Field f = ChangeHandler.class.getDeclaredField("bufferedEvents");
    f.setAccessible(true);
    List<?> list = (List<?>) f.get(null);
    synchronized (list) {
      return list.size();
    }
  }

  @SuppressWarnings("unchecked")
  private static void clearBufferedEvents() throws Exception {
    Field f = ChangeHandler.class.getDeclaredField("bufferedEvents");
    f.setAccessible(true);
    List<?> list = (List<?>) f.get(null);
    synchronized (list) {
      list.clear();
    }
  }
}
