package org.alice.ide.croquet.models.ast;

import org.junit.Test;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class StatementContextMenuTest {
  @Test
  public void isPublic() {
    assertTrue(Modifier.isPublic(StatementContextMenu.class.getModifiers()));
  }
  @Test
  public void isNotAbstract() {
    assertFalse(Modifier.isAbstract(StatementContextMenu.class.getModifiers()));
  }
}
