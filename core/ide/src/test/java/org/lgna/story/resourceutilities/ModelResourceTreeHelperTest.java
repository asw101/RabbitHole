package org.lgna.story.resourceutilities;

import org.junit.Test;
import org.lgna.story.resources.ModelResource;

import static org.junit.Assert.*;

public class ModelResourceTreeHelperTest {
  interface ExampleResource extends ModelResource {}
  interface OtherResource extends ModelResource {}
  interface Unrelated {}

  @Test
  public void createAlicePackageNameStripsRootPrefix() {
    assertEquals("org.lgna.story.resources.animals.pets",
        ModelResourceTreeHelper.createAlicePackageName("edu.example.animals.pets", "edu.example", "org.lgna.story.resources."));
  }

  @Test
  public void findFirstMatchingInterfaceReturnsFirstModelResource() {
    Class<? extends ModelResource> match = ModelResourceTreeHelper.findFirstMatchingInterface(
        new Class<?>[] {Unrelated.class, ExampleResource.class, OtherResource.class}, ModelResource.class);

    assertEquals(ExampleResource.class, match);
  }

  @Test
  public void getDynamicResourceClassNameUsesResourceSimpleName() {
    assertEquals("org.lgna.story.resources.DynamicExampleResource",
        ModelResourceTreeHelper.getDynamicResourceClassName(ExampleResource.class));
  }
}
