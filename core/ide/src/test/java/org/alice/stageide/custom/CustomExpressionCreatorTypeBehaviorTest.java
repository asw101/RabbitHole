package org.alice.stageide.custom;

import org.alice.ide.cascade.fillerinners.ExpressionFillerInner;
import org.alice.stageide.cascade.fillerinners.AudioSourceFillerInner;
import org.alice.stageide.cascade.fillerinners.ColorFillerInner;
import org.alice.stageide.cascade.fillerinners.KeyFillerInner;
import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.story.AudioSource;
import org.lgna.story.Color;
import org.lgna.story.Key;
import org.lgna.story.Paint;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CustomExpressionCreatorTypeBehaviorTest {
  private static boolean appliesTo(ExpressionFillerInner inner, Class<?> type) {
    return inner.isAssignableTo(JavaType.getInstance(type));
  }

  @Test
  public void colorExpressionsAreAvailableForColorAndPaintTargetsOnly() {
    ExpressionFillerInner inner = new ColorFillerInner();

    assertTrue(appliesTo(inner, Color.class));
    assertTrue(appliesTo(inner, Paint.class));
    assertFalse(appliesTo(inner, Key.class));
    assertFalse(appliesTo(inner, AudioSource.class));
  }

  @Test
  public void keyExpressionsAreOnlyAvailableForKeyTargets() {
    ExpressionFillerInner inner = new KeyFillerInner();

    assertTrue(appliesTo(inner, Key.class));
    assertFalse(appliesTo(inner, Color.class));
    assertFalse(appliesTo(inner, Paint.class));
    assertFalse(appliesTo(inner, AudioSource.class));
  }

  @Test
  public void audioSourceExpressionsAreOnlyAvailableForAudioSourceTargets() {
    ExpressionFillerInner inner = new AudioSourceFillerInner();

    assertTrue(appliesTo(inner, AudioSource.class));
    assertFalse(appliesTo(inner, Color.class));
    assertFalse(appliesTo(inner, Paint.class));
    assertFalse(appliesTo(inner, Key.class));
  }
}
