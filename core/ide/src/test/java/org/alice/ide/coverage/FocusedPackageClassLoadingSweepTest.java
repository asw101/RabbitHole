package org.alice.ide.coverage;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class FocusedPackageClassLoadingSweepTest {

  @Test
  public void sweepsOrgAliceIdeCroquetModelsTree() {
    assertPackageTreeSweep("org.alice.ide.croquet.models");
  }

  @Test
  public void sweepsOrgAliceStageideSceneeditorTree() {
    assertPackageTreeSweep("org.alice.stageide.sceneeditor");
  }

  @Test
  public void sweepsOrgAliceIdeAstTree() {
    assertPackageTreeSweep("org.alice.ide.ast");
  }

  @Test
  public void sweepsOrgAliceIdeIconsTree() {
    assertPackageTreeSweep("org.alice.ide.icons");
  }

  @Test
  public void sweepsOrgAliceIdeCommonTree() {
    assertPackageTreeSweep("org.alice.ide.common");
  }

  @Test
  public void sweepsOrgLgnaIkTree() {
    assertPackageTreeSweep("org.lgna.ik");
  }

  private void assertPackageTreeSweep(String packagePrefix) {
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepPackageTree(packagePrefix);
    assertTrue(packagePrefix + " should resolve at least one class", result.discovered > 0);
    assertTrue(packagePrefix + " should initialize at least one class", result.loaded > 0);
  }
}
