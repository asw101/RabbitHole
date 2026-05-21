package org.alice.ide.typemanager;

import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TypeManagerCreateTypeForTest {
  public static class CustomStoryType {
    public CustomStoryType(String value) {
    }
  }

  @Test
  public void createTypeFor_createsConstructorsFromSuperConstructors() throws Exception {
    NamedUserType type = TypeManagerTestSupport.invokeCreateTypeFor(JavaType.getInstance(CustomStoryType.class), "GeneratedType", null, null);
    assertEquals("GeneratedType", type.getName());
    assertEquals(JavaType.getInstance(CustomStoryType.class), type.superType.getValue());
    assertFalse(type.constructors.isEmpty());
  }
}
