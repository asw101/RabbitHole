package org.alice.ide.ast.export.type;

import org.junit.Test;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class FunctionInfoTest {
  @Test
  public void isFinalClass() {
    assertTrue(Modifier.isFinal(FunctionInfo.class.getModifiers()));
  }
  @Test
  public void constructAndAccessReturnClassName() {
    FunctionInfo info = new FunctionInfo("java.lang.Boolean", "isReady");
    assertEquals("java.lang.Boolean", info.getReturnClassName());
    assertEquals("isReady", info.getName());
  }
  @Test
  public void getReturnClassName_returnsConstructorArg() {
    FunctionInfo info = new FunctionInfo("void", "doWork");
    assertEquals("void", info.getReturnClassName());
  }
}
