package org.alice.ide.type;

import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;

import static org.junit.Assert.*;

/**
 * Tests for the type key hierarchy:
 * TypeKey, AbstractExtendsTypeKey, ExtendsTypeKey,
 * ExtendsTypeWithNamedType, ExtendsTypeWithSuperArgumentFieldKey,
 * ExtendsTypeWithConstructorParameterTypeKey.
 */
public class TypeUtilitiesTest {

  // -- TypeKey.createType ------------------------------------------------

  @Test
  public void typeKey_createType_returnsNonNull() {
    ExtendsTypeKey key = new ExtendsTypeKey(JavaType.getInstance(Object.class));
    NamedUserType type = key.createType();
    assertNotNull(type);
  }

  // -- ExtendsTypeKey equality -------------------------------------------

  @Test
  public void extendsTypeKey_equalsSameSuper() {
    JavaType sup = JavaType.getInstance(String.class);
    ExtendsTypeKey a = new ExtendsTypeKey(sup);
    ExtendsTypeKey b = new ExtendsTypeKey(sup);
    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
  }

  @Test
  public void extendsTypeKey_notEqualsDifferentSuper() {
    ExtendsTypeKey a = new ExtendsTypeKey(JavaType.getInstance(String.class));
    ExtendsTypeKey b = new ExtendsTypeKey(JavaType.getInstance(Integer.class));
    assertNotEquals(a, b);
  }

  @Test
  public void extendsTypeKey_notEqualsNull() {
    ExtendsTypeKey a = new ExtendsTypeKey(JavaType.getInstance(Object.class));
    assertNotEquals(a, null);
  }

  @Test
  public void extendsTypeKey_notEqualsDifferentClass() {
    JavaType sup = JavaType.getInstance(Object.class);
    ExtendsTypeKey a = new ExtendsTypeKey(sup);
    ExtendsTypeWithNamedType b = new ExtendsTypeWithNamedType(sup, "Foo");
    assertNotEquals(a, b);
  }

  @Test
  public void extendsTypeKey_getSuperType() {
    JavaType sup = JavaType.getInstance(Object.class);
    ExtendsTypeKey key = new ExtendsTypeKey(sup);
    assertSame(sup, key.getSuperType());
  }

  // -- ExtendsTypeWithNamedType ------------------------------------------

  @Test
  public void extendsTypeWithNamedType_equalsSameSuperAndName() {
    JavaType sup = JavaType.getInstance(Object.class);
    ExtendsTypeWithNamedType a = new ExtendsTypeWithNamedType(sup, "Alien2");
    ExtendsTypeWithNamedType b = new ExtendsTypeWithNamedType(sup, "Alien2");
    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
  }

  @Test
  public void extendsTypeWithNamedType_notEqualsDifferentName() {
    JavaType sup = JavaType.getInstance(Object.class);
    ExtendsTypeWithNamedType a = new ExtendsTypeWithNamedType(sup, "A");
    ExtendsTypeWithNamedType b = new ExtendsTypeWithNamedType(sup, "B");
    assertNotEquals(a, b);
  }

  @Test
  public void extendsTypeWithNamedType_notEqualsDifferentSuper() {
    ExtendsTypeWithNamedType a = new ExtendsTypeWithNamedType(JavaType.getInstance(String.class), "X");
    ExtendsTypeWithNamedType b = new ExtendsTypeWithNamedType(JavaType.getInstance(Integer.class), "X");
    assertNotEquals(a, b);
  }

  // -- ExtendsTypeWithConstructorParameterTypeKey -------------------------

  @Test
  public void constructorParamKey_equalsSameSuperAndParam() {
    JavaType sup = JavaType.getInstance(Object.class);
    JavaType param = JavaType.getInstance(String.class);
    ExtendsTypeWithConstructorParameterTypeKey a = new ExtendsTypeWithConstructorParameterTypeKey(sup, param);
    ExtendsTypeWithConstructorParameterTypeKey b = new ExtendsTypeWithConstructorParameterTypeKey(sup, param);
    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
  }

  @Test
  public void constructorParamKey_notEqualsDifferentParam() {
    JavaType sup = JavaType.getInstance(Object.class);
    ExtendsTypeWithConstructorParameterTypeKey a =
        new ExtendsTypeWithConstructorParameterTypeKey(sup, JavaType.getInstance(String.class));
    ExtendsTypeWithConstructorParameterTypeKey b =
        new ExtendsTypeWithConstructorParameterTypeKey(sup, JavaType.getInstance(Integer.class));
    assertNotEquals(a, b);
  }

  // -- ExtendsTypeWithSuperArgumentFieldKey --------------------------------

  @Test
  public void superArgFieldKey_equalsSameFieldRef() {
    JavaType sup = JavaType.getInstance(Object.class);
    org.lgna.project.ast.UserField field = new org.lgna.project.ast.UserField();
    field.name.setValue("myField");
    field.valueType.setValue(JavaType.getInstance(String.class));
    ExtendsTypeWithSuperArgumentFieldKey a = new ExtendsTypeWithSuperArgumentFieldKey(sup, field);
    ExtendsTypeWithSuperArgumentFieldKey b = new ExtendsTypeWithSuperArgumentFieldKey(sup, field);
    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
  }

  @Test
  public void superArgFieldKey_notEqualsDifferentField() {
    JavaType sup = JavaType.getInstance(Object.class);
    org.lgna.project.ast.UserField field1 = new org.lgna.project.ast.UserField();
    field1.name.setValue("f1");
    field1.valueType.setValue(JavaType.getInstance(String.class));
    org.lgna.project.ast.UserField field2 = new org.lgna.project.ast.UserField();
    field2.name.setValue("f2");
    field2.valueType.setValue(JavaType.getInstance(String.class));
    ExtendsTypeWithSuperArgumentFieldKey a = new ExtendsTypeWithSuperArgumentFieldKey(sup, field1);
    ExtendsTypeWithSuperArgumentFieldKey b = new ExtendsTypeWithSuperArgumentFieldKey(sup, field2);
    assertNotEquals(a, b);
  }

  // -- hashCode consistency ----------------------------------------------

  @Test
  public void hashCode_consistent_acrossMultipleCalls() {
    ExtendsTypeKey key = new ExtendsTypeKey(JavaType.getInstance(String.class));
    int h1 = key.hashCode();
    int h2 = key.hashCode();
    assertEquals(h1, h2);
  }
}
