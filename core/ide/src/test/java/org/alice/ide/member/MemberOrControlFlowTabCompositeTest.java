package org.alice.ide.member;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.UUID;

import static org.junit.Assert.*;

public class MemberOrControlFlowTabCompositeTest {

  @Test
  public void memberOrControlFlowTabComposite_isAbstract() {
    assertTrue(Modifier.isAbstract(MemberOrControlFlowTabComposite.class.getModifiers()));
  }

  @Test
  public void memberOrControlFlowTabComposite_extendsSimpleTabComposite() {
    assertEquals(org.lgna.croquet.SimpleTabComposite.class, MemberOrControlFlowTabComposite.class.getSuperclass());
  }

  @Test
  public void memberOrControlFlowTabComposite_hasUuidConstructor() throws Exception {
    assertNotNull(MemberOrControlFlowTabComposite.class.getConstructor(UUID.class));
  }

  @Test
  public void memberOrControlFlowTabComposite_declaresCustomizeTitleAppearance() throws Exception {
    Method method = MemberOrControlFlowTabComposite.class.getMethod("customizeTitleComponentAppearance", org.lgna.croquet.views.BooleanStateButton.class);
    assertNotNull(method);
  }
}
