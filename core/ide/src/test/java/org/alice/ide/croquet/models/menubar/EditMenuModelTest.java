package org.alice.ide.croquet.models.menubar;

import org.junit.Test;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class EditMenuModelTest {
  @Test
  public void isPublic() {
    assertTrue(Modifier.isPublic(EditMenuModel.class.getModifiers()));
  }
  @Test
  public void extendsPredeterminedMenuModel() {
    assertTrue(org.lgna.croquet.PredeterminedMenuModel.class.isAssignableFrom(EditMenuModel.class));
  }
}
