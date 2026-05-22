package org.alice.ide.ast.export.type;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Consolidated constructor-validation tests for FieldInfo, FunctionInfo, and ResourceInfo.
 * All three hold two string fields with identical null/empty acceptance rules.
 */
@RunWith(Parameterized.class)
public class ExportTypeInfoTest {

  @FunctionalInterface
  private interface InfoFactory {
    Object[] create(String first, String second);
  }

  @Parameterized.Parameters(name = "{0}")
  public static Collection<Object[]> types() {
    return Arrays.asList(new Object[][] {
        {"FieldInfo", (InfoFactory) (a, b) -> new Object[] {new FieldInfo(a, b), a, b},
            "getValueClassName", "getName"},
        {"FunctionInfo", (InfoFactory) (a, b) -> new Object[] {new FunctionInfo(a, b), a, b},
            "getReturnClassName", "getName"},
        {"ResourceInfo", (InfoFactory) (a, b) -> new Object[] {new ResourceInfo(a, b), a, b},
            "getClassName", "getFieldName"},
    });
  }

  private final String typeName;
  private final InfoFactory factory;
  private final String firstGetter;
  private final String secondGetter;

  public ExportTypeInfoTest(String typeName, InfoFactory factory, String firstGetter, String secondGetter) {
    this.typeName = typeName;
    this.factory = factory;
    this.firstGetter = firstGetter;
    this.secondGetter = secondGetter;
  }

  private Object invokeGetter(Object obj, String methodName) throws Exception {
    return obj.getClass().getMethod(methodName).invoke(obj);
  }

  @Test
  public void constructor_setsFirstField() throws Exception {
    Object[] result = factory.create("com.example.Type", "fieldName");
    assertEquals("com.example.Type", invokeGetter(result[0], firstGetter));
  }

  @Test
  public void constructor_setsSecondField() throws Exception {
    Object[] result = factory.create("com.example.Type", "fieldName");
    assertEquals("fieldName", invokeGetter(result[0], secondGetter));
  }

  @Test
  public void nullFirstField_allowed() throws Exception {
    Object[] result = factory.create(null, "name");
    assertNull(invokeGetter(result[0], firstGetter));
  }

  @Test
  public void nullSecondField_allowed() throws Exception {
    Object[] result = factory.create("com.example.Type", null);
    assertNull(invokeGetter(result[0], secondGetter));
  }

  @Test
  public void emptyStrings_allowed() throws Exception {
    Object[] result = factory.create("", "");
    assertEquals("", invokeGetter(result[0], firstGetter));
    assertEquals("", invokeGetter(result[0], secondGetter));
  }
}
