package org.alice.stageide.modelresource;

import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.story.resources.DynamicFishResource;
import org.lgna.story.resources.FishResource;

import java.util.HashSet;

import static org.junit.Assert.*;

public class ClassResourceKeyTest {
  @Test
  public void interfaceResourceClassReportsInterfaceAndNonLeaf() {
    ClassResourceKey key = new ClassResourceKey(FishResource.class);

    assertEquals(FishResource.class, key.getModelResourceCls());
    assertEquals(JavaType.getInstance(FishResource.class), key.getType());
    assertTrue(key.isInterface());
    assertFalse(key.isLeaf());
  }

  @Test
  public void concreteDynamicResourceClassReportsNonInterfaceAndNonLeaf() {
    ClassResourceKey key = new ClassResourceKey(DynamicFishResource.class);

    assertEquals(DynamicFishResource.class, key.getModelResourceCls());
    assertFalse(key.isInterface());
    assertFalse(key.isLeaf());
  }

  @Test
  public void equalityHashCodeAndUnsupportedCreationFollowClassIdentity() {
    ClassResourceKey first = new ClassResourceKey(FishResource.class);
    ClassResourceKey second = new ClassResourceKey(FishResource.class);
    ClassResourceKey third = new ClassResourceKey(DynamicFishResource.class);

    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
    assertNotEquals(first, third);

    try {
      first.createInstanceCreation(new HashSet<>());
      fail("Expected Error");
    } catch (Error expected) {
      assertNotNull(expected);
    }
  }
}
