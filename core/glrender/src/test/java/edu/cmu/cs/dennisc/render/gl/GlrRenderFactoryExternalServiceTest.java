package edu.cmu.cs.dennisc.render.gl;

import edu.cmu.cs.dennisc.render.RenderFactory;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

/**
 * External service integration tests for {@link GlrRenderFactory}.
 * Tests the rendering lock, automatic display count, invokeLater queue,
 * and structural contracts of the factory singleton — all without
 * requiring a live GL context.
 */
public class GlrRenderFactoryExternalServiceTest {


  // ── Singleton pattern ─────────────────────────────────────────────

  @Test
  public void getInstance_returnsNonNull() {
    assertNotNull(GlrRenderFactory.getInstance());
  }

  @Test
  public void getInstance_returnsSameInstance() {
    assertSame(GlrRenderFactory.getInstance(), GlrRenderFactory.getInstance());
  }

  @Test
  public void implementsRenderFactory() {
    assertTrue(GlrRenderFactory.getInstance() instanceof RenderFactory);
  }

  // ── Rendering lock ────────────────────────────────────────────────

  @Test
  public void renderingLock_fieldExists() throws Exception {
    Field f = GlrRenderFactory.class.getDeclaredField("renderingLock");
    f.setAccessible(true);
    Object lock = f.get(GlrRenderFactory.getInstance());
    assertNotNull(lock);
    assertTrue(lock instanceof Semaphore);
  }

  @Test
  public void acquireRelease_cycle_noDeadlock() {
    GlrRenderFactory factory = GlrRenderFactory.getInstance();
    factory.acquireRenderingLock();
    factory.releaseRenderingLock();
  }

  @Test
  public void acquireRelease_inSeparateThread() throws Exception {
    GlrRenderFactory factory = GlrRenderFactory.getInstance();
    AtomicBoolean acquired = new AtomicBoolean(false);
    Thread t = new Thread(() -> {
      factory.acquireRenderingLock();
      acquired.set(true);
      factory.releaseRenderingLock();
    });
    t.start();
    t.join(5000);
    assertFalse("Thread should have finished", t.isAlive());
    assertTrue("Lock should have been acquired", acquired.get());
  }

  // ── automaticDisplayCount ─────────────────────────────────────────

  @Test
  public void automaticDisplayCount_fieldExists() throws Exception {
    Field f = GlrRenderFactory.class.getDeclaredField("automaticDisplayCount");
    f.setAccessible(true);
    assertNotNull(f);
    assertEquals(int.class, f.getType());
  }

  // ── invokeLater queue ─────────────────────────────────────────────

  @Test
  public void runnables_fieldExists() throws Exception {
    Field f = GlrRenderFactory.class.getDeclaredField("runnables");
    f.setAccessible(true);
    Object q = f.get(GlrRenderFactory.getInstance());
    assertNotNull(q);
    assertTrue(q instanceof Queue);
  }

  @Test
  public void invokeLater_addsToQueue() throws Exception {
    GlrRenderFactory factory = GlrRenderFactory.getInstance();
    Field f = GlrRenderFactory.class.getDeclaredField("runnables");
    f.setAccessible(true);
    Queue<?> queue = (Queue<?>) f.get(factory);
    int sizeBefore = queue.size();
    factory.invokeLater(() -> {});
    assertEquals(sizeBefore + 1, queue.size());
  }

  // ── onscreenLookingGlasses / offscreenLookingGlasses ──────────────

  @Test
  public void onscreenLookingGlasses_fieldExists() throws Exception {
    Field f = GlrRenderFactory.class.getDeclaredField("onscreenLookingGlasses");
    f.setAccessible(true);
    Object list = f.get(GlrRenderFactory.getInstance());
    assertNotNull(list);
    assertTrue(list instanceof List);
  }

  @Test
  public void offscreenLookingGlasses_fieldExists() throws Exception {
    Field f = GlrRenderFactory.class.getDeclaredField("offscreenLookingGlasses");
    f.setAccessible(true);
    Object list = f.get(GlrRenderFactory.getInstance());
    assertNotNull(list);
    assertTrue(list instanceof List);
  }

  // ── toBeReleased list ─────────────────────────────────────────────

  @Test
  public void toBeReleased_fieldExists() throws Exception {
    Field f = GlrRenderFactory.class.getDeclaredField("toBeReleased");
    f.setAccessible(true);
    Object list = f.get(GlrRenderFactory.getInstance());
    assertNotNull(list);
    assertTrue(list instanceof List);
  }

  // ── releaseListener ───────────────────────────────────────────────

  @Test
  public void releaseListener_fieldExists() throws Exception {
    Field f = GlrRenderFactory.class.getDeclaredField("releaseListener");
    f.setAccessible(true);
    assertNotNull(f.get(GlrRenderFactory.getInstance()));
  }

  // ── automaticDisplayListeners ─────────────────────────────────────

  @Test
  public void automaticDisplayListeners_fieldExists() throws Exception {
    Field f = GlrRenderFactory.class.getDeclaredField("automaticDisplayListeners");
    f.setAccessible(true);
    Object list = f.get(GlrRenderFactory.getInstance());
    assertNotNull(list);
    assertTrue(list instanceof List);
  }

  // ── ReusableAutomaticDisplayEvent inner class ─────────────────────

  @Test
  public void reusableAutomaticDisplayEvent_innerClassExists() {
    boolean found = false;
    for (Class<?> inner : GlrRenderFactory.class.getDeclaredClasses()) {
      if (inner.getSimpleName().equals("ReusableAutomaticDisplayEvent")) {
        found = true;
        break;
      }
    }
    assertTrue("ReusableAutomaticDisplayEvent inner class should exist", found);
  }

  // ── SingletonHolder inner class ───────────────────────────────────

  @Test
  public void singletonHolder_innerClassExists() {
    boolean found = false;
    for (Class<?> inner : GlrRenderFactory.class.getDeclaredClasses()) {
      if (inner.getSimpleName().equals("SingletonHolder")) {
        found = true;
        break;
      }
    }
    assertTrue("SingletonHolder inner class should exist", found);
  }

  // ── createImageBuffer method ──────────────────────────────────────

  @Test
  public void createImageBuffer_methodSignature() throws Exception {
    Method m = GlrRenderFactory.class.getDeclaredMethod(
        "createImageBuffer", edu.cmu.cs.dennisc.color.Color4f.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  // ── step method ───────────────────────────────────────────────────

  @Test
  public void step_methodExists() throws Exception {
    Method m = GlrRenderFactory.class.getDeclaredMethod("step");
    assertNotNull(m);
  }
}
