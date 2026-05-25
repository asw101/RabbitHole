package org.alice.stageide.croquet.models.cascade.adapters;

import org.junit.Test;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.LambdaExpression;
import org.lgna.project.ast.UserLambda;
import org.lgna.story.event.ArrowKeyPressListener;
import org.lgna.story.event.KeyPressListener;
import org.lgna.story.event.MouseClickOnScreenListener;
import org.lgna.story.event.NumberKeyPressListener;
import org.lgna.story.event.ProximityEnterListener;
import org.lgna.story.event.ProximityExitListener;
import org.lgna.story.event.SceneActivationListener;
import org.lgna.story.event.TimeListener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class LambdaExpressionFillInBehaviorTest {
  private void assertListenerFillIn(LambdaExpressionFillIn fillIn, Class<?> listenerClass) {
    assertTrue(fillIn.isAutomaticallySelectedWhenSoleOption());

    LambdaExpression transientValue = fillIn.getTransientValue(null);
    LambdaExpression secondTransientValue = fillIn.getTransientValue(null);
    LambdaExpression createdValue = fillIn.createValue(null);
    UserLambda expectedLambda = (UserLambda) AstUtilities.createLambdaExpression(listenerClass).value.getValue();

    assertSame(transientValue, secondTransientValue);
    assertNotSame(transientValue, createdValue);
    assertNotNull(transientValue.value.getValue());
    assertNotNull(createdValue.value.getValue());
    assertTrue(transientValue.value.getValue() instanceof UserLambda);
    assertTrue(createdValue.value.getValue() instanceof UserLambda);

    UserLambda transientLambda = (UserLambda) transientValue.value.getValue();
    UserLambda createdLambda = (UserLambda) createdValue.value.getValue();

    assertEquals(expectedLambda.getReturnType(), transientLambda.getReturnType());
    assertEquals(expectedLambda.getRequiredParameters().size(), transientLambda.getRequiredParameters().size());
    assertTrue(transientLambda.isSignatureLocked.getValue());

    assertEquals(expectedLambda.getReturnType(), createdLambda.getReturnType());
    assertEquals(expectedLambda.getRequiredParameters().size(), createdLambda.getRequiredParameters().size());
    assertTrue(createdLambda.isSignatureLocked.getValue());
  }

  @Test
  public void singletonAccessorsReturnStableInstances() {
    assertSame(ArrowKeyAdapterFillIn.getInstance(), ArrowKeyAdapterFillIn.getInstance());
    assertSame(NumberKeyAdapterFillIn.getInstance(), NumberKeyAdapterFillIn.getInstance());
    assertSame(SceneActivationEventFillIn.getInstance(), SceneActivationEventFillIn.getInstance());
    assertSame(MouseClickedOnScreenAdapterFillIn.getInstance(), MouseClickedOnScreenAdapterFillIn.getInstance());
    assertSame(EnterProximityEventListenerAdapterFillIn.getInstance(), EnterProximityEventListenerAdapterFillIn.getInstance());
    assertSame(ExitProximityEventListenerAdapterFillIn.getInstance(), ExitProximityEventListenerAdapterFillIn.getInstance());
    assertSame(KeyAdapterFillIn.getInstance(), KeyAdapterFillIn.getInstance());
    assertSame(TimerEventListenerAdapterFillIn.getInstance(), TimerEventListenerAdapterFillIn.getInstance());
  }

  @Test
  public void listenerFillInsCreateExpectedHeadlessLambdaShapes() {
    assertListenerFillIn(ArrowKeyAdapterFillIn.getInstance(), ArrowKeyPressListener.class);
    assertListenerFillIn(NumberKeyAdapterFillIn.getInstance(), NumberKeyPressListener.class);
    assertListenerFillIn(SceneActivationEventFillIn.getInstance(), SceneActivationListener.class);
    assertListenerFillIn(MouseClickedOnScreenAdapterFillIn.getInstance(), MouseClickOnScreenListener.class);
    assertListenerFillIn(EnterProximityEventListenerAdapterFillIn.getInstance(), ProximityEnterListener.class);
    assertListenerFillIn(ExitProximityEventListenerAdapterFillIn.getInstance(), ProximityExitListener.class);
    assertListenerFillIn(KeyAdapterFillIn.getInstance(), KeyPressListener.class);
    assertListenerFillIn(TimerEventListenerAdapterFillIn.getInstance(), TimeListener.class);
  }
}
