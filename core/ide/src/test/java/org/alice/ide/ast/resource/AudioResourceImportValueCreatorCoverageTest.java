package org.alice.ide.ast.resource;

import org.junit.Test;
import org.lgna.common.resources.AudioResource;
import org.lgna.project.ast.ResourceExpression;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

import static org.junit.Assert.*;

public class AudioResourceImportValueCreatorCoverageTest {
  @Test
  public void getInstance_returnsSingleton() {
    assertSame(AudioResourceImportValueCreator.getInstance(), AudioResourceImportValueCreator.getInstance());
  }

  @Test
  public void inheritedResourceClassField_isAudioResource() throws Exception {
    Field field = ResourceImportValueCreator.class.getDeclaredField("resourceCls");
    field.setAccessible(true);
    assertEquals(AudioResource.class, field.get(AudioResourceImportValueCreator.getInstance()));
  }

  @Test
  public void createValueFromImportedValue_returnsAudioResourceExpression() throws Exception {
    Method method = ResourceImportValueCreator.class.getDeclaredMethod("createValueFromImportedValue", org.lgna.common.Resource.class);
    method.setAccessible(true);
    AudioResource resource = new AudioResource(UUID.randomUUID());
    ResourceExpression expression = (ResourceExpression) method.invoke(AudioResourceImportValueCreator.getInstance(), resource);
    assertSame(resource, expression.resource.getValue());
  }
}
