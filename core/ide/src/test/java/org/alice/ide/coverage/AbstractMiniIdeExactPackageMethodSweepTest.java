package org.alice.ide.coverage;

import org.alice.ide.testing.TestIdeBootstrap;
import org.junit.Rule;
import org.junit.rules.Timeout;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.rules.Timeout;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.Timeout;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;

import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.rules.Timeout;

public abstract class AbstractMiniIdeExactPackageMethodSweepTest {

  @Rule
  public Timeout globalTimeout = Timeout.seconds(60);
  @BeforeClass
  public static void boot() {
    TestIdeBootstrap.boot();
  }

  @AfterClass
  public static void shutdown() {
    TestIdeBootstrap.shutdown();
  }

  @Test
  public final void sweepsPackageWithDefaultArgsAndMethodInvocation() {
    String packageName = getPackageName();
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepExactPackageWithDefaultArgs(packageName);

    assertTrue(packageName + " should resolve at least one class", result.discovered > 0);
    assertTrue(packageName + " should initialize at least one class", result.loaded > 0);
    assertTrue(packageName + " should invoke at least one method",
        (result.staticMethodsInvoked + result.instanceMethodsInvoked) > 0);
  }

  protected abstract String getPackageName();
}
