package org.alice.ide.croquet.models.declaration;

import org.alice.ide.croquet.models.StandardExpressionState;
import org.junit.Test;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class InitializerStateTest {
  @Test
  public void isPublic() {
    assertTrue(Modifier.isPublic(InitializerState.class.getModifiers()));
  }
  @Test
  public void extendsStandardExpressionState() {
    assertTrue(StandardExpressionState.class.isAssignableFrom(InitializerState.class));
  }
  @Test
  public void isNotAbstract() {
    assertFalse(Modifier.isAbstract(InitializerState.class.getModifiers()));
  }
}
