package org.alice.stageide.modelresource;

import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.story.resources.BipedResource;
import org.lgna.story.resources.PropResource;

import static org.junit.Assert.*;

public class ClassResourceKeyTest {
  @Test
  public void constructor_setsModelResourceCls() {
    ClassResourceKey key = new ClassResourceKey(BipedResource.class);
    assertEquals(BipedResource.class, key.getModelResourceCls());
  }

  @Test
  public void getType_returnsJavaType() {
    ClassResourceKey key = new ClassResourceKey(PropResource.class);
    assertEquals(JavaType.getInstance(PropResource.class), key.getType());
  }

  @Test
  public void equals_sameClass() {
    ClassResourceKey key1 = new ClassResourceKey(BipedResource.class);
    ClassResourceKey key2 = new ClassResourceKey(BipedResource.class);
    assertEquals(key1, key2);
  }

  @Test
  public void equals_differentClass() {
    ClassResourceKey key1 = new ClassResourceKey(BipedResource.class);
    ClassResourceKey key2 = new ClassResourceKey(PropResource.class);
    assertNotEquals(key1, key2);
  }

  @Test
  public void equals_self() {
    ClassResourceKey key = new ClassResourceKey(BipedResource.class);
    assertEquals(key, key);
  }

  @Test
  public void equals_null() {
    ClassResourceKey key = new ClassResourceKey(BipedResource.class);
    assertNotEquals(key, null);
  }

  @Test
  public void equals_differentType() {
    ClassResourceKey key = new ClassResourceKey(BipedResource.class);
    assertNotEquals(key, "not a key");
  }

  @Test
  public void hashCode_consistent() {
    ClassResourceKey key = new ClassResourceKey(BipedResource.class);
    assertEquals(key.hashCode(), key.hashCode());
  }

  @Test
  public void hashCode_equalForEqualObjects() {
    ClassResourceKey key1 = new ClassResourceKey(BipedResource.class);
    ClassResourceKey key2 = new ClassResourceKey(BipedResource.class);
    assertEquals(key1.hashCode(), key2.hashCode());
  }

  @Test
  public void toString_containsClassName() {
    ClassResourceKey key = new ClassResourceKey(BipedResource.class);
    assertTrue(key.toString().contains("BipedResource"));
  }

  @Test
  public void isInterface_forNonInterface() {
    ClassResourceKey key = new ClassResourceKey(BipedResource.class);
    assertTrue(key.isInterface());
  }

  @Test
  public void isInstanceCreator_returnsTrue() {
    ClassResourceKey key = new ClassResourceKey(BipedResource.class);
    assertTrue(key.isInstanceCreator());
  }

  @Test
  public void getInternalName_notNull() {
    ClassResourceKey key = new ClassResourceKey(BipedResource.class);
    assertNotNull(key.getInternalName());
  }

  @Test
  public void getSearchText_notNull() {
    ClassResourceKey key = new ClassResourceKey(BipedResource.class);
    assertNotNull(key.getSearchText());
  }
}
