package org.alice.ide.croquet.edits.ast;

import org.alice.ide.ast.ReflectionTestHelper;
import org.junit.Test;
import org.lgna.croquet.history.UserActivity;

import java.lang.reflect.Constructor;

import static org.junit.Assert.*;

public class DissolveStatementWithBodyEditCoverageTest {
  @Test
  public void extendsBlockStatementEdit() {
    assertTrue(BlockStatementEdit.class.isAssignableFrom(DissolveStatementWithBodyEdit.class));
  }

  @Test
  public void hasIndexAndStatementsFields() {
    ReflectionTestHelper.assertFieldIsPrivateFinal(DissolveStatementWithBodyEdit.class, "index");
    ReflectionTestHelper.assertFieldIsPrivateFinal(DissolveStatementWithBodyEdit.class, "statements");
  }

  @Test
  public void hasUserActivityConstructor() throws Exception {
    Constructor<?> constructor = DissolveStatementWithBodyEdit.class.getDeclaredConstructor(UserActivity.class);
    assertNotNull(constructor);
  }

  @Test
  public void hasBinaryDecoderConstructor() {
    boolean found = false;
    for (Constructor<?> constructor : DissolveStatementWithBodyEdit.class.getDeclaredConstructors()) {
      if (constructor.getParameterCount() == 2 && constructor.getParameterTypes()[0].getSimpleName().equals("BinaryDecoder")) {
        found = true;
      }
    }
    assertTrue(found);
  }
}
