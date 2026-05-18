package org.lgna.project.ast;

import org.junit.Test;
import org.lgna.project.annotations.ClassTemplate;
import org.lgna.project.annotations.ConstructorTemplate;
import org.lgna.project.annotations.FieldTemplate;
import org.lgna.project.annotations.MethodTemplate;
import org.lgna.project.annotations.Visibility;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.function.BinaryOperator;

import static org.junit.Assert.*;

public class JavaMemberDeepTest {
  private static final BinaryOperator<String> FIRST_LOCALIZER = (a, b) -> a;

  @SuppressWarnings("unused")
  static class Fixture {
    public int value;
    public static final String NAME = "test";
    private volatile transient int secret;

    public Fixture() {
    }

    public Fixture(int value) {
      this.value = value;
    }

    public void doSomething() {
    }

    public static int compute(int x) {
      return x;
    }

    public final synchronized void syncMethod() {
    }

    @Override
    public String toString() {
      return "fixture";
    }
  }

  @SuppressWarnings("unused")
  static class ProxyFixture {
    public int proxyField;

    public ProxyFixture() {
    }

    public void proxyMethod() {
    }
  }

  @SuppressWarnings("unused")
  static class FieldAccessFixture {
    public int publicField;
    protected int protectedField;
    int packageField;
    private int privateField;
  }

  @SuppressWarnings("unused")
  static class FieldVisibilityFixture {
    @FieldTemplate(visibility = Visibility.COMPLETELY_HIDDEN)
    public int annotatedField;
    public int plainField;
  }

  @SuppressWarnings("unused")
  static class MethodDefaultChainFixture {
    public void isolatedMethod(int value) {
    }
  }

  @SuppressWarnings("unused")
  static abstract class MethodAccessFixture {
    public void publicMethod() {
    }

    protected void protectedMethod() {
    }

    void packageMethod() {
    }

    private void privateMethod() {
    }

    @Deprecated
    public void deprecatedMethod() {
    }

    @MethodTemplate(visibility = Visibility.TUCKED_AWAY)
    public void templateMethod() {
    }

    public static int staticMethod(int value) {
      return value;
    }

    public final synchronized void finalSyncMethod() {
    }

    public void varArgMethod(String label, int... values) {
    }

    public void chainMethod(int value) {
    }

    public void chainMethod(int value, String label) {
    }

    public void keyedMethod(KeywordValue... values) {
    }

    public strictfp double strictMethod(double value) {
      return value;
    }

    public native void nativeMethod();

    protected abstract void abstractMethod();
  }

  @SuppressWarnings("unused")
  static class ConstructorDefaultChainFixture {
    public ConstructorDefaultChainFixture() {
    }
  }

  @SuppressWarnings("unused")
  static class ConstructorFixture {
    public ConstructorFixture() {
    }

    public ConstructorFixture(int value) {
    }

    public ConstructorFixture(int value, String label) {
    }

    protected ConstructorFixture(long value) {
    }

    ConstructorFixture(String value) {
    }

    private ConstructorFixture(double value) {
    }

    @ConstructorTemplate(visibility = Visibility.TUCKED_AWAY)
    public ConstructorFixture(boolean value) {
    }

    public ConstructorFixture(String label, int... values) {
    }

    public ConstructorFixture(KeywordValue... values) {
    }
  }

  static final class KeywordFactory {
  }

  @ClassTemplate(keywordFactoryCls = KeywordFactory.class)
  static final class KeywordValue {
  }

  private static Field field(Class<?> declaringClass, String name) throws Exception {
    return declaringClass.getDeclaredField(name);
  }

  private static Method method(Class<?> declaringClass, String name, Class<?>... parameterTypes) throws Exception {
    return declaringClass.getDeclaredMethod(name, parameterTypes);
  }

  private static Constructor<?> constructor(Class<?> declaringClass, Class<?>... parameterTypes) throws Exception {
    return declaringClass.getDeclaredConstructor(parameterTypes);
  }

  private static JavaField javaField(Class<?> declaringClass, String name) throws Exception {
    return JavaField.getInstance(field(declaringClass, name));
  }

  private static JavaMethod javaMethod(Class<?> declaringClass, String name, Class<?>... parameterTypes) throws Exception {
    return JavaMethod.getInstance(method(declaringClass, name, parameterTypes));
  }

  private static JavaConstructor javaConstructor(Class<?> declaringClass, Class<?>... parameterTypes) throws Exception {
    return JavaConstructor.getInstance(constructor(declaringClass, parameterTypes));
  }

  private static JavaField invalidJavaField() {
    return JavaField.getInstance(new FieldReflectionProxy(new ClassReflectionProxy(Fixture.class), "missingField"));
  }

  private static JavaMethod invalidJavaMethod() {
    return JavaMethod.getInstance(new MethodReflectionProxy(new ClassReflectionProxy(Fixture.class), "missingMethod", new ClassReflectionProxy[0], false));
  }

  private static JavaConstructor invalidJavaConstructor() {
    return JavaConstructor.getInstance(new ConstructorReflectionProxy(
        new ClassReflectionProxy(ConstructorFixture.class),
        new ClassReflectionProxy[] {new ClassReflectionProxy(Float.TYPE)},
        false));
  }

  @Test
  public void javaFieldGetInstanceReturnsNullForNullProxy() {
    assertNull(JavaField.getInstance((FieldReflectionProxy) null));
  }

  @Test
  public void javaFieldGetInstanceFromFieldCachesSingleton() throws Exception {
    Field reflectedField = field(Fixture.class, "value");

    JavaField first = JavaField.getInstance(reflectedField);
    JavaField second = JavaField.getInstance(reflectedField);

    assertSame(first, second);
    assertSame(first, JavaField.getInstance(new FieldReflectionProxy(reflectedField)));
  }

  @Test
  public void javaFieldGetInstanceFromClassAndNameUsesReflection() throws Exception {
    assertSame(JavaField.getInstance(field(Fixture.class, "value")), JavaField.getInstance(Fixture.class, "value"));
  }

  @Test
  public void javaFieldNamePropertyIsAlwaysNull() throws Exception {
    assertNull(javaField(Fixture.class, "value").getNamePropertyIfItExists());
  }

  @Test
  public void javaFieldExposesItsReflectionProxy() throws Exception {
    FieldReflectionProxy proxy = new FieldReflectionProxy(field(ProxyFixture.class, "proxyField"));

    assertSame(proxy, JavaField.getInstance(proxy).getFieldReflectionProxy());
  }

  @Test
  public void javaFieldDeclaringTypeUsesJavaType() throws Exception {
    assertSame(JavaType.getInstance(Fixture.class), javaField(Fixture.class, "value").getDeclaringType());
  }

  @Test
  public void javaFieldVisibilityReadsTemplateAnnotation() throws Exception {
    assertEquals(Visibility.COMPLETELY_HIDDEN, javaField(FieldVisibilityFixture.class, "annotatedField").getVisibility());
  }

  @Test
  public void javaFieldVisibilityIsNullWithoutTemplate() throws Exception {
    assertNull(javaField(FieldVisibilityFixture.class, "plainField").getVisibility());
  }

  @Test
  public void javaFieldVisibilityIsNullForInvalidProxy() {
    assertNull(invalidJavaField().getVisibility());
  }

  @Test
  public void javaFieldNameDelegatesToProxy() throws Exception {
    assertEquals("value", javaField(Fixture.class, "value").getName());
  }

  @Test
  public void javaFieldValidityTracksReification() throws Exception {
    assertTrue(javaField(Fixture.class, "value").isValid());
    assertFalse(invalidJavaField().isValid());
  }

  @Test
  public void javaFieldValueTypeReflectsFieldTypeAndInvalidFallback() throws Exception {
    assertSame(JavaType.INTEGER_PRIMITIVE_TYPE, javaField(Fixture.class, "value").getValueType());
    assertSame(JavaType.OBJECT_TYPE, invalidJavaField().getValueType());
  }

  @Test
  public void javaFieldAccessLevelReflectsMemberModifiers() throws Exception {
    assertEquals(AccessLevel.PUBLIC, javaField(FieldAccessFixture.class, "publicField").getAccessLevel());
    assertEquals(AccessLevel.PROTECTED, javaField(FieldAccessFixture.class, "protectedField").getAccessLevel());
    assertEquals(AccessLevel.PACKAGE, javaField(FieldAccessFixture.class, "packageField").getAccessLevel());
    assertEquals(AccessLevel.PRIVATE, javaField(FieldAccessFixture.class, "privateField").getAccessLevel());
    assertEquals(AccessLevel.PRIVATE, invalidJavaField().getAccessLevel());
  }

  @Test
  public void javaFieldModifierQueriesReflectUnderlyingField() throws Exception {
    assertFalse(javaField(Fixture.class, "value").isStatic());
    assertTrue(javaField(Fixture.class, "NAME").isStatic());
    assertTrue(javaField(Fixture.class, "NAME").isFinal());
    assertTrue(javaField(Fixture.class, "secret").isVolatile());
    assertTrue(javaField(Fixture.class, "secret").isTransient());
    assertFalse(invalidJavaField().isStatic());
    assertFalse(invalidJavaField().isFinal());
    assertFalse(invalidJavaField().isVolatile());
    assertFalse(invalidJavaField().isTransient());
  }

  @Test
  public void javaFieldEquivalenceTracksUnderlyingField() throws Exception {
    JavaField first = javaField(Fixture.class, "value");
    JavaField same = JavaField.getInstance(field(Fixture.class, "value"));
    JavaField different = javaField(Fixture.class, "NAME");

    assertTrue(first.isEquivalentTo(same));
    assertFalse(first.isEquivalentTo(different));
    assertFalse(first.isEquivalentTo("not a field"));
  }

  @Test
  public void javaFieldIsNeverUserAuthored() throws Exception {
    assertFalse(javaField(Fixture.class, "value").isUserAuthored());
  }

  @Test
  public void javaFieldFormatNameUsesInstanceAndStaticPaths() throws Exception {
    assertEquals("value", javaField(Fixture.class, "value").formatName(FIRST_LOCALIZER));
    assertEquals("NAME", javaField(Fixture.class, "NAME").formatName(FIRST_LOCALIZER));
  }

  @Test
  public void javaMethodGetInstanceReturnsNullForNullProxy() {
    assertNull(JavaMethod.getInstance((MethodReflectionProxy) null));
  }

  @Test
  public void javaMethodGetInstanceFromMethodCachesSingleton() throws Exception {
    Method reflectedMethod = method(Fixture.class, "compute", Integer.TYPE);

    JavaMethod first = JavaMethod.getInstance(reflectedMethod);
    JavaMethod second = JavaMethod.getInstance(reflectedMethod);

    assertSame(first, second);
    assertSame(first, JavaMethod.getInstance(new MethodReflectionProxy(reflectedMethod)));
  }

  @Test
  public void javaMethodGetInstanceFromClassSignatureUsesReflection() throws Exception {
    assertSame(JavaMethod.getInstance(method(Fixture.class, "compute", Integer.TYPE)), JavaMethod.getInstance(Fixture.class, "compute", Integer.TYPE));
  }

  @Test
  public void javaMethodExposesItsReflectionProxy() throws Exception {
    MethodReflectionProxy proxy = new MethodReflectionProxy(method(ProxyFixture.class, "proxyMethod"));

    assertSame(proxy, JavaMethod.getInstance(proxy).getMethodReflectionProxy());
  }

  @Test
  public void javaMethodAnnotationPresenceReflectsUnderlyingMethod() throws Exception {
    assertTrue(javaMethod(MethodAccessFixture.class, "deprecatedMethod").isAnnotationPresent(Deprecated.class));
    assertFalse(javaMethod(MethodAccessFixture.class, "publicMethod").isAnnotationPresent(Deprecated.class));
  }

  @Test
  public void javaMethodFormatNameUsesLocalizer() throws Exception {
    assertEquals("compute", javaMethod(Fixture.class, "compute", Integer.TYPE).formatName(FIRST_LOCALIZER));
  }

  @Test
  public void javaMethodNameAndPropertyBehaveAsExpected() throws Exception {
    JavaMethod javaMethod = javaMethod(Fixture.class, "doSomething");

    assertEquals("doSomething", javaMethod.getName());
    assertNull(javaMethod.getNamePropertyIfItExists());
  }

  @Test
  public void javaMethodReturnTypeHandlesValidAndInvalidProxies() throws Exception {
    assertSame(JavaType.INTEGER_PRIMITIVE_TYPE, javaMethod(Fixture.class, "compute", Integer.TYPE).getReturnType());
    assertNull(invalidJavaMethod().getReturnType());
  }

  @Test
  public void javaMethodRequiredParametersExposeFixedParameters() throws Exception {
    List<JavaMethodParameter> parameters = javaMethod(Fixture.class, "compute", Integer.TYPE).getRequiredParameters();

    assertEquals(1, parameters.size());
    assertSame(JavaType.INTEGER_PRIMITIVE_TYPE, parameters.get(0).getValueType());
  }

  @Test
  public void javaMethodVarArgsSplitRequiredAndVariableParameters() throws Exception {
    JavaMethod javaMethod = javaMethod(MethodAccessFixture.class, "varArgMethod", String.class, int[].class);

    assertEquals(1, javaMethod.getRequiredParameters().size());
    assertSame(JavaType.STRING_TYPE, javaMethod.getRequiredParameters().get(0).getValueType());
    assertNull(javaMethod.getKeyedParameter());
    assertNotNull(javaMethod.getVariableLengthParameter());
    assertSame(JavaType.getInstance(int[].class), javaMethod.getVariableLengthParameter().getValueType());
  }

  @Test
  public void javaMethodNonVarArgsHaveNoSpecialParameters() throws Exception {
    JavaMethod javaMethod = javaMethod(Fixture.class, "doSomething");

    assertNull(javaMethod.getKeyedParameter());
    assertNull(javaMethod.getVariableLengthParameter());
  }

  @Test
  public void javaMethodKeyedParametersUseKeywordFactoryMetadata() throws Exception {
    JavaMethod javaMethod = javaMethod(MethodAccessFixture.class, "keyedMethod", KeywordValue[].class);

    assertNotNull(javaMethod.getKeyedParameter());
    assertSame(JavaType.getInstance(KeywordValue[].class), javaMethod.getKeyedParameter().getValueType());
    assertNull(javaMethod.getVariableLengthParameter());
  }

  @Test
  public void javaMethodDeclaringTypeUsesJavaType() throws Exception {
    assertSame(JavaType.getInstance(Fixture.class), javaMethod(Fixture.class, "doSomething").getDeclaringType());
  }

  @Test
  public void javaMethodVisibilityReadsTemplateAnnotation() throws Exception {
    assertEquals(Visibility.TUCKED_AWAY, javaMethod(MethodAccessFixture.class, "templateMethod").getVisibility());
  }

  @Test
  public void javaMethodVisibilityIsNullWithoutTemplate() throws Exception {
    assertNull(javaMethod(MethodAccessFixture.class, "publicMethod").getVisibility());
  }

  @Test
  public void javaMethodChainCanBeSetAndCleared() throws Exception {
    // Use a unique method that won't be touched by other tests
    JavaMethod javaMethod = javaMethod(MethodDefaultChainFixture.class, "isolatedMethod", Integer.TYPE);
    JavaMethod other = javaMethod(MethodAccessFixture.class, "publicMethod");

    // Set chain
    javaMethod.setNextLongerInChain(other);
    assertSame(other, javaMethod.getNextLongerInChain());

    // Clear chain
    javaMethod.setNextLongerInChain(null);
    assertNull(javaMethod.getNextLongerInChain());
  }

  @Test
  public void javaMethodChainSettersAndShortestChainChecksWork() throws Exception {
    JavaMethod shorter = javaMethod(MethodAccessFixture.class, "chainMethod", Integer.TYPE);
    JavaMethod longer = javaMethod(MethodAccessFixture.class, "chainMethod", Integer.TYPE, String.class);

    shorter.setNextLongerInChain(longer);
    longer.setNextShorterInChain(shorter);

    assertSame(longer, shorter.getNextLongerInChain());
    assertSame(shorter, longer.getNextShorterInChain());
    assertTrue(longer.isParameterInShortestChainedMethod(longer.getRequiredParameters().get(0)));
    assertFalse(longer.isParameterInShortestChainedMethod(longer.getRequiredParameters().get(1)));
  }

  @Test
  public void javaMethodSignatureIsAlwaysLocked() throws Exception {
    assertTrue(javaMethod(Fixture.class, "doSomething").isSignatureLocked());
  }

  @Test
  public void javaMethodAccessLevelReflectsMemberModifiers() throws Exception {
    assertEquals(AccessLevel.PUBLIC, javaMethod(MethodAccessFixture.class, "publicMethod").getAccessLevel());
    assertEquals(AccessLevel.PROTECTED, javaMethod(MethodAccessFixture.class, "protectedMethod").getAccessLevel());
    assertEquals(AccessLevel.PACKAGE, javaMethod(MethodAccessFixture.class, "packageMethod").getAccessLevel());
    assertEquals(AccessLevel.PRIVATE, javaMethod(MethodAccessFixture.class, "privateMethod").getAccessLevel());
  }

  @Test
  public void javaMethodModifierQueriesReflectUnderlyingMethod() throws Exception {
    assertTrue(javaMethod(MethodAccessFixture.class, "staticMethod", Integer.TYPE).isStatic());
    assertTrue(javaMethod(MethodAccessFixture.class, "abstractMethod").isAbstract());
    assertTrue(javaMethod(MethodAccessFixture.class, "finalSyncMethod").isFinal());
    assertTrue(javaMethod(MethodAccessFixture.class, "nativeMethod").isNative());
    assertTrue(javaMethod(MethodAccessFixture.class, "finalSyncMethod").isSynchronized());
    Method strictMethod = method(MethodAccessFixture.class, "strictMethod", Double.TYPE);
    assertEquals(Modifier.isStrict(strictMethod.getModifiers()), javaMethod(MethodAccessFixture.class, "strictMethod", Double.TYPE).isStrictFloatingPoint());
  }

  @Test
  public void javaMethodEquivalenceTracksUnderlyingMethod() throws Exception {
    JavaMethod first = javaMethod(Fixture.class, "doSomething");
    JavaMethod same = JavaMethod.getInstance(method(Fixture.class, "doSomething"));
    JavaMethod different = javaMethod(Fixture.class, "compute", Integer.TYPE);

    assertTrue(first.isEquivalentTo(same));
    assertFalse(first.isEquivalentTo(different));
    assertFalse(first.isEquivalentTo("not a method"));
  }

  @Test
  public void javaMethodIsNeverUserAuthored() throws Exception {
    assertFalse(javaMethod(Fixture.class, "doSomething").isUserAuthored());
  }

  @Test
  public void javaConstructorGetInstanceReturnsNullForNullProxy() {
    assertNull(JavaConstructor.getInstance((ConstructorReflectionProxy) null));
  }

  @Test
  public void javaConstructorGetInstanceFromConstructorCachesSingleton() throws Exception {
    Constructor<?> reflectedConstructor = constructor(Fixture.class, Integer.TYPE);

    JavaConstructor first = JavaConstructor.getInstance(reflectedConstructor);
    JavaConstructor second = JavaConstructor.getInstance(reflectedConstructor);

    assertSame(first, second);
    assertSame(first, JavaConstructor.getInstance(new ConstructorReflectionProxy(reflectedConstructor)));
  }

  @Test
  public void javaConstructorGetInstanceFromClassSignatureUsesReflection() throws Exception {
    assertSame(JavaConstructor.getInstance(constructor(Fixture.class, Integer.TYPE)), JavaConstructor.getInstance(Fixture.class, Integer.TYPE));
  }

  @Test
  public void javaConstructorExposesItsReflectionProxy() throws Exception {
    ConstructorReflectionProxy proxy = new ConstructorReflectionProxy(constructor(ProxyFixture.class));

    assertSame(proxy, JavaConstructor.getInstance(proxy).getConstructorReflectionProxy());
  }

  @Test
  public void javaConstructorDeclaringTypeUsesJavaType() throws Exception {
    assertSame(JavaType.getInstance(Fixture.class), javaConstructor(Fixture.class).getDeclaringType());
  }

  @Test
  public void javaConstructorRequiredParametersExposeFixedParameters() throws Exception {
    List<JavaConstructorParameter> parameters = javaConstructor(ConstructorFixture.class, Integer.TYPE).getRequiredParameters();

    assertEquals(1, parameters.size());
    assertSame(JavaType.INTEGER_PRIMITIVE_TYPE, parameters.get(0).getValueType());
  }

  @Test
  public void javaConstructorVarArgsSplitRequiredAndVariableParameters() throws Exception {
    JavaConstructor javaConstructor = javaConstructor(ConstructorFixture.class, String.class, int[].class);

    assertEquals(1, javaConstructor.getRequiredParameters().size());
    assertSame(JavaType.STRING_TYPE, javaConstructor.getRequiredParameters().get(0).getValueType());
    assertNotNull(javaConstructor.getVariableLengthParameter());
    assertSame(JavaType.getInstance(int[].class), javaConstructor.getVariableLengthParameter().getValueType());
    assertNull(javaConstructor.getKeyedParameter());
  }

  @Test
  public void javaConstructorKeyedParametersUseKeywordFactoryMetadata() throws Exception {
    JavaConstructor javaConstructor = javaConstructor(ConstructorFixture.class, KeywordValue[].class);

    assertNotNull(javaConstructor.getKeyedParameter());
    assertSame(JavaType.getInstance(KeywordValue[].class), javaConstructor.getKeyedParameter().getValueType());
    assertNull(javaConstructor.getVariableLengthParameter());
  }

  @Test
  public void javaConstructorVisibilityHandlesPresentAbsentAndInvalidProxies() throws Exception {
    assertEquals(Visibility.TUCKED_AWAY, javaConstructor(ConstructorFixture.class, Boolean.TYPE).getVisibility());
    assertNull(javaConstructor(ConstructorFixture.class).getVisibility());
    assertNull(invalidJavaConstructor().getVisibility());
  }

  @Test
  public void javaConstructorChainSettersAndShortestChainChecksWork() throws Exception {
    JavaConstructor shorter = javaConstructor(ConstructorFixture.class, Integer.TYPE);
    JavaConstructor longer = javaConstructor(ConstructorFixture.class, Integer.TYPE, String.class);

    assertNull(shorter.getNextLongerInChain());
    assertNull(longer.getNextShorterInChain());

    shorter.setNextLongerInChain(longer);
    longer.setNextShorterInChain(shorter);

    assertSame(longer, shorter.getNextLongerInChain());
    assertSame(shorter, longer.getNextShorterInChain());
    assertTrue(longer.isParameterInShortestChainedConstructor(longer.getRequiredParameters().get(0)));
    assertFalse(longer.isParameterInShortestChainedConstructor(longer.getRequiredParameters().get(1)));
  }

  @Test
  public void javaConstructorChainDefaultsToNull() throws Exception {
    JavaConstructor javaConstructor = javaConstructor(ConstructorDefaultChainFixture.class);

    assertNull(javaConstructor.getNextLongerInChain());
    assertNull(javaConstructor.getNextShorterInChain());
  }

  @Test
  public void javaConstructorSignatureIsAlwaysLocked() throws Exception {
    assertTrue(javaConstructor(Fixture.class).isSignatureLocked());
  }

  @Test
  public void javaConstructorAccessLevelReflectsMemberModifiers() throws Exception {
    assertEquals(AccessLevel.PUBLIC, javaConstructor(ConstructorFixture.class).getAccessLevel());
    assertEquals(AccessLevel.PROTECTED, javaConstructor(ConstructorFixture.class, Long.TYPE).getAccessLevel());
    assertEquals(AccessLevel.PACKAGE, javaConstructor(ConstructorFixture.class, String.class).getAccessLevel());
    assertEquals(AccessLevel.PRIVATE, javaConstructor(ConstructorFixture.class, Double.TYPE).getAccessLevel());
    assertNull(invalidJavaConstructor().getAccessLevel());
  }

  @Test
  public void javaConstructorEquivalenceTracksUnderlyingConstructor() throws Exception {
    JavaConstructor first = javaConstructor(Fixture.class);
    JavaConstructor same = JavaConstructor.getInstance(constructor(Fixture.class));
    JavaConstructor different = javaConstructor(Fixture.class, Integer.TYPE);

    assertTrue(first.isEquivalentTo(same));
    assertFalse(first.isEquivalentTo(different));
    assertFalse(first.isEquivalentTo("not a constructor"));
  }

  @Test
  public void javaConstructorIsNeverUserAuthored() throws Exception {
    assertFalse(javaConstructor(Fixture.class).isUserAuthored());
  }
}
