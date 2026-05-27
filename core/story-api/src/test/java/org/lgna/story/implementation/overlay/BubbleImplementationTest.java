package org.lgna.story.implementation.overlay;

import edu.cmu.cs.dennisc.animation.Animation;
import edu.cmu.cs.dennisc.animation.AnimationObserver;
import edu.cmu.cs.dennisc.animation.Animator;
import edu.cmu.cs.dennisc.animation.FrameObserver;
import edu.cmu.cs.dennisc.color.Color4f;
import edu.cmu.cs.dennisc.scenegraph.Layer;
import edu.cmu.cs.dennisc.scenegraph.graphics.Bubble;
import edu.cmu.cs.dennisc.scenegraph.graphics.SpeechBubble;
import edu.cmu.cs.dennisc.scenegraph.graphics.ThoughtBubble;
import org.junit.Test;
import org.lgna.story.SProgram;
import org.lgna.story.SScene;
import org.lgna.story.implementation.OrthographicCameraImp;
import org.lgna.story.implementation.ProgramImp;
import org.lgna.story.implementation.SceneImp;
import org.lgna.story.implementation.StandInImp;

import java.awt.Font;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class BubbleImplementationTest {
  private static final Bubble.Originator NO_OP_ORIGINATOR = (originOfTail, bodyConnectionLocationOfTail, textBoundsOffset, bubble, renderTarget, actualViewport, camera, textSize) -> {
  };

  @Test
  public void speechBubbleImpCreatesSpeechBubbleWithConfiguredProperties() {
    ProgramImp program = new TestProgramImp();
    BubbleImp bubbleImp = new SpeechBubbleImp(
        new DelegatingStandInImp(program),
        NO_OP_ORIGINATOR,
        "Hello, Alice",
        new Font("Dialog", Font.BOLD, 18),
        new Color4f(0.1f, 0.2f, 0.3f, 1.0f),
        new Color4f(0.9f, 0.9f, 1.0f, 1.0f),
        new Color4f(0.4f, 0.5f, 0.6f, 1.0f),
        Bubble.PositionPreference.TOP_RIGHT);

    assertTrue(bubbleImp.getBubble() instanceof SpeechBubble);
    assertSame(program, bubbleImp.getProgram());
    assertSame(NO_OP_ORIGINATOR, bubbleImp.getBubble().getOriginator());
    assertEquals(Bubble.PositionPreference.TOP_RIGHT, bubbleImp.getBubble().getPositionPreference());
    assertEquals("Hello, Alice", bubbleImp.getBubble().text.getValue());
    assertEquals(18, bubbleImp.getBubble().font.getValue().getSize());
    assertEquals(new Color4f(0.1f, 0.2f, 0.3f, 1.0f), bubbleImp.getBubble().textColor.getValue());
    assertEquals(new Color4f(0.9f, 0.9f, 1.0f, 1.0f), bubbleImp.getBubble().fillColor.getValue());
    assertEquals(new Color4f(0.4f, 0.5f, 0.6f, 1.0f), bubbleImp.getBubble().outlineColor.getValue());
  }

  @Test
  public void thoughtBubbleImpCreatesThoughtBubbleWithConfiguredProperties() {
    BubbleImp bubbleImp = new ThoughtBubbleImp(
        new DelegatingStandInImp(null),
        NO_OP_ORIGINATOR,
        "Thinking",
        Bubble.DEFAULT_FONT,
        Bubble.DEFAULT_TEXT_COLOR,
        Bubble.DEFAULT_FILL_COLOR,
        Bubble.DEFAULT_OUTLINE_COLOR,
        Bubble.PositionPreference.TOP_LEFT);

    assertTrue(bubbleImp.getBubble() instanceof ThoughtBubble);
    assertSame(NO_OP_ORIGINATOR, bubbleImp.getBubble().getOriginator());
    assertEquals(Bubble.PositionPreference.TOP_LEFT, bubbleImp.getBubble().getPositionPreference());
    assertEquals("Thinking", bubbleImp.getBubble().text.getValue());
  }

  @Test
  public void bubbleImpPortionAddsAndRemovesGraphicsFromCameraLayer() {
    BubbleFixture fixture = createBubbleFixture();

    assertFalse(fixture.layer.getGraphics().contains(fixture.bubbleImp.getBubble()));

    fixture.bubbleImp.portion.setValue(0.5);
    assertEquals(0.5, fixture.bubbleImp.portion.getValue(), 1e-6);
    assertTrue(fixture.layer.getGraphics().contains(fixture.bubbleImp.getBubble()));

    fixture.bubbleImp.portion.setValue(0.0);
    assertEquals(0.0, fixture.bubbleImp.portion.getValue(), 1e-6);
    assertFalse(fixture.layer.getGraphics().contains(fixture.bubbleImp.getBubble()));
  }

  @Test
  public void bubbleAnimationMapsOverlayStatesToBubblePortion() {
    BubbleFixture fixture = createBubbleFixture();
    BubbleAnimation animation = new BubbleAnimation(0.5, 1.0, 0.5, fixture.bubbleImp);

    animation.updateStateAndPortion(OpenUpdateCloseOverlayGraphicAnimation.State.OPENNING, 0.25);
    assertEquals(0.25, fixture.bubbleImp.portion.getValue(), 1e-6);
    assertTrue(fixture.layer.getGraphics().contains(fixture.bubbleImp.getBubble()));

    animation.updateStateAndPortion(OpenUpdateCloseOverlayGraphicAnimation.State.UPDATING, 0.5);
    assertEquals(1.0, fixture.bubbleImp.portion.getValue(), 1e-6);

    animation.updateStateAndPortion(OpenUpdateCloseOverlayGraphicAnimation.State.CLOSING, 0.25);
    assertEquals(0.75, fixture.bubbleImp.portion.getValue(), 1e-6);

    animation.updateStateAndPortion(OpenUpdateCloseOverlayGraphicAnimation.State.CLOSING, 1.0);
    assertEquals(0.0, fixture.bubbleImp.portion.getValue(), 1e-6);
    assertFalse(fixture.layer.getGraphics().contains(fixture.bubbleImp.getBubble()));
  }

  private static BubbleFixture createBubbleFixture() {
    TestScene scene = new TestScene();
    SceneImp sceneImp = scene.getImplementation();
    OrthographicCameraImp cameraImp = new OrthographicCameraImp();
    cameraImp.setVehicle(sceneImp);

    StandInImp entity = new StandInImp();
    entity.setVehicle(sceneImp);

    SpeechBubbleImp bubbleImp = new SpeechBubbleImp(
        entity,
        NO_OP_ORIGINATOR,
        "Visible bubble",
        Bubble.DEFAULT_FONT,
        Bubble.DEFAULT_TEXT_COLOR,
        Bubble.DEFAULT_FILL_COLOR,
        Bubble.DEFAULT_OUTLINE_COLOR,
        Bubble.PositionPreference.TOP_CENTER);
    return new BubbleFixture(sceneImp, cameraImp.getPostRenderLayer(), bubbleImp);
  }

  private static final class BubbleFixture {
    private final SceneImp sceneImp;
    private final Layer layer;
    private final SpeechBubbleImp bubbleImp;

    private BubbleFixture(SceneImp sceneImp, Layer layer, SpeechBubbleImp bubbleImp) {
      this.sceneImp = sceneImp;
      this.layer = layer;
      this.bubbleImp = bubbleImp;
    }
  }

  private static final class DelegatingStandInImp extends StandInImp {
    private final ProgramImp program;

    private DelegatingStandInImp(ProgramImp program) {
      this.program = program;
    }

    @Override
    public ProgramImp getProgram() {
      return this.program;
    }
  }

  private static final class TestScene extends SScene {
    @Override
    public void handleActiveChanged(Boolean isActive, Integer activationCount) {
    }
  }

  private static final class TestProgramImp extends ProgramImp {
    private final Animator animator = new Animator() {
      @Override
      public double getCurrentTime() {
        return 0;
      }

      @Override
      public double getSpeedFactor() {
        return 1.0;
      }

      @Override
      public void setSpeedFactor(double speedFactor) {
      }

      @Override
      public void update() {
      }

      @Override
      public void invokeLater(Animation animation, AnimationObserver animationObserver) {
      }

      @Override
      public void invokeAndWait(Animation animation, AnimationObserver animationObserver)
          throws InterruptedException, InvocationTargetException {
      }

      @Override
      public void invokeAndWait_ThrowRuntimeExceptionsIfNecessary(Animation animation, AnimationObserver animationObserver) {
      }

      @Override
      public void addFrameObserver(FrameObserver runnable) {
      }

      @Override
      public void removeFrameObserver(FrameObserver runnable) {
      }

      @Override
      public void completeAll() {
      }

      @Override
      public void cancelAnimation() {
      }
    };

    private TestProgramImp() {
      super((SProgram) null, null);
    }

    @Override
    public Animator getAnimator() {
      return this.animator;
    }
  }
}
