package org.alice.stageide.gallerybrowser.search.croquet;

import org.alice.stageide.support.StageIdePackageSmokeTestSupport;
import org.junit.Test;

public class PackageBehaviorTest {
  @Test
  public void packageTreeLoads() throws Exception {
    StageIdePackageSmokeTestSupport.assertPackageTreeLoads("org.alice.stageide.gallerybrowser.search.croquet");
  }
}
