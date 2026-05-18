package org.alice.stageide.sceneeditor;

import org.junit.Test;
import org.lgna.croquet.BooleanState;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Tests for {@link ShowJointedModelJointAxesState} — hierarchy,
 * factory method, and field accessor.
 */
public class ShowJointedModelJointAxesStateTest {

  // ---- class structure --------------------------------------------------

  @Test
  public void classIsLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.sceneeditor.ShowJointedModelJointAxesState");
  }

  @Test
  public void extendsBooleanState() {
    assertTrue(BooleanState.class.isAssignableFrom(ShowJointedModelJointAxesState.class));
  }

  @Test
  public void classIsNotAbstract() {
    assertFalse(Modifier.isAbstract(
        ShowJointedModelJointAxesState.class.getModifiers()));
  }

  // ---- factory method ---------------------------------------------------

  @Test
  public void getInstanceMethod_exists() throws NoSuchMethodException {
    Method m = ShowJointedModelJointAxesState.class.getMethod("getInstance",
        org.lgna.project.ast.AbstractField.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertEquals(ShowJointedModelJointAxesState.class, m.getReturnType());
  }

  // ---- getField accessor ------------------------------------------------

  @Test
  public void getFieldMethod_exists() throws NoSuchMethodException {
    Method m = ShowJointedModelJointAxesState.class.getMethod("getField");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(org.lgna.project.ast.AbstractField.class, m.getReturnType());
  }

  // ---- constructor is private -------------------------------------------

  @Test
  public void constructor_isPrivate() {
    for (var ctor : ShowJointedModelJointAxesState.class.getDeclaredConstructors()) {
      assertTrue("ctor must be private",
          Modifier.isPrivate(ctor.getModifiers()));
    }
  }

  // ---- BooleanState has inherited toggles -------------------------------

  @Test
  public void getValueMethod_inherited() throws NoSuchMethodException {
    // BooleanState should provide getValue() returning Boolean
    Method m = ShowJointedModelJointAxesState.class.getMethod("getValue");
    assertNotNull(m);
  }
}
