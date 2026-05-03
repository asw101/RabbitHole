package org.lgna.project.resource;

import org.junit.Test;
import org.lgna.common.Resource;
import org.lgna.project.ast.UserField;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class ResourcesTypeWrapperTest {

  @Test
  public void mapsEachResourceToGeneratedField() {
    TestResource resource = new TestResource("image.png");
    ResourcesTypeWrapper wrapper = new ResourcesTypeWrapper(resources(resource));

    UserField field = wrapper.getFieldForResource(resource);

    assertNotNull(field);
    assertEquals("image_png", field.getName());
    assertEquals(field, wrapper.getType().fields.get(0));
  }

  @Test
  public void mapsDuplicateFixedNamesToTheirDistinctFields() {
    TestResource first = new TestResource("image.png");
    TestResource duplicate = new TestResource("image-png");
    ResourcesTypeWrapper wrapper = new ResourcesTypeWrapper(resources(first, duplicate));

    UserField firstField = wrapper.getFieldForResource(first);
    UserField duplicateField = wrapper.getFieldForResource(duplicate);

    assertNotNull(firstField);
    assertNotNull(duplicateField);
    assertNotEquals(firstField, duplicateField);
    assertEquals("image_png", firstField.getName());
    assertEquals("image_png_duplicate_0", duplicateField.getName());
  }

  private static Set<Resource> resources(Resource... resources) {
    Set<Resource> resourceSet = new LinkedHashSet<>();
    for (Resource resource : resources) {
      resourceSet.add(resource);
    }
    return resourceSet;
  }

  public static class TestResource extends Resource {
    public TestResource(Class<?> resourceClass, String resourceName, String contentType) {
      super(resourceClass, resourceName, contentType);
    }

    TestResource(String fileName) {
      super(fileName, "text/plain", new byte[0]);
    }
  }
}
