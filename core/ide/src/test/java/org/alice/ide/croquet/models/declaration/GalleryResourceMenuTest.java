package org.alice.ide.croquet.models.declaration;

import org.junit.Test;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class GalleryResourceMenuTest {
  @Test
  public void isPublic() {
    assertTrue(Modifier.isPublic(GalleryResourceMenu.class.getModifiers()));
  }
  @Test
  public void extendsCascadeMenuModel() {
    assertTrue(org.lgna.croquet.CascadeMenuModel.class.isAssignableFrom(GalleryResourceMenu.class));
  }
  @Test
  public void hasGetInstanceStaticMethod() throws Exception {
    Method m = GalleryResourceMenu.class.getMethod("getInstance", org.lgna.project.ast.AbstractType.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }
}
