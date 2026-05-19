package org.alice.ide.croquet.models.declaration;

import org.junit.Test;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class GalleryResourceBlankTest {
  @Test
  public void isPublic() {
    assertTrue(Modifier.isPublic(GalleryResourceBlank.class.getModifiers()));
  }
  @Test
  public void extendsCascadeBlank() {
    assertTrue(org.lgna.croquet.CascadeBlank.class.isAssignableFrom(GalleryResourceBlank.class));
  }
  @Test
  public void hasGetInstanceStaticMethod() throws Exception {
    Method m = GalleryResourceBlank.class.getMethod("getInstance", org.lgna.project.ast.AbstractType.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }
}
