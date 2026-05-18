package org.alice.ide.ast.type.merge.core;

import org.junit.Test;
import org.lgna.project.ast.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Deep coverage tests for {@link MergeUtilities} — testing merge matching,
 * equivalence, and field comparison logic beyond the existing MergeUtilitiesTest.
 */
public class MergeUtilitiesDeepTest {

  // ---- findMatchingTypeInExistingTypes(type, collection) ----

  @Test
  public void findMatchingType_matchingNameExists_returnsMatch() {
    NamedUserType src = createNamedType("MyScene");
    NamedUserType dst = createNamedType("MyScene");
    Collection<NamedUserType> dstTypes = Collections.singletonList(dst);

    NamedUserType result = MergeUtilities.findMatchingTypeInExistingTypes(src, dstTypes);
    assertNotNull(result);
    assertEquals("MyScene", result.getName());
  }

  @Test
  public void findMatchingType_noMatch_returnsNull() {
    NamedUserType src = createNamedType("SceneA");
    NamedUserType dst = createNamedType("SceneB");
    Collection<NamedUserType> dstTypes = Collections.singletonList(dst);

    NamedUserType result = MergeUtilities.findMatchingTypeInExistingTypes(src, dstTypes);
    assertNull(result);
  }

  @Test
  public void findMatchingType_emptyCollection_returnsNull() {
    NamedUserType src = createNamedType("MyScene");
    Collection<NamedUserType> dstTypes = Collections.emptyList();

    NamedUserType result = MergeUtilities.findMatchingTypeInExistingTypes(src, dstTypes);
    assertNull(result);
  }

  @Test
  public void findMatchingType_multipleTypes_returnsFirstMatch() {
    NamedUserType src = createNamedType("SceneB");
    NamedUserType dst1 = createNamedType("SceneA");
    NamedUserType dst2 = createNamedType("SceneB");
    NamedUserType dst3 = createNamedType("SceneB"); // duplicate name
    List<NamedUserType> dstTypes = Arrays.asList(dst1, dst2, dst3);

    NamedUserType result = MergeUtilities.findMatchingTypeInExistingTypes(src, dstTypes);
    assertSame("Should return first match", dst2, result);
  }

  // ---- findMethodWithMatchingName ----

  @Test
  public void findMethodWithMatchingName_multipleMethodsInType_returnsCorrectOne() {
    NamedUserType type = createNamedType("TestType");
    addMethod(type, "alpha");
    addMethod(type, "beta");
    addMethod(type, "gamma");

    UserMethod query = createMethod("beta");
    UserMethod result = MergeUtilities.findMethodWithMatchingName(query, type);
    assertNotNull(result);
    assertEquals("beta", result.getName());
  }

  @Test
  public void findMethodWithMatchingName_emptyType_returnsNull() {
    NamedUserType type = createNamedType("EmptyType");
    UserMethod query = createMethod("anything");
    UserMethod result = MergeUtilities.findMethodWithMatchingName(query, type);
    assertNull(result);
  }

  // ---- isHeaderEquivalent ----

  @Test
  public void isHeaderEquivalent_sameName_sameReturn_returnsTrue() {
    NamedUserType typeA = createTypeWithVoidMethod("TypeA", "doWork");
    NamedUserType typeB = createTypeWithVoidMethod("TypeB", "doWork");
    assertTrue(MergeUtilities.isHeaderEquivalent(
        typeA.methods.get(0), typeB.methods.get(0)));
  }

  @Test
  public void isHeaderEquivalent_differentName_returnsFalse() {
    NamedUserType typeA = createTypeWithVoidMethod("TypeA", "doWork");
    NamedUserType typeB = createTypeWithVoidMethod("TypeB", "doPlay");
    assertFalse(MergeUtilities.isHeaderEquivalent(
        typeA.methods.get(0), typeB.methods.get(0)));
  }

  @Test
  public void isHeaderEquivalent_sameMethodDifferentTypes_returnsTrue() {
    NamedUserType typeA = createTypeWithVoidMethod("Foo", "run");
    NamedUserType typeB = createTypeWithVoidMethod("Bar", "run");
    assertTrue("Methods with same signature in different types should be header-equivalent",
        MergeUtilities.isHeaderEquivalent(typeA.methods.get(0), typeB.methods.get(0)));
  }

  // ---- isEquivalent (CodeGenerator) ----

  @Test
  public void isEquivalent_identicalMethods_returnsTrue() {
    NamedUserType typeA = createTypeWithVoidMethod("TypeA", "process");
    NamedUserType typeB = createTypeWithVoidMethod("TypeB", "process");
    assertTrue(MergeUtilities.isEquivalent(
        typeA.methods.get(0), typeB.methods.get(0)));
  }

  @Test
  public void isEquivalent_differentMethodNames_returnsFalse() {
    NamedUserType typeA = createTypeWithVoidMethod("TypeA", "process");
    NamedUserType typeB = createTypeWithVoidMethod("TypeB", "compute");
    assertFalse(MergeUtilities.isEquivalent(
        typeA.methods.get(0), typeB.methods.get(0)));
  }

  // ---- isValueTypeEquivalent ----

  @Test
  public void isValueTypeEquivalent_bothString_returnsTrue() {
    UserField a = createField("f1", JavaType.getInstance(String.class));
    UserField b = createField("f2", JavaType.getInstance(String.class));
    assertTrue(MergeUtilities.isValueTypeEquivalent(a, b));
  }

  @Test
  public void isValueTypeEquivalent_stringVsInteger_returnsFalse() {
    UserField a = createField("f1", JavaType.getInstance(String.class));
    UserField b = createField("f2", JavaType.getInstance(Integer.class));
    assertFalse(MergeUtilities.isValueTypeEquivalent(a, b));
  }

  @Test
  public void isValueTypeEquivalent_namedUserTypes_sameNames_returnsTrue() {
    NamedUserType customType1 = createNamedType("MyCustomType");
    NamedUserType customType2 = createNamedType("MyCustomType");
    UserField a = createField("f1", customType1);
    UserField b = createField("f2", customType2);
    assertTrue(MergeUtilities.isValueTypeEquivalent(a, b));
  }

  @Test
  public void isValueTypeEquivalent_namedUserTypes_differentNames_returnsFalse() {
    NamedUserType customType1 = createNamedType("TypeX");
    NamedUserType customType2 = createNamedType("TypeY");
    UserField a = createField("f1", customType1);
    UserField b = createField("f2", customType2);
    assertFalse(MergeUtilities.isValueTypeEquivalent(a, b));
  }

  // ---- mendMethodInvocationsAndFieldAccesses ----

  @Test
  public void mendMethodInvocationsAndFieldAccesses_emptyBody_doesNotThrow() {
    NamedUserType type = createTypeWithVoidMethod("SafeType", "safeMethod");
    // Just verify it doesn't throw on a trivially simple type
    MergeUtilities.mendMethodInvocationsAndFieldAccesses(type);
  }

  // ---- helpers ----

  private static NamedUserType createNamedType(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    type.superType.setValue(JavaType.getInstance(Object.class));
    return type;
  }

  private static UserMethod createMethod(String name) {
    UserMethod method = new UserMethod();
    method.name.setValue(name);
    method.returnType.setValue(JavaType.VOID_TYPE);
    method.managementLevel.setValue(ManagementLevel.NONE);
    method.body.setValue(new BlockStatement());
    return method;
  }

  private static void addMethod(NamedUserType type, String name) {
    type.methods.add(createMethod(name));
  }

  private static NamedUserType createTypeWithVoidMethod(String typeName, String methodName) {
    NamedUserType type = createNamedType(typeName);
    addMethod(type, methodName);
    return type;
  }

  private static UserField createField(String name, AbstractType<?, ?, ?> valueType) {
    UserField field = new UserField();
    field.name.setValue(name);
    field.valueType.setValue(valueType);
    field.initializer.setValue(new NullLiteral());
    return field;
  }
}
