package org.alice.ide.ast.export;

import org.junit.Test;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class MethodInfoTest {
  @Test
  public void extendsMemberInfo() {
    assertTrue(MemberInfo.class.isAssignableFrom(MethodInfo.class));
  }
  @Test
  public void isPublicConcrete() {
    int mods = MethodInfo.class.getModifiers();
    assertTrue(Modifier.isPublic(mods));
    assertFalse(Modifier.isAbstract(mods));
  }
}
