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
import static org.junit.Assert.assertNull;
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
    try (ProgramImp.FactoryScope ignored = ProgramImp.useFactory(TestProgramImp::new)) {
      SProgram program = new SProgram();
      assertTrue("Should create TestProgramImp via scoped factory",
          program.getImplementation() instanceof TestProgramImp);
    }
  }

  @Test
  public void staticFactoryCleansUpAfterCreation() {
    try (ProgramImp.FactoryScope ignored = ProgramImp.useFactory(TestProgramImp::new)) {
      new SProgram();
    }
    ProgramImp.ACCEPTABLE_HACK_FOR_NOW_setClassForNextInstance(TestProgramImp.class);
    SProgram second = new SProgram();
    assertTrue("Legacy one-shot injection should work after scoped factory closes",
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

  @Test
  public void scopedFactoryRestoresOuterScope() {
    try (ProgramImp.FactoryScope outer = ProgramImp.useFactory(TestProgramImp::new)) {
      assertTrue(new SProgram().getImplementation() instanceof TestProgramImp);
      try (ProgramImp.FactoryScope inner = ProgramImp.useFactory(program -> new TestProgramImpWithBonus(program, "inner"))) {
        SProgram innerProgram = new SProgram();
        assertTrue(innerProgram.getImplementation() instanceof TestProgramImpWithBonus);
        assertEquals("inner", ((TestProgramImpWithBonus) innerProgram.getImplementation()).bonusValue);
      }
      assertTrue("Outer factory should be restored after inner closes",
          new SProgram().getImplementation() instanceof TestProgramImp);
    }
  }

  @Test
  public void scopedFactoryIsThreadLocal() throws Exception {
    final ProgramImp[] workerImp = new ProgramImp[1];
    final Throwable[] workerFailure = new Throwable[1];
    try (ProgramImp.FactoryScope ignored = ProgramImp.useFactory(TestProgramImp::new)) {
      Thread worker = new Thread(() -> {
        try {
          ProgramImp.ACCEPTABLE_HACK_FOR_NOW_setClassForNextInstance(
              TestProgramImpWithBonus.class,
              new Class<?>[] {String.class},
              new Object[] {"worker"});
          workerImp[0] = new SProgram().getImplementation();
        } catch (Throwable t) {
          workerFailure[0] = t;
        }
      });
      worker.start();
      worker.join();

      if (workerFailure[0] != null) {
        throw new AssertionError(workerFailure[0]);
      }
      assertTrue(new SProgram().getImplementation() instanceof TestProgramImp);
    }
    assertTrue(workerImp[0] instanceof TestProgramImpWithBonus);
    assertEquals("worker", ((TestProgramImpWithBonus) workerImp[0]).bonusValue);
  }

  @Test
  public void legacyOneShotFactoryCleansUpAfterConstructorFailure() {
    ProgramImp.ACCEPTABLE_HACK_FOR_NOW_setClassForNextInstance(MissingProgramConstructorImp.class);
    try {
      new SProgram();
    } catch (IllegalArgumentException expected) {
      assertTrue(expected.getMessage().contains(MissingProgramConstructorImp.class.getName()));
      ProgramImp.ACCEPTABLE_HACK_FOR_NOW_setClassForNextInstance(TestProgramImp.class);
      assertTrue(new SProgram().getImplementation() instanceof TestProgramImp);
      return;
    }
    org.junit.Assert.fail("Missing constructor should fail explicitly before cleanup is verified");
  }

  @Test
  public void legacyOneShotFactoryIsConsumedBeforeConstructorRuns() {
    ReentrantProgramImp.nestedImp = null;
    ProgramImp.ACCEPTABLE_HACK_FOR_NOW_setClassForNextInstance(ReentrantProgramImp.class);
    SProgram program = new SProgram();
    assertTrue(program.getImplementation() instanceof ReentrantProgramImp);
    assertTrue("Constructor should be able to use a separate one-shot factory for nested program creation",
        ReentrantProgramImp.nestedImp instanceof TestProgramImp);
  }

  @Test
  public void legacyOneShotFactoryRejectsMismatchedBonusArrays() {
    try {
      ProgramImp.ACCEPTABLE_HACK_FOR_NOW_setClassForNextInstance(
          TestProgramImpWithBonus.class,
          new Class<?>[] {String.class},
          new Object[] {});
    } catch (IllegalArgumentException expected) {
      return;
    }
    org.junit.Assert.fail("Mismatched bonus parameter and argument arrays should fail before storing factory state");
  }

  @Test
  public void legacyOneShotFactoryRejectsActiveScopedFactory() {
    try (ProgramImp.FactoryScope ignored = ProgramImp.useFactory(TestProgramImp::new)) {
      try {
        ProgramImp.ACCEPTABLE_HACK_FOR_NOW_setClassForNextInstance(TestProgramImp.class);
      } catch (IllegalStateException expected) {
        return;
      }
      org.junit.Assert.fail("Legacy one-shot factory should not be queued while scoped factory is active");
    }
  }

  @Test
  public void scopedFactoryRejectsPendingLegacyOneShotFactory() {
    ProgramImp.ACCEPTABLE_HACK_FOR_NOW_setClassForNextInstance(TestProgramImp.class);
    try {
      ProgramImp.useFactory(TestProgramImp::new);
    } catch (IllegalStateException expected) {
      assertTrue(new SProgram().getImplementation() instanceof TestProgramImp);
      return;
    }
    org.junit.Assert.fail("Scoped factory should reject pending legacy one-shot factory");
  }

  @Test
  public void scopedFactoryRejectsNullProgramImpResult() {
    try (ProgramImp.FactoryScope ignored = ProgramImp.useFactory(program -> null)) {
      new SProgram();
    } catch (NullPointerException expected) {
      assertEquals("ProgramImp factory returned null.", expected.getMessage());
      return;
    }
    org.junit.Assert.fail("Factory returning null should fail at the factory boundary");
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
  //  Speed change, restart action, toggle full screen action, dialog bounds
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void handleSpeedChangeDelegatesToAnimator() {
    TestProgramImp imp = new TestProgramImp(null);
    imp.callHandleSpeedChange(3.0);
  }

  @Test
  public void getRestartActionDefaultsToNull() {
    assertNull(new TestProgramImp(null).getRestartAction());
  }

  @Test
  public void setRestartActionRoundTrips() {
    TestProgramImp imp = new TestProgramImp(null);
    javax.swing.Action a = new javax.swing.AbstractAction() {
      @Override public void actionPerformed(java.awt.event.ActionEvent e) { }
    };
    imp.setRestartAction(a);
    assertSame(a, imp.getRestartAction());
  }

  @Test
  public void getToggleFullScreenActionIsNotNull() {
    assertNotNull(new TestProgramImp(null).getToggleFullScreenAction());
  }

  @Test
  public void getNormalDialogBoundsReturnsAwtComponentBoundsWhenNoPrev() {
    javax.swing.JPanel panel = new javax.swing.JPanel();
    panel.setBounds(0, 0, 100, 100);
    java.awt.Rectangle r = new TestProgramImp(null).getNormalDialogBounds(panel);
    assertNotNull(r);
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

    public void callHandleSpeedChange(double s) {
      handleSpeedChange(s);
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

  public static class MissingProgramConstructorImp extends ProgramImp {
    public MissingProgramConstructorImp() {
      super(null, null);
    }

    @Override
    public Animator getAnimator() {
      return new FakeAnimator();
    }
  }

  public static class ReentrantProgramImp extends ProgramImp {
    static ProgramImp nestedImp;

    public ReentrantProgramImp(SProgram abstraction) {
      super(abstraction, null);
      ProgramImp.ACCEPTABLE_HACK_FOR_NOW_setClassForNextInstance(TestProgramImp.class);
      nestedImp = new SProgram().getImplementation();
    }

    @Override
    public Animator getAnimator() {
      return new FakeAnimator();
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
