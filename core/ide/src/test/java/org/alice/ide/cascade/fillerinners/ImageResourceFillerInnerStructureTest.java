package org.alice.ide.cascade.fillerinners;

import org.junit.Test;
import org.lgna.common.resources.ImageResource;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static org.junit.Assert.*;

public class ImageResourceFillerInnerStructureTest {
  @Test
  public void classUsesImageResourceGenericArgument() {
    ParameterizedType type = (ParameterizedType) ImageResourceFillerInner.class.getGenericSuperclass();
    Type[] arguments = type.getActualTypeArguments();

    assertEquals(ResourceFillerInner.class, type.getRawType());
    assertEquals(ImageResource.class, arguments[0]);
  }

  @Test
  public void constructorIsPublicNoArg() throws Exception {
    assertNotNull(ImageResourceFillerInner.class.getConstructor());
  }

  @Test
  public void protectedTemplateMethodsMatchExpectedNames() {
    assertNotNull(findMethod("getResourceExpressionFillIn"));
    assertNotNull(findMethod("getImportNewResourceFillIn"));
  }

  private java.lang.reflect.Method findMethod(String name) {
    for (java.lang.reflect.Method method : ImageResourceFillerInner.class.getDeclaredMethods()) {
      if (method.getName().equals(name)) {
        return method;
      }
    }
    return null;
  }
}
