package org.alice.stageide.properties.uicontroller;

import edu.cmu.cs.dennisc.scenegraph.scale.Resizer;
import org.alice.math.immutable.Dimension3;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ModelSizePropertyControllerLogicBehaviorExtendedTest {
  private static void assertConstructorThrowsAssertionError(Class<?> type) throws Exception {
    Constructor<?> constructor = type.getDeclaredConstructor();
    constructor.setAccessible(true);
    try {
      constructor.newInstance();
      fail("Expected constructor to reject instantiation");
    } catch (InvocationTargetException ite) {
      assertTrue(ite.getCause() instanceof AssertionError);
    }
  }

  @Test
  public void utilityConstructorRejectsInstantiation() throws Exception {
    assertConstructorThrowsAssertionError(ModelSizePropertyControllerLogic.class);
  }

  @Test
  public void analyzeResizersEnablesEveryLinkWhenUniformAndIndependentAxesExist() {
    ModelSizePropertyControllerLogic.ResizerConfiguration configuration = ModelSizePropertyControllerLogic.analyzeResizers(
        List.of(Resizer.UNIFORM, Resizer.X_AXIS, Resizer.Y_AXIS, Resizer.Z_AXIS, Resizer.XY_PLANE, Resizer.XZ_PLANE, Resizer.YZ_PLANE));

    assertTrue(configuration.hasLinkAll);
    assertTrue(configuration.enableLinkAll);
    assertTrue(configuration.enableLinkXY);
    assertTrue(configuration.enableLinkXZ);
    assertTrue(configuration.enableLinkYZ);
    assertFalse(configuration.initialXyValue());
    assertFalse(configuration.initialXzValue());
    assertFalse(configuration.initialYzValue());
  }

  @Test
  public void computeSizeFromUiLeavesDesiredSizeUnchangedWhenNoAxisTriggeredTheUpdate() {
    Dimension3 desired = new Dimension3(7.0, 8.0, 9.0);

    assertEquals(desired, ModelSizePropertyControllerLogic.computeSizeFromUi(
        desired,
        new Dimension3(1.0, 2.0, 3.0),
        ModelSizePropertyControllerLogic.SourceAxis.NONE,
        true, true, true, true));
  }

  @Test
  public void computeSizeFromUiScalesWidthWhenDepthIsEditedWithXZLinking() {
    Dimension3 scaled = ModelSizePropertyControllerLogic.computeSizeFromUi(
        new Dimension3(2.0, 5.0, 16.0),
        new Dimension3(1.0, 5.0, 8.0),
        ModelSizePropertyControllerLogic.SourceAxis.DEPTH,
        false, false, true, false);

    assertEquals(new Dimension3(2.0, 5.0, 16.0), scaled);
  }
}
