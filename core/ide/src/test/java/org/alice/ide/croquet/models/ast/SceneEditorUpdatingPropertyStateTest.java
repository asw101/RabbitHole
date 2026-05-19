package org.alice.ide.croquet.models.ast;

import org.alice.ide.ast.PropertyState;
import org.junit.Test;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class SceneEditorUpdatingPropertyStateTest {
  @Test
  public void isPublic() {
    assertTrue(Modifier.isPublic(SceneEditorUpdatingPropertyState.class.getModifiers()));
  }
  @Test
  public void extendsPropertyState() {
    assertTrue(PropertyState.class.isAssignableFrom(SceneEditorUpdatingPropertyState.class));
  }
  @Test
  public void isNotAbstract() {
    assertFalse(Modifier.isAbstract(SceneEditorUpdatingPropertyState.class.getModifiers()));
  }
}
