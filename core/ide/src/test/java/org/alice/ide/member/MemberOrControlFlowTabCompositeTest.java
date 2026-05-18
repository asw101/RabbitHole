package org.alice.ide.member;

import org.junit.Test;

import static org.junit.Assert.*;

public class MemberOrControlFlowTabCompositeTest {

  @Test
  public void class_isAbstract() {
    assertTrue(java.lang.reflect.Modifier.isAbstract(MemberOrControlFlowTabComposite.class.getModifiers()));
  }

  @Test
  public void class_isPublic() {
    assertTrue(java.lang.reflect.Modifier.isPublic(MemberOrControlFlowTabComposite.class.getModifiers()));
  }

  @Test
  public void class_hasCustomizeTitleComponentAppearance() throws Exception {
    var methods = MemberOrControlFlowTabComposite.class.getDeclaredMethods();
    boolean found = false;
    for (var m : methods) {
      if ("customizeTitleComponentAppearance".equals(m.getName())) {
        found = true;
        break;
      }
    }
    assertTrue("Expected customizeTitleComponentAppearance method", found);
  }
}
