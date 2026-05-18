package org.alice.stageide.ast;

import org.junit.Test;
import org.lgna.project.ast.JavaType;

import static org.junit.Assert.*;

public class JointedTypeInfoTest {
  @Test
  public void getDeclarationInstance_nonJointedType_returnsNull() {
    JointedTypeInfo info = JointedTypeInfo.getDeclarationInstance(JavaType.getInstance(String.class));
    assertNull(info);
  }

  @Test
  public void isDeclarationJointed_nonJointedType_returnsFalse() {
    assertFalse(JointedTypeInfo.isDeclarationJointed(JavaType.getInstance(Object.class)));
  }

  @Test
  public void isJointed_nonJointedType_returnsFalse() {
    assertFalse(JointedTypeInfo.isJointed(JavaType.getInstance(String.class)));
  }

  @Test
  public void getInstances_nonJointedType_returnsEmptyList() {
    assertTrue(JointedTypeInfo.getInstances(JavaType.getInstance(String.class)).isEmpty());
  }

  @Test
  public void getDeclarationInstance_objectType_returnsNull() {
    assertNull(JointedTypeInfo.getDeclarationInstance(JavaType.getInstance(Integer.class)));
  }
}
