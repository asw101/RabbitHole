package org.lgna.project.ast;

import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class ReflectionProxyDeepTest {

  @Retention(RetentionPolicy.RUNTIME)
  private @interface Marker {
  }

  @SuppressWarnings("unused")
  static class Fixture {
    public int value;
    public static String name = "test";

    public void doSomething(int x) {
    }

    public static void varArgsMethod(String format, Object... args) {
    }

    public static void promotedVarArgs(String prefix, Integer fixed, Object... args) {
    }

    public static void trimmedVarArgs(String prefix, Object... args) {
    }

    public void annotated(@Marker int x, @Marker String y) {
    }
  }

  static class FixtureNested {
  }

  private static Method declaredMethod(Class<?> cls, String name, Class<?>... parameterTypes) {
    try {
      return cls.getDeclaredMethod(name, parameterTypes);
    } catch (NoSuchMethodException e) {
      throw new AssertionError(e);
    }
  }

  private static Field publicField(Class<?> cls, String name) {
    try {
      return cls.getField(name);
    } catch (NoSuchFieldException e) {
      throw new AssertionError(e);
    }
  }

  private static ClassReflectionProxy[] proxies(Class<?>... clses) {
    return ClassReflectionProxy.create(clses);
  }

  @Test
  public void classCreateBuildsProxyArrayForMultipleClasses() {
    ClassReflectionProxy[] created = ClassReflectionProxy.create(new Class<?>[]{String.class, Integer.class, int[].class});

    assertEquals(3, created.length);
    assertEquals(String.class.getName(), created[0].getName());
    assertEquals(Integer.class.getName(), created[1].getName());
    assertEquals(int[].class.getName(), created[2].getName());
  }

  @Test
  public void classGetReificationsReturnsOriginalClasses() {
    ClassReflectionProxy[] created = proxies(String.class, Integer.class, int[].class);

    assertArrayEquals(new Class<?>[]{String.class, Integer.class, int[].class}, ClassReflectionProxy.getReifications(created));
  }

  @Test
  public void classConstructorFromStringPreservesName() {
    ClassReflectionProxy proxy = new ClassReflectionProxy("com.example.MissingType");

    assertEquals("com.example.MissingType", proxy.getName());
  }

  @Test
  public void classConstructorFromClassStoresReification() {
    ClassReflectionProxy proxy = new ClassReflectionProxy(String.class);

    assertSame(String.class, proxy.getReification());
  }

  @Test
  public void classGetSimpleNameUsesReifiedClassWhenAvailable() {
    assertEquals("String", new ClassReflectionProxy(String.class).getSimpleName());
  }

  @Test
  public void classGetSimpleNameFallsBackToStringName() {
    assertEquals("MissingType", new ClassReflectionProxy("com.example.MissingType").getSimpleName());
  }

  @Test
  public void classIsArrayReturnsTrueForReifiedArray() {
    assertTrue(new ClassReflectionProxy(String[].class).isArray());
  }

  @Test
  public void classIsArrayReturnsTrueForStringOnlyArray() {
    assertTrue(new ClassReflectionProxy("[Lcom.example.MissingType;").isArray());
  }

  @Test
  public void classIsArrayReturnsFalseForStringOnlyNonArray() {
    assertFalse(new ClassReflectionProxy("com.example.MissingType").isArray());
  }

  @Test
  public void classGetComponentReturnsReifiedComponentType() {
    ClassReflectionProxy component = new ClassReflectionProxy(String[].class).getComponentClassReflectionProxy();

    assertNotNull(component);
    assertSame(String.class, component.getReification());
  }

  @Test
  public void classGetComponentReturnsStringOnlyComponentForObjectArrayDescriptor() {
    ClassReflectionProxy component = new ClassReflectionProxy("[Lcom.example.MissingType;").getComponentClassReflectionProxy();

    assertNotNull(component);
    assertEquals("com.example.MissingType", component.getName());
  }

  @Test
  public void classGetComponentReturnsStringOnlyComponentForMultiDimensionalDescriptor() {
    ClassReflectionProxy component = new ClassReflectionProxy("[[Lcom.example.MissingType;").getComponentClassReflectionProxy();

    assertNotNull(component);
    assertEquals("[Lcom.example.MissingType;", component.getName());
  }

  @Test
  public void classGetComponentReturnsNullForNonArray() {
    assertNull(new ClassReflectionProxy("com.example.MissingType").getComponentClassReflectionProxy());
  }

  @Test
  public void classGetDeclaringClassReturnsNestedDeclaringClassWhenReified() {
    ClassReflectionProxy declaringClassReflectionProxy = new ClassReflectionProxy(FixtureNested.class).getDeclaringClassReflectionProxy();

    assertNotNull(declaringClassReflectionProxy);
    assertSame(ReflectionProxyDeepTest.class, declaringClassReflectionProxy.getReification());
  }

  @Test
  public void classGetDeclaringClassReturnsNullForTopLevelClass() {
    assertNull(new ClassReflectionProxy(String.class).getDeclaringClassReflectionProxy());
  }

  @Test
  public void classGetDeclaringClassFallsBackToStringName() {
    ClassReflectionProxy declaring = new ClassReflectionProxy("com.example.Outer$Inner").getDeclaringClassReflectionProxy();

    assertNotNull(declaring);
    assertEquals("com.example.Outer", declaring.getName());
  }

  @Test
  public void classGetDeclaringClassReturnsNullForStringWithoutDollar() {
    assertNull(new ClassReflectionProxy("com.example.MissingType").getDeclaringClassReflectionProxy());
  }

  @Test
  public void classGetPackageReturnsReifiedPackage() {
    PackageReflectionProxy proxy = new ClassReflectionProxy(String.class).getPackageReflectionProxy();

    assertNotNull(proxy);
    assertEquals("java.lang", proxy.getName());
    assertEquals(String.class.getPackage(), proxy.getReification());
  }

  @Test
  public void classGetPackageFallsBackToStringName() {
    PackageReflectionProxy proxy = new ClassReflectionProxy("com.example.MissingType").getPackageReflectionProxy();

    assertNotNull(proxy);
    assertEquals("com.example", proxy.getName());
  }

  @Test
  public void classReifyReturnsClassForValidName() {
    assertSame(String.class, new ClassReflectionProxy("java.lang.String").getReification());
  }

  @Test
  public void classReifyReturnsNullForInvalidName() {
    assertNull(new ClassReflectionProxy("com.example.DoesNotExist").getReification());
  }

  @Test
  public void classToStringIncludesProxyName() {
    assertEquals("ClassReflectionProxy[name=java.lang.String]", new ClassReflectionProxy(String.class).toString());
  }

  @Test
  public void classEqualsAndHashCodeMatchForReifiedProxies() {
    ClassReflectionProxy left = new ClassReflectionProxy(String.class);
    ClassReflectionProxy right = new ClassReflectionProxy("java.lang.String");

    assertEquals(left, right);
    assertEquals(left.hashCode(), right.hashCode());
  }

  @Test
  public void classEqualsAndHashCodeMatchForNonReifiableProxies() {
    ClassReflectionProxy left = new ClassReflectionProxy("com.example.DoesNotExist");
    ClassReflectionProxy right = new ClassReflectionProxy("com.example.DoesNotExist");

    assertEquals(left, right);
    assertEquals(left.hashCode(), right.hashCode());
  }

  @Test
  public void classEqualsReturnsFalseForDifferentNames() {
    assertNotEquals(new ClassReflectionProxy("com.example.One"), new ClassReflectionProxy("com.example.Two"));
  }

  @Test
  public void classEqualsReturnsFalseForNonReflectionProxyObject() {
    assertFalse(new ClassReflectionProxy(String.class).equals("java.lang.String"));
  }

  @Test
  public void classEqualsReturnsTrueForSameInstance() {
    ClassReflectionProxy proxy = new ClassReflectionProxy(String.class);

    assertTrue(proxy.equals(proxy));
  }

  @Test
  public void methodConstructorFromMethodCopiesMetadata() {
    Method method = declaredMethod(Fixture.class, "varArgsMethod", String.class, Object[].class);
    MethodReflectionProxy proxy = new MethodReflectionProxy(method);

    assertEquals("varArgsMethod", proxy.getName());
    assertTrue(proxy.isVarArgs());
    assertEquals(Fixture.class.getName(), proxy.getDeclaringClassReflectionProxy().getName());
    assertEquals(2, proxy.getParameterClassReflectionProxies().length);
  }

  @Test
  public void methodConstructorFromProxiesStoresConfiguredValues() {
    MethodReflectionProxy proxy = new MethodReflectionProxy(new ClassReflectionProxy(Fixture.class), "doSomething", proxies(int.class), false);

    assertEquals("doSomething", proxy.getName());
    assertFalse(proxy.isVarArgs());
    assertEquals(1, proxy.getParameterClassReflectionProxies().length);
  }

  @Test
  public void methodReifyReturnsDeclaredMethodWhenPresent() {
    Method expected = declaredMethod(Fixture.class, "doSomething", int.class);
    MethodReflectionProxy proxy = new MethodReflectionProxy(new ClassReflectionProxy(Fixture.class), "doSomething", proxies(int.class), false);

    assertEquals(expected, proxy.getReification());
  }

  @Test
  public void methodReifyReturnsNullForMissingMethod() {
    MethodReflectionProxy proxy = new MethodReflectionProxy(new ClassReflectionProxy(Fixture.class), "missing", proxies(int.class), false);

    assertNull(proxy.getReification());
  }

  @Test
  public void methodReifyReturnsNullWhenDeclaringClassCannotBeReified() {
    MethodReflectionProxy proxy = new MethodReflectionProxy(new ClassReflectionProxy("com.example.MissingType"), "missing", proxies(int.class), false);

    assertNull(proxy.getReification());
  }

  @Test
  public void methodEqualsAndHashCodeMatchForReifiedProxies() {
    Method method = declaredMethod(Fixture.class, "doSomething", int.class);
    MethodReflectionProxy left = new MethodReflectionProxy(method);
    MethodReflectionProxy right = new MethodReflectionProxy(new ClassReflectionProxy(Fixture.class), "doSomething", proxies(int.class), false);

    assertEquals(left, right);
    assertEquals(left.hashCode(), right.hashCode());
  }

  @Test
  public void methodEqualsAndHashCodeMatchForNonReifiableProxies() {
    MethodReflectionProxy left = new MethodReflectionProxy(new ClassReflectionProxy("com.example.MissingType"), "work", proxies(String.class), false);
    MethodReflectionProxy right = new MethodReflectionProxy(new ClassReflectionProxy("com.example.MissingType"), "work", proxies(String.class), false);

    assertEquals(left, right);
    assertEquals(left.hashCode(), right.hashCode());
  }

  @Test
  public void methodEqualsReturnsFalseForDifferentNames() {
    MethodReflectionProxy left = new MethodReflectionProxy(new ClassReflectionProxy("com.example.MissingType"), "one", proxies(String.class), false);
    MethodReflectionProxy right = new MethodReflectionProxy(new ClassReflectionProxy("com.example.MissingType"), "two", proxies(String.class), false);

    assertNotEquals(left, right);
  }

  @Test
  public void methodEqualsReturnsFalseForDifferentParameterLengths() {
    MethodReflectionProxy left = new MethodReflectionProxy(new ClassReflectionProxy("com.example.MissingType"), "work", proxies(String.class), false);
    MethodReflectionProxy right = new MethodReflectionProxy(new ClassReflectionProxy("com.example.MissingType"), "work", proxies(String.class, Integer.class), false);

    assertNotEquals(left, right);
  }

  @Test
  public void methodEqualsReturnsFalseForDifferentParameterTypes() {
    MethodReflectionProxy left = new MethodReflectionProxy(new ClassReflectionProxy("com.example.MissingType"), "work", proxies(String.class), false);
    MethodReflectionProxy right = new MethodReflectionProxy(new ClassReflectionProxy("com.example.MissingType"), "work", proxies(Integer.class), false);

    assertNotEquals(left, right);
  }

  @Test
  public void methodEqualsReturnsFalseForDifferentDeclaringClasses() {
    MethodReflectionProxy left = new MethodReflectionProxy(new ClassReflectionProxy("com.example.Left"), "work", proxies(String.class), false);
    MethodReflectionProxy right = new MethodReflectionProxy(new ClassReflectionProxy("com.example.Right"), "work", proxies(String.class), false);

    assertNotEquals(left, right);
  }

  @Test
  public void methodGetReifiedParameterAnnotationsReturnsAnnotations() {
    MethodReflectionProxy proxy = new MethodReflectionProxy(declaredMethod(Fixture.class, "annotated", int.class, String.class));
    Annotation[][] annotations = proxy.getReifiedParameterAnnotations();

    assertNotNull(annotations);
    assertEquals(2, annotations.length);
    assertEquals(1, annotations[0].length);
    assertEquals(Marker.class, annotations[0][0].annotationType());
  }

  @Test
  public void methodGetReifiedParameterAnnotationsReturnsNullWhenMethodMissing() {
    MethodReflectionProxy proxy = new MethodReflectionProxy(new ClassReflectionProxy(Fixture.class), "missing", proxies(String.class), false);

    assertNull(proxy.getReifiedParameterAnnotations());
  }

  @Test
  public void methodGetParameterAnnotationsFallsBackToEmptyOuterArray() {
    MethodReflectionProxy proxy = new MethodReflectionProxy(new ClassReflectionProxy("com.example.MissingType"), "missing", proxies(String.class, Integer.class), false);
    Annotation[][] annotations = proxy.getParameterAnnotations();

    assertNotNull(annotations);
    assertEquals(2, annotations.length);
    assertNull(annotations[0]);
    assertNull(annotations[1]);
  }

  @Test
  public void methodGetParameterAnnotationsReturnsReifiedAnnotations() {
    MethodReflectionProxy proxy = new MethodReflectionProxy(declaredMethod(Fixture.class, "annotated", int.class, String.class));
    Annotation[][] annotations = proxy.getParameterAnnotations();

    assertEquals(2, annotations.length);
    assertEquals(Marker.class, annotations[1][0].annotationType());
  }

  @Test
  public void methodGetReplacementIfNecessaryReturnsNullWhenDeclaringClassMissing() {
    MethodReflectionProxy original = new MethodReflectionProxy(new ClassReflectionProxy("com.example.MissingType"), "work", proxies(String.class), false);

    assertNull(MethodReflectionProxy.getReplacementIfNecessary(original));
  }

  @Test
  public void methodGetReplacementIfNecessaryReturnsNullWhenExactMethodExists() {
    MethodReflectionProxy original = new MethodReflectionProxy(new ClassReflectionProxy(Fixture.class), "varArgsMethod", proxies(String.class, Object[].class), false);

    assertNull(MethodReflectionProxy.getReplacementIfNecessary(original));
  }

  @Test
  public void methodGetReplacementIfNecessaryFindsDirectVarArgsReplacement() {
    MethodReflectionProxy original = new MethodReflectionProxy(new ClassReflectionProxy(Fixture.class), "promotedVarArgs", proxies(String.class, Integer.class), false);
    MethodReflectionProxy replacement = MethodReflectionProxy.getReplacementIfNecessary(original);

    assertNotNull(replacement);
    assertEquals(declaredMethod(Fixture.class, "promotedVarArgs", String.class, Integer.class, Object[].class), replacement.getReification());
    assertTrue(replacement.isVarArgs());
  }

  @Test
  public void methodGetReplacementIfNecessaryFindsTrimmedArrayVarArgsReplacement() {
    MethodReflectionProxy original = new MethodReflectionProxy(new ClassReflectionProxy(Fixture.class), "trimmedVarArgs", proxies(String.class, String[].class), false);
    MethodReflectionProxy replacement = MethodReflectionProxy.getReplacementIfNecessary(original);

    assertNotNull(replacement);
    assertEquals(declaredMethod(Fixture.class, "trimmedVarArgs", String.class, Object[].class), replacement.getReification());
    assertTrue(replacement.isVarArgs());
  }

  @Test
  public void methodGetReplacementIfNecessaryReturnsNullForMissingOriginalVarArgsMethod() {
    MethodReflectionProxy original = new MethodReflectionProxy(new ClassReflectionProxy(Fixture.class), "missing", proxies(String.class), true);

    assertNull(MethodReflectionProxy.getReplacementIfNecessary(original));
  }

  @Test
  public void methodToStringIncludesNameAndDeclaringClass() {
    MethodReflectionProxy proxy = new MethodReflectionProxy(new ClassReflectionProxy(Fixture.class), "doSomething", proxies(int.class), false);

    assertTrue(proxy.toString().contains("declaringClassName=" + Fixture.class.getName()));
    assertTrue(proxy.toString().contains(";name=doSomething"));
  }

  @Test
  public void fieldConstructorFromFieldCopiesMetadata() {
    Field field = publicField(Fixture.class, "value");
    FieldReflectionProxy proxy = new FieldReflectionProxy(field);

    assertEquals("value", proxy.getName());
    assertEquals(Fixture.class.getName(), proxy.getDeclaringClassReflectionProxy().getName());
  }

  @Test
  public void fieldConstructorFromProxiesStoresConfiguredValues() {
    FieldReflectionProxy proxy = new FieldReflectionProxy(new ClassReflectionProxy(Fixture.class), "name");

    assertEquals("name", proxy.getName());
  }

  @Test
  public void fieldReifyReturnsPublicFieldWhenPresent() {
    FieldReflectionProxy proxy = new FieldReflectionProxy(new ClassReflectionProxy(Fixture.class), "value");

    assertEquals(publicField(Fixture.class, "value"), proxy.getReification());
  }

  @Test
  public void fieldReifyReturnsNullForMissingField() {
    FieldReflectionProxy proxy = new FieldReflectionProxy(new ClassReflectionProxy(Fixture.class), "missing");

    assertNull(proxy.getReification());
  }

  @Test
  public void fieldReifyReturnsNullWhenDeclaringClassCannotBeReified() {
    FieldReflectionProxy proxy = new FieldReflectionProxy(new ClassReflectionProxy("com.example.MissingType"), "value");

    assertNull(proxy.getReification());
  }

  @Test
  public void fieldEqualsAndHashCodeMatchForReifiedProxies() {
    Field field = publicField(Fixture.class, "name");
    FieldReflectionProxy left = new FieldReflectionProxy(field);
    FieldReflectionProxy right = new FieldReflectionProxy(new ClassReflectionProxy(Fixture.class), "name");

    assertEquals(left, right);
    assertEquals(left.hashCode(), right.hashCode());
  }

  @Test
  public void fieldEqualsAndHashCodeMatchForNonReifiableProxies() {
    FieldReflectionProxy left = new FieldReflectionProxy(new ClassReflectionProxy("com.example.MissingType"), "value");
    FieldReflectionProxy right = new FieldReflectionProxy(new ClassReflectionProxy("com.example.MissingType"), "value");

    assertEquals(left, right);
    assertEquals(left.hashCode(), right.hashCode());
  }

  @Test
  public void fieldEqualsReturnsFalseForDifferentNames() {
    FieldReflectionProxy left = new FieldReflectionProxy(new ClassReflectionProxy("com.example.MissingType"), "value");
    FieldReflectionProxy right = new FieldReflectionProxy(new ClassReflectionProxy("com.example.MissingType"), "name");

    assertNotEquals(left, right);
  }

  @Test
  public void fieldToStringIncludesNameAndDeclaringClass() {
    FieldReflectionProxy proxy = new FieldReflectionProxy(new ClassReflectionProxy(Fixture.class), "value");

    assertTrue(proxy.toString().contains("declaringClassName=" + Fixture.class.getName()));
    assertTrue(proxy.toString().contains(";name=value"));
  }
}
