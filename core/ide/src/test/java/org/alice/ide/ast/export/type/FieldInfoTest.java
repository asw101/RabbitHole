package org.alice.ide.ast.export.type;

import org.junit.Test;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class FieldInfoTest {
  @Test
  public void isFinalClass() {
    assertTrue(Modifier.isFinal(FieldInfo.class.getModifiers()));
  }
  @Test
  public void constructWithValueClassNameAndName() throws Exception {
    FieldInfo info = new FieldInfo("java.lang.String", "myField");
    assertEquals("java.lang.String", info.getValueClassName());
    assertEquals("myField", info.getName());
  }
  @Test
  public void getValueClassName_returnsConstructorArg() {
    FieldInfo info = new FieldInfo("int", "count");
    assertEquals("int", info.getValueClassName());
  }
  @Test
  public void getName_returnsConstructorArg() {
    FieldInfo info = new FieldInfo("double", "value");
    assertEquals("value", info.getName());
  }
}
