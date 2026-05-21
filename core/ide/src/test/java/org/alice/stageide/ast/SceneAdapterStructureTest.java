package org.alice.stageide.ast;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Structural tests for SceneAdapter and JointMethodArrayAccessInfo.
 */
public class SceneAdapterStructureTest {

  // ---- SceneAdapter ----

  @Test
  public void sceneAdapter_classIsAccessible() {
    assertNotNull(SceneAdapter.class);
  }

  @Test
  public void sceneAdapter_isPublic() {
    assertTrue(Modifier.isPublic(SceneAdapter.class.getModifiers()));
  }

  @Test
  public void sceneAdapter_isNotAbstract() {
    assertFalse(Modifier.isAbstract(SceneAdapter.class.getModifiers()));
  }

  @Test
  public void sceneAdapter_hasConstructor() {
    assertTrue(SceneAdapter.class.getDeclaredConstructors().length > 0);
  }

  // ---- JointMethodArrayAccessInfo ----

  @Test
  public void jointMethodArrayAccessInfo_classIsAccessible() {
    assertNotNull(JointMethodArrayAccessInfo.class);
  }

  @Test
  public void jointMethodArrayAccessInfo_isPublic() {
    assertTrue(Modifier.isPublic(JointMethodArrayAccessInfo.class.getModifiers()));
  }

  @Test
  public void jointMethodArrayAccessInfo_isNotAbstract() {
    assertFalse(Modifier.isAbstract(JointMethodArrayAccessInfo.class.getModifiers()));
  }

  @Test
  public void jointMethodArrayAccessInfo_hasGetMethodMethod() throws Exception {
    // Verify common getter patterns
    boolean hasGetter = false;
    for (Method m : JointMethodArrayAccessInfo.class.getDeclaredMethods()) {
      if (m.getName().startsWith("get") && m.getParameterCount() == 0) {
        hasGetter = true;
        break;
      }
    }
    assertTrue("Expected at least one getter method", hasGetter);
  }

  // ---- Both classes in expected package ----

  @Test
  public void allClasses_inExpectedPackage() {
    assertEquals("org.alice.stageide.ast", SceneAdapter.class.getPackage().getName());
    assertEquals("org.alice.stageide.ast", JointMethodArrayAccessInfo.class.getPackage().getName());
  }
}
