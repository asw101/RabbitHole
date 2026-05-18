package org.alice.ide.ast.export.type;

import org.junit.Test;
import static org.junit.Assert.*;

public class ResourceInfoTest {

  @Test
  public void constructor_setsClassName() {
    ResourceInfo info = new ResourceInfo("com.example.MyResource", null);
    assertEquals("com.example.MyResource", info.getClassName());
  }

  @Test
  public void constructor_setsFieldName() {
    ResourceInfo info = new ResourceInfo("com.example.MyResource", "ADULT_FEMALE");
    assertEquals("ADULT_FEMALE", info.getFieldName());
  }

  @Test
  public void constructor_nullFieldName() {
    ResourceInfo info = new ResourceInfo("com.example.Type", null);
    assertNull(info.getFieldName());
  }

  @Test
  public void emptyClassName_allowed() {
    ResourceInfo info = new ResourceInfo("", "field");
    assertEquals("", info.getClassName());
  }

  @Test
  public void nullClassName_allowed() {
    ResourceInfo info = new ResourceInfo(null, "field");
    assertNull(info.getClassName());
  }
}
