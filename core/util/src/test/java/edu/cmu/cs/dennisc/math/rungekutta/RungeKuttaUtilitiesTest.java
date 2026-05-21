package edu.cmu.cs.dennisc.math.rungekutta;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class RungeKuttaUtilitiesTest {
  private static class TestDerivative extends Derivative {
    private final double value;

    private TestDerivative(double value) {
      this.value = value;
    }
  }

  private static class TrackingFunction implements Function<TestDerivative> {
    private final List<String> calls = new ArrayList<>();
    private double updatedDt;

    @Override
    public TestDerivative evaluate(double t) {
      this.calls.add("a:" + t);
      return new TestDerivative(t);
    }

    @Override
    public TestDerivative evaluate(double t, double dt, TestDerivative derivative) {
      this.calls.add("step:" + t + ":" + dt + ":" + derivative.value);
      return new TestDerivative(t + dt + derivative.value);
    }

    @Override
    public void update(TestDerivative a, TestDerivative b, TestDerivative c, TestDerivative d, double dt) {
      this.calls.add("update:" + a.value + ":" + b.value + ":" + c.value + ":" + d.value);
      this.updatedDt = dt;
    }

    @Override
    public void update() {
      this.calls.add("finalize");
    }
  }

  @Test
  public void rk4EvaluatesAllStagesAndFinalizes() {
    TrackingFunction function = new TrackingFunction();

    RungeKuttaUtilities.rk4(function, 2.0, 0.5);

    Assert.assertEquals(java.util.Arrays.asList(
        "a:2.0",
        "step:2.0:0.25:2.0",
        "step:2.0:0.25:4.25",
        "step:2.0:0.5:6.5",
        "update:2.0:4.25:6.5:9.0",
        "finalize"), function.calls);
    Assert.assertEquals(0.5, function.updatedDt, 0.0);
  }
}
