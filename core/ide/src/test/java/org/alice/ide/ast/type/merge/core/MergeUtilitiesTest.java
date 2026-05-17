package org.alice.ide.ast.type.merge.core;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

/**
 * Tests for {@link MergeUtilities} — AST type/method matching and equivalence checks.
 * Tests only the methods that don't require IDE singleton or ProjectStack.
 */
public class MergeUtilitiesTest {

  // ---- isHeaderEquivalent ----
  // Note: isHeaderEquivalent uses a JavaCodeGenerator that needs the method
  // to have a declaring type set. We test with methods attached to types.

  @Test
  public void isHeaderEquivalent_identicalMethods() {
    NamedUserType typeA = createTypeWithMethod("TypeA", "doSomething");
    NamedUserType typeB = createTypeWithMethod("TypeB", "doSomething");
    UserMethod a = typeA.methods.get(0);
    UserMethod b = typeB.methods.get(0);
    assertTrue(MergeUtilities.isHeaderEquivalent(a, b));
  }

  @Test
  public void isHeaderEquivalent_differentNames() {
    NamedUserType typeA = createTypeWithMethod("TypeA", "doFoo");
    NamedUserType typeB = createTypeWithMethod("TypeB", "doBar");
    UserMethod a = typeA.methods.get(0);
    UserMethod b = typeB.methods.get(0);
    assertFalse(MergeUtilities.isHeaderEquivalent(a, b));
  }

  // ---- findMethodWithMatchingName ----

  @Test
  public void findMethodWithMatchingName_found() {
    NamedUserType type = createTypeWithMethod("MyType", "myMethod");
    UserMethod query = createSimpleMethod("myMethod");
    UserMethod result = MergeUtilities.findMethodWithMatchingName(query, type);
    assertNotNull(result);
    assertEquals("myMethod", result.getName());
  }

  @Test
  public void findMethodWithMatchingName_notFound() {
    NamedUserType type = createTypeWithMethod("MyType", "existingMethod");
    UserMethod query = createSimpleMethod("missingMethod");
    UserMethod result = MergeUtilities.findMethodWithMatchingName(query, type);
    assertNull(result);
  }

  // ---- isValueTypeEquivalent ----

  @Test
  public void isValueTypeEquivalent_sameType() {
    UserField a = createField("field1", JavaType.getInstance(Double.class));
    UserField b = createField("field2", JavaType.getInstance(Double.class));
    assertTrue(MergeUtilities.isValueTypeEquivalent(a, b));
  }

  @Test
  public void isValueTypeEquivalent_differentTypes() {
    UserField a = createField("field1", JavaType.getInstance(Double.class));
    UserField b = createField("field2", JavaType.getInstance(Integer.class));
    assertFalse(MergeUtilities.isValueTypeEquivalent(a, b));
  }

  // ---- helpers ----

  private static UserMethod createSimpleMethod(String name) {
    UserMethod method = new UserMethod();
    method.name.setValue(name);
    method.returnType.setValue(JavaType.VOID_TYPE);
    method.managementLevel.setValue(ManagementLevel.NONE);
    return method;
  }

  private static NamedUserType createTypeWithMethod(String typeName, String methodName) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(typeName);
    type.superType.setValue(JavaType.getInstance(Object.class));
    UserMethod method = createSimpleMethod(methodName);
    type.methods.add(method);
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
