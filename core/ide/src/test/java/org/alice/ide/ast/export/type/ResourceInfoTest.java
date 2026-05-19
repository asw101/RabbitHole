package org.alice.ide.ast.export.type;

import org.junit.Test;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class ResourceInfoTest {
  @Test
  public void isFinalClass() {
    assertTrue(Modifier.isFinal(ResourceInfo.class.getModifiers()));
  }
  @Test
  public void constructAndAccessClassName() {
    ResourceInfo info = new ResourceInfo("com.example.MyResource", "DEFAULT");
    assertEquals("com.example.MyResource", info.getClassName());
    assertEquals("DEFAULT", info.getFieldName());
  }
  @Test
  public void getClassName_returnsConstructorArg() {
    ResourceInfo info = new ResourceInfo("Foo", "bar");
    assertEquals("Foo", info.getClassName());
  }
  @Test
  public void getFieldName_returnsConstructorArg() {
    ResourceInfo info = new ResourceInfo("X", "Y");
    assertEquals("Y", info.getFieldName());
  }
}
