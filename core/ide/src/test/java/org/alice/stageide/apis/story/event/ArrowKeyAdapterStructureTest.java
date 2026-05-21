package org.alice.stageide.apis.story.event;

import org.junit.Test;
import org.lgna.story.event.ArrowKeyEvent;
import org.lgna.story.event.ArrowKeyPressListener;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class ArrowKeyAdapterStructureTest {
  @Test
  public void classExtendsAbstractAdapterAndImplementsArrowKeyPressListener() {
    assertEquals(AbstractAdapter.class, ArrowKeyAdapter.class.getSuperclass());
    assertTrue(ArrowKeyPressListener.class.isAssignableFrom(ArrowKeyAdapter.class));
    assertTrue(Modifier.isPublic(ArrowKeyAdapter.class.getModifiers()));
  }

  @Test
  public void constructorIsPublic() throws Exception {
    assertNotNull(ArrowKeyAdapter.class.getConstructor(
        org.lgna.project.virtualmachine.LambdaContext.class,
        org.lgna.project.ast.Lambda.class,
        org.lgna.project.virtualmachine.UserInstance.class));
  }

  @Test
  public void arrowKeyPressedMethodUsesArrowKeyEvent() throws Exception {
    Method method = ArrowKeyAdapter.class.getMethod("arrowKeyPressed", ArrowKeyEvent.class);
    assertEquals(void.class, method.getReturnType());
  }
}
