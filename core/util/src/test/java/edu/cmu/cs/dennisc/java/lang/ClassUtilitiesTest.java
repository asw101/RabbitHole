package edu.cmu.cs.dennisc.java.lang;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for ClassUtilities — forName with primitives/replacements,
 * getInstance, assignability, array dimensions, package/class name parsing.
 */
public class ClassUtilitiesTest {

  // --- forName with primitive types ---

  @Test
  public void forName_int() throws ClassNotFoundException {
    Class<?> cls = ClassUtilities.forName("int");
    assertEquals(int.class, cls);
  }

  @Test
  public void forName_boolean() throws ClassNotFoundException {
    Class<?> cls = ClassUtilities.forName("boolean");
    assertEquals(boolean.class, cls);
  }

  @Test
  public void forName_byte() throws ClassNotFoundException {
    Class<?> cls = ClassUtilities.forName("byte");
    assertEquals(byte.class, cls);
  }

  @Test
  public void forName_char() throws ClassNotFoundException {
    Class<?> cls = ClassUtilities.forName("char");
    assertEquals(char.class, cls);
  }

  @Test
  public void forName_double() throws ClassNotFoundException {
    Class<?> cls = ClassUtilities.forName("double");
    assertEquals(double.class, cls);
  }

  @Test
  public void forName_float() throws ClassNotFoundException {
    Class<?> cls = ClassUtilities.forName("float");
    assertEquals(float.class, cls);
  }

  @Test
  public void forName_long() throws ClassNotFoundException {
    Class<?> cls = ClassUtilities.forName("long");
    assertEquals(long.class, cls);
  }

  @Test
  public void forName_short() throws ClassNotFoundException {
    Class<?> cls = ClassUtilities.forName("short");
    assertEquals(short.class, cls);
  }

  @Test
  public void forName_void() throws ClassNotFoundException {
    Class<?> cls = ClassUtilities.forName("void");
    assertEquals(void.class, cls);
  }

  // --- forName with normal class ---

  @Test
  public void forName_normalClass() throws ClassNotFoundException {
    Class<?> cls = ClassUtilities.forName("java.lang.String");
    assertEquals(String.class, cls);
  }

  @Test(expected = ClassNotFoundException.class)
  public void forName_nonExistentClass() throws ClassNotFoundException {
    ClassUtilities.forName("com.nonexistent.FakeClass");
  }

  // --- getInstance ---

  @Test
  public void getInstance_matchingType() {
    String str = "hello";
    String result = ClassUtilities.getInstance(str, String.class);
    assertSame(str, result);
  }

  @Test
  public void getInstance_subtype() {
    Integer num = 42;
    Number result = ClassUtilities.getInstance(num, Number.class);
    assertSame(num, result);
  }

  @Test
  public void getInstance_nonMatchingType() {
    String str = "hello";
    Integer result = ClassUtilities.getInstance(str, Integer.class);
    assertNull(result);
  }

  @Test
  public void getInstance_nullObject() {
    String result = ClassUtilities.getInstance(null, String.class);
    assertNull(result);
  }

  // --- isAssignableToAtLeastOne ---

  @Test
  public void isAssignableToAtLeastOne_matching() {
    assertTrue(ClassUtilities.isAssignableToAtLeastOne(
        Integer.class, Number.class, String.class));
  }

  @Test
  public void isAssignableToAtLeastOne_noMatch() {
    assertFalse(ClassUtilities.isAssignableToAtLeastOne(
        Integer.class, String.class, Boolean.class));
  }

  @Test
  public void isAssignableToAtLeastOne_exactMatch() {
    assertTrue(ClassUtilities.isAssignableToAtLeastOne(
        String.class, String.class));
  }

  // --- getArrayDimensionCount ---

  @Test
  public void getArrayDimensionCount_noArray() {
    assertEquals(0, ClassUtilities.getArrayDimensionCount("java.lang.String"));
  }

  @Test
  public void getArrayDimensionCount_oneDimension() {
    assertEquals(1, ClassUtilities.getArrayDimensionCount("[java.lang.String"));
  }

  @Test
  public void getArrayDimensionCount_twoDimensions() {
    assertEquals(2, ClassUtilities.getArrayDimensionCount("[[java.lang.String"));
  }

  // --- getPackageName ---

  @Test
  public void getPackageName_normal() {
    assertEquals("java.lang", ClassUtilities.getPackageName("java.lang.String"));
  }

  @Test
  public void getPackageName_defaultPackage() {
    // No dot means no package — returns null
    String result = ClassUtilities.getPackageName("SimpleClass");
    assertNull(result);
  }

  // --- getSimpleClassNames ---

  @Test
  public void getSimpleClassNames_normal() {
    String[] names = ClassUtilities.getSimpleClassNames("java.lang.String");
    assertNotNull(names);
    assertTrue(names.length > 0);
    assertEquals("String", names[names.length - 1]);
  }

  @Test
  public void getSimpleClassNames_innerClass() {
    // Uses $ for inner classes in JVM naming
    String[] names = ClassUtilities.getSimpleClassNames("java.util.Map$Entry");
    assertNotNull(names);
    assertTrue(names.length >= 2);
  }

  // --- getPackage ---

  @Test
  public void getPackage_normalClass() {
    Package pkg = ClassUtilities.getPackage(String.class);
    assertNotNull(pkg);
    assertEquals("java.lang", pkg.getName());
  }

  // --- getTrimmedClassName ---

  @Test
  public void getTrimmedClassName_removesPackage() {
    String trimmed = ClassUtilities.getTrimmedClassName(String.class);
    assertNotNull(trimmed);
    assertFalse(trimmed.contains("java.lang"));
    assertTrue(trimmed.contains("String"));
  }

  @Test
  public void getTrimmedClassName_primitive() {
    String trimmed = ClassUtilities.getTrimmedClassName(int.class);
    assertNotNull(trimmed);
    assertEquals("int", trimmed);
  }
}
