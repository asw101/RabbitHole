package org.alice.stageide.custom;

import org.lgna.croquet.SimpleItemState;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Tests for {@link KeyState} — singleton, hierarchy, and API surface.
 */
public class KeyStateTest {

  // ---- class structure --------------------------------------------------

  @Test
  public void classIsLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.custom.KeyState");
  }

  @Test
  public void extendsSimpleItemState() {
    assertTrue(SimpleItemState.class.isAssignableFrom(KeyState.class));
  }

  @Test
  public void classIsFinal() {
    assertTrue(Modifier.isFinal(KeyState.class.getModifiers()));
  }

  // ---- singleton accessor -----------------------------------------------

  @Test
  public void getInstanceMethod_exists() throws NoSuchMethodException {
    Method m = KeyState.class.getMethod("getInstance");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertEquals(KeyState.class, m.getReturnType());
  }

  // ---- constructor is private -------------------------------------------

  @Test
  public void constructor_isPrivate() throws Exception {
    for (var ctor : KeyState.class.getDeclaredConstructors()) {
      if (ctor.getParameterCount() == 0) {
        assertTrue("no-arg ctor must be private", Modifier.isPrivate(ctor.getModifiers()));
      }
    }
  }

  // ---- public API methods -----------------------------------------------

  @Test
  public void handleKeyPressedMethod_exists() throws NoSuchMethodException {
    Method m = KeyState.class.getMethod("handleKeyPressed",
        org.alice.stageide.custom.components.KeyViewController.class,
        java.awt.event.KeyEvent.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void createViewControllerMethod_exists() throws NoSuchMethodException {
    Method m = KeyState.class.getMethod("createViewController");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(org.alice.stageide.custom.components.KeyViewController.class,
        m.getReturnType());
  }

  // ---- Key enum sanity --------------------------------------------------

  @Test
  public void keyEnum_hasValues() {
    org.lgna.story.Key[] keys = org.lgna.story.Key.values();
    assertTrue("Key enum should have entries", keys.length > 0);
  }
}
