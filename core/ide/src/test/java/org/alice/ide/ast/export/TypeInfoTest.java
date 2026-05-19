package org.alice.ide.ast.export;

import org.junit.Test;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class TypeInfoTest {
  @Test
  public void extendsDeclarationInfo() {
    assertTrue(DeclarationInfo.class.isAssignableFrom(TypeInfo.class));
  }
  @Test
  public void isPublicNotAbstract() {
    int mods = TypeInfo.class.getModifiers();
    assertTrue(Modifier.isPublic(mods));
    assertFalse(Modifier.isAbstract(mods));
  }
  @Test
  public void hasGetConstructorInfosMethod() throws Exception {
    Method m = TypeInfo.class.getMethod("getConstructorInfos");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }
  @Test
  public void hasGetMethodInfosMethod() throws Exception {
    Method m = TypeInfo.class.getMethod("getMethodInfos");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }
  @Test
  public void hasGetFieldInfosMethod() throws Exception {
    Method m = TypeInfo.class.getMethod("getFieldInfos");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }
  @Test
  public void hasGetSuperTypeInfoMethod() throws Exception {
    Method m = TypeInfo.class.getMethod("getSuperTypeInfo");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }
}
