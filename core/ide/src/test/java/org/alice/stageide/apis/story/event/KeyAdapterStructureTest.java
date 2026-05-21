package org.alice.stageide.apis.story.event;

import org.junit.Test;
import org.lgna.story.event.KeyEvent;
import org.lgna.story.event.KeyPressListener;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class KeyAdapterStructureTest {
  @Test
  public void classExtendsAbstractAdapterAndImplementsKeyPressListener() {
    assertEquals(AbstractAdapter.class, KeyAdapter.class.getSuperclass());
    assertTrue(KeyPressListener.class.isAssignableFrom(KeyAdapter.class));
    assertTrue(Modifier.isPublic(KeyAdapter.class.getModifiers()));
  }

  @Test
  public void constructorMatchesAbstractAdapterSignature() throws Exception {
    Constructor<KeyAdapter> constructor = KeyAdapter.class.getConstructor(
        org.lgna.project.virtualmachine.LambdaContext.class,
        org.lgna.project.ast.Lambda.class,
        org.lgna.project.virtualmachine.UserInstance.class);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
  }

  @Test
  public void keyPressedMethodUsesKeyEvent() throws Exception {
    Method method = KeyAdapter.class.getMethod("keyPressed", KeyEvent.class);
    assertEquals(void.class, method.getReturnType());
  }
}
