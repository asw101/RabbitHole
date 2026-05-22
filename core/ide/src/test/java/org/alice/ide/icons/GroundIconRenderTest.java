package org.alice.ide.icons;

import javax.swing.Icon;

public class GroundIconRenderTest extends AbstractRenderableIconTest {
  @Override
  protected Icon createIcon() {
    return new GroundIcon(getSize());
  }
}
