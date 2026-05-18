package org.alice.ide.typemanager;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

/**
 * Boundary and edge-case tests for {@link ResourceTypeUtilities}.
 *
 * <p>Complements {@link ResourceTypeUtilitiesTest} by exercising structural
 * variations (types with fields set but empty constructors, chained super-invocation
 * shapes) and validating null-safety contracts.
 */
public class ResourceTypeUtilitiesEdgeCaseTest {

  // ── Helper: build type with a UserField but no constructors ────────

  private static NamedUserType buildTypeWithFieldsNoCtors() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("HasFields");
    type.superType.setValue(JavaType.getInstance(Object.class));
    UserField field = new UserField();
    field.name.setValue("myField");
    field.valueType.setValue(JavaType.getInstance(String.class));
    type.fields.add(field);
    return type;
  }

  // ── Helper: build type with constructor that has body but no super invocation args ──

  private static NamedUserType buildTypeWithEmptyBodyCtor() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("EmptyBodyCtor");
    type.superType.setValue(JavaType.getInstance(Object.class));

    NamedUserConstructor ctor = new NamedUserConstructor();
    ConstructorBlockStatement body = new ConstructorBlockStatement();
    SuperConstructorInvocationStatement superInv = new SuperConstructorInvocationStatement();
    body.constructorInvocationStatement.setValue(superInv);
    ctor.body.setValue(body);
    type.constructors.add(ctor);
    return type;
  }

  // ── Tests ──────────────────────────────────────────────────────────

  @Test
  public void getResourceType_typeWithFieldsButNoCtors_returnsNull() {
    NamedUserType type = buildTypeWithFieldsNoCtors();
    assertNull("Type with fields but no constructors → null",
        ResourceTypeUtilities.getResourceType(type));
  }

  @Test
  public void getResourceFieldOrType_typeWithFieldsButNoCtors_returnsNull() {
    NamedUserType type = buildTypeWithFieldsNoCtors();
    assertNull(ResourceTypeUtilities.getResourceFieldOrType(type));
  }

  @Test
  public void getResourceType_emptyBodyCtor_returnsNull() {
    NamedUserType type = buildTypeWithEmptyBodyCtor();
    assertNull("Empty-body ctor (0 super args) → null",
        ResourceTypeUtilities.getResourceType(type));
  }

  @Test
  public void getResourceFieldOrType_oneParamReturnsExpectedType() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("IntParamType");
    type.superType.setValue(JavaType.getInstance(Object.class));

    NamedUserConstructor ctor = new NamedUserConstructor();
    ConstructorBlockStatement body = new ConstructorBlockStatement();
    body.constructorInvocationStatement.setValue(new SuperConstructorInvocationStatement());
    ctor.body.setValue(body);

    UserParameter param = new UserParameter();
    param.name.setValue("count");
    param.valueType.setValue(JavaType.getInstance(Integer.class));
    ctor.requiredParameters.add(param);
    type.constructors.add(ctor);

    Declaration result = ResourceTypeUtilities.getResourceFieldOrType(type);
    assertTrue("Should return the Integer JavaType",
        result instanceof JavaType);
    assertEquals(JavaType.getInstance(Integer.class), result);
  }

  @Test
  public void getResourceType_consistentWithGetResourceFieldOrType() {
    // When getResourceFieldOrType returns a JavaType, getResourceType should return it directly
    NamedUserType type = new NamedUserType();
    type.name.setValue("ConsistencyTest");
    type.superType.setValue(JavaType.getInstance(Object.class));

    NamedUserConstructor ctor = new NamedUserConstructor();
    ConstructorBlockStatement body = new ConstructorBlockStatement();
    body.constructorInvocationStatement.setValue(new SuperConstructorInvocationStatement());
    ctor.body.setValue(body);

    UserParameter param = new UserParameter();
    param.name.setValue("val");
    param.valueType.setValue(JavaType.getInstance(Double.class));
    ctor.requiredParameters.add(param);
    type.constructors.add(ctor);

    Declaration fieldOrType = ResourceTypeUtilities.getResourceFieldOrType(type);
    JavaType resourceType = ResourceTypeUtilities.getResourceType(type);

    assertNotNull(fieldOrType);
    assertNotNull(resourceType);
    assertSame("When fieldOrType is a JavaType, getResourceType returns same ref",
        fieldOrType, resourceType);
  }

  @Test
  public void getResourceFieldOrType_fieldAccess_fieldNameMatchesSource() {
    // Build a type whose super invocation has a FieldAccess to Thread.State.RUNNABLE
    NamedUserType type = new NamedUserType();
    type.name.setValue("RunnableFieldType");
    type.superType.setValue(JavaType.getInstance(Object.class));

    NamedUserConstructor ctor = new NamedUserConstructor();
    ConstructorBlockStatement body = new ConstructorBlockStatement();
    SuperConstructorInvocationStatement superInv = new SuperConstructorInvocationStatement();

    JavaField runnableField = JavaField.getInstance(Thread.State.class, "RUNNABLE");
    FieldAccess access = new FieldAccess(
        new TypeExpression(JavaType.getInstance(Thread.State.class)),
        runnableField);

    UserParameter fakeParam = new UserParameter();
    fakeParam.name.setValue("state");
    fakeParam.valueType.setValue(JavaType.getInstance(Thread.State.class));

    superInv.requiredArguments.add(new SimpleArgument(fakeParam, access));
    body.constructorInvocationStatement.setValue(superInv);
    ctor.body.setValue(body);
    type.constructors.add(ctor);

    Declaration result = ResourceTypeUtilities.getResourceFieldOrType(type);
    assertNotNull(result);
    assertTrue(result instanceof JavaField);
    assertEquals("RUNNABLE", ((JavaField) result).getName());
  }

  @Test
  public void getResourceType_multipleFieldAccess_returnsDeclaringType() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("TerminatedFieldType");
    type.superType.setValue(JavaType.getInstance(Object.class));

    NamedUserConstructor ctor = new NamedUserConstructor();
    ConstructorBlockStatement body = new ConstructorBlockStatement();
    SuperConstructorInvocationStatement superInv = new SuperConstructorInvocationStatement();

    JavaField terminatedField = JavaField.getInstance(Thread.State.class, "TERMINATED");
    FieldAccess access = new FieldAccess(
        new TypeExpression(JavaType.getInstance(Thread.State.class)),
        terminatedField);

    UserParameter fakeParam = new UserParameter();
    fakeParam.name.setValue("state");
    fakeParam.valueType.setValue(JavaType.getInstance(Thread.State.class));

    superInv.requiredArguments.add(new SimpleArgument(fakeParam, access));
    body.constructorInvocationStatement.setValue(superInv);
    ctor.body.setValue(body);
    type.constructors.add(ctor);

    JavaType result = ResourceTypeUtilities.getResourceType(type);
    assertNotNull(result);
    assertEquals(JavaType.getInstance(Thread.State.class), result);
  }

  @Test
  public void getResourceFieldOrType_superWithNullLiteral_returnsNull() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("NullLiteralType");
    type.superType.setValue(JavaType.getInstance(Object.class));

    NamedUserConstructor ctor = new NamedUserConstructor();
    ConstructorBlockStatement body = new ConstructorBlockStatement();
    SuperConstructorInvocationStatement superInv = new SuperConstructorInvocationStatement();

    UserParameter fakeParam = new UserParameter();
    fakeParam.name.setValue("x");
    fakeParam.valueType.setValue(JavaType.getInstance(Object.class));

    superInv.requiredArguments.add(new SimpleArgument(fakeParam, new NullLiteral()));
    body.constructorInvocationStatement.setValue(superInv);
    ctor.body.setValue(body);
    type.constructors.add(ctor);

    assertNull("NullLiteral is not FieldAccess → null",
        ResourceTypeUtilities.getResourceFieldOrType(type));
  }

  @Test
  public void getResourceFieldOrType_superWithIntegerLiteral_returnsNull() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("IntLiteralType");
    type.superType.setValue(JavaType.getInstance(Object.class));

    NamedUserConstructor ctor = new NamedUserConstructor();
    ConstructorBlockStatement body = new ConstructorBlockStatement();
    SuperConstructorInvocationStatement superInv = new SuperConstructorInvocationStatement();

    UserParameter fakeParam = new UserParameter();
    fakeParam.name.setValue("n");
    fakeParam.valueType.setValue(JavaType.getInstance(Integer.class));

    superInv.requiredArguments.add(new SimpleArgument(fakeParam, new IntegerLiteral(42)));
    body.constructorInvocationStatement.setValue(superInv);
    ctor.body.setValue(body);
    type.constructors.add(ctor);

    assertNull("IntegerLiteral is not FieldAccess → null",
        ResourceTypeUtilities.getResourceFieldOrType(type));
  }
}
