package org.alice.ide.icons;

import javax.swing.Icon;

public class DiscIconRenderTest extends AbstractRenderableIconTest {
  @Override
  protected Icon createIcon() {
    return new DiscIcon(getSize());
  }
}
