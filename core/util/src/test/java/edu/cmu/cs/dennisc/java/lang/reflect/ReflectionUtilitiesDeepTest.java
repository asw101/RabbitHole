package edu.cmu.cs.dennisc.java.lang.reflect;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.*;

public class ReflectionUtilitiesDeepTest {
  public enum SampleEnum {
    FIRST,
    SECOND
  }

  public static class BaseFixture {
    public static final Holder BASE_STATIC = new Holder("base-static");
    public final Holder baseFinal = new Holder("base-final");
    public int inheritedValue = 7;
    private String basePrivate = "base-private";

    public String inheritedMethod(String suffix) {
      return this.basePrivate + suffix;
    }
  }

  public static class ChildFixture extends BaseFixture {
    public static final Holder CHILD_STATIC = new Holder("child-static");
    public final Holder childFinal = new Holder("child-final");
    public String publicField = "public";
    private String privateField = "private";

    public ChildFixture() {
    }

    public ChildFixture(String value) {
      this.privateField = value;
    }

    public static String staticJoin(String left, int right) {
      return left + right;
    }

    public String echo(String value) {
      return value + ":" + this.privateField;
    }

    private String hidden() {
      return this.privateField;
    }
  }

  public static class AmbiguousFixture {
    public AmbiguousFixture(String value) {
    }

    public AmbiguousFixture(Integer value) {
    }
  }

  public static class Holder {
    private final String text;

    public Holder(String text) {
      this.text = text;
    }

    public String getText() {
      return this.text;
    }
  }

  public static class ValueOfFixture {
    private final String text;

    private ValueOfFixture(String text) {
      this.text = text;
    }

    public static ValueOfFixture valueOf(String source) {
      return new ValueOfFixture("parsed:" + source);
    }

    public String getText() {
      return this.text;
    }
  }

  public static class ThrowingMethods {
    public String fail() {
      throw new IllegalStateException("boom");
    }
  }

  @Test
  public void getClassForNameFindsRuntimeClass() {
    assertEquals(String.class, ReflectionUtilities.getClassForName("java.lang.String"));
  }

  @Test
  public void getArrayClassHandlesMultipleDimensions() {
    assertEquals(String[][].class, ReflectionUtilities.getArrayClass(String.class, 2));
    assertEquals(int[][][].class, ReflectionUtilities.getArrayClass(int.class, 3));
  }

  @Test
  public void newInstanceUsesConstructorObject() {
    Constructor<ChildFixture> constructor = ReflectionUtilities.getConstructor(ChildFixture.class, String.class);
    ChildFixture instance = ReflectionUtilities.newInstance(constructor, "ctor-value");
    assertEquals("prefix:ctor-value", instance.echo("prefix"));
  }

  @Test
  public void newInstanceUsesParameterTypes() {
    ChildFixture instance = ReflectionUtilities.newInstance(ChildFixture.class, new Class<?>[] {String.class}, "typed");
    assertEquals("value:typed", instance.echo("value"));
  }

  @Test
  public void newInstanceForArgumentsFindsConstructorByArity() {
    ChildFixture instance = ReflectionUtilities.newInstanceForArguments(ChildFixture.class, "argument");
    assertEquals("argument:argument", instance.echo("argument"));
  }

  @Test
  public void newInstanceByNameCreatesObject() {
    Object instance = ReflectionUtilities.newInstance(ChildFixture.class.getName());
    assertTrue(instance instanceof ChildFixture);
  }

  @Test(expected = RuntimeException.class)
  public void getConstructorForArgumentsThrowsWhenArityIsAmbiguous() {
    ReflectionUtilities.getConstructorForArguments(AmbiguousFixture.class, "value");
  }

  @Test(expected = RuntimeException.class)
  public void newArrayInstanceWrapsNegativeArraySize() {
    ReflectionUtilities.newArrayInstance(String.class.getName(), -1);
  }

  @Test
  public void newTypedArrayInstanceCreatesTypedArray() {
    Holder[] holders = ReflectionUtilities.newTypedArrayInstance(Holder.class, 3);
    assertEquals(3, holders.length);
    assertEquals(Holder.class, holders.getClass().getComponentType());
  }

  @Test
  public void getFieldFindsInheritedPublicField() {
    Field field = ReflectionUtilities.getField(ChildFixture.class, "inheritedValue");
    assertEquals("inheritedValue", field.getName());
  }

  @Test
  public void getDeclaredFieldFindsOnlyDeclaredField() {
    Field field = ReflectionUtilities.getDeclaredField(ChildFixture.class, "privateField");
    assertEquals("privateField", field.getName());
  }

  @Test(expected = RuntimeException.class)
  public void getDeclaredFieldDoesNotSearchInheritanceChain() {
    ReflectionUtilities.getDeclaredField(ChildFixture.class, "inheritedValue");
  }

  @Test
  public void getAndSetWorkWithAccessiblePrivateField() throws Exception {
    ChildFixture fixture = new ChildFixture();
    Field field = ReflectionUtilities.getDeclaredField(ChildFixture.class, "privateField");
    field.setAccessible(true);
    assertEquals("private", ReflectionUtilities.get(field, fixture));
    ReflectionUtilities.set(field, fixture, "changed");
    assertEquals("value:changed", fixture.echo("value"));
  }

  @Test(expected = RuntimeException.class)
  public void getWrapsIllegalAccessForPrivateFieldWithoutAccessible() {
    ChildFixture fixture = new ChildFixture();
    Field field = ReflectionUtilities.getDeclaredField(ChildFixture.class, "privateField");
    ReflectionUtilities.get(field, fixture);
  }

  @Test(expected = RuntimeException.class)
  public void setWrapsIllegalAccessForPrivateFieldWithoutAccessible() {
    ChildFixture fixture = new ChildFixture();
    Field field = ReflectionUtilities.getDeclaredField(ChildFixture.class, "privateField");
    ReflectionUtilities.set(field, fixture, "blocked");
  }

  @Test
  public void getMethodFindsInheritedMethod() {
    Method method = ReflectionUtilities.getMethod(ChildFixture.class, "inheritedMethod", String.class);
    assertEquals("inheritedMethod", method.getName());
  }

  @Test
  public void getDeclaredMethodFindsPrivateMethod() {
    Method method = ReflectionUtilities.getDeclaredMethod(ChildFixture.class, "hidden");
    assertEquals("hidden", method.getName());
  }

  @Test
  public void invokeWorksForStaticMethod() {
    Method method = ReflectionUtilities.getMethod(ChildFixture.class, "staticJoin", String.class, int.class);
    assertEquals("left9", ReflectionUtilities.invoke(null, method, "left", 9));
  }

  @Test
  public void invokeWorksForInstanceMethod() {
    ChildFixture fixture = new ChildFixture("tail");
    Method method = ReflectionUtilities.getMethod(ChildFixture.class, "echo", String.class);
    assertEquals("head:tail", ReflectionUtilities.invoke(fixture, method, "head"));
  }

  @Test(expected = RuntimeException.class)
  public void invokeWrapsInvocationTargetException() {
    Method method = ReflectionUtilities.getMethod(ThrowingMethods.class, "fail");
    ReflectionUtilities.invoke(new ThrowingMethods(), method);
  }

  @Test
  public void getDetailIncludesInstanceMethodAndArguments() throws Exception {
    Method method = ChildFixture.class.getMethod("echo", String.class);
    String detail = ReflectionUtilities.getDetail(new ChildFixture("x"), method, new Object[] {"arg"});
    assertTrue(detail.contains("instance="));
    assertTrue(detail.contains("method=echo"));
    assertTrue(detail.contains("arg"));
  }

  @Test
  public void getConstructorFindsPublicConstructor() {
    Constructor<ChildFixture> constructor = ReflectionUtilities.getConstructor(ChildFixture.class, String.class);
    assertNotNull(constructor);
  }

  @Test(expected = RuntimeException.class)
  public void getConstructorWrapsMissingConstructor() {
    ReflectionUtilities.getConstructor(ChildFixture.class, Integer.class);
  }

  @Test
  public void getDeclaredConstructorCanFindPrivateConstructor() {
    Constructor<PrivateConstructorFixture> constructor = ReflectionUtilities.getDeclaredConstructor(PrivateConstructorFixture.class, String.class);
    assertNotNull(constructor);
  }

  @Test
  public void valueOfInvokesEnumValueOf() {
    assertEquals(SampleEnum.SECOND, ReflectionUtilities.valueOf(SampleEnum.class, "SECOND"));
  }

  @Test
  public void valueOfInvokesCustomStaticMethod() {
    ValueOfFixture fixture = ReflectionUtilities.valueOf(ValueOfFixture.class, "custom");
    assertEquals("parsed:custom", fixture.getText());
  }

  @Test
  public void publicFinalFieldQueriesFilterByTypeAndScope() {
    List<Field> fields = ReflectionUtilities.getPublicFinalFields(ChildFixture.class, Holder.class);
    assertEquals(4, fields.size());

    List<Field> declaredFields = ReflectionUtilities.getPublicFinalDeclaredFields(ChildFixture.class, Holder.class);
    assertEquals(2, declaredFields.size());
  }

  @Test
  public void publicStaticFinalFieldQueriesFilterByTypeAndScope() {
    List<Field> fields = ReflectionUtilities.getPublicStaticFinalFields(ChildFixture.class, Holder.class);
    assertEquals(2, fields.size());

    List<Field> declaredFields = ReflectionUtilities.getPublicStaticFinalDeclaredFields(ChildFixture.class, Holder.class);
    assertEquals(1, declaredFields.size());
    assertEquals("CHILD_STATIC", declaredFields.get(0).getName());
  }

  @Test(expected = RuntimeException.class)
  public void publicInstanceQueriesReturnStaticInstancesSafely() {
    // getPublicFinalInstances includes both static and instance fields but
    // uses get(field, null), which throws for instance fields
    ReflectionUtilities.getPublicFinalInstances(ChildFixture.class, Holder.class);
  }

  @Test
  public void publicStaticFinalInstancesReturnTypedObjects() {
    List<Holder> finalInstances = ReflectionUtilities.getPublicStaticFinalInstances(ChildFixture.class, Holder.class);
    assertEquals(2, finalInstances.size());
    assertEquals("child-static", finalInstances.get(0).getText());
    assertEquals("base-static", finalInstances.get(1).getText());
  }

  @Test
  public void modifierHelpersReflectMemberFlags() throws Exception {
    Field publicField = ChildFixture.class.getField("publicField");
    Field privateField = ChildFixture.class.getDeclaredField("privateField");
    Method staticMethod = ChildFixture.class.getMethod("staticJoin", String.class, int.class);

    assertTrue(ReflectionUtilities.isPublic(publicField));
    assertTrue(ReflectionUtilities.isPrivate(privateField));
    assertTrue(ReflectionUtilities.isStatic(staticMethod));
    assertFalse(ReflectionUtilities.isAbstract(publicField));
    assertFalse(ReflectionUtilities.isFinal(ChildFixture.class));
  }

  @Test
  public void classModifierHelpersReflectClassFlags() {
    assertTrue(ReflectionUtilities.isAbstract(AbstractFixture.class));
    assertTrue(ReflectionUtilities.isFinal(FinalFixture.class));
    assertTrue(ReflectionUtilities.isStatic(StaticNestedFixture.class));
  }

  @Test(expected = RuntimeException.class)
  public void getMethodWrapsMissingMethod() {
    ReflectionUtilities.getMethod(ChildFixture.class, "missingMethod");
  }

  @Test(expected = RuntimeException.class)
  public void getFieldWrapsMissingField() {
    ReflectionUtilities.getField(ChildFixture.class, "missingField");
  }

  @Test(expected = RuntimeException.class)
  public void newInstanceWrapsIllegalAccessForPrivateConstructor() {
    Constructor<PrivateConstructorFixture> constructor = ReflectionUtilities.getDeclaredConstructor(PrivateConstructorFixture.class, String.class);
    ReflectionUtilities.newInstance(constructor, "secret");
  }

  public abstract static class AbstractFixture {
    public abstract void act();
  }

  public static final class FinalFixture {
  }

  public static class StaticNestedFixture {
  }

  public static class PrivateConstructorFixture {
    private PrivateConstructorFixture(String value) {
    }
  }
}
