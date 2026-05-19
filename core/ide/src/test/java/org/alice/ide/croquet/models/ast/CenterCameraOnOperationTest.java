package org.alice.ide.croquet.models.ast;

import org.junit.Test;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class CenterCameraOnOperationTest {
  @Test
  public void isPublic() {
    assertTrue(Modifier.isPublic(CenterCameraOnOperation.class.getModifiers()));
  }
  @Test
  public void isNotAbstract() {
    assertFalse(Modifier.isAbstract(CenterCameraOnOperation.class.getModifiers()));
  }
  @Test
  public void extendsActionOperation() {
    assertTrue(org.lgna.croquet.ActionOperation.class.isAssignableFrom(CenterCameraOnOperation.class));
  }
}
