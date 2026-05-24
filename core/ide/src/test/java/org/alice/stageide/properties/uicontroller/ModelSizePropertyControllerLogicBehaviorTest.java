package org.alice.stageide.properties.uicontroller;

import edu.cmu.cs.dennisc.scenegraph.scale.Resizer;
import org.alice.math.immutable.Dimension3;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ModelSizePropertyControllerLogicBehaviorTest {
  @Test
  public void analyzeResizersCapturesLinkedAndIndependentAxes() {
    ModelSizePropertyControllerLogic.ResizerConfiguration configuration =
        ModelSizePropertyControllerLogic.analyzeResizers(List.of(Resizer.X_AXIS, Resizer.Y_AXIS, Resizer.XY_PLANE));

    assertTrue(configuration.hasLinkXY);
    assertFalse(configuration.hasLinkAll);
    assertTrue(configuration.hasIndependentX);
    assertTrue(configuration.hasIndependentY);
    assertFalse(configuration.hasIndependentZ);
    assertTrue(configuration.enableLinkXY);
    assertFalse(configuration.enableLinkAll);
    assertTrue(configuration.initialXyValue());
  }

  @Test
  public void computeSizeFromUiScalesOnlyTheDimensionsLinkedToTheEditedAxis() {
    Dimension3 widthScaled = ModelSizePropertyControllerLogic.computeSizeFromUi(
        new Dimension3(4.0, 5.0, 6.0),
        new Dimension3(2.0, 3.0, 4.0),
        ModelSizePropertyControllerLogic.SourceAxis.WIDTH,
        true, false, false, false);
    Dimension3 depthScaled = ModelSizePropertyControllerLogic.computeSizeFromUi(
        new Dimension3(2.0, 5.0, 16.0),
        new Dimension3(2.0, 5.0, 8.0),
        ModelSizePropertyControllerLogic.SourceAxis.DEPTH,
        false, false, false, true);

    assertEquals(new Dimension3(4.0, 6.0, 8.0), widthScaled);
    assertEquals(new Dimension3(2.0, 10.0, 16.0), depthScaled);
  }

  @Test
  public void clampNegativeScaleReturnsOriginalScaleObjectWhenItIsAlreadyValid() {
    Dimension3 validScale = new Dimension3(1.0, 2.0, 3.0);

    assertSame(validScale, ModelSizePropertyControllerLogic.clampNegativeScale(new Dimension3(4.0, 5.0, 6.0), validScale));
  }

  @Test
  public void clampNegativeScaleShrinksToTinyPositiveValuesWhenAnyComponentIsNegative() {
    Dimension3 clamped = ModelSizePropertyControllerLogic.clampNegativeScale(
        new Dimension3(4.0, 2.0, 1.0),
        new Dimension3(-1.0, 3.0, 2.0));

    assertEquals(new Dimension3(0.01, 0.005, 0.0025), clamped);
  }
}
