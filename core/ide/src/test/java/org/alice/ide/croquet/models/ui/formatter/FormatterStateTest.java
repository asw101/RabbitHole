package org.alice.ide.croquet.models.ui.formatter;

import org.junit.Test;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class FormatterStateTest {
  @Test
  public void isPublic() {
    assertTrue(Modifier.isPublic(FormatterState.class.getModifiers()));
  }
  @Test
  public void isNotAbstract() {
    assertFalse(Modifier.isAbstract(FormatterState.class.getModifiers()));
  }
}
