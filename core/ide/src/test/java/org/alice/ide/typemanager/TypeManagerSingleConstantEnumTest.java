package org.alice.ide.typemanager;

import org.junit.Test;
import org.lgna.project.ast.JavaField;
import org.lgna.project.ast.JavaType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TypeManagerSingleConstantEnumTest {
  public enum SingleOptionResource { ONLY }

  @Test
  public void getEnumConstantFieldIfOneAndOnly_returnsOnlyConstantField() {
    JavaField field = TypeManager.getEnumConstantFieldIfOneAndOnly(JavaType.getInstance(SingleOptionResource.class));
    assertNotNull(field);
    assertEquals("ONLY", field.getName());
  }
}
