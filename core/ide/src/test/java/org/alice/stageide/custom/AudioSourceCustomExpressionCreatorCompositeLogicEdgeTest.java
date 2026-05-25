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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class AudioSourceCustomExpressionCreatorCompositeLogicEdgeTest {
  @Test
  public void decodeSelectionReturnsDefaultsForNonInstanceCreationExpressions() {
    AudioSourceCustomExpressionCreatorCompositeLogic.Selection selection =
        AudioSourceCustomExpressionCreatorCompositeLogic.decodeSelection(new DoubleLiteral(7.0));

    assertNull(selection.getResourceExpression());
    assertNull(selection.getAudioResource());
    assertEquals(1.0, selection.getVolumeLevel(), 0.00001);
    assertEquals(0.0, selection.getStartTime(), 0.00001);
    assertTrue(Double.isNaN(selection.getStopTime()));
  }

  @Test
  public void decodeSelectionPreservesResourceAndDefaultsMissingOptionalArguments() {
    AudioResource resource = new AudioResource(UUID.randomUUID());
    Expression expression = AstUtilities.createInstanceCreation(
        JavaConstructor.getInstance(AudioSource.class, AudioResource.class),
        new ResourceExpression(AudioResource.class, resource));

    AudioSourceCustomExpressionCreatorCompositeLogic.Selection selection =
        AudioSourceCustomExpressionCreatorCompositeLogic.decodeSelection(expression);

    assertSame(resource, selection.getAudioResource());
    assertSame(resource, selection.getResourceExpression().resource.getValue());
    assertEquals(1.0, selection.getVolumeLevel(), 0.00001);
    assertEquals(0.0, selection.getStartTime(), 0.00001);
    assertTrue(Double.isNaN(selection.getStopTime()));
  }

  @Test
  public void decodeSelectionIgnoresNonResourceFirstArgument() {
    Expression expression = AstUtilities.createInstanceCreation(
        JavaConstructor.getInstance(AudioSource.class, AudioResource.class, Number.class),
        new DoubleLiteral(2.0),
        new DoubleLiteral(0.25));

    AudioSourceCustomExpressionCreatorCompositeLogic.Selection selection =
        AudioSourceCustomExpressionCreatorCompositeLogic.decodeSelection(expression);

    assertNull(selection.getResourceExpression());
    assertNull(selection.getAudioResource());
    assertEquals(1.0, selection.getVolumeLevel(), 0.00001);
    assertEquals(0.0, selection.getStartTime(), 0.00001);
  }
}
