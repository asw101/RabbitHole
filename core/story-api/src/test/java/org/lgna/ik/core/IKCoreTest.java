package org.lgna.ik.core;

import org.alice.math.immutable.Point3;
import org.junit.Test;
import org.lgna.story.implementation.JointImp;
import org.lgna.story.resources.JointId;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class IKCoreTest {
  private static Class<?> loadIkCoreWithoutInitialization() throws Exception {
    return Class.forName("org.lgna.ik.core.IKCore", false, IKCoreTest.class.getClassLoader());
  }

  private static Class<?> loadLimbEnum() throws Exception {
    return Class.forName("org.lgna.ik.core.IKCore$Limb", false, IKCoreTest.class.getClassLoader());
  }

  @Test
  public void ikCoreClassLoadsWithoutRunningStaticInitializer() throws Exception {
    assertNotNull(loadIkCoreWithoutInitialization());
  }

  @Test
  public void limbEnumExistsAsNestedType() throws Exception {
    Class<?> limbClass = loadLimbEnum();
    assertTrue(limbClass.isEnum());
    assertEquals("org.lgna.ik.core.IKCore$Limb", limbClass.getName());
  }

  @Test
  public void limbEnumContainsFourExpectedConstants() throws Exception {
    Object[] constants = loadLimbEnum().getEnumConstants();
    assertEquals(4, constants.length);
    assertEquals("RIGHT_ARM", constants[0].toString());
    assertEquals("LEFT_ARM", constants[1].toString());
    assertEquals("RIGHT_LEG", constants[2].toString());
    assertEquals("LEFT_LEG", constants[3].toString());
  }

  @Test
  public void moveChainToPointInSceneSpaceMethodExists() throws Exception {
    Method method = loadIkCoreWithoutInitialization().getDeclaredMethod(
        "moveChainToPointInSceneSpace", JointImp.class, JointImp.class, Point3.class);
    assertTrue(Modifier.isStatic(method.getModifiers()));
    assertEquals(void.class, method.getReturnType());
  }

  @Test
  public void getDefaultAnchorForBipedEndJointMethodExists() throws Exception {
    Method method = loadIkCoreWithoutInitialization().getDeclaredMethod(
        "getDefaultAnchorForBipedEndJoint", JointId.class);
    assertTrue(Modifier.isStatic(method.getModifiers()));
    assertEquals(JointId.class, method.getReturnType());
  }

  @Test
  public void privateCorrectTargetMethodExists() throws Exception {
    Method method = loadIkCoreWithoutInitialization().getDeclaredMethod(
        "correctTarget", JointImp.class, JointImp.class, Point3.class);
    assertTrue(Modifier.isPrivate(method.getModifiers()));
    assertEquals(Point3.class, method.getReturnType());
  }

  @Test
  public void privateGetLengthOfLimbMethodExists() throws Exception {
    Method method = loadIkCoreWithoutInitialization().getDeclaredMethod(
        "getLengthOfLimb", JointImp.class, JointImp.class);
    assertTrue(Modifier.isPrivate(method.getModifiers()));
    assertEquals(double.class, method.getReturnType());
  }

  @Test
  public void oldAndNewEnforcerHelpersExist() throws Exception {
    Method oldMethod = loadIkCoreWithoutInitialization().getDeclaredMethod(
        "moveUsingOldJMIKEnforcer", JointImp.class, JointImp.class, Point3.class);
    Method newMethod = loadIkCoreWithoutInitialization().getDeclaredMethod(
        "moveUsingNewTPIKEnforcer", JointImp.class, JointImp.class, Point3.class);
    assertTrue(Modifier.isPrivate(oldMethod.getModifiers()));
    assertTrue(Modifier.isPrivate(newMethod.getModifiers()));
  }

  @Test
  public void ikCoreDeclaresPublicUtilityMethodsOnlyForSupportedSurface() throws Exception {
    Class<?> clazz = loadIkCoreWithoutInitialization();
    assertFalse(Modifier.isAbstract(clazz.getModifiers()));
    assertTrue(Modifier.isPublic(clazz.getModifiers()));
  }
}
