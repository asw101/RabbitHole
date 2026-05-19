package org.alice.ide.ast.draganddrop;

import org.alice.ide.croquet.models.IdeDragModel;
import org.junit.Test;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class CodeDragModelTest {
  @Test
  public void isAbstract() {
    assertTrue(Modifier.isAbstract(CodeDragModel.class.getModifiers()));
  }
  @Test
  public void isPublic() {
    assertTrue(Modifier.isPublic(CodeDragModel.class.getModifiers()));
  }
  @Test
  public void extendsIdeDragModel() {
    assertTrue(IdeDragModel.class.isAssignableFrom(CodeDragModel.class));
  }
}
