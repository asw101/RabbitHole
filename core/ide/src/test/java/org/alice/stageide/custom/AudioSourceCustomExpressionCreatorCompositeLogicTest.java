package org.alice.stageide.custom;

import org.junit.Test;
import org.lgna.common.resources.AudioResource;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.DoubleLiteral;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.JavaConstructor;
import org.lgna.project.ast.ResourceExpression;
import org.lgna.story.AudioSource;

import java.util.UUID;

import static org.junit.Assert.*;

public class AudioSourceCustomExpressionCreatorCompositeLogicTest {
  @Test
  public void getOptionalArgumentCountTracksDefaults() {
    assertEquals(0, AudioSourceCustomExpressionCreatorCompositeLogic.getOptionalArgumentCount(1.0, 0.0, Double.NaN));
    assertEquals(1, AudioSourceCustomExpressionCreatorCompositeLogic.getOptionalArgumentCount(0.5, 0.0, Double.NaN));
    assertEquals(2, AudioSourceCustomExpressionCreatorCompositeLogic.getOptionalArgumentCount(1.0, 2.0, Double.NaN));
    assertEquals(3, AudioSourceCustomExpressionCreatorCompositeLogic.getOptionalArgumentCount(1.0, 2.0, 4.0));
  }

  @Test
  public void decodeSelectionReadsResourceAndLiterals() {
    AudioResource resource = new AudioResource(UUID.randomUUID());
    Expression expression = AstUtilities.createInstanceCreation(JavaConstructor.getInstance(AudioSource.class, AudioResource.class, Number.class, Number.class, Number.class), new ResourceExpression(AudioResource.class, resource), new DoubleLiteral(0.5), new DoubleLiteral(1.5), new DoubleLiteral(3.0));

    AudioSourceCustomExpressionCreatorCompositeLogic.Selection selection = AudioSourceCustomExpressionCreatorCompositeLogic.decodeSelection(expression);

    assertSame(resource, selection.getAudioResource());
    assertEquals(0.5, selection.getVolumeLevel(), 0.00001);
    assertEquals(1.5, selection.getStartTime(), 0.00001);
    assertEquals(3.0, selection.getStopTime(), 0.00001);
  }
}
