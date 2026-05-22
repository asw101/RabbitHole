package org.alice.ide.icons;

import javax.swing.Icon;

public class TorusIconRenderTest extends AbstractRenderableIconTest {
  @Override
  protected Icon createIcon() {
    return new TorusIcon(getSize());
  }
}
