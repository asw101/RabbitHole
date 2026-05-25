package org.alice.stageide.properties.uicontroller;

import edu.cmu.cs.dennisc.scenegraph.scale.Resizer;
import org.alice.math.immutable.Dimension3;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ModelSizePropertyControllerLogicTest {
  @Test
  public void analyzeResizersDetectsLinksAndIndependentAxes() {
    ModelSizePropertyControllerLogic.ResizerConfiguration configuration = ModelSizePropertyControllerLogic.analyzeResizers(
        List.of(Resizer.X_AXIS, Resizer.Y_AXIS, Resizer.XY_PLANE));

    assertTrue(configuration.hasLinkXY);
    assertFalse(configuration.hasLinkAll);
    assertTrue(configuration.hasX);
    assertTrue(configuration.hasY);
    assertFalse(configuration.hasZ);
    assertTrue(configuration.enableLinkXY);
    assertFalse(configuration.enableLinkAll);
    assertTrue(configuration.initialXyValue());
  }

  @Test
  public void computeSizeFromUiScalesAllDimensionsFromWidth() {
    Dimension3 scaled = ModelSizePropertyControllerLogic.computeSizeFromUi(
        new Dimension3(4.0, 5.0, 6.0),
        new Dimension3(2.0, 3.0, 4.0),
        ModelSizePropertyControllerLogic.SourceAxis.WIDTH,
        true, false, false, false);

    assertEquals(new Dimension3(4.0, 6.0, 8.0), scaled);
  }

  @Test
  public void computeSizeFromUiScalesOnlyLinkedAxisFromHeight() {
    Dimension3 scaled = ModelSizePropertyControllerLogic.computeSizeFromUi(
        new Dimension3(2.0, 10.0, 4.0),
        new Dimension3(2.0, 5.0, 8.0),
        ModelSizePropertyControllerLogic.SourceAxis.HEIGHT,
        false, false, false, true);

    assertEquals(new Dimension3(2.0, 10.0, 16.0), scaled);
  }

  @Test
  public void resolveSourceAxisRecognizesEditedField() {
    assertEquals(ModelSizePropertyControllerLogic.SourceAxis.WIDTH,
        ModelSizePropertyControllerLogic.resolveSourceAxis(true, false, false));
    assertEquals(ModelSizePropertyControllerLogic.SourceAxis.HEIGHT,
        ModelSizePropertyControllerLogic.resolveSourceAxis(false, true, false));
    assertEquals(ModelSizePropertyControllerLogic.SourceAxis.DEPTH,
        ModelSizePropertyControllerLogic.resolveSourceAxis(false, false, true));
    assertEquals(ModelSizePropertyControllerLogic.SourceAxis.NONE,
        ModelSizePropertyControllerLogic.resolveSourceAxis(false, false, false));
  }

  @Test
  public void shouldOfferResetButtonOnlyForJointedOrBillboardModels() {
    assertTrue(ModelSizePropertyControllerLogic.shouldOfferResetButton(true, false));
    assertTrue(ModelSizePropertyControllerLogic.shouldOfferResetButton(false, true));
    assertFalse(ModelSizePropertyControllerLogic.shouldOfferResetButton(false, false));
  }

  @Test
  public void clampNegativeScaleUsesNearZeroFractionOfOldScale() {
    Dimension3 clamped = ModelSizePropertyControllerLogic.clampNegativeScale(
        new Dimension3(4.0, 2.0, 1.0),
        new Dimension3(-1.0, 3.0, 2.0));

    assertEquals(new Dimension3(0.01, 0.005, 0.0025), clamped);
  }
}
