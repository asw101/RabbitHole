package org.alice.ide.ast.export;

import org.junit.Test;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class MemberInfoTest {
  @Test
  public void extendsDeclarationInfo() {
    assertTrue(DeclarationInfo.class.isAssignableFrom(MemberInfo.class));
  }
  @Test
  public void isPublic() {
    assertTrue(Modifier.isPublic(MemberInfo.class.getModifiers()));
  }
  @Test
  public void isNotAbstract() {
    assertFalse(Modifier.isAbstract(MemberInfo.class.getModifiers()));
  }
}
