package org.alice.ide.croquet.models.ast;

import org.junit.Test;
import org.lgna.croquet.CompletionModel;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class InsertStatementCompletionModelTest {
  @Test
  public void isInterface() {
    assertTrue(Modifier.isInterface(InsertStatementCompletionModel.class.getModifiers()));
  }
  @Test
  public void extendsCompletionModel() {
    assertTrue(CompletionModel.class.isAssignableFrom(InsertStatementCompletionModel.class));
  }
  @Test
  public void declaresNoOwnMethods() {
    assertEquals(0, InsertStatementCompletionModel.class.getDeclaredMethods().length);
  }
}
