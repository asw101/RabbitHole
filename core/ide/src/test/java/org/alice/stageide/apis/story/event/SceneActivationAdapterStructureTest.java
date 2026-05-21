package org.alice.stageide.apis.story.event;

import org.junit.Test;
import org.lgna.story.event.SceneActivationEvent;
import org.lgna.story.event.SceneActivationListener;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class SceneActivationAdapterStructureTest {
  @Test
  public void classExtendsAbstractAdapterAndImplementsSceneActivationListener() {
    assertEquals(AbstractAdapter.class, SceneActivationAdapter.class.getSuperclass());
    assertTrue(SceneActivationListener.class.isAssignableFrom(SceneActivationAdapter.class));
    assertTrue(Modifier.isPublic(SceneActivationAdapter.class.getModifiers()));
  }

  @Test
  public void constructorIsPublic() throws Exception {
    assertNotNull(SceneActivationAdapter.class.getConstructor(
        org.lgna.project.virtualmachine.LambdaContext.class,
        org.lgna.project.ast.Lambda.class,
        org.lgna.project.virtualmachine.UserInstance.class));
  }

  @Test
  public void sceneActivatedMethodUsesSceneActivationEvent() throws Exception {
    Method method = SceneActivationAdapter.class.getMethod("sceneActivated", SceneActivationEvent.class);
    assertEquals(void.class, method.getReturnType());
  }
}
