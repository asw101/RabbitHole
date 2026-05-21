package org.alice.ide.typemanager;

import org.junit.Test;
import org.lgna.project.ast.JavaField;
import org.lgna.project.ast.JavaType;

import static org.junit.Assert.assertEquals;

public class TypeManagerArgumentFieldNameTest {
  public enum SingleOptionResource { ONLY }

  public static class TestAncestor {
    public TestAncestor(SingleOptionResource resource) {
    }
  }

  @Test
  public void createClassNameFromArgumentField_stripsResourceSuffixFromDeclaringType() {
    String name = TypeManager.createClassNameFromArgumentField(JavaType.getInstance(TestAncestor.class), JavaField.getInstance(SingleOptionResource.class, "ONLY"));
    assertEquals("SingleOption", name);
  }
}
