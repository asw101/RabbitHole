package org.alice.ide.icons;

import javax.swing.Icon;

public class AxesIconRenderTest extends AbstractRenderableIconTest {
  @Override
  protected Icon createIcon() {
    return new AxesIcon(getSize());
  }
}
