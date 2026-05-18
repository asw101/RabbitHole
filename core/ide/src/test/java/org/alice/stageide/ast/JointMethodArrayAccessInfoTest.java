package org.alice.stageide.ast;

import org.junit.Test;
import org.lgna.project.ast.JavaMethod;

import static org.junit.Assert.*;

public class JointMethodArrayAccessInfoTest {
  @Test
  public void constructorStoresMethod() throws Exception {
    JavaMethod method = JavaMethod.getInstance(String.class.getMethod("trim"));
    JointMethodArrayAccessInfo info = new JointMethodArrayAccessInfo(method, 3);

    assertSame(method, info.getMethod());
  }

  @Test
  public void constructorStoresIndex() throws Exception {
    JointMethodArrayAccessInfo info = new JointMethodArrayAccessInfo(JavaMethod.getInstance(String.class.getMethod("trim")), 7);

    assertEquals(7, info.getIndex());
  }

  @Test
  public void negativeIndexIsPreserved() throws Exception {
    JointMethodArrayAccessInfo info = new JointMethodArrayAccessInfo(JavaMethod.getInstance(String.class.getMethod("trim")), -1);

    assertEquals(-1, info.getIndex());
  }
}
