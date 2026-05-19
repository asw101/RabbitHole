package org.alice.ide.croquet.models.menubar;

import org.junit.Test;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class FileMenuModelTest {
  @Test
  public void isPublic() {
    assertTrue(Modifier.isPublic(FileMenuModel.class.getModifiers()));
  }
  @Test
  public void isNotAbstract() {
    assertFalse(Modifier.isAbstract(FileMenuModel.class.getModifiers()));
  }
  @Test
  public void extendsStaticMenuModel() {
    assertTrue(org.lgna.croquet.StaticMenuModel.class.isAssignableFrom(FileMenuModel.class));
  }
}
