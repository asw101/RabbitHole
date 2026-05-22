package org.alice.ide.coverage;

public class LookingGlassDragGuiSweepTest extends AbstractExactGuiPackageSweepTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Override
  protected String getPackageName() {
    return "edu.cmu.cs.dennisc.ui.lookingglass";
  }
}
