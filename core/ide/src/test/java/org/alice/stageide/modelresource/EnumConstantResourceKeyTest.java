package org.alice.stageide.modelresource;

import org.junit.Test;
import org.lgna.story.resources.fish.ArapaimaResource;

import static org.junit.Assert.*;

public class EnumConstantResourceKeyTest {
  @Test
  public void getEnumConstant_returnsSameConstant() {
    EnumConstantResourceKey key = new EnumConstantResourceKey(ArapaimaResource.DEFAULT);
    assertSame(ArapaimaResource.DEFAULT, key.getEnumConstant());
  }

  @Test
  public void getModelResourceCls_returnsDeclaringClass() {
    EnumConstantResourceKey key = new EnumConstantResourceKey(ArapaimaResource.DEFAULT);
    assertEquals(ArapaimaResource.class, key.getModelResourceCls());
  }

  @Test
  public void equals_sameEnum() {
    EnumConstantResourceKey key1 = new EnumConstantResourceKey(ArapaimaResource.DEFAULT);
    EnumConstantResourceKey key2 = new EnumConstantResourceKey(ArapaimaResource.DEFAULT);
    assertEquals(key1, key2);
  }

  @Test
  public void equals_self() {
    EnumConstantResourceKey key = new EnumConstantResourceKey(ArapaimaResource.DEFAULT);
    assertEquals(key, key);
  }

  @Test
  public void equals_null() {
    EnumConstantResourceKey key = new EnumConstantResourceKey(ArapaimaResource.DEFAULT);
    assertNotEquals(key, null);
  }

  @Test
  public void equals_differentType() {
    EnumConstantResourceKey key = new EnumConstantResourceKey(ArapaimaResource.DEFAULT);
    assertNotEquals(key, "not a key");
  }

  @Test
  public void hashCode_consistent() {
    EnumConstantResourceKey key = new EnumConstantResourceKey(ArapaimaResource.DEFAULT);
    assertEquals(key.hashCode(), key.hashCode());
  }

  @Test
  public void hashCode_equalForEqualObjects() {
    EnumConstantResourceKey key1 = new EnumConstantResourceKey(ArapaimaResource.DEFAULT);
    EnumConstantResourceKey key2 = new EnumConstantResourceKey(ArapaimaResource.DEFAULT);
    assertEquals(key1.hashCode(), key2.hashCode());
  }

  @Test
  public void isLeaf_returnsTrue() {
    EnumConstantResourceKey key = new EnumConstantResourceKey(ArapaimaResource.DEFAULT);
    assertTrue(key.isLeaf());
  }

  @Test
  public void toString_containsEnumName() {
    EnumConstantResourceKey key = new EnumConstantResourceKey(ArapaimaResource.DEFAULT);
    assertTrue(key.toString().contains("DEFAULT"));
  }

  @Test
  public void isInstanceCreator_returnsTrue() {
    EnumConstantResourceKey key = new EnumConstantResourceKey(ArapaimaResource.DEFAULT);
    assertTrue(key.isInstanceCreator());
  }
}
