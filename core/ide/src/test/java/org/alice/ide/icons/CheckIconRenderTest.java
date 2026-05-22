package org.alice.ide.icons;

import javax.swing.Icon;

public class CheckIconRenderTest extends AbstractRenderableIconTest {
  @Override
  protected Icon createIcon() {
    return new CheckIcon(getSize());
  }
}
