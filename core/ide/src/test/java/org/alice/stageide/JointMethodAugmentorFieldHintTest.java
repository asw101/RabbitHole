package org.alice.stageide;

import org.junit.Test;
import org.lgna.project.ast.JavaField;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

public class JointMethodAugmentorFieldHintTest {
  @Test
  public void getFieldMethodNameHint_readsFieldTemplateHint() throws Exception {
    Method method = JointMethodAugmentor.class.getDeclaredMethod("getFieldMethodNameHint", org.lgna.project.ast.AbstractField.class);
    method.setAccessible(true);
    String hint = (String) method.invoke(null, JavaField.getInstance(org.lgna.story.resources.biped.BlackCatResource.class, "TAIL_0"));
    assertEquals("getTail", hint);
  }
}
