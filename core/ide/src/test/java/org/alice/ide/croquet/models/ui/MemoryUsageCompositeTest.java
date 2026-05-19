package org.alice.ide.croquet.models.ui;

import org.junit.Test;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class MemoryUsageCompositeTest {
  @Test
  public void isPublic() {
    assertTrue(Modifier.isPublic(MemoryUsageComposite.class.getModifiers()));
  }
  @Test
  public void isNotAbstract() {
    assertFalse(Modifier.isAbstract(MemoryUsageComposite.class.getModifiers()));
  }
  @Test
  public void extendsFrameComposite() {
    assertTrue(org.lgna.croquet.FrameComposite.class.isAssignableFrom(MemoryUsageComposite.class));
  }
}
