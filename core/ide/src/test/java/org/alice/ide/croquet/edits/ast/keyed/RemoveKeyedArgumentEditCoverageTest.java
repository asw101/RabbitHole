package org.alice.ide.croquet.edits.ast.keyed;

import org.junit.Test;
import org.lgna.croquet.edits.AbstractEdit;

import java.lang.reflect.Constructor;

import static org.junit.Assert.*;

public class RemoveKeyedArgumentEditCoverageTest {
  @Test
  public void extendsAbstractEdit() {
    assertTrue(AbstractEdit.class.isAssignableFrom(RemoveKeyedArgumentEdit.class));
  }

  @Test
  public void hasNullaryUserActivityConstructor() throws Exception {
    Constructor<?> constructor = RemoveKeyedArgumentEdit.class.getDeclaredConstructor(org.lgna.croquet.history.UserActivity.class);
    assertNotNull(constructor);
  }

  @Test
  public void hasBinaryDecoderConstructor() {
    boolean found = false;
    for (Constructor<?> constructor : RemoveKeyedArgumentEdit.class.getDeclaredConstructors()) {
      if (constructor.getParameterCount() == 2 && constructor.getParameterTypes()[0].getSimpleName().equals("BinaryDecoder")) {
        found = true;
      }
    }
    assertTrue(found);
  }

  @Test
  public void doOrRedoInternal_methodExists() throws Exception {
    assertNotNull(RemoveKeyedArgumentEdit.class.getDeclaredMethod("doOrRedoInternal", boolean.class));
  }
}
