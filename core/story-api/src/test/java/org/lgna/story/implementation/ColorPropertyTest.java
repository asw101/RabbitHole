package org.lgna.story.implementation;

import org.junit.Test;
import org.lgna.story.Color;

import static org.junit.Assert.assertEquals;

public class ColorPropertyTest {
  private static class StubOwner extends PropertyOwnerImp {
    @Override
    public ProgramImp getProgram() {
      return null;
    }
  }

  private static class StubColorProperty extends ColorProperty {
    private Color value;

    StubColorProperty(Color initialValue) {
      super(new StubOwner());
      this.value = initialValue;
    }

    @Override
    public Color getValue() {
      return this.value;
    }

    @Override
    protected void handleSetValue(Color value) {
      this.value = value;
    }

    Color interpolateValue(Color a, Color b, double portion) {
      return super.interpolate(a, b, portion);
    }
  }

  @Test
  public void interpolateDelegatesToColorInterpolation() {
    StubColorProperty property = new StubColorProperty(Color.BLACK);

    Color halfway = property.interpolateValue(Color.BLACK, Color.WHITE, 0.5);

    assertEquals(0.5, halfway.getRed(), 1e-6);
    assertEquals(0.5, halfway.getGreen(), 1e-6);
    assertEquals(0.5, halfway.getBlue(), 1e-6);
  }

  @Test
  public void animateValueImmediatelySetsTargetColor() {
    StubColorProperty property = new StubColorProperty(Color.RED);

    property.animateValue(Color.BLUE, 0.0, EntityImp.DEFAULT_STYLE);

    assertEquals(Color.BLUE, property.getValue());
  }

  @Test
  public void animateValueCompletesWithoutProgramWhenDurationIsPositive() {
    StubColorProperty property = new StubColorProperty(Color.RED);

    property.animateValue(Color.GREEN, 1.0, EntityImp.DEFAULT_STYLE);

    assertEquals(Color.GREEN, property.getValue());
  }
}
