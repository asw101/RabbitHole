package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for {@link ChangeHandler} — rendering mode state machine,
 * event counting, and buffered event logic. All tested via public API
 * plus reflection for internal state verification.
 */
public class ChangeHandlerStateTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }

  @Before
  public void resetState() throws Exception {
    // Reset renderingModeCount to 0 and eventCount to 0
    setStaticField("renderingModeCount", 0);
    ChangeHandler.resetEventCount();
    clearBufferedEvents();
  }

  @After
  public void cleanup() throws Exception {
    setStaticField("renderingModeCount", 0);
    ChangeHandler.resetEventCount();
    clearBufferedEvents();
  }

  // ── getEventCountSinceLastReset / resetEventCount ──────────────────

  @Test
  public void eventCount_initiallyZero() {
    assertEquals(0, ChangeHandler.getEventCountSinceLastReset());
  }

  @Test
  public void resetEventCount_resetsToZero() {
    ChangeHandler.resetEventCount();
    assertEquals(0, ChangeHandler.getEventCountSinceLastReset());
  }

  // ── pushRenderingMode / popRenderingMode ──────────────────────────

  @Test
  public void pushRenderingMode_decrementsCount() throws Exception {
    int before = getRenderingModeCount();
    ChangeHandler.pushRenderingMode();
    assertEquals(before - 1, getRenderingModeCount());
    // restore
    ChangeHandler.popRenderingMode();
  }

  @Test
  public void popRenderingMode_incrementsCount() throws Exception {
    ChangeHandler.pushRenderingMode();
    int afterPush = getRenderingModeCount();
    ChangeHandler.popRenderingMode();
    assertEquals(afterPush + 1, getRenderingModeCount());
  }

  @Test
  public void pushPop_cycle_restoresOriginalCount() throws Exception {
    int original = getRenderingModeCount();
    ChangeHandler.pushRenderingMode();
    ChangeHandler.popRenderingMode();
    assertEquals(original, getRenderingModeCount());
  }

  @Test
  public void nestedPushPop_restoresCount() throws Exception {
    int original = getRenderingModeCount();
    ChangeHandler.pushRenderingMode();
    ChangeHandler.pushRenderingMode();
    ChangeHandler.popRenderingMode();
    ChangeHandler.popRenderingMode();
    assertEquals(original, getRenderingModeCount());
  }

  @Test
  public void isInRenderingMode_afterPush_true() throws Exception {
    ChangeHandler.pushRenderingMode();
    assertTrue(isInRenderingMode());
    ChangeHandler.popRenderingMode();
  }

  @Test
  public void isInRenderingMode_initial_false() throws Exception {
    assertFalse(isInRenderingMode());
  }

  @Test
  public void isInRenderingMode_afterPushAndPop_false() throws Exception {
    ChangeHandler.pushRenderingMode();
    ChangeHandler.popRenderingMode();
    assertFalse(isInRenderingMode());
  }

  @Test
  public void doublePush_isInRenderingMode() throws Exception {
    ChangeHandler.pushRenderingMode();
    ChangeHandler.pushRenderingMode();
    assertTrue(isInRenderingMode());
    ChangeHandler.popRenderingMode();
    assertTrue(isInRenderingMode());
    ChangeHandler.popRenderingMode();
  }

  // ── handleBufferedChanges ─────────────────────────────────────────

  @Test
  public void handleBufferedChanges_emptyBuffer_noException() {
    ChangeHandler.handleBufferedChanges();
  }

  @Test
  public void handleBufferedChanges_clearsBuffer() throws Exception {
    clearBufferedEvents();
    ChangeHandler.handleBufferedChanges();
    assertEquals(0, getBufferedEventCount());
  }

  // ── renderingModeLock synchronization ──────────────────────────────

  @Test
  public void renderingModeLock_isNotNull() throws Exception {
    Field lockField = ChangeHandler.class.getDeclaredField("renderingModeLock");
    lockField.setAccessible(true);
    assertNotNull(lockField.get(null));
  }

  // ── bufferedEvents list ───────────────────────────────────────────

  @Test
  public void bufferedEvents_isNotNull() throws Exception {
    Field f = ChangeHandler.class.getDeclaredField("bufferedEvents");
    f.setAccessible(true);
    assertNotNull(f.get(null));
  }

  @Test
  public void bufferedEvents_initiallyEmpty() throws Exception {
    assertEquals(0, getBufferedEventCount());
  }

  // ── Listener fields exist and are non-null ────────────────────────

  @Test
  public void releaseListener_isNotNull() throws Exception {
    assertNotNull(getStaticField("releaseListener"));
  }

  @Test
  public void propertyListener_isNotNull() throws Exception {
    assertNotNull(getStaticField("propertyListener"));
  }

  @Test
  public void componentsListener_isNotNull() throws Exception {
    assertNotNull(getStaticField("componentsListener"));
  }

  @Test
  public void graphicsListener_isNotNull() throws Exception {
    assertNotNull(getStaticField("graphicsListener"));
  }

  @Test
  public void absoluteTransformationListener_isNotNull() throws Exception {
    assertNotNull(getStaticField("absoluteTransformationListener"));
  }

  @Test
  public void hierarchyListener_isNotNull() throws Exception {
    assertNotNull(getStaticField("hierarchyListener"));
  }

  @Test
  public void textureListener_isNotNull() throws Exception {
    assertNotNull(getStaticField("textureListener"));
  }

  // ── Concurrent push/pop safety ────────────────────────────────────

  @Test
  public void concurrentPushPop_noException() throws Exception {
    Thread t1 = new Thread(() -> {
      for (int i = 0; i < 100; i++) {
        ChangeHandler.pushRenderingMode();
        ChangeHandler.popRenderingMode();
      }
    });
    Thread t2 = new Thread(() -> {
      for (int i = 0; i < 100; i++) {
        ChangeHandler.pushRenderingMode();
        ChangeHandler.popRenderingMode();
      }
    });
    t1.start();
    t2.start();
    t1.join(5000);
    t2.join(5000);
    assertFalse(t1.isAlive());
    assertFalse(t2.isAlive());
  }

  // ── Helpers ───────────────────────────────────────────────────────

  private static int getRenderingModeCount() throws Exception {
    Field f = ChangeHandler.class.getDeclaredField("renderingModeCount");
    f.setAccessible(true);
    return f.getInt(null);
  }

  private static boolean isInRenderingMode() throws Exception {
    java.lang.reflect.Method m = ChangeHandler.class.getDeclaredMethod("isInRenderingMode");
    m.setAccessible(true);
    return (boolean) m.invoke(null);
  }

  private static void setStaticField(String name, int value) throws Exception {
    Field f = ChangeHandler.class.getDeclaredField(name);
    f.setAccessible(true);
    f.setInt(null, value);
  }

  private static Object getStaticField(String name) throws Exception {
    Field f = ChangeHandler.class.getDeclaredField(name);
    f.setAccessible(true);
    return f.get(null);
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
