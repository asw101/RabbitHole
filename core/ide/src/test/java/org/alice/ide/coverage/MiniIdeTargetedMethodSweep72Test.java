package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

public class MiniIdeTargetedMethodSweep72Test extends AbstractMiniIdeNamedClassMethodSweepTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Override
  protected String[] getClassNames() {
    return new String[] {
        "org.alice.stageide.gallerybrowser.uri.UriGalleryDragModel",
        "org.alice.stageide.gallerybrowser.ImportGalleryResourceComposite"
    };
  }
}
