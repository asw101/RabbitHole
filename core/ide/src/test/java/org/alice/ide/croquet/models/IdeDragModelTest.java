package org.alice.ide.croquet.models;

import org.junit.Test;
import org.lgna.croquet.DragModel;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import static org.junit.Assert.*;

public class IdeDragModelTest {
  @Test
  public void isAbstract() {
    assertTrue(Modifier.isAbstract(IdeDragModel.class.getModifiers()));
  }
  @Test
  public void implementsDragModel() {
    assertTrue(DragModel.class.isAssignableFrom(IdeDragModel.class));
  }
  @Test
  public void hasCreateListOfPotentialDropReceptorsMethod() throws Exception {
    Method m = IdeDragModel.class.getMethod("createListOfPotentialDropReceptors");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isFinal(m.getModifiers()));
  }
}
