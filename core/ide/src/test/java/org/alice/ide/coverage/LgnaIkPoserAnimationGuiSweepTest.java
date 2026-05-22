package org.alice.ide.coverage;

public class LgnaIkPoserAnimationGuiSweepTest extends AbstractExactGuiPackageSweepTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Override
  protected String getPackageName() {
    return "org.lgna.ik.poser.animation";
  }
}
