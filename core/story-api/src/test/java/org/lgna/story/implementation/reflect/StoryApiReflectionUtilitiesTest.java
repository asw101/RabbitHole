package org.lgna.story.implementation.reflect;

import org.junit.Test;
import org.lgna.story.SBiped;
import org.lgna.story.SFlyer;
import org.lgna.story.SJointedModel;
import org.lgna.story.SQuadruped;
import org.lgna.story.implementation.JointedModelImp;
import org.lgna.story.resources.BipedResource;
import org.lgna.story.resources.FlyerResource;
import org.lgna.story.resources.QuadrupedResource;

import java.lang.reflect.Constructor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class StoryApiReflectionUtilitiesTest {
  private static class ResourceLessJointedModel extends SJointedModel {
    @Override
    public JointedModelImp getImplementation() {
      return null;
    }
  }

  @Test
  public void privateConstructorThrowsAssertionError() throws Exception {
    Constructor<StoryApiReflectionUtilities> constructor = StoryApiReflectionUtilities.class.getDeclaredConstructor();
    constructor.setAccessible(true);
    try {
      constructor.newInstance();
      fail("expected constructor to throw");
    } catch (ReflectiveOperationException exception) {
      assertEquals(AssertionError.class, exception.getCause().getClass());
    }
  }

  @Test
  public void bipedModelResolvesBipedResourceClass() {
    assertEquals(BipedResource.class, StoryApiReflectionUtilities.getResourceClassForModelClass(SBiped.class));
  }

  @Test
  public void flyerModelResolvesFlyerResourceClass() {
    assertEquals(FlyerResource.class, StoryApiReflectionUtilities.getResourceClassForModelClass(SFlyer.class));
  }

  @Test
  public void quadrupedModelResolvesQuadrupedResourceClass() {
    assertEquals(QuadrupedResource.class, StoryApiReflectionUtilities.getResourceClassForModelClass(SQuadruped.class));
  }

  @Test
  public void modelWithoutSingleResourceConstructorReturnsNull() {
    assertNull(StoryApiReflectionUtilities.getResourceClassForModelClass(ResourceLessJointedModel.class));
  }
}
