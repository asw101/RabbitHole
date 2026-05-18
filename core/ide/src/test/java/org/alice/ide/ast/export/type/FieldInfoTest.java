package org.alice.ide.ast.export.type;

import org.junit.Test;
import static org.junit.Assert.*;

public class FieldInfoTest {

  @Test
  public void constructor_setsName() {
    FieldInfo info = new FieldInfo("java.lang.Double", "speed");
    assertEquals("speed", info.getName());
  }

  @Test
  public void constructor_setsValueClassName() {
    FieldInfo info = new FieldInfo("java.lang.Integer", "count");
    assertEquals("java.lang.Integer", info.getValueClassName());
  }

  @Test
  public void nullValueClassName_allowed() {
    FieldInfo info = new FieldInfo(null, "x");
    assertNull(info.getValueClassName());
    assertEquals("x", info.getName());
  }

  @Test
  public void nullName_allowed() {
    FieldInfo info = new FieldInfo("java.lang.Object", null);
    assertNull(info.getName());
    assertEquals("java.lang.Object", info.getValueClassName());
  }

  @Test
  public void emptyStrings_allowed() {
    FieldInfo info = new FieldInfo("", "");
    assertEquals("", info.getValueClassName());
    assertEquals("", info.getName());
  }
}
