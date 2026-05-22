package org.alice.ide.icons;

import javax.swing.Icon;

public class SphereIconRenderTest extends AbstractRenderableIconTest {
  @Override
  protected Icon createIcon() {
    return new SphereIcon(getSize());
  }
}
