package org.alice.ide.ast.resource;

import org.junit.Test;
import org.lgna.common.resources.ImageResource;
import org.lgna.project.ast.ResourceExpression;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

import static org.junit.Assert.*;

public class ImageResourceImportValueCreatorCoverageTest {
  @Test
  public void getInstance_returnsSingleton() {
    assertSame(ImageResourceImportValueCreator.getInstance(), ImageResourceImportValueCreator.getInstance());
  }

  @Test
  public void inheritedResourceClassField_isImageResource() throws Exception {
    Field field = ResourceImportValueCreator.class.getDeclaredField("resourceCls");
    field.setAccessible(true);
    assertEquals(ImageResource.class, field.get(ImageResourceImportValueCreator.getInstance()));
  }

  @Test
  public void createValueFromImportedValue_returnsImageResourceExpression() throws Exception {
    Method method = ResourceImportValueCreator.class.getDeclaredMethod("createValueFromImportedValue", org.lgna.common.Resource.class);
    method.setAccessible(true);
    ImageResource resource = new ImageResource(UUID.randomUUID());
    ResourceExpression expression = (ResourceExpression) method.invoke(ImageResourceImportValueCreator.getInstance(), resource);
    assertSame(resource, expression.resource.getValue());
  }
}
