package org.alice.ide.typemanager;

import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TypeManagerGetNamedUserTypesFromSuperTypesTest {
  public static class PlainType {
    public PlainType() {
    }
  }

  public static class ResourceBackedType {
    public ResourceBackedType(String value) {
    }
  }

  @Test
  public void getNamedUserTypesFromSuperTypes_preservesInputOrder() {
    List<NamedUserType> types = TypeManager.getNamedUserTypesFromSuperTypes(Arrays.asList(
        JavaType.getInstance(PlainType.class),
        JavaType.getInstance(ResourceBackedType.class)));
    assertEquals("PlainType", types.get(0).getName());
    assertEquals("ResourceBackedType", types.get(1).getName());
  }
}
