package org.alice.ide.croquet.models.declaration;

import org.junit.Test;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class InitializerStateOwnerTest {
  @Test
  public void isInterface() {
    assertTrue(Modifier.isInterface(InitializerStateOwner.class.getModifiers()));
  }
  @Test
  public void hasGetValueTypeMethod() throws Exception {
    Method m = InitializerStateOwner.class.getMethod("getValueType");
    assertNotNull(m);
  }
  @Test
  public void getValueType_returnsAbstractType() throws Exception {
    Method m = InitializerStateOwner.class.getMethod("getValueType");
    assertEquals(org.lgna.project.ast.AbstractType.class, m.getReturnType());
  }
}
