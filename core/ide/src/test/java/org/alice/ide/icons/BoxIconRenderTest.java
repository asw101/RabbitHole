package org.alice.ide.icons;

import javax.swing.Icon;

public class BoxIconRenderTest extends AbstractRenderableIconTest {
  @Override
  protected Icon createIcon() {
    return new BoxIcon(getSize());
  }
}
