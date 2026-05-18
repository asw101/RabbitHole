package org.alice.ide.ast.export.type;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Consolidated tests for the export-info DTOs: FieldInfo, FunctionInfo, ResourceInfo.
 * Each is a simple value holder — we verify constructor→getter wiring and null tolerance.
 */
public class ExportInfoDtoTest {

  @Test
  public void fieldInfo_storesNameAndValueClassName() {
    FieldInfo info = new FieldInfo("java.lang.Double", "speed");
    assertEquals("speed", info.getName());
    assertEquals("java.lang.Double", info.getValueClassName());
  }

  @Test
  public void fieldInfo_acceptsNulls() {
    FieldInfo withNullName = new FieldInfo("java.lang.Object", null);
    assertNull(withNullName.getName());
    FieldInfo withNullType = new FieldInfo(null, "x");
    assertNull(withNullType.getValueClassName());
  }

  @Test
  public void functionInfo_storesNameAndReturnClassName() {
    FunctionInfo info = new FunctionInfo("java.lang.Boolean", "isReady");
    assertEquals("isReady", info.getName());
    assertEquals("java.lang.Boolean", info.getReturnClassName());
  }

  @Test
  public void functionInfo_acceptsNulls() {
    FunctionInfo withNullName = new FunctionInfo("java.lang.Object", null);
    assertNull(withNullName.getName());
    FunctionInfo withNullReturn = new FunctionInfo(null, "fn");
    assertNull(withNullReturn.getReturnClassName());
  }

  @Test
  public void resourceInfo_storesClassNameAndFieldName() {
    ResourceInfo info = new ResourceInfo("com.example.MyResource", "ADULT_FEMALE");
    assertEquals("com.example.MyResource", info.getClassName());
    assertEquals("ADULT_FEMALE", info.getFieldName());
  }

  @Test
  public void resourceInfo_acceptsNulls() {
    ResourceInfo withNullField = new ResourceInfo("com.example.Type", null);
    assertNull(withNullField.getFieldName());
    ResourceInfo withNullClass = new ResourceInfo(null, "field");
    assertNull(withNullClass.getClassName());
  }
}
