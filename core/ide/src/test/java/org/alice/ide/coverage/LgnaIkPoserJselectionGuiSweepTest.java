package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

public class LgnaIkPoserJselectionGuiSweepTest extends AbstractExactGuiPackageSweepTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Override
  protected String getPackageName() {
    return "org.lgna.ik.poser.jselection";
  }
}
