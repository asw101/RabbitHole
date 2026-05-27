package org.lgna.story.implementation.overlay;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class OpenUpdateCloseOverlayGraphicAnimationTest {
  private record Sample(OpenUpdateCloseOverlayGraphicAnimation.State state, double portion) {
  }

  private static class RecordingAnimation extends OpenUpdateCloseOverlayGraphicAnimation {
    private final List<Sample> samples = new ArrayList<>();

    RecordingAnimation(double openingDuration, double updatingDuration, double closingDuration) {
      super(openingDuration, updatingDuration, closingDuration);
    }

    @Override
    protected void updateStateAndPortion(State state, double portion) {
      this.samples.add(new Sample(state, portion));
    }

    Sample lastSample() {
      return this.samples.get(this.samples.size() - 1);
    }

    Sample firstSample() {
      return this.samples.get(0);
    }
  }

  private static void assertSample(Sample sample, OpenUpdateCloseOverlayGraphicAnimation.State state, double portion) {
    assertEquals(state, sample.state());
    assertEquals(portion, sample.portion(), 1e-6);
  }

  @Test
  public void updateTransitionsThroughOpeningUpdatingAndClosingPhases() {
    RecordingAnimation animation = new RecordingAnimation(1.0, 2.0, 1.0);

    assertEquals(4.0, animation.update(0.0, null), 1e-6);
    assertSample(animation.lastSample(), OpenUpdateCloseOverlayGraphicAnimation.State.OPENNING, 0.0);

    assertEquals(3.5, animation.update(0.5, null), 1e-6);
    assertSample(animation.lastSample(), OpenUpdateCloseOverlayGraphicAnimation.State.OPENNING, 0.5);

    assertEquals(2.5, animation.update(1.5, null), 1e-6);
    assertSample(animation.lastSample(), OpenUpdateCloseOverlayGraphicAnimation.State.UPDATING, 0.25);

    assertEquals(0.5, animation.update(3.5, null), 1e-6);
    assertSample(animation.lastSample(), OpenUpdateCloseOverlayGraphicAnimation.State.CLOSING, 0.5);

    assertEquals(0.0, animation.update(4.1, null), 1e-6);
    assertSample(animation.lastSample(), OpenUpdateCloseOverlayGraphicAnimation.State.CLOSING, 1.0);
  }

  @Test
  public void zeroDurationsImmediatelyResolveToClosingState() {
    RecordingAnimation animation = new RecordingAnimation(0.0, 0.0, 0.0);

    assertEquals(0.0, animation.update(0.0, null), 1e-6);
    assertSample(animation.lastSample(), OpenUpdateCloseOverlayGraphicAnimation.State.CLOSING, 1.0);
  }

  @Test
  public void completeInvokesOpeningPrologueAndFinalClosingState() {
    RecordingAnimation animation = new RecordingAnimation(1.0, 1.0, 1.0);

    animation.complete(null);

    assertSample(animation.firstSample(), OpenUpdateCloseOverlayGraphicAnimation.State.OPENNING, 0.0);
    assertSample(animation.lastSample(), OpenUpdateCloseOverlayGraphicAnimation.State.CLOSING, 1.0);
  }
}
