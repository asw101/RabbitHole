package org.alice.ide.croquet.models;

import org.junit.Test;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class MenuBarCompositeTest {
  @Test
  public void isAbstract() {
    assertTrue(Modifier.isAbstract(MenuBarComposite.class.getModifiers()));
  }
  @Test
  public void isPublic() {
    assertTrue(Modifier.isPublic(MenuBarComposite.class.getModifiers()));
  }
  @Test
  public void extendsCroquetMenuBarComposite() {
    assertTrue(org.lgna.croquet.MenuBarComposite.class.isAssignableFrom(MenuBarComposite.class));
  }
}
