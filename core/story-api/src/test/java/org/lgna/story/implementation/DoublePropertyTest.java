package org.lgna.story.implementation;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DoublePropertyTest {
  private static class StubOwner extends PropertyOwnerImp {
    @Override
    public ProgramImp getProgram() {
      return null;
    }
  }

  private static class StubDoubleProperty extends DoubleProperty {
    private Double value;

    StubDoubleProperty(Double initialValue, Double minValue) {
      super(new StubOwner(), minValue);
      this.value = initialValue;
    }

    StubDoubleProperty(Double initialValue) {
      super(new StubOwner());
      this.value = initialValue;
    }

    @Override
    public Double getValue() {
      return this.value;
    }

    @Override
    protected void handleSetValue(Double value) {
      this.value = value;
    }

    double interpolateValue(Double a, Double b, double portion) {
      return super.interpolate(a, b, portion);
    }
  }

  @Test
  public void interpolateHalfwayAveragesValues() {
    StubDoubleProperty property = new StubDoubleProperty(0.0, 0.0);

    assertEquals(5.0, property.interpolateValue(2.0, 8.0, 0.5), 1e-6);
  }

  @Test
  public void animateValueClampsToMinimumForImmediateUpdate() {
    StubDoubleProperty property = new StubDoubleProperty(1.0, 0.25);

    property.animateValue(-3.0, 0.0, EntityImp.DEFAULT_STYLE);

    assertEquals(0.25, property.getValue(), 1e-6);
  }

  @Test
  public void animateValueClampsToMinimumWithoutProgramForTimedUpdate() {
    StubDoubleProperty property = new StubDoubleProperty(1.0, 0.5);

    property.animateValue(-4.0, 1.0, EntityImp.DEFAULT_STYLE);

    assertEquals(0.5, property.getValue(), 1e-6);
  }

  @Test
  public void constructorWithoutMinimumAllowsNegativeValues() {
    StubDoubleProperty property = new StubDoubleProperty(1.0);

    property.animateValue(-2.5, 0.0, EntityImp.DEFAULT_STYLE);

    assertEquals(-2.5, property.getValue(), 1e-6);
  }
}
