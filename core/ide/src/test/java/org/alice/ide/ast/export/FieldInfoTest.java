package org.alice.ide.ast.export;

import org.junit.Test;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class FieldInfoTest {
  @Test
  public void extendsMemberInfo() {
    assertTrue(MemberInfo.class.isAssignableFrom(FieldInfo.class));
  }
  @Test
  public void isPublicConcrete() {
    int mods = FieldInfo.class.getModifiers();
    assertTrue(Modifier.isPublic(mods));
    assertFalse(Modifier.isAbstract(mods));
  }
}
