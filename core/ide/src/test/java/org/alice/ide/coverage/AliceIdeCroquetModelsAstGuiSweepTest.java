package org.alice.ide.coverage;

public class AliceIdeCroquetModelsAstGuiSweepTest extends AbstractExactGuiPackageSweepTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Override
  protected String getPackageName() {
    return "org.alice.ide.croquet.models.ast";
  }
}
