package org.alice.ide.coverage;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;

import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.rules.Timeout;

public abstract class AbstractExactGuiPackageSweepTest {

  @Rule
  public Timeout globalTimeout = Timeout.seconds(60);
  protected abstract String getPackageName();

  @Test
  public final void sweepsGuiPackageOnEdt() {
    String packageName = getPackageName();
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepExactPackage(packageName);
    assertTrue(packageName + " should resolve at least one class", result.discovered > 0);
    assertTrue(packageName + " should initialize at least one class", result.loaded > 0);
  }
}
