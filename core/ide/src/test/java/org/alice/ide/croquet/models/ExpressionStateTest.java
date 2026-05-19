package org.alice.ide.croquet.models;

import org.junit.Test;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class ExpressionStateTest {
  @Test
  public void isAbstract() {
    assertTrue(Modifier.isAbstract(ExpressionState.class.getModifiers()));
  }
  @Test
  public void isPublic() {
    assertTrue(Modifier.isPublic(ExpressionState.class.getModifiers()));
  }
  @Test
  public void hasCreateEditorMethod() throws Exception {
    Method m = ExpressionState.class.getMethod("createEditor", org.alice.ide.x.AstI18nFactory.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }
  @Test
  public void hasCreateViewMethod() throws Exception {
    Method m = ExpressionState.class.getMethod("createView", org.alice.ide.x.AstI18nFactory.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }
}
