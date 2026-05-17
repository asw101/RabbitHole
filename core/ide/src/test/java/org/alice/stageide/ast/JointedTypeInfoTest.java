package org.alice.stageide.ast;

import org.junit.Test;
import org.lgna.project.ast.JavaType;

import static org.junit.Assert.*;

/**
 * Tests for {@link JointedTypeInfo} — jointed model type detection.
 */
public class JointedTypeInfoTest {

  @Test
  public void getDeclarationInstance_nonJointedType_returnsNull() {
    // String is not a jointed model type
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
  public void isDeclarationJointed_nullType_returnsFalse() {
    assertFalse(JointedTypeInfo.isDeclarationJointed(null));
  }

  @Test
  public void getInstances_nonJointedType_returnsEmptyList() {
    assertTrue(JointedTypeInfo.getInstances(JavaType.getInstance(Integer.class)).isEmpty());
  }
}
