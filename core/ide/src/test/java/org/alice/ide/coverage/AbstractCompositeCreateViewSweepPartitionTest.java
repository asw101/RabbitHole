package org.alice.ide.coverage;

import org.alice.ide.testing.TestIdeBootstrap;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public abstract class AbstractCompositeCreateViewSweepPartitionTest {
  protected abstract int getPartitionIndex();

  protected int getPartitionCount() {
    return 30;
  }

  @After
  public void shutdownIdeBootstrap() {
    TestIdeBootstrap.shutdown();
  }

  @Test
  public void createsViewsForAssignedCompositePartition() {
    TestIdeBootstrap.boot();

    CompositeCreateViewSweepSupport.CompositeCreateViewSweepResult result =
        CompositeCreateViewSweepSupport.sweepPartition(getPartitionIndex(), getPartitionCount());

    assertTrue(result.summary(), result.assignedClassCount > 0);
    assertTrue(result.summary() + " " + result.failures, result.failures.isEmpty());
  }
}
