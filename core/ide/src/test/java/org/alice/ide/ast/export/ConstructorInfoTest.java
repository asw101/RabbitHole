package org.alice.ide.ast.export;

import org.junit.Test;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class ConstructorInfoTest {
  @Test
  public void extendsMemberInfo() {
    assertTrue(MemberInfo.class.isAssignableFrom(ConstructorInfo.class));
  }
  @Test
  public void isPublicConcrete() {
    int mods = ConstructorInfo.class.getModifiers();
    assertTrue(Modifier.isPublic(mods));
    assertFalse(Modifier.isAbstract(mods));
  }
}
