package org.alice.ide.coverage;

public class AliceIdeCroquetModelsProjectStatsCroquetGuiSweepTest extends AbstractExactGuiPackageSweepTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Override
  protected String getPackageName() {
    return "org.alice.ide.croquet.models.project.stats.croquet";
  }
}
