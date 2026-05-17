package edu.cmu.cs.dennisc.java.lang.reflect;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for ReflectionUtilities — modifier checks, array class creation,
 * newInstance, field/method/constructor access, invoke, and public static final introspection.
 */
public class ReflectionUtilitiesTest {

  // --- Test fixtures ---

  public static class SampleClass {
    public static final String PUBLIC_CONSTANT = "constant";
    public static final int INT_CONSTANT = 42;
    public final String instanceFinal = "instance";
    private String privateField = "private";
    protected String protectedField = "protected";

    public SampleClass() {}
    public SampleClass(String value) { this.privateField = value; }
    private SampleClass(int x) {}

    public String getPrivateField() { return privateField; }
    public static String staticMethod() { return "static-result"; }
    public String instanceMethod(String arg) { return "echo:" + arg; }
    protected void protectedMethod() {}
    private void privateMethod() {}
  }

  public abstract static class AbstractSample {
    public abstract void doWork();
  }

  public static final class FinalSample {}

  public static class StaticSample {}

  public interface SampleInterface {}

  public static class ChildSample extends SampleClass implements SampleInterface {}

  // Enum for valueOf testing
  public enum SampleEnum { ALPHA, BETA, GAMMA }

  // --- Modifier checks ---

  @Test
  public void isPublic_publicConstructor() throws Exception {
    Constructor<?> c = SampleClass.class.getConstructor();
    assertTrue(ReflectionUtilities.isPublic(c));
  }

  @Test
  public void isPrivate_privateMethod() throws Exception {
    Method m = SampleClass.class.getDeclaredMethod("privateMethod");
    assertTrue(ReflectionUtilities.isPrivate(m));
  }

  @Test
  public void isProtected_protectedMethod() throws Exception {
    Method m = SampleClass.class.getDeclaredMethod("protectedMethod");
    assertTrue(ReflectionUtilities.isProtected(m));
  }

  @Test
  public void isPublic_publicMethod() throws Exception {
    Method m = SampleClass.class.getMethod("instanceMethod", String.class);
    assertTrue(ReflectionUtilities.isPublic(m));
  }

  @Test
  public void isAbstract_abstractClass() {
    assertTrue(ReflectionUtilities.isAbstract(AbstractSample.class));
  }

  @Test
  public void isAbstract_concreteClass() {
    assertFalse(ReflectionUtilities.isAbstract(SampleClass.class));
  }

  @Test
  public void isFinal_finalClass() {
    assertTrue(ReflectionUtilities.isFinal(FinalSample.class));
  }

  @Test
  public void isFinal_nonFinalClass() {
    assertFalse(ReflectionUtilities.isFinal(SampleClass.class));
  }

  @Test
  public void isStatic_staticInnerClass() {
    assertTrue(ReflectionUtilities.isStatic(StaticSample.class));
  }

  @Test
  public void isStatic_staticMethod() throws Exception {
    Method m = SampleClass.class.getMethod("staticMethod");
    assertTrue(ReflectionUtilities.isStatic(m));
  }

  @Test
  public void isStatic_instanceMethod() throws Exception {
    Method m = SampleClass.class.getMethod("instanceMethod", String.class);
    assertFalse(ReflectionUtilities.isStatic(m));
  }

  @Test
  public void isStatic_staticField() throws Exception {
    Field f = SampleClass.class.getField("PUBLIC_CONSTANT");
    assertTrue(ReflectionUtilities.isStatic(f));
  }

  @Test
  public void isStatic_instanceField() throws Exception {
    Field f = SampleClass.class.getField("instanceFinal");
    assertFalse(ReflectionUtilities.isStatic(f));
  }

  // --- Array class creation ---

  @Test
  public void getArrayClass_simple() {
    Class<?> arrayClass = ReflectionUtilities.getArrayClass(String.class);
    assertEquals(String[].class, arrayClass);
  }

  @Test
  public void getArrayClass_multiDimensional() {
    Class<?> array2D = ReflectionUtilities.getArrayClass(int.class, 2);
    assertEquals(int[][].class, array2D);
  }

  @Test
  public void getArrayClass_singleDimension() {
    Class<?> arrayClass = ReflectionUtilities.getArrayClass(double.class, 1);
    assertEquals(double[].class, arrayClass);
  }

  // --- newInstance ---

  @Test
  public void newInstance_defaultConstructor() {
    SampleClass obj = ReflectionUtilities.newInstance(SampleClass.class);
    assertNotNull(obj);
  }

  @Test
  public void newInstance_withClassName() {
    Object obj = ReflectionUtilities.newInstance(SampleClass.class.getName());
    assertNotNull(obj);
    assertTrue(obj instanceof SampleClass);
  }

  @Test
  public void newInstance_withParameters() {
    SampleClass obj = ReflectionUtilities.newInstance(
        SampleClass.class,
        new Class<?>[]{String.class},
        "custom-value");
    assertNotNull(obj);
    assertEquals("custom-value", obj.getPrivateField());
  }

  @Test
  public void newInstanceForArguments() {
    SampleClass obj = ReflectionUtilities.newInstanceForArguments(SampleClass.class, "arg-value");
    assertNotNull(obj);
    assertEquals("arg-value", obj.getPrivateField());
  }

  // --- Array instance creation ---

  @Test
  public void newArrayInstance() {
    Object arr = ReflectionUtilities.newArrayInstance(String.class.getName(), 5);
    assertTrue(arr instanceof String[]);
    assertEquals(5, ((String[]) arr).length);
  }

  @Test
  public void newTypedArrayInstance() {
    String[] arr = ReflectionUtilities.newTypedArrayInstance(String.class, 3);
    assertNotNull(arr);
    assertEquals(3, arr.length);
  }

  // --- Field access ---

  @Test
  public void getField_public() {
    Field f = ReflectionUtilities.getField(SampleClass.class, "PUBLIC_CONSTANT");
    assertNotNull(f);
    assertEquals("PUBLIC_CONSTANT", f.getName());
  }

  @Test
  public void getDeclaredField_private() {
    Field f = ReflectionUtilities.getDeclaredField(SampleClass.class, "privateField");
    assertNotNull(f);
  }

  @Test
  public void get_fieldValue() throws Exception {
    SampleClass obj = new SampleClass("test-get");
    Field f = SampleClass.class.getDeclaredField("privateField");
    f.setAccessible(true);
    Object value = ReflectionUtilities.get(f, obj);
    assertEquals("test-get", value);
  }

  @Test
  public void set_fieldValue() throws Exception {
    SampleClass obj = new SampleClass();
    Field f = SampleClass.class.getDeclaredField("privateField");
    f.setAccessible(true);
    ReflectionUtilities.set(f, obj, "new-value");
    assertEquals("new-value", obj.getPrivateField());
  }

  // --- Method access ---

  @Test
  public void getMethod_public() {
    Method m = ReflectionUtilities.getMethod(SampleClass.class, "instanceMethod", String.class);
    assertNotNull(m);
    assertEquals("instanceMethod", m.getName());
  }

  @Test
  public void getDeclaredMethod_private() {
    Method m = ReflectionUtilities.getDeclaredMethod(SampleClass.class, "privateMethod");
    assertNotNull(m);
  }

  // --- Constructor access ---

  @Test
  public void getConstructor_default() {
    Constructor<SampleClass> c = ReflectionUtilities.getConstructor(SampleClass.class);
    assertNotNull(c);
  }

  @Test
  public void getConstructor_withParams() {
    Constructor<SampleClass> c = ReflectionUtilities.getConstructor(SampleClass.class, String.class);
    assertNotNull(c);
  }

  @Test
  public void getConstructorForArguments() {
    Constructor<SampleClass> c = ReflectionUtilities.getConstructorForArguments(SampleClass.class, "arg");
    assertNotNull(c);
  }

  @Test
  public void getDeclaredConstructor_private() {
    Constructor<SampleClass> c = ReflectionUtilities.getDeclaredConstructor(SampleClass.class, int.class);
    assertNotNull(c);
  }

  // --- Invoke ---

  @Test
  public void invoke_instanceMethod() throws Exception {
    SampleClass obj = new SampleClass();
    Method m = SampleClass.class.getMethod("instanceMethod", String.class);
    Object result = ReflectionUtilities.invoke(obj, m, "hello");
    assertEquals("echo:hello", result);
  }

  @Test
  public void invoke_staticMethod() throws Exception {
    Method m = SampleClass.class.getMethod("staticMethod");
    Object result = ReflectionUtilities.invoke(null, m);
    assertEquals("static-result", result);
  }

  // --- Public static final introspection ---

  @Test
  public void getPublicStaticFinalFields() {
    List<Field> fields = ReflectionUtilities.getPublicStaticFinalFields(SampleClass.class, String.class);
    assertNotNull(fields);
    assertFalse(fields.isEmpty());
    boolean found = false;
    for (Field f : fields) {
      if ("PUBLIC_CONSTANT".equals(f.getName())) {
        found = true;
        break;
      }
    }
    assertTrue("Should find PUBLIC_CONSTANT", found);
  }

  @Test
  public void getPublicStaticFinalInstances() {
    List<String> instances = ReflectionUtilities.getPublicStaticFinalInstances(SampleClass.class, String.class);
    assertNotNull(instances);
    assertTrue(instances.contains("constant"));
  }

  @Test
  public void getPublicFinalFields_includesInstanceFields() {
    List<Field> fields = ReflectionUtilities.getPublicFinalFields(SampleClass.class, String.class);
    assertNotNull(fields);
    boolean foundInstance = false;
    for (Field f : fields) {
      if ("instanceFinal".equals(f.getName())) {
        foundInstance = true;
        break;
      }
    }
    assertTrue("Should find instanceFinal field", foundInstance);
  }

  @Test
  public void getPublicStaticFinalDeclaredFields() {
    List<Field> fields = ReflectionUtilities.getPublicStaticFinalDeclaredFields(SampleClass.class, Object.class);
    assertNotNull(fields);
    assertFalse(fields.isEmpty());
  }

  // --- valueOf ---

  @Test
  public void valueOf_enum() {
    SampleEnum result = ReflectionUtilities.valueOf(SampleEnum.class, "BETA");
    assertEquals(SampleEnum.BETA, result);
  }

  // --- getDetail ---

  @Test
  public void getDetail_returnsNonNullString() throws Exception {
    SampleClass obj = new SampleClass();
    Method m = SampleClass.class.getMethod("instanceMethod", String.class);
    String detail = ReflectionUtilities.getDetail(obj, m, new Object[]{"arg1"});
    assertNotNull(detail);
    assertTrue(detail.length() > 0);
  }

  @Test
  public void getDetail_nullMethod() {
    String detail = ReflectionUtilities.getDetail("inst", null, new Object[]{"a", "b"});
    assertTrue(detail.contains("null"));
    assertTrue(detail.contains("inst"));
  }

  @Test
  public void getDetail_multipleArgs() throws Exception {
    SampleClass obj = new SampleClass();
    Method m = SampleClass.class.getMethod("instanceMethod", String.class);
    String detail = ReflectionUtilities.getDetail(obj, m, new Object[]{"x", "y", "z"});
    assertTrue(detail.contains("x"));
    assertTrue(detail.contains("y"));
    assertTrue(detail.contains("z"));
  }

  // --- isAbstract(Member) ---

  @Test
  public void isAbstract_abstractMethod() throws Exception {
    Method m = AbstractSample.class.getDeclaredMethod("doWork");
    assertTrue(ReflectionUtilities.isAbstract(m));
  }

  @Test
  public void isAbstract_concreteMethod() throws Exception {
    Method m = SampleClass.class.getMethod("instanceMethod", String.class);
    assertFalse(ReflectionUtilities.isAbstract(m));
  }

  // --- getPublicFinalInstances ---

  @Test
  public void getPublicFinalInstances_findsStaticConstants() {
    // getPublicFinalInstances reads public field values via cls.getFields() and field.get(null),
    // so only works for static fields (instance fields would require an instance)
    List<Integer> instances = ReflectionUtilities.getPublicFinalInstances(SampleClass.class, Integer.class);
    assertNotNull(instances);
  }

  // --- getPublicFinalDeclaredFields ---

  @Test
  public void getPublicFinalDeclaredFields_returnsOnlyDeclaredFields() {
    List<Field> fields = ReflectionUtilities.getPublicFinalDeclaredFields(SampleClass.class, String.class);
    assertNotNull(fields);
    boolean foundConstant = false;
    boolean foundInstance = false;
    for (Field f : fields) {
      if ("PUBLIC_CONSTANT".equals(f.getName())) foundConstant = true;
      if ("instanceFinal".equals(f.getName())) foundInstance = true;
    }
    assertTrue("Should find PUBLIC_CONSTANT", foundConstant);
    assertTrue("Should find instanceFinal", foundInstance);
  }

  // --- getPublicStaticFinalFields with non-matching type ---

  @Test
  public void getPublicStaticFinalFields_intType() {
    List<Field> fields = ReflectionUtilities.getPublicStaticFinalFields(SampleClass.class, int.class);
    assertNotNull(fields);
  }

  // --- getPublicStaticFinalInstances with int type ---

  @Test
  public void getPublicStaticFinalInstances_intType() {
    List<Object> instances = ReflectionUtilities.getPublicStaticFinalInstances(SampleClass.class, Object.class);
    assertNotNull(instances);
    assertFalse(instances.isEmpty());
  }

  // --- getField error case ---

  @Test(expected = RuntimeException.class)
  public void getField_nonExistent_throws() {
    ReflectionUtilities.getField(SampleClass.class, "nonExistentField");
  }

  // --- getDeclaredField error case ---

  @Test(expected = RuntimeException.class)
  public void getDeclaredField_nonExistent_throws() {
    ReflectionUtilities.getDeclaredField(SampleClass.class, "nonExistentField");
  }

  // --- getMethod error case ---

  @Test(expected = RuntimeException.class)
  public void getMethod_nonExistent_throws() {
    ReflectionUtilities.getMethod(SampleClass.class, "nonExistentMethod");
  }

  // --- getDeclaredMethod error case ---

  @Test(expected = RuntimeException.class)
  public void getDeclaredMethod_nonExistent_throws() {
    ReflectionUtilities.getDeclaredMethod(SampleClass.class, "nonExistentMethod");
  }

  // --- getConstructor error case ---

  @Test(expected = RuntimeException.class)
  public void getConstructor_nonExistent_throws() {
    ReflectionUtilities.getConstructor(SampleClass.class, Double.class);
  }

  // --- getDeclaredConstructor error case ---

  @Test(expected = RuntimeException.class)
  public void getDeclaredConstructor_nonExistent_throws() {
    ReflectionUtilities.getDeclaredConstructor(SampleClass.class, Double.class);
  }

  // --- getConstructorForArguments error case ---

  @Test(expected = RuntimeException.class)
  public void getConstructorForArguments_noMatch_throws() {
    ReflectionUtilities.getConstructorForArguments(SampleClass.class, 1.0, 2.0, 3.0);
  }

  // --- getDeclaredConstructorForArguments ---

  @Test(expected = RuntimeException.class)
  public void getDeclaredConstructorForArguments_noMatch_throws() {
    ReflectionUtilities.getDeclaredConstructorForArguments(SampleClass.class, 1.0, 2.0, 3.0);
  }

  @Test
  public void getDeclaredConstructorForArguments_noArg() {
    Constructor<SampleClass> c = ReflectionUtilities.getDeclaredConstructorForArguments(SampleClass.class);
    assertNotNull(c);
  }

  // --- newInstance with Constructor ---

  @Test
  public void newInstance_withConstructor() {
    Constructor<SampleClass> c = ReflectionUtilities.getConstructor(SampleClass.class, String.class);
    SampleClass obj = ReflectionUtilities.newInstance(c, "via-constructor");
    assertEquals("via-constructor", obj.getPrivateField());
  }

  // --- getArrayClass with primitive types ---

  @Test
  public void getArrayClass_objectType() {
    Class<?> cls = ReflectionUtilities.getArrayClass(Object.class);
    assertEquals(Object[].class, cls);
  }

  @Test
  public void getArrayClass_threeDimensions() {
    Class<?> cls = ReflectionUtilities.getArrayClass(String.class, 3);
    assertEquals(String[][][].class, cls);
  }

  // --- newTypedArrayInstance ---

  @Test
  public void newTypedArrayInstance_integerType() {
    Integer[] arr = ReflectionUtilities.newTypedArrayInstance(Integer.class, 5);
    assertEquals(5, arr.length);
  }

  // --- get/set static fields ---

  @Test
  public void get_staticField() {
    Field f = ReflectionUtilities.getField(SampleClass.class, "PUBLIC_CONSTANT");
    Object value = ReflectionUtilities.get(f, null);
    assertEquals("constant", value);
  }

  // --- isStatic on non-static field ---

  @Test
  public void isPublic_publicField() throws Exception {
    Field f = SampleClass.class.getField("PUBLIC_CONSTANT");
    assertTrue(ReflectionUtilities.isPublic(f));
  }

  @Test
  public void isPrivate_privateField() throws Exception {
    Field f = SampleClass.class.getDeclaredField("privateField");
    assertTrue(ReflectionUtilities.isPrivate(f));
  }

  @Test
  public void isProtected_protectedField() throws Exception {
    Field f = SampleClass.class.getDeclaredField("protectedField");
    assertTrue(ReflectionUtilities.isProtected(f));
  }

  // --- getClassForName ---

  @Test
  public void getClassForName_validClass() {
    Class<?> cls = ReflectionUtilities.getClassForName("java.lang.String");
    assertEquals(String.class, cls);
  }

  @Test(expected = RuntimeException.class)
  public void getClassForName_invalidClass_throws() {
    ReflectionUtilities.getClassForName("com.nonexistent.FakeClass");
  }

  // --- invoke error case ---

  @Test(expected = RuntimeException.class)
  public void invoke_wrongArgument_throws() throws Exception {
    SampleClass obj = new SampleClass();
    Method m = SampleClass.class.getMethod("instanceMethod", String.class);
    ReflectionUtilities.invoke(obj, m, 12345);
  }
}
