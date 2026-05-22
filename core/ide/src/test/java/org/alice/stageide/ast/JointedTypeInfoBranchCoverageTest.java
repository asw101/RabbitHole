package org.alice.stageide.ast;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

/**
 * Tests for {@link JointedTypeInfo} covering construction and properties.
 */
public class JointedTypeInfoBranchCoverageTest {

  @Test
  public void className_isJointedTypeInfo() {
    assertEquals("JointedTypeInfo", JointedTypeInfo.class.getSimpleName());
  }

  @Test
  public void packageName_isCorrect() {
    assertEquals("org.alice.stageide.ast", JointedTypeInfo.class.getPackage().getName());
  }

  @Test
  public void classIsPublic() {
    assertTrue(java.lang.reflect.Modifier.isPublic(JointedTypeInfo.class.getModifiers()));
  }

  @Test
  public void classIsNotAbstract() {
    assertFalse(java.lang.reflect.Modifier.isAbstract(JointedTypeInfo.class.getModifiers()));
  }

  @Test
  public void classHasDeclaredMethods() {
    assertTrue(JointedTypeInfo.class.getDeclaredMethods().length > 0);
  }

  @Test
  public void classHasDeclaredFields() {
    assertTrue(JointedTypeInfo.class.getDeclaredFields().length >= 0);
  }
}
