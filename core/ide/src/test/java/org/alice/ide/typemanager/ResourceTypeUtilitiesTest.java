package org.alice.ide.typemanager;

import org.junit.Test;
import org.lgna.project.ast.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

/**
 * Characterization tests for {@link ResourceTypeUtilities}.
 * Covers all 7 branches of getResourceFieldOrType and the
 * getResourceType dispatch on its return value.
 *
 * <p>AST shapes built programmatically — no project or IDE context needed.
 */
public class ResourceTypeUtilitiesTest {

  private static final JavaType OBJECT_TYPE = JavaType.getInstance(Object.class);

  // ── AST helpers ─────────────────────────────────────────────────────

  /**
   * Build a NamedUserType whose single constructor has zero required params
   * and whose super-constructor invocation has zero arguments.
   * Branch: CONSTRUCTOR_COUNT==1 → REQUIRED_PARAMETER_COUNT==0 → SUPER_ARG_COUNT==0 → null
   */
  private static NamedUserType buildZeroArgSuperType() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("ZeroArgType");
    type.superType.setValue(OBJECT_TYPE);

    NamedUserConstructor ctor = new NamedUserConstructor();
    ConstructorBlockStatement body = new ConstructorBlockStatement();
    SuperConstructorInvocationStatement superInvocation = new SuperConstructorInvocationStatement();
    // zero required arguments on super
    body.constructorInvocationStatement.setValue(superInvocation);
    ctor.body.setValue(body);
    type.constructors.add(ctor);
    return type;
  }

  /**
   * Build a NamedUserType whose single constructor has zero required params
   * and whose super-constructor invocation has one FieldAccess argument.
   * Branch: CONSTRUCTOR_COUNT==1 → REQUIRED_PARAM==0 → SUPER_ARG==1 → FieldAccess → field
   */
  private static NamedUserType buildOneFieldAccessArgType() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("OneFieldArgType");
    type.superType.setValue(OBJECT_TYPE);

    NamedUserConstructor ctor = new NamedUserConstructor();
    ConstructorBlockStatement body = new ConstructorBlockStatement();
    SuperConstructorInvocationStatement superInvocation = new SuperConstructorInvocationStatement();

    // Create a FieldAccess expression — use Thread.State.NEW as a known static field
    JavaField enumField = JavaField.getInstance(Thread.State.class, "NEW");
    FieldAccess fieldAccess = new FieldAccess(
        new TypeExpression(JavaType.getInstance(Thread.State.class)),
        enumField);

    // We need an AbstractParameter to satisfy SimpleArgument — use the super constructor's parameter.
    // Since Object() has no params, we fabricate via a UserParameter as the "parameter" identity.
    UserParameter fakeParam = new UserParameter();
    fakeParam.name.setValue("resource");
    fakeParam.valueType.setValue(OBJECT_TYPE);

    SimpleArgument arg = new SimpleArgument(fakeParam, fieldAccess);
    superInvocation.requiredArguments.add(arg);

    body.constructorInvocationStatement.setValue(superInvocation);
    ctor.body.setValue(body);
    type.constructors.add(ctor);
    return type;
  }

  /**
   * Build a NamedUserType whose single constructor has zero required params
   * and whose super-constructor invocation has one NON-FieldAccess argument (e.g., NullLiteral).
   * Branch: CONSTRUCTOR_COUNT==1 → REQUIRED_PARAM==0 → SUPER_ARG==1 → not FieldAccess → null
   */
  private static NamedUserType buildOneNonFieldAccessArgType() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("NonFieldArgType");
    type.superType.setValue(OBJECT_TYPE);

    NamedUserConstructor ctor = new NamedUserConstructor();
    ConstructorBlockStatement body = new ConstructorBlockStatement();
    SuperConstructorInvocationStatement superInvocation = new SuperConstructorInvocationStatement();

    UserParameter fakeParam = new UserParameter();
    fakeParam.name.setValue("arg");
    fakeParam.valueType.setValue(OBJECT_TYPE);

    SimpleArgument arg = new SimpleArgument(fakeParam, new NullLiteral());
    superInvocation.requiredArguments.add(arg);

    body.constructorInvocationStatement.setValue(superInvocation);
    ctor.body.setValue(body);
    type.constructors.add(ctor);
    return type;
  }

  /**
   * Build a NamedUserType whose single constructor has zero required params
   * and whose super-constructor invocation has >1 arguments.
   * Branch: CONSTRUCTOR_COUNT==1 → REQUIRED_PARAM==0 → SUPER_ARG==default → null
   */
  private static NamedUserType buildMultiArgSuperType() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("MultiArgType");
    type.superType.setValue(OBJECT_TYPE);

    NamedUserConstructor ctor = new NamedUserConstructor();
    ConstructorBlockStatement body = new ConstructorBlockStatement();
    SuperConstructorInvocationStatement superInvocation = new SuperConstructorInvocationStatement();

    UserParameter p1 = new UserParameter();
    p1.name.setValue("a");
    p1.valueType.setValue(OBJECT_TYPE);
    UserParameter p2 = new UserParameter();
    p2.name.setValue("b");
    p2.valueType.setValue(OBJECT_TYPE);

    superInvocation.requiredArguments.add(new SimpleArgument(p1, new NullLiteral()));
    superInvocation.requiredArguments.add(new SimpleArgument(p2, new NullLiteral()));

    body.constructorInvocationStatement.setValue(superInvocation);
    ctor.body.setValue(body);
    type.constructors.add(ctor);
    return type;
  }

  /**
   * Build a NamedUserType whose single constructor has exactly 1 required parameter.
   * Branch: CONSTRUCTOR_COUNT==1 → REQUIRED_PARAM==1 → return parameter0.getValueType()
   */
  private static NamedUserType buildOneRequiredParamType() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("OneParamType");
    type.superType.setValue(OBJECT_TYPE);

    NamedUserConstructor ctor = new NamedUserConstructor();
    ConstructorBlockStatement body = new ConstructorBlockStatement();
    SuperConstructorInvocationStatement superInvocation = new SuperConstructorInvocationStatement();
    body.constructorInvocationStatement.setValue(superInvocation);
    ctor.body.setValue(body);

    UserParameter param = new UserParameter();
    param.name.setValue("resource");
    param.valueType.setValue(JavaType.getInstance(String.class));
    ctor.requiredParameters.add(param);

    type.constructors.add(ctor);
    return type;
  }

  /**
   * Build a NamedUserType whose single constructor has >1 required parameters.
   * Branch: CONSTRUCTOR_COUNT==1 → REQUIRED_PARAM > 1 → null
   */
  private static NamedUserType buildMultiRequiredParamType() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("MultiParamType");
    type.superType.setValue(OBJECT_TYPE);

    NamedUserConstructor ctor = new NamedUserConstructor();
    ConstructorBlockStatement body = new ConstructorBlockStatement();
    SuperConstructorInvocationStatement superInvocation = new SuperConstructorInvocationStatement();
    body.constructorInvocationStatement.setValue(superInvocation);
    ctor.body.setValue(body);

    UserParameter p1 = new UserParameter();
    p1.name.setValue("a");
    p1.valueType.setValue(JavaType.getInstance(String.class));
    UserParameter p2 = new UserParameter();
    p2.name.setValue("b");
    p2.valueType.setValue(JavaType.getInstance(Integer.class));
    ctor.requiredParameters.add(p1);
    ctor.requiredParameters.add(p2);

    type.constructors.add(ctor);
    return type;
  }

  /**
   * Build a NamedUserType with zero constructors.
   * Branch: CONSTRUCTOR_COUNT default → null
   */
  private static NamedUserType buildNoConstructorType() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("NoCtorType");
    type.superType.setValue(OBJECT_TYPE);
    // no constructors added
    return type;
  }

  /**
   * Build a NamedUserType with more than one constructor.
   * Branch: CONSTRUCTOR_COUNT default → null
   */
  private static NamedUserType buildMultiConstructorType() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("MultiCtorType");
    type.superType.setValue(OBJECT_TYPE);

    NamedUserConstructor ctor1 = new NamedUserConstructor();
    ConstructorBlockStatement body1 = new ConstructorBlockStatement();
    body1.constructorInvocationStatement.setValue(new SuperConstructorInvocationStatement());
    ctor1.body.setValue(body1);

    NamedUserConstructor ctor2 = new NamedUserConstructor();
    ConstructorBlockStatement body2 = new ConstructorBlockStatement();
    body2.constructorInvocationStatement.setValue(new SuperConstructorInvocationStatement());
    ctor2.body.setValue(body2);

    type.constructors.add(ctor1);
    type.constructors.add(ctor2);
    return type;
  }

  // ── getResourceFieldOrType branch tests ────────────────────────────

  @Test
  public void getResourceFieldOrType_zeroCtors_returnsNull() {
    NamedUserType type = buildNoConstructorType();
    assertNull("Zero constructors should yield null",
        ResourceTypeUtilities.getResourceFieldOrType(type));
  }

  @Test
  public void getResourceFieldOrType_multipleCtors_returnsNull() {
    NamedUserType type = buildMultiConstructorType();
    assertNull("Multiple constructors should yield null",
        ResourceTypeUtilities.getResourceFieldOrType(type));
  }

  @Test
  public void getResourceFieldOrType_zeroReqParams_zeroSuperArgs_returnsNull() {
    NamedUserType type = buildZeroArgSuperType();
    assertNull("Zero super args should yield null",
        ResourceTypeUtilities.getResourceFieldOrType(type));
  }

  @Test
  public void getResourceFieldOrType_zeroReqParams_oneFieldAccessArg_returnsField() {
    NamedUserType type = buildOneFieldAccessArgType();
    Declaration result = ResourceTypeUtilities.getResourceFieldOrType(type);
    assertNotNull("One FieldAccess arg should yield a field", result);
    assertTrue("Result should be a JavaField", result instanceof JavaField);
    JavaField field = (JavaField) result;
    assertEquals("NEW", field.getName());
  }

  @Test
  public void getResourceFieldOrType_zeroReqParams_oneNonFieldAccessArg_returnsNull() {
    NamedUserType type = buildOneNonFieldAccessArgType();
    assertNull("One non-FieldAccess arg should yield null",
        ResourceTypeUtilities.getResourceFieldOrType(type));
  }

  @Test
  public void getResourceFieldOrType_zeroReqParams_multiSuperArgs_returnsNull() {
    NamedUserType type = buildMultiArgSuperType();
    assertNull("Multiple super args should yield null",
        ResourceTypeUtilities.getResourceFieldOrType(type));
  }

  @Test
  public void getResourceFieldOrType_oneReqParam_returnsParameterValueType() {
    NamedUserType type = buildOneRequiredParamType();
    Declaration result = ResourceTypeUtilities.getResourceFieldOrType(type);
    assertNotNull("One required param should yield param type", result);
    assertTrue("Result should be a JavaType", result instanceof JavaType);
    assertEquals(JavaType.getInstance(String.class), result);
  }

  @Test
  public void getResourceFieldOrType_multiReqParams_returnsNull() {
    NamedUserType type = buildMultiRequiredParamType();
    assertNull("Multiple required params should yield null",
        ResourceTypeUtilities.getResourceFieldOrType(type));
  }

  // ── getResourceType dispatch tests ─────────────────────────────────

  @Test
  public void getResourceType_whenFieldOrTypeReturnsNull_returnsNull() {
    NamedUserType type = buildNoConstructorType();
    assertNull(ResourceTypeUtilities.getResourceType(type));
  }

  @Test
  public void getResourceType_whenFieldOrTypeReturnsJavaType_returnsThatType() {
    NamedUserType type = buildOneRequiredParamType();
    JavaType result = ResourceTypeUtilities.getResourceType(type);
    assertNotNull(result);
    assertEquals(JavaType.getInstance(String.class), result);
  }

  @Test
  public void getResourceType_whenFieldOrTypeReturnsJavaField_returnsDeclaringType() {
    NamedUserType type = buildOneFieldAccessArgType();
    JavaType result = ResourceTypeUtilities.getResourceType(type);
    assertNotNull(result);
    // Thread.State.NEW's declaring type is Thread.State
    assertEquals(JavaType.getInstance(Thread.State.class), result);
  }

  // ── private constructor guard ──────────────────────────────────────

  @Test(expected = InvocationTargetException.class)
  public void constructor_isPrivate_throwsAssertionError() throws Exception {
    Constructor<ResourceTypeUtilities> ctor =
        ResourceTypeUtilities.class.getDeclaredConstructor();
    ctor.setAccessible(true);
    ctor.newInstance();
  }

  @Test
  public void constructor_cannotBeInstantiatedDirectly() {
    Constructor<?>[] ctors = ResourceTypeUtilities.class.getDeclaredConstructors();
    assertEquals("Should have exactly one constructor", 1, ctors.length);
    assertFalse("Constructor should not be accessible",
        ctors[0].canAccess(null));
  }
}
