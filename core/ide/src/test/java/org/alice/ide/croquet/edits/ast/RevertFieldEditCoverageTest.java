package org.alice.ide.croquet.edits.ast;

import org.alice.ide.ast.ReflectionTestHelper;
import org.junit.Test;
import org.lgna.croquet.edits.AbstractEdit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class RevertFieldEditCoverageTest {
  @Test
  public void extendsAbstractEdit() {
    assertTrue(AbstractEdit.class.isAssignableFrom(RevertFieldEdit.class));
  }

  @Test
  public void hasFieldAndRedoStateCodeFields() {
    ReflectionTestHelper.assertFieldIsPrivateFinal(RevertFieldEdit.class, "field");
    ReflectionTestHelper.assertFieldIsPrivateFinal(RevertFieldEdit.class, "redoStateCode");
  }

  @Test
  public void hasBinaryDecoderConstructor() {
    boolean found = false;
    for (Constructor<?> constructor : RevertFieldEdit.class.getDeclaredConstructors()) {
      if (constructor.getParameterCount() == 2 && constructor.getParameterTypes()[0].getSimpleName().equals("BinaryDecoder")) {
        found = true;
      }
    }
    assertTrue(found);
  }

  @Test
  public void getFieldMethod_isProtected() throws Exception {
    assertTrue(Modifier.isProtected(RevertFieldEdit.class.getDeclaredMethod("getField").getModifiers()));
  }
}
