package org.alice.ide.croquet.models.menubar;

import org.junit.Test;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class PreferencesMenuModelTest {
  @Test
  public void isPublic() {
    assertTrue(Modifier.isPublic(PreferencesMenuModel.class.getModifiers()));
  }
}
