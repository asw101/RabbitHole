package org.alice.stageide.custom;

import org.alice.ide.croquet.models.StandardExpressionState;
import org.junit.Test;
import org.lgna.project.ast.JavaType;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Tests for {@link AudioResourceExpressionState} —
 * singleton, hierarchy, type binding, and AudioResource accessor.
 */
public class AudioResourceExpressionStateTest {

  // ---- class structure --------------------------------------------------

  @Test
  public void classIsLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.custom.AudioResourceExpressionState");
  }

  @Test
  public void extendsStandardExpressionState() {
    assertTrue(StandardExpressionState.class
        .isAssignableFrom(AudioResourceExpressionState.class));
  }

  @Test
  public void classIsNotAbstract() {
    assertFalse(Modifier.isAbstract(
        AudioResourceExpressionState.class.getModifiers()));
  }

  // ---- singleton accessor -----------------------------------------------

  @Test
  public void getInstanceMethod_exists() throws NoSuchMethodException {
    Method m = AudioResourceExpressionState.class.getMethod("getInstance");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertEquals(AudioResourceExpressionState.class, m.getReturnType());
  }

  // ---- constructor is private -------------------------------------------

  @Test
  public void constructor_isPrivate() throws Exception {
    for (var ctor : AudioResourceExpressionState.class.getDeclaredConstructors()) {
      if (ctor.getParameterCount() == 0) {
        assertTrue("no-arg ctor must be private", Modifier.isPrivate(ctor.getModifiers()));
      }
    }
  }

  // ---- public API -------------------------------------------------------

  @Test
  public void getAudioResourceMethod_exists() throws NoSuchMethodException {
    Method m = AudioResourceExpressionState.class.getMethod("getAudioResource");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(org.lgna.common.resources.AudioResource.class, m.getReturnType());
  }

  // ---- AudioResource type resolution ------------------------------------

  @Test
  public void audioResourceType_resolves() {
    JavaType type = JavaType.getInstance(org.lgna.common.resources.AudioResource.class);
    assertNotNull(type);
  }
}
