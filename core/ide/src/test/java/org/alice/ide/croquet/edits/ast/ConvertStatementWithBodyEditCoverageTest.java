package org.alice.ide.croquet.edits.ast;

import org.alice.ide.ast.ReflectionTestHelper;
import org.junit.Test;
import org.lgna.croquet.history.UserActivity;
import org.lgna.project.ast.AbstractStatementWithBody;

import java.lang.reflect.Constructor;

import static org.junit.Assert.*;

public class ConvertStatementWithBodyEditCoverageTest {
  @Test
  public void extendsBlockStatementEdit() {
    assertTrue(BlockStatementEdit.class.isAssignableFrom(ConvertStatementWithBodyEdit.class));
  }

  @Test
  public void hasReplacementField() {
    ReflectionTestHelper.assertFieldIsPrivateFinal(ConvertStatementWithBodyEdit.class, "replacement");
  }

  @Test
  public void hasBinaryDecoderConstructor() {
    boolean found = false;
    for (Constructor<?> constructor : ConvertStatementWithBodyEdit.class.getDeclaredConstructors()) {
      if (constructor.getParameterCount() == 2 && constructor.getParameterTypes()[0].getSimpleName().equals("BinaryDecoder")) {
        found = true;
      }
    }
    assertTrue(found);
  }

  @Test
  public void hasUserActivityConstructor() {
    boolean found = false;
    for (Constructor<?> constructor : ConvertStatementWithBodyEdit.class.getDeclaredConstructors()) {
      if (constructor.getParameterCount() == 2 && constructor.getParameterTypes()[0] == UserActivity.class
          && AbstractStatementWithBody.class.isAssignableFrom(constructor.getParameterTypes()[1])) {
        found = true;
      }
    }
    assertTrue(found);
  }
}
