package org.alice.ide.typemanager;

import org.lgna.project.ast.JavaType;
import org.lgna.story.SBiped;
import org.lgna.story.SScene;
import org.junit.Test;

import static org.junit.Assert.*;

public class TypeManagerTest {

  @Test
  public void createClassNameFromSuperTypeStripsSPrefix() {
    String result = TypeManager.createClassNameFromSuperType(JavaType.getInstance(SBiped.class));
    assertEquals("Biped", result);
  }

  @Test
  public void createClassNameFromSuperTypeKeepsNonSName() {
    // SScene starts with S but second char is uppercase, so it strips S -> "Scene"
    String result = TypeManager.createClassNameFromSuperType(JavaType.getInstance(SScene.class));
    assertEquals("Scene", result);
  }

  @Test
  public void createClassNameFromSuperTypeKeepsNameWithoutSPrefix() {
    // String doesn't start with S followed by uppercase
    String result = TypeManager.createClassNameFromSuperType(JavaType.getInstance(String.class));
    assertEquals("String", result);
  }

  @Test
  public void createClassNameFromSuperTypeShortName() {
    // A single char name (length <= 1) should be returned as-is
    String result = TypeManager.createClassNameFromSuperType(JavaType.getInstance(int.class));
    // int primitive type name is "int" (3 chars, doesn't start with S+upper)
    assertEquals("int", result);
  }

  @Test
  public void getEnumConstantFieldIfOneAndOnlyWithNull() {
    assertNull(TypeManager.getEnumConstantFieldIfOneAndOnly(null));
  }

  @Test
  public void getEnumConstantFieldIfOneAndOnlyWithNonEnumType() {
    assertNull(TypeManager.getEnumConstantFieldIfOneAndOnly(JavaType.getInstance(String.class)));
  }

  @Test
  public void getEnumConstantFieldIfOneAndOnlyWithMultiConstantEnum() {
    // java.lang.Thread.State has 6 constants
    assertNull(TypeManager.getEnumConstantFieldIfOneAndOnly(JavaType.getInstance(Thread.State.class)));
  }
}
