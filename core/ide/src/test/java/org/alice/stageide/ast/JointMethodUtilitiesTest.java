package org.alice.stageide.ast;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

/**
 * Tests for {@link JointMethodUtilities} — joint getter detection logic.
 */
public class JointMethodUtilitiesTest {

  // ---- isJointGetter ----

  @Test
  public void isJointGetter_userMethodWithVoidReturn_returnsFalse() {
    UserMethod method = new UserMethod();
    method.name.setValue("getLeftArm");
    method.returnType.setValue(JavaType.VOID_TYPE);
    method.managementLevel.setValue(ManagementLevel.NONE);
    assertFalse(JointMethodUtilities.isJointGetter(method));
  }

  // ---- isJointArrayGetter ----

  @Test
  public void isJointArrayGetter_userMethodWithVoidReturn_returnsFalse() {
    UserMethod method = new UserMethod();
    method.name.setValue("getFingers");
    method.returnType.setValue(JavaType.VOID_TYPE);
    method.managementLevel.setValue(ManagementLevel.NONE);
    assertFalse(JointMethodUtilities.isJointArrayGetter(method));
  }

  // ---- getJointArrayLength ----

  @Test
  public void getJointArrayLength_userMethod_returnsNegativeOne() {
    UserMethod method = new UserMethod();
    method.name.setValue("getToes");
    method.returnType.setValue(JavaType.VOID_TYPE);
    method.managementLevel.setValue(ManagementLevel.NONE);
    assertEquals(-1, JointMethodUtilities.getJointArrayLength(method));
  }
}
