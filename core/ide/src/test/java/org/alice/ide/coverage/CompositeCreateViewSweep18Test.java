package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

public class CompositeCreateViewSweep18Test extends AbstractCompositeCreateViewSweepPartitionTest {
  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }

  @Override
  protected int getPartitionIndex() {
    return 17;
  }
}
