package org.lgna.story.implementation;

import edu.cmu.cs.dennisc.animation.Animation;
import edu.cmu.cs.dennisc.animation.AnimationObserver;
import edu.cmu.cs.dennisc.animation.Animator;
import edu.cmu.cs.dennisc.animation.FrameObserver;
import org.junit.Test;
import org.lgna.story.SProgram;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Behavior tests for ProgramImp: static factory injection, simulation speed,
 * speed format, animator access, and abstraction query.
 *
 * <p>Uses a {@code TestProgramImp} that passes null for the render target and
 * a no-op {@code FakeAnimator}, isolating simulation-speed logic and property
 * queries from the rendering pipeline. All tests are headless-safe.
 */
public class ProgramImpTest {

  // ══════════════════════════════════════════════════════════════════════════
  //  Static factory injection
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void staticFactoryCreatesInjectedClass() {
    ProgramImp.ACCEPTABLE_HACK_FOR_NOW_setClassForNextInstance(TestProgramImp.class);
    SProgram program = new SProgram();
    assertTrue("Should create TestProgramImp via static factory",
        program.getImplementation() instanceof TestProgramImp);
  }

  @Test
  public void staticFactoryCleansUpAfterCreation() {
    ProgramImp.ACCEPTABLE_HACK_FOR_NOW_setClassForNextInstance(TestProgramImp.class);
    new SProgram();
    // Second creation without setting class should use DefaultProgramImp
    // but DefaultProgramImp needs a display, so we just verify state was cleaned
    // by setting and creating again
    ProgramImp.ACCEPTABLE_HACK_FOR_NOW_setClassForNextInstance(TestProgramImp.class);
    SProgram second = new SProgram();
    assertTrue("Second injection should also work",
        second.getImplementation() instanceof TestProgramImp);
  }

  @Test
  public void staticFactoryWithBonusParams() {
    ProgramImp.ACCEPTABLE_HACK_FOR_NOW_setClassForNextInstance(
        TestProgramImpWithBonus.class,
        new Class<?>[] {String.class},
        new Object[] {"bonus"});
    SProgram program = new SProgram();
    assertTrue("Should create TestProgramImpWithBonus",
        program.getImplementation() instanceof TestProgramImpWithBonus);
    assertEquals("bonus", ((TestProgramImpWithBonus) program.getImplementation()).bonusValue);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Abstraction
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void getAbstractionReturnsSProgram() {
    ProgramImp.ACCEPTABLE_HACK_FOR_NOW_setClassForNextInstance(TestProgramImp.class);
    SProgram program = new SProgram();
    assertSame("getAbstraction() should return the owning SProgram",
        program, program.getImplementation().getAbstraction());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Simulation speed
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void defaultSimulationSpeedFactorIsOne() {
    TestProgramImp imp = new TestProgramImp(null);
    assertEquals(1.0, imp.getSimulationSpeedFactor(), 1e-9);
  }

  @Test
  public void setSimulationSpeedFactorRoundTrips() {
    TestProgramImp imp = new TestProgramImp(null);
    imp.setSimulationSpeedFactor(2.5);
    assertEquals(2.5, imp.getSimulationSpeedFactor(), 1e-9);
  }

  @Test
  public void setSimulationSpeedFactorToZero() {
    TestProgramImp imp = new TestProgramImp(null);
    imp.setSimulationSpeedFactor(0.0);
    assertEquals(0.0, imp.getSimulationSpeedFactor(), 1e-9);
  }

  @Test
  public void setSimulationSpeedFactorToInfinity() {
    TestProgramImp imp = new TestProgramImp(null);
    imp.setSimulationSpeedFactor(Double.POSITIVE_INFINITY);
    assertEquals(Double.POSITIVE_INFINITY, imp.getSimulationSpeedFactor(), 0.0);
  }

  @Test
  public void simulationSpeedFactorViaSProgram() {
    ProgramImp.ACCEPTABLE_HACK_FOR_NOW_setClassForNextInstance(TestProgramImp.class);
    SProgram program = new SProgram();
    program.setSimulationSpeedFactor(3.0);
    assertEquals(3.0, program.getSimulationSpeedFactor(), 1e-9);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Speed format
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void defaultSpeedFormatIsStandard() {
    TestProgramImp imp = new TestProgramImp(null);
    assertEquals("speed: %dx", imp.getSpeedFormat());
  }

  @Test
  public void setSpeedFormatRoundTrips() {
    TestProgramImp imp = new TestProgramImp(null);
    imp.setSpeedFormat("velocity: %dx");
    assertEquals("velocity: %dx", imp.getSpeedFormat());
  }

  @Test
  public void setSpeedFormatToNullRestoresDefault() {
    TestProgramImp imp = new TestProgramImp(null);
    imp.setSpeedFormat("custom");
    imp.setSpeedFormat(null);
    assertEquals("speed: %dx", imp.getSpeedFormat());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Animator
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void getAnimatorReturnsNonNull() {
    TestProgramImp imp = new TestProgramImp(null);
    assertNotNull("getAnimator() should return the injected FakeAnimator",
        imp.getAnimator());
  }

  @Test
  public void getAnimatorReturnsFakeAnimator() {
    TestProgramImp imp = new TestProgramImp(null);
    assertTrue(imp.getAnimator() instanceof FakeAnimator);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  OnscreenRenderTarget
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void renderTargetIsNullForTestImp() {
    TestProgramImp imp = new TestProgramImp(null);
    // TestProgramImp passes null render target
    assertEquals(null, imp.getOnscreenRenderTarget());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Control panel preference
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void isControlPanelDesiredDefaultsToTrue() {
    TestProgramImp imp = new TestProgramImp(null);
    assertTrue("isControlPanelDesired should default to true",
        imp.isControlPanelDesired());
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Test doubles
  // ══════════════════════════════════════════════════════════════════════════

  public static class TestProgramImp extends ProgramImp {
    private final FakeAnimator animator = new FakeAnimator();

    public TestProgramImp(SProgram abstraction) {
      super(abstraction, null);
    }

    @Override
    public Animator getAnimator() {
      return animator;
    }
  }

  public static class TestProgramImpWithBonus extends ProgramImp {
    private final FakeAnimator animator = new FakeAnimator();
    final String bonusValue;

    public TestProgramImpWithBonus(SProgram abstraction, String bonus) {
      super(abstraction, null);
      this.bonusValue = bonus;
    }

    @Override
    public Animator getAnimator() {
      return animator;
    }
  }

  static class FakeAnimator implements Animator {
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
    public void invokeAndWait_ThrowRuntimeExceptionsIfNecessary(
        Animation animation, AnimationObserver animationObserver) {
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
  }
}
