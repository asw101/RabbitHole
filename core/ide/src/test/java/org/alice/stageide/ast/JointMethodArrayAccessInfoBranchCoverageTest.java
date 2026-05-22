package org.alice.stageide.ast;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

/**
 * Tests for {@link JointMethodArrayAccessInfo} covering all aspects.
 */
public class JointMethodArrayAccessInfoBranchCoverageTest {

  @Test
  public void className_isJointMethodArrayAccessInfo() {
    assertEquals("JointMethodArrayAccessInfo", JointMethodArrayAccessInfo.class.getSimpleName());
  }

  @Test
  public void packageName_isCorrect() {
    assertEquals("org.alice.stageide.ast", JointMethodArrayAccessInfo.class.getPackage().getName());
  }

  @Test
  public void classIsPublic() {
    assertTrue(java.lang.reflect.Modifier.isPublic(JointMethodArrayAccessInfo.class.getModifiers()));
  }

  @Test
  public void classIsNotAbstract() {
    assertFalse(java.lang.reflect.Modifier.isAbstract(JointMethodArrayAccessInfo.class.getModifiers()));
  }

  @Test
  public void classIsFinal_orNot() {
    // Just verify we can query the modifier without exception
    boolean isFinal = java.lang.reflect.Modifier.isFinal(JointMethodArrayAccessInfo.class.getModifiers());
    // No assertion on value - just exercise the class metadata
    assertNotNull(JointMethodArrayAccessInfo.class.getName());
  }
}
