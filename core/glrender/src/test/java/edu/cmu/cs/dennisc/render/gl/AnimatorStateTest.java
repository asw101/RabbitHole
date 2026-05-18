package edu.cmu.cs.dennisc.render.gl;

import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Tests for the {@code Animator} abstract class — state machine for
 * frame timing, sleep millis, and frame counting. All tested via
 * reflection since Animator is package-private.
 */
public class AnimatorStateTest {

  private static final Class<?> ANIMATOR_CLASS;
  private static final Class<?> THREAD_DEFERENCE_ACTION_CLASS;

  static {
    try {
      ANIMATOR_CLASS = Class.forName("edu.cmu.cs.dennisc.render.gl.Animator");
      THREAD_DEFERENCE_ACTION_CLASS = Class.forName(
          "edu.cmu.cs.dennisc.render.gl.Animator$ThreadDeferenceAction");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  // ── Class structure ───────────────────────────────────────────────

  @Test
  public void animator_isAbstract() {
    assertTrue(Modifier.isAbstract(ANIMATOR_CLASS.getModifiers()));
  }

  @Test
  public void animator_implementsRunnable() {
    assertTrue(Runnable.class.isAssignableFrom(ANIMATOR_CLASS));
  }

  @Test
  public void animator_isPackagePrivate() {
    assertFalse(Modifier.isPublic(ANIMATOR_CLASS.getModifiers()));
    assertFalse(Modifier.isPrivate(ANIMATOR_CLASS.getModifiers()));
    assertFalse(Modifier.isProtected(ANIMATOR_CLASS.getModifiers()));
  }

  // ── ThreadDeferenceAction enum ────────────────────────────────────

  @Test
  public void threadDeferenceAction_isEnum() {
    assertTrue(THREAD_DEFERENCE_ACTION_CLASS.isEnum());
  }

  @Test
  public void threadDeferenceAction_hasSleepConstant() {
    boolean found = false;
    for (Object c : THREAD_DEFERENCE_ACTION_CLASS.getEnumConstants()) {
      if (c.toString().equals("SLEEP")) {
        found = true;
        break;
      }
    }
    assertTrue("Should have SLEEP constant", found);
  }

  @Test
  public void threadDeferenceAction_hasYieldConstant() {
    boolean found = false;
    for (Object c : THREAD_DEFERENCE_ACTION_CLASS.getEnumConstants()) {
      if (c.toString().equals("YIELD")) {
        found = true;
        break;
      }
    }
    assertTrue("Should have YIELD constant", found);
  }

  // ── DEFAULT_SLEEP_MILLIS constant ─────────────────────────────────

  @Test
  public void defaultSleepMillis_is16() throws Exception {
    Field f = ANIMATOR_CLASS.getDeclaredField("DEFAULT_SLEEP_MILLIS");
    f.setAccessible(true);
    assertEquals(16L, f.getLong(null));
  }

  // ── Fields exist ──────────────────────────────────────────────────

  @Test
  public void isActive_fieldExists() throws Exception {
    Field f = ANIMATOR_CLASS.getDeclaredField("isActive");
    f.setAccessible(true);
    assertEquals(boolean.class, f.getType());
  }

  @Test
  public void frameCount_fieldExists() throws Exception {
    Field f = ANIMATOR_CLASS.getDeclaredField("frameCount");
    f.setAccessible(true);
    assertEquals(int.class, f.getType());
  }

  @Test
  public void frameRate_fieldExists() throws Exception {
    Field f = ANIMATOR_CLASS.getDeclaredField("frameRate");
    f.setAccessible(true);
    assertEquals(int.class, f.getType());
  }

  @Test
  public void sleepMillis_fieldExists() throws Exception {
    Field f = ANIMATOR_CLASS.getDeclaredField("sleepMillis");
    f.setAccessible(true);
    assertEquals(long.class, f.getType());
  }

  // ── Method signatures ─────────────────────────────────────────────

  @Test
  public void getFrameRate_isPublic() throws Exception {
    Method m = ANIMATOR_CLASS.getDeclaredMethod("getFrameRate");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(int.class, m.getReturnType());
  }

  @Test
  public void getFrameCount_isPublic() throws Exception {
    Method m = ANIMATOR_CLASS.getDeclaredMethod("getFrameCount");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(int.class, m.getReturnType());
  }

  @Test
  public void getStartTimeMillis_isPublic() throws Exception {
    Method m = ANIMATOR_CLASS.getDeclaredMethod("getStartTimeMillis");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(long.class, m.getReturnType());
  }

  @Test
  public void getSleepMillis_isPublic() throws Exception {
    Method m = ANIMATOR_CLASS.getDeclaredMethod("getSleepMillis");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(long.class, m.getReturnType());
  }

  @Test
  public void setSleepMillis_isPublic() throws Exception {
    Method m = ANIMATOR_CLASS.getDeclaredMethod("setSleepMillis", long.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void step_isAbstract() throws Exception {
    Method m = ANIMATOR_CLASS.getDeclaredMethod("step");
    assertTrue(Modifier.isAbstract(m.getModifiers()));
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void start_isPublic() throws Exception {
    Method m = ANIMATOR_CLASS.getDeclaredMethod("start");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void stop_isPublic() throws Exception {
    Method m = ANIMATOR_CLASS.getDeclaredMethod("stop");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void run_isPublic() throws Exception {
    Method m = ANIMATOR_CLASS.getDeclaredMethod("run");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  // ── Concrete subclass instantiation ───────────────────────────────

  @Test
  public void concreteSubclass_initialSleepMillis_isDefault() throws Exception {
    Object animator = createTestAnimator();
    Method m = ANIMATOR_CLASS.getDeclaredMethod("getSleepMillis");
    m.setAccessible(true);
    assertEquals(16L, m.invoke(animator));
  }

  @Test
  public void concreteSubclass_setSleepMillis_changes() throws Exception {
    Object animator = createTestAnimator();
    Method setter = ANIMATOR_CLASS.getDeclaredMethod("setSleepMillis", long.class);
    setter.setAccessible(true);
    setter.invoke(animator, 32L);
    Method getter = ANIMATOR_CLASS.getDeclaredMethod("getSleepMillis");
    getter.setAccessible(true);
    assertEquals(32L, getter.invoke(animator));
  }

  @Test
  public void concreteSubclass_initialFrameCount_isZero() throws Exception {
    Object animator = createTestAnimator();
    Method m = ANIMATOR_CLASS.getDeclaredMethod("getFrameCount");
    m.setAccessible(true);
    assertEquals(0, m.invoke(animator));
  }

  @Test
  public void concreteSubclass_initialFrameRate_isZero() throws Exception {
    Object animator = createTestAnimator();
    Method m = ANIMATOR_CLASS.getDeclaredMethod("getFrameRate");
    m.setAccessible(true);
    assertEquals(0, m.invoke(animator));
  }

  // ── Helper ────────────────────────────────────────────────────────

  private static Object createTestAnimator() {
    return new AnimatorTestSubclass();
  }

  /**
   * Concrete package-private subclass of Animator for testing.
   */
  static class AnimatorTestSubclass extends Animator {
    @Override
    protected ThreadDeferenceAction step() {
      return ThreadDeferenceAction.SLEEP;
    }
  }
}
