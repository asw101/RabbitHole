package org.alice.stageide.ast;

import org.junit.Test;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Branch coverage tests for {@link JointMethodUtilities} class structure.
 */
public class JointMethodUtilitiesBranchCoverageTest {

  @Test
  public void className_isJointMethodUtilities() {
    assertEquals("JointMethodUtilities", JointMethodUtilities.class.getSimpleName());
  }

  @Test
  public void packageName_isCorrect() {
    assertEquals("org.alice.stageide.ast", JointMethodUtilities.class.getPackage().getName());
  }

  @Test
  public void classIsPublic() {
    assertTrue(Modifier.isPublic(JointMethodUtilities.class.getModifiers()));
  }

  @Test
  public void classHasIsJointGetterMethod() throws Exception {
    assertNotNull(JointMethodUtilities.class.getMethod("isJointGetter",
        org.lgna.project.ast.AbstractMethod.class));
  }

  @Test
  public void classHasIsJointArrayGetterMethod() throws Exception {
    assertNotNull(JointMethodUtilities.class.getMethod("isJointArrayGetter",
        org.lgna.project.ast.AbstractMethod.class));
  }

  @Test
  public void classHasGetJointArrayLengthMethod() throws Exception {
    assertNotNull(JointMethodUtilities.class.getMethod("getJointArrayLength",
        org.lgna.project.ast.AbstractMethod.class));
  }

  @Test
  public void classHasGetJointNameMethod() throws Exception {
    assertNotNull(JointMethodUtilities.class.getMethod("getJointName",
        org.lgna.project.ast.AbstractMethod.class, java.util.Locale.class));
  }

  @Test
  public void hasDeclaredMethods() {
    assertTrue(JointMethodUtilities.class.getDeclaredMethods().length > 0);
  }
}
